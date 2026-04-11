# WynnSkillpointBench

Benchmark and correctness testing for Wynncraft skillpoint equip-ordering algorithms.

## Current Standings

### Correctness

| Algorithm | PASS | FAIL | TOTAL |
|---|---|---|---|
| TheThirdAlgorithm | 27 | 0 | 27 |
| MyFirstAlgorithm | 27 | 0 | 27 |
| CascadeBoundChecker | 27 | 0 | 27 |
| WynnSolverAlgorithm | 27 | 0 | 27 |
| OurSecondAlgorithm | 27 | 0 | 27 |
| MySecondAlgorithm | 27 | 0 | 27 |
| TheFourthAlgorithm | 27 | 0 | 27 |
| WynnAlgorithm | 25 | 2 | 27 |
| GreedyAlgorithm | 26 | 1 | 27 |
| SCCGraphAlgorithm | 26 | 1 | 27 |

### Performance (ServerSimJMH)

| Algorithm | Mean(us/run) | Median | Worst | vs 1st |
|---|---:|---:|---:|---:|
| TheThirdAlgorithm | 918.711 | 924.964 | 945.626 | 1.0x |
| TheFourthAlgorithm | 919.826 | 910.667 | 940.139 | 1.0x |
| OurSecondAlgorithm | 1090.715 | 1104.575 | 1152.163 | 1.2x |
| CascadeBound | 1682.988 | 1709.582 | 1722.333 | 1.8x |
| MySecondAlgorithm | 2216.825 | 2144.239 | 2487.513 | 2.4x |
| MyFirstAlgorithm | 2229.100 | 2217.632 | 2405.998 | 2.4x |
| GreedyAlgorithm | 3270.796 | 3199.790 | 3633.044 | 3.6x |
| WynnSolver | 4546.241 | 4565.405 | 4909.946 | 4.9x |
| WynnAlgorithm | 6854.298 | 6888.420 | 8439.641 | 7.5x |
| SCCGraphAlgorithm | 8836.854 | 8397.180 | 10116.290 | 9.6x |


## Skill Point Algorithm Bounty

Wynncraft is seeking an optimized **Skill Point allocation algorithm** capable of efficiently validating and maximizing equipment combinations under strict performance constraints.

A bounty reward of **up to 100 in-game shares** will be granted for a successful solution.
Exceptional implementations may qualify for a higher reward.

### Objective

Design an algorithm that:
- Accepts a given set of equipment items
   - 4 armor & 4 accessories
   - 1 Weapon
   - Tomes (implied that the function should accept 14 tomes, but only guild tome is relevant to sp)
      - The simplest way to handle guild tome is probably to just add the sp provisions to the base assigned array
- Evaluates viable combinations
- Returns a list of true/false for the **combination containing the highest number of valid items**
   - In case of a combination tie, the combination with the **highest total given skill points** should win

### Requirements

Your solution must be written in **Java** so we can evaluate on a real scenario.

#### Performance
- **Worst-case execution time:** ≤ 200,000 nanoseconds
- **Target average execution time:** ≤ 70,000 nanoseconds

#### Functional Constraints
- Each piece of equipment has **skill point requirements** that must be validated
- Equipment may **add or subtract skill points** when equipped
- Skill points from equipment **must not recursively enable other equipment** (no bootstrapping between items)

#### Validation Rules
- A piece of equipment is considered **valid** only if all its requirements are met at the time of evaluation
- The algorithm must determine validity across the full combination, **the order of items should not be relevant**
- In case of a combination tie, the combination with the **highest total given skill points** should win

### Example Edge Cases

TODO

### Q&A

In practice, would the algo run on:
- every item equip/unequip? yes
- every skill point change? yes
- every weapon swap? yes

Should weapon, set bonus, and crafted SP provisions be considered for tie breaking? No

---

## Algorithms

| Class | Approach | Worst-case Time |
|-------|----------|-----------------|
| `WynnAlgorithm` | Greedy positives + 2^n negative-mask enumeration | O(n² · 2^q), q = negative items. All-negative worst case: O(n² · 2^n) |
| `SCCGraphAlgorithm` | Dependency graph → Kosaraju SCC → permute within SCCs | O(n · ∏mᵢ!) across SCC sizes mᵢ. Single-SCC worst case: O(n · n!) |
| `OptimizedDFS` | DFS with dominance pruning + bitmask memoization | O(m · 2^m), m = non-free items after preprocessing (hard-coded m ≤ 8) |
| `WynnSolverAlgorithm` | Free-item activation + backtracking over activation orderings with cascade validity | O(n · k!), k = non-free items. Worst case: O(n · n!) but pruning + early exit keep real builds fast |
| `GreedyAlgorithm` | Greedy with minimum tracking + negative-bonus adjusted requirements | O(n²) |
| `ExactMaskDpChecker` | Precompute sustainability for all masks + BFS over reachable masks | O(n · 2^n) |
| `CascadeBoundChecker` | Forced-closure for safe items + DFS with bitmask memoization over branch items | O(f² · b · 2^n), f = forced (safe) items, b = branch (negative/risky) items. All-branch worst case: O(n · 2^n) |
| `MyFirstAlgorithm` | Greedy fast path + BFS bitmask DP fallback with sustainability checks | O(m² · 2^m), m = non-free items (hard-capped m ≤ 8). Greedy-only best case: O(n²) |
| `MySecondAlgorithm` | Unconditional greedy + BFS bitmask DP with vulnerability masks for narrowed sustainability checks | O(m² · 2^m), m = non-free items (hard-capped m ≤ 8). Greedy-only best case: O(n²) |
| `OurSecondAlgorithm` | Greedy + SWAR-packed BitmaskDP (5 dims in one long, 12-bit slots) with maxNeed precomputation | O(m · 2^m), m = non-free items (hard-capped m ≤ 8). Greedy-only best case: O(n²) |
| `TheThirdAlgorithm` | Base-case shortcuts (1-2 items) + greedy + SWAR-packed BitmaskDP with bitset BFS and no-negative-bonus shortcut | O(m · 2^m), m = non-free items (hard-capped m ≤ 8). Greedy-only best case: O(n²) |
| `TheFourthAlgorithm` | TheThirdAlgorithm + absent-item bit-iteration (BLSR+TZCNT) + globalMaxReq batch requirement check | O(m · 2^m), m = non-free items (hard-capped m ≤ 8). Greedy-only best case: O(n²) |

All algorithms extending `SkillpointChecker` implement:
```java
boolean[] check(WynnItem[] items, int[] assignedSkillpoints)
```
Returns a boolean array indicating which items can be equipped.

If your algorithm uses caching, you must also override:
```java
void clearCache()
```
The base class provides a no-op default. Benchmarks call `clearCache()` between independent scenarios (e.g. between equip-sequence permutations, before SP-change sequences, and before weapon swaps) to ensure cached state from one scenario does not leak into another.

## Requirements

- **Java 21** (Gradle needs 21)

## Quick Start

```bash
# Run correctness tests (all algorithms × all test cases)
./gradlew test

# Run correctness tests for a specific algorithm
./gradlew test -Palgo=YourAlgorithm

# Run the server-simulation benchmark (the primary performance metric)
./gradlew jmhRun -Pbm=ServerSimJMH

# Run ServerSimJMH for a specific algorithm
./gradlew jmhRun -Palgo=YourAlgorithm -Pbm=ServerSimJMH

# Clean first if you changed algorithm code (ensures no stale bytecode in the JMH jar)
./gradlew clean jmhRun -Pbm=ServerSimJMH
```

If your default Java is not 21, prefix commands with `JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64` (or wherever your java21 is at).

## Tests

Tests are in `src/test/java/skillpoints/SkillpointTest.java`. They use JUnit 5 parameterized tests — every test case runs against every algorithm.

**Adding a test case:** add an entry to `TestCases.java` (hand-written, curated cases shared between tests and benchmarks).

**Auto-generated test cases** live in `GeneratedTestCases.java` — these are produced by the python scripts in `scripts/python/` and are run automatically alongside the hand-written cases via `./gradlew test`. To run only one set:

```bash
./gradlew test -Pcases=curated    # only hand-written TestCases
./gradlew test -Pcases=generated  # only GeneratedTestCases
./gradlew test                    # both (default)
```

**Adding an algorithm:**

1. Create your class in `src/main/java/skillpoints/`, extending `SkillpointChecker`
2. Add a `REGISTRY.put(...)` entry to `AlgorithmRegistry.java`
3. Run tests and the benchmark:
   ```bash
   ./gradlew test -Palgo=YourAlgorithm
   ./gradlew jmhRun -Palgo=YourAlgorithm -Pbm=ServerSimJMH
   ```

## Benchmark — ServerSimJMH

`ServerSimJMH` is the primary benchmark for evaluating algorithm performance. It simulates a rough (presumptuous) estimate of a server-side load for 40 players over a time window, combining all the real-world scenarios into a single mixed workload. Each benchmark invocation executes:
- **10 equip sequences** (8 permutations each — items added one at a time)
- **10 skill-point change sequences** (1 SP at a time from zero to full allocation)
- **4000 weapon swaps** (single `check()` per swap)

Builds are chosen from the 9 full-build test cases using a seeded RNG (`0xCAFE_BABEL`), so results are deterministic.

Benchmarking uses [JMH](https://github.com/openjdk/jmh) (Java Microbenchmark Harness) for statistically rigorous results. JMH handles JIT warmup detection, fork isolation, dead-code elimination prevention, and reports confidence intervals.

Results are written to `build/results/jmh/results.json`.

```bash
# Run ServerSimJMH for all algorithms
./gradlew jmhRun -Pbm=ServerSimJMH

# Run for specific algorithm(s)
./gradlew jmhRun -Palgo=WynnAlgorithm -Pbm=ServerSimJMH
./gradlew jmhRun -Palgo=WynnAlgorithm,WynnSolver -Pbm=ServerSimJMH
```

---

<details>
<summary>Other benchmark classes</summary>

The individual scenario benchmarks below are what `ServerSimJMH` combines. They can be run separately for debugging or profiling a specific scenario but are not used for overall algorithm evaluation.

Default config: 1 fork, 1×200ms warmup, 3×200ms measurement, average time in microseconds. All operate on the 9 full-build (8-item) test cases. Shared setup and run logic lives in `BenchOps.java`.

### `EquipSequenceJMH` — Item-by-item equipping

Simulates how a player actually equips gear — one piece at a time, with the algorithm rerunning on each equip.

For each full-build test case:
1. 8 seeded random permutations of equip order are generated
2. For each permutation, items are added incrementally (1 item, then 2, …, then all 8), calling `check()` at each step
3. Cache is preserved within a permutation but cleared between permutations

Each `@Benchmark` invocation runs all 8 permutations (64 total `check()` calls).

### `SkillPointChangeJMH` — Incremental SP allocation

Simulates a player adjusting their skill-point build one point at a time. Starting from zero assigned skill points, adds 1 point per step in round-robin order across attributes until the full allocation defined by the test case is reached, rerunning the algorithm after every single point change.

Total `check()` calls per invocation = sum of all assigned skill points (e.g. 191 for case8).

### `WeaponSwapJMH` — Single re-validation

Simulates a weapon swap: a single `check()` call with the full build. The simplest benchmark — measures the cost of one complete re-validation.

### Running individual benchmarks

```bash
./gradlew jmhRun -Pbm=EquipSequenceJMH
./gradlew jmhRun -Pbm=SkillPointChangeJMH
./gradlew jmhRun -Pbm=WeaponSwapJMH

# Specific algo + benchmark class + case
./gradlew jmhRun -Palgo=WynnSolver -Pcase=case8_fullBuild_8items -Pbm=EquipSequenceJMH

# Run all benchmarks including all classes
./gradlew jmh
```

</details>
