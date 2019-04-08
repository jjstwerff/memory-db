package org.memorydb.generate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

public class Record implements Comparable<Record> {
	private final String name;
	private final List<Field> fields;
	private final Map<String, Field> onName;
	private final Map<String, Index> indexes;
	private final List<Field> linked;
	private final Project project;
	private final TreeMap<Integer, Integer> freeBits; // bytes inside the records that are not completely claimed
	private Record parent; // optional parent table
	private int size; // size of the record in bytes
	private int totalSize; // maximum size of the record in bytes
	private boolean related; // true when other records have a relation towards this record
	private final boolean full; // true when this record can be in it's own space.. not for ARRAY or SUB parts
	private boolean object; // true when this record is only used as an object in other records and not in a set or index.
	private boolean content; // true when this record is only used as content of other records
	private final Map<Record, Integer> includes; // includes the fields of these other records
	private final List<Record> included; // included in these records
	private int conditionSize = -1;
	private int upPos = -1;

	public Record(String name, Project project, boolean full) {
		this.name = name;
		this.project = project;
		this.fields = new ArrayList<>();
		this.onName = new TreeMap<>();
		this.indexes = new LinkedHashMap<>();
		this.linked = new ArrayList<>();
		this.size = full ? 4 : 0;
		this.totalSize = -1;
		this.freeBits = new TreeMap<>();
		this.full = full;
		this.includes = new TreeMap<>();
		this.included = new ArrayList<>();
		related = false;
		object = false;
	}

	public Field field(String fieldName, Type type, String... values) {
		if (onName.containsKey(fieldName))
			throw new GenerateException("Duplicate field name " + fieldName + " on table " + getName());
		Field field = new Field(this, fieldName, type, values);
		fields.add(field);
		onName.put(fieldName, field);
		return field;
	}

	public Field field(String fieldName, Type type, Record relatedRecord) {
		if (onName.containsKey(fieldName))
			throw new GenerateException("Duplicate field name " + fieldName + " on table " + getName());
		Field field = new Field(this, fieldName, type, relatedRecord);
		fields.add(field);
		onName.put(fieldName, field);
		if (type == Type.RELATION) {
			if (!relatedRecord.full && !relatedRecord.content)
				throw new GenerateException("Cannot create relation " + name + "." + fieldName + " to a non full record " + relatedRecord.name);
			relatedRecord.related = true;
		} else if (type == Type.ARRAY) {
			if (relatedRecord.full || relatedRecord.related || !relatedRecord.getIndexes().isEmpty() || relatedRecord.getField("toParent") != null)
				throw new GenerateException("Related " + relatedRecord.getName() + " cannot be both an ARRAY and a RELATION record");
			relatedRecord.linked.add(field);
		} else if (type == Type.OBJECT) {
			if (relatedRecord.related || !relatedRecord.full || relatedRecord.content)
				throw new GenerateException("Related " + relatedRecord.getName() + " cannot be an INDEX or ARRAY and be an OBJECT");
			relatedRecord.object = true;
		}
		return field;
	}

	public Field field(String fieldName, Record relatedRecord, String... indexFields) {
		if (onName.containsKey(fieldName))
			throw new GenerateException("Duplicate field name " + fieldName + " on table " + getName());
		Field field = new Field(this, fieldName, relatedRecord, indexFields);
		if (onName.containsKey(fieldName))
			throw new GenerateException("Duplicate field " + fieldName + " on table " + getName());
		fields.add(field);
		onName.put(fieldName, field);
		if (relatedRecord.getParent() != null && relatedRecord.getParent() != this)
			throw new GenerateException("No support for multiple hierarchical paths oo " + relatedRecord.getName() + " from " + getName() + "." + fieldName);
		if (relatedRecord.getParent() == null) {
			relatedRecord.setParent(this);
			if (content)
				relatedRecord.addSize(5); // type 1 byte, index 4 bytes
		} else if (!relatedRecord.getParent().equals(this))
			throw new GenerateException("Multiple parents");
		return field;
	}

	public Field getCondition() {
		for (Field field : getFields()) {
			if (field.isCondition())
				return field;
		}
		return null;
	}

	public int getConditionSize() {
		return conditionSize;
	}

	public void setConditionSize(int conditionSize) {
		this.conditionSize = conditionSize;
	}

	/**
	 * Include all fields from the specified record into the current record
	 */
	public void include(Record record) {
		if (!record.content)
			throw new GenerateException("Can only includes content records");
		if (getCondition() != null)
			setSize(getTotalSize());
		includes.put(record, size);
		addSize(record.getTotalSize());
		record.included.add(this);
	}

	/**
	 * Called from field declaration to get it's position in the record.
	 */
	/* package private */ int getPos(Field field) {
		int fSize = field.getSize(); // field size in bits
		if (fSize < 8)
			return claimBits(fSize);
		int res = size * 8;
		addSize(fSize >> 3);
		return res;
	}

	private void addSize(int bytes) {
		size += bytes;
	}

	public List<Record> getIncluded() {
		return included;
	}

	public Set<Record> getIncludes() {
		return includes.keySet();
	}

	public int reserve() {
		if (included.isEmpty())
			return isFull() ? 12 : 16;
		for (Record incl : included) {
			if (!incl.isFull())
				return 17;
		}
		return 13;
	}

	public String includePos(Record incl, Record table) {
		if (full || !included.isEmpty())
			return Integer.toString(includes.get(incl));
		if (includes.get(incl) == 0)
			return "idx * " + size + " + " + table.reserve();
		return "idx * " + size + " + " + table.reserve() + " + " + includes.get(incl);
	}

	private int claimBits(int fSize) {
		assert (fSize < 8 && fSize > 0);
		Entry<Integer, Integer> entry = freeBits.ceilingEntry(fSize);
		if (entry == null) {
			freeBits.put(8, size * 8);
			addSize(1);
			entry = freeBits.ceilingEntry(fSize);
		}
		int pos = entry.getValue();
		freeBits.remove(entry.getKey());
		if (fSize > (pos & 7))
			freeBits.put((pos & 7) - fSize, pos + fSize);
		return pos;
	}

	public Field getField(String fieldName) {
		return onName.get(fieldName);
	}

	public Index index(String indexName, String... indexFields) {
		return index(null, indexName, project.mainIndex() * 4 + 12, indexFields);
	}

	Index index(Record on, String indexName, int pos, String... indexFields) {
		int flag = claimBits(1);
		if (getCondition() != null)
			setSize(getTotalSize());
		Index index = new Index(on, this, indexName, Arrays.asList(indexFields), size * 8, flag, pos, indexes.isEmpty());
		addSize(8);
		if (indexes.containsKey(indexName))
			throw new GenerateException("Duplicate index " + indexName);
		indexes.put(indexName, index);
		if (on == null)
			project.addIndex(index);
		return index;
	}

	public Index getIndex(String indexName) {
		return indexes.get(indexName);
	}

	public Collection<Index> getIndexes() {
		return indexes.values();
	}

	public Set<String> getImports() {
		Set<String> res = new TreeSet<>();
		for (Field fld : fields) {
			if (fld.getType() == Type.DATE) {
				res.add("java.time.LocalDateTime");
				res.add("org.memorydb.structure.DateTime");
			}
			if (fld.getType() == Type.SET) {
				Index index = fld.getIndex();
				if (index == null)
					throw new GenerateException("Unknown index in SET " + name + "." + fld.getName());
				for (String tp : index.getJavaTypes()) {
					if (tp.equals("LocalDateTime"))
						res.add("java.time.LocalDateTime");
				}
			}
			if (fld.getType() == Type.ENUMERATE) {
				res.add("java.util.HashMap");
				res.add("java.util.Map");
			}
		}
		return res;
	}

	public Set<String> getChangeImports() {
		Set<String> res = new TreeSet<>();
		for (Field fld : fields) {
			if (fld.getType() == Type.DATE) {
				res.add("java.time.LocalDateTime");
				res.add("org.memorydb.structure.DateTime");
			}
			if (fld.getType() == Type.ENUMERATE || fld.getType() == Type.BOOLEAN || fld.isMandatory())
				res.add("org.memorydb.handler.MutationException");
			if (fld.getType() == Type.STRING_POINTER)
				res.add("org.memorydb.structure.StringPointer");
		}
		return res;
	}

	public Index getPrimary() {
		if (indexes.values().isEmpty())
			return null;
		return indexes.values().iterator().next();
	}

	public String getKeyFields() {
		return keyFields(5);
	}

	public String getParseKeys() {
		if (parent != null) {
			StringBuilder bld = new StringBuilder();
			if (parent.parent != null) {
				bld.append("\t\t").append("int rec[] = new int[1];\n");
				bld.append("\t\t").append(parent.parent.name).append(" on = parent.up();\n");
				bld.append("\t\tparser.getRelation(\"").append(parent.name).append("\", (recNr, idx) -> {\n");
				bld.append("\t\t\trec[0] = ").append(parent.name).append(".parseKey(parser, on).rec();\n");
				bld.append("\t\t\treturn true;\n");
				bld.append("\t\t}, -1);\n");
				bld.append("\t\tparent = rec[0] > 0 ? parent.copy(rec[0]) : parent;\n");
			}
			bld.append(keyFields(2));
			bld.append(recSearch(2));
			return bld.toString();
		}
		return keyFields(2)+recSearch(2);
	}

	private String keyFields(int indent) {
		StringBuilder bld = new StringBuilder();
		StringBuilder ind = getIndent(indent);
		for (Field field : fields) {
			if (!field.isKey())
				continue;
			switch (field.getType()) {
			case RELATION:
				bld.append(ind).append(field.getJavaType()).append(" ").append(field.getName()).append(" = new ").append(field.getRelated().getName()).append("(store);\n");
				bld.append(ind).append("parser.getRelation(\"").append(field.getName()).append("\", (recNr, idx) -> {\n");
				bld.append(ind).append("\t").append(field.getName()).append(".parseKey(parser);\n");
				bld.append(ind).append("\treturn true;\n");
				bld.append(ind).append("}, -1);\n");
				break;
			case LONG:
				bld.append(ind).append(field.getJavaType()).append(" ").append(field.getName()).append(" = ");
				bld.append("parser.getLong(\"").append(field.getName()).append("\");\n");
				break;
			case INTEGER:
				bld.append(ind).append(field.getJavaType()).append(" ").append(field.getName()).append(" = ");
				bld.append("parser.getInt(\"").append(field.getName()).append("\");\n");
				break;
			case SHORT:
				bld.append(ind).append(field.getJavaType()).append(" ").append(field.getName()).append(" = ");
				bld.append("(short) parser.getInt(\"").append(field.getName()).append("\");\n");
				break;
			case BYTE:
				bld.append(ind).append(field.getJavaType()).append(" ").append(field.getName()).append(" = ");
				bld.append("(byte) parser.getInt(\"").append(field.getName()).append("\");\n");
				break;
			case DATE:
				bld.append(ind).append(field.getJavaType()).append(" ").append(field.getName()).append(" = ");
				bld.append("DateTime.of(parser.getString(\"").append(field.getName()).append("\"));\n");
				break;
			default:
				bld.append(ind).append(field.getJavaType()).append(" ").append(field.getName()).append(" = ");
				bld.append("parser.getString(\"").append(field.getName()).append("\");\n");
				break;
			}
		}
		return bld.toString();
	}

	private String recSearch(int indent) {
		StringBuilder bld = new StringBuilder();
		StringBuilder ind = getIndent(indent);
		Index index = getPrimary();
		if (index == null) {
			bld.append(ind).append("int nextRec = 0;\n");
			return bld.toString();
		}
		if (parent != null && !parent.getIncluded().isEmpty()) {
			bld.append(ind).append("int nextRec = new " + parent.getName() + ".Index").append(index.getName().substring(0, 1).toUpperCase()).append(index.getName().substring(1));
			bld.append("(parent, ");
		} else if (parent != null) {
			bld.append(ind).append("int nextRec = parent.new Index").append(index.getName().substring(0, 1).toUpperCase()).append(index.getName().substring(1)).append("(");
		} else {
			bld.append(ind).append("int nextRec = new Index").append(index.getName().substring(0, 1).toUpperCase()).append(index.getName().substring(1)).append("(store, ");
		}
		bld.append(index.getKeyData()).append(").search();\n");
		return bld.toString();
	}

	private StringBuilder getIndent(int indent) {
		StringBuilder ind = new StringBuilder();
		for (int i = 0; i < indent; i++)
			ind.append("\t");
		return ind;
	}

	public String getSetKeys() {
		StringBuilder bld = new StringBuilder();
		boolean first = true;
		for (Field field : fields) {
			if (!field.isKey())
				continue;
			if (first)
				first = false;
			else
				bld.append("\n");
			bld.append("\t\t\t\t\trecord.set").append(field.getName().substring(0, 1).toUpperCase()).append(field.getName().substring(1)).append("(");
			bld.append(field.getName()).append(");");
		}
		return bld.toString();
	}

	public String getArrayFields(Field field) {
		return setFields("\t\t", field);
	}

	public String getOtherFields() {
		return setFields("\t\t", null);
	}

	public boolean isNoFields() {
		if (!includes.isEmpty())
			return false;
		for (Field field : fields)
			if (!field.isKey())
				return false;
		return true;
	}

	public boolean isFieldIndex() {
		for (Field fld : fields) {
			if (fld.getIndex() == null)
				continue;
			if (!fld.getIndex().getKeys().isEmpty())
				return true;
		}
		return false;
	}

	private String setFields(String indent, Field onField) {
		StringBuilder bld = new StringBuilder();
		for (Field field : fields) {
			if (field.isKey())
				continue;
			boolean normal = field.getType() != Type.ARRAY && field.getType() != Type.SET && field.getType() != Type.OBJECT;
			String fldUpper = field.getUpperName();
			bld.append(indent).append("if (parser.has").append(normal ? "Field" : "Sub").append("(\"" + field.getName() + "\")) {\n");
			switch (field.getType()) {
			case ENUMERATE:
				bld.append(indent).append("\tString value").append(fldUpper).append(" = parser.getString(\"").append(field.getName()).append("\");\n");
				bld.append(indent).append("\t").append(fldUpper).append(" ").append(field.getName()).append(" = ").append(fldUpper).append(".get(value").append(fldUpper)
						.append(");\n");
				bld.append(indent).append("\tif (value").append(fldUpper).append(" != null && ").append(field.getName()).append(" == null)\n");
				bld.append(indent).append("\t\tparser.error(\"Cannot parse '\" + value").append(fldUpper).append(" + \"' for field ").append(name).append(".")
						.append(field.getName()).append("\");\n");
				bld.append(indent).append("\t").append("set").append(fldUpper).append("(").append("value").append(fldUpper).append(" == null ? null : ").append(field.getName())
						.append(");\n");
				break;
			case BOOLEAN:
				bld.append(indent).append("\tBoolean value").append(fldUpper).append(" = parser.getBoolean(\"").append(field.getName()).append("\");\n");
				bld.append(indent).append("\tif (value").append(fldUpper).append(" == null)\n");
				bld.append(indent).append("\t\tthrow new MutationException(\"Mandatory '" + field.getName() + "' field\");\n");
				bld.append(indent).append("\t").append("set").append(fldUpper).append("(value").append(fldUpper).append(");\n");
				break;
			case NULL_BOOLEAN:
				bld.append(indent).append("\t").append("set").append(fldUpper).append("(");
				bld.append("parser.getBoolean(\"").append(field.getName()).append("\"));\n");
				break;
			case INTEGER:
				bld.append(indent).append("\t").append("set").append(fldUpper).append("(");
				bld.append("parser.getInt(\"").append(field.getName()).append("\"));\n");
				break;
			case LONG:
				bld.append(indent).append("\t").append("set").append(fldUpper).append("(");
				bld.append("parser.getLong(\"").append(field.getName()).append("\"));\n");
				break;
			case FLOAT:
				bld.append(indent).append("\t").append("set").append(fldUpper).append("(");
				bld.append("parser.getDouble(\"").append(field.getName()).append("\"));\n");
				break;
			case SHORT:
				bld.append(indent).append("\t").append("set").append(fldUpper).append("(");
				bld.append("(short) parser.getInt(\"").append(field.getName()).append("\"));\n");
				break;
			case BYTE:
				bld.append(indent).append("\t").append("set").append(fldUpper).append("(");
				bld.append("(byte) parser.getInt(\"").append(field.getName()).append("\"));\n");
				break;
			case DATE:
				bld.append(indent).append("\t").append("set").append(fldUpper).append("(");
				bld.append("DateTime.of(parser.getString(\"").append(field.getName()).append("\")));\n");
				break;
			case RELATION:
				getRelData(indent, onField, bld, field);
				break;
			case SET:
				bld.append(indent).append("\t").append(field.getRelated().getName()).append(".parse(parser, this);\n");
				break;
			case ARRAY:
				bld.append(indent).append("\t").append(fldUpper).append("Array sub = new ").append(fldUpper).append("Array(this, -1);\n");
				bld.append(indent).append("\twhile (parser.getSub())\n");
				bld.append(indent).append("\t\tsub.add().parse(parser);\n");
				break;
			case OBJECT:
				bld.append(indent).append("\t").append("set").append(field.getName().substring(0, 1).toUpperCase()).append(field.getName().substring(1)).append("(");
				bld.append(field.getRelated().getName()).append(".parse(parser, store()));\n");
				break;
			default:
				bld.append(indent).append("\t").append("set").append(field.getName().substring(0, 1).toUpperCase()).append(field.getName().substring(1)).append("(");
				bld.append("parser.getString(\"").append(field.getName()).append("\"));\n");
				break;
			}
			bld.append(indent).append("}\n");
		}
		return bld.toString();
	}

	private void getRelData(String indent, Field onField, StringBuilder bld, Field field) {
		bld.append(indent).append("\tparser.getRelation(\"").append(field.getName()).append("\", (recNr, idx) -> {\n");
		Record relatedParent = field.getRelated().getParent();
		if (relatedParent == null) {
			bld.append(indent).append("\t\t").append(field.getRelated().getName()).append(" relRec = ").append(field.getRelated().getName()).append(".parseKey(parser, store());\n");
		} else {
			String common = null;
			if (this == relatedParent) {
				common = "this";
			} else if ((onField == null && relatedParent == parent) || (onField != null && onField.getParent() == relatedParent)) {
				common = "up()";
			} else if ((onField == null && parent != null && relatedParent == parent.getParent()) || (onField != null && onField.getParent().parent == relatedParent)) {
				common = "up().up()";
			} else {
				common = "up().up().up()";
			}
			bld.append(indent).append("\t\t").append(field.getRelated().getName()).append(" relRec = ").append(field.getRelated().getName()).append(".parseKey(parser, ").append(common).append(");\n");
		}
		if (onField != null) {
			bld.append(indent).append("\t\t").append(onField.getUpperName()).append("Array old = new ").append(onField.getUpperName()).append("Array(store, recNr, idx);\n");
			bld.append(indent).append("\t\t").append("old.set").append(field.getUpperName()).append("(relRec);\n");
		} else if (included.isEmpty()) {
			bld.append(indent).append("\t\ttry (Change").append(name).append(" old = (Change").append(name).append(") this.copy(recNr)) {\n");
			bld.append(indent).append("\t\t\t").append("old.set").append(field.getUpperName()).append("(relRec);\n");
			bld.append(indent).append("\t\t}\n");
		} else {
			bld.append(indent).append("\t\tChange").append(name).append(" old = (Change").append(name).append(") this.copy(recNr);\n");
			bld.append(indent).append("\t\t").append("old.set").append(field.getUpperName()).append("(relRec);\n");
		}
		bld.append(indent).append("\t\treturn relRec != null;\n");
		bld.append(indent).append("\t}, ");
		if (isFull() || !included.isEmpty())
			bld.append("rec());\n");
		else
			bld.append("idx);\n");
	}

	public String getName() {
		return name;
	}

	public List<Field> getFields() {
		return fields;
	}

	public int getTotalFields() {
		int total = 0;
		for (Entry<Record, Integer> entry : includes.entrySet())
			total += entry.getKey().getTotalFields();
		return total + fields.size();
	}

	public Record getParent() {
		return parent;
	}

	private void setParent(Record parent) {
		this.parent = parent;
		this.upPos = size;
		size += 4;
	}

	public int getUpPos() {
		return upPos;
	}

	public int getSize() {
		return size;
	}

	public int getTotalSize() {
		if (totalSize < 0) {
			totalSize = size;
			for (Field fld : fields) {
				int pos = (fld.getPos() + fld.getSize() + 7) / 8;
				if (totalSize < pos) {
					totalSize = pos;
				}
			}
		}
		return totalSize;
	}

	/* Get the size of the initial allocation for an Array with 5 elements */
	public int getAlloc() {
		return (11 + 5 * size) / 8; // 4 byte for size .. + 7 for rounding up
	}

	public void setSize(int size) {
		this.size = size;
		this.totalSize = -1;
	}

	public boolean isRelated() {
		return related;
	}

	public boolean isFull() {
		return full;
	}

	public boolean isObject() {
		return object;
	}

	public Record content() {
		content = true;
		return this;
	}

	public List<String> getKeyRetrieve() {
		for (Index idx : indexes.values()) {
			if (idx.isPrimary())
				return idx.getRetrieve();
		}
		return null;
	}

	public List<String> getKeyNames() {
		for (Index idx : indexes.values()) {
			if (idx.isPrimary())
				return idx.getNames();
		}
		return null;
	}

	@Override
	public String toString() {
		return name;
	}

	/**
	 * Verify that every position inside the record is filled and the size is correct
	 */
	public void verify() {
		Map<Integer, Integer> sizes = new TreeMap<>();
		for (Field f : fields) {
			int pos = f.getPos() / 8;
			if (f.getWhen() != null)
				continue;
			if (f.getSize() < 8) {
				sizes.put(pos, 1);
			} else {
				if (sizes.containsKey(pos) && f.getWhen() == null)
					throw new GenerateException("Duplicate position");
				sizes.put(pos, f.getSize() / 8);
			}
		}
		for (Index index : indexes.values()) {
			int field = index.getFieldPos();
			if (sizes.containsKey(field))
				throw new GenerateException("Duplicate position");
			sizes.put(field, 8);
			int pos = index.getFlagPos() / 8;
			sizes.put(pos, 1);
		}
		/*
		int pos = full ? 4 : 0;
		for (Entry<Integer, Integer> s : sizes.entrySet()) {
			if (s.getKey() != pos)
				throw new GenerateException("Missing positions for " + name);
			pos += s.getValue();
		}
		*/
	}

	public void dump(StringBuilder bld) {
		bld.append("name=" + name);
		if (parent != null)
			bld.append(", parent=" + parent.getName());
		bld.append(", size=" + getTotalSize());
		if (related)
			bld.append(", related=true");
		if (full)
			bld.append(", full=true");
		boolean sub = false;
		if (!fields.isEmpty()) {
			bld.append(", fields=\n");
			for (Field fld : fields)
				fld.dump(bld);
			sub = true;
		}
		if (!includes.isEmpty()) {
			bld.append(sub ? "& " : ", ");
			bld.append("includes=\n");
			for (Entry<Record, Integer> fld : includes.entrySet())
				bld.append("  record=").append(fld.getKey().getName()).append(", pos=").append(fld.getValue()).append("\n");
			sub = true;
		}
		if (!indexes.isEmpty()) {
			bld.append(sub ? "& " : ", ");
			bld.append("indexes=\n");
			for (Entry<String, Index> fld : indexes.entrySet())
				bld.append("  index=").append(fld.getKey()).append(", pos=").append(fld.getValue().getFieldPos()).append(", parentPos=").append(fld.getValue().getParentPos())
						.append("\n");
			sub = true;
		}
		if (!sub)
			bld.append("\n");
	}

	public SortedMap<Integer, Integer> getFreeBits() {
		return freeBits;
	}

	@Override
	public int compareTo(Record o) {
		return name.compareTo(o.name);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Record))
			return false;
		if (this == o)
			return true;
		return name.equals(((Record) o).name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	public List<Field> getLinked() {
		return linked;
	}
}
