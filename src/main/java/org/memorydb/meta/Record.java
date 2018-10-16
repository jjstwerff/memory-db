package org.memorydb.meta;

import java.io.IOException;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.structure.FieldData;
import org.memorydb.structure.IndexOperation;
import org.memorydb.structure.Key;
import org.memorydb.structure.MemoryRecord;
import org.memorydb.structure.RecordData;
import org.memorydb.structure.RecordInterface;
import org.memorydb.structure.RedBlackTree;
import org.memorydb.structure.Store;
import org.memorydb.structure.TreeIndex;

/**
 * Automatically generated record class for table Record
 */
@RecordData(
	name = "Record",
	keyFields = {"name"})
public class Record implements MemoryRecord, RecordInterface {
	/* package private */ Store store;
	protected int rec;
	/* package private */ static final int RECORD_SIZE = 51;

	public Record(Store store) {
		this.store = store;
		this.rec = 0;
	}

	public Record(Store store, int rec) {
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
	public ChangeRecord change() {
		return new ChangeRecord(this);
	}

	@FieldData(
		name = "name",
		type = "STRING",
		mandatory = false
	)
	public String getName() {
		return rec == 0 ? null : store.getString(store.getInt(rec, 4));
	}

	@FieldData(
		name = "fields",
		type = "ARRAY",
		related = FieldsArray.class,
		mandatory = false
	)
	public FieldsArray getFields() {
		return new FieldsArray(this, -1);
	}

	public FieldsArray getFields(int index) {
		return new FieldsArray(this, index);
	}

	public FieldsArray addFields() {
		return getFields().add();
	}

	@FieldData(
		name = "fieldOnName",
		type = "SET",
		keyNames = {"name"},
		keyTypes = {"STRING"},
		related = Field.class,
		mandatory = false
	)
	public IndexFieldOnName getFieldOnName() {
		return new IndexFieldOnName(new Field(store));
	}

	public Field getFieldOnName(String key1) {
		Field resultRec = new Field(store);
		IndexFieldOnName idx = new IndexFieldOnName(resultRec, key1);
		int res = idx.search();
		if (res == 0)
			return resultRec;
		return new Field(store, res);
	}

	public ChangeField addFieldOnName() {
		return new ChangeField(this, 0);
	}

	/* package private */ class IndexFieldOnName extends TreeIndex<Field> {

		public IndexFieldOnName(Field record) {
			super(record, null, 256, 33);
		}

		public IndexFieldOnName(Field record, String key1) {
			super(record, new Key() {
				@Override
				public int compareTo(int recNr) {
					if (recNr < 0)
						return -1;
					assert store.validate(recNr);
					record.setRec(recNr);
					int o = 0;
					o = RedBlackTree.compare(key1, record.getName());
					return o;
				}

				@Override
				public IndexOperation oper() {
					return IndexOperation.EQ;
				}
			}, 256, 33);
		}

		@Override
		protected int readTop() {
			return store.getInt(rec, 12);
		}

		@Override
		protected void changeTop(int value) {
			store.setInt(rec, 12, value);
		}

		@Override
		protected int compareTo(int a, int b) {
			Field recA = new Field(store, a);
			Field recB = new Field(store, b);
			int o = 0;
			o = compare(recA.getName(), recB.getName());
			return o;
		}
	}

	@FieldData(
		name = "setIndexes",
		type = "SET",
		keyNames = {"index_name"},
		keyTypes = {"STRING"},
		related = SetIndex.class,
		mandatory = false
	)
	public IndexSetIndexes getSetIndexes() {
		return new IndexSetIndexes(new SetIndex(store));
	}

	public SetIndex getSetIndexes(String key1) {
		SetIndex resultRec = new SetIndex(store);
		IndexSetIndexes idx = new IndexSetIndexes(resultRec, key1);
		int res = idx.search();
		if (res == 0)
			return resultRec;
		return new SetIndex(store, res);
	}

	public ChangeSetIndex addSetIndexes() {
		return new ChangeSetIndex(this, 0);
	}

	/* package private */ class IndexSetIndexes extends TreeIndex<SetIndex> {

		public IndexSetIndexes(SetIndex record) {
			super(record, null, 64, 9);
		}

		public IndexSetIndexes(SetIndex record, String key1) {
			super(record, new Key() {
				@Override
				public int compareTo(int recNr) {
					if (recNr < 0)
						return -1;
					assert store.validate(recNr);
					record.setRec(recNr);
					int o = 0;
					o = RedBlackTree.compare(key1, record.getIndex().getName());
					return o;
				}

				@Override
				public IndexOperation oper() {
					return IndexOperation.EQ;
				}
			}, 64, 9);
		}

		@Override
		protected int readTop() {
			return store.getInt(rec, 16);
		}

		@Override
		protected void changeTop(int value) {
			store.setInt(rec, 16, value);
		}

		@Override
		protected int compareTo(int a, int b) {
			SetIndex recA = new SetIndex(store, a);
			SetIndex recB = new SetIndex(store, b);
			int o = 0;
			o = compare(recA.getIndex().getName(), recB.getIndex().getName());
			return o;
		}
	}

	@FieldData(
		name = "freeBits",
		type = "SET",
		keyNames = {"size"},
		keyTypes = {"INTEGER"},
		related = FreeBits.class,
		mandatory = false
	)
	public IndexFreeBits getFreeBits() {
		return new IndexFreeBits(new FreeBits(store));
	}

	public FreeBits getFreeBits(int key1) {
		FreeBits resultRec = new FreeBits(store);
		IndexFreeBits idx = new IndexFreeBits(resultRec, key1);
		int res = idx.search();
		if (res == 0)
			return resultRec;
		return new FreeBits(store, res);
	}

	public ChangeFreeBits addFreeBits() {
		return new ChangeFreeBits(this, 0);
	}

	/* package private */ class IndexFreeBits extends TreeIndex<FreeBits> {

		public IndexFreeBits(FreeBits record) {
			super(record, null, 96, 13);
		}

		public IndexFreeBits(FreeBits record, int key1) {
			super(record, new Key() {
				@Override
				public int compareTo(int recNr) {
					if (recNr < 0)
						return -1;
					assert store.validate(recNr);
					record.setRec(recNr);
					int o = 0;
					o = RedBlackTree.compare(key1, record.getSize());
					return o;
				}

				@Override
				public IndexOperation oper() {
					return IndexOperation.EQ;
				}
			}, 96, 13);
		}

		@Override
		protected int readTop() {
			return store.getInt(rec, 20);
		}

		@Override
		protected void changeTop(int value) {
			store.setInt(rec, 20, value);
		}

		@Override
		protected int compareTo(int a, int b) {
			FreeBits recA = new FreeBits(store, a);
			FreeBits recB = new FreeBits(store, b);
			int o = 0;
			o = compare(recA.getSize(), recB.getSize());
			return o;
		}
	}

	@FieldData(
		name = "parent",
		type = "RELATION",
		related = Record.class,
		mandatory = false
	)
	public Record getParent() {
		return new Record(store, rec == 0 ? 0 : store.getInt(rec, 24));
	}

	@FieldData(
		name = "size",
		type = "INTEGER",
		mandatory = true
	)
	public int getSize() {
		return rec == 0 ? Integer.MIN_VALUE : store.getInt(rec, 28);
	}

	@FieldData(
		name = "related",
		type = "BOOLEAN",
		mandatory = false
	)
	public boolean isRelated() {
		return rec == 0 ? false : (store.getByte(rec, 32) & 1) > 0;
	}

	@FieldData(
		name = "full",
		type = "BOOLEAN",
		mandatory = false
	)
	public boolean isFull() {
		return rec == 0 ? false : (store.getByte(rec, 33) & 1) > 0;
	}

	@FieldData(
		name = "description",
		type = "STRING",
		mandatory = false
	)
	public String getDescription() {
		return rec == 0 ? null : store.getString(store.getInt(rec, 34));
	}

	@Override
	@FieldData(
		name = "upRecord",
		type = "RELATION",
		related = Project.class,
		mandatory = false
	)
	public Project getUpRecord() {
		return new Project(store, rec == 0 ? 0 : store.getInt(rec, 47));
	}

	@Override
	public void output(Write write, int iterate) throws IOException {
		if (rec == 0 || iterate <= 0)
			return;
		write.field("name", getName(), true);
		FieldsArray fldFields = getFields();
		if (fldFields != null) {
			write.sub("fields", false);
			for (FieldsArray sub : fldFields)
				sub.output(write, iterate - 1);
			write.endSub();
		}
		IndexFieldOnName fldFieldOnName = getFieldOnName();
		if (fldFieldOnName != null) {
			write.sub("fieldOnName", false);
			for (Field sub : fldFieldOnName)
				sub.output(write, iterate - 1);
			write.endSub();
		}
		IndexSetIndexes fldSetIndexes = getSetIndexes();
		if (fldSetIndexes != null) {
			write.sub("setIndexes", false);
			for (SetIndex sub : fldSetIndexes)
				sub.output(write, iterate - 1);
			write.endSub();
		}
		IndexFreeBits fldFreeBits = getFreeBits();
		if (fldFreeBits != null) {
			write.sub("freeBits", false);
			for (FreeBits sub : fldFreeBits)
				sub.output(write, iterate - 1);
			write.endSub();
		}
		write.field("parent", getParent(), false);
		write.field("size", getSize(), false);
		write.field("related", isRelated(), false);
		write.field("full", isFull(), false);
		write.field("description", getDescription(), false);
		write.endRecord();
	}

	@Override
	public String keys() throws IOException {
		StringBuilder res = new StringBuilder();
		if (rec == 0)
			return "";
		res.append("Project").append("{").append(getUpRecord().keys()).append("}");
		res.append(", ");
		res.append("Name").append("=").append(getName());
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

	public Record parse(Parser parser, Project parent) {
		while (parser.getSub()) {
			String name = parser.getString("name");
			int nextRec = parent.new IndexRecords(this, name).search();
			if (parser.isDelete(nextRec)) {
				try (ChangeRecord record = new ChangeRecord(this)) {
					store.free(record.getRec());
					record.setRec(0);
				}
				continue;
			}
			if (nextRec == 0) {
				try (ChangeRecord record = new ChangeRecord(parent, 0)) {
					record.setName(name);
					record.parseFields(parser);
					rec = record.rec;
				}
			} else {
				rec = nextRec;
				try (ChangeRecord record = new ChangeRecord(this)) {
					record.parseFields(parser);
				}
			}
		}
		return this;
	}

	public boolean parseKey(Parser parser) {
		Project parent = getUpRecord();
		String name = parser.getString("name");
		int nextRec = parent.new IndexRecords(this, name).search();
		parser.finishRelation();
		rec = nextRec;
		return nextRec != 0;
	}

	@Override
	public Object get(int field) {
		switch (field) {
		case 1:
			return getName();
		case 6:
			return getParent();
		case 7:
			return getSize();
		case 8:
			return isRelated();
		case 9:
			return isFull();
		case 10:
			return getDescription();
		default:
			return null;
		}
	}

	@Override
	public Iterable<? extends RecordInterface> iterate(int field, Object... key) {
		switch (field) {
		case 2:
			return getFields();
		case 3:
			if (key.length > 0)
				return new IndexFieldOnName(new Field(store), (String) key[0]);
			return getFieldOnName();
		case 4:
			if (key.length > 0)
				return new IndexSetIndexes(new SetIndex(store), (String) key[0]);
			return getSetIndexes();
		case 5:
			if (key.length > 0)
				return new IndexFreeBits(new FreeBits(store), (int) key[0]);
			return getFreeBits();
		default:
			return null;
		}
	}

	@Override
	public FieldType type(int field) {
		switch (field) {
		case 1:
			return FieldType.STRING;
		case 2:
			return FieldType.ARRAY;
		case 3:
			return FieldType.ARRAY;
		case 4:
			return FieldType.ARRAY;
		case 5:
			return FieldType.ARRAY;
		case 6:
			return FieldType.OBJECT;
		case 7:
			return FieldType.INTEGER;
		case 8:
			return FieldType.BOOLEAN;
		case 9:
			return FieldType.BOOLEAN;
		case 10:
			return FieldType.STRING;
		default:
			return null;
		}
	}

	@Override
	public String name(int field) {
		switch (field) {
		case 1:
			return "name";
		case 2:
			return "fields";
		case 3:
			return "fieldOnName";
		case 4:
			return "setIndexes";
		case 5:
			return "freeBits";
		case 6:
			return "parent";
		case 7:
			return "size";
		case 8:
			return "related";
		case 9:
			return "full";
		case 10:
			return "description";
		default:
			return null;
		}
	}

	@Override
	public boolean exists() {
		return getRec() != 0;
	}
}
