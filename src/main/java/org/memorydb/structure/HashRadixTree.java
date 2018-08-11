package org.memorydb.structure;

import java.util.Set;
import java.util.TreeSet;

import org.memorydb.structure.RadixKey.KeyPart;

public abstract class HashRadixTree extends BufferRadixTree {
	private static final long HASH_SIZE = 4l;
	private long tableAddr;
	/* package private */ int hashSize;
	/* package private */ int directCompareFields;
	private final int startKey;

	public HashRadixTree(RadixState state, int hashSize, int directCompareFields) {
		super(state);
		this.hashSize = hashSize;
		tableAddr = getUnsafe().allocateMemory(hashSize * HASH_SIZE);
		getUnsafe().setMemory(tableAddr, hashSize * HASH_SIZE, (byte) 0);
		this.directCompareFields = directCompareFields;
		startKey = state.addKey(new HashRadixKey(this), new HashRadixKey(this));
	}

	@Override
	protected int minKey() {
		return startKey;
	}

	private int getHashPtr(int val) {
		if (val < 0 || val >= hashSize)
			throw new RadixException("Hash table corruption");
		return getUnsafe().getInt(tableAddr + val * HASH_SIZE);
	}

	private void setHashPtr(int val, int node) {
		if (val < 0 || val >= hashSize)
			throw new RadixException("Hash table corruption");
		getUnsafe().putInt(tableAddr + val * HASH_SIZE, node);
	}

	@Override
	public void insert(int index) {
		setKey(minKey(), index);
		super.internInsert(index);
		state.stopSteps();
		fixLowestNode();
	}

	private void fixLowestNode() {
		int node = fullSearch(false);
		for (int d = 0; d < state.depth(); d++)
			if (testBit(state.step(d)) >= 32) {
				node = -state.step(d);
				break;
			}
		setHashPtr((int) state.key(minKey()).parts.get(0).lval, node);
		state.stopSteps();
	}

	@Override
	protected boolean remove() {
		boolean res = super.innerRemove();
		state.stopSteps();
		if (res)
			fixLowestNode();
		return res;
	}

	@Override
	protected int fullSearch(boolean highest) {
		return super.search(0, highest); // prevent calling own search
	}

	@Override
	protected int search(int top, boolean highest) {
		HashRadixKey key = (HashRadixKey) state.key(minKey());
		int startNode = getHashPtr((int) key.parts.get(0).lval);
		if (startNode > 0) {
			state.startSteps();
			return startNode;
		}
		return super.search(startNode, highest);
	}

	@Override
	public void close() {
		super.close();
		if (tableAddr != -1)
			try {
				getUnsafe().freeMemory(tableAddr);
			} catch (Exception e) {
				// should not happen when used in sane environment
				throw new InputOutputException(e);
			}
		tableAddr = -1;
	}

	/**
	 * This is only a fall-back function for when a not closed object is finalized
	 */
	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}

	@Override
	public String showNodes() {
		return "hash:" + hash() + "\n" + super.showNodes();
	}

	/* package private */ String hash() {
		StringBuilder bld = new StringBuilder();
		for (int h = 0; h < hashSize; h++) {
			if (h > 0)
				bld.append(", ");
			int p = getHashPtr(h);
			bld.append(h).append(":");
			if (p < 0) {
				bld.append("n:").append(-p);
			} else if (p > 0) {
				bld.append("i:").append(p);
			}
		}
		return bld.toString();
	}

	@Override
	protected void validate(int n, int nr) {
		setKey(minKey(), n);
		RadixKey key = state.key(minKey());
		int idx = getInternalIndex(false);
		if (n != idx)
			throw new RadixException("Searching the value: " + n + ":" + key + " returned another one: " + idx);
	}

	public int uniqueKeys(int records) {
		Set<String> keys = new TreeSet<>();
		HashRadixKey key = (HashRadixKey) state.key(minKey());
		StringBuilder bld = new StringBuilder();
		for (int index = 0; index < records; index++) {
			bld.setLength(0);
			setKey(minKey(), index);
			for (int p = 0; p < directCompareFields; p++) {
				KeyPart keyPart = key.parts.get(p + 1);
				bld.append(keyPart.sval != null ? keyPart.sval : keyPart.lval).append("|");
			}
			keys.add(bld.toString());
		}
		return keys.size();
	}

	public HashRadixTree optimize() {
		HashRadixTree res = (HashRadixTree) copy();
		int index = first();
		while (index >= 0) {
			res.insert(index);
			index = next();
		}
		state.stopSteps();
		return res;
	}
}
