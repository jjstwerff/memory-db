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
import java.util.HashMap;
import java.util.Map;

/**
 * Automatically generated record class for table State
 */
@RecordData(name = "State")
public class State implements MemoryRecord, RecordInterface {
	/* package private */ final Store store;
	protected final int rec;
	private final int field;
	/* package private */ static final int RECORD_SIZE = 19;

	public State(Store store, int rec) {
		rec = store.correct(rec);
		this.store = store;
		this.rec = rec;
		this.field = 0;
	}

	public State(Store store, int rec, int field) {
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
	public State copy(int newRec) {
		assert store.validate(newRec);
		return new State(store, newRec);
	}

	@Override
	public Store store() {
		return store;
	}

	@Override
	public ChangeState change() {
		return new ChangeState(this);
	}

	public enum Type {
		OWNER, FAMILY, ROMANCE, MEMBER, FRIENDSHIP, FAVOR, FACT, JOB, POLITICS;

		private static Map<String, Type> map = new HashMap<>();

		static {
			for (Type tp : Type.values()) {
				map.put(tp.toString(), tp);
			}
		}

		public static Type get(String val) {
			return map.containsKey(val) ? map.get(val) : null;
		}
	}

	@FieldData(name = "type", type = "ENUMERATE", enumerate = { "OWNER", "FAMILY", "ROMANCE", "MEMBER", "FRIENDSHIP", "FAVOR", "FACT", "JOB", "POLITICS" }, mandatory = false)
	public State.Type getType() {
		int data = rec == 0 ? 0 : store.getByte(rec, 4) & 31;
		if (data <= 0 || data > Type.values().length)
			return null;
		return Type.values()[data - 1];
	}

	@FieldData(name = "level", type = "BYTE", mandatory = false)
	public byte getLevel() {
		return rec == 0 ? 0 : store.getByte(rec, 5);
	}

	@FieldData(name = "name", type = "STRING", mandatory = false)
	public String getName() {
		return rec == 0 ? null : store.getString(store.getInt(rec, 6));
	}

	public static class IndexRelations extends TreeIndex implements Iterable<State> {
		public IndexRelations(Store store) {
			super(store, null, 80, 11);
		}

		public IndexRelations(Store store, String key1) {
			super(store, new Key() {
				@Override
				public int compareTo(int recNr) {
					if (recNr < 0)
						return -1;
					assert store.validate(recNr);
					State rec = new State(store, recNr);
					int o = 0;
					o = RedBlackTree.compare(key1, rec.getName());
					return o;
				}

				@Override
				public IndexOperation oper() {
					return IndexOperation.EQ;
				}
			}, 80, 11);
		}

		private IndexRelations(Store store, Key key, int flag, int field) {
			super(store, key, flag, field);
		}

		@Override
		public IndexRelations copy() {
			return new IndexRelations(store, key, flag, field);
		}

		@Override
		public State field(String name) {
			int r = new IndexRelations(store, name).search();
			return r <= 0 ? null : new IterRecord(store, r);
		}

		@Override
		public State start() {
			int r = first();
			return r <= 0 ? null : new IterRecord(store, r);
		}

		private class IterRecord extends State {
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
			return store.getInt(0, 16);
		}

		@Override
		protected void changeTop(int value) {
			store.setInt(0, 16, value);
		}

		@Override
		protected int compareTo(int a, int b) {
			State recA = new State(store, a);
			State recB = new State(store, b);
			int o = 0;
			o = compare(recA.getName(), recB.getName());
			return o;
		}

		@Override
		public Iterator<State> iterator() {
			return new Iterator<>() {
				int nextRec = search();

				@Override
				public boolean hasNext() {
					return nextRec > 0;
				}

				@Override
				public State next() {
					int n = nextRec;
					nextRec = toNext(nextRec);
					return n <= 0 ? null : new State(store, n);
				}
			};
		}
	}

	@Override
	public void output(Write write, int iterate) {
		if (rec == 0 || iterate <= 0)
			return;
		write.field("type", getType());
		write.field("level", getLevel());
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

	public static State parse(Parser parser, Store store) {
		State rec = null;
		while (parser.getSub()) {
			rec = parseKey(parser, store);
			if (parser.isDelete()) {
				if (rec != null)
					try (ChangeState record = new ChangeState(rec)) {
						store.free(record.rec());
					}
				continue;
			}
			if (rec == null) {
				try (ChangeState record = new ChangeState(store, 0)) {
					String name = parser.getString("name");
					record.setName(name);
					record.parseFields(parser);
					return record;
				}
			} else {
				try (ChangeState record = new ChangeState(rec)) {
					record.parseFields(parser);
				}
			}
		}
		return rec;
	}

	public static State parseKey(Parser parser, Store store) {
		String name = parser.getString("name");
		int nextRec = new IndexRelations(store, name).search();
		parser.finishRelation();
		return nextRec <= 0 ? null : new State(store, nextRec);
	}

	@Override
	public Object java() {
		switch (field) {
		case 1:
			return getType();
		case 2:
			return getLevel();
		case 3:
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
		case 2:
			return FieldType.INTEGER;
		case 3:
			return FieldType.STRING;
		default:
			return null;
		}
	}

	@Override
	public String name() {
		switch (field) {
		case 1:
			return "type";
		case 2:
			return "level";
		case 3:
			return "name";
		default:
			return null;
		}
	}

	@Override
	public State start() {
		return new State(store, rec, 1);
	}

	@Override
	public State next() {
		return field >= 3 ? null : new State(store, rec, field + 1);
	}

	@Override
	public State copy() {
		return new State(store, rec, field);
	}
}
