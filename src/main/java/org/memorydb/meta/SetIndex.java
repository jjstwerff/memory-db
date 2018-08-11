package org.memorydb.meta;

import java.io.IOException;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.structure.FieldData;
import org.memorydb.structure.MemoryRecord;
import org.memorydb.structure.RecordData;
import org.memorydb.structure.RecordInterface;
import org.memorydb.structure.Store;

/**
 * Automatically generated record class for table SetIndex
 */
@RecordData(
	name = "SetIndex",
	keyFields = {"index"})
public class SetIndex implements MemoryRecord, RecordInterface {
	/* package private */ Store store;
	protected int rec;
	/* package private */ static final int RECORD_SIZE = 21;

	public SetIndex(Store store) {
		this.store = store;
		this.rec = 0;
	}

	public SetIndex(Store store, int rec) {
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
	public ChangeSetIndex change() {
		return new ChangeSetIndex(this);
	}

	@FieldData(
		name = "index",
		type = "RELATION",
		related = Index.class,
		mandatory = false
	)
	public Index getIndex() {
		return new Index(store, rec == 0 ? 0 : store.getInt(rec, 4));
	}

	@Override
	@FieldData(
		name = "upRecord",
		type = "RELATION",
		related = Record.class,
		mandatory = false
	)
	public Record getUpRecord() {
		return new Record(store, rec == 0 ? 0 : store.getInt(rec, 17));
	}

	@Override
	public void output(Write write, int iterate) throws IOException {
		if (rec == 0 || iterate <= 0)
			return;
		write.field("index", getIndex(), true);
		write.endRecord();
	}

	@Override
	public String keys() throws IOException {
		StringBuilder res = new StringBuilder();
		if (rec == 0)
			return "";
		res.append("Record").append("{").append(getUpRecord().keys()).append("}");
		res.append(", ");
		res.append("IndexName").append("=").append(getIndex().getName());
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

	public SetIndex parse(Parser parser, Record parent) {
		while (parser.getSub()) {
			Index index = new Index(store);
			parser.getRelation("index", (int recNr) -> {
				index.parseKey(parser);
				return true;
			}, -1);
			int nextRec = parent.new IndexSetIndexes(this, index.getName()).search();
			if (parser.isDelete(nextRec)) {
				try (ChangeSetIndex record = new ChangeSetIndex(this)) {
					store.free(record.getRec());
					record.setRec(0);
				}
				continue;
			}
			if (nextRec == 0) {
				try (ChangeSetIndex record = new ChangeSetIndex(parent, 0)) {
					record.setIndex(index);
					record.parseFields(parser);
					rec = record.rec;
				}
			} else {
				rec = nextRec;
				try (ChangeSetIndex record = new ChangeSetIndex(this)) {
					record.parseFields(parser);
				}
			}
		}
		return this;
	}

	public boolean parseKey(Parser parser) {
		Record parent = getUpRecord();
		parser.getRelation("Record", (int recNr) -> {
			parent.setRec(recNr);
			parent.parseKey(parser);
			return true;
		}, getRec());
		Index index = new Index(store);
		parser.getRelation("index", (int recNr) -> {
			index.parseKey(parser);
			return true;
		}, -1);
		int nextRec = parent.new IndexSetIndexes(this, index.getName()).search();
		parser.finishRelation();
		rec = nextRec;
		return nextRec != 0;
	}

	@Override
	public Object get(int field) {
		switch (field) {
		case 1:
			return getIndex();
		default:
			return null;
		}
	}

	@Override
	public Iterable<? extends RecordInterface> iterate(int field, Object... key) {
		switch (field) {
		default:
			return null;
		}
	}

	@Override
	public FieldType type(int field) {
		switch (field) {
		case 1:
			return FieldType.OBJECT;
		default:
			return null;
		}
	}

	@Override
	public String name(int field) {
		switch (field) {
		case 1:
			return "index";
		default:
			return null;
		}
	}

	@Override
	public boolean exists() {
		return getRec() != 0;
	}
}
