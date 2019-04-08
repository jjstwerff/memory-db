package org.memorydb.world;

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
 * Automatically generated record class for table Special
 */
@RecordData(name = "Special")
public class Special implements MemoryRecord, RecordInterface {
	/* package private */ final Store store;
	protected final int rec;
	private final int field;
	/* package private */ static final int RECORD_SIZE = 26;

	public Special(Store store, int rec) {
		rec = store.correct(rec);
		this.store = store;
		this.rec = rec;
		this.field = 0;
	}

	public Special(Store store, int rec, int field) {
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
	public Special copy(int newRec) {
		assert store.validate(newRec);
		return new Special(store, newRec);
	}

	@Override
	public Store store() {
		return store;
	}

	@Override
	public ChangeSpecial change() {
		return new ChangeSpecial(this);
	}

	@FieldData(name = "name", type = "STRING", mandatory = false)
	public String getName() {
		return rec == 0 ? null : store.getString(store.getInt(rec, 4));
	}

	@FieldData(name = "opposite", type = "STRING", mandatory = false)
	public String getOpposite() {
		return rec == 0 ? null : store.getString(store.getInt(rec, 8));
	}

	@FieldData(name = "description", type = "STRING", mandatory = false)
	public String getDescription() {
		return rec == 0 ? null : store.getString(store.getInt(rec, 12));
	}

	@FieldData(name = "taste", type = "BOOLEAN", mandatory = false)
	public boolean isTaste() {
		return rec == 0 ? false : (store.getByte(rec, 16) & 1) > 0;
	}

	public static class IndexSpecials extends TreeIndex implements Iterable<Special> {
		public IndexSpecials(Store store) {
			super(store, null, 136, 18);
		}

		public IndexSpecials(Store store, String key1) {
			super(store, new Key() {
				@Override
				public int compareTo(int recNr) {
					if (recNr < 0)
						return -1;
					assert store.validate(recNr);
					Special rec = new Special(store, recNr);
					int o = 0;
					o = RedBlackTree.compare(key1, rec.getName());
					return o;
				}

				@Override
				public IndexOperation oper() {
					return IndexOperation.EQ;
				}
			}, 136, 18);
		}

		private IndexSpecials(Store store, Key key, int flag, int field) {
			super(store, key, flag, field);
		}

		@Override
		public IndexSpecials copy() {
			return new IndexSpecials(store, key, flag, field);
		}

		@Override
		public Special field(String name) {
			int r = new IndexSpecials(store, name).search();
			return r <= 0 ? null : new IterRecord(store, r);
		}

		@Override
		public Special start() {
			int r = first();
			return r <= 0 ? null : new IterRecord(store, r);
		}

		private class IterRecord extends Special {
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
			Special recA = new Special(store, a);
			Special recB = new Special(store, b);
			int o = 0;
			o = compare(recA.getName(), recB.getName());
			return o;
		}

		@Override
		public Iterator<Special> iterator() {
			return new Iterator<>() {
				int nextRec = search();

				@Override
				public boolean hasNext() {
					return nextRec > 0;
				}

				@Override
				public Special next() {
					int n = nextRec;
					nextRec = toNext(nextRec);
					return n <= 0 ? null : new Special(store, n);
				}
			};
		}
	}

	@Override
	public void output(Write write, int iterate) {
		if (rec == 0 || iterate <= 0)
			return;
		write.field("name", getName());
		write.field("opposite", getOpposite());
		write.field("description", getDescription());
		write.field("taste", isTaste());
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

	public static Special parse(Parser parser, Store store) {
		Special rec = null;
		while (parser.getSub()) {
			rec = parseKey(parser, store);
			if (parser.isDelete()) {
				if (rec != null)
					try (ChangeSpecial record = new ChangeSpecial(rec)) {
						store.free(record.rec());
					}
				continue;
			}
			if (rec == null) {
				try (ChangeSpecial record = new ChangeSpecial(store, 0)) {
					String name = parser.getString("name");
					record.setName(name);
					record.parseFields(parser);
					return record;
				}
			} else {
				try (ChangeSpecial record = new ChangeSpecial(rec)) {
					record.parseFields(parser);
				}
			}
		}
		return rec;
	}

	public static Special parseKey(Parser parser, Store store) {
		String name = parser.getString("name");
		int nextRec = new IndexSpecials(store, name).search();
		parser.finishRelation();
		return nextRec <= 0 ? null : new Special(store, nextRec);
	}

	@Override
	public Object java() {
		switch (field) {
		case 1:
			return getName();
		case 2:
			return getOpposite();
		case 3:
			return getDescription();
		case 4:
			return isTaste();
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
		case 2:
			return FieldType.STRING;
		case 3:
			return FieldType.STRING;
		case 4:
			return FieldType.BOOLEAN;
		default:
			return null;
		}
	}

	@Override
	public String name() {
		switch (field) {
		case 1:
			return "name";
		case 2:
			return "opposite";
		case 3:
			return "description";
		case 4:
			return "taste";
		default:
			return null;
		}
	}

	@Override
	public Special start() {
		return new Special(store, rec, 1);
	}

	@Override
	public Special next() {
		return field >= 4 ? null : new Special(store, rec, field + 1);
	}

	@Override
	public Special copy() {
		return new Special(store, rec, field);
	}
}
