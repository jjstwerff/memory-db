package org.memorydb.meta;

import java.util.Iterator;

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
@RecordData(name = "Record")
public class Record implements MemoryRecord, RecordInterface {
	/* package private */ final Store store;
	protected final int rec;
	/* package private */ static final int RECORD_SIZE = 37;

	public Record(Store store, int rec) {
		rec = store.correct(rec);
		this.store = store;
		this.rec = rec;
	}

	@Override
	public int rec() {
		return rec;
	}

	@Override
	public Record copy(int rec) {
		assert store.validate(rec);
		return new Record(store, rec);
	}

	@Override
	public Store store() {
		return store;
	}

	@Override
	public ChangeRecord change() {
		return new ChangeRecord(this);
	}

	@FieldData(name = "name", type = "STRING", mandatory = false)
	public String getName() {
		return rec == 0 ? null : store.getString(store.getInt(rec, 4));
	}

	@FieldData(name = "fieldName", type = "SET", related = Field.class, mandatory = false)
	public IndexFieldName getFieldName() {
		return new IndexFieldName();
	}

	public Field getFieldName(String key1) {
		return new Field(store, new IndexFieldName(key1).search());
	}

	public ChangeField addFieldName() {
		return new ChangeField(this, 0);
	}

	/* package private */ class IndexFieldName extends TreeIndex implements Iterable<Field> {

		public IndexFieldName() {
			super(store(), null, 112, 15);
		}

		public IndexFieldName(String key1) {
			super(store(), new Key() {
				@Override
				public int compareTo(int recNr) {
					if (recNr < 0)
						return -1;
					assert store().validate(recNr);
					Field old = new Field(store(), recNr);
					int o = 0;
					o = RedBlackTree.compare(key1, old.getName());
					return o;
				}

				@Override
				public IndexOperation oper() {
					return IndexOperation.EQ;
				}
			}, 112, 15);
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
		public Iterator<Field> iterator() {
			return new Iterator<Field>() {
				int nextRec = search();

				@Override
				public boolean hasNext() {
					return nextRec > 0;
				}

				@Override
				public Field next() {
					int n = nextRec;
					nextRec = toNext(nextRec);
					return new Field(store(), n);
				}
			};
		}
	}

	@FieldData(name = "fields", type = "SET", related = Field.class, mandatory = false)
	public IndexFields getFields() {
		return new IndexFields();
	}

	public Field getFields(int key1) {
		return new Field(store, new IndexFields(key1).search());
	}

	public ChangeField addFields() {
		return new ChangeField(this, 0);
	}

	/* package private */ class IndexFields extends TreeIndex implements Iterable<Field> {

		public IndexFields() {
			super(store(), null, 216, 28);
		}

		public IndexFields(int key1) {
			super(store(), new Key() {
				@Override
				public int compareTo(int recNr) {
					if (recNr < 0)
						return -1;
					Field old = new Field(store(), recNr);
					int o = 0;
					o = RedBlackTree.compare(key1, old.getNr());
					return o;
				}

				@Override
				public IndexOperation oper() {
					return IndexOperation.EQ;
				}
			}, 216, 28);
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
		public Iterator<Field> iterator() {
			return new Iterator<>() {
				int nextRec = search();

				@Override
				public boolean hasNext() {
					return nextRec > 0;
				}

				@Override
				public Field next() {
					int n = nextRec;
					nextRec = toNext(nextRec);
					return new Field(store, n);
				}
			};
		}
	}

	@FieldData(name = "condition", type = "RELATION", related = Field.class, mandatory = false)
	public Field getCondition() {
		return new Field(store, rec == 0 ? 0 : store.getInt(rec, 16));
	}

	@FieldData(name = "description", type = "STRING", mandatory = false)
	public String getDescription() {
		return rec == 0 ? null : store.getString(store.getInt(rec, 20));
	}

	@Override
	public Project up() {
		return new Project(store, rec == 0 ? 0 : store.getInt(rec, 33));
	}

	@Override
	public void output(Write write, int iterate) {
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
	public String keys() {
		StringBuilder res = new StringBuilder();
		if (rec == 0)
			return "";
		res.append("Project").append("{").append(up().keys()).append("}");
		res.append(", ");
		res.append("Name").append("=").append(getName());
		return res.toString();
	}

	@Override
	public String toString() {
		Write write = new Write(new StringBuilder());
		output(write, 4);
		return write.toString();
	}

	public Record parse(Parser parser, Project parent) {
		while (parser.getSub()) {
			String name = parser.getString("name");
			int nextRec = parent.new IndexRecords(name).search();
			if (parser.isDelete(nextRec)) {
				try (ChangeRecord record = new ChangeRecord(this)) {
					store.free(record.rec());
				}
				continue;
			}
			if (nextRec == 0) {
				try (ChangeRecord record = new ChangeRecord(parent, 0)) {
					record.setName(name);
					record.parseFields(parser);
				}
			} else {
				try (ChangeRecord record = new ChangeRecord(this)) {
					record.parseFields(parser);
				}
			}
		}
		return this;
	}

	public Record parseKey(Parser parser) {
		Project parent = up();
		String name = parser.getString("name");
		int nextRec = parent.new IndexRecords(name).search();
		parser.finishRelation();
		return nextRec == 0 ? null : new Record(store, nextRec);
	}

	@Override
	public Object java() {
		int field = 0;
		switch (field) {
		case 1:
			return getName();
		case 4:
			return getCondition();
		case 5:
			return getDescription();
		default:
			return null;
		}
	}

	@Override
	public FieldType type() {
		int field = 0;
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
	public String name() {
		int field = 0;
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
	public Record next() {
		return null;
	}

	@Override
	public Record copy() {
		return new Record(store, rec);
	}
}
