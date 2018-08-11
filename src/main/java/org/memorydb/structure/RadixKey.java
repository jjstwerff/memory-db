package org.memorydb.structure;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;

/**
 * Key implementation used in the RadixTree class.
 */
public class RadixKey {
	private int size; // size in bytes
	protected int numParts; // number of parts
	protected List<KeyPart> parts = new ArrayList<>();

	/* package private */ static class KeyPart {
		int pos; // position in bytes relative to the other key parts.. starts at 0 for the first part
		int size; // size in bytes of this part.. needed mostly for numbers that have less than the full 64 bits
		String sval; // value of this part when it is a String.. otherwise null
		long lval; // value of this part when it is not a string.. otherwise 0

		private void set(KeyPart o) {
			pos = o.pos;
			size = o.size;
			sval = o.sval;
			lval = o.lval;
		}

		@Override
		public String toString() {
			if (sval == null)
				return Long.toString(lval);
			return sval;
		}
	}

	/**
	 * Return number of bytes used for this specific key in the keys buffer.
	 */
	protected int combineKey(Object... key) {
		try (KeyChanger newKey = newKey()) {
			for (Object kp : key)
				newKey.addKeyPart(kp);
		}
		return size;
	}

	public class KeyChanger implements AutoCloseable {
		public void addKeyPart(Object kp) {
			KeyPart part = curPart();
			part.pos = size;
			part.sval = null;
			part.lval = 0;
			part.size = 8;
			if (kp instanceof String) {
				part.sval = (String) kp;
				part.size = part.sval.length() * 2 + 2;
			} else if (kp instanceof Date) {
				part.lval = ((Date) kp).getTime();
				part.lval ^= 1L << 63;
			} else if (kp instanceof Double) {
				part.lval = Double.doubleToRawLongBits((Double) kp);
				part.lval ^= 1L << 63;
			} else if (kp instanceof Long) {
				part.lval = (Long) kp;
				part.lval ^= 1L << 63;
			} else if (kp instanceof Integer) {
				int v = (Integer) kp;
				v ^= 1 << 31;
				part.lval = v;
				part.size = 4;
			} else
				throw new NotImplementedException("Unknown key type " + kp.getClass());
			size += part.size;
			numParts++;
		}

		public void addKeyPart(int kp) {
			KeyPart part = curPart();
			part.pos = size;
			part.sval = null;
			part.lval = kp;
			part.lval ^= 1L << 31;
			part.size = 4;
			size += part.size;
			numParts++;
		}

		public void addKeyPart(long kp) {
			KeyPart part = curPart();
			part.pos = size;
			part.sval = null;
			part.lval = kp;
			part.lval ^= 1L << 63;
			part.size = 8;
			size += part.size;
			numParts++;
		}

		public void addKeyPart(double kp) {
			addKeyPart(Double.doubleToRawLongBits(kp));
		}

		@Override
		public void close() {
			finish();
		}
	}

	protected KeyChanger newKey() {
		size = 0;
		numParts = 0;
		return new KeyChanger();
	}

	private KeyPart curPart() {
		if (numParts >= parts.size()) {
			KeyPart part = new KeyPart();
			parts.add(part);
			return part;
		}
		return parts.get(numParts);
	}

	public int bits() {
		return size * 8;
	}

	/* package private */ boolean bit(int bit, boolean highest) {
		for (int p = 0; p < numParts; p++) {
			KeyPart keyPart = parts.get(p);
			int rbit = bit - keyPart.pos * 8;
			if (rbit >= keyPart.size * 8)
				continue;
			if (keyPart.sval != null) {
				if (rbit / 16 >= keyPart.sval.length())
					return highest;
				return ((keyPart.sval.charAt(rbit / 16)) & (1 << 15 - rbit % 16)) > 0;
			}
			return (keyPart.lval & (1l << (keyPart.size == 4 ? 31l : 63l) - rbit)) > 0;
		}
		return highest;
	}

	/* package private */ int bytes(int pos) {
		for (int p = 0; p < numParts; p++) {
			KeyPart keyPart = parts.get(p);
			int rpos = pos - keyPart.pos;
			if (rpos >= keyPart.size)
				continue;
			if (keyPart.sval != null) {
				if (rpos >= keyPart.sval.length() * 2)
					return 0;
				return ((keyPart.sval.charAt(rpos / 2)) >> (8 - (rpos % 2) * 8)) & 255;
			}
			return (int) ((keyPart.lval >> ((keyPart.size == 4 ? 24l : 56l) - rpos * 8l)) & 255);
		}
		return 0;
	}

	public String showBits() {
		StringBuilder builder = new StringBuilder();
		for (int p = 0; p < size; p++) {
			for (int b = 0; b < 8; b++)
				builder.append(bit(p * 8 + b, false) ? 1 : 0);
			builder.append(" ");
		}
		return builder.toString();
	}

	/** Copy key details from the given key */
	public void copy(RadixKey key) {
		size = 0;
		numParts = 0;
		for (int p = 0; p < key.numParts; p++, numParts++) {
			KeyPart part = curPart();
			part.set(key.parts.get(p));
			size += part.size;
		}
		finish();
	}

	public int size() {
		return size;
	}

	@Override
	public String toString() {
		return parts.toString();
	}

	/* package private */ void finish() {
		// nothing here
	}
}
