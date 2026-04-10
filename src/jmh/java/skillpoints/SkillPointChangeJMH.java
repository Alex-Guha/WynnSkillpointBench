package skillpoints;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

/**
 * JMH benchmark simulating incremental skill-point allocation.
 *
 * Starting from zero assigned skill points, adds 1 point at a time in
 * round-robin order across attributes until the full allocation defined
 * by the test case is reached. The algorithm is re-run after every
 * single point change.
 *
 * This models the scenario where a player adjusts their skill-point
 * build one point at a time and the server re-validates on each change.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 1, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 3, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Fork(1)
@State(Scope.Thread)
public class SkillPointChangeJMH {

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
    private int[][] spSteps;

    @Setup(Level.Trial)
    public void setup() {
        checker = AlgorithmRegistry.create(algoName);
        needsClone = checker instanceof GreedyAlgorithm;

        var tc = TestCases.ALL.get(caseName);
        if (tc == null)
            throw new IllegalArgumentException("Unknown test case: " + caseName);

        // Pre-clone items once (they don't change across SP steps)
        items = SkillpointTest.cloneItems(tc.items());
        spSteps = BenchOps.buildSpIncrements(tc.assignedSkillpoints());
    }

    @Benchmark
    public void bench(Blackhole bh) {
        BenchOps.runSpChange(checker, items, spSteps, needsClone, true, bh);
    }
}
