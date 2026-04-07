package skillpoints;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Shared test-case data used by both JUnit tests and JMH benchmarks.
 */
public final class TestCases {

    public record TestCase(
            String name,
            WynnItem[] items,
            int[] assignedSkillpoints,
            boolean[] expectedEquippable) {
    }

    /** All test cases, keyed by name. Insertion-ordered. */
    public static final Map<String, TestCase> ALL = new LinkedHashMap<>();

    static {
        add("case1_optimal",
                new WynnItem[] {
                        new WynnItem(new int[] { 50, 0, 0, 40, 0 }, new int[] { 9, 0, 0, 8, 0 }),
                        new WynnItem(new int[] { 75, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 10, 0 }),
                        new WynnItem(new int[] { 50, 0, 0, 0, 0 }, new int[] { 7, 0, 0, -3, 0 }),
                },
                new int[] { 68, 0, 0, 33, 0 },
                new boolean[] { true, true, true });

        add("case1_subopt_assign",
                new WynnItem[] {
                        new WynnItem(new int[] { 50, 0, 0, 40, 0 }, new int[] { 9, 0, 0, 8, 0 }),
                        new WynnItem(new int[] { 75, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 10, 0 }),
                        new WynnItem(new int[] { 50, 0, 0, 0, 0 }, new int[] { 7, 0, 0, -3, 0 }),
                },
                new int[] { 59, 0, 0, 43, 0 },
                new boolean[] { true, true, true });

        add("case1_tff",
                new WynnItem[] {
                        new WynnItem(new int[] { 50, 0, 0, 40, 0 }, new int[] { 9, 0, 0, 8, 0 }),
                        new WynnItem(new int[] { 75, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 10, 0 }),
                        new WynnItem(new int[] { 50, 0, 0, 0, 0 }, new int[] { 7, 0, 0, -3, 0 }),
                },
                new int[] { 59, 0, 0, 40, 0 },
                new boolean[] { true, false, false });

        add("case2_strictChain_abc",
                new WynnItem[] {
                        new WynnItem(new int[] { 1, 0, 0, 0, 0 }, new int[] { 0, 2, -1, 0, 0 }),
                        new WynnItem(new int[] { 0, 2, 0, 0, 0 }, new int[] { 0, 0, 1, 0, 0 }),
                        new WynnItem(new int[] { 0, 0, 1, 0, 0 }, new int[] { 0, 0, 0, 1, 0 }),
                },
                new int[] { 1, 0, 1, 0, 0 },
                new boolean[] { true, true, true });

        add("case2_strictChain_cab",
                new WynnItem[] {
                        new WynnItem(new int[] { 0, 0, 1, 0, 0 }, new int[] { 0, 0, 0, 1, 0 }),
                        new WynnItem(new int[] { 1, 0, 0, 0, 0 }, new int[] { 0, 2, -1, 0, 0 }),
                        new WynnItem(new int[] { 0, 2, 0, 0, 0 }, new int[] { 0, 0, 1, 0, 0 }),
                },
                new int[] { 1, 0, 1, 0, 0 },
                new boolean[] { true, true, true });

        add("case3_noRequirements",
                new WynnItem[] {
                        new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 5, 5, 5, 5, 5 }),
                        new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { -2, 0, 0, 0, 0 }),
                },
                new int[] { 0, 0, 0, 0, 0 },
                new boolean[] { true, true });

        add("case4_impossibleRequirements",
                new WynnItem[] {
                        new WynnItem(new int[] { 100, 0, 0, 0, 0 }, new int[] { 5, 0, 0, 0, 0 }),
                },
                new int[] { 10, 0, 0, 0, 0 },
                new boolean[] { false });

        add("case5_negativeInvalidatesPrior",
                new WynnItem[] {
                        new WynnItem(new int[] { 10, 0, 0, 0, 0 }, new int[] { 5, 0, 0, 0, 0 }),
                        new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { -20, 0, 0, 0, 0 }),
                },
                new int[] { 10, 0, 0, 0, 0 },
                new boolean[] { true, false });

        add("case6_exactRequirement",
                new WynnItem[] {
                        new WynnItem(new int[] { 50, 30, 0, 0, 0 }, new int[] { 0, 0, 10, 0, 0 }),
                },
                new int[] { 50, 30, 0, 0, 0 },
                new boolean[] { true });

        add("case7_mutualDependency",
                new WynnItem[] {
                        new WynnItem(new int[] { 0, 10, 0, 0, 0 }, new int[] { 10, 0, 0, 0, 0 }),
                        new WynnItem(new int[] { 10, 0, 0, 0, 0 }, new int[] { 0, 10, 0, 0, 0 }),
                },
                new int[] { 0, 0, 0, 0, 0 },
                new boolean[] { false, false });

        add("case8_fullBuild_8items",
                new WynnItem[] {
                        new WynnItem(new int[] { 40, 0, 0, 40, 40 }, new int[] { 9, 0, 0, 9, 9 }),
                        new WynnItem(new int[] { 0, 15, 0, 0, 50 }, new int[] { 0, 15, 0, 0, 25 }),
                        new WynnItem(new int[] { 30, 30, 30, 30, 30 }, new int[] { 8, 8, 8, 8, 8 }),
                        new WynnItem(new int[] { 40, 70, 0, 0, 0 }, new int[] { 13, 0, -50, 0, 0 }),
                        new WynnItem(new int[] { 25, 0, 0, 0, 0 }, new int[] { 5, 0, 0, 0, -3 }),
                        new WynnItem(new int[] { 25, 25, 25, 25, 25 }, new int[] { 3, 3, 3, 3, 3 }),
                        new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 4, 4, 4, 4, 4 }),
                        new WynnItem(new int[] { 50, 0, 0, 0, 0 }, new int[] { 7, 0, 0, -3, 0 }),
                },
                new int[] { 21, 40, 73, 28, 29 },
                new boolean[] { true, true, true, true, true, true, true, true });

        add("case9_dexIntAgiBuild",
                new WynnItem[] {
                        new WynnItem(new int[] { 0, 40, 40, 0, 40 }, new int[] { -30, 0, 0, -30, 0 }),
                        new WynnItem(new int[] { 0, 50, 0, 0, 65 }, new int[] { 0, 8, 0, 0, 0 }),
                        new WynnItem(new int[] { 0, 50, 55, 0, 50 }, new int[] { 0, 6, 4, 0, 6 }),
                        new WynnItem(new int[] { 0, 55, 0, 0, 55 }, new int[] { 0, 0, 0, 0, 0 }),
                        new WynnItem(new int[] { 0, 50, 0, 0, 45 }, new int[] { 0, 4, 0, 0, 0 }),
                        new WynnItem(new int[] { 0, 60, 0, 0, 0 }, new int[] { 0, 0, 6, 0, 0 }),
                        new WynnItem(new int[] { 0, 45, 45, 0, 45 }, new int[] { -15, 0, 0, 0, 0 }),
                        new WynnItem(new int[] { 0, 45, 0, 0, 0 }, new int[] { 0, 8, 0, 0, 0 }),
                },
                new int[] { 0, 45, 49, 0, 65 },
                new boolean[] { true, true, true, true, true, true, true, true });

        add("case10_intAgiHeavyMageBuild",
                new WynnItem[] {
                        new WynnItem(new int[] { 0, 0, 100, 0, 0 }, new int[] { 0, -80, 5, 0, 0 }),
                        new WynnItem(new int[] { 50, 0, 65, 0, 50 }, new int[] { 0, 0, 0, 0, 0 }),
                        new WynnItem(new int[] { 50, 0, 55, 0, 0 }, new int[] { 6, 0, 6, 0, 0 }),
                        new WynnItem(new int[] { 0, 0, 70, 0, 80 }, new int[] { -10, -10, 35, -40, 60 }),
                        new WynnItem(new int[] { 0, 0, 55, 0, 0 }, new int[] { 0, 0, 4, 0, 0 }),
                        new WynnItem(new int[] { 0, 0, 65, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }),
                        new WynnItem(new int[] { 0, 0, 100, 0, 0 }, new int[] { 0, 0, 6, 0, 0 }),
                        new WynnItem(new int[] { 0, 0, 55, 0, 0 }, new int[] { 0, 0, 4, 0, 0 }),
                },
                new int[] { 60, 0, 60, 0, 80 },
                new boolean[] { true, true, true, true, true, true, true, true });

        add("case11_strDexDefWarriorBuild",
                new WynnItem[] {
                        new WynnItem(new int[] { 75, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }),
                        new WynnItem(new int[] { 0, 0, 0, 65, 0 }, new int[] { 0, 10, 0, 5, 0 }),
                        new WynnItem(new int[] { 49, 31, 0, 37, 0 }, new int[] { 0, 0, 0, 0, -43 }),
                        new WynnItem(new int[] { 60, 0, 0, 70, 0 }, new int[] { 20, 0, 0, 25, 0 }),
                        new WynnItem(new int[] { 0, 45, 0, 0, 0 }, new int[] { 0, 6, 0, 0, 0 }),
                        new WynnItem(new int[] { 0, 45, 0, 0, 0 }, new int[] { 0, 6, 0, 0, 0 }),
                        new WynnItem(new int[] { 45, 0, 0, 50, 0 }, new int[] { 0, 0, 0, 7, 0 }),
                        new WynnItem(new int[] { 0, 50, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }),
                },
                new int[] { 60, 58, 0, 58, 0 },
                new boolean[] { true, true, true, true, true, true, true, true });

        add("case12_strStackingBuildWithNegativeAgi",
                new WynnItem[] {
                        new WynnItem(new int[] { 75, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }),
                        new WynnItem(new int[] { 90, 0, 0, 0, 0 }, new int[] { 15, 0, 0, 0, 0 }),
                        new WynnItem(new int[] { 49, 31, 0, 37, 0 }, new int[] { 0, 0, 0, 0, -43 }),
                        new WynnItem(new int[] { 0, 65, 0, 65, 0 }, new int[] { 0, 0, 0, 0, 0 }),
                        new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 3, 3, 0, 0, 0 }),
                        new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 3, 3, 0, 0, 0 }),
                        new WynnItem(new int[] { 100, 0, 0, 0, 0 }, new int[] { 6, 0, 0, 0, 0 }),
                        new WynnItem(new int[] { 0, 40, 0, 40, 0 }, new int[] { 0, 7, 0, 0, 0 }),
                },
                new int[] { 84, 52, 0, 65, 0 },
                new boolean[] { true, true, true, true, true, true, true, true });

        add("case13_intAgiMageBuildWithMoontowersLargeNegatives",
                new WynnItem[] {
                        new WynnItem(new int[] { 40, 0, 40, 0, 0 }, new int[] { 7, 0, 7, 0, 0 }),
                        new WynnItem(new int[] { 0, 0, 120, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }),
                        new WynnItem(new int[] { 0, 0, 0, 0, 70 }, new int[] { 0, 0, 0, 0, 5 }),
                        new WynnItem(new int[] { 0, 0, 70, 0, 80 }, new int[] { -10, -10, 35, -40, 60 }),
                        new WynnItem(new int[] { 0, 0, 45, 0, 45 }, new int[] { 0, 0, 0, 0, 0 }),
                        new WynnItem(new int[] { 0, 0, 45, 0, 45 }, new int[] { 0, 0, 0, 0, 0 }),
                        new WynnItem(new int[] { 0, 0, 0, 0, 50 }, new int[] { 0, 0, 0, 0, 6 }),
                        new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }),
                },
                new int[] { 50, 0, 78, 0, 69 },
                new boolean[] { true, true, true, true, true, true, true, true });

        add("case14_strIntMeleeBuild",
                new WynnItem[] {
                        new WynnItem(new int[] { 65, 0, 0, 0, 0 }, new int[] { 10, 0, 0, 0, -5 }),
                        new WynnItem(new int[] { 50, 0, 50, 50, 0 }, new int[] { 0, 0, 0, 0, 0 }),
                        new WynnItem(new int[] { 105, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }),
                        new WynnItem(new int[] { 60, 0, 60, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }),
                        new WynnItem(new int[] { 0, 0, 0, 45, 0 }, new int[] { 0, 0, 0, 4, 0 }),
                        new WynnItem(new int[] { 50, 0, 0, 0, 0 }, new int[] { 4, 0, 0, 2, 0 }),
                        new WynnItem(new int[] { 60, 0, 0, 0, 0 }, new int[] { 0, 4, 0, 0, 0 }),
                        new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }),
                },
                new int[] { 96, 0, 60, 44, 0 },
                new boolean[] { true, true, true, true, true, true, true, true });

        add("case15_doubleDiamondHydroRings",
                new WynnItem[] {
                        new WynnItem(new int[] { 0, 0, 100, 0, 0 }, new int[] { 0, 0, 5, 0, 0 }),
                        new WynnItem(new int[] { 0, 0, 100, 0, 0 }, new int[] { 0, 0, 5, 0, 0 }),
                },
                new int[] { 0, 0, 100, 0, 0 },
                new boolean[] { true, true });

        add("case15_doubleDiamondHydroRings_fail",
                new WynnItem[] {
                        new WynnItem(new int[] { 0, 0, 100, 0, 0 }, new int[] { 0, 0, 5, 0, 0 }),
                        new WynnItem(new int[] { 0, 0, 100, 0, 0 }, new int[] { 0, 0, 5, 0, 0 }),
                },
                new int[] { 0, 0, 95, 0, 0 },
                new boolean[] { false, false });

        add("case16_strStackingWithCascadingBonuses",
                new WynnItem[] {
                        new WynnItem(new int[] { 75, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }),
                        new WynnItem(new int[] { 90, 0, 0, 0, 0 }, new int[] { 15, 0, 0, 0, 0 }),
                        new WynnItem(new int[] { 75, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }),
                        new WynnItem(new int[] { 95, 0, 0, 0, 0 }, new int[] { 10, 0, 0, 0, 0 }),
                        new WynnItem(new int[] { 100, 0, 0, 0, 0 }, new int[] { 7, 0, 0, 0, 0 }),
                        new WynnItem(new int[] { 100, 0, 0, 0, 0 }, new int[] { 7, 0, 0, 0, 0 }),
                        new WynnItem(new int[] { 50, 0, 0, 0, 0 }, new int[] { 5, 3, 0, 0, 0 }),
                        new WynnItem(new int[] { 0, 50, 0, 50, 0 }, new int[] { 0, 0, 0, 0, 0 }),
                },
                new int[] { 85, 57, 0, 50, 0 },
                new boolean[] { true, true, true, true, true, true, true, true });

        add("case17_negDefBlocksChain",
                new WynnItem[] {
                        new WynnItem(new int[] { 75, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 10, 0 }),
                        new WynnItem(new int[] { 50, 0, 0, 50, 0 }, new int[] { 9, 0, 0, 8, 0 }),
                        new WynnItem(new int[] { 50, 0, 0, 0, 0 }, new int[] { 7, 0, 0, -3, 0 }),
                },
                new int[] { 59, 0, 0, 40, 0 },
                new boolean[] { false, false, true });

        add("case18_multiStatAllEquip",
                new WynnItem[] {
                        new WynnItem(new int[] { 0, 45, 0, 45, 0 }, new int[] { 10, 10, 0, 10, 0 }),
                        new WynnItem(new int[] { 0, 50, 0, 55, 0 }, new int[] { 0, 5, -35, 5, 0 }),
                        new WynnItem(new int[] { 50, 0, 0, 40, 0 }, new int[] { 9, 0, 0, 8, 0 }),
                        new WynnItem(new int[] { 0, 65, 0, 65, 0 }, new int[] { 0, 0, 0, 0, 0 }),
                        new WynnItem(new int[] { 45, 0, 0, 0, 55 }, new int[] { 0, 0, 0, 0, 5 }),
                        new WynnItem(new int[] { 45, 0, 0, 0, 55 }, new int[] { 0, 0, 0, 0, 5 }),
                        new WynnItem(new int[] { 0, 25, 0, 0, 0 }, new int[] { 0, 6, 0, 0, 0 }),
                        new WynnItem(new int[] { 0, 0, 0, 0, 45 }, new int[] { 0, 0, 0, 0, 0 }),
                },
                new int[] { 48, 47, 0, 45, 60 },
                new boolean[] { true, true, true, true, true, true, true, true });

        add("case19_dualRingsWithNegNecklace",
                new WynnItem[] {
                        new WynnItem(new int[] { 100, 0, 0, 0, 0 }, new int[] { 7, 0, 0, 0, 0 }),
                        new WynnItem(new int[] { 100, 0, 0, 0, 0 }, new int[] { 7, 0, 0, 0, 0 }),
                        new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { -3, 0, 0, 0, 0 }),
                },
                new int[] { 100, 0, 0, 0, 0 },
                new boolean[] { true, true, true });

        add("case20_bothDisabledInsufficientStr",
                new WynnItem[] {
                        new WynnItem(new int[] { 40, 70, 0, 0, 0 }, new int[] { 13, 0, -50, 0, 0 }),
                        new WynnItem(new int[] { 50, 0, 0, 0, 0 }, new int[] { 7, 0, 0, 0, 0 }),
                },
                new int[] { 37, 70, 0, 0, 0 },
                new boolean[] { false, false });

        add("case21_repurposedVessels",
                new WynnItem[] {
                        new WynnItem(new int[] { 0, 45, 0, 45, 0 }, new int[] { 30, -3, -3, -3, -3 }),
                },
                new int[] { 0, 48, 0, 48, 0 },
                new boolean[] { true });

        add("case21_repurposedVessels_fail",
                new WynnItem[] {
                        new WynnItem(new int[] { 0, 45, 0, 45, 0 }, new int[] { 30, -3, -3, -3, -3 }),
                },
                new int[] { 0, 45, 0, 45, 0 },
                new boolean[] { true });
        

        // SUGO TEST CASES START


        add("sugo001",
        new WynnItem[] {
                new WynnItem(new int[] { 65, 0, 0, 70, 0 }, new int[] { 12, 0, 0, 12, 0 }), // Titanomachia
                new WynnItem(new int[] { 0, 0, 50, 75, 0 }, new int[] { 0, 0, 6, 0, 0 }), // Empyreal Emberplate
                new WynnItem(new int[] { 105, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Chain Rule
                new WynnItem(new int[] { 60, 0, 0, 70, 0 }, new int[] { 20, 0, 0, 25, 0 }), // Crusade Sabatons
                new WynnItem(new int[] { 0, 0, 65, 0, 0 }, new int[] { 0, 0, 3, 0, 0 }), // Moon Pool Circlet
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Intensity
                new WynnItem(new int[] { 0, 0, 0, 40, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Dragon's Eye Bracelet
                new WynnItem(new int[] { 55, 0, 0, 45, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Simulacrum
        },
        new int[] { 73, 0, 59, 70, 0 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo002",
        new WynnItem[] {
                new WynnItem(new int[] { 0, 50, 40, 0, 0 }, new int[] { 0, 8, 8, 0, 0 }), // Logistics
                new WynnItem(new int[] { 0, 50, 0, 0, 65 }, new int[] { 0, 8, 0, 0, 0 }), // Etiolation
                new WynnItem(new int[] { 40, 40, 40, 40, 40 }, new int[] { 2, 2, 2, 2, 2 }), // Rainbow Sanctuary
                new WynnItem(new int[] { 0, 0, 50, 0, 50 }, new int[] { 0, 0, 0, 0, 0 }), // Virtuoso
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 1, 1, 1, 1, 1 }), // Photon
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 1, 1, 1, 1, 1 }), // Photon
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 4, 4, 4, 4, 4 }), // Prowess
                new WynnItem(new int[] { 0, 0, 0, 0, 40 }, new int[] { 0, 0, 0, 0, 0 }), // Renda Langit
        },
        new int[] { 34, 42, 34, 34, 57 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo003",
        new WynnItem[] {
                new WynnItem(new int[] { 0, 50, 40, 0, 0 }, new int[] { 0, 8, 8, 0, 0 }), // Logistics
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Discoverer
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 3, 3, 3, 3, 3 }), // Ringlets
                new WynnItem(new int[] { 70, 0, 0, 0, 70 }, new int[] { 0, 0, 0, 0, 0 }), // Revenant
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 1, 1, 1, 1, 1 }), // Photon
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 1, 1, 1, 1, 1 }), // Photon
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 4, 4, 4, 4, 4 }), // Prowess
                new WynnItem(new int[] { 20, 20, 20, 20, 20 }, new int[] { 4, 4, 4, 4, 4 }), // Diamond Fusion Necklace
        },
        new int[] { 57, 37, 27, 11, 57 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo004",
        new WynnItem[] {
                new WynnItem(new int[] { 45, 60, 30, 0, 0 }, new int[] { 10, 0, 15, 0, 0 }), // Caesura
                new WynnItem(new int[] { 50, 60, 0, 0, 0 }, new int[] { 13, 0, 0, 0, 0 }), // Delirium
                new WynnItem(new int[] { 40, 40, 40, 0, 0 }, new int[] { 0, 0, 0, -30, -30 }), // Chaos-Woven Greaves
                new WynnItem(new int[] { 0, 65, 75, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Stardew
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 3, 3, 0, 0, 0 }), // Hypoxia
                new WynnItem(new int[] { 0, 0, 55, 0, 0 }, new int[] { 0, 0, 4, 0, 0 }), // Yang
                new WynnItem(new int[] { 0, 100, 0, 0, 0 }, new int[] { 0, 6, 0, 0, 0 }), // Diamond Static Bracelet
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 5, 0, 0, 0 }), // Lightning Flash
        },
        new int[] { 42, 92, 56, 0, 0 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo005",
        new WynnItem[] {
                new WynnItem(new int[] { 45, 60, 30, 0, 0 }, new int[] { 10, 0, 15, 0, 0 }), // Caesura
                new WynnItem(new int[] { 0, 0, 120, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Time Rift
                new WynnItem(new int[] { 0, 75, 0, 0, 0 }, new int[] { 0, 15, 0, 0, -13 }), // Asphyxia
                new WynnItem(new int[] { 0, 65, 75, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Stardew
                new WynnItem(new int[] { 0, 25, 25, 0, 0 }, new int[] { 0, 4, 3, 0, 0 }), // Finesse
                new WynnItem(new int[] { 0, 25, 25, 0, 0 }, new int[] { 0, 4, 3, 0, 0 }), // Finesse
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 4, 4, 4, 4, 4 }), // Prowess
                new WynnItem(new int[] { 0, 0, 65, 0, 0 }, new int[] { 0, 0, 10, 0, 0 }), // Amanuensis
        },
        new int[] { 41, 63, 85, 0, 0 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo006",
        new WynnItem[] {
                new WynnItem(new int[] { 0, 0, 80, 0, 0 }, new int[] { 0, 0, 25, 0, 0 }), // Dwindled Knowledge
                new WynnItem(new int[] { 0, 0, 120, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Time Rift
                new WynnItem(new int[] { 40, 40, 40, 0, 0 }, new int[] { 0, 0, 0, -30, -30 }), // Chaos-Woven Greaves
                new WynnItem(new int[] { 0, 65, 75, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Stardew
                new WynnItem(new int[] { 0, 0, 55, 0, 0 }, new int[] { 0, 0, 4, 0, 0 }), // Yang
                new WynnItem(new int[] { 0, 0, 55, 0, 0 }, new int[] { 0, 0, 4, 0, 0 }), // Yang
                new WynnItem(new int[] { 0, 0, 100, 0, 0 }, new int[] { 0, 0, 6, 0, 0 }), // Diamond Hydro Bracelet
                new WynnItem(new int[] { 0, 0, 80, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Xebec
        },
        new int[] { 40, 65, 81, 0, 0 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo007",
        new WynnItem[] {
                new WynnItem(new int[] { 0, 0, 80, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Gnossis
                new WynnItem(new int[] { 0, 0, 120, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Time Rift
                new WynnItem(new int[] { 0, 30, 60, 0, 0 }, new int[] { 5, 0, 0, 5, 5 }), // Aleph Null
                new WynnItem(new int[] { 0, 65, 75, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Stardew
                new WynnItem(new int[] { 0, 0, 55, 0, 0 }, new int[] { 0, 0, 4, 0, 0 }), // Yang
                new WynnItem(new int[] { 0, 0, 65, 0, 0 }, new int[] { 0, 0, 3, 0, 0 }), // Moon Pool Circlet
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 4, 4, 4, 4, 4 }), // Prowess
                new WynnItem(new int[] { 0, 0, 100, 0, 0 }, new int[] { 0, 0, 7, 0, 0 }), // Diamond Hydro Necklace
        },
        new int[] { 0, 61, 102, 0, 0 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo008",
        new WynnItem[] {
                new WynnItem(new int[] { 40, 0, 40, 0, 0 }, new int[] { 7, 0, 7, 0, 0 }), // Aquamarine
                new WynnItem(new int[] { 0, 0, 120, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Time Rift
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 5, 5, 5, 5, 5 }), // Vaward
                new WynnItem(new int[] { 0, 0, 65, 80, 0 }, new int[] { 0, 0, 25, 0, 0 }), // Resurgence
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Old Keeper's Ring
                new WynnItem(new int[] { 0, 0, 55, 0, 0 }, new int[] { 0, 0, 4, 0, 0 }), // Yang
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Vindicator
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 3, 3, 3, 3, 3 }), // Gigabyte
        },
        new int[] { 32, 0, 76, 72, 0 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo009",
        new WynnItem[] {
                new WynnItem(new int[] { 65, 0, 0, 70, 0 }, new int[] { 12, 0, 0, 12, 0 }), // Titanomachia
                new WynnItem(new int[] { 0, 0, 0, 60, 60 }, new int[] { 0, 0, 0, 0, 0 }), // Firebird
                new WynnItem(new int[] { 0, 0, 0, 70, 0 }, new int[] { 0, 0, 0, 5, 0 }), // Fire Sanctuary
                new WynnItem(new int[] { 0, 0, 0, 75, 65 }, new int[] { 0, 0, 0, 0, 0 }), // Boreal
                new WynnItem(new int[] { 60, 0, 0, 55, 0 }, new int[] { 5, 0, 0, 0, 0 }), // Downfall
                new WynnItem(new int[] { 50, 0, 0, 0, 0 }, new int[] { 4, 0, 0, 2, 0 }), // Bygg
                new WynnItem(new int[] { 0, 0, 0, 60, 0 }, new int[] { 0, 0, 0, 6, 0 }), // Dupliblaze
                new WynnItem(new int[] { 0, 0, 0, 30, 0 }, new int[] { 0, 0, 0, 7, 0 }), // Strobelight
        },
        new int[] { 56, 0, 0, 55, 65 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo010",
        new WynnItem[] {
                new WynnItem(new int[] { 60, 0, 0, 0, 70 }, new int[] { 12, 0, 0, 0, 16 }), // Dune Storm
                new WynnItem(new int[] { 0, 0, 0, 0, 105 }, new int[] { 0, 0, 0, 0, 0 }), // Conduit of Spirit
                new WynnItem(new int[] { 0, 0, 0, 0, 80 }, new int[] { 0, 0, 0, 0, 13 }), // Sagittarius
                new WynnItem(new int[] { 70, 0, 0, 0, 70 }, new int[] { 0, 0, 0, 0, 0 }), // Revenant
                new WynnItem(new int[] { 0, 0, 0, 0, 100 }, new int[] { 0, 0, 0, 0, 5 }), // Diamond Steam Ring
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Intensity
                new WynnItem(new int[] { 0, 0, 0, 0, 30 }, new int[] { 0, 0, 0, 0, 0 }), // Vortex Bracer
                new WynnItem(new int[] { 0, 0, 0, 0, 50 }, new int[] { -5, 0, 0, 0, 10 }), // Necklace of a Thousand Storms
        },
        new int[] { 65, 0, 0, 0, 61 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo011",
        new WynnItem[] {
                new WynnItem(new int[] { 20, 0, 0, 0, 30 }, new int[] { 5, 0, 0, -3, 5 }), // The Siren's Call
                new WynnItem(new int[] { 0, 30, 0, 0, 30 }, new int[] { 0, 7, 12, 0, 7 }), // Gale's Freedom
                new WynnItem(new int[] { 105, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Chain Rule
                new WynnItem(new int[] { 40, 0, 0, 0, 40 }, new int[] { 10, 0, 0, 0, 5 }), // Earthsky Eclipse
                new WynnItem(new int[] { 40, 0, 30, 0, 0 }, new int[] { 0, 0, 5, 0, 0 }), // Olive
                new WynnItem(new int[] { 40, 0, 30, 0, 0 }, new int[] { 0, 0, 5, 0, 0 }), // Olive
                new WynnItem(new int[] { 50, 0, 0, 0, 0 }, new int[] { 5, 0, 0, 0, 0 }), // Thanos Banner
                new WynnItem(new int[] { 0, 0, 0, 0, 40 }, new int[] { 0, 0, 0, 0, 0 }), // Renda Langit
        },
        new int[] { 85, 30, 18, 0, 30 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo012",
        new WynnItem[] {
                new WynnItem(new int[] { 0, 45, 0, 0, 55 }, new int[] { 0, 8, 0, 0, 7 }), // Nephilim
                new WynnItem(new int[] { 40, 40, 0, 0, 40 }, new int[] { 0, 0, -30, -30, 0 }), // Twilight-Gilded Cloak
                new WynnItem(new int[] { 65, 40, 0, 0, 0 }, new int[] { 8, 8, 0, 0, 0 }), // Physalis
                new WynnItem(new int[] { 80, 80, 0, 0, 0 }, new int[] { 20, 10, 0, 0, 0 }), // Warchief
                new WynnItem(new int[] { 0, 55, 0, 0, 55 }, new int[] { 0, 0, 0, 0, 0 }), // Breezehands
                new WynnItem(new int[] { 100, 0, 0, 0, 0 }, new int[] { 7, 0, 0, 0, 0 }), // Diamond Fiber Ring
                new WynnItem(new int[] { 100, 0, 0, 0, 0 }, new int[] { 6, 0, 0, 0, 0 }), // Diamond Fiber Bracelet
                new WynnItem(new int[] { 0, 100, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Diamond Static Necklace
        },
        new int[] { 72, 74, 0, 0, 55 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo013",
        new WynnItem[] {
                new WynnItem(new int[] { 40, 70, 0, 0, 0 }, new int[] { 13, 0, -50, 0, 0 }), // Brainwash
                new WynnItem(new int[] { 80, 80, 0, 0, 0 }, new int[] { 20, 20, 0, 0, 0 }), // Bete Noire
                new WynnItem(new int[] { 55, 0, 0, 55, 0 }, new int[] { 12, 0, 0, 0, -12 }), // Doomsday Omen
                new WynnItem(new int[] { 25, 0, 0, 50, 0 }, new int[] { 7, 0, 0, 7, 0 }), // Mantlewalkers
                new WynnItem(new int[] { 60, 0, 0, 55, 0 }, new int[] { 5, 0, 0, 0, 0 }), // Downfall
                new WynnItem(new int[] { 60, 0, 0, 55, 0 }, new int[] { 5, 0, 0, 0, 0 }), // Downfall
                new WynnItem(new int[] { 60, 0, 0, 0, 0 }, new int[] { 0, 4, 0, 0, 0 }), // Enmity
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Contrast
        },
        new int[] { 38, 76, 0, 50, 0 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo014",
        new WynnItem[] {
                new WynnItem(new int[] { 0, 60, 0, 0, 0 }, new int[] { 0, 10, 0, 0, 0 }), // Luminiferous Aether
                new WynnItem(new int[] { 40, 40, 0, 0, 40 }, new int[] { 0, 0, -30, -30, 0 }), // Twilight-Gilded Cloak
                new WynnItem(new int[] { 0, 60, 0, 0, 60 }, new int[] { 0, 12, 0, 0, 12 }), // Leictreach Makani
                new WynnItem(new int[] { 80, 80, 0, 0, 0 }, new int[] { 20, 10, 0, 0, 0 }), // Warchief
                new WynnItem(new int[] { 0, 55, 0, 0, 55 }, new int[] { 0, 0, 0, 0, 0 }), // Breezehands
                new WynnItem(new int[] { 30, 0, 0, 0, 40 }, new int[] { 4, 0, 0, 0, 0 }), // Dasher
                new WynnItem(new int[] { 20, 0, 0, 0, 0 }, new int[] { 6, 2, -8, 0, 2 }), // Rycar's Bravado
                new WynnItem(new int[] { 0, 100, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Diamond Static Necklace
        },
        new int[] { 70, 66, 0, 0, 58 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo015",
        new WynnItem[] {
                new WynnItem(new int[] { 0, 0, 0, 0, 80 }, new int[] { 0, 0, 0, 0, 9 }), // Unravel
                new WynnItem(new int[] { 0, 45, 0, 0, 55 }, new int[] { 0, 0, 0, 0, 0 }), // Wanderlust
                new WynnItem(new int[] { 0, 0, 0, 0, 80 }, new int[] { 0, 0, 0, 0, 13 }), // Sagittarius
                new WynnItem(new int[] { 0, 0, 0, 0, 70 }, new int[] { 0, 0, 0, 0, 0 }), // Skidbladnir
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Intensity
                new WynnItem(new int[] { 0, 0, 0, 0, 100 }, new int[] { 0, 0, 0, 0, 5 }), // Diamond Steam Ring
                new WynnItem(new int[] { 20, 0, 0, 0, 0 }, new int[] { 5, 0, 0, 0, 0 }), // Buster Bracer
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Metamorphosis
        },
        new int[] { 20, 45, 0, 0, 80 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo016",
        new WynnItem[] {
                new WynnItem(new int[] { 0, 0, 100, 0, 0 }, new int[] { 0, -80, 5, 0, 0 }), // Aphotic
                new WynnItem(new int[] { 0, 0, 0, 0, 105 }, new int[] { 0, 0, 0, 0, 0 }), // Conduit of Spirit
                new WynnItem(new int[] { 60, 0, 0, 0, 60 }, new int[] { 0, 0, 0, 0, 0 }), // Anaerobic
                new WynnItem(new int[] { 0, 0, 70, 0, 80 }, new int[] { -10, -10, 35, -40, 60 }), // Moontower
                new WynnItem(new int[] { 25, 0, 25, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Draoi Fair
                new WynnItem(new int[] { 0, 0, 55, 0, 0 }, new int[] { 0, 0, 4, 0, 0 }), // Yang
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 12, 0, 0 }), // Anya's Penumbra
                new WynnItem(new int[] { 0, 0, 80, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Xebec
        },
        new int[] { 70, 0, 54, 0, 80 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo017",
        new WynnItem[] {
                new WynnItem(new int[] { 0, 0, 100, 0, 0 }, new int[] { 0, -80, 5, 0, 0 }), // Aphotic
                new WynnItem(new int[] { 0, 0, 120, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Time Rift
                new WynnItem(new int[] { 50, 0, 55, 0, 0 }, new int[] { 6, 0, 6, 0, 0 }), // Tao
                new WynnItem(new int[] { 0, 0, 70, 0, 80 }, new int[] { -10, -10, 35, -40, 60 }), // Moontower
                new WynnItem(new int[] { 0, 0, 55, 0, 0 }, new int[] { 0, 0, 4, 0, 0 }), // Yang
                new WynnItem(new int[] { 0, 0, 65, 0, 0 }, new int[] { 0, 0, 3, 0, 0 }), // Moon Pool Circlet
                new WynnItem(new int[] { 0, 0, 100, 0, 0 }, new int[] { 0, 0, 6, 0, 0 }), // Diamond Hydro Bracelet
                new WynnItem(new int[] { 0, 0, 80, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Xebec
        },
        new int[] { 60, 0, 61, 0, 80 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo018",
        new WynnItem[] {
                new WynnItem(new int[] { 0, 0, 100, 0, 0 }, new int[] { 0, -80, 5, 0, 0 }), // Aphotic
                new WynnItem(new int[] { 45, 0, 45, 0, 0 }, new int[] { 12, 0, 0, 0, 0 }), // Leviathan
                new WynnItem(new int[] { 50, 0, 55, 0, 0 }, new int[] { 6, 0, 6, 0, 0 }), // Tao
                new WynnItem(new int[] { 0, 0, 70, 0, 80 }, new int[] { -10, -10, 35, -40, 60 }), // Moontower
                new WynnItem(new int[] { 0, 0, 55, 0, 0 }, new int[] { 0, 0, 4, 0, 0 }), // Yang
                new WynnItem(new int[] { 0, 0, 55, 0, 0 }, new int[] { 0, 0, 4, 0, 0 }), // Yang
                new WynnItem(new int[] { 0, 0, 100, 0, 0 }, new int[] { 0, 0, 6, 0, 0 }), // Diamond Hydro Bracelet
                new WynnItem(new int[] { 0, 0, 100, 0, 0 }, new int[] { 0, 0, 7, 0, 0 }), // Diamond Hydro Necklace
        },
        new int[] { 49, 0, 56, 0, 80 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo019",
        new WynnItem[] {
                new WynnItem(new int[] { 65, 65, 0, 0, 0 }, new int[] { 10, 10, 0, 0, 0 }), // Nychthemeron
                new WynnItem(new int[] { 50, 60, 0, 0, 0 }, new int[] { 13, 0, 0, 0, 0 }), // Delirium
                new WynnItem(new int[] { 40, 40, 40, 0, 0 }, new int[] { 0, 0, 0, -30, -30 }), // Chaos-Woven Greaves
                new WynnItem(new int[] { 0, 90, 0, 0, 0 }, new int[] { 0, 10, 0, 0, 0 }), // Electro Mage's Boots
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 1, 1, 1, 1, 1 }), // Photon
                new WynnItem(new int[] { 25, 0, 25, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Draoi Fair
                new WynnItem(new int[] { 0, 100, 0, 0, 0 }, new int[] { 0, 6, 0, 0, 0 }), // Diamond Static Bracelet
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Metamorphosis
        },
        new int[] { 51, 79, 39, 0, 0 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo020",
        new WynnItem[] {
                new WynnItem(new int[] { 60, 0, 0, 50, 0 }, new int[] { 5, 0, 0, 5, 0 }), // Nuclear Emesis
                new WynnItem(new int[] { 80, 80, 0, 0, 0 }, new int[] { 20, 20, 0, 0, 0 }), // Bete Noire
                new WynnItem(new int[] { 50, 50, 0, 0, 0 }, new int[] { 10, 10, 0, 0, 0 }), // Tera
                new WynnItem(new int[] { 0, 50, 0, 50, 0 }, new int[] { 0, 12, 0, 12, 0 }), // Nether's Scar
                new WynnItem(new int[] { 40, 0, 30, 0, 0 }, new int[] { 0, 0, 5, 0, 0 }), // Olive
                new WynnItem(new int[] { 40, 0, 30, 0, 0 }, new int[] { 0, 0, 5, 0, 0 }), // Olive
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 4, 4, 4, 4, 4 }), // Prowess
                new WynnItem(new int[] { 55, 0, 0, 45, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Simulacrum
        },
        new int[] { 61, 54, 26, 46, 0 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo021",
        new WynnItem[] {
                new WynnItem(new int[] { 60, 0, 0, 50, 0 }, new int[] { 5, 0, 0, 5, 0 }), // Nuclear Emesis
                new WynnItem(new int[] { 0, 0, 0, 115, 0 }, new int[] { 15, 0, 0, 0, 0 }), // Cannonade
                new WynnItem(new int[] { 105, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Chain Rule
                new WynnItem(new int[] { 60, 0, 0, 70, 0 }, new int[] { 20, 0, 0, 25, 0 }), // Crusade Sabatons
                new WynnItem(new int[] { 60, 0, 0, 55, 0 }, new int[] { 5, 0, 0, 0, 0 }), // Downfall
                new WynnItem(new int[] { 60, 0, 0, 55, 0 }, new int[] { 5, 0, 0, 0, 0 }), // Downfall
                new WynnItem(new int[] { 0, 0, 0, 60, 0 }, new int[] { 0, 0, 0, 6, 0 }), // Dupliblaze
                new WynnItem(new int[] { 55, 0, 0, 45, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Simulacrum
        },
        new int[] { 60, 0, 0, 79, 0 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo022",
        new WynnItem[] {
                new WynnItem(new int[] { 0, 0, 80, 0, 0 }, new int[] { 0, 0, 15, 0, 0 }), // Third Eye
                new WynnItem(new int[] { 33, 33, 33, 33, 33 }, new int[] { 6, 6, 6, 6, 6 }), // Libra
                new WynnItem(new int[] { 40, 40, 40, 40, 40 }, new int[] { 2, 2, 2, 2, 2 }), // Rainbow Sanctuary
                new WynnItem(new int[] { 0, 0, 85, 0, 0 }, new int[] { 0, 0, 10, 0, 0 }), // Martingale
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Intensity
                new WynnItem(new int[] { 0, 0, 65, 0, 0 }, new int[] { 0, 0, 3, 0, 0 }), // Moon Pool Circlet
                new WynnItem(new int[] { 30, 30, 60, 30, 30 }, new int[] { 0, 0, 0, 0, 0 }), // Succession
                new WynnItem(new int[] { 20, 20, 20, 20, 20 }, new int[] { 4, 4, 4, 4, 4 }), // Diamond Fusion Necklace
        },
        new int[] { 30, 30, 65, 30, 30 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo023",
        new WynnItem[] {
                new WynnItem(new int[] { 40, 70, 0, 0, 0 }, new int[] { 13, 0, -50, 0, 0 }), // Brainwash
                new WynnItem(new int[] { 80, 80, 0, 0, 0 }, new int[] { 20, 20, 0, 0, 0 }), // Bete Noire
                new WynnItem(new int[] { 0, 75, 0, 0, 0 }, new int[] { 0, 15, 0, 0, -13 }), // Asphyxia
                new WynnItem(new int[] { 60, 0, 0, 70, 0 }, new int[] { 20, 0, 0, 25, 0 }), // Crusade Sabatons
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Intensity
                new WynnItem(new int[] { 60, 0, 0, 55, 0 }, new int[] { 5, 0, 0, 0, 0 }), // Downfall
                new WynnItem(new int[] { 0, 100, 0, 0, 0 }, new int[] { 0, 6, 0, 0, 0 }), // Diamond Static Bracelet
                new WynnItem(new int[] { 55, 0, 0, 45, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Simulacrum
        },
        new int[] { 47, 75, 0, 70, 0 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo024",
        new WynnItem[] {
                new WynnItem(new int[] { 45, 60, 30, 0, 0 }, new int[] { 10, 0, 15, 0, 0 }), // Caesura
                new WynnItem(new int[] { 0, 50, 0, 0, 65 }, new int[] { 0, 8, 0, 0, 0 }), // Etiolation
                new WynnItem(new int[] { 0, 30, 60, 0, 0 }, new int[] { 5, 0, 0, 5, 5 }), // Aleph Null
                new WynnItem(new int[] { 0, 0, 50, 0, 50 }, new int[] { 0, 0, 0, 0, 0 }), // Virtuoso
                new WynnItem(new int[] { 55, 0, 0, 0, 55 }, new int[] { 0, 3, 2, 3, 0 }), // Ingress
                new WynnItem(new int[] { 55, 0, 0, 0, 55 }, new int[] { 0, 3, 2, 3, 0 }), // Ingress
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 4, 4, 4, 4, 4 }), // Prowess
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Metamorphosis
        },
        new int[] { 41, 48, 37, 0, 61 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo025",
        new WynnItem[] {
                new WynnItem(new int[] { 60, 0, 0, 0, 70 }, new int[] { 12, 0, 0, 0, 16 }), // Dune Storm
                new WynnItem(new int[] { 0, 0, 0, 0, 90 }, new int[] { 0, 0, 0, 0, 0 }), // Echoes of the Lost
                new WynnItem(new int[] { 0, 0, 0, 0, 80 }, new int[] { 0, 0, 0, 0, 13 }), // Sagittarius
                new WynnItem(new int[] { 70, 0, 0, 0, 70 }, new int[] { 0, 0, 0, 0, 0 }), // Revenant
                new WynnItem(new int[] { 0, 25, 25, 0, 0 }, new int[] { 0, 4, 3, 0, 0 }), // Finesse
                new WynnItem(new int[] { 0, 25, 25, 0, 0 }, new int[] { 0, 4, 3, 0, 0 }), // Finesse
                new WynnItem(new int[] { 0, 40, 0, 0, 0 }, new int[] { 0, 1, 0, 0, 0 }), // Lachesism
                new WynnItem(new int[] { 0, 0, 45, 0, 45 }, new int[] { 0, 0, 0, 0, 0 }), // Reckoning
        },
        new int[] { 60, 32, 39, 0, 70 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo026",
        new WynnItem[] {
                new WynnItem(new int[] { 40, 70, 0, 0, 0 }, new int[] { 13, 0, -50, 0, 0 }), // Brainwash
                new WynnItem(new int[] { 0, 75, 0, 65, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Insignia
                new WynnItem(new int[] { 0, 75, 0, 0, 0 }, new int[] { 0, 15, 0, 0, -13 }), // Asphyxia
                new WynnItem(new int[] { 60, 0, 0, 70, 0 }, new int[] { 20, 0, 0, 25, 0 }), // Crusade Sabatons
                new WynnItem(new int[] { 0, 100, 0, 0, 0 }, new int[] { 0, 8, 0, 0, 0 }), // Diamond Static Ring
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Intensity
                new WynnItem(new int[] { 0, 100, 0, 0, 0 }, new int[] { 0, 6, 0, 0, 0 }), // Diamond Static Bracelet
                new WynnItem(new int[] { 55, 0, 0, 45, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Simulacrum
        },
        new int[] { 47, 85, 0, 70, 0 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo027",
        new WynnItem[] {
                new WynnItem(new int[] { 40, 70, 0, 0, 0 }, new int[] { 13, 0, -50, 0, 0 }), // Brainwash
                new WynnItem(new int[] { 0, 50, 0, 0, 65 }, new int[] { 0, 8, 0, 0, 0 }), // Etiolation
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Ornate Shadow Cover
                new WynnItem(new int[] { 0, 70, 0, 0, 0 }, new int[] { 0, 8, 0, 0, 0 }), // Weatherwalkers
                new WynnItem(new int[] { 0, 60, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Gloomstone
                new WynnItem(new int[] { 0, 60, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Gloomstone
                new WynnItem(new int[] { 0, 100, 0, 0, 0 }, new int[] { 0, 6, 0, 0, 0 }), // Diamond Static Bracelet
                new WynnItem(new int[] { 0, 0, 0, 0, 50 }, new int[] { -5, 0, 0, 0, 10 }), // Necklace of a Thousand Storms
        },
        new int[] { 45, 84, 0, 0, 55 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo028",
        new WynnItem[] {
                new WynnItem(new int[] { 45, 60, 30, 0, 0 }, new int[] { 10, 0, 15, 0, 0 }), // Caesura
                new WynnItem(new int[] { 80, 80, 0, 0, 0 }, new int[] { 20, 20, 0, 0, 0 }), // Bete Noire
                new WynnItem(new int[] { 0, 75, 0, 0, 0 }, new int[] { 0, 15, 0, 0, -13 }), // Asphyxia
                new WynnItem(new int[] { 0, 65, 75, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Stardew
                new WynnItem(new int[] { 0, 0, 65, 0, 0 }, new int[] { 0, 0, 3, 0, 0 }), // Moon Pool Circlet
                new WynnItem(new int[] { 0, 0, 40, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Cold Wave
                new WynnItem(new int[] { 0, 100, 0, 0, 0 }, new int[] { 0, 6, 0, 0, 0 }), // Diamond Static Bracelet
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 5, 0, 0, 0 }), // Lightning Flash
        },
        new int[] { 70, 70, 57, 0, 0 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo029",
        new WynnItem[] {
                new WynnItem(new int[] { 45, 60, 30, 0, 0 }, new int[] { 10, 0, 15, 0, 0 }), // Caesura
                new WynnItem(new int[] { 0, 50, 0, 0, 65 }, new int[] { 0, 8, 0, 0, 0 }), // Etiolation
                new WynnItem(new int[] { 0, 30, 60, 0, 0 }, new int[] { 5, 0, 0, 5, 5 }), // Aleph Null
                new WynnItem(new int[] { 40, 0, 0, 0, 40 }, new int[] { 10, 0, 0, 0, 5 }), // Earthsky Eclipse
                new WynnItem(new int[] { 55, 0, 0, 0, 55 }, new int[] { 0, 3, 2, 3, 0 }), // Ingress
                new WynnItem(new int[] { 55, 0, 0, 0, 55 }, new int[] { 0, 3, 2, 3, 0 }), // Ingress
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 4, 4, 4, 4, 4 }), // Prowess
                new WynnItem(new int[] { 20, 20, 20, 20, 20 }, new int[] { 4, 4, 4, 4, 4 }), // Diamond Fusion Necklace
        },
        new int[] { 41, 38, 33, 10, 52 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo030",
        new WynnItem[] {
                new WynnItem(new int[] { 40, 0, 40, 0, 0 }, new int[] { 7, 0, 7, 0, 0 }), // Aquamarine
                new WynnItem(new int[] { 0, 0, 0, 0, 105 }, new int[] { 0, 0, 0, 0, 0 }), // Conduit of Spirit
                new WynnItem(new int[] { 0, 0, 0, 0, 105 }, new int[] { 0, 0, 0, 0, 20 }), // Windborne
                new WynnItem(new int[] { 0, 0, 0, 0, 70 }, new int[] { 0, 0, 0, 0, 0 }), // Skidbladnir
                new WynnItem(new int[] { 0, 0, 0, 0, 100 }, new int[] { 0, 0, 0, 0, 5 }), // Diamond Steam Ring
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 1, 1, 1, 1, 1 }), // Photon
                new WynnItem(new int[] { 0, 0, 0, 0, 30 }, new int[] { 0, 0, 0, 0, 0 }), // Vortex Bracer
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Contrast
        },
        new int[] { 39, 0, 39, 0, 99 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo031",
        new WynnItem[] {
                new WynnItem(new int[] { 40, 0, 40, 0, 0 }, new int[] { 7, 0, 7, 0, 0 }), // Aquamarine
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Discoverer
                new WynnItem(new int[] { 40, 0, 30, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Elder Oak Roots
                new WynnItem(new int[] { 0, 0, 70, 0, 80 }, new int[] { -10, -10, 35, -40, 60 }), // Moontower
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Old Keeper's Ring
                new WynnItem(new int[] { 0, 0, 40, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Cold Wave
                new WynnItem(new int[] { 45, 0, 0, 0, 40 }, new int[] { 3, 0, 0, 0, 3 }), // Provenance
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Contrast
        },
        new int[] { 48, 0, 63, 0, 77 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo032",
        new WynnItem[] {
                new WynnItem(new int[] { 65, 0, 0, 70, 0 }, new int[] { 12, 0, 0, 12, 0 }), // Titanomachia
                new WynnItem(new int[] { 0, 0, 0, 115, 0 }, new int[] { 15, 0, 0, 0, 0 }), // Cannonade
                new WynnItem(new int[] { 105, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Chain Rule
                new WynnItem(new int[] { 60, 0, 0, 70, 0 }, new int[] { 20, 0, 0, 25, 0 }), // Crusade Sabatons
                new WynnItem(new int[] { 0, 0, 40, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Cold Wave
                new WynnItem(new int[] { 60, 0, 0, 55, 0 }, new int[] { 5, 0, 0, 0, 0 }), // Downfall
                new WynnItem(new int[] { 60, 0, 0, 0, 0 }, new int[] { 0, 4, 0, 0, 0 }), // Enmity
                new WynnItem(new int[] { 55, 0, 0, 45, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Simulacrum
        },
        new int[] { 60, 0, 40, 78, 0 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo033",
        new WynnItem[] {
                new WynnItem(new int[] { 40, 70, 0, 0, 0 }, new int[] { 13, 0, -50, 0, 0 }), // Brainwash
                new WynnItem(new int[] { 0, 0, 0, 115, 0 }, new int[] { 15, 0, 0, 0, 0 }), // Cannonade
                new WynnItem(new int[] { 0, 40, 0, 40, 0 }, new int[] { 0, 0, 0, 9, 0 }), // Flummox
                new WynnItem(new int[] { 60, 0, 0, 70, 0 }, new int[] { 20, 0, 0, 25, 0 }), // Crusade Sabatons
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Intensity
                new WynnItem(new int[] { 60, 0, 0, 55, 0 }, new int[] { 5, 0, 0, 0, 0 }), // Downfall
                new WynnItem(new int[] { 60, 0, 0, 0, 0 }, new int[] { 0, 4, 0, 0, 0 }), // Enmity
                new WynnItem(new int[] { 55, 0, 0, 45, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Simulacrum
        },
        new int[] { 47, 70, 0, 81, 0 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo034",
        new WynnItem[] {
                new WynnItem(new int[] { 45, 60, 30, 0, 0 }, new int[] { 10, 0, 15, 0, 0 }), // Caesura
                new WynnItem(new int[] { 50, 60, 0, 0, 0 }, new int[] { 13, 0, 0, 0, 0 }), // Delirium
                new WynnItem(new int[] { 105, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Chain Rule
                new WynnItem(new int[] { 40, 0, 0, 0, 40 }, new int[] { 10, 0, 0, 0, 5 }), // Earthsky Eclipse
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Intensity
                new WynnItem(new int[] { 0, 0, 40, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Cold Wave
                new WynnItem(new int[] { 0, 40, 0, 0, 0 }, new int[] { 0, 1, 0, 0, 0 }), // Lachesism
                new WynnItem(new int[] { 0, 20, 0, 0, 20 }, new int[] { 0, 5, 0, 0, 5 }), // Bottled Thunderstorm
        },
        new int[] { 72, 54, 30, 0, 35 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo035",
        new WynnItem[] {
                new WynnItem(new int[] { 0, 55, 40, 55, 0 }, new int[] { 0, 5, 10, 0, 0 }), // Transplanted Psyche
                new WynnItem(new int[] { 0, 50, 50, 0, 0 }, new int[] { 0, 10, 10, 0, 0 }), // Soul Signal
                new WynnItem(new int[] { 0, 30, 60, 0, 0 }, new int[] { 5, 0, 0, 5, 5 }), // Aleph Null
                new WynnItem(new int[] { 0, 65, 75, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Stardew
                new WynnItem(new int[] { 0, 0, 55, 0, 0 }, new int[] { 0, 0, 4, 0, 0 }), // Yang
                new WynnItem(new int[] { 0, 0, 55, 0, 0 }, new int[] { 0, 0, 4, 0, 0 }), // Yang
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 4, 4, 4, 4, 4 }), // Prowess
                new WynnItem(new int[] { 0, 0, 100, 0, 0 }, new int[] { 0, 0, 7, 0, 0 }), // Diamond Hydro Necklace
        },
        new int[] { 0, 46, 68, 46, 0 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo036",
        new WynnItem[] {
                new WynnItem(new int[] { 0, 40, 40, 0, 40 }, new int[] { 0, 10, 10, 0, 10 }), // Cumulonimbus
                new WynnItem(new int[] { 0, 50, 0, 0, 65 }, new int[] { 0, 8, 0, 0, 0 }), // Etiolation
                new WynnItem(new int[] { 0, 30, 60, 0, 0 }, new int[] { 5, 0, 0, 5, 5 }), // Aleph Null
                new WynnItem(new int[] { 0, 65, 75, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Stardew
                new WynnItem(new int[] { 0, 0, 60, 0, 30 }, new int[] { 0, 0, 0, 0, 6 }), // Stratus
                new WynnItem(new int[] { 0, 0, 60, 0, 30 }, new int[] { 0, 0, 0, 0, 6 }), // Stratus
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 4, 4, 4, 4, 4 }), // Prowess
                new WynnItem(new int[] { 0, 0, 100, 0, 0 }, new int[] { 0, 0, 7, 0, 0 }), // Diamond Hydro Necklace
        },
        new int[] { 0, 43, 86, 0, 34 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo037",
        new WynnItem[] {
                new WynnItem(new int[] { 45, 60, 30, 0, 0 }, new int[] { 10, 0, 15, 0, 0 }), // Caesura
                new WynnItem(new int[] { 0, 65, 65, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Stratosphere
                new WynnItem(new int[] { 0, 30, 60, 0, 0 }, new int[] { 5, 0, 0, 5, 5 }), // Aleph Null
                new WynnItem(new int[] { 60, 0, 60, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Galleon
                new WynnItem(new int[] { 40, 0, 30, 0, 0 }, new int[] { 0, 0, 5, 0, 0 }), // Olive
                new WynnItem(new int[] { 40, 0, 30, 0, 0 }, new int[] { 0, 0, 5, 0, 0 }), // Olive
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 4, 4, 4, 4, 4 }), // Prowess
                new WynnItem(new int[] { 0, 100, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Diamond Static Necklace
        },
        new int[] { 41, 96, 36, 0, 0 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo038",
        new WynnItem[] {
                new WynnItem(new int[] { 0, 55, 40, 55, 0 }, new int[] { 0, 5, 10, 0, 0 }), // Transplanted Psyche
                new WynnItem(new int[] { 35, 0, 35, 0, 0 }, new int[] { 7, 0, 9, 0, 0 }), // Azurite
                new WynnItem(new int[] { 0, 30, 60, 0, 0 }, new int[] { 5, 0, 0, 5, 5 }), // Aleph Null
                new WynnItem(new int[] { 60, 0, 0, 70, 0 }, new int[] { 20, 0, 0, 25, 0 }), // Crusade Sabatons
                new WynnItem(new int[] { 40, 0, 30, 0, 0 }, new int[] { 0, 0, 5, 0, 0 }), // Olive
                new WynnItem(new int[] { 40, 0, 30, 0, 0 }, new int[] { 0, 0, 5, 0, 0 }), // Olive
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 4, 4, 4, 4, 4 }), // Prowess
                new WynnItem(new int[] { 10, 10, 10, 10, 10 }, new int[] { 1, 1, 1, 1, 1 }), // Umami
        },
        new int[] { 43, 51, 27, 60, 1 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo039",
        new WynnItem[] {
                new WynnItem(new int[] { 0, 35, 0, 35, 55 }, new int[] { -35, 0, 0, 0, 5 }), // Greatbird Eyrie
                new WynnItem(new int[] { 0, 50, 0, 0, 65 }, new int[] { 0, 8, 0, 0, 0 }), // Etiolation
                new WynnItem(new int[] { 0, 40, 40, 0, 60 }, new int[] { -35, 7, 0, 0, 7 }), // Perch of the Shrouded Sun
                new WynnItem(new int[] { 20, 20, 20, 20, 20 }, new int[] { 5, 5, 5, 5, 5 }), // Boots of Blue Stone
                new WynnItem(new int[] { 40, 0, 30, 0, 0 }, new int[] { 0, 0, 5, 0, 0 }), // Olive
                new WynnItem(new int[] { 40, 0, 30, 0, 0 }, new int[] { 0, 0, 5, 0, 0 }), // Olive
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 4, 4, 4, 4, 4 }), // Prowess
                new WynnItem(new int[] { 0, 40, 0, 40, 0 }, new int[] { 0, 4, 0, 4, 0 }), // Tenuto
        },
        new int[] { 101, 31, 21, 31, 46 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo040",
        new WynnItem[] {
                new WynnItem(new int[] { 40, 70, 0, 0, 0 }, new int[] { 13, 0, -50, 0, 0 }), // Brainwash
                new WynnItem(new int[] { 0, 0, 0, 115, 0 }, new int[] { 15, 0, 0, 0, 0 }), // Cannonade
                new WynnItem(new int[] { 0, 0, 0, 70, 0 }, new int[] { 0, 0, 0, 5, 0 }), // Fire Sanctuary
                new WynnItem(new int[] { 60, 0, 0, 70, 0 }, new int[] { 20, 0, 0, 25, 0 }), // Crusade Sabatons
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Intensity
                new WynnItem(new int[] { 60, 0, 0, 55, 0 }, new int[] { 5, 0, 0, 0, 0 }), // Downfall
                new WynnItem(new int[] { 0, 0, 0, 60, 0 }, new int[] { 0, 0, 0, 6, 0 }), // Dupliblaze
                new WynnItem(new int[] { 55, 0, 0, 45, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Simulacrum
        },
        new int[] { 47, 70, 0, 79, 0 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo041",
        new WynnItem[] {
                new WynnItem(new int[] { 0, 60, 0, 0, 0 }, new int[] { 0, 10, 0, 0, 0 }), // Luminiferous Aether
                new WynnItem(new int[] { 40, 40, 0, 0, 40 }, new int[] { 0, 0, -30, -30, 0 }), // Twilight-Gilded Cloak
                new WynnItem(new int[] { 40, 25, 0, 0, 0 }, new int[] { 9, 7, 0, 0, 0 }), // Post-Ultima
                new WynnItem(new int[] { 80, 80, 0, 0, 0 }, new int[] { 20, 10, 0, 0, 0 }), // Warchief
                new WynnItem(new int[] { 100, 0, 0, 0, 0 }, new int[] { 7, 0, 0, 0, 0 }), // Diamond Fiber Ring
                new WynnItem(new int[] { 100, 0, 0, 0, 0 }, new int[] { 7, 0, 0, 0, 0 }), // Diamond Fiber Ring
                new WynnItem(new int[] { 100, 0, 0, 0, 0 }, new int[] { 6, 0, 0, 0, 0 }), // Diamond Fiber Bracelet
                new WynnItem(new int[] { 0, 100, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Diamond Static Necklace
        },
        new int[] { 71, 73, 0, 0, 40 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo042",
        new WynnItem[] {
                new WynnItem(new int[] { 0, 0, 100, 0, 0 }, new int[] { 0, 0, 29, 0, 0 }), // Anamnesis
                new WynnItem(new int[] { 0, 65, 55, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Schadenfreude
                new WynnItem(new int[] { 0, 30, 60, 0, 0 }, new int[] { 5, 0, 0, 5, 5 }), // Aleph Null
                new WynnItem(new int[] { 0, 45, 0, 45, 0 }, new int[] { 30, -3, -3, -3, -3 }), // Repurposed Vessels
                new WynnItem(new int[] { 0, 0, 55, 0, 0 }, new int[] { 0, 0, 4, 0, 0 }), // Yang
                new WynnItem(new int[] { 0, 0, 55, 0, 0 }, new int[] { 0, 0, 4, 0, 0 }), // Yang
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 4, 4, 4, 4, 4 }), // Prowess
                new WynnItem(new int[] { 0, 0, 100, 0, 0 }, new int[] { 0, 0, 7, 0, 0 }), // Diamond Hydro Necklace
        },
        new int[] { 0, 64, 88, 36, 0 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo043",
        new WynnItem[] {
                new WynnItem(new int[] { 0, 0, 100, 0, 0 }, new int[] { 0, 0, 29, 0, 0 }), // Anamnesis
                new WynnItem(new int[] { 0, 0, 120, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Time Rift
                new WynnItem(new int[] { 40, 40, 40, 0, 0 }, new int[] { 0, 0, 0, -30, -30 }), // Chaos-Woven Greaves
                new WynnItem(new int[] { 0, 65, 75, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Stardew
                new WynnItem(new int[] { 0, 0, 65, 0, 0 }, new int[] { 0, 0, 3, 0, 0 }), // Moon Pool Circlet
                new WynnItem(new int[] { 40, 0, 30, 0, 0 }, new int[] { 0, 0, 5, 0, 0 }), // Olive
                new WynnItem(new int[] { 20, 0, 0, 0, 0 }, new int[] { 5, 0, 0, 0, 0 }), // Buster Bracer
                new WynnItem(new int[] { 0, 0, 100, 0, 0 }, new int[] { 0, 0, 7, 0, 0 }), // Diamond Hydro Necklace
        },
        new int[] { 35, 65, 92, 0, 0 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo044",
        new WynnItem[] {
                new WynnItem(new int[] { 0, 0, 0, 0, 60 }, new int[] { 0, 7, 0, 0, 10 }), // Gale's Sight
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Discoverer
                new WynnItem(new int[] { 0, 0, 0, 0, 70 }, new int[] { 0, 0, 0, 0, 7 }), // Pain Cycle
                new WynnItem(new int[] { 70, 0, 0, 0, 70 }, new int[] { 0, 0, 0, 0, 0 }), // Revenant
                new WynnItem(new int[] { 55, 0, 0, 0, 55 }, new int[] { 0, 3, 2, 3, 0 }), // Ingress
                new WynnItem(new int[] { 0, 0, 0, 30, 30 }, new int[] { 0, 0, 0, 0, 0 }), // Bloodborne
                new WynnItem(new int[] { 0, 0, 0, 0, 30 }, new int[] { 0, 0, 0, 0, 0 }), // Vortex Bracer
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Contrast
        },
        new int[] { 70, 0, 0, 27, 60 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo045",
        new WynnItem[] {
                new WynnItem(new int[] { 40, 70, 0, 0, 0 }, new int[] { 13, 0, -50, 0, 0 }), // Brainwash
                new WynnItem(new int[] { 0, 0, 0, 0, 65 }, new int[] { 0, 0, 0, 0, 20 }), // Soarfae
                new WynnItem(new int[] { 0, 0, 0, 0, 80 }, new int[] { 0, 0, 0, 0, 13 }), // Sagittarius
                new WynnItem(new int[] { 70, 0, 0, 0, 70 }, new int[] { 0, 0, 0, 0, 0 }), // Revenant
                new WynnItem(new int[] { 55, 0, 0, 0, 55 }, new int[] { 0, 3, 2, 3, 0 }), // Ingress
                new WynnItem(new int[] { 55, 0, 0, 0, 55 }, new int[] { 0, 3, 2, 3, 0 }), // Ingress
                new WynnItem(new int[] { 0, 0, 0, 0, 30 }, new int[] { 0, 0, 0, 0, 0 }), // Vortex Bracer
                new WynnItem(new int[] { 0, 20, 0, 0, 20 }, new int[] { 0, 5, 0, 0, 5 }), // Bottled Thunderstorm
        },
        new int[] { 57, 59, 0, 0, 60 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo046",
        new WynnItem[] {
                new WynnItem(new int[] { 40, 70, 0, 0, 0 }, new int[] { 13, 0, -50, 0, 0 }), // Brainwash
                new WynnItem(new int[] { 0, 0, 0, 0, 65 }, new int[] { 0, 0, 0, 0, 20 }), // Soarfae
                new WynnItem(new int[] { 0, 0, 0, 0, 70 }, new int[] { 0, 0, 0, 0, 7 }), // Pain Cycle
                new WynnItem(new int[] { 70, 0, 0, 0, 70 }, new int[] { 0, 0, 0, 0, 0 }), // Revenant
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Intensity
                new WynnItem(new int[] { 0, 0, 0, 0, 100 }, new int[] { 0, 0, 0, 0, 5 }), // Diamond Steam Ring
                new WynnItem(new int[] { 0, 0, 0, 0, 30 }, new int[] { 0, 0, 0, 0, 0 }), // Vortex Bracer
                new WynnItem(new int[] { 0, 0, 0, 0, 50 }, new int[] { -5, 0, 0, 0, 10 }), // Necklace of a Thousand Storms
        },
        new int[] { 62, 70, 0, 0, 63 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo047",
        new WynnItem[] {
                new WynnItem(new int[] { 0, 0, 0, 0, 80 }, new int[] { 0, 0, 0, 0, 9 }), // Unravel
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Discoverer
                new WynnItem(new int[] { 0, 0, 0, 0, 80 }, new int[] { 0, 0, 0, 0, 13 }), // Sagittarius
                new WynnItem(new int[] { 0, 0, 0, 0, 70 }, new int[] { 0, 0, 0, 0, 0 }), // Skidbladnir
                new WynnItem(new int[] { 55, 0, 0, 0, 55 }, new int[] { 0, 3, 2, 3, 0 }), // Ingress
                new WynnItem(new int[] { 0, 0, 0, 30, 30 }, new int[] { 0, 0, 0, 0, 0 }), // Bloodborne
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 3 }), // Double Vision
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Contrast
        },
        new int[] { 55, 0, 0, 27, 77 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo048",
        new WynnItem[] {
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Scarlet Veil
                new WynnItem(new int[] { 40, 40, 0, 0, 40 }, new int[] { 0, 0, -30, -30, 0 }), // Twilight-Gilded Cloak
                new WynnItem(new int[] { 65, 40, 0, 0, 0 }, new int[] { 8, 8, 0, 0, 0 }), // Physalis
                new WynnItem(new int[] { 80, 80, 0, 0, 0 }, new int[] { 20, 10, 0, 0, 0 }), // Warchief
                new WynnItem(new int[] { 0, 55, 0, 0, 55 }, new int[] { 0, 0, 0, 0, 0 }), // Breezehands
                new WynnItem(new int[] { 100, 0, 0, 0, 0 }, new int[] { 7, 0, 0, 0, 0 }), // Diamond Fiber Ring
                new WynnItem(new int[] { 100, 0, 0, 0, 0 }, new int[] { 6, 0, 0, 0, 0 }), // Diamond Fiber Bracelet
                new WynnItem(new int[] { 45, 0, 0, 0, 0 }, new int[] { 6, 0, 0, 0, 0 }), // Recalcitrance
        },
        new int[] { 66, 72, 0, 0, 55 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo049",
        new WynnItem[] {
                new WynnItem(new int[] { 0, 0, 80, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Gnossis
                new WynnItem(new int[] { 45, 45, 45, 0, 45 }, new int[] { 5, 5, 5, -99, 5 }), // Phantasmagoria
                new WynnItem(new int[] { 0, 30, 60, 0, 0 }, new int[] { 5, 0, 0, 5, 5 }), // Aleph Null
                new WynnItem(new int[] { 0, 40, 50, 0, 0 }, new int[] { 0, 0, 10, 0, 0 }), // Pro Tempore
                new WynnItem(new int[] { 25, 0, 25, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Draoi Fair
                new WynnItem(new int[] { 0, 0, 65, 0, 0 }, new int[] { 0, 0, 3, 0, 0 }), // Moon Pool Circlet
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 4, 4, 4, 4, 4 }), // Prowess
                new WynnItem(new int[] { 0, 0, 40, 0, 40 }, new int[] { 0, 0, 0, 0, 5 }), // Charm of the Storms
        },
        new int[] { 36, 41, 58, 0, 31 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo050",
        new WynnItem[] {
                new WynnItem(new int[] { 0, 70, 70, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Prosencephalon
                new WynnItem(new int[] { 0, 40, 40, 0, 0 }, new int[] { 7, 5, 5, 0, 0 }), // Umbral Mail
                new WynnItem(new int[] { 40, 40, 40, 0, 0 }, new int[] { 0, 0, 0, -30, -30 }), // Chaos-Woven Greaves
                new WynnItem(new int[] { 0, 65, 75, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Stardew
                new WynnItem(new int[] { 0, 0, 55, 0, 0 }, new int[] { 0, 0, 4, 0, 0 }), // Yang
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 1, 1, 1, 1, 1 }), // Photon
                new WynnItem(new int[] { 35, 45, 0, 0, 0 }, new int[] { 0, 3, 3, 0, 0 }), // Misalignment
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Metamorphosis
        },
        new int[] { 32, 61, 62, 0, 0 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo051",
        new WynnItem[] {
                new WynnItem(new int[] { 0, 70, 70, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Prosencephalon
                new WynnItem(new int[] { 0, 65, 65, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Stratosphere
                new WynnItem(new int[] { 0, 50, 45, 0, 0 }, new int[] { 0, 10, 10, 0, 0 }), // Entanglement
                new WynnItem(new int[] { 0, 65, 75, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Stardew
                new WynnItem(new int[] { 40, 0, 30, 0, 0 }, new int[] { 0, 0, 5, 0, 0 }), // Olive
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 1, 1, 1, 1, 1 }), // Photon
                new WynnItem(new int[] { 0, 0, 0, 40, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Dragon's Eye Bracelet
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Contrast
        },
        new int[] { 39, 59, 59, 39, 0 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo052",
        new WynnItem[] {
                new WynnItem(new int[] { 65, 65, 0, 0, 0 }, new int[] { 10, 10, 0, 0, 0 }), // Nychthemeron
                new WynnItem(new int[] { 90, 0, 0, 0, 0 }, new int[] { 15, 0, 0, 0, 0 }), // Taurus
                new WynnItem(new int[] { 40, 25, 0, 0, 0 }, new int[] { 9, 7, 0, 0, 0 }), // Post-Ultima
                new WynnItem(new int[] { 95, 0, 0, 0, 0 }, new int[] { 10, 0, 0, 0, 0 }), // Blind Thrust
                new WynnItem(new int[] { 50, 0, 0, 0, 0 }, new int[] { 4, 0, 0, 0, 0 }), // Ad Terram
                new WynnItem(new int[] { 50, 0, 0, 0, 0 }, new int[] { 4, 0, 0, 0, 0 }), // Ad Terram
                new WynnItem(new int[] { 45, 0, 30, 0, 0 }, new int[] { 0, 0, 6, 0, 0 }), // Blissful Solace
                new WynnItem(new int[] { 0, 70, 0, 0, 0 }, new int[] { 0, 5, 0, 0, 0 }), // H-209 Miniature Defibrillator
        },
        new int[] { 63, 58, 30, 0, 0 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo053",
        new WynnItem[] {
                new WynnItem(new int[] { 0, 0, 0, 100, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Dreadnought
                new WynnItem(new int[] { 0, 0, 0, 115, 0 }, new int[] { 15, 0, 0, 0, 0 }), // Cannonade
                new WynnItem(new int[] { 0, 0, 60, 60, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Ophiuchus
                new WynnItem(new int[] { 60, 0, 0, 70, 0 }, new int[] { 20, 0, 0, 25, 0 }), // Crusade Sabatons
                new WynnItem(new int[] { 25, 0, 25, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Draoi Fair
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Intensity
                new WynnItem(new int[] { 0, 0, 0, 60, 0 }, new int[] { 0, 0, 0, 6, 0 }), // Dupliblaze
                new WynnItem(new int[] { 55, 0, 0, 45, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Simulacrum
        },
        new int[] { 60, 0, 60, 84, 0 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo054",
        new WynnItem[] {
                new WynnItem(new int[] { 0, 0, 0, 100, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Dreadnought
                new WynnItem(new int[] { 0, 0, 0, 115, 0 }, new int[] { 15, 0, 0, 0, 0 }), // Cannonade
                new WynnItem(new int[] { 0, 0, 60, 60, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Ophiuchus
                new WynnItem(new int[] { 25, 0, 0, 50, 0 }, new int[] { 7, 0, 0, 7, 0 }), // Mantlewalkers
                new WynnItem(new int[] { 0, 0, 0, 45, 0 }, new int[] { 0, 0, 0, 4, 0 }), // Hellion
                new WynnItem(new int[] { 25, 0, 25, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Draoi Fair
                new WynnItem(new int[] { 0, 0, 0, 100, 0 }, new int[] { 0, 0, 0, 7, 0 }), // Diamond Solar Bracelet
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Abrasion
        },
        new int[] { 10, 0, 60, 104, 0 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo055",
        new WynnItem[] {
                new WynnItem(new int[] { 0, 45, 0, 45, 0 }, new int[] { 10, 10, 0, 10, 0 }), // Darksteel Full Helm
                new WynnItem(new int[] { 90, 0, 0, 0, 0 }, new int[] { 15, 0, 0, 0, 0 }), // Taurus
                new WynnItem(new int[] { 50, 0, 0, 40, 0 }, new int[] { 9, 0, 0, 8, 0 }), // Earth Breaker
                new WynnItem(new int[] { 0, 65, 0, 65, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Dawnbreak
                new WynnItem(new int[] { 60, 0, 0, 55, 0 }, new int[] { 5, 0, 0, 0, 0 }), // Downfall
                new WynnItem(new int[] { 60, 0, 0, 55, 0 }, new int[] { 5, 0, 0, 0, 0 }), // Downfall
                new WynnItem(new int[] { 50, 0, 0, 0, 0 }, new int[] { 5, 3, 0, 0, 0 }), // Momentum
                new WynnItem(new int[] { 0, 70, 0, 0, 0 }, new int[] { 0, 5, 0, 0, 0 }), // H-209 Miniature Defibrillator
        },
        new int[] { 56, 57, 0, 47, 0 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo056",
        new WynnItem[] {
                new WynnItem(new int[] { 45, 60, 30, 0, 0 }, new int[] { 10, 0, 15, 0, 0 }), // Caesura
                new WynnItem(new int[] { 0, 0, 100, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Aquarius
                new WynnItem(new int[] { 40, 40, 40, 0, 0 }, new int[] { 0, 0, 0, -30, -30 }), // Chaos-Woven Greaves
                new WynnItem(new int[] { 0, 65, 75, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Stardew
                new WynnItem(new int[] { 0, 0, 65, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Azeotrope
                new WynnItem(new int[] { 0, 0, 65, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Azeotrope
                new WynnItem(new int[] { 35, 45, 0, 0, 0 }, new int[] { 0, 3, 3, 0, 0 }), // Misalignment
                new WynnItem(new int[] { 0, 0, 100, 0, 0 }, new int[] { 0, 0, 7, 0, 0 }), // Diamond Hydro Necklace
        },
        new int[] { 45, 62, 82, 0, 0 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo057",
        new WynnItem[] {
                new WynnItem(new int[] { 0, 0, 110, 0, 0 }, new int[] { 3, 3, 0, 3, 3 }), // Resolution
                new WynnItem(new int[] { 0, 0, 100, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Aquarius
                new WynnItem(new int[] { 40, 40, 40, 0, 0 }, new int[] { 0, 0, 0, -30, -30 }), // Chaos-Woven Greaves
                new WynnItem(new int[] { 0, 65, 75, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Stardew
                new WynnItem(new int[] { 0, 0, 55, 0, 0 }, new int[] { 0, 0, 4, 0, 0 }), // Yang
                new WynnItem(new int[] { 0, 0, 55, 0, 0 }, new int[] { 0, 0, 4, 0, 0 }), // Yang
                new WynnItem(new int[] { 0, 0, 100, 0, 0 }, new int[] { 0, 0, 6, 0, 0 }), // Diamond Hydro Bracelet
                new WynnItem(new int[] { 0, 0, 100, 0, 0 }, new int[] { 0, 0, 7, 0, 0 }), // Diamond Hydro Necklace
        },
        new int[] { 37, 62, 92, 0, 0 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo058",
        new WynnItem[] {
                new WynnItem(new int[] { 0, 0, 110, 0, 0 }, new int[] { 3, 3, 0, 3, 3 }), // Resolution
                new WynnItem(new int[] { 0, 50, 50, 0, 0 }, new int[] { 0, 10, 10, 0, 0 }), // Soul Signal
                new WynnItem(new int[] { 40, 40, 40, 0, 0 }, new int[] { 0, 0, 0, -30, -30 }), // Chaos-Woven Greaves
                new WynnItem(new int[] { 0, 65, 75, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Stardew
                new WynnItem(new int[] { 0, 0, 55, 0, 0 }, new int[] { 0, 0, 4, 0, 0 }), // Yang
                new WynnItem(new int[] { 0, 0, 55, 0, 0 }, new int[] { 0, 0, 4, 0, 0 }), // Yang
                new WynnItem(new int[] { 0, 0, 100, 0, 0 }, new int[] { 0, 0, 6, 0, 0 }), // Diamond Hydro Bracelet
                new WynnItem(new int[] { 0, 65, 0, 0, 0 }, new int[] { 0, 5, 0, 0, 0 }), // Auxetic Capacitor
        },
        new int[] { 37, 52, 86, 0, 0 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo059",
        new WynnItem[] {
                new WynnItem(new int[] { 20, 20, 20, 20, 20 }, new int[] { 4, 4, 4, 4, 4 }), // Spectrum
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Discoverer
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 5, 5, 5, 5, 5 }), // Vaward
                new WynnItem(new int[] { 0, 0, 65, 0, 65 }, new int[] { 0, 0, 12, 0, 12 }), // Capricorn
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 1, 3, 0, 3, 1 }), // Summa
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Old Keeper's Ring
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Vindicator
                new WynnItem(new int[] { 20, 20, 20, 20, 20 }, new int[] { 4, 4, 4, 4, 4 }), // Diamond Fusion Necklace
        },
        new int[] { 14, 12, 52, 12, 51 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo060",
        new WynnItem[] {
                new WynnItem(new int[] { 0, 50, 20, 0, 0 }, new int[] { 0, 7, 0, 0, 0 }), // Neuron
                new WynnItem(new int[] { 0, 65, 65, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Stratosphere
                new WynnItem(new int[] { 40, 40, 40, 0, 0 }, new int[] { 0, 0, 0, -30, -30 }), // Chaos-Woven Greaves
                new WynnItem(new int[] { 0, 40, 50, 0, 0 }, new int[] { 0, 0, 10, 0, 0 }), // Pro Tempore
                new WynnItem(new int[] { 0, 100, 0, 0, 0 }, new int[] { 0, 8, 0, 0, 0 }), // Diamond Static Ring
                new WynnItem(new int[] { 0, 0, 25, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Lodestone
                new WynnItem(new int[] { 0, 100, 0, 0, 0 }, new int[] { 0, 6, 0, 0, 0 }), // Diamond Static Bracelet
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Abrasion
        },
        new int[] { 40, 93, 55, 0, 0 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo061",
        new WynnItem[] {
                new WynnItem(new int[] { 45, 60, 30, 0, 0 }, new int[] { 10, 0, 15, 0, 0 }), // Caesura
                new WynnItem(new int[] { 45, 0, 45, 0, 0 }, new int[] { 12, 0, 0, 0, 0 }), // Leviathan
                new WynnItem(new int[] { 105, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Chain Rule
                new WynnItem(new int[] { 0, 0, 75, 0, 0 }, new int[] { 0, 0, 10, 0, 0 }), // Condensation
                new WynnItem(new int[] { 0, 0, 65, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Azeotrope
                new WynnItem(new int[] { 0, 0, 65, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Azeotrope
                new WynnItem(new int[] { 35, 45, 0, 0, 0 }, new int[] { 0, 3, 3, 0, 0 }), // Misalignment
                new WynnItem(new int[] { 0, 0, 80, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Xebec
        },
        new int[] { 83, 57, 57, 0, 0 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo062",
        new WynnItem[] {
                new WynnItem(new int[] { 0, 0, 0, 0, 80 }, new int[] { 0, 0, 0, 0, 9 }), // Unravel
                new WynnItem(new int[] { 45, 45, 45, 0, 45 }, new int[] { 5, 5, 5, -99, 5 }), // Phantasmagoria
                new WynnItem(new int[] { 0, 0, 0, 0, 80 }, new int[] { 0, 0, 0, 0, 13 }), // Sagittarius
                new WynnItem(new int[] { 0, 30, 30, 0, 40 }, new int[] { 0, 0, 0, 0, 10 }), // Steamjet Walkers
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 1, 3, 0, 3, 1 }), // Summa
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 1, 3, 0, 3, 1 }), // Summa
                new WynnItem(new int[] { 0, 0, 45, 0, 45 }, new int[] { -3, -3, 0, -5, 0 }), // Breakthrough
                new WynnItem(new int[] { 0, 45, 45, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Eyes on All
        },
        new int[] { 46, 42, 45, 0, 63 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo063",
        new WynnItem[] {
                new WynnItem(new int[] { 0, 40, 40, 0, 40 }, new int[] { 0, 10, 10, 0, 10 }), // Cumulonimbus
                new WynnItem(new int[] { 45, 45, 45, 0, 45 }, new int[] { 5, 5, 5, -99, 5 }), // Phantasmagoria
                new WynnItem(new int[] { 0, 0, 65, 0, 65 }, new int[] { 0, 0, 5, 0, 5 }), // Apophenia
                new WynnItem(new int[] { 0, 30, 30, 0, 40 }, new int[] { 0, 0, 0, 0, 10 }), // Steamjet Walkers
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Intensity
                new WynnItem(new int[] { 0, 25, 25, 0, 0 }, new int[] { 0, 4, 3, 0, 0 }), // Finesse
                new WynnItem(new int[] { 0, 0, 45, 0, 45 }, new int[] { -3, -3, 0, -5, 0 }), // Breakthrough
                new WynnItem(new int[] { 0, 20, 0, 0, 20 }, new int[] { 0, 5, 0, 0, 5 }), // Bottled Thunderstorm
        },
        new int[] { 48, 31, 47, 0, 35 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo064",
        new WynnItem[] {
                new WynnItem(new int[] { 35, 25, 25, 35, 35 }, new int[] { 0, 0, 0, 0, 0 }), // Splintered Dawn
                new WynnItem(new int[] { 33, 33, 33, 33, 33 }, new int[] { 6, 6, 6, 6, 6 }), // Libra
                new WynnItem(new int[] { 40, 40, 40, 40, 40 }, new int[] { 2, 2, 2, 2, 2 }), // Rainbow Sanctuary
                new WynnItem(new int[] { 0, 65, 75, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Stardew
                new WynnItem(new int[] { 25, 0, 25, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Draoi Fair
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 1, 3, 0, 3, 1 }), // Summa
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 4, 4, 4, 4, 4 }), // Prowess
                new WynnItem(new int[] { 0, 0, 80, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Xebec
        },
        new int[] { 29, 50, 68, 27, 29 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo065",
        new WynnItem[] {
                new WynnItem(new int[] { 20, 20, 20, 20, 20 }, new int[] { 4, 4, 4, 4, 4 }), // Spectrum
                new WynnItem(new int[] { 33, 33, 33, 33, 33 }, new int[] { 6, 6, 6, 6, 6 }), // Libra
                new WynnItem(new int[] { 40, 40, 40, 40, 40 }, new int[] { 2, 2, 2, 2, 2 }), // Rainbow Sanctuary
                new WynnItem(new int[] { 0, 0, 85, 0, 0 }, new int[] { 0, 0, 10, 0, 0 }), // Martingale
                new WynnItem(new int[] { 0, 0, 65, 0, 0 }, new int[] { 0, 0, 3, 0, 0 }), // Moon Pool Circlet
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 1, 1, 1, 1, 1 }), // Photon
                new WynnItem(new int[] { 30, 30, 60, 30, 30 }, new int[] { 0, 0, 0, 0, 0 }), // Succession
                new WynnItem(new int[] { 20, 20, 20, 20, 20 }, new int[] { 4, 4, 4, 4, 4 }), // Diamond Fusion Necklace
        },
        new int[] { 25, 25, 65, 25, 25 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo066",
        new WynnItem[] {
                new WynnItem(new int[] { 35, 25, 25, 35, 35 }, new int[] { 0, 0, 0, 0, 0 }), // Splintered Dawn
                new WynnItem(new int[] { 33, 33, 33, 33, 33 }, new int[] { 6, 6, 6, 6, 6 }), // Libra
                new WynnItem(new int[] { 40, 40, 40, 40, 40 }, new int[] { 2, 2, 2, 2, 2 }), // Rainbow Sanctuary
                new WynnItem(new int[] { 0, 65, 75, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Stardew
                new WynnItem(new int[] { 25, 0, 25, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Draoi Fair
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 1, 3, 0, 3, 1 }), // Summa
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 4, 4, 4, 4, 4 }), // Prowess
                new WynnItem(new int[] { 0, 0, 80, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Xebec
        },
        new int[] { 29, 50, 68, 27, 29 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo067",
        new WynnItem[] {
                new WynnItem(new int[] { 35, 25, 25, 35, 35 }, new int[] { 0, 0, 0, 0, 0 }), // Splintered Dawn
                new WynnItem(new int[] { 33, 33, 33, 33, 33 }, new int[] { 6, 6, 6, 6, 6 }), // Libra
                new WynnItem(new int[] { 40, 40, 40, 40, 40 }, new int[] { 2, 2, 2, 2, 2 }), // Rainbow Sanctuary
                new WynnItem(new int[] { 0, 65, 75, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Stardew
                new WynnItem(new int[] { 0, 0, 65, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Azeotrope
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 1, 3, 0, 3, 1 }), // Summa
                new WynnItem(new int[] { 0, 0, 0, 40, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Dragon's Eye Bracelet
                new WynnItem(new int[] { 20, 20, 20, 20, 20 }, new int[] { 4, 4, 4, 4, 4 }), // Diamond Fusion Necklace
        },
        new int[] { 29, 50, 63, 27, 29 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo068",
        new WynnItem[] {
                new WynnItem(new int[] { 45, 45, 45, 45, 0 }, new int[] { 5, 5, 5, 5, -99 }), // Guillotine
                new WynnItem(new int[] { 0, 50, 50, 0, 0 }, new int[] { 0, 10, 10, 0, 0 }), // Soul Signal
                new WynnItem(new int[] { 0, 30, 60, 0, 0 }, new int[] { 5, 0, 0, 5, 5 }), // Aleph Null
                new WynnItem(new int[] { 0, 50, 0, 40, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Frenzied Mockery
                new WynnItem(new int[] { 0, 0, 65, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Azeotrope
                new WynnItem(new int[] { 0, 0, 65, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Azeotrope
                new WynnItem(new int[] { 0, 55, 0, 45, 0 }, new int[] { 0, 1, 0, 1, 0 }), // Veneration
                new WynnItem(new int[] { 0, 40, 0, 40, 0 }, new int[] { 0, 4, 0, 4, 0 }), // Tenuto
        },
        new int[] { 40, 50, 50, 36, 0 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo069",
        new WynnItem[] {
                new WynnItem(new int[] { 40, 40, 0, 40, 0 }, new int[] { 0, 0, -30, 0, -30 }), // Obsidian-Framed Helmet
                new WynnItem(new int[] { 90, 0, 0, 0, 0 }, new int[] { 15, 0, 0, 0, 0 }), // Taurus
                new WynnItem(new int[] { 49, 31, 0, 37, 0 }, new int[] { 0, 0, 0, 0, -43 }), // Writhing Growth
                new WynnItem(new int[] { 95, 0, 0, 0, 0 }, new int[] { 10, 0, 0, 0, 0 }), // Blind Thrust
                new WynnItem(new int[] { 50, 0, 0, 0, 0 }, new int[] { 4, 0, 0, 0, 0 }), // Ad Terram
                new WynnItem(new int[] { 50, 0, 0, 0, 0 }, new int[] { 4, 0, 0, 0, 0 }), // Ad Terram
                new WynnItem(new int[] { 50, 0, 0, 0, 0 }, new int[] { 5, 3, 0, 0, 0 }), // Momentum
                new WynnItem(new int[] { 0, 70, 0, 0, 0 }, new int[] { 0, 5, 0, 0, 0 }), // H-209 Miniature Defibrillator
        },
        new int[] { 77, 67, 0, 40, 0 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo070",
        new WynnItem[] {
                new WynnItem(new int[] { 60, 0, 0, 50, 0 }, new int[] { 5, 0, 0, 5, 0 }), // Nuclear Emesis
                new WynnItem(new int[] { 80, 80, 0, 0, 0 }, new int[] { 20, 20, 0, 0, 0 }), // Bete Noire
                new WynnItem(new int[] { 50, 50, 0, 0, 0 }, new int[] { 10, 10, 0, 0, 0 }), // Tera
                new WynnItem(new int[] { 0, 50, 0, 50, 0 }, new int[] { 0, 12, 0, 12, 0 }), // Nether's Scar
                new WynnItem(new int[] { 40, 0, 30, 0, 0 }, new int[] { 0, 0, 5, 0, 0 }), // Olive
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Suppression
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 4, 4, 4, 4, 4 }), // Prowess
                new WynnItem(new int[] { 55, 0, 0, 45, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Simulacrum
        },
        new int[] { 61, 54, 26, 46, 0 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo071",
        new WynnItem[] {
                new WynnItem(new int[] { 0, 50, 0, 45, 0 }, new int[] { 0, 0, 0, 10, 0 }), // Kindled Orchid
                new WynnItem(new int[] { 0, 55, 0, 50, 0 }, new int[] { 0, 5, 0, 4, 6 }), // Calidade Mail
                new WynnItem(new int[] { 40, 40, 0, 40, 40 }, new int[] { 0, 0, -20, 0, 0 }), // Runebound Chains
                new WynnItem(new int[] { 0, 50, 0, 50, 0 }, new int[] { 0, 12, 0, 12, 0 }), // Nether's Scar
                new WynnItem(new int[] { 0, 0, 0, 40, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Flashfire Knuckle
                new WynnItem(new int[] { 0, 55, 0, 0, 55 }, new int[] { 0, 0, 0, 0, 0 }), // Breezehands
                new WynnItem(new int[] { 0, 0, 0, 40, 0 }, new int[] { 0, 0, 0, 4, 0 }), // Flashfire Gauntlet
                new WynnItem(new int[] { 45, 0, 0, 0, 0 }, new int[] { 6, 0, 0, 0, 0 }), // Recalcitrance
        },
        new int[] { 45, 50, 0, 41, 49 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo072",
        new WynnItem[] {
                new WynnItem(new int[] { 0, 45, 0, 45, 0 }, new int[] { 10, 10, 0, 10, 0 }), // Darksteel Full Helm
                new WynnItem(new int[] { 90, 0, 0, 0, 0 }, new int[] { 15, 0, 0, 0, 0 }), // Taurus
                new WynnItem(new int[] { 49, 31, 0, 37, 0 }, new int[] { 0, 0, 0, 0, -43 }), // Writhing Growth
                new WynnItem(new int[] { 95, 0, 0, 0, 0 }, new int[] { 10, 0, 0, 0, 0 }), // Blind Thrust
                new WynnItem(new int[] { 0, 60, 0, 40, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Lost Seconds
                new WynnItem(new int[] { 0, 60, 0, 40, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Lost Seconds
                new WynnItem(new int[] { 50, 0, 0, 0, 0 }, new int[] { 5, 3, 0, 0, 0 }), // Momentum
                new WynnItem(new int[] { 0, 70, 0, 0, 0 }, new int[] { 0, 5, 0, 0, 0 }), // H-209 Miniature Defibrillator
        },
        new int[] { 75, 57, 0, 45, 0 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo073",
        new WynnItem[] {
                new WynnItem(new int[] { 60, 0, 0, 0, 70 }, new int[] { 12, 0, 0, 0, 16 }), // Dune Storm
                new WynnItem(new int[] { 0, 0, 0, 0, 105 }, new int[] { 0, 0, 0, 0, 0 }), // Conduit of Spirit
                new WynnItem(new int[] { 0, 0, 0, 0, 80 }, new int[] { 0, 0, 0, 0, 13 }), // Sagittarius
                new WynnItem(new int[] { 0, 0, 0, 0, 70 }, new int[] { 0, 0, 0, 0, 0 }), // Skidbladnir
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 1, 1, 1, 1, 1 }), // Photon
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 1, 1, 1, 1, 1 }), // Photon
                new WynnItem(new int[] { 0, 0, 0, 0, 30 }, new int[] { 0, 0, 0, 0, 0 }), // Vortex Bracer
                new WynnItem(new int[] { 0, 0, 0, 0, 40 }, new int[] { 0, 0, 0, 0, 0 }), // Renda Langit
        },
        new int[] { 58, 0, 0, 0, 74 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo074",
        new WynnItem[] {
                new WynnItem(new int[] { 35, 0, 0, 0, 35 }, new int[] { 0, 0, 0, 0, 0 }), // Filter Mask
                new WynnItem(new int[] { 0, 30, 0, 0, 30 }, new int[] { 0, 7, 12, 0, 7 }), // Gale's Freedom
                new WynnItem(new int[] { 0, 30, 60, 0, 0 }, new int[] { 5, 0, 0, 5, 5 }), // Aleph Null
                new WynnItem(new int[] { 0, 0, 0, 0, 70 }, new int[] { 0, 0, 0, 0, 0 }), // Skidbladnir
                new WynnItem(new int[] { 0, 0, 55, 0, 0 }, new int[] { 0, 0, 4, 0, 0 }), // Yang
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Intensity
                new WynnItem(new int[] { 35, 0, 0, 0, 35 }, new int[] { 0, 0, 0, 0, 0 }), // Synapse
                new WynnItem(new int[] { 0, 0, 0, 0, 40 }, new int[] { 0, 0, 0, 0, 0 }), // Renda Langit
        },
        new int[] { 30, 30, 44, 0, 58 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo075",
        new WynnItem[] {
                new WynnItem(new int[] { 40, 70, 0, 0, 0 }, new int[] { 13, 0, -50, 0, 0 }), // Brainwash
                new WynnItem(new int[] { 0, 75, 0, 65, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Insignia
                new WynnItem(new int[] { 0, 75, 0, 0, 0 }, new int[] { 0, 15, 0, 0, -13 }), // Asphyxia
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Ornate Shadow Cloud
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Intensity
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 1, 1, 1, 1, 1 }), // Photon
                new WynnItem(new int[] { 0, 100, 0, 0, 0 }, new int[] { 0, 6, 0, 0, 0 }), // Diamond Static Bracelet
                new WynnItem(new int[] { 55, 0, 0, 45, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Simulacrum
        },
        new int[] { 41, 84, 0, 64, 0 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo076",
        new WynnItem[] {
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Ossuary
                new WynnItem(new int[] { 0, 55, 0, 55, 55 }, new int[] { 0, 5, 0, 0, 0 }), // Future Shock Plating
                new WynnItem(new int[] { 40, 40, 0, 40, 40 }, new int[] { 0, 0, -20, 0, 0 }), // Runebound Chains
                new WynnItem(new int[] { 0, 45, 0, 0, 30 }, new int[] { 0, 0, 0, -5, 15 }), // Thunderous Step
                new WynnItem(new int[] { 35, 35, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Agave
                new WynnItem(new int[] { 0, 35, 0, 0, 0 }, new int[] { 0, 0, 1, 0, 0 }), // Microchip
                new WynnItem(new int[] { 0, 40, 0, 0, 0 }, new int[] { 0, 1, 0, 0, 0 }), // Lachesism
                new WynnItem(new int[] { 0, 40, 0, 40, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Pulse Stopper
        },
        new int[] { 40, 54, 0, 60, 40 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo077",
        new WynnItem[] {
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Ossuary
                new WynnItem(new int[] { 0, 0, 0, 0, 90 }, new int[] { 0, 0, 0, 0, 0 }), // Echoes of the Lost
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Ornate Shadow Cover
                new WynnItem(new int[] { 0, 0, 0, 0, 70 }, new int[] { 0, 0, 0, 0, 0 }), // Skidbladnir
                new WynnItem(new int[] { 0, 0, 0, 0, 50 }, new int[] { 0, 0, 0, 0, 5 }), // Rask
                new WynnItem(new int[] { 0, 0, 0, 0, 50 }, new int[] { 0, 0, 0, 0, 5 }), // Rask
                new WynnItem(new int[] { 0, 0, 0, 0, 30 }, new int[] { 0, 0, 0, 0, 0 }), // Vortex Bracer
                new WynnItem(new int[] { 0, 0, 0, 0, 0 }, new int[] { 0, 0, 0, 0, 0 }), // Contrast
        },
        new int[] { 0, 0, 0, 0, 80 },
        new boolean[] { true, true, true, true, true, true, true, true });

        add("sugo078",
        new WynnItem[] {
                new WynnItem(new int[] { 0, 0, 0, 0, 80 }, new int[] { 0, 0, 0, 0, 9 }), // Unravel
                new WynnItem(new int[] { 0, 45, 0, 0, 55 }, new int[] { 0, 0, 0, 0, 0 }), // Wanderlust
                new WynnItem(new int[] { 40, 40, 0, 40, 40 }, new int[] { 0, 0, -20, 0, 0 }), // Runebound Chains
                new WynnItem(new int[] { 0, 0, 0, 0, 70 }, new int[] { 0, 0, 0, 0, 0 }), // Skidbladnir
                new WynnItem(new int[] { 55, 0, 0, 0, 55 }, new int[] { 0, 3, 2, 3, 0 }), // Ingress
                new WynnItem(new int[] { 55, 0, 0, 0, 55 }, new int[] { 0, 3, 2, 3, 0 }), // Ingress
                new WynnItem(new int[] { 0, 0, 0, 0, 30 }, new int[] { 0, 0, 0, 0, 0 }), // Vortex Bracer
                new WynnItem(new int[] { 0, 20, 0, 0, 20 }, new int[] { 0, 5, 0, 0, 5 }), // Bottled Thunderstorm
        },
        new int[] { 55, 34, 0, 34, 75 },
        new boolean[] { true, true, true, true, true, true, true, true });


        // SUGO TEST CASES END

    }

    private static void add(String name, WynnItem[] items, int[] sp, boolean[] expected) {
        ALL.put(name, new TestCase(name, items, sp, expected));
    }

    private TestCases() {
    }
}
