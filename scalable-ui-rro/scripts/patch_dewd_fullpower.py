#!/usr/bin/env python3
"""In-place patch DewdDynamicAospRRO (preserves zip layout so overlay still loads)."""
from __future__ import annotations

import argparse
import shutil
import struct
import zipfile
import zlib
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
    data = replace_all(
        data,
        b"com.android.car.mapsplaceholder/.MapsPlaceholderActivity",
        b"com.test.design/.presentation.ivi.map.MapsPlaceholderAct",
        "default_map_activity",
    )
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


def find_cdh(apk: bytes, filename: str, local_header_offset: int) -> int:
    needle = filename.encode("utf-8")
    start = 0
    while True:
        idx = apk.find(needle, start)
        if idx < 0:
            raise SystemExit(f"CDH not found for {filename}")
        cdh = idx - 46
        if (
            cdh >= 0
            and apk[cdh : cdh + 4] == b"PK\x01\x02"
            and struct.unpack_from("<H", apk, cdh + 28)[0] == len(needle)
            and struct.unpack_from("<I", apk, cdh + 42)[0] == local_header_offset
        ):
            return cdh
        start = idx + 1


def patch_entry(apk: bytearray, zpath: Path, filename: str, patch_fn) -> None:
    with zipfile.ZipFile(zpath) as z:
        info = z.getinfo(filename)
        header_off = info.header_offset
        old_comp_size = info.compress_size
        old_uncomp_size = info.file_size
        method = info.compress_type
        raw = z.read(filename)

    if len(raw) != old_uncomp_size:
        raise SystemExit(f"{filename}: unexpected size")

    nlen, elen = struct.unpack_from("<HH", apk, header_off + 26)
    data_off = header_off + 30 + nlen + elen
    patched = patch_fn(raw)
    if len(patched) != len(raw):
        raise SystemExit(f"{filename}: uncompressed size changed")

    if method == zipfile.ZIP_STORED:
        new_comp = patched
    elif method == zipfile.ZIP_DEFLATED:
        best = None
        for level in range(0, 10):
            co = zlib.compressobj(level, zlib.DEFLATED, -15)
            cand = co.compress(patched) + co.flush()
            if len(cand) <= old_comp_size and (best is None or len(cand) > len(best)):
                # Prefer largest that still fits (less padding risk); any fit is OK
                best = cand
        if best is None:
            raise SystemExit(f"{filename}: could not recompress into {old_comp_size} bytes")
        new_comp = best
    else:
        raise SystemExit(f"{filename}: unsupported compress_type {method}")

    crc = zlib.crc32(patched) & 0xFFFFFFFF
    # Write into existing slot; keep old_comp_size in headers so layout stays valid.
    slot = bytearray(old_comp_size)
    slot[: len(new_comp)] = new_comp
    if method == zipfile.ZIP_DEFLATED and len(new_comp) < old_comp_size:
        # Fill remainder with empty non-final stored blocks then rely on already-final stream.
        # Safer: keep compress_size as len(new_comp) in CDH/LFH.
        pass

    apk[data_off : data_off + old_comp_size] = slot

    # Update CRC + sizes. Prefer actual new compressed length in CDH/LFH.
    new_comp_size = len(new_comp)
    struct.pack_into("<I", apk, header_off + 14, crc)
    # Some AAOS overlays leave LFH sizes at 0; only set if previously non-zero.
    lfh_comp = struct.unpack_from("<I", apk, header_off + 18)[0]
    if lfh_comp != 0:
        struct.pack_into("<I", apk, header_off + 18, new_comp_size)
        struct.pack_into("<I", apk, header_off + 22, old_uncomp_size)

    cdh = find_cdh(apk, filename, header_off)
    struct.pack_into("<I", apk, cdh + 16, crc)
    struct.pack_into("<I", apk, cdh + 20, new_comp_size)
    struct.pack_into("<I", apk, cdh + 24, old_uncomp_size)
    print(
        f"{filename}: method={method} uncomp={old_uncomp_size} "
        f"comp {old_comp_size}->{new_comp_size} crc={crc:08x}"
    )


def main() -> None:
    ap = argparse.ArgumentParser()
    ap.add_argument("--input", type=Path, required=True)
    ap.add_argument("--output", type=Path, required=True)
    args = ap.parse_args()

    shutil.copyfile(args.input, args.output)
    # Work on a temp copy path for ZipFile metadata, then mutate output bytes.
    apk = bytearray(args.output.read_bytes())

    # Patch using metadata from the pristine input (offsets match the copy).
    patch_entry(apk, args.input, "resources.arsc", patch_arsc)
    # After arsc mutation offsets unchanged (same-length); still use input for headers.
    patch_entry(apk, args.input, "res/xml/map_panel.xml", patch_map_panel)
    patch_entry(apk, args.input, "res/xml/widget_panel.xml", patch_widget_panel)

    args.output.write_bytes(apk)

    # Verify readable
    with zipfile.ZipFile(args.output) as z:
        for name in (
            "resources.arsc",
            "res/xml/map_panel.xml",
            "res/xml/widget_panel.xml",
        ):
            data = z.read(name)
            print(f"verify {name}: {len(data)} bytes")

    print(f"wrote {args.output} ({args.output.stat().st_size} bytes)")


if __name__ == "__main__":
    main()
