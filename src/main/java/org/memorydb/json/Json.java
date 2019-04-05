package org.memorydb.json;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.structure.RecordData;
import org.memorydb.structure.Store;

/**
 * Automatically generated record class for table Json
 */
@RecordData(name = "Json")
public class Json implements Part {
	/* package private */ final Store store;
	protected final int rec;
	/* package private */ static final int RECORD_SIZE = 13;

	public Json(Store store, int rec) {
		rec = store.correct(rec);
		this.store = store;
		this.rec = rec;
	}

	@Override
	public int rec() {
		return rec;
	}

	@Override
	public Json copy(int newRec) {
		assert store.validate(newRec);
		return new Json(store, newRec);
	}

	@Override
	public Store store() {
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
	public void output(Write write, int iterate) {
		if (rec == 0 || iterate <= 0)
			return;
		outputPart(write, iterate);
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

	public static Json parse(Parser parser, Store store) {
		Json rec = null;
		while (parser.getSub()) {
			rec = parseKey(parser, store);
			if (parser.isDelete()) {
				if (rec != null)
					try (ChangeJson record = new ChangeJson(rec)) {
						store.free(record.rec());
					}
				continue;
			}
			if (rec == null) {
				try (ChangeJson record = new ChangeJson(store, 0)) {

					record.parseFields(parser);
					return record;
				}
			} else {
				try (ChangeJson record = new ChangeJson(rec)) {
					record.parseFields(parser);
				}
			}
		}
		return rec;
	}

	public static Json parseKey(Parser parser, Store store) {
		int nextRec = 0;
		parser.finishRelation();
		return nextRec <= 0 ? null : new Json(store, nextRec);
	}

	@Override
	public Json copy() {
		return new Json(store, rec);
	}
}
