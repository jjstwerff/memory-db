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
 * Automatically generated record class for table FreeBits
 */
@RecordData(
	name = "FreeBits",
	keyFields = {"size"})
public class FreeBits implements MemoryRecord, RecordInterface {
	/* package private */ Store store;
	protected int rec;
	/* package private */ static final int RECORD_SIZE = 25;

	public FreeBits(Store store) {
		this.store = store;
		this.rec = 0;
	}

	public FreeBits(Store store, int rec) {
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
	public ChangeFreeBits change() {
		return new ChangeFreeBits(this);
	}

	@FieldData(
		name = "size",
		type = "INTEGER",
		mandatory = false
	)
	public int getSize() {
		return rec == 0 ? Integer.MIN_VALUE : store.getInt(rec, 4);
	}

	@FieldData(
		name = "pos",
		type = "INTEGER",
		mandatory = false
	)
	public int getPos() {
		return rec == 0 ? Integer.MIN_VALUE : store.getInt(rec, 8);
	}

	@Override
	@FieldData(
		name = "upRecord",
		type = "RELATION",
		related = Record.class,
		mandatory = false
	)
	public Record getUpRecord() {
		return new Record(store, rec == 0 ? 0 : store.getInt(rec, 21));
	}

	@Override
	public void output(Write write, int iterate) throws IOException {
		if (rec == 0 || iterate <= 0)
			return;
		write.field("size", getSize());
		write.field("pos", getPos());
		write.endRecord();
	}

	@Override
	public String keys() throws IOException {
		StringBuilder res = new StringBuilder();
		if (rec == 0)
			return "";
		res.append("Record").append("{").append(getUpRecord().keys()).append("}");
		res.append(", ");
		res.append("Size").append("=").append(getSize());
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

	public FreeBits parse(Parser parser, Record parent) {
		while (parser.getSub()) {
			int size = parser.getInt("size");
			int nextRec = parent.new IndexFreeBits(this, size).search();
			if (parser.isDelete(nextRec)) {
				try (ChangeFreeBits record = new ChangeFreeBits(this)) {
					store.free(record.getRec());
					record.setRec(0);
				}
				continue;
			}
			if (nextRec == 0) {
				try (ChangeFreeBits record = new ChangeFreeBits(parent, 0)) {
					record.setSize(size);
					record.parseFields(parser);
					rec = record.rec;
				}
			} else {
				rec = nextRec;
				try (ChangeFreeBits record = new ChangeFreeBits(this)) {
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
		int size = parser.getInt("size");
		int nextRec = parent.new IndexFreeBits(this, size).search();
		parser.finishRelation();
		rec = nextRec;
		return nextRec != 0;
	}

	@Override
	public Object get(int field) {
		switch (field) {
		case 1:
			return getSize();
		case 2:
			return getPos();
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
			return FieldType.INTEGER;
		case 2:
			return FieldType.INTEGER;
		default:
			return null;
		}
	}

	@Override
	public String name(int field) {
		switch (field) {
		case 1:
			return "size";
		case 2:
			return "pos";
		default:
			return null;
		}
	}

	@Override
	public boolean exists() {
		return getRec() != 0;
	}
}
