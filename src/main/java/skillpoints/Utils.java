package skillpoints;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Utils {
	
	// Copied from wikipedia, Heap's algorithm
	// Currently unused
	// https://en.wikipedia.org/wiki/Heap%27s_algorithm
	public static class PermutationGenerator<T> implements Iterable<List<T>> {
		private ArrayList<T> items;
		PermutationGenerator(List<T> items) { this.items = new ArrayList<T>(items); }
		
		@Override
		public Iterator<List<T>> iterator() { return new CustomIter(); }
		private class CustomIter implements Iterator<List<T>> {
			boolean done = items.size() == 0;
			int i = 1;
			int[] c = new int[items.size()];
			ArrayList<T> buffer = new ArrayList<T>(items);

			@Override
			public boolean hasNext() {
				return !done;
			}
			
			public void advance() {
				while (i < items.size()) {
					if (c[i] < i) {
						if (i % 2 == 0) {
							Collections.swap(items, 0, i);
						}
						else {
							Collections.swap(items, c[i], i);
						}
						// output
						c[i] += 1;
						i = 1;
						return;
					}
					else {
						c[i] = 0;
						i += 1;
					};
				}
				done = true;
			}

			@Override
			public List<T> next() {
				Collections.copy(buffer, items);
				advance();
				return buffer;
			}
		}
	}
}
