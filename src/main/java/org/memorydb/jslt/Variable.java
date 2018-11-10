package org.memorydb.jslt;

import java.io.IOException;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.structure.FieldData;
import org.memorydb.structure.RecordData;
import org.memorydb.structure.RecordInterface;
import org.memorydb.structure.Store;

/**
 * Automatically generated record class for table Variable
 */
@RecordData(
	name = "Variable",
	keyFields = {})
public class Variable implements ResultType {
	/* package private */ Store store;
	protected int rec;
	/* package private */ static final int RECORD_SIZE = 19;

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
	public int resulttypePosition() {
		return 14;
	}

	@Override
	public ChangeVariable change() {
		return new ChangeVariable(this);
	}

	@FieldData(
		name = "name",
		type = "STRING",
		mandatory = true
	)
	public String getName() {
		return rec == 0 ? null : store.getString(store.getInt(rec, 4));
	}

	@FieldData(
		name = "nr",
		type = "INTEGER",
		mandatory = true
	)
	public int getNr() {
		return rec == 0 ? Integer.MIN_VALUE : store.getInt(rec, 8);
	}

	@FieldData(
		name = "eager",
		type = "BOOLEAN",
		mandatory = false
	)
	public boolean isEager() {
		return rec == 0 ? false : (store.getByte(rec, 12) & 1) > 0;
	}

	@FieldData(
		name = "extension",
		type = "BOOLEAN",
		mandatory = false
	)
	public boolean isExtension() {
		return rec == 0 ? false : (store.getByte(rec, 13) & 1) > 0;
	}

	@Override
	public void output(Write write, int iterate) throws IOException {
		if (rec == 0 || iterate <= 0)
			return;
		write.field("name", getName(), true);
		write.field("nr", getNr(), false);
		write.field("eager", isEager(), false);
		write.field("extension", isExtension(), false);
		outputResultType(write, iterate, false);
		write.endRecord();
	}

	@Override
	public String keys() throws IOException {
		StringBuilder res = new StringBuilder();
		if (rec == 0)
			return "";
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

	public Variable parse(Parser parser) {
		while (parser.getSub()) {
			int nextRec = 0;
			if (parser.isDelete(nextRec)) {
				try (ChangeVariable record = new ChangeVariable(this)) {
					store.free(record.getRec());
					record.setRec(0);
				}
				continue;
			}
			if (nextRec == 0) {
				try (ChangeVariable record = new ChangeVariable(store)) {

					record.parseFields(parser);
					rec = record.rec;
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
		rec = nextRec;
		return nextRec != 0;
	}

	@Override
	public Object get(int field) {
		if (field >= 4 && field <= 6)
			return ResultType.super.getResultType(field - 4);
		switch (field) {
		case 1:
			return getName();
		case 2:
			return getNr();
		case 3:
			return isEager();
		case 4:
			return isExtension();
		default:
			return null;
		}
	}

	@Override
	public Iterable<? extends RecordInterface> iterate(int field, Object... key) {
		if (field >= 4 && field <= 6)
			return ResultType.super.iterateResultType(field - 4);
		switch (field) {
		default:
			return null;
		}
	}

	@Override
	public FieldType type(int field) {
		if (field >= 4 && field <= 6)
			return ResultType.super.typeResultType(field - 4);
		switch (field) {
		case 1:
			return FieldType.STRING;
		case 2:
			return FieldType.INTEGER;
		case 3:
			return FieldType.BOOLEAN;
		case 4:
			return FieldType.BOOLEAN;
		default:
			return null;
		}
	}

	@Override
	public String name(int field) {
		if (field >= 4 && field <= 6)
			return ResultType.super.nameResultType(field - 4);
		switch (field) {
		case 1:
			return "name";
		case 2:
			return "nr";
		case 3:
			return "eager";
		case 4:
			return "extension";
		default:
			return null;
		}
	}

	@Override
	public boolean exists() {
		return getRec() != 0;
	}
}
