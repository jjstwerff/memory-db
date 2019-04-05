package org.memorydb.jslt;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.structure.FieldData;
import org.memorydb.structure.MemoryRecord;
import org.memorydb.structure.RecordData;
import org.memorydb.structure.RecordInterface;
import org.memorydb.structure.Store;

/**
 * Automatically generated record class for table Alternative
 */
@RecordData(name = "Alternative")
public class Alternative implements MemoryRecord, RecordInterface {
	/* package private */ final Store store;
	protected final int rec;
	private final int field;
	/* package private */ static final int RECORD_SIZE = 34;

	public Alternative(Store store, int rec) {
		rec = store.correct(rec);
		this.store = store;
		this.rec = rec;
		this.field = 0;
	}

	public Alternative(Store store, int rec, int field) {
		rec = store.correct(rec);
		this.store = store;
		this.rec = rec;
		this.field = field;
	}

	@Override
	public int rec() {
		return rec;
	}

	@Override
	public Alternative copy(int newRec) {
		assert store.validate(newRec);
		return new Alternative(store, newRec);
	}

	@Override
	public Store store() {
		return store;
	}

	@Override
	public ChangeAlternative change() {
		return new ChangeAlternative(this);
	}

	@FieldData(name = "nr", type = "INTEGER", mandatory = false)
	public int getNr() {
		return rec == 0 ? Integer.MIN_VALUE : store.getInt(rec, 4);
	}

	@FieldData(name = "parameters", type = "ARRAY", related = ParametersArray.class, mandatory = false)
	public ParametersArray getParameters() {
		return new ParametersArray(this, -1);
	}

	public ParametersArray getParameters(int index) {
		return new ParametersArray(this, index);
	}

	public ParametersArray addParameters() {
		return getParameters().add();
	}

	@FieldData(name = "anyParm", type = "BOOLEAN", mandatory = false)
	public boolean isAnyParm() {
		return rec == 0 ? false : (store.getByte(rec, 12) & 1) > 0;
	}

	@FieldData(name = "if", type = "OBJECT", related = Expr.class, mandatory = false)
	public Expr getIf() {
		return new Expr(store, rec == 0 ? 0 : store.getInt(rec, 13));
	}

	@FieldData(name = "code", type = "ARRAY", related = CodeArray.class, mandatory = false)
	public CodeArray getCode() {
		return new CodeArray(this, -1);
	}

	public CodeArray getCode(int index) {
		return new CodeArray(this, index);
	}

	public CodeArray addCode() {
		return getCode().add();
	}

	@Override
	public Macro up() {
		return new Macro(store, rec == 0 ? 0 : store.getInt(rec, 30));
	}

	@Override
	public void output(Write write, int iterate) {
		if (rec == 0 || iterate <= 0)
			return;
		write.field("nr", getNr());
		ParametersArray fldParameters = getParameters();
		if (fldParameters != null) {
			write.sub("parameters");
			for (ParametersArray sub : fldParameters)
				sub.output(write, iterate);
			write.endSub();
		}
		write.field("anyParm", isAnyParm());
		Expr fldIf = getIf();
		if (fldIf != null && fldIf.rec() != 0) {
			write.sub("if");
			fldIf.output(write, iterate);
			write.endSub();
		}
		CodeArray fldCode = getCode();
		if (fldCode != null) {
			write.sub("code");
			for (CodeArray sub : fldCode)
				sub.output(write, iterate);
			write.endSub();
		}
		write.endRecord();
	}

	@Override
	public String keys() {
		StringBuilder res = new StringBuilder();
		if (rec == 0)
			return "";
		res.append("Macro").append("{").append(up().keys()).append("}");
		res.append(", ");
		res.append("Nr").append("=").append(getNr());
		return res.toString();
	}

	@Override
	public String toString() {
		Write write = new Write(new StringBuilder());
		output(write, 4);
		return write.toString();
	}

	public static Alternative parse(Parser parser, Macro parent) {
		Alternative rec = null;
		while (parser.getSub()) {
			rec = parseKey(parser, parent);
			if (parser.isDelete()) {
				if (rec != null)
					try (ChangeAlternative record = new ChangeAlternative(rec)) {
						parent.store.free(record.rec());
					}
				continue;
			}
			if (rec == null) {
				try (ChangeAlternative record = new ChangeAlternative(parent, 0)) {
					int nr = parser.getInt("nr");
					record.setNr(nr);
					record.parseFields(parser);
					return record;
				}
			} else {
				try (ChangeAlternative record = new ChangeAlternative(rec)) {
					record.parseFields(parser);
				}
			}
		}
		return rec;
	}

	public static Alternative parseKey(Parser parser, Macro parent) {
		int nr = parser.getInt("nr");
		int nextRec = parent.new IndexAlternatives(nr).search();
		parser.finishRelation();
		return nextRec <= 0 ? null : new Alternative(parent.store(), nextRec);
	}

	@Override
	public Object java() {
		switch (field) {
		case 1:
			return getNr();
		case 3:
			return isAnyParm();
		case 4:
			return getIf();
		default:
			return null;
		}
	}

	@Override
	public FieldType type() {
		switch (field) {
		case 0:
			return FieldType.OBJECT;
		case 1:
			return FieldType.INTEGER;
		case 2:
			return FieldType.ARRAY;
		case 3:
			return FieldType.BOOLEAN;
		case 4:
			return FieldType.OBJECT;
		case 5:
			return FieldType.ARRAY;
		default:
			return null;
		}
	}

	@Override
	public String name() {
		switch (field) {
		case 1:
			return "nr";
		case 2:
			return "parameters";
		case 3:
			return "anyParm";
		case 4:
			return "if";
		case 5:
			return "code";
		default:
			return null;
		}
	}

	@Override
	public Alternative start() {
		return new Alternative(store, rec, 1);
	}

	@Override
	public Alternative next() {
		return field >= 5 ? null : new Alternative(store, rec, field + 1);
	}

	@Override
	public Alternative copy() {
		return new Alternative(store, rec, field);
	}
}
