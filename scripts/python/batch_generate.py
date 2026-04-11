#!/usr/bin/env python3
"""Batch-convert cached WynnBuilder builds into Java TestCases.add() snippets.

Usage:
    python batch_generate.py builds.jsonl [-o test_cases_output.java]
    python batch_generate.py builds.jsonl --skip-crafted --start-index 22

Reads a JSONL cache (from scrape_builds.py) and produces ready-to-paste Java
snippets for TestCases.java. Crafted builds are skipped by default.

Output goes to stdout (pipe or redirect as needed), diagnostics to stderr.
"""

import argparse
import json
import os
import re
import sys
import time

# Import from sibling module
sys.path.insert(0, os.path.dirname(__file__))
from wynnbuilder_to_testcase import (
    compute_min_skillpoints,
    decode_url,
    ensure_items_db,
    format_test_case,
    item_to_wynn,
    load_item_db,
    SLOT_NAMES,
)

DEFAULT_ITEMS_DB = os.path.join(os.path.dirname(__file__), "items.json")



def process_build(entry: dict, case_index: int, id_map: dict, prefix: str = "sugo") -> str | None:
    """Process a single build entry. Returns Java snippet or None on failure."""
    fragment = entry["fragment"]
    label = entry.get("label", "")

    try:
        item_ids, skillpoints = decode_url(fragment)
    except Exception as e:
        print(f"  SKIP: decode error: {e}", file=sys.stderr)
        return None

    # Collect gear (slots 0-7, skip weapon at 8)
    gear = []
    for i, item_id in enumerate(item_ids):
        if i == 8:
            continue
        if item_id is None:
            continue
        item = id_map.get(item_id)
        if item is None:
            print(f"  SKIP: item ID {item_id} not found in database", file=sys.stderr)
            return None
        name = item.get("displayName", item.get("name", "?"))
        reqs, bons = item_to_wynn(item)
        gear.append((name, reqs, bons))

    if not gear:
        print(f"  SKIP: no gear found", file=sys.stderr)
        return None

    # Compute minimum skillpoints
    try:
        skillpoints = compute_min_skillpoints(gear)
    except Exception as e:
        print(f"  SKIP: skillpoint computation failed: {e}", file=sys.stderr)
        return None

    case_name = f"{prefix}{case_index:03d}"
    return format_test_case(case_name, gear, skillpoints)


def main():
    ap = argparse.ArgumentParser(description="Batch-generate Java test cases from cached builds.")
    ap.add_argument("input", help="JSONL file from scrape_builds.py")
    ap.add_argument("-o", "--output", default=None, help="Output file (default: stdout)")
    ap.add_argument("--skip-crafted", action="store_true", default=True,
                    help="Skip builds with crafted items (default: true)")
    ap.add_argument("--include-crafted", action="store_true",
                    help="Include crafted builds (overrides --skip-crafted)")
    ap.add_argument("--prefix", default="sugo",
                    help="Case name prefix (default: sugo)")
    ap.add_argument("--start-index", type=int, default=1,
                    help="Starting case number index (default: 1)")
    ap.add_argument("--items-db", default=DEFAULT_ITEMS_DB, help="Path to items.json")
    args = ap.parse_args()

    skip_crafted = not args.include_crafted

    ensure_items_db(args.items_db)
    id_map = load_item_db(args.items_db)

    with open(args.input) as f:
        entries = [json.loads(line) for line in f if line.strip()]

    total = len(entries)
    skipped_craft = 0
    skipped_err = 0
    generated = 0
    case_idx = args.start_index

    out = open(args.output, "w") if args.output else sys.stdout

    for i, entry in enumerate(entries):
        if skip_crafted and entry.get("has_craft", False):
            skipped_craft += 1
            continue

        label = entry.get("label", "(unlabeled)")
        print(f"[{i+1}/{total}] Processing: {label[:70]}...", file=sys.stderr)

        snippet = process_build(entry, case_idx, id_map, prefix=args.prefix)
        if snippet is None:
            skipped_err += 1
            continue

        out.write("\n")
        out.write(f"        {snippet}\n")
        generated += 1
        case_idx += 1

    if args.output:
        out.close()

    print(f"\nDone: {generated} generated, {skipped_craft} crafted skipped, "
          f"{skipped_err} errors skipped", file=sys.stderr)


if __name__ == "__main__":
    main()
