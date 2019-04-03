package org.memorydb.jslt;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.structure.RecordData;
import org.memorydb.structure.RecordInterface;
import org.memorydb.structure.Store;

/**
 * Automatically generated record class for table MatchObject
 */
@RecordData(name = "MatchObject")
public class MatchObject implements Match {
	/* package private */ final Store store;
	protected final int rec;
	/* package private */ static final int RECORD_SIZE = 17;

	public MatchObject(Store store) {
		this.store = store;
		this.rec = 0;
	}

	public MatchObject(Store store, int rec) {
		rec = store.correct(rec);
		this.store = store;
		this.rec = rec;
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

	public static void parse(Parser parser, Store store) {
		while (parser.getSub()) {
			int nextRec = 0;
			if (parser.isDelete(nextRec)) {
				try (ChangeMatchObject record = new ChangeMatchObject(store, nextRec)) {
					store.free(record.rec());
				}
				continue;
			}
			if (nextRec == 0) {
				try (ChangeMatchObject record = new ChangeMatchObject(store, 0)) {

					record.parseFields(parser);
				}
			} else {
				try (ChangeMatchObject record = new ChangeMatchObject(store, nextRec)) {
					record.parseFields(parser);
				}
			}
		}
	}

	@Override
	public MatchObject parseKey(Parser parser) {
		int nextRec = 0;
		parser.finishRelation();
		return nextRec <= 0 ? null : new MatchObject(store, nextRec);
	}

	@Override
	public Object java() {
		int field = 0;
		return Match.super.getMatch(field);
	}

	@Override
	public FieldType type() {
		int field = 0;
		return Match.super.typeMatch(field);
	}

	@Override
	public String name() {
		int field = 0;
		return Match.super.nameMatch(field);
	}

	@Override
	public MatchObject next() {
		return null;
	}

	@Override
	public MatchObject copy() {
		return new MatchObject(store, rec);
	}
}
