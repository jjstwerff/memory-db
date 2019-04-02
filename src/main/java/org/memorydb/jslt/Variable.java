package org.memorydb.jslt;

import java.util.Iterator;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.structure.FieldData;
import org.memorydb.structure.RecordData;
import org.memorydb.structure.RecordInterface;
import org.memorydb.structure.Store;

/**
 * Automatically generated record class for table Variable
 */
@RecordData(name = "Variable")
public class Variable implements ResultType {
	/* package private */ final Store store;
	protected final int rec;
	/* package private */ static final int RECORD_SIZE = 18;

	public Variable(Store store) {
		this.store = store;
		this.rec = 0;
	}

	public Variable(Store store, int rec) {
		rec = store.correct(rec);
		this.store = store;
		this.rec = rec;
	}

	@Override
	public int rec() {
		return rec;
	}

	@Override
	public Variable copy(int newRec) {
		assert store.validate(rec);
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

	public Variable parse(Parser parser) {
		while (parser.getSub()) {
			int nextRec = 0;
			if (parser.isDelete(nextRec)) {
				try (ChangeVariable record = new ChangeVariable(this)) {
					store.free(record.rec());
				}
				continue;
			}
			if (nextRec == 0) {
				try (ChangeVariable record = new ChangeVariable(store)) {

					record.parseFields(parser);
				}
			} else {
				rec = nextRec;
				try (ChangeVariable record = new ChangeVariable(this)) {
					record.parseFields(parser);
				}
			}
		}
		return this;
	}

	@Override
	public boolean parseKey(Parser parser) {
		int nextRec = 0;
		parser.finishRelation();
		if (nextRec != 0)
			rec = nextRec;
		return nextRec != 0;
	}

	@Override
	public Object java() {
		int field = 0;
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

	public Iterable<? extends RecordInterface> iterate(int field, Object... key) {
		if (field >= 3 && field <= 5)
			return ResultType.super.iterateResultType(field - 3);
		switch (field) {
		default:
			return null;
		}
	}

	@Override
	public FieldType type() {
		int field = 0;
		if (field > 3 && field <= 5)
			return ResultType.super.typeResultType(field - 3);
		switch (field) {
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
		int field = 0;
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
	public Variable next() {
		return null;
	}

	@Override
	public Variable copy() {
		return new Variable(store, rec);
	}
}
