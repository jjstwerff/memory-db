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

	public static void parse(Parser parser, Store store) {
		while (parser.getSub()) {
			String name = parser.getString("name");
			int nextRec = new IndexSources(store, name).search();
			if (parser.isDelete(nextRec)) {
				try (ChangeSource record = new ChangeSource(store, nextRec)) {
					store.free(record.rec());
				}
				continue;
			}
			if (nextRec == 0) {
				try (ChangeSource record = new ChangeSource(store, 0)) {
					record.setName(name);
					record.parseFields(parser);
				}
			} else {
				try (ChangeSource record = new ChangeSource(store, nextRec)) {
					record.parseFields(parser);
				}
			}
		}
	}

	public Source parseKey(Parser parser) {
		String name = parser.getString("name");
		int nextRec = new IndexSources(store, name).search();
		parser.finishRelation();
		return nextRec <= 0 ? null : new Source(store, nextRec);
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
