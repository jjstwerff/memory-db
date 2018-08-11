package org.memorydb.structure;

import java.util.Map;
import java.util.TreeMap;

import org.memorydb.handler.CorruptionException;

public class StringHash {
	private final Hash index;
	private long strings;
	private long capacity;
	private int stringsLen;
	private static final long INITIAL_SIZE = 100l;
	private static final int MAX_STRING = 1000000;

	public StringHash() {
		index = new Hash();
		this.capacity = INITIAL_SIZE;
		strings = index.getUnsafe().allocateMemory(capacity);
		stringsLen = 0;
	}

	private int length(int pos) {
		assert pos >= 0 && pos < capacity - 1;
		return index.getUnsafe().getShort(strings + pos);
	}

	private void setLength(int idx, int val) {
		assert idx >= 0 && idx < capacity - 1;
		index.getUnsafe().putShort(strings + idx, (short) val);
	}

	private interface Handle {
		boolean handle(int ch);
	}

	private int addChar(int idx, Handle handle) {
		int b0 = index.getUnsafe().getByte(strings + idx) & 0xFF;
		if (b0 < 0x80) {
			if (!handle.handle(b0))
				return -1;
			return idx + 1;
		}
		byte b1 = index.getUnsafe().getByte(strings + idx + 1);
		if (b0 < 0xE0) {
			if (!handle.handle(((b0 & 0x1F) << 6) + (b1 & 0x3F)))
				return -1;
			return idx + 2;
		}
		byte b2 = index.getUnsafe().getByte(strings + idx + 2);
		if (b0 < 0xF0) {
			if (!handle.handle(((b0 & 0xF) << 12) + ((b1 & 0x3F) << 6) + (b2 & 0x3F)))
				return -1;
			return idx + 3;
		}
		throw new CorruptionException("Encoding error");
		/*
		byte b3 = index.getUnsafe().getByte(strings + idx + 3);
		addChar(bld, 0xd800 + (b0 & 0x3) << 8 + ((b1 & 0x3f) << 2) + ((b2 & 0x30) >> 4)); // TODO not correctly
		addChar(bld, 0xdc00 + (b2 & 0xf) << 6 + (b3 & 0x3f));
		return idx + 4;*/
	}

	private void setChar(char c) {
		int ch = c & 0xFFFF;
		if (ch <= 0x8F)
			put(ch);
		else if (ch < 0x800) {
			put((ch >> 6) + 0xC0);
			put((ch & 0x3F) + 0x80);
		} else if (ch <= 0xFFFF) {
			put((ch >> 12) + 0xE0);
			put(((ch >> 6) & 0x3F) + 0x80);
			put((ch & 0x3F) + 0x80);
		} else // TODO implement UTF-16 double character
			throw new CorruptionException("Encoding error");
	}

	private void put(int ch) {
		index.getUnsafe().putByte(strings + stringsLen++, (byte) ch);
	}

	public boolean contains(String str) {
		return index.get(str) >= 0;
	}

	public String get(int idx) {
		StringBuilder bld = new StringBuilder();
		get(idx, bld);
		return bld.toString();
	}

	public String get(int idx, int length) {
		StringBuilder bld = new StringBuilder();
		int till = length + idx;
		if (till >= capacity)
			return bld.toString();
		int i = idx;
		while (i < till)
			i = addChar(i, ch -> {
				bld.append((char) ch);
				return true;
			});
		return bld.toString();
	}

	public void get(int idx, StringBuilder bld) {
		if (idx < 0 || idx + 2 >= capacity)
			return;
		int length = length(idx);
		if (length + idx + 2 >= capacity)
			return;
		if (length >= MAX_STRING)
			throw new CorruptionException("String too long");
		int i = idx + 2;
		while (i < idx + 2 + length)
			i = addChar(i, ch -> {
				bld.append((char) ch);
				return true;
			});
	}

	public int getHash(int idx) {
		final int[] h = new int[1];
		h[0] = 0;
		int length = length(idx);
		int i = idx + 2;
		while (i < idx + 2 + length)
			i = addChar(i, ch -> {
				h[0] = h[0] * 32 + ch;
				return true;
			});
		return h[0];
	}

	public int getHash(String s) {
		int h = 0;
		for (int i = 0; i < s.length(); i++)
			h = h * 32 + s.charAt(i);
		return h;
	}

	public boolean equals(int idx, String key) {
		int p = 0;
		boolean[] h = new boolean[1];
		int klength = key.length();
		h[0] = true;
		int length = length(idx);
		int i = idx + 2;
		while (i < idx + 2 + length) {
			int cp = p++;
			if (cp >= klength)
				return false;
			i = addChar(i, ch -> {
				if (key.charAt(cp) != ch)
					h[0] = false;
				return h[0];
			});
			if (i == -1)
				return false;
		}
		if (p < key.length())
			return false;
		return h[0];
	}

	/** If this string is already known return this position, otherwise
	 *  create a new position for it and return that.
	 */
	public int put(String str) {
		if (str == null)
			return -1;
		int length = str.length();
		if (length >= MAX_STRING)
			throw new CorruptionException("String too long");
		int i = index.get(str);
		if (i < 0) {
			if (stringsLen + length * 4 + 2 > capacity) {
				long newLength = Math.max(stringsLen + length + INITIAL_SIZE * 2, capacity * 2l);
				if (newLength > Integer.MAX_VALUE - 8)
					throw new CorruptionException("Extends beyond maximum capacity of StringHash");
				capacity = (int) newLength;
				strings = index.getUnsafe().reallocateMemory(strings, capacity);
			}
			i = stringsLen;
			stringsLen += 2; // reserve space for the length of this string
			for (int c = 0; c < length; c++)
				setChar(str.charAt(c));
			setLength(i, stringsLen - i - 2);
			index.put(i);
		}
		return i;
	}

	public int pos(String str) {
		return index.get(str);
	}

	private class Hash extends IndexHash<String> {
		public Hash() {
			super();
		}

		@Override
		protected int getHash(int pos) {
			return StringHash.this.getHash(pos);
		}

		@Override
		protected int getHash(String key) {
			return StringHash.this.getHash(key);
		}

		@Override
		protected boolean equalsKey(int pos, String key) {
			return StringHash.this.equals(pos, key);
		}
	}

	public void clear() {
		index.clear();
		stringsLen = 0;
	}

	public String analyze() {
		StringBuilder bld = new StringBuilder();
		int total = 0;
		int size = 0;
		for (int pos : index) {
			size += length(pos);
			total++;
		}
		bld.append("strings number:" + total + " average:" + (total == 0 ? 0 : size / total) + "\n");
		bld.append("data:" + stringsLen * 2 / 1024 + "kb total:" + capacity * 2l / 1024 + "kb\n");
		bld.append("hash:" + index.size * 8 / 1024 + "kb total:" + index.capacity * 8 / 1024 + "kb");
		return bld.toString();
	}

	@Override
	public String toString() {
		Map<String, Integer> values = new TreeMap<>();
		for (int pos : index)
			values.put(get(pos), pos);
		return values.toString();
	}
}
