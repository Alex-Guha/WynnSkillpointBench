#!/usr/bin/env python3
"""Scrape WynnBuilder URLs from a Wynncraft forum thread.

Usage:
    python scrape_builds.py <forum_url> [-o builds.jsonl]

Outputs one JSON object per line (JSONL) with fields:
    url       — full WynnBuilder URL
    fragment  — the hash fragment (the encoded build)
    label     — nearby text hinting at build name (best-effort)
    has_craft — whether the URL likely contains crafted items

Example:
    python scrape_builds.py "https://forums.wynncraft.com/threads/the-ultimate-build-guide.320092/"
"""

import argparse
import json
import re
import sys
import urllib.request
from html.parser import HTMLParser

WYNNBUILDER_RE = re.compile(
    r'https://wynnbuilder\.github\.io/builder/#([A-Za-z0-9+\-_]+)'
)


class BuildLinkExtractor(HTMLParser):
    """Extract WynnBuilder links and nearby label text from forum HTML."""

    def __init__(self):
        super().__init__()
        self.results: list[dict] = []
        self.seen_fragments: set[str] = set()
        self._current_text: list[str] = []
        self._in_link = False
        self._link_href = ""

    def handle_starttag(self, tag, attrs):
        if tag == "a":
            href = dict(attrs).get("href") or ""
            if "wynnbuilder.github.io/builder/#" in href:
                self._in_link = True
                self._link_href = href

    def handle_endtag(self, tag):
        if tag == "a" and self._in_link:
            self._in_link = False
            label = " ".join(self._current_text).strip()
            self._current_text = []
            m = WYNNBUILDER_RE.search(self._link_href)
            if m and m.group(1) not in self.seen_fragments:
                frag = m.group(1)
                self.seen_fragments.add(frag)
                self.results.append({
                    "url": f"https://wynnbuilder.github.io/builder/#{frag}",
                    "fragment": frag,
                    "label": label or "(unlabeled)",
                })

    def handle_data(self, data):
        if self._in_link:
            self._current_text.append(data.strip())


def detect_crafted(fragment: str) -> bool:
    """Quick heuristic: try to decode and check if any slot has kind=1."""
    from wynnbuilder_to_testcase import BitVector, EQUIP_KIND_BITS, ITEM_ID_BITS, POWDERABLE
    try:
        bv = BitVector(fragment)
        bv.read(6)   # format flag
        bv.read(10)  # version
        for i in range(9):
            kind = bv.read(EQUIP_KIND_BITS)
            if kind == 1:
                return True
            if kind == 0:
                raw_id = bv.read(ITEM_ID_BITS)
                if i in POWDERABLE and raw_id > 0:
                    from wynnbuilder_to_testcase import skip_powders
                    skip_powders(bv)
            elif kind == 2:
                hash_bits = bv.read(12) * 6
                bv.pos += hash_bits
    except Exception:
        pass
    return False


def scrape(url: str) -> list[dict]:
    req = urllib.request.Request(url, headers={"User-Agent": "WynnSkillpointBench/1.0"})
    html = urllib.request.urlopen(req).read().decode("utf-8", errors="replace")
    parser = BuildLinkExtractor()
    parser.feed(html)
    for entry in parser.results:
        entry["has_craft"] = detect_crafted(entry["fragment"])
    return parser.results


def main():
    ap = argparse.ArgumentParser(description="Scrape WynnBuilder links from a forum thread.")
    ap.add_argument("url", help="Forum thread URL")
    ap.add_argument("-o", "--output", default=None,
                    help="Output file (default: stdout). Use .jsonl extension.")
    args = ap.parse_args()

    builds = scrape(args.url)
    print(f"Found {len(builds)} unique builds "
          f"({sum(1 for b in builds if not b['has_craft'])} non-crafted, "
          f"{sum(1 for b in builds if b['has_craft'])} crafted)",
          file=sys.stderr)

    out = open(args.output, "w") if args.output else sys.stdout
    for b in builds:
        out.write(json.dumps(b) + "\n")
    if args.output:
        out.close()
        print(f"Written to {args.output}", file=sys.stderr)


if __name__ == "__main__":
    main()
