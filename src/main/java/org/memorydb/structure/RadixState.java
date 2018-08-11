package org.memorydb.structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The combined state data for the use of radix trees.
 * It allows to iterate multiple trees at once even the same tree multiple times.
 * The task of this structure is to reuse the same claimed memory as much as possible.
 *
 * This structure should not be shared between threads but could technically be reused for
 * multiple sequential requests.
 */
public final class RadixState {
	/**
	 * The steps to the current record during the last search or during an iteration over the
	 * records. It starts a the top hand holds the nodes it encountered during the pass.
	 * A step can have stepBit() = true when the next node or the record index is from the 'one'
	 * element of the current node.
	 */
	private int steps[] = new int[50];
	private final List<RadixKey> keys;
	private int minDepth = Integer.MIN_VALUE;
	private int depth = Integer.MIN_VALUE;

	public RadixState() {
		keys = new ArrayList<>();
		keys.add(new RadixKey());
		keys.add(new RadixKey());
	}

	/**
	 * The current depth in the steps structure.
	 */
	/* package private */ int depth() {
		return depth;
	}

	/* package private */ void upDepth() {
		depth--;
	}

	/* package private */ void startSteps() {
		if (depth != Integer.MIN_VALUE) {
			steps[depth++ + minDepth] = minDepth;
			minDepth += depth;
		} else {
			if (minDepth != Integer.MIN_VALUE)
				throw new RadixException("Incorrect radix state");
			minDepth = 0;
		}
		depth = 0;
	}

	/* package private */ void stopSteps() {
		if (depth == Integer.MIN_VALUE || minDepth == Integer.MIN_VALUE)
			throw new RadixException("Stopping not started steps");
		if (minDepth > 0) {
			depth = minDepth - 1 - steps[minDepth - 1];
			minDepth = steps[minDepth - 1];
		} else {
			depth = Integer.MIN_VALUE;
			minDepth = Integer.MIN_VALUE;
		}
	}

	/* package private */ int step(int i) {
		return steps[i + minDepth] & Integer.MAX_VALUE;
	}

	/**
	 * Add a step above the current depth and increase the current depth
	 */
	/* package private */ void addStep(int val) {
		if (depth + 1 + minDepth >= steps.length)
			steps = Arrays.copyOf(steps, steps.length * 17 / 10);
		steps[depth++ + minDepth] = val;
	}

	/* package private */ boolean stepBit(int i) {
		return steps[i + minDepth] < 0;
	}

	/**
	 * Set the value of the step bit for the current step (newest / current depth)
	 */
	/* package private */ void stepBit(boolean val) {
		if (depth <= 0)
			throw new RadixException("No step yet to change");
		if (val)
			steps[depth - 1 + minDepth] |= 0x80000000;
		else
			steps[depth - 1 + minDepth] &= Integer.MAX_VALUE;
	}

	/**
	 * The defined keys. The 0 key is the working key for searches.
	 * The 1 key is used for insert to hold the current record that tests the same as the new or
	 * the last record during the validation routine.
	 */
	/* package private */ RadixKey key(int k) {
		return keys.get(k);
	}

	/* package private */ int addKey(RadixKey key1, RadixKey key2) {
		int res = -1;
		int i = 0;
		for (RadixKey k : keys) {
			if (k.equals(key1)) {
				res = i;
				break;
			}
			i++;
		}
		if (res < 0) {
			res = keys.size();
			keys.add(key1);
			keys.add(key2);
		}
		return res;
	}
}
