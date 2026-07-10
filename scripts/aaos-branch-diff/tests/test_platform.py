"""Unit tests for platform utilities and classifiers."""

from __future__ import annotations

import unittest

from aaos_branch_diff.file_classifier import classify_file, detect_language, normalize_path
from aaos_branch_diff.models import FileCategory
from aaos_branch_diff.parsers.java_parser import parse_java
from aaos_branch_diff.platform_utils import (
    decode_git_blob,
    is_probably_binary,
    normalize_line_endings,
    safe_display_path,
)


class TestJavaParser(unittest.TestCase):
    def test_class_extends_single_reference_type(self):
        """Regression: iterating ReferenceType must not yield tuples without .name."""
        src = """
        package com.oem.app;
        class Foo extends android.app.Activity implements Runnable {
            public void onCreate() {}
        }
        """
        classes, apis = parse_java(src, "app/src/main/java/com/oem/app/Foo.java")
        self.assertEqual(len(classes), 1)
        self.assertEqual(classes[0].name, "Foo")
        self.assertEqual(len(classes[0].methods), 1)
        extend_apis = [a for a in apis if a.usage_context == "extends"]
        self.assertTrue(any("Activity" in a.qualified_name for a in extend_apis))

    def test_generics_in_method_params(self):
        src = """
        package com.oem.app;
        import java.util.List;
        class Foo {
            void bar(List<String> items, int x) {}
        }
        """
        classes, _ = parse_java(src, "app/src/main/java/com/oem/app/Foo.java")
        self.assertEqual(len(classes[0].methods), 1)
        self.assertIn("List<String>", classes[0].methods[0].signature)


class TestNormalizePath(unittest.TestCase):
    def test_backslashes(self):
        self.assertEqual(normalize_path(r"app\src\main\java\Foo.java"), "app/src/main/java/Foo.java")

    def test_safe_display(self):
        self.assertEqual(safe_display_path(r"C:\work\repo"), "C:/work/repo")


class TestLineEndings(unittest.TestCase):
    def test_crlf(self):
        self.assertEqual(normalize_line_endings("a\r\nb\r"), "a\nb\n")


class TestBinaryDetection(unittest.TestCase):
    def test_text(self):
        self.assertFalse(is_probably_binary(b"package com.example;\n"))

    def test_binary(self):
        self.assertTrue(is_probably_binary(b"\x00\x01\x02"))


class TestDecodeGitBlob(unittest.TestCase):
    def test_utf8(self):
        self.assertEqual(decode_git_blob("hello".encode()), "hello")

    def test_utf8_bom(self):
        self.assertEqual(decode_git_blob(b"\xef\xbb\xbfhello"), "hello")

    def test_binary_returns_none(self):
        self.assertIsNone(decode_git_blob(b"\x00\xff\xfe"))


class TestFileClassifier(unittest.TestCase):
    def test_windows_main_source(self):
        path = r"app\src\main\java\com\oem\ivi\MainActivity.java"
        self.assertEqual(classify_file(path), FileCategory.SOURCE_MAIN)
        self.assertEqual(detect_language(path, FileCategory.SOURCE_MAIN).value, "java")

    def test_windows_test(self):
        path = r"app\src\androidTest\kotlin\com\oem\MainActivityTest.kt"
        self.assertEqual(classify_file(path), FileCategory.SOURCE_TEST)

    def test_gradle_kts(self):
        path = r"app\build.gradle.kts"
        self.assertEqual(classify_file(path), FileCategory.GRADLE)


if __name__ == "__main__":
    unittest.main()
