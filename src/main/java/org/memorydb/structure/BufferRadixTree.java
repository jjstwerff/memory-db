package org.memorydb.structure;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

public abstract class BufferRadixTree extends RadixTree implements AutoCloseable {
	private static final long NODE_SIZE = 10l;
	private static Unsafe unsafe;
	int maxNode;
	private long addr;
	private int size;
	private int del;

	static {
		unsafe = readUnsafe();
	}

	private static Unsafe readUnsafe() {
		try {
			Field singleoneInstanceField = Unsafe.class.getDeclaredField("theUnsafe");
			singleoneInstanceField.setAccessible(true);
			return (Unsafe) singleoneInstanceField.get(null);
		} catch (Exception e) {
			throw new InputOutputException(e);
		}
	}

	/* package private */ Unsafe getUnsafe() {
		return unsafe;
	}

	/**
	 * This is only a fall-back function when an unclosed BufferRadixTree is finalized by the garbage collector 
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}

	public BufferRadixTree(RadixState state) {
		super(state);
		maxNode = 0;
		size = 10; // was 1000
		del = -1;
		addr = unsafe.allocateMemory(size * NODE_SIZE);
	}

	@Override
	protected int next(boolean bit, int n) {
		test(n);
		return unsafe.getInt(addr + n * NODE_SIZE + (bit ? 4 : 0));
	}

	@Override
	protected void next(boolean bit, int n, int val) {
		test(n);
		unsafe.putInt(addr + n * NODE_SIZE + (bit ? 4 : 0), val);
	}

	@Override
	protected int testBit(int n) {
		test(n);
		return unsafe.getShort(addr + n * NODE_SIZE + 8);
	}

	@Override
	protected void testBit(int n, int val) {
		test(n);
		unsafe.putShort(addr + n * NODE_SIZE + 8, (short) val);
	}

	private void test(int n) {
		if (n < 0 || n >= size)
			throw new RadixException("Node " + n + " out bounds");
	}

	@Override
	protected int addNode() {
		if (maxNode == -1)
			throw new RadixException("Already closed");
		if (del >= 0) {
			int res = del;
			del = next(true, del);
			testBit(res, 0);
			next(true, res, 0);
			next(false, res, 0);
			return res;
		}
		if (maxNode >= size) {
			size = size * 17 / 10;
			addr = unsafe.reallocateMemory(addr, size * NODE_SIZE);
		}
		return maxNode++;
	}

	@Override
	protected void delNode(int n) {
		testBit(n, -1);
		next(true, n, del);
		next(false, n, -1);
		del = n;
	}

	public String showNodes() {
		StringBuilder bld = new StringBuilder();
		for (int n = 0; n < maxNode; n++) {
			bld.append(n).append(" t:");
			bld.append(testBit(n)).append(" z:");
			int zero = next(false, n);
			if (zero >= 0) {
				setKey(minKey(), zero);
				bld.append(zero).append(state.key(minKey()));
			} else {
				bld.append(-zero);
			}
			bld.append(" o:");
			int one = next(true, n);
			if (one >= 0) {
				setKey(minKey(), one);
				bld.append(one).append(state.key(minKey()));
			} else {
				bld.append(-one);
			}
			bld.append("\n");
		}
		return bld.toString();
	}

	@Override
	public void close() {
		if (addr != -1)
			try {
				unsafe.freeMemory(addr);
			} catch (Exception e) {
				// should not happen when used in sane environment
				throw new InputOutputException(e);
			}
		addr = -1;
		size = -1;
		maxNode = -1;
		del = -1;
	}
}
