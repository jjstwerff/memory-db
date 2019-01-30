package org.memorydb.jslt;

import java.io.IOException;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.structure.RecordData;
import org.memorydb.structure.RecordInterface;
import org.memorydb.structure.Store;

/**
 * Automatically generated record class for table MatchObject
 */
@RecordData(
	name = "MatchObject",
	keyFields = {})
public class MatchObject implements Match {
	/* package private */ Store store;
	protected int rec;
	/* package private */ static final int RECORD_SIZE = 13;

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
	public int matchPosition() {
		return 4;
	}

	@Override
	public ChangeMatchObject change() {
		return new ChangeMatchObject(this);
	}

	@Override
	public void output(Write write, int iterate) throws IOException {
		if (rec == 0 || iterate <= 0)
			return;
		outputMatch(write, iterate);
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

	public MatchObject parse(Parser parser) {
		while (parser.getSub()) {
			int nextRec = 0;
			if (parser.isDelete(nextRec)) {
				try (ChangeMatchObject record = new ChangeMatchObject(this)) {
					store.free(record.getRec());
					record.setRec(0);
				}
				continue;
			}
			if (nextRec == 0) {
				try (ChangeMatchObject record = new ChangeMatchObject(store)) {

					record.parseFields(parser);
					rec = record.rec;
				}
			} else {
				rec = nextRec;
				try (ChangeMatchObject record = new ChangeMatchObject(this)) {
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
	public Object get(int field) {
		if (field >= 0 && field <= 16)
			return Match.super.getMatch(field - 0);
		switch (field) {
		default:
			return null;
		}
	}

	@Override
	public Iterable<? extends RecordInterface> iterate(int field, Object... key) {
		if (field >= 0 && field <= 16)
			return Match.super.iterateMatch(field - 0);
		switch (field) {
		default:
			return null;
		}
	}

	@Override
	public FieldType type(int field) {
		if (field >= 0 && field <= 16)
			return Match.super.typeMatch(field - 0);
		switch (field) {
		default:
			return null;
		}
	}

	@Override
	public String name(int field) {
		if (field >= 0 && field <= 16)
			return Match.super.nameMatch(field - 0);
		switch (field) {
		default:
			return null;
		}
	}

	@Override
	public boolean exists() {
		return getRec() != 0;
	}
}
