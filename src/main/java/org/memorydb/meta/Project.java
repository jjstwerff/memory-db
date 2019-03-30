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
 * Automatically generated record class for table Project
 */
@RecordData(name = "Project")
public class Project implements MemoryRecord, RecordInterface {
	/* package private */ Store store;
	protected final int rec;
	/* package private */ static final int RECORD_SIZE = 29;

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
	public int rec() {
		return rec;
	}

	@Override
	public Project copy(int rec) {
		assert store.validate(rec);
		return new Project(store, rec);
	}

	@Override
	public Store store() {
		return store;
	}

	@Override
	public ChangeProject change() {
		return new ChangeProject(this);
	}

	@FieldData(name = "records", type = "SET", related = Record.class, mandatory = false)
	public IndexRecords getRecords() {
		return new IndexRecords();
	}

	public Record getRecords(String key1) {
		return new Record(store, new IndexRecords(key1).search());
	}

	public ChangeRecord addRecords() {
		return new ChangeRecord(this, 0);
	}

	/* package private */ class IndexRecords extends TreeIndex implements Iterable<Record> {
		public IndexRecords() {
			super(store(), null, 192, 25);
		}

		public IndexRecords(String key1) {
			super(store(), new Key() {
				@Override
				public int compareTo(int recNr) {
					if (recNr < 0)
						return -1;
					assert Project.this.store.validate(recNr);
					Record rec = new Record(store(), recNr);
					int o = 0;
					o = RedBlackTree.compare(key1, rec.getName());
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
			return store.getInt(rec(), 4);
		}

		@Override
		protected void changeTop(int value) {
			store.setInt(rec(), 4, value);
		}

		@Override
		protected int compareTo(int a, int b) {
			Record recA = new Record(store, a);
			Record recB = new Record(store, b);
			int o = 0;
			o = compare(recA.getName(), recB.getName());
			return o;
		}

		@Override
		public Iterator<Record> iterator() {
			return new Iterator<Record>() {
				int nextRec = search();

				@Override
				public boolean hasNext() {
					return nextRec != 0;
				}

				@Override
				public Record next() {
					int n = nextRec;
					nextRec = toNext(nextRec);
					return new Record(store(), n);
				}
			};
		}
	}

	@FieldData(name = "main", type = "RELATION", related = Record.class, mandatory = false)
	public Record getMain() {
		return new Record(store, rec == 0 ? 0 : store.getInt(rec, 8));
	}

	@FieldData(name = "pack", type = "STRING", mandatory = false)
	public String getPack() {
		return rec == 0 ? null : store.getString(store.getInt(rec, 12));
	}

	@FieldData(name = "directory", type = "STRING", mandatory = false)
	public String getDirectory() {
		return rec == 0 ? null : store.getString(store.getInt(rec, 16));
	}

	public class IndexMeta extends TreeIndex implements Iterable<Project> {
		public IndexMeta() {
			super(store(), null, 160, 21);
		}

		public IndexMeta(String key1) {
			super(store(), new Key() {
				@Override
				public int compareTo(int recNr) {
					if (recNr < 0)
						return -1;
					assert store().validate(recNr);
					Project rec = Project.this.copy(recNr);
					int o = 0;
					o = RedBlackTree.compare(key1, rec.getPack());
					return o;
				}

				@Override
				public IndexOperation oper() {
					return IndexOperation.EQ;
				}
			}, 160, 21);
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
			o = compare(recA.getPack(), recB.getPack());
			return o;
		}

		@Override
		public Iterator<Project> iterator() {
			return new Iterator<>() {
				int nextRec = search();

				@Override
				public boolean hasNext() {
					return nextRec != 0;
				}

				@Override
				public Project next() {
					int n = nextRec;
					nextRec = toNext(nextRec);
					return new Project(store, n);
				}
			};
		}
	}

	@Override
	public void output(Write write, int iterate) {
		if (rec == 0 || iterate <= 0)
			return;
		IndexRecords fldRecords = getRecords();
		if (fldRecords != null) {
			write.sub("records");
			for (Record sub : fldRecords)
				sub.output(write, iterate);
			write.endSub();
		}
		write.field("main", getMain());
		write.field("pack", getPack());
		write.field("directory", getDirectory());
		write.endRecord();
	}

	@Override
	public String keys() {
		StringBuilder res = new StringBuilder();
		if (rec == 0)
			return "";
		res.append("Pack").append("=").append(getPack());
		return res.toString();
	}

	@Override
	public String toString() {
		Write write = new Write(new StringBuilder());
		output(write, 4);
		return write.toString();
	}

	public Project parse(Parser parser) {
		while (parser.getSub()) {
			String pack = parser.getString("pack");
			int nextRec = new IndexMeta(pack).search();
			if (parser.isDelete(nextRec)) {
				try (ChangeProject record = new ChangeProject(this)) {
					store.free(record.rec());
				}
				continue;
			}
			if (nextRec == 0) {
				try (ChangeProject record = new ChangeProject(store, rec)) {
					record.setPack(pack);
					record.parseFields(parser);
				}
			} else {
				try (ChangeProject record = new ChangeProject(this)) {
					record.parseFields(parser);
				}
			}
		}
		return this;
	}

	public Project parseKey(Parser parser) {
		String pack = parser.getString("pack");
		int nextRec = new IndexMeta(pack).search();
		parser.finishRelation();
		if (nextRec == 0)
			return null;
		return copy(nextRec);
	}

	@Override
	public Object java() {
		int field = 0;
		switch (field) {
		case 2:
			return getMain();
		case 3:
			return getPack();
		case 4:
			return getDirectory();
		default:
			return null;
		}
	}

	@Override
	public FieldType type() {
		int field = 0;
		switch (field) {
		case 1:
			return FieldType.ARRAY;
		case 2:
			return FieldType.OBJECT;
		case 3:
			return FieldType.STRING;
		case 4:
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
			return "records";
		case 2:
			return "main";
		case 3:
			return "pack";
		case 4:
			return "directory";
		default:
			return null;
		}
	}

	@Override
	public Project next() {
		return null;
	}

	@Override
	public Project copy() {
		return new Project(store, rec);
	}
}
