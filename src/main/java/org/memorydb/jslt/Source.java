package org.memorydb.jslt;

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
 * Automatically generated record class for table Source
 */
@RecordData(
	name = "Source",
	keyFields = {"name"})
public class Source implements MemoryRecord, RecordInterface {
	/* package private */ Store store;
	protected int rec;
	/* package private */ static final int RECORD_SIZE = 21;

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
	public ChangeSource change() {
		return new ChangeSource(this);
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
		name = "listeners",
		type = "SET",
		keyNames = {"nr"},
		keyTypes = {"INTEGER"},
		related = Listener.class,
		mandatory = false
	)
	public IndexListeners getListeners() {
		return new IndexListeners(new Listener(store));
	}

	public Listener getListeners(int key1) {
		Listener resultRec = new Listener(store);
		IndexListeners idx = new IndexListeners(resultRec, key1);
		int res = idx.search();
		if (res == 0)
			return resultRec;
		return new Listener(store, res);
	}

	public ChangeListener addListeners() {
		return new ChangeListener(this, 0);
	}

	/* package private */ class IndexListeners extends TreeIndex<Listener> {

		public IndexListeners(Listener record) {
			super(record, null, 96, 13);
		}

		public IndexListeners(Listener record, int key1) {
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
			}, 96, 13);
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
			Listener recA = new Listener(store, a);
			Listener recB = new Listener(store, b);
			int o = 0;
			o = compare(recA.getNr(), recB.getNr());
			return o;
		}
	}

	public class IndexSources extends TreeIndex<Source> {
		public IndexSources() {
			super(Source.this, null, 96, 13);
		}

		public IndexSources(String key1) {
			super(Source.this, new Key() {
				@Override
				public int compareTo(int recNr) {
					if (recNr < 0)
						return -1;
					assert store.validate(recNr);
					setRec(recNr);
					int o = 0;
					o = RedBlackTree.compare(key1, Source.this.getName());
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
	}

	@Override
	public void output(Write write, int iterate) throws IOException {
		if (rec == 0 || iterate <= 0)
			return;
		write.field("name", getName());
		IndexListeners fldListeners = getListeners();
		if (fldListeners != null) {
			write.sub("listeners");
			for (Listener sub : fldListeners)
				sub.output(write, iterate);
			write.endSub();
		}
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

	public Source parse(Parser parser) {
		while (parser.getSub()) {
			String name = parser.getString("name");
			int nextRec = new IndexSources(name).search();
			if (parser.isDelete(nextRec)) {
				try (ChangeSource record = new ChangeSource(this)) {
					store.free(record.getRec());
					record.setRec(0);
				}
				continue;
			}
			if (nextRec == 0) {
				try (ChangeSource record = new ChangeSource(store)) {
					record.setName(name);
					record.parseFields(parser);
					rec = record.rec;
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
		rec = nextRec;
		return nextRec != 0;
	}

	@Override
	public Object get(int field) {
		switch (field) {
		case 1:
			return getName();
		default:
			return null;
		}
	}

	@Override
	public Iterable<? extends RecordInterface> iterate(int field, Object... key) {
		switch (field) {
		case 2:
			if (key.length > 0)
				return new IndexListeners(new Listener(store), (int) key[0]);
			return getListeners();
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
			return "listeners";
		default:
			return null;
		}
	}

	@Override
	public boolean exists() {
		return getRec() != 0;
	}
}
