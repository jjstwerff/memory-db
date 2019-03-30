package org.memorydb.jslt;

import java.io.IOException;

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
@RecordData(
	name = "Alternative",
	keyFields = {"nr"})
public class Alternative implements MemoryRecord, RecordInterface {
	/* package private */ Store store;
	protected int rec;
	/* package private */ static final int RECORD_SIZE = 34;

	public Alternative(Store store) {
		this.store = store;
		this.rec = 0;
	}

	public Alternative(Store store, int rec) {
		rec = store.correct(rec);
		this.store = store;
		this.rec = rec;
	}

	@Override
	public int getRec() {
		return rec;
	}

	@Override
	public void setRec(int rec) {
		assert store.validate(rec);
		this.rec = rec;
	}

	@Override
	public Store getStore() {
		return store;
	}

	@Override
	public ChangeAlternative change() {
		return new ChangeAlternative(this);
	}

	@FieldData(
		name = "nr",
		type = "INTEGER",
		mandatory = false
	)
	public int getNr() {
		return rec == 0 ? Integer.MIN_VALUE : store.getInt(rec, 4);
	}

	@FieldData(
		name = "parameters",
		type = "ARRAY",
		related = ParametersArray.class,
		mandatory = false
	)
	public ParametersArray getParameters() {
		return new ParametersArray(this, -1);
	}

	public ParametersArray getParameters(int index) {
		return new ParametersArray(this, index);
	}

	public ParametersArray addParameters() {
		return getParameters().add();
	}

	@FieldData(
		name = "anyParm",
		type = "BOOLEAN",
		mandatory = false
	)
	public boolean isAnyParm() {
		return rec == 0 ? false : (store.getByte(rec, 12) & 1) > 0;
	}

	@FieldData(
		name = "if",
		type = "OBJECT",
		related = Expr.class,
		mandatory = false
	)
	public Expr getIf() {
		return new Expr(store, rec == 0 ? 0 : store.getInt(rec, 13));
	}

	@FieldData(
		name = "code",
		type = "ARRAY",
		related = CodeArray.class,
		mandatory = false
	)
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
	@FieldData(
		name = "upRecord",
		type = "RELATION",
		related = Macro.class,
		mandatory = false
	)
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
		if (fldIf != null && fldIf.getRec() != 0) {
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
	public String keys() throws IOException {
		StringBuilder res = new StringBuilder();
		if (rec == 0)
			return "";
		res.append("Macro").append("{").append(getUpRecord().keys()).append("}");
		res.append(", ");
		res.append("Nr").append("=").append(getNr());
		return res.toString();
	}

	@Override
	public String toString() {
		Write write = new Write(new StringBuilder());
		try {
			output(write, 4);
		} catch (IOException e) {
			return "";
		}
		return write.toString();
	}

	public Alternative parse(Parser parser, Macro parent) {
		while (parser.getSub()) {
			int nr = parser.getInt("nr");
			int nextRec = parent.new IndexAlternatives(this, nr).search();
			if (parser.isDelete(nextRec)) {
				try (ChangeAlternative record = new ChangeAlternative(this)) {
					store.free(record.getRec());
					record.setRec(0);
				}
				continue;
			}
			if (nextRec == 0) {
				try (ChangeAlternative record = new ChangeAlternative(parent, 0)) {
					record.setNr(nr);
					record.parseFields(parser);
					rec = record.rec;
				}
			} else {
				rec = nextRec;
				try (ChangeAlternative record = new ChangeAlternative(this)) {
					record.parseFields(parser);
				}
			}
		}
		return this;
	}

	public boolean parseKey(Parser parser) {
		Macro parent = getUpRecord();
		int nr = parser.getInt("nr");
		int nextRec = parent.new IndexAlternatives(this, nr).search();
		parser.finishRelation();
		if (nextRec != 0)
			rec = nextRec;
		return nextRec != 0;
	}

	@Override
	public Object get(int field) {
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
	public Iterable<? extends RecordInterface> iterate(int field, Object... key) {
		switch (field) {
		case 2:
			return getParameters();
		case 5:
			return getCode();
		default:
			return null;
		}
	}

	@Override
	public FieldType type(int field) {
		switch (field) {
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
	public String name(int field) {
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
	public boolean exists() {
		return getRec() != 0;
	}
}
