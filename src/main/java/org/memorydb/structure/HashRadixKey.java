package org.memorydb.structure;

public class HashRadixKey extends RadixKey {
	private int hash;
	private int directCompareFields;
	private int hashSize;

	/* package private */ HashRadixKey(HashRadixTree tree) {
		this.directCompareFields = tree.directCompareFields + 1;
		this.hashSize = tree.hashSize;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof HashRadixKey))
			return false;
		HashRadixKey other = (HashRadixKey) o;
		return other.directCompareFields == directCompareFields && other.hashSize == hashSize;
	}

	@Override
	public int hashCode() {
		return hash + 32 * directCompareFields + 32 * 32 * hashSize;
	}

	@Override
	protected KeyChanger newKey() {
		KeyChanger res = super.newKey();
		res.addKeyPart(0); // reserve space for a hash table index value
		hash = 0;
		return res;
	}

	@Override
	/* package private */ void finish() {
		assert numParts >= directCompareFields;
		for (int p = 1; p < parts.size(); p++) { // skip first part with hash
			if (p >= directCompareFields)
				break;
			KeyPart keyPart = parts.get(p);
			if (keyPart.sval != null)
				hash = hash * 37 + keyPart.sval.hashCode();
			else
				hash = hash * 37 + Long.hashCode(keyPart.lval);
		}
		parts.get(0).lval = Math.abs(hash) % hashSize;
	}
}
