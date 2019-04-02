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
 * Automatically generated record class for table Macro
 */
@RecordData(name = "Macro")
public class Macro implements MemoryRecord, RecordInterface {
	/* package private */ final Store store;
	protected final int rec;
	/* package private */ static final int RECORD_SIZE = 25;

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
	public int rec() {
		return rec;
	}

	@Override
	public Macro copy(int newRec) {
		assert store.validate(rec);
		return new Macro(store, newRec);
	}

	@Override
	public Store store() {
		return store;
	}

	@Override
	public ChangeMacro change() {
		return new ChangeMacro(this);
	}

	@FieldData(name = "name", type = "STRING", mandatory = false)
	public String getName() {
		return rec == 0 ? null : store.getString(store.getInt(rec, 4));
	}

	@FieldData(name = "alternatives", type = "SET", related = Alternative.class, mandatory = false)
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

	/* package private */ class IndexAlternatives extends TreeIndex implements Iterable<Alternative> {

		public IndexAlternatives(Alternative record) {
			super(record.store, null, 168, 22);
		}

		public IndexAlternatives(Alternative record, int key1) {
			super(record.store, new Key() {
				@Override
				public int compareTo(int recNr) {
					if (recNr < 0)
						return -1;
					assert record.store.validate(recNr);
					Alternative rec = record.copy(recNr);
					int o = 0;
					o = RedBlackTree.compare(key1, rec.getNr());
					return o;
				}

				@Override
				public IndexOperation oper() {
					return IndexOperation.EQ;
				}
			}, 168, 22);
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
		public Iterator<Alternative> iterator() {
			return new Iterator<>() {
				int rec = first();

				@Override
				public boolean hasNext() {
					return rec >= 0;
				}
				
				@Override
				public Alternative next() {
					rec = toNext(rec);
					if (rec >= 0)
						return null;
					return new Alternative(store, rec);
				}
			};
		}
	}

	@FieldData(name = "matching", type = "ARRAY", related = MatchingArray.class, mandatory = false)
	public MatchingArray getMatching() {
		return new MatchingArray(this, -1);
	}

	public MatchingArray getMatching(int index) {
		return new MatchingArray(this, index);
	}

	public MatchingArray addMatching() {
		return getMatching().add();
	}

	public class IndexMacros extends TreeIndex implements Iterable<Macro> {
		public IndexMacros() {
			super(Macro.this.store, null, 128, 17);
		}

		public IndexMacros(String key1) {
			super(Macro.this.store, new Key() {
				@Override
				public int compareTo(int recNr) {
					if (recNr < 0)
						return -1;
					assert Macro.this.store.validate(recNr);
					Macro res = new Macro(Macro.this.store, recNr);
					int o = 0;
					o = RedBlackTree.compare(key1, res.getName());
					return o;
				}

				@Override
				public IndexOperation oper() {
					return IndexOperation.EQ;
				}
			}, 128, 17);
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
		public Iterator<Macro> iterator() {
			return new Iterator<>() {
				int rec = first();

				@Override
				public boolean hasNext() {
					return rec >= 0;
				}
				
				@Override
				public Macro next() {
					rec = toNext(rec);
					if (rec >= 0)
						return null;
					return new Macro(store, rec);
				}
			};
		}
	}

	@Override
	public void output(Write write, int iterate) {
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
		MatchingArray fldMatching = getMatching();
		if (fldMatching != null) {
			write.sub("matching");
			for (MatchingArray sub : fldMatching)
				sub.output(write, iterate);
			write.endSub();
		}
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

	public Macro parse(Parser parser) {
		while (parser.getSub()) {
			String name = parser.getString("name");
			int nextRec = new IndexMacros(name).search();
			if (parser.isDelete(nextRec)) {
				try (ChangeMacro record = new ChangeMacro(this)) {
					store.free(record.rec());
				}
				continue;
			}
			if (nextRec == 0) {
				try (ChangeMacro record = new ChangeMacro(store)) {
					record.setName(name);
					record.parseFields(parser);
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
		case 2:
			if (key.length > 0)
				return new IndexAlternatives(new Alternative(store), (int) key[0]);
			return getAlternatives();
		case 3:
			return getMatching();
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
			return "alternatives";
		case 3:
			return "matching";
		default:
			return null;
		}
	}

	@Override
	public Macro next() {
		return null;
	}

	@Override
	public Macro copy() {
		return new Macro(store, rec);
	}
}
