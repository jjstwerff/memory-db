package org.memorydb.handler;

import java.util.Arrays;

public class IntArray {
	private int[] array = new int[30];
	private int size = 0;

	public void add(int val) {
		if (size >= array.length)
			array = Arrays.copyOf(array, array.length * 2);
		array[size++] = val;
	}

	public int get(int pos) {
		if (pos < 0 || pos >= size)
			return Integer.MIN_VALUE;
		return array[pos];
	}

	public void put(int pos, int val) {
		if (pos < 0 || pos >= size)
			return;
		array[pos] = val;
	}

	@Override
	public String toString() {
		StringBuilder bld = new StringBuilder();
		bld.append("[");
		for (int v : array) {
			if (bld.length() > 1)
				bld.append(", ");
			bld.append(v);
		}
		bld.append("]");
		return bld.toString();
	}

	public int size() {
		return size;
	}

	public void trim(int toSize) {
		if (toSize > size || toSize < 0)
			return;
		size = toSize;
	}

	public boolean isEmpty() {
		return size == 0;
	}
}
