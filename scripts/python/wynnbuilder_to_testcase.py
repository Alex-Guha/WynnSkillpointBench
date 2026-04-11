#!/usr/bin/env python3
"""Convert a WynnBuilder URL into a Java TestCases.add(...) snippet.

Usage:
    python wynnbuilder_to_testcase.py <url_or_fragment> [case_name]
    python wynnbuilder_to_testcase.py "https://wynnbuilder.github.io/builder/#CN0O0VTctzAT4nzw4gvLU6eG05mwmLEunzobspBGs-Ud0" case_myBuild

Requires items.json in the same directory (or specify via --items-db).
Download once from the WynnBuilder repo:
    curl -o items.json https://raw.githubusercontent.com/wynnbuilder/wynnbuilder.github.io/main/compress/items_compress.json

Notes: This is the old wynnbuilder site, not the beta one. This allows compabitility with existing
build databases, such as Sugo's forum thread, where we will be pulling most of the information from.

Honestly a lot of the code was Claude generated, can't be asked to read through Wynnbuilder src,
but I did verify the decoding works using like half of the Warrior builds in Sugo's thread, and the
results look correct. I made sure the skill point autoassignment matches the existing algorithm, and
that Claude doesn't try some crazy bootstrapping with our code, so it should be good to go.

Sugo's thread: https://forums.wynncraft.com/threads/the-ultimate-build-guide.320092/

(Does that make him a contributor for this project? I mean I guess... thanks Sugo, even if some of
these builds are incredibly dubious in game, but hey more test cases for me to work with :) )

For now, crafted builds are skipped because they're a huge pain to decode, and they are actually
simpler cases of normal builds since they can't contribute skill point requirements. Additionally,
I still have no idea how -requirements wowrk on crafteds, so whatever I guess. For once, I am very
thankful that Sugo posts noncrafted builds, otherwise I might've had to actually deal with crafteds.
"""

import argparse
import json
import os
import sys
import urllib.request

# ---------------------------------------------------------------------------
# WynnBuilder Base64 & BitVector
# ---------------------------------------------------------------------------

B64 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz+-"
B64_MAP = {c: i for i, c in enumerate(B64)}

ITEM_ID_BITS = 13
EQUIP_KIND_BITS = 2
POWDER_ID_BITS = 5
POWDER_TIERS = 6
POWDER_WRAP_BITS = 2
SP_BITS = 12
POWDERABLE = {0, 1, 2, 3, 8}  # helmet, chest, legs, boots, weapon

SLOT_NAMES = [
    "Helmet", "Chestplate", "Leggings", "Boots",
    "Ring 1", "Ring 2", "Bracelet", "Necklace", "Weapon",
]

SP_REQ_FIELDS = ["strReq", "dexReq", "intReq", "defReq", "agiReq"]
SP_BONUS_FIELDS = ["str", "dex", "int", "def", "agi"]


class BitVector:
    """LSB-first bit vector matching WynnBuilder's JS implementation."""

    def __init__(self, b64_string: str):
        self.length = len(b64_string) * 6
        self.bits: list[int] = []
        self.pos = 0

        current = 0
        idx = 0
        for ch in b64_string:
            val = B64_MAP[ch]
            pre = idx % 32
            current |= val << pre
            idx += 6
            post = idx % 32
            if post < pre:
                self.bits.append(current & 0xFFFFFFFF)
                current = val >> (6 - post)
        if idx % 32 != 0:
            self.bits.append(current & 0xFFFFFFFF)

    def read(self, n: int) -> int:
        start, end = self.pos, self.pos + n
        self.pos = end
        if n == 0:
            return 0
        sw, ew = start // 32, (end - 1) // 32
        if sw == ew:
            mask = ((1 << n) - 1) << (start % 32)
            return (self.bits[sw] & mask) >> (start % 32)
        sp = start % 32
        lo_bits = 32 - sp
        lo = (self.bits[sw] >> sp) & ((1 << lo_bits) - 1)
        hi_bits = n - lo_bits
        hi = self.bits[sw + 1] & ((1 << hi_bits) - 1)
        return lo | (hi << lo_bits)

    def read_signed(self, n: int) -> int:
        val = self.read(n)
        if val >= (1 << (n - 1)):
            val -= 1 << n
        return val


# ---------------------------------------------------------------------------
# Decoding
# ---------------------------------------------------------------------------

def skip_powders(bv: BitVector):
    """Advance past powder data for a powderable slot."""
    if bv.read(1) == 0:  # NO_POWDERS
        return
    prev = bv.read(POWDER_ID_BITS)  # first powder
    while True:
        if bv.read(1) == 0:  # REPEAT
            continue
        if bv.read(1) == 0:  # REPEAT_TIER
            bv.read(POWDER_WRAP_BITS)
            continue
        if bv.read(1) == 0:  # NEW_POWDER
            prev = bv.read(POWDER_ID_BITS)
            continue
        break  # NEW_ITEM — done


def decode_url(fragment: str) -> tuple[list[int | None], list[int]]:
    """Decode a WynnBuilder URL fragment into (item_ids[9], skillpoints[5])."""
    bv = BitVector(fragment)

    # Header
    _format_flag = bv.read(6)
    _version = bv.read(10)

    # 9 equipment slots
    item_ids: list[int | None] = []
    for i in range(9):
        kind = bv.read(EQUIP_KIND_BITS)
        if kind == 0:  # NORMAL
            raw_id = bv.read(ITEM_ID_BITS)
            item_ids.append(raw_id - 1 if raw_id > 0 else None)
            if i in POWDERABLE and raw_id > 0:
                skip_powders(bv)
        elif kind == 2:  # CUSTOM
            hash_bits = bv.read(12) * 6
            bv.pos += hash_bits
            item_ids.append(None)
        else:
            item_ids.append(None)

    # Skip tomes
    if bv.read(1) == 1:  # HAS_TOMES
        for _ in range(14):
            if bv.read(1) == 1:  # USED
                bv.read(8)  # tome ID

    # Skillpoints
    skillpoints = [0] * 5
    if bv.read(1) == 0:  # ASSIGNED (not automatic)
        for j in range(5):
            if bv.read(1) == 1:  # ELEMENT_ASSIGNED
                skillpoints[j] = bv.read_signed(SP_BITS)

    return item_ids, skillpoints


# ---------------------------------------------------------------------------
# Item database
# ---------------------------------------------------------------------------

def load_item_db(path: str) -> dict[int, dict]:
    with open(path) as f:
        db = json.load(f)
    return {item["id"]: item for item in db["items"]}


def item_to_wynn(item: dict) -> tuple[list[int], list[int]]:
    """Extract (requirements[5], bonuses[5]) from an item dict."""
    reqs = [item.get(f, 0) for f in SP_REQ_FIELDS]
    bons = [item.get(f, 0) for f in SP_BONUS_FIELDS]
    return reqs, bons


# ---------------------------------------------------------------------------
# Skillpoint auto-assignment (mirrors WynnBuilder's algorithm)
# ---------------------------------------------------------------------------

def compute_min_skillpoints(
    items: list[tuple[str, list[int], list[int]]],
) -> list[int]:
    """Find the minimum assigned skillpoints so all items are equippable in some order.

    Uses a recursive permutation search with pruning, matching WynnBuilder's
    calculate_skillpoints() logic. For each candidate equip order, we greedily
    assign just enough base skillpoints at each step to meet the next item's
    requirements. The ordering that minimizes total assigned SP wins.

    After finding the best order, we also run a "fix pop" pass: check that no
    item's own negative bonus would cause it to violate its own requirement in
    the final state, and bump assigned SP if needed.
    """
    n = len(items)
    if n == 0:
        return [0, 0, 0, 0, 0]

    # Unpack for fast indexed access
    reqs = [items[i][1] for i in range(n)]
    bons = [items[i][2] for i in range(n)]

    best_total = float("inf")
    best_assigned = [0, 0, 0, 0, 0]

    def can_equip(totals: list[int], idx: int) -> bool:
        r = reqs[idx]
        for i in range(5):
            if r[i] != 0 and r[i] > totals[i]:
                return False
        return True

    def apply_to_fit(totals: list[int], idx: int) -> list[int]:
        """Return extra SP needed to equip item idx given current totals.
        Also modifies totals in-place to include those extras."""
        r = reqs[idx]
        deltas = [0, 0, 0, 0, 0]
        for i in range(5):
            if r[i] != 0 and r[i] > totals[i]:
                diff = r[i] - totals[i]
                deltas[i] = diff
                totals[i] += diff
        return deltas

    def apply_bonuses(totals: list[int], idx: int):
        b = bons[idx]
        for i in range(5):
            totals[i] += b[i]

    def recurse(
        assigned: list[int],
        totals: list[int],
        total_sp: int,
        skipped_states: list[list[int]],
        prior_skipped: list[int],
        remaining: list[int],
    ):
        nonlocal best_total, best_assigned

        # Prune: can't beat current best
        if total_sp >= best_total:
            return

        if len(remaining) == 1:
            # Base case: equip last item
            idx = remaining[0]
            t = totals[:]
            deltas = apply_to_fit(t, idx)
            apply_bonuses(t, idx)

            # Fix pop: check all items stay valid with final totals
            for j in range(n):
                r = reqs[j]
                b = bons[j]
                for i in range(5):
                    if r[i] != 0:
                        # Item needs req[i], but its own bonus may be negative.
                        # After all items equipped, effective requirement is
                        # req[i] accounting for the item's own negative bonus
                        # on the final skillpoint total.
                        need = r[i] + b[i]  # isValid check
                        if need > t[i]:
                            diff = need - t[i]
                            deltas[i] += diff
                            t[i] += diff

            delta_total = sum(deltas)
            new_total = total_sp + delta_total
            if new_total >= best_total:
                return

            # Pruning: check no skipped item could have been equipped earlier
            for k, sk_idx in enumerate(prior_skipped):
                sim = [skipped_states[k][i] + deltas[i] for i in range(5)]
                if can_equip(sim, sk_idx):
                    return  # redundant ordering

            a = [assigned[i] + deltas[i] for i in range(5)]
            best_total = new_total
            best_assigned = a
            return

        # Recursive case: try each remaining item next
        for pos in range(len(remaining)):
            idx = remaining[pos]
            t = totals[:]
            deltas = apply_to_fit(t, idx)
            delta_total = sum(deltas)

            if total_sp + delta_total >= best_total:
                continue

            # Pruning: would these deltas have let a prior-skipped item equip?
            pruned = False
            for k, sk_idx in enumerate(prior_skipped):
                sim = [skipped_states[k][i] + deltas[i] for i in range(5)]
                if can_equip(sim, sk_idx):
                    pruned = True
                    break
            if pruned:
                continue

            # Pruning: would any item skipped at this level have been equippable?
            for j in range(pos):
                if can_equip(t, remaining[j]):
                    pruned = True
                    break
            if pruned:
                continue

            t2 = t[:]
            apply_bonuses(t2, idx)
            a = [assigned[i] + deltas[i] for i in range(5)]

            new_skipped = prior_skipped + remaining[:pos]
            new_states = skipped_states + [totals[:]] * pos

            new_remaining = remaining[pos + 1:] + remaining[:pos]

            recurse(a, t2, total_sp + delta_total,
                    new_states, new_skipped, new_remaining)

    recurse([0]*5, [0]*5, 0, [], [], list(range(n)))
    return best_assigned


# ---------------------------------------------------------------------------
# Output
# ---------------------------------------------------------------------------

def format_int_array(arr: list[int]) -> str:
    return "{ " + ", ".join(str(v) for v in arr) + " }"


def format_test_case(
    case_name: str,
    items: list[tuple[str, list[int], list[int]]],
    skillpoints: list[int],
) -> str:
    """Generate a Java TestCases.add(...) snippet."""
    lines = [f'add("{case_name}",']
    lines.append("        new WynnItem[] {")
    for name, reqs, bons in items:
        r = format_int_array(reqs)
        b = format_int_array(bons)
        lines.append(
            f"                new WynnItem(new int[] {r}, new int[] {b}), // {name}"
        )
    lines.append("        },")
    lines.append(f"        new int[] {format_int_array(skillpoints)},")
    # All items from a valid build are equippable
    bools = ", ".join(["true"] * len(items))
    lines.append(f"        new boolean[] {{ {bools} }});")
    return "\n".join(lines)


# ---------------------------------------------------------------------------
# Main
# ---------------------------------------------------------------------------

DEFAULT_ITEMS_DB = os.path.join(os.path.dirname(__file__), "items.json")
ITEMS_URL = "https://raw.githubusercontent.com/wynnbuilder/wynnbuilder.github.io/main/compress/items_compress.json"


def ensure_items_db(path: str):
    if os.path.exists(path):
        return
    print(f"Downloading items database to {path} ...", file=sys.stderr)
    urllib.request.urlretrieve(ITEMS_URL, path)
    print("Done.", file=sys.stderr)


def main():
    parser = argparse.ArgumentParser(
        description="Convert a WynnBuilder URL to a Java test case snippet."
    )
    parser.add_argument("url", help="WynnBuilder URL or just the fragment after #")
    parser.add_argument("case_name", nargs="?", default="case_newBuild",
                        help="Test case name (default: case_newBuild)")
    parser.add_argument("--items-db", default=DEFAULT_ITEMS_DB,
                        help=f"Path to items.json (default: {DEFAULT_ITEMS_DB})")
    args = parser.parse_args()

    # Extract fragment
    fragment = args.url
    if "#" in fragment:
        fragment = fragment.split("#", 1)[1]

    ensure_items_db(args.items_db)
    id_map = load_item_db(args.items_db)
    item_ids, skillpoints = decode_url(fragment)

    # Look up items, skip empty slots and weapon (index 8)
    gear: list[tuple[str, list[int], list[int]]] = []
    print("Decoded equipment:", file=sys.stderr)
    for i, item_id in enumerate(item_ids):
        if item_id is None:
            print(f"  {SLOT_NAMES[i]}: (empty)", file=sys.stderr)
            continue
        if i == 8:  # weapon — no skillpoint requirements in the game model
            item = id_map.get(item_id)
            name = item.get("displayName", item.get("name", "?")) if item else "?"
            print(f"  {SLOT_NAMES[i]}: {name} (skipped — weapon)", file=sys.stderr)
            continue
        item = id_map.get(item_id)
        if item is None:
            print(f"  {SLOT_NAMES[i]}: ID {item_id} NOT FOUND", file=sys.stderr)
            continue
        name = item.get("displayName", item.get("name", "?"))
        reqs, bons = item_to_wynn(item)
        gear.append((name, reqs, bons))
        print(f"  {SLOT_NAMES[i]}: {name}  req={reqs}  bon={bons}", file=sys.stderr)

    # Auto-compute skillpoints if not explicitly assigned in the URL
    if all(sp == 0 for sp in skillpoints):
        print("Skillpoints: automatic — computing minimum assignment...",
              file=sys.stderr)
        skillpoints = compute_min_skillpoints(gear)
        print(f"Computed skillpoints: {skillpoints} (total: {sum(skillpoints)})",
              file=sys.stderr)
    else:
        print(f"Skillpoints (from URL): {skillpoints}", file=sys.stderr)
    print(file=sys.stderr)

    # Output Java snippet
    print(format_test_case(args.case_name, gear, skillpoints))


if __name__ == "__main__":
    main()
