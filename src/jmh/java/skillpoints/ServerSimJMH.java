package skillpoints;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

/**
 * JMH benchmark simulating server-side load for 40 players over some
 * time window.
 *
 * Each benchmark invocation executes:
 * <ul>
 * <li>10 equip sequences (8 permutations each, items added one at a time)</li>
 * <li>10 skill-point change sequences (1 SP at a time from zero to full)</li>
 * <li>4000 weapon swaps (single check() per swap)</li>
 * </ul>
 *
 * Builds for each operation are chosen from the 9 full-build test cases
 * using a seeded RNG, so results are deterministic and reproducible.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 1, time = 2, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Thread)
public class ServerSimJMH {

    private static final int NUM_EQUIP_SEQUENCES = 10;
    private static final int NUM_SP_CHANGES = 10;
    private static final int NUM_WEAPON_SWAPS = 4000;
    private static final long BASE_SEED = 0xCAFE_BABEL;

    // ── Parameters ──────────────────────────────────────────────────────

    @Param({}) // populated automatically from AlgorithmRegistry via build.gradle
    String algoName;

    /** Multiple runs with different seeds so Mean/Median/Worst are useful. */
    @Param({ "0", "1", "2", "3", "4" })
    int runIndex;

    // ── Pre-built workloads ─────────────────────────────────────────────

    private SkillpointChecker checker;
    private boolean needsClone;

    // Equip sequences: [seqIdx] -> (permSteps, assignedSP)
    private WynnItem[][][][] equipPermSteps;
    private int[][] equipAssignedSP;

    // SP-change sequences: [seqIdx] -> (items, spSteps)
    private WynnItem[][] spChangeItems;
    private int[][][] spChangeSteps;

    // Weapon swaps: [swapIdx] -> (items, assignedSP)
    private WynnItem[][] swapItems;
    private int[][] swapAssignedSP;

    @Setup(Level.Trial)
    public void setup() {
        checker = AlgorithmRegistry.create(algoName);
        needsClone = checker instanceof GreedyAlgorithm;

        Random rng = new Random(BASE_SEED ^ runIndex);
        String[] cases = BenchOps.FULL_BUILD_CASES;

        // ── Equip sequences ─────────────────────────────────────────────
        equipPermSteps = new WynnItem[NUM_EQUIP_SEQUENCES][][][];
        equipAssignedSP = new int[NUM_EQUIP_SEQUENCES][];

        for (int i = 0; i < NUM_EQUIP_SEQUENCES; i++) {
            var tc = TestCases.ALL.get(cases[rng.nextInt(cases.length)]);
            equipAssignedSP[i] = tc.assignedSkillpoints();
            // Each sequence gets its own sub-seed so permutations vary
            equipPermSteps[i] = BenchOps.buildEquipPermutations(
                    tc.items(), rng.nextLong(), EquipSequenceJMH.NUM_PERMUTATIONS);
        }

        // ── SP-change sequences ─────────────────────────────────────────
        spChangeItems = new WynnItem[NUM_SP_CHANGES][];
        spChangeSteps = new int[NUM_SP_CHANGES][][];

        for (int i = 0; i < NUM_SP_CHANGES; i++) {
            var tc = TestCases.ALL.get(cases[rng.nextInt(cases.length)]);
            spChangeItems[i] = SkillpointTest.cloneItems(tc.items());
            spChangeSteps[i] = BenchOps.buildSpIncrements(tc.assignedSkillpoints());
        }

        // ── Weapon swaps ────────────────────────────────────────────────
        swapItems = new WynnItem[NUM_WEAPON_SWAPS][];
        swapAssignedSP = new int[NUM_WEAPON_SWAPS][];

        for (int i = 0; i < NUM_WEAPON_SWAPS; i++) {
            var tc = TestCases.ALL.get(cases[rng.nextInt(cases.length)]);
            swapItems[i] = SkillpointTest.cloneItems(tc.items());
            swapAssignedSP[i] = tc.assignedSkillpoints();
        }
    }

    @Benchmark
    public void bench(Blackhole bh) {
        // Cache persists for the entire sim; only resets between runIndex trials
        checker.clearCache();

        // 10 equip sequences
        for (int i = 0; i < NUM_EQUIP_SEQUENCES; i++) {
            BenchOps.runEquipSequence(checker, equipPermSteps[i],
                    equipAssignedSP[i], needsClone, false, bh);
        }

        // 10 skill-point change sequences
        for (int i = 0; i < NUM_SP_CHANGES; i++) {
            BenchOps.runSpChange(checker, spChangeItems[i],
                    spChangeSteps[i], needsClone, false, bh);
        }

        // 4000 weapon swaps
        for (int i = 0; i < NUM_WEAPON_SWAPS; i++) {
            BenchOps.runWeaponSwap(checker, swapItems[i],
                    swapAssignedSP[i], needsClone, false, bh);
        }
    }
}
