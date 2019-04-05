package org.memorydb.jslt;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.structure.RecordData;
import org.memorydb.structure.Store;

/**
 * Automatically generated record class for table MatchObject
 */
@RecordData(name = "MatchObject")
public class MatchObject implements Match {
	/* package private */ final Store store;
	protected final int rec;
	private final int field;
	/* package private */ static final int RECORD_SIZE = 17;

	public MatchObject(Store store, int rec) {
		rec = store.correct(rec);
		this.store = store;
		this.rec = rec;
		this.field = 0;
	}

	public MatchObject(Store store, int rec, int field) {
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
	public MatchObject copy(int newRec) {
		assert store.validate(newRec);
		return new MatchObject(store, newRec);
	}

	@Override
	public Store store() {
		return store;
	}

	@Override
	public int matchPosition() {
		return 4;
	}

	@Override
	public ChangeMatchObject change() {
		return new ChangeMatchObject(this);
	}

	@Override
	public void output(Write write, int iterate) {
		if (rec == 0 || iterate <= 0)
			return;
		outputMatch(write, iterate);
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

	public static MatchObject parse(Parser parser, Store store) {
		MatchObject rec = null;
		while (parser.getSub()) {
			rec = parseKey(parser, store);
			if (parser.isDelete()) {
				if (rec != null) {
					ChangeMatchObject record = new ChangeMatchObject(rec);
					store.free(record.rec());
				}
				continue;
			}
			if (rec == null) {
				ChangeMatchObject record = new ChangeMatchObject(store, 0);

				record.parseFields(parser);
				return record;
			} else {
				ChangeMatchObject record = new ChangeMatchObject(rec);
				record.parseFields(parser);
			}
		}
		return rec;
	}

	public static MatchObject parseKey(Parser parser, Store store) {
		int nextRec = 0;
		parser.finishRelation();
		return nextRec <= 0 ? null : new MatchObject(store, nextRec);
	}

	@Override
	public Object java() {
		return Match.super.getMatch(field);
	}

	@Override
	public FieldType type() {
		return Match.super.typeMatch(field);
	}

	@Override
	public String name() {
		return Match.super.nameMatch(field);
	}

	@Override
	public MatchObject start() {
		return new MatchObject(store, rec, 1);
	}

	@Override
	public MatchObject next() {
		return field >= 15 ? null : new MatchObject(store, rec, field + 1);
	}

	@Override
	public MatchObject copy() {
		return new MatchObject(store, rec, field);
	}
}
