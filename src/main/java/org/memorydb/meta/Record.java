package org.memorydb.meta;

import java.io.IOException;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.meta.Project.IndexRecords;
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
	/* package private */ static final int RECORD_SIZE = 37;

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
		name = "fieldName",
		type = "SET",
		keyNames = {"name"},
		keyTypes = {"STRING"},
		related = Field.class,
		mandatory = false
	)
	public IndexFieldName getFieldName() {
		return new IndexFieldName(new Field(store));
	}

	public Field getFieldName(String key1) {
		Field resultRec = new Field(store);
		IndexFieldName idx = new IndexFieldName(resultRec, key1);
		int res = idx.search();
		if (res == 0)
			return resultRec;
		return new Field(store, res);
	}

	public ChangeField addFieldName() {
		return new ChangeField(this, 0);
	}

	/* package private */ class IndexFieldName extends TreeIndex<Field> {

		public IndexFieldName(Field record) {
			super(record, null, 384, 49);
		}

		public IndexFieldName(Field record, String key1) {
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
			}, 384, 49);
		}

		@Override
		protected int readTop() {
			return store.getInt(rec, 8);
		}

		@Override
		protected void changeTop(int value) {
			store.setInt(rec, 8, value);
		}

		@Override
		protected int compareTo(int a, int b) {
			Field recA = new Field(store, a);
			Field recB = new Field(store, b);
			int o = 0;
			o = compare(recA.getName(), recB.getName());
			return o;
		}

		@Override
		public Object get(int field) {
			return new Field(store, field);
		}
	}

	@FieldData(
		name = "fields",
		type = "SET",
		keyNames = {"nr"},
		keyTypes = {"INTEGER"},
		related = Field.class,
		mandatory = false
	)
	public IndexFields getFields() {
		return new IndexFields(new Field(store));
	}

	public Field getFields(int key1) {
		Field resultRec = new Field(store);
		IndexFields idx = new IndexFields(resultRec, key1);
		int res = idx.search();
		if (res == 0)
			return resultRec;
		return new Field(store, res);
	}

	public ChangeField addFields() {
		return new ChangeField(this, 0);
	}

	/* package private */ class IndexFields extends TreeIndex<Field> {

		public IndexFields(Field record) {
			super(record, null, 488, 62);
		}

		public IndexFields(Field record, int key1) {
			super(record, new Key() {
				@Override
				public int compareTo(int recNr) {
					if (recNr < 0)
						return -1;
					assert store.validate(recNr);
					record.setRec(recNr);
					int o = 0;
					o = RedBlackTree.compare(key1, record.getNr());
					return o;
				}

				@Override
				public IndexOperation oper() {
					return IndexOperation.EQ;
				}
			}, 488, 62);
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
			o = compare(recA.getNr(), recB.getNr());
			return o;
		}

		@Override
		public Object get(int field) {
			return new Field(store, field);
		}
	}

	@FieldData(
		name = "condition",
		type = "RELATION",
		related = Field.class,
		mandatory = false
	)
	public Field getCondition() {
		return new Field(store, rec == 0 ? 0 : store.getInt(rec, 16));
	}

	@FieldData(
		name = "description",
		type = "STRING",
		mandatory = false
	)
	public String getDescription() {
		return rec == 0 ? null : store.getString(store.getInt(rec, 20));
	}

	@Override
	@FieldData(
		name = "upRecord",
		type = "RELATION",
		related = Project.class,
		mandatory = false
	)
	public Project getUpRecord() {
		return new Project(store, rec == 0 ? 0 : store.getInt(rec, 33));
	}

	@Override
	public void output(Write write, int iterate) throws IOException {
		if (rec == 0 || iterate <= 0)
			return;
		write.field("name", getName());
		IndexFieldName fldFieldName = getFieldName();
		if (fldFieldName != null) {
			write.sub("fieldName");
			for (Field sub : fldFieldName)
				sub.output(write, iterate);
			write.endSub();
		}
		IndexFields fldFields = getFields();
		if (fldFields != null) {
			write.sub("fields");
			for (Field sub : fldFields)
				sub.output(write, iterate);
			write.endSub();
		}
		write.field("condition", getCondition());
		write.field("description", getDescription());
		write.endRecord();
	}

	@Override
	public String keys() throws IOException {
		StringBuilder res = new StringBuilder();
		if (rec == 0)
			return "";
		res.append("name").append("=").append(getName());
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

	public static int parseKey(Parser parser, Project project) {
		Record into = new Record(project.getStore(), 0);
		IndexRecords idx = project.new IndexRecords(into, parser.getString("name"));
		int recId = idx.search();
		if (recId == 0)
			return 0;
		return recId;
	}

	@Override
	public Object get(int field) {
		switch (field) {
		case 1:
			return getName();
		case 2:
			return getFieldName();
		case 3:
			return getFields();
		case 4:
			return getCondition();
		case 5:
			return getDescription();
		default:
			return null;
		}
	}

	@Override
	public Iterable<? extends RecordInterface> iterate(int field, Object... key) {
		switch (field) {
		case 2:
			if (key.length > 0)
				return new IndexFieldName(new Field(store), (String) key[0]);
			return getFieldName();
		case 3:
			if (key.length > 0)
				return new IndexFields(new Field(store), (int) key[0]);
			return getFields();
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
			return FieldType.OBJECT;
		case 5:
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
			return "fieldName";
		case 3:
			return "fields";
		case 4:
			return "condition";
		case 5:
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
