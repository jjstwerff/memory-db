package org.memorydb.json;

import java.io.IOException;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.structure.RecordData;
import org.memorydb.structure.RecordInterface;
import org.memorydb.structure.Store;

/**
 * Automatically generated record class for table Json
 */
@RecordData(
	name = "Json",
	keyFields = {})
public class Json implements Part {
	/* package private */ Store store;
	protected int rec;
	/* package private */ static final int RECORD_SIZE = 13;

	public Json(Store store) {
		this.store = store;
		this.rec = 0;
	}

	public Json(Store store, int rec) {
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
	public int partPosition() {
		return 4;
	}

	@Override
	public ChangeJson change() {
		return new ChangeJson(this);
	}

	@Override
	public void output(Write write, int iterate) throws IOException {
		if (rec == 0 || iterate <= 0)
			return;
		outputPart(write, iterate);
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

	public Json parse(Parser parser) {
		while (parser.getSub()) {
			int nextRec = 0;
			if (parser.isDelete(nextRec)) {
				try (ChangeJson record = new ChangeJson(this)) {
					store.free(record.getRec());
					record.setRec(0);
				}
				continue;
			}
			if (nextRec == 0) {
				try (ChangeJson record = new ChangeJson(store)) {

					record.parseFields(parser);
					rec = record.rec;
				}
			} else {
				rec = nextRec;
				try (ChangeJson record = new ChangeJson(this)) {
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
		return getPart(field);
	}

	@Override
	public Iterable<? extends RecordInterface> iterate(int field, Object... key) {
		return iteratePart(field, key);
	}

	@Override
	public FieldType type() {
		return typePart();
	}

	@Override
	public FieldType type(int field) {
		return typePart(field);
	}

	@Override
	public String name(int field) {
		return namePart(field);
	}

	@Override
	public boolean exists() {
		return getRec() != 0;
	}
}
