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
 * Automatically generated record class for table Macro
 */
@RecordData(
	name = "Macro",
	keyFields = {"name"})
public class Macro implements MemoryRecord, RecordInterface {
	/* package private */ Store store;
	protected int rec;
	/* package private */ static final int RECORD_SIZE = 21;

	public Macro(Store store) {
		this.store = store;
		this.rec = 0;
	}

	public Macro(Store store, int rec) {
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
	public ChangeMacro change() {
		return new ChangeMacro(this);
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
		name = "alternatives",
		type = "SET",
		keyNames = {"nr"},
		keyTypes = {"INTEGER"},
		related = Alternative.class,
		mandatory = false
	)
	public IndexAlternatives getAlternatives() {
		return new IndexAlternatives(new Alternative(store));
	}

	public Alternative getAlternatives(int key1) {
		Alternative resultRec = new Alternative(store);
		IndexAlternatives idx = new IndexAlternatives(resultRec, key1);
		int res = idx.search();
		if (res == 0)
			return resultRec;
		return new Alternative(store, res);
	}

	public ChangeAlternative addAlternatives() {
		return new ChangeAlternative(this, 0);
	}

	/* package private */ class IndexAlternatives extends TreeIndex<Alternative> {

		public IndexAlternatives(Alternative record) {
			super(record, null, 160, 21);
		}

		public IndexAlternatives(Alternative record, int key1) {
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
			}, 160, 21);
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
			Alternative recA = new Alternative(store, a);
			Alternative recB = new Alternative(store, b);
			int o = 0;
			o = compare(recA.getNr(), recB.getNr());
			return o;
		}

		@Override
		public Object get(int field) {
			return new Alternative(store, field);
		}
	}

	public class IndexMacros extends TreeIndex<Macro> {
		public IndexMacros() {
			super(Macro.this, null, 96, 13);
		}

		public IndexMacros(String key1) {
			super(Macro.this, new Key() {
				@Override
				public int compareTo(int recNr) {
					if (recNr < 0)
						return -1;
					assert store.validate(recNr);
					setRec(recNr);
					int o = 0;
					o = RedBlackTree.compare(key1, Macro.this.getName());
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
			return store.getInt(0, 16);
		}

		@Override
		protected void changeTop(int value) {
			store.setInt(0, 16, value);
		}

		@Override
		protected int compareTo(int a, int b) {
			Macro recA = new Macro(store, a);
			Macro recB = new Macro(store, b);
			int o = 0;
			o = compare(recA.getName(), recB.getName());
			return o;
		}

		@Override
		public Object get(int field) {
			return new Macro(store, field);
		}
	}

	@Override
	public void output(Write write, int iterate) throws IOException {
		if (rec == 0 || iterate <= 0)
			return;
		write.field("name", getName());
		IndexAlternatives fldAlternatives = getAlternatives();
		if (fldAlternatives != null) {
			write.sub("alternatives");
			for (Alternative sub : fldAlternatives)
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

	public Macro parse(Parser parser) {
		while (parser.getSub()) {
			String name = parser.getString("name");
			int nextRec = new IndexMacros(name).search();
			if (parser.isDelete(nextRec)) {
				try (ChangeMacro record = new ChangeMacro(this)) {
					store.free(record.getRec());
					record.setRec(0);
				}
				continue;
			}
			if (nextRec == 0) {
				try (ChangeMacro record = new ChangeMacro(store)) {
					record.setName(name);
					record.parseFields(parser);
					rec = record.rec;
				}
			} else {
				rec = nextRec;
				try (ChangeMacro record = new ChangeMacro(this)) {
					record.parseFields(parser);
				}
			}
		}
		return this;
	}

	public boolean parseKey(Parser parser) {
		String name = parser.getString("name");
		int nextRec = new IndexMacros(name).search();
		parser.finishRelation();
		if (nextRec != 0)
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
				return new IndexAlternatives(new Alternative(store), (int) key[0]);
			return getAlternatives();
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
			return "alternatives";
		default:
			return null;
		}
	}

	@Override
	public boolean exists() {
		return getRec() != 0;
	}
}
