package skillpoints;

public abstract class SkillpointChecker {

	public abstract boolean[] check(WynnItem[] items, int[] assignedSkillpoints);

	/** Clear any cross-call caches so the next check() starts cold. No-op by default. */
	public void clearCache() {}
}
