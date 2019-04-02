package org.memorydb.world;

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
	/* package private */ static final int RECORD_SIZE = 26;

	public Special(Store store) {
		this.store = store;
		this.rec = 0;
	}

	public Special(Store store, int rec) {
		rec = store.correct(rec);
		this.store = store;
		this.rec = rec;
	}

	@Override
	public int rec() {
		return rec;
	}

	@Override
	public Special copy(int newRec) {
		assert store.validate(rec);
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

	public class IndexSpecials extends TreeIndex implements Iterable<Special> {
		public IndexSpecials() {
			super(Special.this, null, 136, 18);
		}

		public IndexSpecials(String key1) {
			super(Special.this, new Key() {
				@Override
				public int compareTo(int recNr) {
					if (recNr < 0)
						return -1;
					assert store.validate(recNr);
					rec(recNr);
					int o = 0;
					o = RedBlackTree.compare(key1, Special.this.getName());
					return o;
				}

				@Override
				public IndexOperation oper() {
					return IndexOperation.EQ;
				}
			}, 136, 18);
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

	public Special parse(Parser parser) {
		while (parser.getSub()) {
			String name = parser.getString("name");
			int nextRec = new IndexSpecials(name).search();
			if (parser.isDelete(nextRec)) {
				try (ChangeSpecial record = new ChangeSpecial(this)) {
					store.free(record.rec());
					record.rec(0);
				}
				continue;
			}
			if (nextRec == 0) {
				try (ChangeSpecial record = new ChangeSpecial(store)) {
					record.setName(name);
					record.parseFields(parser);
					rec = record.rec;
				}
			} else {
				rec = nextRec;
				try (ChangeSpecial record = new ChangeSpecial(this)) {
					record.parseFields(parser);
				}
			}
		}
		return this;
	}

	public boolean parseKey(Parser parser) {
		String name = parser.getString("name");
		int nextRec = new IndexSpecials(name).search();
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
		int field = 0;
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
	public Special next() {
		return null;
	}

	@Override
	public Special copy() {
		return new Special(store, rec);
	}
}
