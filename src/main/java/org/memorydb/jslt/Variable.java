package org.memorydb.jslt;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.structure.FieldData;
import org.memorydb.structure.RecordData;
import org.memorydb.structure.Store;

/**
 * Automatically generated record class for table Variable
 */
@RecordData(name = "Variable")
public class Variable implements ResultType {
	/* package private */ final Store store;
	protected final int rec;
	private final int field;
	/* package private */ static final int RECORD_SIZE = 18;

	public Variable(Store store, int rec) {
		rec = store.correct(rec);
		this.store = store;
		this.rec = rec;
		this.field = 0;
	}

	public Variable(Store store, int rec, int field) {
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
	public Variable copy(int newRec) {
		assert store.validate(newRec);
		return new Variable(store, newRec);
	}

	@Override
	public Store store() {
		return store;
	}

	@Override
	public int resulttypePosition() {
		return 13;
	}

	@Override
	public ChangeVariable change() {
		return new ChangeVariable(this);
	}

	@FieldData(name = "name", type = "STRING", mandatory = true)
	public String getName() {
		return rec == 0 ? null : store.getString(store.getInt(rec, 4));
	}

	@FieldData(name = "nr", type = "INTEGER", mandatory = false)
	public int getNr() {
		return rec == 0 ? Integer.MIN_VALUE : store.getInt(rec, 8);
	}

	@FieldData(name = "multiple", type = "BOOLEAN", mandatory = false)
	public boolean isMultiple() {
		return rec == 0 ? false : (store.getByte(rec, 12) & 1) > 0;
	}

	@Override
	public void output(Write write, int iterate) {
		if (rec == 0 || iterate <= 0)
			return;
		write.field("name", getName());
		write.field("nr", getNr());
		write.field("multiple", isMultiple());
		outputResultType(write, iterate);
		write.endRecord();
	}

	@Override
	public String keys() {
		StringBuilder res = new StringBuilder();
		if (rec == 0)
			return "";
		return res.toString();
	}

	@Override
	public String toString() {
		Write write = new Write(new StringBuilder());
		output(write, 4);
		return write.toString();
	}

	public static Variable parse(Parser parser, Store store) {
		Variable rec = null;
		while (parser.getSub()) {
			rec = parseKey(parser, store);
			if (parser.isDelete()) {
				if (rec != null) {
					ChangeVariable record = new ChangeVariable(rec);
					store.free(record.rec());
				}
				continue;
			}
			if (rec == null) {
				ChangeVariable record = new ChangeVariable(store, 0);

				record.parseFields(parser);
				return record;
			} else {
				ChangeVariable record = new ChangeVariable(rec);
				record.parseFields(parser);
			}
		}
		return rec;
	}

	public static Variable parseKey(Parser parser, Store store) {
		int nextRec = 0;
		parser.finishRelation();
		return nextRec <= 0 ? null : new Variable(store, nextRec);
	}

	@Override
	public Object java() {
		if (field > 3 && field <= 5)
			return ResultType.super.getResultType(field - 3);
		switch (field) {
		case 1:
			return getName();
		case 2:
			return getNr();
		case 3:
			return isMultiple();
		default:
			return null;
		}
	}

	@Override
	public FieldType type() {
		if (field > 3 && field <= 5)
			return ResultType.super.typeResultType(field - 3);
		switch (field) {
		case 0:
			return FieldType.OBJECT;
		case 1:
			return FieldType.STRING;
		case 2:
			return FieldType.INTEGER;
		case 3:
			return FieldType.BOOLEAN;
		default:
			return null;
		}
	}

	@Override
	public String name() {
		if (field > 3 && field <= 5)
			return ResultType.super.nameResultType(field - 3);
		switch (field) {
		case 1:
			return "name";
		case 2:
			return "nr";
		case 3:
			return "multiple";
		default:
			return null;
		}
	}

	@Override
	public Variable start() {
		return new Variable(store, rec, 1);
	}

	@Override
	public Variable next() {
		return field >= 5 ? null : new Variable(store, rec, field + 1);
	}

	@Override
	public Variable copy() {
		return new Variable(store, rec, field);
	}
}
