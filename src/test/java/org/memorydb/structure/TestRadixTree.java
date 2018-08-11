package org.memorydb.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import org.memorydb.structure.RadixKey.KeyChanger;

public class TestRadixTree {
	List<Elm> elms = new ArrayList<>();
	RadixState radixState = new RadixState();

	private static class Elm {
		private final String s;
		private final int i;

		public Elm(String s, int i) {
			this.s = s;
			this.i = i;
		}

		@Override
		public String toString() {
			return s + ":" + i;
		}

		private int compareTo(Elm other) {
			int o = s.compareTo(other.s);
			if (o != 0)
				return o;
			return Integer.compare(i, other.i);
		}
	}

	private class Tree extends BufferRadixTree {
		public Tree() {
			super(radixState);
		}

		public int combineKey(final int k, final Object... key) {
			return state.key(k).combineKey(key);
		}

		@Override
		protected int compare(int a, int b) {
			return super.compare(a, b);
		}

		@Override
		protected void setKey(int key, int index) {
			Elm elm = elms.get(index);
			try (KeyChanger newKey = state.key(key).newKey()) {
				newKey.addKeyPart(elm.s);
				newKey.addKeyPart(elm.i);
				newKey.addKeyPart(index);
			}
		}

		public Elm get(Object... keys) {
			int index = getIndex(keys);
			if (index >= 0)
				return elms.get(index);
			return null;
		}

		public Elm getHighest(Object... keys) {
			int index = getHighestIndex(keys);
			if (index >= 0)
				return elms.get(index);
			return null;
		}

		@Override
		protected RadixTree copy() {
			return new Tree();
		}
	}

	@Test
	public void testKeys() {
		try (Tree tree = new Tree()) {
			Assert.assertEquals(-1, "aa".compareTo("bb"));

			// integers
			Assert.assertEquals(4, tree.combineKey(0, 12345));
			Assert.assertEquals(4, tree.combineKey(1, 23456));
			Assert.assertEquals(-18, tree.compare(0, 1));
			Assert.assertEquals(18, tree.compare(1, 0));
			Assert.assertEquals(4, tree.combineKey(1, -12345));
			Assert.assertEquals(1, tree.compare(0, 1));
			Assert.assertEquals(-1, tree.compare(1, 0));
			Assert.assertEquals(8, tree.combineKey(1, 12345, 2));
			Assert.assertEquals(-33, tree.compare(0, 1));
			Assert.assertEquals(33, tree.compare(1, 0));

			// strings
			Assert.assertEquals(6, tree.combineKey(0, "aa"));
			Assert.assertEquals(6, tree.combineKey(1, "bb"));
			Assert.assertEquals(-15, tree.compare(0, 1));
			Assert.assertEquals(15, tree.compare(1, 0));
			Assert.assertEquals(8, tree.combineKey(1, "aab"));
			Assert.assertEquals(-42, tree.compare(0, 1));
			Assert.assertEquals(42, tree.compare(1, 0));

			// doubles
			Assert.assertEquals(8, tree.combineKey(0, -1.234));
			Assert.assertEquals(8, tree.combineKey(1, 0.012));
			Assert.assertEquals(-1, tree.compare(0, 1));
			Assert.assertEquals(1, tree.compare(1, 0));
			Assert.assertEquals(8, tree.combineKey(0, 1.234));
			Assert.assertEquals(10, tree.compare(0, 1));
			Assert.assertEquals(-10, tree.compare(1, 0));
			Assert.assertEquals(8, tree.combineKey(0, 1234.5));
			Assert.assertEquals(2, tree.compare(0, 1));
			Assert.assertEquals(-2, tree.compare(1, 0));
		}
	}

	@Test
	public void testRadix() {
		try (Tree tree = new Tree()) {
			add(tree, "one", 1);
			add(tree, "two", 2);
			add(tree, "three", 3);
			add(tree, "four", 4);
			add(tree, "five", 5);
			add(tree, "six", 6);
			add(tree, "seven", 7);
			add(tree, "eight", 8);
			add(tree, "nine", 9);
			add(tree, "ten", 10);
			Assert.assertEquals(10, tree.get("ten").i);
			Assert.assertEquals(6, tree.get("six").i);
			Assert.assertEquals(1, tree.get("one").i);
			Assert.assertEquals(null, tree.get("onne"));
			int[] count = new int[1];
			tree.find(true, IndexOperation.EQ, index -> count[0] += elms.get(index).i);
			Assert.assertEquals(55, count[0]);
			add(tree, "five", 6);
			add(tree, "five", 8);
			count[0] = 0;
			tree.find(true, IndexOperation.EQ, index -> count[0] += elms.get(index).i, "five");
			Assert.assertEquals(19, count[0]);
			Assert.assertEquals(5, tree.get("five").i);
			Assert.assertEquals(8, tree.getHighest("five").i);
			Assert.assertEquals(4, tree.getIndex("five"));
			Assert.assertEquals(11, tree.getHighestIndex("five"));
			count[0] = 0;
			tree.find(true, IndexOperation.EQ, index -> count[0] += elms.get(index).i, "five");
			Assert.assertEquals(19, count[0]);
			count[0] = 0;
			tree.find(true, IndexOperation.EQ, index -> count[0] += elms.get(index).i, "five", 7);
			Assert.assertEquals(0, count[0]);
			tree.find(true, IndexOperation.EQ, index -> count[0] += elms.get(index).i, "fivee");
			Assert.assertEquals(0, count[0]);
			tree.find(true, IndexOperation.GT, index -> count[0] += elms.get(index).i, "five");
			Assert.assertEquals(4, count[0]);
			count[0] = 0;
			tree.find(false, IndexOperation.LT, index -> count[0] += elms.get(index).i, "five");
			Assert.assertEquals(8, count[0]);
			count[0] = 0;
			tree.find(false, IndexOperation.GE, index -> count[0] += elms.get(index).i, "three");
			Assert.assertEquals(5, count[0]);
		}
	}

	@Test
	public void testDeletes() {
		try (Tree tree = new Tree()) {
			Random rand = new Random(2L);
			for (int i = 0; i < 3000; i++) {
				int index = -1;
				if (i > 100) {
					index = (int) (rand.nextDouble() * (elms.size() - 1));
					tree.remove(index);
					tree.validate(elms.size() - 1);
				}
				int val = (int) (rand.nextDouble() * 9999);
				if (index == -1)
					add(tree, Integer.toString(val), val);
				else
					change(tree, index, Integer.toString(val), val);
			}
		}
	}

	private void add(Tree tree, String string, int i) {
		int index = elms.size();
		Elm e = new Elm(string, i);
		elms.add(e);
		tree.insert(index);
		validate(tree);
	}

	private void change(Tree tree, int index, String string, int i) {
		Elm e = new Elm(string, i);
		elms.set(index, e);
		tree.insert(index);
		validate(tree);
	}

	private void validate(Tree tree) {
		if (radixState.depth() != Integer.MIN_VALUE)
			throw new RuntimeException("Not closed steps found");
		tree.validate(elms.size());
		int s = 0;
		int elm = tree.last();
		Elm prev = null;
		while (elm >= 0) {
			Elm e = elms.get(elm);
			if (prev != null && prev.compareTo(e) < 0)
				throw new RuntimeException("Incorrect previous record");
			elm = tree.previous();
			prev = e;
			s++;
		}
		radixState.stopSteps();
		if (s != elms.size())
			throw new RuntimeException("Incorrect previous records");
		if (radixState.depth() != Integer.MIN_VALUE)
			throw new RuntimeException("Not closed steps found");
	}
}
