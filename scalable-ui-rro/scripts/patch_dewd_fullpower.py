#!/usr/bin/env python3
"""Patch DewdDynamicAospRRO for Design Adaptive Space (floating SystemBars + map)."""
from __future__ import annotations

import argparse
import struct
import zipfile
from collections.abc import Callable
from pathlib import Path


def replace_all(data: bytes, old: bytes, new: bytes, label: str) -> bytes:
    if len(old) != len(new):
        raise SystemExit(f"{label}: length mismatch {len(old)} vs {len(new)}")
    count = data.count(old)
    print(f"{label}: {count} hit(s)")
    if count == 0:
        raise SystemExit(f"{label}: not found")
    return data.replace(old, new)


def patch_arsc(data: bytes) -> bytes:
    data = replace_all(
        data,
        b"com.android.car.carlauncher/.WidgetHostActivity",
        b"com.test.design/.presentation.ivi.glanceables.R",
        "widget_component",
    )
    # Keep stock AAOS MapsPlaceholderActivity as full-bleed green map_panel.
    return data


def patch_map_panel(data: bytes) -> bytes:
    data = replace_all(
        data,
        bytes([0x08, 0x00, 0x00, 0x01, 0x1E, 0x00, 0x05, 0x7F]),
        bytes([0x08, 0x00, 0x00, 0x10, 0x00, 0x00, 0x00, 0x00]),
        "map_panel left=0",
    )
    data = replace_all(
        data,
        bytes([0x08, 0x00, 0x00, 0x05, 0x01, 0x28, 0x00, 0x00]),
        bytes([0x08, 0x00, 0x00, 0x05, 0x00, 0x00, 0x00, 0x00]),
        "map_panel rightOffset=0",
    )
    return data


def patch_widget_panel(data: bytes) -> bytes:
    old = bytes([0x08, 0x00, 0x00, 0x10, 0x02, 0x00, 0x00, 0x00])
    new = bytes([0x08, 0x00, 0x00, 0x10, 0x14, 0x00, 0x00, 0x00])
    idx = data.find(old)
    if idx < 0:
        raise SystemExit("widget_panel layer=2 not found")
    print(f"widget_panel layer=20 at {idx}")
    return data[:idx] + new + data[idx + len(old) :]


def _put_dimension_dp(buf: bytearray, offset: int, dp: int) -> None:
    # TYPE_DIMENSION=0x05, COMPLEX_UNIT_DIP=1, radix 23p0
    struct.pack_into("<HBBI", buf, offset, 8, 0, 0x05, (dp << 8) | 0x01)


def _put_fraction_parent(buf: bytearray, offset: int, fraction: float) -> None:
    # TYPE_FRACTION=0x06, unit=parent(0), radix 16p7 (÷128)
    mant = int(round(fraction * 128))
    struct.pack_into("<HBBI", buf, offset, 8, 0, 0x06, (mant << 8) | (1 << 4))


def _patch_system_bar_floating(data: bytes, label: str) -> bytes:
    """
    Convert Dewd full-bleed SystemBars (left=0, width=100%) into floating
    inset bars (12dp side margins) so they read as Scalable UI panels, not
    legacy edge-to-edge CarSystemBar strips.

    Corner radius still requires Design RRO / CarSystemUI window clipping.
    """
    buf = bytearray(data)
    left_zero = bytes([0x08, 0x00, 0x00, 0x10, 0x00, 0x00, 0x00, 0x00])
    width_full = bytes([0x08, 0x00, 0x00, 0x06, 0x00, 0x01, 0x00, 0x00])

    left_hits: list[int] = []
    start = 0
    while True:
        idx = buf.find(left_zero, start)
        if idx < 0:
            break
        left_hits.append(idx)
        start = idx + 1
    width_hits: list[int] = []
    start = 0
    while True:
        idx = buf.find(width_full, start)
        if idx < 0:
            break
        width_hits.append(idx)
        start = idx + 1

    if not left_hits:
        raise SystemExit(f"{label}: left=0 not found")
    if not width_hits:
        raise SystemExit(f"{label}: width=100% not found")

    # Pair left with a nearby width=100% inside Bounds (nav also has bottom=100%).
    pair = None
    for lo in left_hits:
        candidates = [w for w in width_hits if 0 < abs(w - lo) < 80]
        if candidates:
            pair = (lo, min(candidates, key=lambda w: abs(w - lo)))
            break
    if pair is None:
        raise SystemExit(f"{label}: could not pair left/width in Bounds")

    left_off, width_off = pair
    # Tangorpro-class ~1862dp wide → 12dp*2 side inset ≈ 1.29% → width ≈ 98.71%
    side_dp = 12
    width_frac = 1.0 - (side_dp * 2) / 1862.0
    _put_dimension_dp(buf, left_off, side_dp)
    _put_fraction_parent(buf, width_off, width_frac)
    print(
        f"{label}: floating SystemBar left={side_dp}dp width≈{width_frac * 100:.2f}% "
        f"(was full-bleed)"
    )
    return bytes(buf)


def patch_status_bar(data: bytes) -> bytes:
    return _patch_system_bar_floating(data, "status_bar")


def patch_nav_bar(data: bytes) -> bytes:
    return _patch_system_bar_floating(data, "nav_bar")


def rebuild_apk(
    input_path: Path,
    output_path: Path,
    patches: dict[str, Callable[[bytes], bytes]],
) -> None:
    """Rewrite APK with patched entries (allows compressed size to grow)."""
    with zipfile.ZipFile(input_path, "r") as zin:
        infos = {info.filename: info for info in zin.infolist()}
        blobs = {name: zin.read(name) for name in infos}

    for name, patch_fn in patches.items():
        if name not in blobs:
            raise SystemExit(f"missing zip entry: {name}")
        before = blobs[name]
        after = patch_fn(before)
        print(f"rebuild {name}: {len(before)} -> {len(after)} bytes")
        blobs[name] = after

    output_path.parent.mkdir(parents=True, exist_ok=True)
    tmp = output_path.with_suffix(output_path.suffix + ".tmp")
    with zipfile.ZipFile(tmp, "w") as zout:
        for name, info in infos.items():
            out = zipfile.ZipInfo(filename=name, date_time=info.date_time)
            out.compress_type = info.compress_type
            out.external_attr = info.external_attr
            out.create_system = info.create_system
            zout.writestr(out, blobs[name], compress_type=info.compress_type)
    tmp.replace(output_path)


def main() -> None:
    ap = argparse.ArgumentParser()
    ap.add_argument("--input", type=Path, required=True)
    ap.add_argument("--output", type=Path, required=True)
    args = ap.parse_args()

    rebuild_apk(
        args.input,
        args.output,
        {
            "resources.arsc": patch_arsc,
            "res/xml/map_panel.xml": patch_map_panel,
            "res/xml/widget_panel.xml": patch_widget_panel,
            "res/xml/status_bar.xml": patch_status_bar,
            "res/xml/nav_bar.xml": patch_nav_bar,
        },
    )

    with zipfile.ZipFile(args.output) as z:
        for name in (
            "resources.arsc",
            "res/xml/map_panel.xml",
            "res/xml/widget_panel.xml",
            "res/xml/status_bar.xml",
            "res/xml/nav_bar.xml",
        ):
            data = z.read(name)
            print(f"verify {name}: {len(data)} bytes")

    print(f"wrote {args.output} ({args.output.stat().st_size} bytes)")


if __name__ == "__main__":
    main()
