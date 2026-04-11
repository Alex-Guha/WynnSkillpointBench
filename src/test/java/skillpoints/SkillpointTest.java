package skillpoints;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Parameterized tests that run every test case against every algorithm.
 *
 * To add a new test case: add to {@link TestCases}.
 * To add a new algorithm: add to {@link #algorithms()}.
 *
 * For benchmarking, run: {@code ./gradlew jmh}
 */
public class SkillpointTest {

    // -- Per-algorithm tallies ---------------------------------------------

    private static final ConcurrentHashMap<String, AtomicInteger> passes = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, AtomicInteger> failures = new ConcurrentHashMap<>();

    @AfterAll
    static void printSummary() {
        System.out.println(
                "\n══════════════════════════════════════════════════════════════════════════════════════");
        System.out.println("  Algorithm Test Summary");
        System.out.println(
                "══════════════════════════════════════════════════════════════════════════════════════");

        Map<String, long[]> summary = new LinkedHashMap<>();
        passes.forEach((algo, cnt) -> summary.computeIfAbsent(algo, k -> new long[2])[0] = cnt.get());
        failures.forEach((algo, cnt) -> summary.computeIfAbsent(algo, k -> new long[2])[1] = cnt.get());

        for (var entry : summary.entrySet()) {
            long p = entry.getValue()[0], f = entry.getValue()[1];
            System.out.printf("  %-25s  PASS: %3d   FAIL: %3d   TOTAL: %3d%n",
                    entry.getKey(), p, f, p + f);
        }
        System.out.println(
                "══════════════════════════════════════════════════════════════════════════════════════\n");
    }

    // -- Algorithms under test ---------------------------------------------

    static Stream<Named<SkillpointChecker>> algorithms() {
        Stream<Map.Entry<String, java.util.function.Supplier<SkillpointChecker>>> stream =
                AlgorithmRegistry.all().entrySet().stream();
        String algoProp = System.getProperty("test.algo");
        if (algoProp != null) {
            Set<String> allowed = Set.of(algoProp.split(","));
            stream = stream.filter(e -> allowed.contains(e.getKey()));
        }
        return stream.map(e -> Named.of(e.getKey(), e.getValue().get()));
    }

    static WynnItem[] cloneItems(WynnItem[] items) {
        WynnItem[] cloned = new WynnItem[items.length];
        for (int index = 0; index < items.length; index++) {
            WynnItem item = items[index];
            cloned[index] = new WynnItem(item.requirements.clone(), item.bonuses.clone());
        }
        return cloned;
    }

    // -- Test cases (shared with JMH benchmark in TestCases.java) -----------

    static Stream<Named<TestCases.TestCase>> testCases() {
        String casesProp = System.getProperty("test.cases");
        Stream<Map.Entry<String, TestCases.TestCase>> stream;
        if ("curated".equals(casesProp)) {
            stream = TestCases.ALL.entrySet().stream();
        } else if ("generated".equals(casesProp)) {
            stream = GeneratedTestCases.ALL.entrySet().stream();
        } else {
            stream = Stream.concat(
                    TestCases.ALL.entrySet().stream(),
                    GeneratedTestCases.ALL.entrySet().stream());
        }
        return stream.map(e -> Named.of(e.getKey(), e.getValue()));
    }

    // -- The parameterized test --------------------------------------------

    static Stream<org.junit.jupiter.params.provider.Arguments> algorithmAndCase() {
        return algorithms()
                .flatMap(algo -> testCases()
                        .map(tc -> org.junit.jupiter.params.provider.Arguments.of(algo, tc)));
    }

    @ParameterizedTest(name = "{0} / {1}")
    @MethodSource("algorithmAndCase")
    void testEquipResult(SkillpointChecker checker, TestCases.TestCase tc) {
        String algoName = checker.getClass().getSimpleName();

        boolean[] result = checker.check(cloneItems(tc.items()), tc.assignedSkillpoints().clone());

        boolean passed = false;
        for (boolean[] acceptable : tc.acceptableResults()) {
            if (Arrays.equals(acceptable, result)) {
                passed = true;
                break;
            }
        }

        if (passed) {
            System.out.printf("[%s / %s] PASS%n", algoName, tc.name());
        } else {
            String expectedStr = tc.acceptableResults().length == 1
                    ? Arrays.toString(tc.acceptableResults()[0])
                    : Arrays.deepToString(tc.acceptableResults());
            System.out.printf("[%s / %s] FAIL%n    expected: %s%n    actual:   %s%n",
                    algoName, tc.name(), expectedStr, Arrays.toString(result));
        }

        if (passed) {
            passes.computeIfAbsent(algoName, k -> new AtomicInteger()).incrementAndGet();
        } else {
            failures.computeIfAbsent(algoName, k -> new AtomicInteger()).incrementAndGet();
        }

        boolean finalPassed = passed;
        assertTrue(finalPassed,
                () -> {
                    String expectedStr = tc.acceptableResults().length == 1
                            ? Arrays.toString(tc.acceptableResults()[0])
                            : Arrays.deepToString(tc.acceptableResults());
                    return "Case '" + tc.name() + "': expected " + expectedStr
                            + " but got " + Arrays.toString(result);
                });
    }
}
