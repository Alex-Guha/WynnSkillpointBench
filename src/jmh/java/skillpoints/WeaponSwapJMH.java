package skillpoints;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

/**
 * JMH benchmark simulating a weapon swap.
 *
 * Takes a full build and re-runs the algorithm for the same equipment,
 * modelling the common case where a player swaps to a weapon and the
 * server must re-validate the entire build.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 1, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 3, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Fork(1)
@State(Scope.Thread)
public class WeaponSwapJMH {

    // ── Parameters ──────────────────────────────────────────────────────

    @Param({}) // populated automatically from AlgorithmRegistry via build.gradle
    String algoName;

    @Param({
            "case8_fullBuild_8items",
            "case9_dexIntAgiBuild",
            "case10_intAgiHeavyMageBuild",
            "case11_strDexDefWarriorBuild",
            "case12_strStackingBuildWithNegativeAgi",
            "case13_intAgiMageBuildWithMoontowersLargeNegatives",
            "case14_strIntMeleeBuild",
            "case16_strStackingWithCascadingBonuses",
            "case18_multiStatAllEquip",
    })
    String caseName;

    // ── Resolved state ──────────────────────────────────────────────────

    private SkillpointChecker checker;
    private boolean needsClone;
    private WynnItem[] items;
    private int[] assignedSkillpoints;

    @Setup(Level.Trial)
    public void setup() {
        checker = AlgorithmRegistry.create(algoName);
        needsClone = checker instanceof GreedyAlgorithm;

        var tc = TestCases.ALL.get(caseName);
        if (tc == null)
            throw new IllegalArgumentException("Unknown test case: " + caseName);

        items = SkillpointTest.cloneItems(tc.items());
        assignedSkillpoints = tc.assignedSkillpoints();
    }

    @Benchmark
    public void bench(Blackhole bh) {
        BenchOps.runWeaponSwap(checker, items, assignedSkillpoints, needsClone, true, bh);
    }
}
