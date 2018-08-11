package org.memorydb.structure;

import org.memorydb.structure.RadixKey.KeyChanger;

/**
 * This is an implementation of a binary radix tree.
 * Beware that this implementation holds state for the current request to the tree. So this
 * object is not thread safe or usable to iterate again over the tree during another iteration.
 * But the state is relative light weight so feel free to create multiple instances of this
 * object to use on the same tree.
 *
 * Updates to the tree will need a lock not implemented by this class under a multi-thread
 * environment.
 *
 * TODO:
 * Routine to optimize this tree
 * 	  each 10 levels deep = start a new ordered set
 * 	  test performance before and after this optimization
 */

public abstract class RadixTree {
	protected RadixState state; // unique state per thread of all used radix trees there

	public RadixTree(RadixState state) { // only needs to be called once for each thread
		this.state = state;
	}

	/**
	 * Read the next values from the node n.
	 * When positive this is a record index of the database.
	 * When native this is the node index of the next node. 
	 */
	protected abstract int next(boolean bit, int n);

	protected abstract void next(boolean bit, int n, int val);

	/**
	 * This returns the bit in the key that is checked to determine if the one or the zero node
	 * should be selected. 
	 */
	protected abstract int testBit(int n);

	protected abstract void testBit(int n, int val);

	/**
	 * Start with a new node in the index, return the index of this node.
	 */
	protected abstract int addNode();

	/**
	 * Free the node with the given index.
	 */
	protected abstract void delNode(int n);

	/**
	 * Fill the specific key with the key values of the record with the given index. 
	 */
	protected abstract void setKey(int key, int index);

	/**
	 * Produce an empty copy of the current tree.
	 */
	protected abstract RadixTree copy();

	/**
	 * Return the first bit that is not equal between these keys.
	 * Return the negative bit number when the first key is lower.
	 * Return 0 if both keys are equal.
	 */
	/* package private */ int compare(int a, int b) {
		int p = 0;
		RadixKey ka = state.key(minKey() + a);
		RadixKey kb = state.key(minKey() + b);
		while (true) {
			int sA = ka.size();
			int sB = kb.size();
			if (p >= sA && p < sB)
				return -(1 + p * 8);
			if (p >= sB && p < sA)
				return 1 + p * 8;
			if (p >= sA)
				return 0;
			int ba = ka.bytes(p);
			int bb = kb.bytes(p);
			if (ba < bb)
				return -(1 + p * 8 + bits(ba, bb));
			else if (ba != bb)
				return 1 + p * 8 + bits(ba, bb);
			p++;
		}
	}

	private int bits(int a, int b) {
		for (int bit = 0; bit < 8; bit++) {
			int mask = 1 << (7 - bit);
			if ((a & mask) != (b & mask))
				return bit;
		}
		return 8;
	}

	/**
	 * Insert the record with the given index to the tree. 
	 */
	public void insert(int index) {
		setKey(minKey(), index);
		internInsert(index);
		state.stopSteps();
	}

	protected int internInsert(int index) {
		RadixKey key = state.key(minKey());
		int newNode = addNode();
		if (newNode == 0) { // top node
			state.startSteps();
			testBit(newNode, 0);
			boolean bit = key.bit(0, false);
			next(bit, newNode, index);
			next(!bit, newNode, Integer.MIN_VALUE);
			return newNode;
		}
		int curIndex = fullSearch(false);
		if (curIndex == Integer.MIN_VALUE) { // other leaf on top node
			next(key.bit(0, false), 0, index);
			return newNode;
		}
		if (curIndex < 0)
			throw new RadixException("Inconsistent index");
		setKey(minKey() + 1, curIndex);
		int bits = Math.abs(compare(0, 1)) - 1;
		int i = 0;
		while (i < state.depth() - 1 && testBit(state.step(i + 1)) < bits)
			i++;
		testBit(newNode, bits);
		next(state.stepBit(i), state.step(i), -newNode);
		boolean kb = key.bit(bits, false);
		next(kb, newNode, index);
		next(!kb, newNode, i < state.depth() - 1 ? -state.step(i + 1) : curIndex);
		return newNode;
	}

	/**
	 * Get the lowest record index corresponding with the given key fields.
	 */
	public int getIndex(Object... keys) {
		RadixKey key = state.key(minKey());
		key.combineKey(keys);
		return getInternalIndex(false);
	}

	public int getHighestIndex(Object... keys) {
		RadixKey key = state.key(minKey());
		key.combineKey(keys);
		return getInternalIndex(true);
	}

	public int getIndex(RadixKey key) {
		state.key(minKey()).copy(key);
		return getInternalIndex(false);
	}

	/**
	 * Get the lowest record index corresponding with the last set key.
	 */
	protected int getInternalIndex(boolean highest) {
		RadixKey key = state.key(minKey());
		int res = search(0, highest);
		state.stopSteps();
		if (res < 0)
			return Integer.MIN_VALUE;
		setKey(minKey() + 1, res);
		int compare = compare(0, 1);
		if (compare != 0 && -compare < key.size() * 8 + 1)
			return Integer.MIN_VALUE;
		return res;
	}

	public boolean remove(RadixKey key) {
		state.key(minKey()).copy(key);
		return remove();
	}

	/**
	 * Remove a record with the specified index from the tree.
	 */
	public boolean remove(int index) {
		setKey(minKey(), index);
		return remove();
	}

	protected boolean remove() {
		boolean res = innerRemove();
		state.stopSteps();
		return res;
	}

	protected int fullSearch(boolean highest) {
		return search(0, highest);
	}

	protected boolean innerRemove() {
		int res = fullSearch(false);
		if (res < 0)
			return false;
		setKey(minKey() + 1, res);
		if (compare(0, 1) != 0)
			return false;
		int delete = state.step(state.depth() - 1);
		int to = next(!state.stepBit(state.depth() - 1), delete); // current element is one on node to delete
		next(state.stepBit(state.depth() - 2), state.step(state.depth() - 2), to); // current node is one on parent
		delNode(delete);
		return true;
	}

	public static interface RadixCallback {
		void call(int index);
	}

	public void find(boolean singleKey, IndexOperation op, RadixCallback call, Object... keys) {
		RadixKey key = state.key(minKey());
		key.combineKey(keys);
		int till = singleKey ? key.bits() : 0;
		int val = search(0, op == IndexOperation.LE || op == IndexOperation.GT);
		if (val < 0) {
			state.stopSteps();
			return;
		}
		setKey(minKey() + 1, val);
		int compare = compare(0, 1);
		if (compare != 0 && -compare < key.size() * 8 + 1) {
			state.stopSteps();
			return;
		}
		if (op == IndexOperation.LT)
			val = previous();
		if (op == IndexOperation.GT)
			val = next();
		if ((op == IndexOperation.LT || op == IndexOperation.GT) && val >= 0 && singleKey) {
			setKey(minKey(), val);
			till = key.bits(); // current till gets invalid because we want to change to a different key here
		}
		loopValues(op, call, till, val);
		state.stopSteps();
	}

	private void loopValues(IndexOperation op, RadixCallback call, int till, int val) {
		while (val >= 0) {
			call.call(val);
			val = op == IndexOperation.LT || op == IndexOperation.LE ? previous() : next();
			if (val < 0 || (till != 0 && testBit(state.step(state.depth() - 1)) < till))
				break;
		}
	}

	/**
	 * Return the index of the lowest record corresponding with the last set key.
	 * Remember the path till the current record. 
	 */
	protected int search(int topNode, boolean highest) {
		int node = topNode;
		state.startSteps();
		RadixKey key = state.key(minKey());
		while (true) {
			state.addStep(-node);
			boolean bit = key.bit(testBit(-node), highest);
			if (bit)
				state.stepBit(true);
			node = next(bit, -node);
			if (node >= 0 || node == Integer.MIN_VALUE)
				return node;
		}
	}

	protected int first() {
		state.startSteps();
		state.addStep(0);
		int zero = next(false, 0); // top node zero
		if (zero == Integer.MIN_VALUE) {
			state.stepBit(true);
			zero = next(true, 0);
		}
		return intoFirst(zero);
	}

	private int intoFirst(int zero) {
		while (zero < 0 && zero != Integer.MIN_VALUE) {
			state.addStep(-zero);
			zero = next(false, -zero);
		}
		return zero;
	}

	protected int next() {
		while (state.depth() > 0 && state.stepBit(state.depth() - 1))
			state.upDepth();
		if (state.depth() < 0)
			return -1;
		int one = next(true, state.step(state.depth() - 1));
		if (one == Integer.MIN_VALUE)
			return -1;
		state.stepBit(true);
		if (one > 0)
			return one;
		return intoFirst(one);
	}

	protected int last() {
		state.startSteps();
		state.addStep(0);
		int one = next(true, 0); // top node zero
		if (one == Integer.MIN_VALUE)
			one = next(false, 0);
		else
			state.stepBit(true);
		return intoLast(one);
	}

	private int intoLast(int one) {
		while (one < 0 && one != Integer.MIN_VALUE) {
			state.addStep(-one);
			state.stepBit(true);
			one = next(true, -one);
		}
		return one;
	}

	protected int previous() {
		while (state.depth() > 0 && !state.stepBit(state.depth() - 1))
			state.upDepth();
		if (state.depth() <= 0)
			return -1;
		int zero = next(false, state.step(state.depth() - 1));
		if (zero == Integer.MIN_VALUE)
			return -1;
		state.stepBit(false);
		if (zero >= 0)
			return zero;
		return intoLast(zero);
	}

	/**
	 * Validate the integrity of the data structure. It counts the found records and validates
	 * that it corresponds to the given number of records.
	 */
	public void validate(int records) {
		int count = 0;
		int index = first();
		while (index >= 0) {
			int bits = -1;
			for (int d = 0; d < state.depth(); d++) {
				int ni = state.step(d);
				if (ni > 0 && (next(false, ni) == Integer.MIN_VALUE || next(true, ni) == Integer.MIN_VALUE))
					throw new RadixException("Empty leafs outside top node");
				if (testBit(ni) < bits)
					throw new RadixException("Not increasing skip bits " + ni + ":" + testBit(ni) + " < bits:" + bits);
				bits = testBit(ni);
			}
			validate(index, count++);
			index = next();
		}
		state.stopSteps();
		if (records != -1 && records != count)
			throw new RadixException("Incorrect number of records " + count + " expected " + records);
	}

	protected void validate(int n, int nr) {
		setKey(minKey(), n);
		RadixKey key = state.key(minKey());
		// read key of current element
		if (nr > 0 && compare(0, 1) < 0)
			throw new RadixException("Incorrect ordering " + n + " is lower");
		// loop steps
		int cn = 0;
		for (int d = 0; d < state.depth(); d++) {
			int sn = state.step(d);
			if (sn != cn)
				throw new RadixException("Incorrect bits ");
			cn = -next(key.bit(testBit(sn), false), sn);
		}
		if (-cn != n)
			throw new RadixException("Incorrect found record");
		setKey(minKey() + 1, n);
	}

	@SuppressWarnings("unused")
	private String showBits() {
		return state.key(minKey()).showBits() + "\n" + state.key(minKey() + 1).showBits();
	}

	@SuppressWarnings("unused")
	private String showSteps(int n) {
		StringBuilder b = new StringBuilder();
		b.append("[");
		for (int s = 0; s < state.depth(); s++) {
			if (s > 0)
				b.append(", ");
			b.append(state.stepBit(s) ? "O" : "Z");
			b.append(state.step(s));
			b.append(" ");
			b.append(testBit(state.step(s)));
		}
		b.append("]:").append(n);
		return b.toString();
	}

	/**
	 * Start a new key to search for records. There are getIndex and find functions that allow
	 * multiple key parts but those will create extra java objects that could be harmful to
	 * performance.
	 */
	public KeyChanger newKey() {
		return state.key(minKey()).newKey();
	}

	/**
	 * Default key is 0 but other implementations of RadixTree could hold their own type of key
	 */
	protected int minKey() {
		return 0;
	}
}
