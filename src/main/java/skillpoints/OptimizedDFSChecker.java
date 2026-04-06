package skillpoints;

import java.util.List;

/**
 * Adapter that wraps {@link OptimizedDFS#solve} into the {@link SkillpointChecker} interface.
 */
public class OptimizedDFSChecker extends SkillpointChecker {

    @Override
    public boolean[] check(WynnItem[] items, int[] assignedSkillpoints) {
        int[] order = OptimizedDFS.solve(
                List.of(items), assignedSkillpoints.clone());

        boolean[] equipped = new boolean[items.length];
        for (int idx : order) {
            equipped[idx] = true;
        }
        return equipped;
    }
}
