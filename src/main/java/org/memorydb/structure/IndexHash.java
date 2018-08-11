package org.memorydb.structure;

import java.lang.reflect.Field;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

import sun.misc.Unsafe;

/**
 * An abstract hash-table that only holds indexes to a record structure.
 * The hash method is abstract because it should be calculated again from the record.
 * The index of the record is used for the equal function.
 * Multiple values inside the hash table can share the same key value.
 */

public abstract class IndexHash<K> implements Iterable<Integer> {
	static final long INDEX_SIZE = 8l;
	static final int DEFAULT_INITIAL_CAPACITY = 1 << 5;
	static final float DEFAULT_LOAD_FACTOR = 0.5f;
	private static final int REHASH_START = 10;
	private static final int REHASH_INCREASE = 3;
	private static final float INCREASE_FACTOR = 2.0f;
	private static Unsafe unsafe;
	/** A random number that is applied to every given hash value to prevent predictable hash classes. */
	private int hashSeed;
	/** The number of values inserted in this hash table. */
	/* package private */ int size;
	/** When do we want the next resize to happen. */
	private int threshold;
	/** Current load factor that determines the next threshold */
	private final float loadFactor;
	/** Each change will increment this counter, iterators will check this value to determine concurrent modifications */
	private int modCount;
	/** Structure with per 2 numbers: 0=the value, 1=the calculated hash
	 * numbers -1 indicate a not filled table value */
	long table;
	private final Random random;
	/* package private */ int capacity;

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

	private int hash(int pos) {
		assert pos >= 0 && pos < capacity;
		return unsafe.getInt(table + pos * INDEX_SIZE + 4);
	}

	private void hash(int pos, int val) {
		assert pos >= 0 && pos < capacity;
		unsafe.putInt(table + pos * INDEX_SIZE + 4, val);
	}

	private int index(int pos) {
		assert pos >= 0 && pos < capacity;
		return unsafe.getInt(table + pos * INDEX_SIZE);
	}

	private void index(int pos, int val) {
		assert pos >= 0 && pos < capacity;
		unsafe.putInt(table + pos * INDEX_SIZE, val);
	}

	private void allocateNew(int newCapacity) {
		capacity = newCapacity;
		table = unsafe.allocateMemory(capacity * INDEX_SIZE);
		allocateClear();
	}

	private void allocateClear() {
		unsafe.setMemory(table, capacity * INDEX_SIZE, (byte) 255);
	}

	private void clear(int pos) {
		index(pos, -1);
		hash(pos, -1);
	}

	protected abstract int getHash(int pos);

	protected abstract int getHash(K key);

	protected abstract boolean equalsKey(int pos, K key);

	public IndexHash() {
		this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, new Random());
	}

	public IndexHash(int initialCapacity) {
		this(initialCapacity, DEFAULT_LOAD_FACTOR, new Random());
	}

	/**
	 * Start a new hash-table with the needed capacity and calculate the initial threshold. 
	 */
	public IndexHash(int initialCapacity, float loadFactor, Random random) {
		this.random = random;
		hashSeed = random.nextInt();
		if (initialCapacity < 0)
			throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
		if (loadFactor <= 0 || Float.isNaN(loadFactor))
			throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
		this.loadFactor = loadFactor;
		allocateNew(initialCapacity);
		threshold = Math.round(initialCapacity * loadFactor);
	}

	private int fixedHash(int value) {
		return fixed(getHash(value));
	}

	private int fixed(int hash) {
		int h = hashSeed;
		h ^= hash;
		h ^= (h >>> 20) ^ (h >>> 12);
		return h ^ (h >>> 7) ^ (h >>> 4);
	}

	private boolean initHashSeedAsNeeded() {
		int value = capacity;
		int count = 0;
		while (value > 0) {
			count++;
			value = value >> 1;
		}
		boolean rehash = count >= REHASH_START && (count - REHASH_START) % REHASH_INCREASE == 0;
		if (rehash)
			hashSeed = random.nextInt();
		return rehash;
	}

	private void resize(int newCapacity) {
		if (newCapacity > Integer.MAX_VALUE / 2 - 8)
			throw new RadixException("Extends beyond maximum capacity of StringHash");
		if (newCapacity < capacity)
			throw new RadixException("Cannot lower the capacity");
		long oldTable = table;
		int oldCap = capacity;
		allocateNew(newCapacity);
		size = 0;
		transfer(oldTable, oldCap, initHashSeedAsNeeded());
		threshold = Math.round(newCapacity * loadFactor);
	}

	private void transfer(long oldTable, int oldCap, boolean rehash) {
		for (int i = 0; i < oldCap; i++) {
			int value = unsafe.getInt(oldTable + i * INDEX_SIZE);
			if (value == -1)
				continue;
			int hash = rehash ? fixedHash(value) : unsafe.getInt(oldTable + i * INDEX_SIZE + 4);
			internPut(value, hash);
		}
	}

	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public void clear() {
		modCount++;
		allocateClear();
		size = 0;
	}

	private int getIndex(int hash) {
		return Math.abs(hash) % capacity;
	}

	private void internPut(int value, int hash) {
		int pos = getIndex(hash);
		while (index(pos) >= 0)
			pos = nextPos(pos);
		index(pos, value);
		hash(pos, hash);
		size++;
	}

	public void put(int value) {
		if (value < 0)
			throw new RadixException("Putting negative values not allowed");
		modCount++;
		if (size >= threshold)
			resize((int) (capacity * INCREASE_FACTOR));
		int hash = fixedHash(value);
		internPut(value, hash);
	}

	public int get(K key) {
		if (key == null)
			return -1;
		int hash = fixed(getHash(key));
		int pos = getIter(key, getIndex(hash), hash);
		if (pos >= 0)
			return index(pos);
		return -1;
	}

	private int getIter(K key, int pos, int hash) {
		int value = index(pos);
		while (value >= 0) {
			if (hash(pos) == hash && equalsKey(value, key))
				return pos;
			pos = nextPos(pos);
			value = index(pos);
		}
		return -1;
	}

	private int nextPos(int pos) {
		int res = pos + 1;
		while (res >= capacity)
			res -= capacity;
		return res;
	}

	public class GetIter implements Iterator<Integer> {
		private int last;
		private int pos;
		int curModCount;
		private final K key;
		private final int hash;

		GetIter(K key) {
			this.key = key;
			this.hash = fixed(getHash(key));
			this.pos = getIter(key, getIndex(hash), hash);
		}

		@Override
		public boolean hasNext() {
			return pos > -1;
		}

		@Override
		public Integer next() {
			if (pos <= -1)
				throw new NoSuchElementException();
			if (modCount != curModCount)
				throw new ConcurrentModificationException();
			last = pos;
			pos = getIter(key, pos + 1, hash);
			return index(last);
		}

		@Override
		public void remove() {
			internDel(last);
			pos = iterate(last);
		}
	}

	public void del(K key) {
		modCount++;
		int hash = fixed(getHash(key));
		internDel(getIter(key, getIndex(hash), hash));
	}

	private void internDel(int pos) {
		if (pos < 0)
			return;
		int found = pos;
		clear(pos);
		int curPos = nextPos(pos);
		while (index(curPos) >= 0) {
			int nextHash = hash(curPos);
			int expectPos = getIndex(nextHash);
			if (!inRange(found, curPos, expectPos)) {
				index(found, index(curPos));
				hash(found, nextHash);
				clear(curPos);
				found = curPos;
			}
			curPos = nextPos(curPos);
		}
		size--;
	}

	private boolean inRange(int start, int end, int test) {
		if (start < end)
			return start < test && test <= end;
		return start < test || test <= end;
	}

	private int iterate(int pos) {
		while (pos < capacity && index(pos) == -1)
			pos++;
		if (pos < capacity)
			return pos;
		return -1;
	}

	public class Iter implements Iterator<Integer> {
		int last = -1;
		int pos;
		final int curModCount;

		public Iter() {
			pos = iterate(0);
			curModCount = modCount;
		}

		@Override
		public boolean hasNext() {
			return pos > -1;
		}

		@Override
		public Integer next() {
			if (pos <= -1)
				throw new NoSuchElementException();
			if (modCount != curModCount)
				throw new ConcurrentModificationException();
			if (pos == -1)
				return -1;
			last = pos;
			pos = iterate(pos + 1);
			return index(last);
		}

		@Override
		public void remove() {
			internDel(last);
			pos = iterate(last);
		}
	}

	public int[] values() {
		int[] result = new int[size];
		int i = 0;
		int pos = iterate(0);
		while (pos > -1) {
			result[i++] = index(pos);
			pos = iterate(pos + 1);
		}
		return result;
	}

	@Override
	public Iterator<Integer> iterator() {
		return new Iter();
	}
}
