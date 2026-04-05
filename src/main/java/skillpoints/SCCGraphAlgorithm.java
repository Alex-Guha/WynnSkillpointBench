package skillpoints;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;


public class SCCGraphAlgorithm extends SkillpointChecker {

	static class GraphNode<T> {
		T data;
		List<GraphNode<T>> parents = new ArrayList<GraphNode<T>>();
		List<GraphNode<T>> children = new ArrayList<GraphNode<T>>();
		int nodeIndex = 0;
		boolean visited = false;    // convenience, must be reset

		public GraphNode(T data) { this.data = data; }
	}

	
	private static <T> void addEdge(GraphNode<T> from, GraphNode<T> to) {
		from.children.add(to);
		to.parents.add(from);
	}


	private static <T> void visitForward(GraphNode<T> cur, List<GraphNode<T>> res) {
		cur.visited = true;
		for (GraphNode<T> child : cur.children) {
			if (!child.visited) { visitForward(child, res); }
		}
		res.add(cur);
	}


	private static <T> void visitBackward(GraphNode<T> cur, List<GraphNode<T>> res) {
		cur.visited = true;
		for (GraphNode<T> parent : cur.parents) {
			if (!parent.visited) { visitBackward(parent, res); }
		}
		res.add(cur);
	}


	private static <T> List<List<GraphNode<T>>> computeSCCGraph(GraphNode<T> root) {
		List<GraphNode<T>> res = new ArrayList<GraphNode<T>>();
		visitForward(root, res);
		Collections.reverse(res);
		List<List<GraphNode<T>>> sccs = new ArrayList<List<GraphNode<T>>>();
		for (GraphNode<T> node : res) {
			node.visited = false;
		}
		for (GraphNode<T> node : res) {
			if (node.visited) { continue; }
			List<GraphNode<T>> scc = new ArrayList<GraphNode<T>>();
			visitBackward(node, scc);
			sccs.add(scc);
		}
		return sccs;
	}


	static class ItemSCCResult {
		final GraphNode<WynnItem> root;
		final GraphNode<WynnItem> terminal;
		final List<List<GraphNode<WynnItem>>> sccs;
		final boolean[][] negatives;
		final long elapsedNS;
		
		public ItemSCCResult(GraphNode<WynnItem> root,
							 GraphNode<WynnItem> terminal,
							 List<List<GraphNode<WynnItem>>> sccs,
							 boolean[][] negatives, long elapsedNS) {
			this.root = root;
			this.terminal = terminal;
			this.sccs = sccs;
			this.negatives = negatives;
			this.elapsedNS = elapsedNS;
		}
	}


	private static void addItemEdges(GraphNode<WynnItem> from, GraphNode<WynnItem> to) {
		for (int i = 0; i < WynnItem.NUM_SKILLPOINTS; ++i) {
			if (from.data.bonuses[i] > 0 && (
					to.data.requirements[i] > from.data.requirements[i]
					|| to.data.bonuses[i] < 0
				)
			) {
				addEdge(from, to);
				return;
			}
		}
	}


	/**
	 * Break a set of items up into subgraphs based on their skill point dependency.
	 * 
	 * @param items : List of items to process
	 * @return List of subgraphs, each one being a strongly connected component of the
	 *         full skill point dependency graph, in topological sort order.
	 */
	private static ItemSCCResult constructSCCGraph(List<WynnItem> items) {
		long l1 = System.nanoTime();

		List<GraphNode<WynnItem>> nodes = new ArrayList<GraphNode<WynnItem>>();
		GraphNode<WynnItem> root = new GraphNode<WynnItem>(null);
		root.children = nodes;
		GraphNode<WynnItem> terminal = new GraphNode<WynnItem>(null);
		terminal.parents = nodes;

		for (int i = 0; i < items.size(); ++i) {
			GraphNode<WynnItem> g = new GraphNode<WynnItem>(items.get(i));
			g.parents.add(root);
			g.children.add(terminal);
			g.nodeIndex = i;	// Save the list index
			nodes.add(g);
			for (int j = 0; j < i; ++j) {
				addItemEdges(nodes.get(j), nodes.get(i));
				addItemEdges(nodes.get(i), nodes.get(j));
			}
		}
		List<List<GraphNode<WynnItem>>> sccs = computeSCCGraph(root);
		boolean[][] negatives = new boolean[sccs.size()][];
		for (int i = 1; i < sccs.size()-1; ++i) {
			List<GraphNode<WynnItem>> scc = sccs.get(i);
			boolean[] sccNegatives = new boolean[items.size()];
			boolean[] sccMembership = new boolean[items.size()];
			itemCheck:
			for (int j = 0; j < scc.size(); ++j) {
				WynnItem item = scc.get(j).data;
				int itemIndex = scc.get(j).nodeIndex;
				sccMembership[itemIndex] = true;
				for (int k = 0; k < WynnItem.NUM_SKILLPOINTS; ++k) {
					if (item.bonuses[k] < 0) {
						sccNegatives[itemIndex] = true;
						continue itemCheck;
					}
				}
			}
			for (GraphNode<WynnItem> node : scc) {
				node.children.removeIf(n -> !sccMembership[n.nodeIndex]);
				node.parents.removeIf(n -> !sccMembership[n.nodeIndex]);
				node.visited = false;
			}
			negatives[i] = sccNegatives;
		}
		long l2 = System.nanoTime();
		return new ItemSCCResult(root, terminal, sccs, negatives, l2 - l1);
	}

	// Generate all possible full-length "DFS walks" of a graph.
	static class GraphPermutationGenerator<T> implements Iterable<List<GraphNode<T>>> {
		private List<GraphNode<T>> nodes;
		private GraphNode<T> root;

		GraphPermutationGenerator(List<GraphNode<T>> graph) {
			this.nodes = graph;
			this.root = new GraphNode<T>(null);
			this.root.children = nodes;
		}
		@Override
		public Iterator<List<GraphNode<T>>> iterator() { return new CustomIter(); }
		private class CustomIter implements Iterator<List<GraphNode<T>>> {
			boolean done = nodes.size() == 0;
			private int[] pointers = new int[nodes.size()];
			private ArrayList<GraphNode<T>> stack = new ArrayList<GraphNode<T>>();

			CustomIter() {
				GraphNode<T> n = root;
				// Not your average for loop!
				stack.add(n);
				advanceHelper(n, 0);
			}
			
			@Override
			public boolean hasNext() {
				return !done;
			}
			
			public void advanceHelper(GraphNode<T> n, int i) {
				n = stack.get(i);
				addNodeLoop:
				while (stack.size() <= nodes.size()) {
					for (int j = pointers[i]; j < n.children.size(); ++j) {
						// visited check
						if (n.children.get(j).visited) { continue; }
						pointers[i] = j+1; // Next pointer
						n = n.children.get(j);
						n.visited = true;
						i += 1;
						stack.add(n);
						continue addNodeLoop;
					}
					// No children were valid. Backtrack
					pointers[i] = 0;
					n.visited = false;
					stack.remove(stack.size() - 1);
					i -= 1;
					
					if (i < 0) {
						done = true;
						return;
					}

					n = stack.get(i);
				}
			}

			public void advance() {
				int i = nodes.size() - 1;
				GraphNode<T> n = stack.remove(stack.size() - 1);
				n.visited = false;
				
				// Optimize: Unrolled first iteration -- always happens
				n = stack.remove(stack.size() - 1);
				pointers[i] = 0;
				n.visited = false;
				i -= 1;
				if (i < 0) {
					done = true;
					return;
				}
				advanceHelper(n, i);
			}

			@Override
			public List<GraphNode<T>> next() {
				ArrayList<GraphNode<T>> buffer = new ArrayList<GraphNode<T>>();
				for (int i = 1; i < stack.size(); ++i) {
					buffer.add(stack.get(i));
				}
				advance();
				return buffer;
			}
		}
	}

	static class OptimizationContext {
		int bestViolations;
		int bestTotal;
		int[] skillpointReal;
		int[] bestOrder;
		boolean[] bestValid;
		int evals = 0;
		List<WynnItem> items;

		OptimizationContext(int[] initialSkillpoints, List<WynnItem> items) {
			this.skillpointReal = initialSkillpoints;
			this.items = items;
			this.bestViolations = items.size() + 1;
			this.bestValid = new boolean[items.size()];
		}
	}

	private void equipItem(WynnItem item, boolean negative,
			int[] skillpointApplied, int[] skillpointRequired, int[] skillpointMin,
			int[] order, int equipIndex, int itemIndex) {

		for (int i = 0; i < WynnItem.NUM_SKILLPOINTS; ++i) {
			skillpointRequired[i] = Math.max(
					skillpointRequired[i], item.requirements[i] - skillpointApplied[i]
			);

			// Update sp floor, for popping off calcs
			if (item.requirements[i] != 0) {
				skillpointMin[i] = Math.max(
					skillpointMin[i], item.requirements[i] + item.bonuses[i]
				);
			}
		}
		item.apply(skillpointApplied);
		if (negative) {
			for (int i = 0; i < WynnItem.NUM_SKILLPOINTS; ++i) {
				if (skillpointMin[i] != 0) {
					// Apply missing sp to fix negatives
					// at the end, localMin <= localRequired + localApplied
					skillpointRequired[i] = Math.max(
							skillpointRequired[i], skillpointMin[i] - skillpointApplied[i]
					);
				}
			}
		}
		order[equipIndex] = itemIndex;
	}
	
	private boolean permuteCheck(ItemSCCResult groupData, int index,
			int[] skillpointApplied, int[] skillpointRequired, int[] skillpointMin,
			int[] order, int equipIndex, OptimizationContext ctx) {

		// Last SCC is the terminal
		if (index == groupData.sccs.size() - 1) {
			ctx.evals += 1;
			int violations = 0;
			int[] skillpoints = ctx.skillpointReal.clone();
			int[] localMin = new int[WynnItem.NUM_SKILLPOINTS];
			boolean[] valid = new boolean[order.length];
			applyLoop:
			for (int itemIndex : order) {
				WynnItem item = ctx.items.get(itemIndex);
				if (!item.canEquip(skillpoints)) {
					violations += 1;
					continue;
				}
				for (int i = 0; i < WynnItem.NUM_SKILLPOINTS; ++i) {
					if (item.bonuses[i] + skillpoints[i] < localMin[i]) {
						// Pop negative items... if they would force earlier items to pop :sob:
						continue applyLoop;
					}
				}
				valid[itemIndex] = true;
				for (int i = 0; i < WynnItem.NUM_SKILLPOINTS; ++i) {
					// Update sp floor, for popping off calcs
					if (item.requirements[i] != 0) {
						localMin[i] = Math.max(
								localMin[i], item.requirements[i] + item.bonuses[i]
						);
					}
				}
				item.apply(skillpoints);
			}
			int score = IntStream.of(skillpoints).sum();
			if (violations < ctx.bestViolations ||
					(violations == ctx.bestViolations && score > ctx.bestTotal)) {
				ctx.bestOrder = order;
				ctx.bestTotal = score;
				ctx.bestViolations = violations;
				ctx.bestValid = valid;
			}
			return violations == 0;
		}
		List<GraphNode<WynnItem>> scc = groupData.sccs.get(index);

		// TODO: can we do wynncraft's "fast application" for positive-only SCCs?
		// I don't think so... since later steps may want different skews
		if (scc.size() == 1) {
			int itemIndex = scc.get(0).nodeIndex;
			boolean negative = groupData.negatives[index][itemIndex];

			equipItem(scc.get(0).data, negative,
					skillpointApplied, skillpointRequired, skillpointMin,
					order, equipIndex, itemIndex);
			return permuteCheck(groupData, index + 1, skillpointApplied, skillpointRequired, skillpointMin,
					order, equipIndex + 1, ctx);
		}

		for (List<GraphNode<WynnItem>> perm : new GraphPermutationGenerator<WynnItem>(scc)) {
			int[] localRequired = skillpointRequired.clone();
			int[] localApplied = skillpointApplied.clone();
			int[] localMin = skillpointMin.clone();
			int newIndex = equipIndex;
			for (GraphNode<WynnItem> n : perm) {
				int itemIndex = n.nodeIndex;
				boolean negative = groupData.negatives[index][itemIndex];
				// order is always mutated (set before recursive call), this is safe
				equipItem(n.data, negative,
						localApplied, localRequired, localMin,
						order, newIndex, itemIndex);
				newIndex += 1;
				// TODO: ANY level of pruning at all LOL
			}
			if (permuteCheck(groupData, index + 1, localApplied, localRequired, localMin,
					order, equipIndex + scc.size(), ctx)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean[] check(WynnItem[] items, int[] assignedSkillpoints) {
		// TODO: Filter items
		List<WynnItem> toCheck = new ArrayList<WynnItem>();
		List<Integer> indices = new ArrayList<Integer>();
		for (int i = 0; i < items.length; ++i) {
			WynnItem item = items[i];
			toCheck.add(item);
			indices.add(i);
		}
		
		ItemSCCResult res = constructSCCGraph(toCheck);
		OptimizationContext ctx = new OptimizationContext(assignedSkillpoints, toCheck);
//		System.out.println("SCC stats:");
//		for (List<GraphNode<WynnItem>> scc : res.sccs) {
//			System.out.println(scc.size());
//		}
		permuteCheck(res, 1, new int[5], new int[5], new int[5], new int[toCheck.size()], 0, ctx);
//		System.out.println(ctx.evals + " evals.");
		System.out.println("SCC Graph construction took " + res.elapsedNS / 1000000.0 + " ms");
		if (ctx.bestOrder == null) {
			return new boolean[items.length];
		}
//		for (int i : ctx.bestOrder) {
//			System.out.println(i);
//		}
		boolean[] valid = new boolean[items.length];
		for (int i = 0; i < indices.size(); ++i) {
			valid[indices.get(i)] = ctx.bestValid[i];
		}
		return valid;
	}	
}
