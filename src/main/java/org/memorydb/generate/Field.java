package org.memorydb.generate;

import java.util.Arrays;
import java.util.List;

public class Field {
	private final Record table;
	private final String name;
	private final Type type;
	private final Record related;
	private boolean auto; // auto increment type field
	private int pos; // bit position within the parent record (bits for BOOLEAN / NULL_BOOLEAN fields)
	private Index index = null; // possible index on related record
	private List<String> values;
	private boolean key;
	private boolean mandatory = false;
	private String defaultValue;
	private String when = null; // indicate that this field is optional given that the condition field is the given value
	private boolean condition = false; // indicate that this field is a condition to other fields (for now only ENUMERATE type allowed)

	public Field(Record table, String name, Type type, String... values) {
		if (type == Type.RELATION || type == Type.ARRAY || type == Type.SUB)
			throw new GenerateException("Needs a related table on " + type);
		if (type == Type.SET)
			throw new GenerateException("Needs a related table and index fields on SET");
		this.table = table;
		this.name = name;
		this.type = type;
		this.related = null;
		if (type == Type.ENUMERATE) {
			this.values = Arrays.asList(values);
		} else if (values.length != 0) {
			throw new GenerateException("Did not expect values on a non ENUMERATE field");
		}
		this.pos = table.getPos(this);
	}

	public Field(Record table, String name, Type type, Record related) {
		this.table = table;
		this.name = name;
		this.type = type;
		this.related = related;
		this.pos = table.getPos(this);
	}

	public Field(Record table, String name, Record related, String... fields) {
		this.table = table;
		this.name = name;
		this.type = Type.SET;
		this.related = related;
		this.pos = table.getPos(this);
		this.index = related.index(table, name, this.pos >> 3, fields);
	}

	public Record getParent() {
		return table;
	}

	public String getName() {
		return name;
	}

	public String getUpperName() {
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	public Type getType() {
		return type;
	}

	public String getWhen() {
		return when;
	}

	public Field auto() {
		this.auto = true;
		return this;
	}

	public boolean isAuto() {
		return auto;
	}

	public Field mandatory() {
		mandatory = true;
		return this;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public Field defaultValue(String string) {
		defaultValue = string;
		return this;
	}

	public int getPos() {
		return pos;
	}

	public Index getIndex() {
		return index;
	}

	public Field when(String whenValue) {
		Field cond = table.getCondition();
		if (cond == null)
			throw new GenerateException("Define a condition field first");
		if (!cond.values.contains(whenValue))
			throw new GenerateException("Unknown ENUMERATE value '" + whenValue + "' on condition field '" + cond.name + "'");
		this.when = whenValue;
		Field last = table.getFields().get(table.getFields().size() - 2);
		if (!whenValue.equals(last.when)) {
			pos = table.getConditionSize() * 8;
			if (type == Type.SET)
				index.setParentPos(table.getConditionSize());
			table.setSize(table.getConditionSize() + (getSize() + 7) / 8);
			table.getFreeBits().clear();
		}
		return this;
	}

	public Field condition() {
		if (table.getCondition() != null)
			throw new GenerateException("Cannot define a second condition field");
		if (type != Type.ENUMERATE)
			throw new GenerateException("Condition field should be of ENUMERATE type");
		condition = true;
		table.setConditionSize(table.getSize());
		return this;
	}

	public String getGetterType() {
		if (type == Type.RELATION)
			return getJavaType();
		return null;
	}

	public String getWhenCond() {
		return when;
	}

	public int getSize() {
		switch (type) {
		case ENUMERATE:
			if (values == null)
				return -1;
			int s = values.size() + 1;
			int b = 0;
			while (s > 0) {
				b++;
				s >>= 1;
			}
			return b > 8 ? 16 : b;
		case BOOLEAN:
			return 1;
		case NULL_BOOLEAN:
			return 2;
		case BYTE:
			return 8;
		case SHORT:
			return 16;
		case OBJECT:
		case INTEGER:
		case RELATION:
		case SET:
		case STRING:
		case ARRAY:
		case ELEMENT:
			return 32;
		case DATE:
		case FLOAT:
		case LONG:
		case STRING_POINTER:
			return 64;
		default:
			throw new GenerateException("Unknown type");
		}
	}

	public String getJavaType() {
		switch (type) {
		case ARRAY:
			return name.substring(0, 1).toUpperCase() + name.substring(1) + "Array";
		case BOOLEAN:
			return "boolean";
		case DATE:
			return "LocalDateTime";
		case ENUMERATE:
			return (table.isFull() ? table.getName() + "." : "") + name.substring(0, 1).toUpperCase() + name.substring(1);
		case FLOAT:
			return "double";
		case BYTE:
			return "byte";
		case SHORT:
			return "short";
		case INTEGER:
			return "int";
		case NULL_BOOLEAN:
			return "Boolean";
		case LONG:
			return "long";
		case RELATION:
		case OBJECT:
			return related.getName();
		case SET:
			return "Index" + name.substring(0, 1).toUpperCase() + name.substring(1);
		case STRING:
		case STRING_POINTER:
			return "String";
		case ELEMENT:
			return related.getName() + "Array";
		default:
			throw new GenerateException("Unknown type");
		}
	}

	public String getBoxedType() {
		switch (type) {
		case ARRAY:
			return name.substring(0, 1).toUpperCase() + name.substring(1) + "Array";
		case BOOLEAN:
			return "Boolean";
		case DATE:
			return "LocalDateTime";
		case ENUMERATE:
			return (table.isFull() ? table.getName() + "." : "") + name.substring(0, 1).toUpperCase() + name.substring(1);
		case FLOAT:
			return "Double";
		case BYTE:
			return "Byte";
		case SHORT:
			return "Short";
		case INTEGER:
			return "Integer";
		case NULL_BOOLEAN:
			return "Boolean";
		case LONG:
			return "Long";
		case RELATION:
		case OBJECT:
			return related.getName();
		case SET:
			return "Index" + name.substring(0, 1).toUpperCase() + name.substring(1);
		case STRING:
		case STRING_POINTER:
			return "String";
		case ELEMENT:
			return related.getName() + "Array";
		default:
			throw new GenerateException("Unknown type");
		}
	}

	public String getSetType() {
		if (type == Type.STRING_POINTER)
			return "StringPointer";
		return getJavaType();
	}

	public String getDefault() {
		if (defaultValue != null) {
			switch (type) {
			case BYTE:
				return "(byte) " + defaultValue;
			case SHORT:
				return "(short) " + defaultValue;
			case LONG:
				return defaultValue + "L";
			case STRING:
				return "\"" + defaultValue.replaceAll("\"", "\\\"") + "\"";
			case ENUMERATE:
				return (table.isFull() ? table.getName() + "." : "") + getUpperName() + "." + defaultValue;
			default:
				return defaultValue;
			}
		}
		switch (type) {
		case BOOLEAN:
			return "false";
		case BYTE:
			return "(byte) 0";
		case FLOAT:
			return "0.0";
		case SHORT:
			return "(short) 0";
		case INTEGER:
			return "0";
		case LONG:
			return "0L";
		case ARRAY:
		case SET:
			return null;
		case ENUMERATE:
			return mandatory ? getUpperName() + "." + values.get(0) : "null";
		default:
			return "null";
		}
	}

	public String getGetter() {
		if (!table.isFull())
			return getGetter(table);
		switch (type) {
		case ARRAY:
			return "return new " + getUpperName() + "Array(this, -1);";
		case BOOLEAN:
			return "return rec == 0 ? false : (store.getByte(rec, " + (pos >> 3) + ") & " + (1 << (pos & 7)) + ") > 0;";
		case BYTE:
			return "return rec == 0 ? 0 : store.getByte(rec, " + (pos >> 3) + ");";
		case DATE:
			return "return rec == 0 ? null : DateTime.of(store.getLong(rec, " + (pos >> 3) + "));";
		case ENUMERATE:
			return "int data = rec == 0 ? 0 : " + getEnum(false) + ";\n" //
					+ "		if (data <= 0 || data > " + getUpperName() + ".values().length)\n" //
					+ "			return null;\n" //
					+ "		return " + getUpperName() + ".values()[data - 1];";
		case FLOAT:
			return "return rec == 0 ? Double.NaN : Double.longBitsToDouble(store.getLong(rec, " + (pos >> 3) + "));";
		case SHORT:
			return "return rec == 0 ? Short.MIN_VALUE : store.getShort(rec, " + (pos >> 3) + ");";
		case INTEGER:
			return "return rec == 0 ? Integer.MIN_VALUE : store.getInt(rec, " + (pos >> 3) + ");";
		case LONG:
			return "return rec == 0 ? Long.MIN_VALUE : store.getLong(rec, " + (pos >> 3) + ");";
		case NULL_BOOLEAN:
			return "int data = store.getByte(rec, " + (pos >> 3) + ") & " + (3 << (pos & 7)) + ";\n" //
					+ "		return data == 0 ? null : data == " + (3 << (pos & 7));
		case RELATION:
			StringBuilder extra = new StringBuilder();
			if (name.equals("upRecord") && !table.getParent().isFull())
				createUpRecord(extra);
			else
				extra.append("return new " + related.getName() + "(store, rec == 0 ? 0 : store.getInt(rec, " + (pos >> 3) + "));");
			return extra.toString();
		case SET:
			return "return new Index" + getUpperName() + "(new " + related.getName() + "(store));";
		case STRING:
			return "return rec == 0 ? null : store.getString(store.getInt(rec, " + (pos >> 3) + "));";
		case OBJECT:
		case ELEMENT:
			return "return new " + related.getName() + "(store, rec == 0 ? 0 : store.getInt(rec, " + (pos >> 3) + "));";
		case STRING_POINTER:
			return "return rec == 0 ? null : store.getString(store.getInt(rec, " + (pos >> 3) + "), store.getInt(rec, " + (4 + (pos >> 3)) + "));";
		default:
			throw new GenerateException("Unknown type: " + type);
		}
	}

	private void createUpRecord(StringBuilder extra) {
		extra.append("if (rec == 0)\n");
		extra.append("\t\t\treturn null;\n");
		extra.append("\t\tswitch (store.getByte(rec, " + (4 + (pos >> 3)) + ")) {\n");
		int nr = 1;
		for (Record included : table.getParent().getIncluded()) {
			if (included.isFull()) {
				extra.append("\t\tcase ").append(nr++).append(":\n");
				extra.append("\t\t\treturn new ").append(included.getName()).append("(store, store.getInt(rec, ").append(pos >> 3).append("));\n");
			} else
				for (Field link : included.getLinked()) {
					extra.append("\t\tcase ").append(nr++).append(":\n");
					extra.append("\t\t\treturn new ").append(link.getUpperName()).append("Array").append("(store, store.getInt(rec, ").append(pos >> 3)
							.append("), store.getInt(rec, ").append(5 + (pos >> 3)).append("));\n");
				}
		}
		extra.append("\t\tdefault:\n");
		extra.append("\t\t\treturn null;\n");
		extra.append("\t\t}");
	}

	public String getGetter(Record array) {
		boolean included = !table.getIncluded().isEmpty();
		String test = included ? "getRec() == 0" : "alloc == 0 || idx < 0 || idx >= size";
		if (when != null)
			test = "get" + table.getCondition().getUpperName() + "() != " + table.getCondition().getUpperName() + "." + when;
		// keep an integer of room for the size of the array block
		String apos = included ? "getRec(), " + table.getName().toLowerCase() + "Position() + " + (pos >> 3)
				: "alloc, idx * " + table.getSize() + " + " + (array.reserve() + (pos >> 3));
		String store = included ? "getStore()" : "store";
		switch (type) {
		case ARRAY:
			return "return " + test + " ? null : new " + name.substring(0, 1).toUpperCase() + name.substring(1) + "Array(this, -1);";
		case BOOLEAN:
			return "return " + test + " ? false : (" + store + ".getByte(" + apos + ") & " + (1 << (pos & 7)) + ") > 0;";
		case BYTE:
			return "return " + test + " ? 0 : " + store + ".getByte(" + apos + ");";
		case DATE:
			return "return " + test + " ? null : DateTime.of(" + store + ".getLong(" + apos + "));";
		case ENUMERATE:
			return "int data = " + test + " ? 0 : " + getEnum(true) + ";\n" //
					+ "		if (data <= 0)\n" //
					+ "			return null;\n" //
					+ "		return " + getUpperName() + ".values()[data - 1];";
		case FLOAT:
			return "return " + test + " ? Double.NaN : Double.longBitsToDouble(" + store + ".getLong(" + apos + "));";
		case SHORT:
			return "return " + test + " ? Short.MIN_VALUE : " + store + ".getShort(" + apos + ");";
		case INTEGER:
			return "return " + test + " ? Integer.MIN_VALUE : " + store + ".getInt(" + apos + ");";
		case LONG:
			return "return " + test + " ? Long.MIN_VALUE : " + store + ".getLong(" + apos + ");";
		case NULL_BOOLEAN:
			return "int data = " + store + ".getByte(" + apos + ") & " + (3 << (pos & 7)) + ";\n" //
					+ "		return data == 0 ? null : data == " + (3 << (pos & 7));
		case RELATION:
		case OBJECT:
			return "return new " + related.getName() + "(" + store + ", " + test + " ? 0 : " + store + ".getInt(" + apos + "));";
		case SET:
			return "return " + test + " ? null : new Index" + getUpperName() + "(this, new " + related.getName() + "(" + store + "));";
		case STRING:
			return "return " + test + " ? null : " + store + ".getString(" + store + ".getInt(" + apos + "));";
		case STRING_POINTER:
			return "return " + test + " ? null : " + store + ".getString(store.getInt(" + apos + "), " + store + ".getInt(rec, alloc, idx * " + table.getSize() + " + "
					+ (8 + (pos >> 3)) + "));";
		default:
			throw new GenerateException("Unknown type: '" + type + "'");
		}
	}

	private String getEnum(boolean array) {
		boolean included = !table.getIncluded().isEmpty();
		String store = included ? "getStore()" : "store";
		String code = "(rec, ";
		if (included)
			code = "(getRec(), " + table.getName().toLowerCase() + "Position() + ";
		else if (array)
			code += "idx * " + table.getSize() + " + ";
		int b = getSize();
		if (b == 16)
			return store + ".getShort" + code + (pos >> 3) + ")";
		if (b == 8)
			return store + ".getByte" + code + (pos >> 3) + ")";
		if ((pos & 7) == 0)
			return store + ".getByte" + code + (pos >> 3) + ") & " + ((2 << b) - 1);
		return "(" + store + ".getByte" + code + (pos >> 3) + ") >> " + (pos & 7) + ") & " + ((2 << b) - 1);
	}

	public String getSetter() {
		if (!table.isFull())
			return getSetter(table);
		StringBuilder test = new StringBuilder();
		String end = ";";
		if (when != null) {
			Field cond = table.getCondition();
			test.append("if (get" + cond.getUpperName() + "() == " + cond.getUpperName() + "." + when + ") {\n\t\t\t");
			end = ";\n\t\t}";
		}
		switch (type) {
		case ARRAY:
			return null;
		case BOOLEAN:
			return test + "store.setByte(rec, " + (pos >> 3) + ", (store.getByte(rec, " + (pos >> 3) + ") & " + (255 - (1 << (pos & 7))) + ") + (value ? " + (1 << (pos & 7))
					+ " : 0))" + end;
		case BYTE:
			return test + "store.setByte(rec, " + (pos >> 3) + ", value)" + end;
		case DATE:
			return test + (mandatory ? "if (value == null)\n" //
					+ "			throw new MutationException(\"Mandatory '" + name + "' field\");\n\t\t" : "") //
					+ "store.setLong(rec, " + (pos >> 3) + ", value == null ? 0 : DateTime.getLong(value))" + end;
		case ENUMERATE:
			return test + "if (value == null)\n" //
					+ (mandatory ? "\t\t\t" + setEnum("store.", false, "0") + ";\n" : "\t\t\tthrow new MutationException(\"Mandatory '" + name + "' field\");\n") //
					+ "\t\t" + setEnum("store.", false, "1 + value.ordinal()") + end;
		case FLOAT:
			return test + (mandatory ? "if (value == Integer.MIN_VALUE)\n" //
					+ "			throw new MutationException(\"Mandatory '" + name + "' field\");\n" : "") //
					+ "store.setLong(rec, " + (pos >> 3) + ", Double.doubleToLongBits(value))" + end;
		case SHORT:
			return test + "store.setShort(rec, " + (pos >> 3) + ", value)" + end;
		case INTEGER:
			return test + (mandatory ? "if (value == Integer.MIN_VALUE)\n" //
					+ "			throw new MutationException(\"Mandatory '" + name + "' field\");\n\t\t" : "") //
					+ "store.setInt(rec, " + (pos >> 3) + ", value)" + end;
		case LONG:
			return test + "store.setLong(rec, " + (pos >> 3) + ", value)" + end;
		case NULL_BOOLEAN:
			return test + (mandatory ? "if (value == null)\n" //
					+ "			throw new MutationException(\"Mandatory '" + name + "' field\");\n" : "") //
					+ "store.setByte(rec, " + (pos >> 3) + ", (store.getByte(rec, " + (pos >> 3) + ") & " + (255 - (3 << (pos & 7))) + ") + (value == null ? 0 : value ? "
					+ (3 << (pos & 7)) + " : " + (1 << (pos & 7)) + "))" + end;
		case RELATION:
			StringBuilder extra = new StringBuilder();
			extra.append(end);
			if (name.equals("upRecord") && !table.getParent().isFull())
				changeUpRecord(extra);
			return test + "store.setInt(rec, " + (pos >> 3) + ", value == null ? 0 : value.getRec())" + extra;
		case OBJECT:
			return test + "store.setInt(rec, " + (pos >> 3) + ", value == null ? 0 : value.getRec())" + end;
		case SET:
			return null;
		case STRING:
			return test + "store.setInt(rec, " + (pos >> 3) + ", store.putString(value))" + end;
		case STRING_POINTER:
			return test + "store.setInt(rec, " + (pos >> 3) + ", value.getPos()); store.setInt(rec, " + (4 + (pos >> 3)) + ", value.getLength())" + end;
		default:
			throw new GenerateException("Unknown type: '" + type + "'");
		}
	}

	private void changeUpRecord(StringBuilder extra) {
		extra.append("\n");
		extra.append("\t\tstore.setInt(rec, " + (5 + (pos >> 3)) + ", value == null ? 0 : value.getArrayIndex());\n");
		extra.append("\t\tbyte type = 0;\n");
		int nr = 1;
		for (Record included : table.getParent().getIncluded()) {
			if (included.isFull()) {
				extra.append("\t\tif (value instanceof ").append(included.getName()).append(")\n");
				extra.append("\t\t\ttype = ").append(nr++).append(";\n");
			} else
				for (Field link : included.getLinked()) {
					extra.append("\t\tif (value instanceof ").append(link.getUpperName()).append("Array)\n");
					extra.append("\t\t\ttype = ").append(nr++).append(";\n");
				}
		}
		extra.append("\t\tstore.setByte(rec, " + (4 + (pos >> 3)) + ", type);");
	}

	public String getSetter(Record array) {
		boolean included = !table.getIncluded().isEmpty();
		String defaults = gatherDefaults(included);
		String store = included ? "getStore()" : "store";
		StringBuilder test = new StringBuilder();
		if (mandatory) {
			test.append("if (value == null)\n");
			test.append("			throw new MutationException(\"Mandatory '" + name + "' field\");\n\t\t");
		}
		if (when != null) {
			Field cond = table.getCondition();
			test.append("if (get" + cond.getUpperName() + "() == " + cond.getUpperName() + "." + when + ") {\n\t\t\t");
		} else if (included)
			test.append("if (getRec() != 0) {\n\t\t\t");
		else
			test.append("if (alloc != 0 && idx >= 0 && idx < size) {\n\t\t\t");
		String end = ";\n\t\t}";
		// keep an integer of room for the size of the array block
		String apos = included ? "getRec(), " + table.getName().toLowerCase() + "Position() + " + (pos >> 3)
				: "alloc, idx * " + table.getSize() + " + " + (array.reserve() + (pos >> 3));
		switch (type) {
		case ARRAY:
			return null;
		case BOOLEAN:
			return test + store + ".setByte(" + apos + ", (" + store + ".getByte(" + apos + ") & " + (255 - (1 << (pos & 7))) + ") + (value ? " + (1 << (pos & 7)) + " : 0))" + end;
		case BYTE:
			return test + store + ".setByte(" + apos + ", value)" + end;
		case DATE:
			return test + store + ".setLong(" + apos + ", value == null ? 0 : DateTime.getLong(value))" + end;
		case ENUMERATE:
			if (mandatory)
				return test + setEnum(store + ".", true, "1 + value.ordinal()") + defaults + "\n\t\t}";
			return test + "if (value == null) {\n" //
					+ "\t\t\t\t" + setEnum(store + ".", true, "0") + ";\n" //
					+ "\t\t\t\treturn;\n" //
					+ "\t\t\t}\n" //
					+ "\t\t\t" + setEnum(store + ".", true, "1 + value.ordinal()") + defaults + "\n\t\t}";
		case FLOAT:
			return test + store + ".setLong(" + apos + ", Double.doubleToLongBits(value))" + end;
		case SHORT:
			return test + store + ".setShort(" + apos + ", value)" + end;
		case INTEGER:
			return test + store + ".setInt(" + apos + ", value)" + end;
		case LONG:
			return test + store + ".setLong(" + apos + ", value)" + end;
		case NULL_BOOLEAN:
			return test + store + ".setByte(" + apos + ", (" + store + ".getByte(" + apos + ") & " + (255 - (3 << (pos & 7))) + ") + (value == null ? 0 : value ? "
					+ (3 << (pos & 7)) + " : " + (1 << (pos & 7)) + "))" + end;
		case RELATION:
		case OBJECT:
			return test + store + ".setInt(" + apos + ", value == null ? 0 : value.getRec())" + end;
		case SET:
			return null;
		case STRING:
			return test + store + ".setInt(" + apos + ", " + store + ".putString(value))" + end;
		case STRING_POINTER:
			return store + ".setInt(" + apos + ", value.getPos()); " + store + ".setInt(alloc, idx * " + table.getSize() + " + " + (8 + (pos >> 3)) + ", value.getLength())" + end;
		default:
			throw new GenerateException("Unknown type: '" + type + "'");
		}
	}

	private String gatherDefaults(boolean included) {
		if (!condition)
			return ";";
		StringBuilder res = new StringBuilder();
		res.append(";\n			switch (value) {\n");
		String last = null;
		for (Field fld : table.getFields()) {
			if (fld.when == null)
				continue;
			if (!fld.when.equals(last)) {
				if (last != null)
					res.append("break;\n");
				res.append("\t\t\tcase " + fld.when + ":\n				");
			}
			String def = fld.getDefault();
			String apos = included ? "getRec(), " + table.getName().toLowerCase() + "Position() + " + (fld.getPos() >> 3)
					: "alloc, idx * " + table.getSize() + " + " + (4 + (fld.getPos() >> 3));
			String store = included ? "getStore()." : "store.";
			int fps = fld.getPos();
			switch (fld.getType()) {
			case ARRAY:
			case SET:
				res.append(store + "setInt(" + apos + ", 0)");
				break;
			case BOOLEAN:
				if (def.equals("false"))
					res.append(store + "setByte(" + apos + ", " + store + "getByte(" + apos + ") & " + (255 - (1 << (fps & 7))) + ")");
				else if (def.equals("true"))
					res.append(store + "setByte(" + apos + ", (" + store + "getByte(" + apos + ") & " + (255 - (1 << (fps & 7))) + ") + (" + (1 << (fps & 7)) + "))");
				else
					res.append(store + "setByte(" + apos + ", (" + store + "getByte(" + apos + ") & " + (255 - (1 << (fps & 7))) + ") + (" + def + " ? " + (1 << (fps & 7))
							+ " : 0))");
				break;
			case BYTE:
				res.append(store + "setByte(" + apos + ", " + def + ")");
				break;
			case DATE:
				res.append(store + "setLong(" + apos + ", value == null ? 0 : DateTime.getLong(" + def + "))");
				break;
			case ENUMERATE:
				if (def == null || def.equals("null")) {
					if (mandatory)
						res.append(fld.setEnum(store, true, "1"));
					else
						res.append(fld.setEnum(store, true, "0"));
				} else
					res.append(fld.setEnum(store, true, "1 + " + def + ".ordinal()"));
				break;
			case FLOAT:
				res.append(store + "setLong(" + apos + ", Double.doubleToLongBits(" + def + "))");
				break;
			case SHORT:
				res.append(store + "setShort(" + apos + ", " + def + ")");
				break;
			case INTEGER:
				res.append(store + "setInt(" + apos + ", " + def + ")");
				break;
			case LONG:
				res.append(store + "setLong(" + apos + ", " + def + ")");
				break;
			case NULL_BOOLEAN:
				res.append(store + "setByte(" + apos + ", (store.getByte(" + apos + ") & " + (255 - (3 << (fps & 7))) + ") + (value == null ? 0 : " + def + " ? " + (3 << (fps & 7))
						+ " : " + (1 << (pos & 7)) + "))");
				break;
			case OBJECT:
				res.append(store + "setInt(" + apos + ", 0)");
				break;
			case RELATION:
				if (def == null || def.equals("null"))
					res.append(store + "setInt(" + apos + ", 0)");
				else
					res.append(store + "setInt(" + apos + ", " + def + ".getRec())");
				break;
			case STRING:
				res.append(store + "setInt(" + apos + ", " + (def.equals("null") ? "0" : "store.putString(" + def + ")") + ")");
				break;
			case STRING_POINTER:
				res.append(store + "setInt(" + apos + ", " + def + ".getPos()); " + store + "setInt(alloc, idx * " + table.getSize() + " + " + (8 + (fps >> 3)) + ", " + def
						+ ".getLength())");
				break;
			default:
				break;
			}
			res.append(";\n\t\t\t\t");
			last = fld.when;
		}
		res.append("break;\n\t\t\tdefault:\n\t\t\t\tbreak;\n\t\t\t}");
		return res.toString();
	}

	private String setEnum(String store, boolean array, String value) {
		boolean incl = !store.equals("store.");
		String code = incl ? "(getRec(), " + table.getName().toLowerCase() + "Position() + " : "(rec, ";
		if (array && !incl)
			code += "idx * " + table.getSize() + " + ";
		int b = getSize();
		if (b == 16)
			return store + "setShort" + code + (pos >> 3) + ", " + value + ")";
		if (b == 8)
			return store + "setByte" + code + (pos >> 3) + ", " + value + ")";
		if ((pos & 7) == 0)
			return store + "setByte" + code + (pos >> 3) + ", (" + store + "getByte" + code + (pos >> 3) + ") & " + (255 - ((2 << b) - 1) << (pos & 7)) + ") + " + value + ")";
		return store + "setByte" + code + (pos >> 3) + ", (" + store + "getByte" + code + (pos >> 3) + ") & " + (255 - ((2 << b) - 1) << (pos & 7)) + ") + (" + value + " << "
				+ (pos & 7) + "))";
	}

	public Record getRelated() {
		return related;
	}

	public List<String> getValues() {
		return values;
	}

	public void setKey(boolean key) {
		this.key = key;
	}

	public boolean isKey() {
		return key;
	}

	@Override
	public String toString() {
		return type + (related != null ? "<" + related.getName() + ">" : "") + " " + table.getName() + "." + name;
	}

	public void dump(StringBuilder bld) {
		bld.append("  name=" + name + ", type=" + type);
		if (related != null)
			bld.append(", related=" + related.getName());
		if (auto)
			bld.append(", auto=true");
		bld.append(", pos=" + pos);
		if (key)
			bld.append(", key=true");
		if (mandatory)
			bld.append(", mandatory=true");
		if (condition)
			bld.append(", condition=true");
		if (when != null)
			bld.append(", when=" + when);
		bld.append("\n");
	}

	public String getArrayFields() {
		return related.getArrayFields(table);
	}

	public boolean isCondition() {
		return condition;
	}
}
