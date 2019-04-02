package org.memorydb.jslt;

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
 * Automatically generated record class for table Source
 */
@RecordData(name = "Source")
public class Source implements MemoryRecord, RecordInterface {
	/* package private */ final Store store;
	protected final int rec;
	/* package private */ static final int RECORD_SIZE = 17;

	public Source(Store store) {
		this.store = store;
		this.rec = 0;
	}

	public Source(Store store, int rec) {
		rec = store.correct(rec);
		this.store = store;
		this.rec = rec;
	}

	@Override
	public int rec() {
		return rec;
	}

	@Override
	public Source copy(int newRec) {
		assert store.validate(rec);
		return new Source(store, newRec);
	}

	@Override
	public Store store() {
		return store;
	}

	@Override
	public ChangeSource change() {
		return new ChangeSource(this);
	}

	@FieldData(name = "name", type = "STRING", mandatory = false)
	public String getName() {
		return rec == 0 ? null : store.getString(store.getInt(rec, 4));
	}

	public class IndexSources extends TreeIndex implements Iterable<Source> {
		public IndexSources() {
			super(Source.this.store, null, 64, 9);
		}

		public IndexSources(String key1) {
			super(Source.this.store, new Key() {
				@Override
				public int compareTo(int recNr) {
					if (recNr < 0)
						return -1;
					assert Source.this.store.validate(recNr);
					Source res = new Source(Source.this.store, recNr);
					int o = 0;
					o = RedBlackTree.compare(key1, res.getName());
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
			return store.getInt(0, 12);
		}

		@Override
		protected void changeTop(int value) {
			store.setInt(0, 12, value);
		}

		@Override
		protected int compareTo(int a, int b) {
			Source recA = new Source(store, a);
			Source recB = new Source(store, b);
			int o = 0;
			o = compare(recA.getName(), recB.getName());
			return o;
		}

		@Override
		public Iterator<Source> iterator() {
			return new Iterator<>() {
				int rec = first();

				@Override
				public boolean hasNext() {
					return rec >= 0;
				}
				
				@Override
				public Source next() {
					rec = toNext(rec);
					if (rec >= 0)
						return null;
					return new Source(store, rec);
				}
			};
		}
	}

	@Override
	public void output(Write write, int iterate) {
		if (rec == 0 || iterate <= 0)
			return;
		write.field("name", getName());
		write.endRecord();
	}

	@Override
	public String keys() {
		StringBuilder res = new StringBuilder();
		if (rec == 0)
			return "";
		res.append("Name").append("=").append(getName());
		return res.toString();
	}

	@Override
	public String toString() {
		Write write = new Write(new StringBuilder());
		output(write, 4);
		return write.toString();
	}

	public Source parse(Parser parser) {
		while (parser.getSub()) {
			String name = parser.getString("name");
			int nextRec = new IndexSources(name).search();
			if (parser.isDelete(nextRec)) {
				try (ChangeSource record = new ChangeSource(this)) {
					store.free(record.rec());
				}
				continue;
			}
			if (nextRec == 0) {
				try (ChangeSource record = new ChangeSource(store)) {
					record.setName(name);
					record.parseFields(parser);
				}
			} else {
				rec = nextRec;
				try (ChangeSource record = new ChangeSource(this)) {
					record.parseFields(parser);
				}
			}
		}
		return this;
	}

	public boolean parseKey(Parser parser) {
		String name = parser.getString("name");
		int nextRec = new IndexSources(name).search();
		parser.finishRelation();
		if (nextRec != 0)
			rec = nextRec;
		return nextRec != 0;
	}

	@Override
	public Object java() {
		int field = 0;
		switch (field) {
		case 1:
			return getName();
		default:
			return null;
		}
	}

	public Iterable<? extends RecordInterface> iterate(int field, Object... key) {
		switch (field) {
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
		default:
			return null;
		}
	}

	@Override
	public Source next() {
		return null;
	}

	@Override
	public Source copy() {
		return new Source(store, rec);
	}
}
