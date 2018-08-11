package org.memorydb.structure;

import java.util.Iterator;
import java.util.Random;

public class IntSet {
	private final Hash index;

	public IntSet() {
		index = new Hash(new Random());
	}

	public IntSet(Random rand) {
		index = new Hash(rand);
	}

	public boolean contains(int val) {
		return index.get(val) >= 0;
	}

	public void add(int val) {
		int i = index.get(val);
		if (i < 0)
			index.put(val);
	}

	private static class Hash extends IndexHash<Integer> {
		public Hash(Random rand) {
			super(IndexHash.DEFAULT_INITIAL_CAPACITY, IndexHash.DEFAULT_LOAD_FACTOR, rand);
		}

		@Override
		protected int getHash(int idx) {
			return idx;
		}

		@Override
		protected boolean equalsKey(int pos, Integer key) {
			return key.equals(pos);
		}

		@Override
		protected int getHash(Integer key) {
			return key;
		}
	}

	public void clear() {
		index.clear();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		Iterator<Integer> iterator = index.iterator();
		builder.append("{");
		while (iterator.hasNext()) {
			if (first)
				first = false;
			else
				builder.append(", ");
			builder.append(iterator.next());
		}
		builder.append("}");
		return builder.toString();
	}

	public int size() {
		return index.size();
	}

	public void remove(int val) {
		index.del(val);
	}
}
