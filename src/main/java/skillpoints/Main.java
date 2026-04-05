package skillpoints;

public class Main {
	public static void main(String[] args) {
		WynnItem[] items0 = new WynnItem[]{
			new WynnItem(new int[]{ 50,  0,  0, 40,  0},	// earth breaker
					     new int[]{  9,  0,  0,  8,  0}),
			new WynnItem(new int[]{ 75,  0,  0,  0,  0},	// Granitic Mettle
				         new int[]{  0,  0,  0, 15,  0}),
			new WynnItem(new int[]{ 50,  0,  0,  0,  0},	// Shatterglass
			             new int[]{  7,  0,  0, -3,  0}),
		};
		WynnItem[] items = new WynnItem[]{
		new WynnItem(new int[]{ 40,  0,  0, 40, 40},	// Valhalla
				     new int[]{  9,  0,  0,  9,  9}),
		new WynnItem(new int[]{  0, 15,  0,  0, 50},	// Dark Shroud
				     new int[]{  0, 15,  0,  0, 25}),
		new WynnItem(new int[]{ 30, 30, 30, 30, 30},	// Far Cosmos
			     	 new int[]{  8,  8,  8,  8,  8}),
		new WynnItem(new int[]{ 40, 70,  0,  0,  0},	// Brainwash
			         new int[]{ 13,  0,-50,  0,  0}),
		new WynnItem(new int[]{ 25,  0,  0,  0,  0},	// Giant's Ring
	                 new int[]{  5,  0,  0,  0, -3}),
		new WynnItem(new int[]{ 25, 25, 25, 25, 25},	// Prism
                     new int[]{  3,  3,  3,  3,  3}),
		new WynnItem(new int[]{  0,  0,  0,  0,  0},	// Prowess
                     new int[]{  4,  4,  4,  4,  4}),
		new WynnItem(new int[]{ 50,  0,  0,  0,  0},	// Shatterglass
		             new int[]{  7,  0,  0, -3,  0}),
		};
		int[] skillpoints = new int[] {21, 40, 73, 28, 29};

//		SkillpointChecker solver = new WynnAlgorithm();
		SkillpointChecker solver = new SCCGraphAlgorithm();
		solver.check(items0, skillpoints);
		long l1 = System.nanoTime();
		boolean[] result = solver.check(items, skillpoints);
		long l2 = System.nanoTime();
		System.out.println("Solve took " + ((l2-l1) / 1e6) + " ms");
		for (boolean b : result) {
			System.out.println(b);
		}
	}
}
