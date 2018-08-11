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
 * Automatically generated record class for table Project
 */
@RecordData(
	name = "Project",
	keyFields = {"name"})
public class Project implements MemoryRecord, RecordInterface {
	/* package private */ Store store;
	protected int rec;
	/* package private */ static final int RECORD_SIZE = 33;

	public Project(Store store) {
		this.store = store;
		this.rec = 0;
	}

	public Project(Store store, int rec) {
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
	public ChangeProject change() {
		return new ChangeProject(this);
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
		name = "pack",
		type = "STRING",
		mandatory = false
	)
	public String getPack() {
		return rec == 0 ? null : store.getString(store.getInt(rec, 8));
	}

	@FieldData(
		name = "indexes",
		type = "SET",
		keyNames = {"name"},
		keyTypes = {"STRING"},
		related = Index.class,
		mandatory = false
	)
	public IndexIndexes getIndexes() {
		return new IndexIndexes(new Index(store));
	}

	public Index getIndexes(String key1) {
		Index resultRec = new Index(store);
		IndexIndexes idx = new IndexIndexes(resultRec, key1);
		int res = idx.search();
		if (res == 0)
			return resultRec;
		return new Index(store, res);
	}

	public ChangeIndex addIndexes() {
		return new ChangeIndex(this, 0);
	}

	/* package private */ class IndexIndexes extends TreeIndex<Index> {

		public IndexIndexes(Index record) {
			super(record, null, 232, 30);
		}

		public IndexIndexes(Index record, String key1) {
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
			}, 232, 30);
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
			Index recA = new Index(store, a);
			Index recB = new Index(store, b);
			int o = 0;
			o = compare(recA.getName(), recB.getName());
			return o;
		}
	}

	@FieldData(
		name = "records",
		type = "SET",
		keyNames = {"name"},
		keyTypes = {"STRING"},
		related = Record.class,
		mandatory = false
	)
	public IndexRecords getRecords() {
		return new IndexRecords(new Record(store));
	}

	public Record getRecords(String key1) {
		Record resultRec = new Record(store);
		IndexRecords idx = new IndexRecords(resultRec, key1);
		int res = idx.search();
		if (res == 0)
			return resultRec;
		return new Record(store, res);
	}

	public ChangeRecord addRecords() {
		return new ChangeRecord(this, 0);
	}

	/* package private */ class IndexRecords extends TreeIndex<Record> {

		public IndexRecords(Record record) {
			super(record, null, 304, 39);
		}

		public IndexRecords(Record record, String key1) {
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
			}, 304, 39);
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
			Record recA = new Record(store, a);
			Record recB = new Record(store, b);
			int o = 0;
			o = compare(recA.getName(), recB.getName());
			return o;
		}
	}

	@FieldData(
		name = "dir",
		type = "STRING",
		mandatory = false
	)
	public String getDir() {
		return rec == 0 ? null : store.getString(store.getInt(rec, 20));
	}

	public class IndexMeta extends TreeIndex<Project> {
		public IndexMeta() {
			super(Project.this, null, 192, 25);
		}

		public IndexMeta(String key1) {
			super(Project.this, new Key() {
				@Override
				public int compareTo(int recNr) {
					if (recNr < 0)
						return -1;
					assert store.validate(recNr);
					setRec(recNr);
					int o = 0;
					o = RedBlackTree.compare(key1, Project.this.getName());
					return o;
				}

				@Override
				public IndexOperation oper() {
					return IndexOperation.EQ;
				}
			}, 192, 25);
		}

		@Override
		protected int readTop() {
			return store.getInt(0, 12);
		}

		@Override
		protected void changeTop(int value) {
			store.setInt(0, 12, value);
		}

		@Override
		protected int compareTo(int a, int b) {
			Project recA = new Project(store, a);
			Project recB = new Project(store, b);
			int o = 0;
			o = compare(recA.getName(), recB.getName());
			return o;
		}
	}

	@Override
	public void output(Write write, int iterate) throws IOException {
		if (rec == 0 || iterate <= 0)
			return;
		write.field("name", getName(), true);
		write.field("pack", getPack(), false);
		IndexIndexes fldIndexes = getIndexes();
		if (fldIndexes != null) {
			write.sub("indexes", false);
			for (Index sub : fldIndexes)
				sub.output(write, iterate - 1);
			write.endSub();
		}
		IndexRecords fldRecords = getRecords();
		if (fldRecords != null) {
			write.sub("records", false);
			for (Record sub : fldRecords)
				sub.output(write, iterate - 1);
			write.endSub();
		}
		write.field("dir", getDir(), false);
		write.endRecord();
	}

	@Override
	public String keys() throws IOException {
		StringBuilder res = new StringBuilder();
		if (rec == 0)
			return "";
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

	public Project parse(Parser parser) {
		while (parser.getSub()) {
			String name = parser.getString("name");
			int nextRec = new IndexMeta(name).search();
			if (parser.isDelete(nextRec)) {
				try (ChangeProject record = new ChangeProject(this)) {
					store.free(record.getRec());
					record.setRec(0);
				}
				continue;
			}
			if (nextRec == 0) {
				try (ChangeProject record = new ChangeProject(store)) {
					record.setName(name);
					record.parseFields(parser);
					rec = record.rec;
				}
			} else {
				rec = nextRec;
				try (ChangeProject record = new ChangeProject(this)) {
					record.parseFields(parser);
				}
			}
		}
		return this;
	}

	public boolean parseKey(Parser parser) {
		String name = parser.getString("name");
		int nextRec = new IndexMeta(name).search();
		parser.finishRelation();
		rec = nextRec;
		return nextRec != 0;
	}

	@Override
	public Object get(int field) {
		switch (field) {
		case 1:
			return getName();
		case 2:
			return getPack();
		case 5:
			return getDir();
		default:
			return null;
		}
	}

	@Override
	public Iterable<? extends RecordInterface> iterate(int field, Object... key) {
		switch (field) {
		case 3:
			if (key.length > 0)
				return new IndexIndexes(new Index(store), (String) key[0]);
			return getIndexes();
		case 4:
			if (key.length > 0)
				return new IndexRecords(new Record(store), (String) key[0]);
			return getRecords();
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
			return FieldType.STRING;
		case 3:
			return FieldType.ITERATE;
		case 4:
			return FieldType.ITERATE;
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
			return "pack";
		case 3:
			return "indexes";
		case 4:
			return "records";
		case 5:
			return "dir";
		default:
			return null;
		}
	}

	@Override
	public boolean exists() {
		return getRec() != 0;
	}
}
