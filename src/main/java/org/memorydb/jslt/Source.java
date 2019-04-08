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
	private final int field;
	/* package private */ static final int RECORD_SIZE = 17;

	public Source(Store store, int rec) {
		rec = store.correct(rec);
		this.store = store;
		this.rec = rec;
		this.field = 0;
	}

	public Source(Store store, int rec, int field) {
		rec = store.correct(rec);
		this.store = store;
		this.rec = rec;
		this.field = field;
	}

	@Override
	public int rec() {
		return rec;
	}

	@Override
	public Source copy(int newRec) {
		assert store.validate(newRec);
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

	public static class IndexSources extends TreeIndex implements Iterable<Source> {
		public IndexSources(Store store) {
			super(store, null, 64, 9);
		}

		public IndexSources(Store store, String key1) {
			super(store, new Key() {
				@Override
				public int compareTo(int recNr) {
					if (recNr < 0)
						return -1;
					assert store.validate(recNr);
					Source rec = new Source(store, recNr);
					int o = 0;
					o = RedBlackTree.compare(key1, rec.getName());
					return o;
				}

				@Override
				public IndexOperation oper() {
					return IndexOperation.EQ;
				}
			}, 64, 9);
		}

		private IndexSources(Store store, Key key, int flag, int field) {
			super(store, key, flag, field);
		}

		@Override
		public IndexSources copy() {
			return new IndexSources(store, key, flag, field);
		}

		@Override
		public Source field(String name) {
			int r = new IndexSources(store, name).search();
			return r <= 0 ? null : new IterRecord(store, r);
		}

		@Override
		public Source start() {
			int r = first();
			return r <= 0 ? null : new IterRecord(store, r);
		}

		private class IterRecord extends Source {
			private IterRecord(Store store, int r) {
				super(store, r);
			}

			@Override
			public IterRecord next() {
				int r = rec <= 0 ? 0 : toNext(rec);
				return r <= 0 ? null : new IterRecord(store, r);
			}
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
				int nextRec = search();

				@Override
				public boolean hasNext() {
					return nextRec > 0;
				}

				@Override
				public Source next() {
					int n = nextRec;
					nextRec = toNext(nextRec);
					return n <= 0 ? null : new Source(store, n);
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

	public static Source parse(Parser parser, Store store) {
		Source rec = null;
		while (parser.getSub()) {
			rec = parseKey(parser, store);
			if (parser.isDelete()) {
				if (rec != null)
					try (ChangeSource record = new ChangeSource(rec)) {
						store.free(record.rec());
					}
				continue;
			}
			if (rec == null) {
				try (ChangeSource record = new ChangeSource(store, 0)) {
					String name = parser.getString("name");
					record.setName(name);
					record.parseFields(parser);
					return record;
				}
			} else {
				try (ChangeSource record = new ChangeSource(rec)) {
					record.parseFields(parser);
				}
			}
		}
		return rec;
	}

	public static Source parseKey(Parser parser, Store store) {
		String name = parser.getString("name");
		int nextRec = new IndexSources(store, name).search();
		parser.finishRelation();
		return nextRec <= 0 ? null : new Source(store, nextRec);
	}

	@Override
	public Object java() {
		switch (field) {
		case 1:
			return getName();
		default:
			return null;
		}
	}

	@Override
	public FieldType type() {
		switch (field) {
		case 0:
			return FieldType.OBJECT;
		case 1:
			return FieldType.STRING;
		default:
			return null;
		}
	}

	@Override
	public String name() {
		switch (field) {
		case 1:
			return "name";
		default:
			return null;
		}
	}

	@Override
	public Source start() {
		return new Source(store, rec, 1);
	}

	@Override
	public Source next() {
		return field >= 1 ? null : new Source(store, rec, field + 1);
	}

	@Override
	public Source copy() {
		return new Source(store, rec, field);
	}
}
