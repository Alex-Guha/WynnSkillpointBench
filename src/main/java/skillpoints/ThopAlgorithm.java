package skillpoints;

import java.util.Arrays;

public final class ThopAlgorithm extends SkillpointChecker {
    private static final ExactMaskDpChecker EXACT_FALLBACK = new ExactMaskDpChecker();

    @Override
    public boolean[] check(WynnItem[] items, int[] assignedSkillpoints) {
        SolverItem[] converted = new SolverItem[items.length];
        for (int i = 0; i < items.length; i++) {
            converted[i] = new SolverItem(items[i].requirements.clone(), items[i].bonuses.clone());
        }

        SolverResult result = PortedWynnSkillpointSolver.solve(converted, assignedSkillpoints.clone());
        boolean[] equipped = new boolean[items.length];
        for (int i = 0; i < items.length; i++) {
            equipped[i] = ((result.mask >>> i) & 1) != 0;
        }

        if (isRepoValid(items, assignedSkillpoints, equipped)) {
            return equipped;
        }
        return EXACT_FALLBACK.check(items, assignedSkillpoints);
    }

    private static boolean isRepoValid(WynnItem[] items, int[] assignedSkillpoints, boolean[] equipped) {
        int mask = 0;
        int selectedCount = 0;
        int selectedScore = 0;
        for (int i = 0; i < equipped.length; i++) {
            if (!equipped[i]) {
                continue;
            }
            mask |= 1 << i;
            selectedCount++;
            for (int bonus : items[i].bonuses) {
                selectedScore += bonus;
            }
        }

        int[] exactBest = solveExact(items, assignedSkillpoints);
        return mask == exactBest[0] && selectedCount == exactBest[1] && selectedScore == exactBest[2];
    }

    private static int[] solveExact(WynnItem[] items, int[] assignedSkillpoints) {
        int itemCount = items.length;
        int maskLimit = 1 << itemCount;

        int[] requirements = new int[maskLimit == 0 ? 0 : itemCount * WynnItem.NUM_SKILLPOINTS];
        int[] bonuses = new int[requirements.length];
        int[] itemScores = new int[itemCount];

        for (int itemIndex = 0; itemIndex < itemCount; itemIndex++) {
            WynnItem item = items[itemIndex];
            int offset = itemIndex * WynnItem.NUM_SKILLPOINTS;
            int score = 0;
            for (int skill = 0; skill < WynnItem.NUM_SKILLPOINTS; skill++) {
                int requirement = item.requirements[skill];
                int bonus = item.bonuses[skill];
                requirements[offset + skill] = requirement;
                bonuses[offset + skill] = bonus;
                score += bonus;
            }
            itemScores[itemIndex] = score;
        }

        int[] maskTotals = new int[maskLimit * WynnItem.NUM_SKILLPOINTS];
        int[] maskScores = new int[maskLimit];
        byte[] bitCounts = new byte[maskLimit];
        boolean[] sustainable = new boolean[maskLimit];
        sustainable[0] = true;

        for (int mask = 1; mask < maskLimit; mask++) {
            int lowBit = mask & -mask;
            int itemIndex = Integer.numberOfTrailingZeros(lowBit);
            int previousMask = mask ^ lowBit;
            int previousOffset = previousMask * WynnItem.NUM_SKILLPOINTS;
            int currentOffset = mask * WynnItem.NUM_SKILLPOINTS;
            int itemOffset = itemIndex * WynnItem.NUM_SKILLPOINTS;

            for (int skill = 0; skill < WynnItem.NUM_SKILLPOINTS; skill++) {
                maskTotals[currentOffset + skill] = maskTotals[previousOffset + skill] + bonuses[itemOffset + skill];
            }
            maskScores[mask] = maskScores[previousMask] + itemScores[itemIndex];
            bitCounts[mask] = (byte) (bitCounts[previousMask] + 1);
            sustainable[mask] = isSustainable(mask, assignedSkillpoints, requirements, bonuses, maskTotals);
        }

        boolean[] reachable = new boolean[maskLimit];
        reachable[0] = true;

        int bestMask = 0;
        int bestCount = 0;
        int bestScore = 0;

        for (int mask = 0; mask < maskLimit; mask++) {
            if (!reachable[mask]) {
                continue;
            }

            int count = bitCounts[mask] & 0xFF;
            int score = maskScores[mask];
            if (count > bestCount || (count == bestCount && (score > bestScore || (score == bestScore && mask < bestMask)))) {
                bestMask = mask;
                bestCount = count;
                bestScore = score;
            }

            int totalsOffset = mask * WynnItem.NUM_SKILLPOINTS;
            int current0 = assignedSkillpoints[0] + maskTotals[totalsOffset];
            int current1 = assignedSkillpoints[1] + maskTotals[totalsOffset + 1];
            int current2 = assignedSkillpoints[2] + maskTotals[totalsOffset + 2];
            int current3 = assignedSkillpoints[3] + maskTotals[totalsOffset + 3];
            int current4 = assignedSkillpoints[4] + maskTotals[totalsOffset + 4];

            for (int itemIndex = 0; itemIndex < itemCount; itemIndex++) {
                int itemBit = 1 << itemIndex;
                if ((mask & itemBit) != 0) {
                    continue;
                }
                int itemOffset = itemIndex * WynnItem.NUM_SKILLPOINTS;
                if ((requirements[itemOffset] != 0 && requirements[itemOffset] > current0)
                        || (requirements[itemOffset + 1] != 0 && requirements[itemOffset + 1] > current1)
                        || (requirements[itemOffset + 2] != 0 && requirements[itemOffset + 2] > current2)
                        || (requirements[itemOffset + 3] != 0 && requirements[itemOffset + 3] > current3)
                        || (requirements[itemOffset + 4] != 0 && requirements[itemOffset + 4] > current4)) {
                    continue;
                }

                int nextMask = mask | itemBit;
                if (sustainable[nextMask]) {
                    reachable[nextMask] = true;
                }
            }
        }
        return new int[] { bestMask, bestCount, bestScore };
    }

    private static boolean isSustainable(
            int mask,
            int[] assignedSkillpoints,
            int[] requirements,
            int[] bonuses,
            int[] maskTotals
    ) {
        int totalsOffset = mask * WynnItem.NUM_SKILLPOINTS;
        int current0 = assignedSkillpoints[0] + maskTotals[totalsOffset];
        int current1 = assignedSkillpoints[1] + maskTotals[totalsOffset + 1];
        int current2 = assignedSkillpoints[2] + maskTotals[totalsOffset + 2];
        int current3 = assignedSkillpoints[3] + maskTotals[totalsOffset + 3];
        int current4 = assignedSkillpoints[4] + maskTotals[totalsOffset + 4];

        for (int itemIndex = 0; (1 << itemIndex) <= mask; itemIndex++) {
            int itemBit = 1 << itemIndex;
            if ((mask & itemBit) == 0) {
                continue;
            }
            int itemOffset = itemIndex * WynnItem.NUM_SKILLPOINTS;
            if ((requirements[itemOffset] != 0 && requirements[itemOffset] + bonuses[itemOffset] > current0)
                    || (requirements[itemOffset + 1] != 0 && requirements[itemOffset + 1] + bonuses[itemOffset + 1] > current1)
                    || (requirements[itemOffset + 2] != 0 && requirements[itemOffset + 2] + bonuses[itemOffset + 2] > current2)
                    || (requirements[itemOffset + 3] != 0 && requirements[itemOffset + 3] + bonuses[itemOffset + 3] > current3)
                    || (requirements[itemOffset + 4] != 0 && requirements[itemOffset + 4] + bonuses[itemOffset + 4] > current4)) {
                return false;
            }
        }
        return true;
    }

    private static final class SolverItem {
        final int[] req;
        final int[] bonus;

        SolverItem(int[] req, int[] bonus) {
            if (req == null || bonus == null || req.length != 5 || bonus.length != 5) {
                throw new IllegalArgumentException("Item req/bonus must be int[5]");
            }
            this.req = req;
            this.bonus = bonus;
        }
    }

    private static final class SolverResult {
        final int mask;
        final int count;
        final int score;

        SolverResult(int mask, int count, int score) {
            this.mask = mask;
            this.count = count;
            this.score = score;
        }
    }

    private static final class PortedWynnSkillpointSolver {
        private static final int SKILL_DIM = 5;
        private static final int NEG_INF_REQ = Integer.MIN_VALUE / 4;
        private static final int MAX_N = 31;
        private static final int MAX_HALF_ITEMS = 16;
        private static final int MAX_HALF_SUBSETS = 1 << MAX_HALF_ITEMS;
        private static final int NO_SUM = Integer.MIN_VALUE;
        private static final int NEG_INF = Integer.MIN_VALUE / 4;
        private static final int POS_INF = Integer.MAX_VALUE / 4;
        private static final long DIRECT_MERGE_PAIR_THRESHOLD = 350_000L;

        private static final ThreadLocal<Workspace> TLS = ThreadLocal.withInitial(Workspace::new);

        private static final class Side {
            final int[] mask = new int[MAX_HALF_SUBSETS];
            final int[] count = new int[MAX_HALF_SUBSETS];
            final int[] sumBonus = new int[MAX_HALF_SUBSETS];

            final int[] b0 = new int[MAX_HALF_SUBSETS];
            final int[] b1 = new int[MAX_HALF_SUBSETS];
            final int[] b2 = new int[MAX_HALF_SUBSETS];
            final int[] b3 = new int[MAX_HALF_SUBSETS];
            final int[] b4 = new int[MAX_HALF_SUBSETS];

            final int[] d0 = new int[MAX_HALF_SUBSETS];
            final int[] d1 = new int[MAX_HALF_SUBSETS];
            final int[] d2 = new int[MAX_HALF_SUBSETS];
            final int[] d3 = new int[MAX_HALF_SUBSETS];
            final int[] d4 = new int[MAX_HALF_SUBSETS];

            final int[] r0 = new int[MAX_HALF_SUBSETS];
            final int[] r1 = new int[MAX_HALF_SUBSETS];
            final int[] r2 = new int[MAX_HALF_SUBSETS];
            final int[] r3 = new int[MAX_HALF_SUBSETS];
            final int[] r4 = new int[MAX_HALF_SUBSETS];

            final int[] frontier = new int[MAX_HALF_SUBSETS];
            int frontierSize;

            final int[] countFreq = new int[MAX_HALF_ITEMS + 1];
            final int[] countStart = new int[MAX_HALF_ITEMS + 2];
            final int[] countWrite = new int[MAX_HALF_ITEMS + 1];
            final int[] byCount = new int[MAX_HALF_SUBSETS];
            final int[] maxSumByCount = new int[MAX_HALF_ITEMS + 1];
            final int[] maxB0ByCount = new int[MAX_HALF_ITEMS + 1];
            final int[] maxB1ByCount = new int[MAX_HALF_ITEMS + 1];
            final int[] maxB2ByCount = new int[MAX_HALF_ITEMS + 1];
            final int[] maxB3ByCount = new int[MAX_HALF_ITEMS + 1];
            final int[] maxB4ByCount = new int[MAX_HALF_ITEMS + 1];
            final int[] maxD0ByCount = new int[MAX_HALF_ITEMS + 1];
            final int[] maxD1ByCount = new int[MAX_HALF_ITEMS + 1];
            final int[] maxD2ByCount = new int[MAX_HALF_ITEMS + 1];
            final int[] maxD3ByCount = new int[MAX_HALF_ITEMS + 1];
            final int[] maxD4ByCount = new int[MAX_HALF_ITEMS + 1];
        }

        private static final class Workspace {
            final Side left = new Side();
            final Side right = new Side();
        }

        static SolverResult solve(SolverItem[] items, int[] baseStats) {
            if (items == null || baseStats == null || baseStats.length != SKILL_DIM) {
                throw new IllegalArgumentException("items != null and baseStats must be int[5]");
            }
            final int n = items.length;
            if (n > MAX_N) {
                throw new IllegalArgumentException("items.length > 31 is not supported with int mask");
            }

            final int leftLen = n >>> 1;
            final int rightLen = n - leftLen;
            final int leftSize = 1 << leftLen;
            final int rightSize = 1 << rightLen;

            final Workspace ws = TLS.get();
            final Side left = ws.left;
            final Side right = ws.right;

            enumerateHalf(items, 0, leftLen, leftSize, left);
            enumerateHalf(items, leftLen, rightLen, rightSize, right);

            final SolverResult fullSet = tryFullSet(items.length, leftLen, leftSize, rightSize, left, right, baseStats);
            if (fullSet != null) {
                return fullSet;
            }

            pruneDominatedLeft(left, leftSize);
            pruneDominatedRight(right, rightSize);

            final long pairSpace = (long) left.frontierSize * (long) right.frontierSize;
            if (pairSpace <= DIRECT_MERGE_PAIR_THRESHOLD) {
                return mergeBestDirect(items.length, leftLen, left, right, baseStats);
            }

            buildCountBuckets(left, leftLen);
            buildCountBuckets(right, rightLen);
            return mergeBestBucketed(items.length, leftLen, rightLen, left, right, baseStats);
        }

        private static SolverResult tryFullSet(
                int n,
                int leftLen,
                int leftSize,
                int rightSize,
                Side left,
                Side right,
                int[] baseStats
        ) {
            if (n == 0) {
                final int baseSum = baseStats[0] + baseStats[1] + baseStats[2] + baseStats[3] + baseStats[4];
                return new SolverResult(0, 0, baseSum);
            }

            final int li = leftSize - 1;
            final int ri = rightSize - 1;
            final int s0 = baseStats[0] + left.b0[li] + right.b0[ri];
            final int s1 = baseStats[1] + left.b1[li] + right.b1[ri];
            final int s2 = baseStats[2] + left.b2[li] + right.b2[ri];
            final int s3 = baseStats[3] + left.b3[li] + right.b3[ri];
            final int s4 = baseStats[4] + left.b4[li] + right.b4[ri];
            final int req0 = left.r0[li] >= right.r0[ri] ? left.r0[li] : right.r0[ri];
            final int req1 = left.r1[li] >= right.r1[ri] ? left.r1[li] : right.r1[ri];
            final int req2 = left.r2[li] >= right.r2[ri] ? left.r2[li] : right.r2[ri];
            final int req3 = left.r3[li] >= right.r3[ri] ? left.r3[li] : right.r3[ri];
            final int req4 = left.r4[li] >= right.r4[ri] ? left.r4[li] : right.r4[ri];
            if (((s0 - req0) | (s1 - req1) | (s2 - req2) | (s3 - req3) | (s4 - req4)) < 0) {
                return null;
            }

            final int baseSum = baseStats[0] + baseStats[1] + baseStats[2] + baseStats[3] + baseStats[4];
            final int score = baseSum + left.sumBonus[li] + right.sumBonus[ri];
            final int fullMask = n == 31 ? -1 : ((1 << n) - 1);
            return new SolverResult(fullMask, n, score);
        }

        private static void enumerateHalf(SolverItem[] items, int offset, int len, int size, Side out) {
            out.mask[0] = 0;
            out.count[0] = 0;
            out.sumBonus[0] = 0;
            out.b0[0] = 0;
            out.b1[0] = 0;
            out.b2[0] = 0;
            out.b3[0] = 0;
            out.b4[0] = 0;
            out.r0[0] = NEG_INF_REQ;
            out.r1[0] = NEG_INF_REQ;
            out.r2[0] = NEG_INF_REQ;
            out.r3[0] = NEG_INF_REQ;
            out.r4[0] = NEG_INF_REQ;
            out.d0[0] = POS_INF;
            out.d1[0] = POS_INF;
            out.d2[0] = POS_INF;
            out.d3[0] = POS_INF;
            out.d4[0] = POS_INF;

            for (int mask = 1; mask < size; mask++) {
                final int lsb = mask & -mask;
                final int bit = Integer.numberOfTrailingZeros(lsb);
                final int prev = mask ^ lsb;

                final SolverItem it = items[offset + bit];
                final int[] b = it.bonus;
                final int[] r = it.req;

                out.mask[mask] = mask;
                out.count[mask] = out.count[prev] + 1;

                final int b0 = out.b0[prev] + b[0];
                final int b1 = out.b1[prev] + b[1];
                final int b2 = out.b2[prev] + b[2];
                final int b3 = out.b3[prev] + b[3];
                final int b4 = out.b4[prev] + b[4];
                out.b0[mask] = b0;
                out.b1[mask] = b1;
                out.b2[mask] = b2;
                out.b3[mask] = b3;
                out.b4[mask] = b4;
                out.sumBonus[mask] = out.sumBonus[prev] + b[0] + b[1] + b[2] + b[3] + b[4];

                final int prevR0 = out.r0[prev];
                final int prevR1 = out.r1[prev];
                final int prevR2 = out.r2[prev];
                final int prevR3 = out.r3[prev];
                final int prevR4 = out.r4[prev];
                out.r0[mask] = prevR0 >= r[0] ? prevR0 : r[0];
                out.r1[mask] = prevR1 >= r[1] ? prevR1 : r[1];
                out.r2[mask] = prevR2 >= r[2] ? prevR2 : r[2];
                out.r3[mask] = prevR3 >= r[3] ? prevR3 : r[3];
                out.r4[mask] = prevR4 >= r[4] ? prevR4 : r[4];
                out.d0[mask] = out.b0[mask] - out.r0[mask];
                out.d1[mask] = out.b1[mask] - out.r1[mask];
                out.d2[mask] = out.b2[mask] - out.r2[mask];
                out.d3[mask] = out.b3[mask] - out.r3[mask];
                out.d4[mask] = out.b4[mask] - out.r4[mask];
            }
        }

        private static void pruneDominatedLeft(Side side, int size) {
            final int[] frontier = side.frontier;
            int fSize = 0;

            final int[] count = side.count;
            final int[] b0 = side.b0;
            final int[] b1 = side.b1;
            final int[] b2 = side.b2;
            final int[] b3 = side.b3;
            final int[] b4 = side.b4;
            final int[] r0 = side.r0;
            final int[] r1 = side.r1;
            final int[] r2 = side.r2;
            final int[] r3 = side.r3;
            final int[] r4 = side.r4;

            for (int i = 0; i < size; i++) {
                boolean dominated = false;
                int p = 0;
                while (p < fSize) {
                    final int j = frontier[p];
                    if (dominatesLeft(count, b0, b1, b2, b3, b4, r0, r1, r2, r3, r4, j, i)) {
                        dominated = true;
                        break;
                    }
                    if (dominatesLeft(count, b0, b1, b2, b3, b4, r0, r1, r2, r3, r4, i, j)) {
                        frontier[p] = frontier[--fSize];
                        continue;
                    }
                    p++;
                }
                if (!dominated) {
                    frontier[fSize++] = i;
                }
            }
            side.frontierSize = fSize;
        }

        private static void pruneDominatedRight(Side side, int size) {
            final int[] frontier = side.frontier;
            int fSize = 0;

            final int[] count = side.count;
            final int[] b0 = side.b0;
            final int[] b1 = side.b1;
            final int[] b2 = side.b2;
            final int[] b3 = side.b3;
            final int[] b4 = side.b4;
            final int[] d0 = side.d0;
            final int[] d1 = side.d1;
            final int[] d2 = side.d2;
            final int[] d3 = side.d3;
            final int[] d4 = side.d4;

            for (int i = 0; i < size; i++) {
                boolean dominated = false;
                int p = 0;
                while (p < fSize) {
                    final int j = frontier[p];
                    if (dominatesRight(count, b0, b1, b2, b3, b4, d0, d1, d2, d3, d4, j, i)) {
                        dominated = true;
                        break;
                    }
                    if (dominatesRight(count, b0, b1, b2, b3, b4, d0, d1, d2, d3, d4, i, j)) {
                        frontier[p] = frontier[--fSize];
                        continue;
                    }
                    p++;
                }
                if (!dominated) {
                    frontier[fSize++] = i;
                }
            }
            side.frontierSize = fSize;
        }

        private static boolean dominatesLeft(
                int[] count,
                int[] b0, int[] b1, int[] b2, int[] b3, int[] b4,
                int[] r0, int[] r1, int[] r2, int[] r3, int[] r4,
                int a, int b
        ) {
            return count[a] >= count[b]
                    && b0[a] >= b0[b] && b1[a] >= b1[b] && b2[a] >= b2[b] && b3[a] >= b3[b] && b4[a] >= b4[b]
                    && r0[a] <= r0[b] && r1[a] <= r1[b] && r2[a] <= r2[b] && r3[a] <= r3[b] && r4[a] <= r4[b];
        }

        private static boolean dominatesRight(
                int[] count,
                int[] b0, int[] b1, int[] b2, int[] b3, int[] b4,
                int[] d0, int[] d1, int[] d2, int[] d3, int[] d4,
                int a, int b
        ) {
            return count[a] >= count[b]
                    && b0[a] >= b0[b] && b1[a] >= b1[b] && b2[a] >= b2[b] && b3[a] >= b3[b] && b4[a] >= b4[b]
                    && d0[a] >= d0[b] && d1[a] >= d1[b] && d2[a] >= d2[b] && d3[a] >= d3[b] && d4[a] >= d4[b];
        }

        private static void buildCountBuckets(Side side, int maxCount) {
            Arrays.fill(side.countFreq, 0, maxCount + 1, 0);
            Arrays.fill(side.maxSumByCount, 0, maxCount + 1, NO_SUM);
            Arrays.fill(side.maxB0ByCount, 0, maxCount + 1, NEG_INF);
            Arrays.fill(side.maxB1ByCount, 0, maxCount + 1, NEG_INF);
            Arrays.fill(side.maxB2ByCount, 0, maxCount + 1, NEG_INF);
            Arrays.fill(side.maxB3ByCount, 0, maxCount + 1, NEG_INF);
            Arrays.fill(side.maxB4ByCount, 0, maxCount + 1, NEG_INF);
            Arrays.fill(side.maxD0ByCount, 0, maxCount + 1, NEG_INF);
            Arrays.fill(side.maxD1ByCount, 0, maxCount + 1, NEG_INF);
            Arrays.fill(side.maxD2ByCount, 0, maxCount + 1, NEG_INF);
            Arrays.fill(side.maxD3ByCount, 0, maxCount + 1, NEG_INF);
            Arrays.fill(side.maxD4ByCount, 0, maxCount + 1, NEG_INF);

            final int fSize = side.frontierSize;
            for (int i = 0; i < fSize; i++) {
                final int idx = side.frontier[i];
                final int c = side.count[idx];
                side.countFreq[c]++;
                final int sum = side.sumBonus[idx];
                if (sum > side.maxSumByCount[c]) {
                    side.maxSumByCount[c] = sum;
                }
                final int b0 = side.b0[idx];
                final int b1 = side.b1[idx];
                final int b2 = side.b2[idx];
                final int b3 = side.b3[idx];
                final int b4 = side.b4[idx];
                if (b0 > side.maxB0ByCount[c]) side.maxB0ByCount[c] = b0;
                if (b1 > side.maxB1ByCount[c]) side.maxB1ByCount[c] = b1;
                if (b2 > side.maxB2ByCount[c]) side.maxB2ByCount[c] = b2;
                if (b3 > side.maxB3ByCount[c]) side.maxB3ByCount[c] = b3;
                if (b4 > side.maxB4ByCount[c]) side.maxB4ByCount[c] = b4;

                final int d0 = side.d0[idx];
                final int d1 = side.d1[idx];
                final int d2 = side.d2[idx];
                final int d3 = side.d3[idx];
                final int d4 = side.d4[idx];
                if (d0 > side.maxD0ByCount[c]) side.maxD0ByCount[c] = d0;
                if (d1 > side.maxD1ByCount[c]) side.maxD1ByCount[c] = d1;
                if (d2 > side.maxD2ByCount[c]) side.maxD2ByCount[c] = d2;
                if (d3 > side.maxD3ByCount[c]) side.maxD3ByCount[c] = d3;
                if (d4 > side.maxD4ByCount[c]) side.maxD4ByCount[c] = d4;
            }

            side.countStart[0] = 0;
            for (int c = 0; c <= maxCount; c++) {
                side.countStart[c + 1] = side.countStart[c] + side.countFreq[c];
                side.countWrite[c] = side.countStart[c];
            }

            for (int i = 0; i < fSize; i++) {
                final int idx = side.frontier[i];
                final int c = side.count[idx];
                side.byCount[side.countWrite[c]++] = idx;
            }
        }

        private static SolverResult mergeBestDirect(
                int n,
                int leftLen,
                Side left,
                Side right,
                int[] baseStats
        ) {
            final int base0 = baseStats[0];
            final int base1 = baseStats[1];
            final int base2 = baseStats[2];
            final int base3 = baseStats[3];
            final int base4 = baseStats[4];
            final int baseSum = base0 + base1 + base2 + base3 + base4;

            int bestMask = 0;
            int bestCount = 0;
            int bestScore = baseSum;

            final int[] lFrontier = left.frontier;
            final int leftF = left.frontierSize;
            final int[] lCount = left.count;
            final int[] lSum = left.sumBonus;
            final int[] lMask = left.mask;
            final int[] lB0 = left.b0;
            final int[] lB1 = left.b1;
            final int[] lB2 = left.b2;
            final int[] lB3 = left.b3;
            final int[] lB4 = left.b4;
            final int[] lR0 = left.r0;
            final int[] lR1 = left.r1;
            final int[] lR2 = left.r2;
            final int[] lR3 = left.r3;
            final int[] lR4 = left.r4;

            final int[] rFrontier = right.frontier;
            final int rightF = right.frontierSize;
            final int[] rCount = right.count;
            final int[] rSum = right.sumBonus;
            final int[] rMask = right.mask;
            final int[] rB0 = right.b0;
            final int[] rB1 = right.b1;
            final int[] rB2 = right.b2;
            final int[] rB3 = right.b3;
            final int[] rB4 = right.b4;
            final int[] rD0 = right.d0;
            final int[] rD1 = right.d1;
            final int[] rD2 = right.d2;
            final int[] rD3 = right.d3;
            final int[] rD4 = right.d4;

            for (int lp = 0; lp < leftF; lp++) {
                final int li = lFrontier[lp];
                final int lCnt = lCount[li];
                final int lSumVal = lSum[li];
                final int lMaskVal = lMask[li];
                final int lBonus0 = lB0[li];
                final int lBonus1 = lB1[li];
                final int lBonus2 = lB2[li];
                final int lBonus3 = lB3[li];
                final int lBonus4 = lB4[li];
                final int lReq0 = lR0[li];
                final int lReq1 = lR1[li];
                final int lReq2 = lR2[li];
                final int lReq3 = lR3[li];
                final int lReq4 = lR4[li];
                final int thrB0 = lReq0 - base0 - lBonus0;
                final int thrB1 = lReq1 - base1 - lBonus1;
                final int thrB2 = lReq2 - base2 - lBonus2;
                final int thrB3 = lReq3 - base3 - lBonus3;
                final int thrB4 = lReq4 - base4 - lBonus4;
                final int thrD0 = -base0 - lBonus0;
                final int thrD1 = -base1 - lBonus1;
                final int thrD2 = -base2 - lBonus2;
                final int thrD3 = -base3 - lBonus3;
                final int thrD4 = -base4 - lBonus4;

                for (int rp = 0; rp < rightF; rp++) {
                    final int ri = rFrontier[rp];
                    final int combinedCount = lCnt + rCount[ri];
                    if (combinedCount < bestCount) {
                        continue;
                    }
                    if (((rB0[ri] - thrB0) | (rB1[ri] - thrB1) | (rB2[ri] - thrB2) | (rB3[ri] - thrB3) | (rB4[ri] - thrB4)
                            | (rD0[ri] - thrD0) | (rD1[ri] - thrD1) | (rD2[ri] - thrD2) | (rD3[ri] - thrD3) | (rD4[ri] - thrD4)) < 0) {
                        continue;
                    }

                    final int score = baseSum + lSumVal + rSum[ri];
                    if (combinedCount == bestCount && score <= bestScore) {
                        continue;
                    }
                    bestCount = combinedCount;
                    bestScore = score;
                    bestMask = lMaskVal | (rMask[ri] << leftLen);
                }
            }

            if (n < 31) {
                bestMask &= (1 << n) - 1;
            }
            return new SolverResult(bestMask, bestCount, bestScore);
        }

        private static SolverResult mergeBestBucketed(
                int n,
                int leftLen,
                int rightLen,
                Side left,
                Side right,
                int[] baseStats
        ) {
            final int base0 = baseStats[0];
            final int base1 = baseStats[1];
            final int base2 = baseStats[2];
            final int base3 = baseStats[3];
            final int base4 = baseStats[4];
            final int baseSum = base0 + base1 + base2 + base3 + base4;

            int bestMask = 0;
            int bestCount = 0;
            int bestScore = baseSum;

            final int[] lByCount = left.byCount;
            final int[] lStart = left.countStart;
            final int[] lSum = left.sumBonus;
            final int[] lMask = left.mask;
            final int[] lB0 = left.b0;
            final int[] lB1 = left.b1;
            final int[] lB2 = left.b2;
            final int[] lB3 = left.b3;
            final int[] lB4 = left.b4;
            final int[] lR0 = left.r0;
            final int[] lR1 = left.r1;
            final int[] lR2 = left.r2;
            final int[] lR3 = left.r3;
            final int[] lR4 = left.r4;

            final int[] rByCount = right.byCount;
            final int[] rStart = right.countStart;
            final int[] rSum = right.sumBonus;
            final int[] rMask = right.mask;
            final int[] rB0 = right.b0;
            final int[] rB1 = right.b1;
            final int[] rB2 = right.b2;
            final int[] rB3 = right.b3;
            final int[] rB4 = right.b4;
            final int[] rD0 = right.d0;
            final int[] rD1 = right.d1;
            final int[] rD2 = right.d2;
            final int[] rD3 = right.d3;
            final int[] rD4 = right.d4;
            final int[] maxRSumByCount = right.maxSumByCount;
            final int[] maxRB0ByCount = right.maxB0ByCount;
            final int[] maxRB1ByCount = right.maxB1ByCount;
            final int[] maxRB2ByCount = right.maxB2ByCount;
            final int[] maxRB3ByCount = right.maxB3ByCount;
            final int[] maxRB4ByCount = right.maxB4ByCount;
            final int[] maxRD0ByCount = right.maxD0ByCount;
            final int[] maxRD1ByCount = right.maxD1ByCount;
            final int[] maxRD2ByCount = right.maxD2ByCount;
            final int[] maxRD3ByCount = right.maxD3ByCount;
            final int[] maxRD4ByCount = right.maxD4ByCount;

            for (int lc = leftLen; lc >= 0; lc--) {
                if (lc + rightLen < bestCount) {
                    break;
                }

                final int lpStart = lStart[lc];
                final int lpEnd = lStart[lc + 1];
                if (lpStart == lpEnd) {
                    continue;
                }

                for (int lp = lpStart; lp < lpEnd; lp++) {
                    final int li = lByCount[lp];

                    final int lSumVal = lSum[li];
                    final int lMaskVal = lMask[li];
                    final int lBonus0 = lB0[li];
                    final int lBonus1 = lB1[li];
                    final int lBonus2 = lB2[li];
                    final int lBonus3 = lB3[li];
                    final int lBonus4 = lB4[li];
                    final int lReq0 = lR0[li];
                    final int lReq1 = lR1[li];
                    final int lReq2 = lR2[li];
                    final int lReq3 = lR3[li];
                    final int lReq4 = lR4[li];
                    final int thrB0 = lReq0 - base0 - lBonus0;
                    final int thrB1 = lReq1 - base1 - lBonus1;
                    final int thrB2 = lReq2 - base2 - lBonus2;
                    final int thrB3 = lReq3 - base3 - lBonus3;
                    final int thrB4 = lReq4 - base4 - lBonus4;
                    final int thrD0 = -base0 - lBonus0;
                    final int thrD1 = -base1 - lBonus1;
                    final int thrD2 = -base2 - lBonus2;
                    final int thrD3 = -base3 - lBonus3;
                    final int thrD4 = -base4 - lBonus4;

                    int minRc = bestCount - lc;
                    if (minRc < 0) {
                        minRc = 0;
                    }
                    if (minRc > rightLen) {
                        continue;
                    }

                    for (int rc = rightLen; rc >= minRc; rc--) {
                        final int combinedCount = lc + rc;
                        if (combinedCount < bestCount) {
                            break;
                        }

                        final int maxRsum = maxRSumByCount[rc];
                        if (maxRsum == NO_SUM) {
                            continue;
                        }
                        if (combinedCount == bestCount && baseSum + lSumVal + maxRsum <= bestScore) {
                            continue;
                        }
                        if (maxRB0ByCount[rc] < thrB0 || maxRB1ByCount[rc] < thrB1 || maxRB2ByCount[rc] < thrB2
                                || maxRB3ByCount[rc] < thrB3 || maxRB4ByCount[rc] < thrB4
                                || maxRD0ByCount[rc] < thrD0 || maxRD1ByCount[rc] < thrD1 || maxRD2ByCount[rc] < thrD2
                                || maxRD3ByCount[rc] < thrD3 || maxRD4ByCount[rc] < thrD4) {
                            continue;
                        }

                        final int rpStart = rStart[rc];
                        final int rpEnd = rStart[rc + 1];
                        for (int rp = rpStart; rp < rpEnd; rp++) {
                            final int ri = rByCount[rp];

                            if (((rB0[ri] - thrB0) | (rB1[ri] - thrB1) | (rB2[ri] - thrB2) | (rB3[ri] - thrB3) | (rB4[ri] - thrB4)
                                    | (rD0[ri] - thrD0) | (rD1[ri] - thrD1) | (rD2[ri] - thrD2) | (rD3[ri] - thrD3) | (rD4[ri] - thrD4)) < 0) {
                                continue;
                            }

                            final int score = baseSum + lSumVal + rSum[ri];
                            if (combinedCount == bestCount && score <= bestScore) {
                                continue;
                            }
                            bestCount = combinedCount;
                            bestScore = score;
                            bestMask = lMaskVal | (rMask[ri] << leftLen);
                        }
                    }
                }
            }

            if (n < 31) {
                bestMask &= (1 << n) - 1;
            }
            return new SolverResult(bestMask, bestCount, bestScore);
        }
    }
}
