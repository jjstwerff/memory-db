package org.memorydb.generate;

import java.util.ArrayList;
import java.util.List;

public class Index {
	private final Record on;
	private final Record table;
	private final String name;
	private final List<String> fields;
	private final List<KeyField> keys;
	private final int fieldPos; // position in bits of the left/right fields inside the table
	private final int flagPos; // position in bits of the red/black bit inside the table
	private int parentPos; // position of the top in the parent record
	private final boolean primary; // this is the primary index of this table

	public class KeyField {
		private final String keyName;
		private final String type;

		KeyField(String keyName, String type) {
			this.keyName = keyName;
			this.type = type;
		}

		public String getName() {
			return keyName;
		}

		public String getType() {
			return type;
		}
	}

	public Index(Record on, Record table, String name, List<String> fields, int fieldPos, int flagPos, int parentPos, boolean primary) {
		this.on = on;
		this.table = table;
		this.name = name;
		this.fields = fields;
		this.fieldPos = fieldPos;
		this.flagPos = flagPos;
		this.parentPos = parentPos;
		this.primary = primary;
		this.keys = new ArrayList<>();
		for (String field : fields) {
			Field f = table.getField(field.split("\\.")[0]);
			if (f == null)
				throw new GenerateException("Unknown field '" + table.getName() + "." + field + "'");
			if (primary)
				f.setKey(true);
			StringBuilder n = new StringBuilder();
			Type t = Type.INTEGER;
			f = null;
			for (String fName : field.split("\\.")) {
				if (f != null) {
					n.append("_");
					f = f.getRelated().getField(fName);
				} else
					f = table.getField(fName);
				if (f == null)
					throw new GenerateException("Unknown field '" + table.getName() + "." + field + "'");
				t = f.getType();
				n.append(fName);
			}
			keys.add(new KeyField(n.toString(), t.name()));
		}
	}

	public Record getTable() {
		return table;
	}

	public String getName() {
		return name;
	}

	public List<KeyField> getKeys() {
		return keys;
	}

	public int getFieldPos() {
		return fieldPos >> 3;
	}

	public int getParentPos() {
		return parentPos;
	}

	public int getFlagPos() {
		return flagPos;
	}

	public List<String> getJavaTypes() {
		List<String> res = new ArrayList<>();
		for (String field : fields) {
			Record c = table;
			Field f = null;
			for (String part : field.split("\\.")) {
				f = c.getField(part);
				if (f == null)
					throw new GenerateException("Unknown field '" + c.getName() + "." + field + "'");
				c = f.getRelated();
			}
			if (f != null)
				res.add(f.getJavaType());
		}
		return res;
	}

	public List<String> getRetrieve() {
		List<String> res = new ArrayList<>();
		for (String field : fields) {
			StringBuilder retr = new StringBuilder();
			for (String part : field.split("\\.")) {
				if (retr.length() != 0)
					retr.append(".");
				retr.append("get" + part.substring(0, 1).toUpperCase() + part.substring(1) + "()");
			}
			res.add(retr.toString());
		}
		return res;
	}

	public List<String> getNames() {
		List<String> res = new ArrayList<>();
		for (String field : fields) {
			StringBuilder last = new StringBuilder();
			for (String part : field.split("\\."))
				last.append(part.substring(0, 1).toUpperCase() + part.substring(1));
			res.add(last.toString());
		}
		return res;
	}

	public String getKeyData() {
		StringBuilder bld = new StringBuilder();
		for (String field : fields) {
			if (bld.length() != 0)
				bld.append(", ");
			boolean first = true;
			for (String part : field.split("\\.")) {
				if (first) {
					first = false;
					bld.append(part);
				} else
					bld.append(".get").append(part.substring(0, 1).toUpperCase()).append(part.substring(1)).append("()");
			}
		}
		return bld.toString();
	}

	public boolean isPrimary() {
		return primary;
	}

	public String getTopGet() {
		boolean included = on != null && !on.getIncluded().isEmpty();
		String parent = table.getParent() == null ? "0" : "rec";
		if (included) {
			String lname = on.getName().toLowerCase();
			return "record.store().getInt(record.rec(), record." + lname + "Position() + " + parentPos + ")";
		}
		return "store.getInt(" + parent + ", " + parentPos + ")";
	}

	public String getTopSet() {
		boolean included = on != null && !on.getIncluded().isEmpty();
		String parent = table.getParent() == null ? "0" : "rec";
		if (included) {
			String lname = on.getName().toLowerCase();
			return "record.store().setInt(record.rec(), record." + lname + "Position() + " + parentPos + ", value)";
		}
		return "store.setInt(" + parent + ", " + parentPos + ", value)";
	}

	@Override
	public String toString() {
		return name + "<" + table + ">";
	}

	public void dump(StringBuilder bld) {
		bld.append("name=" + name + ", record=" + table.getName() + ", fields=[\n");
		for (String fld : fields) {
			bld.append("\tfield=" + fld + "\n");
		}
		bld.append("]");
		bld.append(", fieldPos=").append(fieldPos);
		bld.append(", flagPos=").append(flagPos);
		bld.append(", parentPos=").append(parentPos);
		if (primary)
			bld.append(", primary=true");
		bld.append("\n");
	}

	public void setParentPos(int parentPos) {
		this.parentPos = parentPos;
	}
}
