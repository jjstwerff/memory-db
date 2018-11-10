package org.memorydb.structure;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import org.memorydb.handler.CorruptionException;
import org.memorydb.handler.MutationException;

/*
 * Future feature: Unique key check
 */
/**
 * This class contains the algorithms for data structures representing a Red Black Tree.
 * This class contains no actual data itself, only a few bookkeeping fields.
 * A concrete subclass must supply the underlying data structure, which can be referenced to with integer pointers.
 */
/*
 * The data structures contain pointers to the left and right nodes. Those pointers can be negative or positive.
 * Positive pointers just point to the given node, but negative pointers are placeholders to the first
 * ancestor in this direction. This means the tree can be efficiently iterated, because going to the next
 * higher element means either:
 * - When the node has a right child, take the lowest element of the right child.
 * - When the node has no right child, take the first higher ancestor, which is encoded as a negative position pointer.
 */
public abstract class RedBlackTree {
	private static final String TOO_MANY_ITERATIONS = "Too many iterations";

	// First node that needs to be inspected for double reds.
	private int repair1;
	// Second node that needs to be inspected for double reds.
	private int repair2;
	// Working data for verify.
	private final int[] measures = new int[2];
	// maximum number of iterations into the tree
	private static final int MAX_DEPTH = 50;

	protected abstract boolean readRed(int rec);

	protected abstract int readLeft(int rec);

	protected abstract int readRight(int rec);

	protected abstract int readTop();

	protected abstract void changeRed(int rec, boolean red);

	protected abstract void changeLeft(int rec, int left);

	protected abstract void changeRight(int rec, int right);

	protected abstract void changeTop(int top);

	protected abstract int compareTo(int a, int b);

	public static int compare(String a, String b) {
		if (a == null && b == null)
			return 0;
		if (a == null)
			return -1;
		if (b == null)
			return 1;
		return a.compareTo(b);
	}

	public static int compare(LocalDateTime a, LocalDateTime b) {
		if (a == null && b == null)
			return 0;
		if (a == null)
			return -1;
		if (b == null)
			return 1;
		return a.compareTo(b);
	}

	public static int compare(Long a, Long b) {
		if (a == null && b == null)
			return 0;
		if (a == null)
			return -1;
		if (b == null)
			return 1;
		return a.compareTo(b);
	}

	public static int compare(Integer a, Integer b) {
		if (a == null && b == null)
			return 0;
		if (a == null)
			return -1;
		if (b == null)
			return 1;
		return a.compareTo(b);
	}

	/**
	 * Insert into the tree.
	 */
	public void insert(int pos) {
		if (pos == 0)
			return;
		changeRed(pos, false);
		changeLeft(pos, 0);
		changeRight(pos, 0);
		if (readTop() == 0)
			changeTop(pos);
		else
			changeTop(put(0, readTop(), pos, 0, 0));
		changeRed(readTop(), false);
	}

	// For balance explanation/visualisation see http://www.cs.cornell.edu/courses/cs3110/2009sp/lectures/lec11.html

	/**
	 * Check if the work node is black and if it has a red child with a red grandchild.
	 * In that case return the top position of a reordered tree without these double red nodes.
	 * 
	 * @return The pointer to the new head after rebalancing.
	 */
	private int balance(int work) {
		if (readRed(work))
			return work;
		if (readLeft(work) > 0) {
			int work2 = readLeft(work);
			int work3 = readLeft(work2);
			if (readRed(work2) && work3 > 0 && readRed(work3))
				return leftLeft(work, work2, work3);
			work3 = readRight(work2);
			if (readRed(work2) && work3 > 0 && readRed(work3))
				return leftRight(work, work2, work3);
		}
		if (readRight(work) > 0) {
			int work2 = readRight(work);
			int work3 = readLeft(work2);
			if (readRed(work2) && work3 > 0 && readRed(work3))
				return rightLeft(work, work2, work3);
			work3 = readRight(work2);
			if (readRed(work2) && work3 > 0 && readRed(work3))
				return rightRight(work, work2, work3);
		}
		return work;
	}

	/* relative to the work Node */
	private int leftLeft(int item, int work2, int work3) {
		// Black, item, Node (Red, work2, Node (Red, work3, a, b), c), d -> Red, work2, Node (Black, work3, a, b), Node (Black, item, c, d)
		int w2right = readRight(work2);
		if (w2right < 0)
			w2right = -work2;
		changeRight(work2, item);
		changeRed(work3, false);
		changeLeft(item, w2right);
		return work2;
	}

	/* relative to the work Node */
	private int leftRight(int item, int work2, int work3) {
		// Black, item, Node (Red, work2, a, Node (Red, work3, b, c)), d -> Red, work3, Node (Black, work2, a, b), Node (Black, item, c, d)
		int w3left = readLeft(work3);
		if (w3left < 0)
			w3left = -work3;
		int w3right = readRight(work3);
		if (w3right < 0)
			w3right = -work3;
		changeLeft(work3, work2);
		changeRight(work3, item);
		changeRed(work2, false);
		changeRight(work2, w3left);
		changeLeft(item, w3right);
		return work3;
	}

	/* relative to the work Node */
	private int rightLeft(int item, int work2, int work3) {
		// Black, item, a, Node (Red, work2, Node (Red, work3, b, c), d) -> Red, work3, Node (Black, item, a, b), Node (Black, work2, c, d)
		int w3left = readLeft(work3);
		if (w3left < 0)
			w3left = -work3;
		int w3right = readRight(work3);
		if (w3right < 0)
			w3right = -work3;
		changeLeft(work3, item);
		changeRight(work3, work2);
		changeRight(item, w3left);
		changeRed(work2, false);
		changeLeft(work2, w3right);
		return work3;
	}

	/* relative to the work Node */
	private int rightRight(int item, int work2, int work3) {
		// Black, item, a, Node (Red, work2, b, Node (Red, work3, c, d)) -> Red, work2, Node (Black, item, a, b), Node (Black, work3, c, d)
		int w2left = readLeft(work2);
		if (w2left < 0)
			w2left = -work2;
		changeLeft(work2, item);
		changeRight(item, w2left);
		changeRed(work3, false);
		return work2;
	}

	/**
	 * Inserts the item into a subtree.
	 * @param depth The current recursion depth. Is limited by MAX_DEPTH to prevent infinite loops due to data corruption.
	 * @param hpos The subtree head pointer.
	 * @param rec The item to insert.
	 * @param leftPos The pointer to the first left-side ancestor, or 0 if none exists.
	 * @param rightPos The pointer to the first right-side ancestor, or 0 if none exists.
	 * @return The pointer to the new head after re-balancing.
	 */
	private int put(int depth, int hpos, int rec, int leftPos, int rightPos) {
		if (depth > MAX_DEPTH)
			throw new CorruptionException(TOO_MANY_ITERATIONS);
		if (hpos <= 0) {
			changeRed(rec, true);
			changeLeft(rec, -leftPos);
			changeRight(rec, -rightPos);
			return rec;
		}
		if (hpos == rec)
			throw new MutationException("Duplicate record");
		int compareTo = compareTo(rec, hpos);
		if (compareTo == 0)
			throw new MutationException("Duplicate key");
		if (compareTo < 0) {
			int left = put(depth + 1, readLeft(hpos), rec, leftPos, hpos);
			changeLeft(hpos, left);
		} else if (compareTo > 0) {
			int right = put(depth + 1, readRight(hpos), rec, hpos, rightPos);
			changeRight(hpos, right);
		}
		return balance(hpos);
	}

	/**
	 * Remove the itemPos Node from the tree.
	 *
	 * @throws IllegalArgumentException If the requested itemPos was not found in the tree.
	 */
	public void remove(int itemPos) {
		boolean[] black = new boolean[1];
		int top = remove(itemPos, 0, readTop(), black);
		if (top == Integer.MIN_VALUE)
			top = 0;
		changeTop(top);
		if (readTop() != 0)
			changeRed(readTop(), false);
	}

	/**
	 * Search and remove the current node from a subtree.
	 * <p>
	 * Afterwards, the black-height might be reduced by 1, and the new head might have been changed from black to red.
	 * <p>
	 * If current is the current node (<code>hpos</code>) and has no children, no Node is modified and {@link Integer#MIN_VALUE} is returned.<br/>
	 *
	 * @param depth The current recursion depth. Is limited by MAX_DEPTH to prevent infinite loops due to data corruption.
	 * @param hpos The subtree head pointer.
	 * @param black Mutable boolean stating whether the black-height has been reduced by 1.
	 * @return The pointer to the new head after rebalancing, or {@link Integer#MIN_VALUE} if there is no subtree left.
	 * @throws IllegalArgumentException If the requested {@link Node} was not found in the tree.
	 */
	private int remove(int current, int depth, int hpos, boolean[] black) {
		if (depth > MAX_DEPTH)
			throw new CorruptionException(TOO_MANY_ITERATIONS);
		if (hpos <= 0)
			throw new IllegalArgumentException("Item not found");
		int compareTo = compareTo(current, hpos);
		if (compareTo < 0) {
			int left = remove(current, depth + 1, readLeft(hpos), black);
			if (left == Integer.MIN_VALUE)
				// The left subtree only contained the one value with its left child pointer to the first left ancestor.
				left = readLeft(current);
			changeLeft(hpos, left);
		} else if (compareTo > 0) {
			int right = remove(current, depth + 1, readRight(hpos), black);
			if (right == Integer.MIN_VALUE)
				// The right subtree only contained the one value with its right child pointer to the first right ancestor.
				right = readRight(current);
			changeRight(hpos, right);
		} else {
			hpos = removeCurrent(hpos, depth, black);
			// There are 3 possibilities:
			// 1. 'current' had 2 children, in which case a node was removed from its left subtree (and moved into 'item's position)
			//    => 'compareTo' must be -1
			// 2. 'current' had only one child, in which case that child's black height must have been 0, which means the child
			//    itself is red and has no further children => 'compareTo' doesn't matter.
			// 3. 'current' had no children, in which case hpos = Integer.MIN_VALUE  => 'compareTo' doesn't matter.

			if (hpos <= 0)
				// Possibility 3
				return hpos;

			if (readLeft(hpos) <= 0 && readRight(hpos) <= 0) {
				// Possibility 2
				// We have replaced a black node with a red node. Change the node black again.
				if (!black[0])
					throw new CorruptionException("Cannot change node to black twice in remove()");
				if (!readRed(hpos))
					throw new CorruptionException("Child of single-child node should be red");
				changeRed(hpos, false);
				black[0] = false;
				return hpos;
			}
			// Possibility 1
			compareTo = -1;
		}
		// Depending on black[0], we might have a black-height imbalance.
		if (black[0])
			hpos = repairBlackImbalance(hpos, black, compareTo);
		return hpos;
	}

	/**
	 * Removes {@link #work}.
	 * <p>
	 * If {@link #work} has two children, the result is guaranteed to be a real pointer. Either its left subtree has its black-height reduced by one and <code>black[0]</code> is true, or its black-height has remained the same and <code>black[0]</code> is false.
	 * The new head will have the same color as {@link #work}.<br/>
	 * If {@link #work} has one child, that child is returned. This means that child must have a black-height of 0, so must be red with no children. This in turn means {@link #work} was black, so <code>black[0]</code> will be true.<br/>
	 * If {@link #work} has no children, {@link Integer#MIN_VALUE} is returned.
	 * <p>
	 * If {@link #work} has two children, modifies {@link #current}, {@link #work}, {@link #work2}, {@link #work3}, {@link #repair1}, {@link #repair2}.<br/>
	 * If {@link #work} has one child, {@link #work} is modified.<br/>
	 * If {@link #work} has no child, no {@link Node} is modified.
	 * 
	 * @param depth The current recursion depth. Is limited by MAX_DEPTH to prevent infinite loops due to data corruption.
	 * @param black Mutable boolean returning whether the black height of this subtree or its left subtree was reduced.
	 * @return The head of the new subtree, or {@link Integer#MIN_VALUE} if the subtree is empty.
	 */
	private int removeCurrent(int rec, int depth, boolean[] black) {
		// node == work, which can be modified by later calls. So store all relevant information now.
		int left = readLeft(rec);
		int right = readRight(rec);
		boolean red = readRed(rec);
		// If both left and right are empty, just delete this node and return Integer.MIN_VALUE.
		// If left or right is empty, delete this node and return the other.
		if (left <= 0)
			return removeLeft(black, left, right, red);
		if (right <= 0)
			return removeRight(black, left, right, red);

		// Neither left nor right is empty. Take the previous value, which must be in the left subtree
		// and recursively delete it and move it to the current position.
		int ipos = max(left);
		int newLeft = remove(ipos, depth + 1, left, black);
		if (newLeft == Integer.MIN_VALUE)
			// If newLeft == Integer.MIN_VALUE, this means that item/ipos was the only

			// node in the left subtree. Its left pointer pointed to its first left ancestor.
			newLeft = readLeft(ipos);
		changeRight(ipos, right);
		changeRed(ipos, red);
		changeLeft(ipos, newLeft);

		// Repair ancestral pointers
		int prev = previous(ipos);
		int next = next(ipos);
		if (prev > 0 && readRight(prev) < 0)
			changeRight(prev, -ipos);
		if (next > 0 && readLeft(next) < 0)
			changeLeft(next, -ipos);
		return ipos;
	}

	private int removeLeft(boolean[] black, int left, int right, boolean red) {
		black[0] = !red;
		if (right <= 0)
			return Integer.MIN_VALUE;
		// The node had one child, so this child's black-height must be 0, so it must be red, therefore 'node' must be black.
		if (red)
			throw new CorruptionException("Expected node with single-child to be black");
		// Fix ancestral pointer
		if (readLeft(right) < 0)
			changeLeft(right, left);
		return right;
	}

	private int removeRight(boolean[] black, int left, int right, boolean red) {
		black[0] = !red;
		// The node had one child, so this child's black-height must be 0, so it must be red, therefore 'node' must be black.
		if (red)
			throw new CorruptionException("Expected node with single-child to be black");
		// Fix ancestral pointer
		if (readRight(left) < 0)
			changeRight(left, right);
		return left;
	}

	/**
	 * Return the highest element by key in a sub-tree.
	 * <p>
	 * @param elm The head of the subtree.
	 * @return The position of the maximum element.
	 */
	private int max(int elm) {
		int depth = 0;
		if (elm == 0)
			return elm;
		while (true) {
			int right = readRight(elm);
			if (right <= 0)
				return elm;
			elm = right;
			if (depth++ > MAX_DEPTH)
				throw new CorruptionException(TOO_MANY_ITERATIONS);
		}
	}

	/**
	 * Return the lowest element by key in a sub-tree.
	 * <p>
	 * Modifies {@link #work}.
	 * @param elm The head of the subtree.
	 * @return The position of the maximum element.
	 */
	private int min(int elm) {
		int depth = 0;
		if (elm == 0)
			return elm;
		while (true) {
			int left = readLeft(elm);
			if (left <= 0)
				return elm;
			elm = left;
			if (depth++ > MAX_DEPTH)
				throw new CorruptionException(TOO_MANY_ITERATIONS);
		}
	}

	/**
	 * Return the first element of the tree.
	 * <p>
	 * Modifies {@link #work}.
	 * @return The position of the first element.
	 */
	protected int first() {
		return min(readTop());
	}

	/**
	 * Return the last element of the tree.
	 * <p>
	 * Modifies {@link #work}.
	 * @return The position of the last element.
	 */
	protected int last() {
		return max(readTop());
	}

	public String dump() {
		StringBuilder bld = new StringBuilder();
		bld.append("top:" + readTop() + "\n");
		for (int r = first(); r > 0; r = next(r)) {
			bld.append("rec:").append(r).append(" red:").append(readRed(r));
			bld.append(" left:").append(readLeft(r)).append(" right:").append(readRight(r)).append(" record:").append(r).append("\n");
		}
		return bld.toString();
	}

	/**
	 * Return the next node position.
	 * <p>
	 * Modifies {@link #work}.
	 */
	protected int next(int elm) {
		if (elm == 0)
			throw new NoSuchElementException("Called next without current element");
		int next = readRight(elm);
		int depth = 0;
		while (next > 0 && readLeft(next) > 0) {
			next = readLeft(next);
			if (depth++ > MAX_DEPTH)
				throw new CorruptionException(TOO_MANY_ITERATIONS);
		}
		return Math.abs(next);
	}

	/**
	 * Return the previous node position.
	 * <p>
	 * Modifies {@link #work}.
	 */
	protected int previous(int elm) {
		if (elm == 0)
			return 0;
		int prev = readLeft(elm);
		int depth = 0;
		while (prev > 0 && readRight(prev) > 0) {
			prev = readRight(prev);
			if (depth++ > MAX_DEPTH)
				throw new CorruptionException(TOO_MANY_ITERATIONS);
		}
		return Math.abs(prev);
	}

	/**
	 * Repairs a black-height invariant violation of a subtree.
	 * <p>
	 * Modifies {@link #current}, {@link #work}, {@link #work2}, {@link #work3}, {@link #repair1} and {@link #repair2}.
	 * @param hpos The subtree head pointer.
	 * @param black A mutable boolean containing whether a black node was removed. On return will contain <code>false</code>
	 * 		if the imbalance was fixed, or <code>true</code> if it still needs fixing further up the tree.
	 * @param compareTo Which subtree had a node removed.
	 * @return The head of the new subtree.
	 */
	private int repairBlackImbalance(int hpos, boolean[] black, int compareTo) {
		repair1 = 0;
		repair2 = 0;
		// The one child's black-height is reduced by one. Also reduce the other (this is always possible, as reducing the black-height of one implies that both black-heights must have been at least 1).
		if (compareTo < 0)
			childToRed(readRight(hpos));
		if (compareTo > 0)
			childToRed(readLeft(hpos));

		// At this point, we can have 2 situations:
		// 1. Either the bigger child's node was black. In this case only 'repair1' is set.
		// 2. The bigger child's node was red. In this case 'hpos' must have been black, and both 'repair1' and 'repair2' have been set.

		// If 'work' is red, change it to black. We have then fixed the black-height invariant.
		if (readRed(hpos)) {
			// This can only happen in situation 1.
			changeRed(hpos, false);
			black[0] = false;
		}

		// Here we use balance() to change any Black->Red->Red->X paths encountered to repair1 and repair2.
		//
		// In situation 1, if there is a double red situation, the balance rotation will be right at the top.
		// Either hpos was already black at the start of the method, and we can change it black again below
		// (because black[0] is still true), or hpos was already black, in which case it doesn't introduce
		// a new double red situation by having it be red again.
		//
		// In situation 2, there might be two rotations necessary, with the end result of both hpos and
		// the bigger child becoming red. But in this situation we are guaranteed that black[0] is still true,
		// because the earlier red->black switch cannot happen in situation 2.

		// By changing a child to red, we can have created one or two double-red situations. Repair this.
		if (repair1 != 0) {
			hpos = repair(repair1, hpos, 0);
		}
		if (repair2 != 0) {
			hpos = repair(repair2, hpos, 0);
		}

		// Change hpos to black if we can and have not done so earlier.
		if (black[0] && readRed(hpos)) {
			changeRed(hpos, false);
			black[0] = false;
		}
		return hpos;
	}

	/**
	 * Reduce the black-height of a subtree by either changing the top node from black to red, or both its children.
	 * Because the double-red invariant holds, if the top node is not black, and the black-height is at least 1, then
	 * the node must have 2 black children.
	 * <p>
	 * The fields {@link #repair1} and {@link #repair2} will be set to the nodes which have been changed to red.
	 * The double-red invariant might be broken around these nodes.
	 * <p>
	 * Require: the black-height of the subtree must be at least 1. The double-red invariant of this subtree must hold.
	 * <p>
	 * Assure: the black-height of the subtree has been reduced by 1, by changing the top or its immediate children from black to red.
	 * <p>
	 * Modifies {@link #work2}, {@link #work3}, {@link #repair1} and {@link #repair2}.
	 * 
	 * @param workPos The subtree to reduce the black-height for.
	 */
	private void childToRed(int workPos) {
		if (readRed(workPos)) {
			// workPos is red, therefore it must have 2 black children
			if (readLeft(workPos) <= 0 || readRight(workPos) <= 0)
				throw new CorruptionException("childToRed() called for invalid situation; red-black tree invariants possibly broken");
			changeRed(readLeft(workPos), true);
			repair1 = readLeft(workPos);
			changeRed(readRight(workPos), true);
			repair2 = readRight(workPos);
		} else {
			changeRed(workPos, true);
			repair1 = workPos;
		}
	}

	/**
	 * Search for {@link #current} and repair all double reds encountered till that point. The double reds must have a black
	 * parent which is {@link #work} or a descendant.
	 * <p>
	 * Modifies {@link #work}, {@link #work2}, {@link #work3}.
	 * 
	 * @param depth The current recursion depth. Is limited by MAX_DEPTH to prevent infinite loops due to data corruption.
	 * @return The head of the new subtree
	 */
	private int repair(int rec, int pos, int depth) {
		if (depth > MAX_DEPTH)
			throw new CorruptionException(TOO_MANY_ITERATIONS);
		int result = balance(pos);
		if (result != rec) {
			int compareTo = compareTo(rec, result);
			if (compareTo < 0)
				changeLeft(result, repair(rec, readLeft(result), depth + 1));
			if (compareTo > 0)
				changeRight(result, repair(rec, readRight(result), depth + 1));
		}
		return result;
	}

	/**
	 * Searches the requested node in the tree. The {@link Comparable} key must be compatible with {@link Node#compareTo(Node)}.
	 * If multiple nodes match the key, results the lowest of them.
	 * 
	 * @param key The key to search for.
	 * @return A {@link Node} matching the search.
	 */
	protected int find(Key key) {
		return searchPos(key);
	}

	/**
	 * Searches for a node in the tree depending on the parameters.
	 * 
	 * @param key The key to search for. Must be compatible with {@link Node#compareTo(Node)}.
	 * @param operation that specifies what node to search relative to the given key.
	 * 	On <code>EQ</code> this call will return <code>null</code> when the key is not found.
	 *  The other operations will only return <code>null</code> when there are no possible matching nodes
	 *  in the tree. For example if <code>GT</code> is given with the highest current key in the tree.
	 *
	 *  When <code>LE</code> is given the returned <code>Node</code> is the highest matching node.
	 *  When <code>LT</code> is given the returned <code>Node</code> is the highest node not matching the key.
	 *  When <code>GE</code> is given the returned <code>Node</code> is the lowest node matching the key.
	 *  When <code>GT</code> is given the returned <code>Node</code> is the lowest node not matching the key.
	 *
	 * @return A {@link Node} matching the search or <code>null</code> if there is no such node.
	 */
	private int searchPos(Key key) {
		IndexOperation oper = key.oper();
		boolean higher = oper == IndexOperation.GT || oper == IndexOperation.LE;
		boolean equals = oper == IndexOperation.EQ;
		if (readTop() == 0)
			return 0;
		int current = readTop();
		int found = -1;
		int count = 0;
		while (count < MAX_DEPTH) {
			int test = key.compareTo(current);
			if (test == 0)
				found = current;
			if ((test < 0 || !equals && !higher && test == 0) && readLeft(current) > 0) {
				current = readLeft(current);
			} else if ((test > 0 || higher && test == 0) && readRight(current) > 0) {
				current = readRight(current);
			} else
				return getRecord(key.oper(), current, found, test);
			count++;
		}
		throw new CorruptionException("Cyclic structure in tree");
	}

	private int getRecord(IndexOperation op, int current, int found, int test) {
		int pos = -1;
		switch (op) {
		case EQ:
			return found > 0 ? found : 0;
		case GE:
			if (test > 0)
				pos = next(current);
			break;
		case GT:
			if (test >= 0)
				pos = next(current);
			break;
		case LE:
			if (test < 0)
				pos = previous(current);
			break;
		case LT:
			if (test <= 0)
				pos = previous(current);
			break;
		}
		if (pos == 0)
			return 0;
		return pos > 0 ? pos : current;
	}

	protected boolean containsKey(Key key) {
		return find(key) != 0;
	}

	/**
	 * Check if the tree still adheres to the 'rules' of the red/black tree definition.
	 */
	public void verify() {
		if (readTop() == 0)
			return;
		int current = readTop();
		if (readRed(current))
			throw new CorruptionException("Root is not black");
		measures[0] = -1; // Depth so far
		measures[1] = 0; // Amount of elements found during DFS traversal (verify(int, int[], Node))
		verify(current, 0, measures);
		verifyList(measures[1]);
	}

	private void verify(int current, int depth, int[] measure) {
		if (depth > MAX_DEPTH)
			throw new CorruptionException(TOO_MANY_ITERATIONS);
		int left = readLeft(current);
		int right = readRight(current);
		if (Math.abs(left) == current || Math.abs(right) == current)
			throw new CorruptionException("Linked to self");
		if (!readRed(current))
			depth++;
		verifySide(current, left, true, depth, measure);
		verifySide(current, right, false, depth, measure);
		measure[1]++;
	}

	private void verifySide(int current, int sidePos, boolean left, int depth, int[] measure) {
		if (sidePos > 0) {
			int cmd = compareTo(sidePos, current);
			if (cmd == 0 && sidePos != current)
				throw new CorruptionException("Duplicate key in index");
			if ((left && cmd > 0) || (!left && cmd < 0)) {
				throw new CorruptionException("Ordering not correct");
			}
			if (readRed(current) && readRed(sidePos))
				throw new CorruptionException("Two reds adjecent");
			verify(sidePos, depth, measure);
		} else if (measure[0] == -1)
			measure[0] = depth;
		else if (measure[0] != depth)
			throw new CorruptionException("Not balanced");
	}

	private void verifyList(int elms) {
		int min = min(readTop());
		int max = max(readTop());
		if (next(max) != 0)
			throw new CorruptionException("Incorrect max element");
		if (previous(min) != 0)
			throw new CorruptionException("Incorrect min element");
		verifyLoop(elms, min, max, true);
		verifyLoop(elms, max, min, false);
	}

	private void verifyLoop(int elms, int first, int last, boolean asc) {
		int elm = first;
		int step = 1;
		while (elm != last) {
			if (step > elms)
				throw new CorruptionException("List too long");
			int work2 = elm;
			elm = asc ? next(elm) : previous(elm);
			if (elm == 0)
				throw new CorruptionException("Incorrect list");
			int work = elm;
			int compareTo = compareTo(work, work2);
			if (asc ? compareTo < 0 : compareTo > 0)
				throw new CorruptionException("Not " + (asc ? "ascending" : "descending") + " list");
			step++;
		}
		if (step < elms)
			throw new CorruptionException("List too short");
	}
}
