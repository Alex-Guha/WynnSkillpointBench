package skillpoints;

import java.util.Random;

import org.openjdk.jmh.infra.Blackhole;

/**
 * Shared building blocks for skillpoint benchmarks.
 *
 * Each operation type has a static {@code prepare} method (called once in
 * {@code @Setup}) and a static {@code run} method (called per invocation in
 * {@code @Benchmark}).
 */
public final class BenchOps {

    /** All 8-item full-build test-case names. */
    public static final String[] FULL_BUILD_CASES = {
            "case8_fullBuild_8items",
            "case9_dexIntAgiBuild",
            "case10_intAgiHeavyMageBuild",
            "case11_strDexDefWarriorBuild",
            "case12_strStackingBuildWithNegativeAgi",
            "case13_intAgiMageBuildWithMoontowersLargeNegatives",
            "case14_strIntMeleeBuild",
            "case16_strStackingWithCascadingBonuses",
            "case18_multiStatAllEquip",
    };

    private BenchOps() {}

    // ── Equip Sequence ──────────────────────────────────────────────────

    /**
     * Build incremental equip-step arrays for {@code numPerms} seeded
     * random permutations of the given items.
     *
     * @return {@code [perm][step] = WynnItem[]} where step k has items 0..k
     */
    public static WynnItem[][][] buildEquipPermutations(WynnItem[] items, long seed, int numPerms) {
        int n = items.length;
        Random rng = new Random(seed);
        WynnItem[][][] out = new WynnItem[numPerms][][];

        for (int p = 0; p < numPerms; p++) {
            int[] order = new int[n];
            for (int i = 0; i < n; i++) order[i] = i;
            for (int i = n - 1; i > 0; i--) {
                int j = rng.nextInt(i + 1);
                int tmp = order[i]; order[i] = order[j]; order[j] = tmp;
            }

            out[p] = new WynnItem[n][];
            for (int step = 0; step < n; step++) {
                WynnItem[] stepItems = new WynnItem[step + 1];
                for (int k = 0; k <= step; k++) {
                    WynnItem orig = items[order[k]];
                    stepItems[k] = new WynnItem(orig.requirements.clone(), orig.bonuses.clone());
                }
                out[p][step] = stepItems;
            }
        }
        return out;
    }

    /**
     * Run one full equip-sequence workload (all permutations, all steps).
     *
     * @param clearCache if true, cache is cleared between permutations
     *                   (standalone benchmark behaviour); if false, cache
     *                   persists across the whole call (server-sim behaviour)
     */
    public static void runEquipSequence(SkillpointChecker checker,
                                        WynnItem[][][] permSteps,
                                        int[] assignedSP,
                                        boolean needsClone,
                                        boolean clearCache,
                                        Blackhole bh) {
        for (WynnItem[][] steps : permSteps) {
            if (clearCache) checker.clearCache();
            for (WynnItem[] stepItems : steps) {
                WynnItem[] items;
                int[] sp;
                if (needsClone) {
                    items = SkillpointTest.cloneItems(stepItems);
                    sp = assignedSP.clone();
                } else {
                    items = stepItems;
                    sp = assignedSP;
                }
                bh.consume(checker.check(items, sp));
            }
        }
    }

    // ── Skill-Point Change ──────────────────────────────────────────────

    /**
     * Build an array of incremental SP allocations from {@code [0,0,0,0,0]}
     * to {@code targetSP}, adding 1 point per step in round-robin order
     * across attributes that still need points.
     *
     * @return {@code int[totalSteps][5]} where totalSteps = sum(targetSP)
     */
    public static int[][] buildSpIncrements(int[] targetSP) {
        int total = 0;
        for (int v : targetSP) total += v;

        int[][] steps = new int[total][];
        int[] current = new int[5];
        int idx = 0;

        while (idx < total) {
            for (int attr = 0; attr < 5 && idx < total; attr++) {
                if (current[attr] < targetSP[attr]) {
                    current[attr]++;
                    steps[idx] = current.clone();
                    idx++;
                }
            }
        }
        return steps;
    }

    /**
     * Run one full SP-change workload: check() at every incremental SP step.
     *
     * @param clearCache if true, cache is cleared at the start
     */
    public static void runSpChange(SkillpointChecker checker,
                                   WynnItem[] items,
                                   int[][] spSteps,
                                   boolean needsClone,
                                   boolean clearCache,
                                   Blackhole bh) {
        if (clearCache) checker.clearCache();
        for (int[] sp : spSteps) {
            WynnItem[] runItems;
            int[] runSP;
            if (needsClone) {
                runItems = SkillpointTest.cloneItems(items);
                runSP = sp.clone();
            } else {
                runItems = items;
                runSP = sp;
            }
            bh.consume(checker.check(runItems, runSP));
        }
    }

    // ── Weapon Swap ─────────────────────────────────────────────────────

    /**
     * Run one weapon-swap: a single check() with the full build.
     *
     * @param clearCache if true, cache is cleared before the call
     */
    public static void runWeaponSwap(SkillpointChecker checker,
                                     WynnItem[] items,
                                     int[] assignedSP,
                                     boolean needsClone,
                                     boolean clearCache,
                                     Blackhole bh) {
        if (clearCache) checker.clearCache();
        WynnItem[] runItems;
        int[] runSP;
        if (needsClone) {
            runItems = SkillpointTest.cloneItems(items);
            runSP = assignedSP.clone();
        } else {
            runItems = items;
            runSP = assignedSP;
        }
        bh.consume(checker.check(runItems, runSP));
    }
}
