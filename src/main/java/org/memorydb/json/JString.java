package org.memorydb.json;

import java.io.Serializable;

public class JString implements Serializable {
	private static final long serialVersionUID = 1L;
	private final CharSequence data;
	private int pos;
	private int length;

	public JString(CharSequence data) {
		this.data = data;
	}

	public void setPosition(int pos, int length) {
		this.pos = pos;
		this.length = length;
	}

	public boolean equalsTo(String str) {
		int len1 = length;
		int len2 = str.length();
		int lim = Math.min(len1, len2);
		int k = 0;
		while (k < lim) {
			char c1 = data.charAt(k + pos);
			char c2 = str.charAt(k);
			if (c1 != c2)
				return false;
			k++;
		}
		return len1 == len2;
	}

	public int compareTo(JString o) {
		int len1 = length;
		int len2 = o.length;
		int lim = Math.min(len1, len2);
		int k = 0;
		while (k < lim) {
			char c1 = data.charAt(k + pos);
			char c2 = o.data.charAt(k + o.pos);
			if (c1 != c2)
				return c1 - c2;
			k++;
		}
		return len1 - len2;
	}

	public int compare(Object key) {
		if (key == null || !(key instanceof String))
			return -1;
		String val = (String) key;
		return val.compareTo(toString());
	}

	@Override
	public String toString() {
		StringBuilder res = new StringBuilder();
		append(res);
		return res.toString();
	}

	public void append(StringBuilder res) {
		JsonReader.parseString(data, pos, res);
	}
}
