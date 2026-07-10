"""Tests for tree-sitter AST parsing."""

from __future__ import annotations

import unittest

from aaos_branch_diff.parsers.ast_parser import parse_source_ast
from aaos_branch_diff.models import Language


class TestAstParser(unittest.TestCase):
    def test_java_tree_sitter(self):
        src = """
        package com.oem.ivi;
        import android.app.Activity;
        class Main extends Activity {
            public void onCreate() { super.onCreate(); }
            private int helper() { return 1; }
        }
        """
        result = parse_source_ast(src, "app/src/main/java/com/oem/ivi/Main.java", Language.JAVA)
        self.assertTrue(result.success or result.classes)
        self.assertGreaterEqual(len(result.classes), 1)
        methods = result.classes[0].methods
        self.assertGreaterEqual(len(methods), 1)
        self.assertTrue(result.parser.startswith("tree-sitter") or "javalang" in result.parser)

    def test_kotlin_tree_sitter(self):
        src = """
        package com.oem.ivi
        import android.car.Car
        class MainActivity {
            fun onStart() {}
            fun connect(car: Car) {}
        }
        """
        result = parse_source_ast(src, "app/src/main/kotlin/com/oem/ivi/MainActivity.kt", Language.KOTLIN)
        self.assertTrue(result.classes)
        self.assertGreaterEqual(len(result.classes[0].methods), 1)


if __name__ == "__main__":
    unittest.main()
