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
 * Automatically generated record class for table Item
 */
@RecordData(name = "Item")
public class Item implements MemoryRecord, RecordInterface {
	/* package private */ final Store store;
	protected final int rec;
	private final int field;
	/* package private */ static final int RECORD_SIZE = 33;

	public Item(Store store, int rec) {
		rec = store.correct(rec);
		this.store = store;
		this.rec = rec;
		this.field = 0;
	}

	public Item(Store store, int rec, int field) {
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
	public Item copy(int newRec) {
		assert store.validate(newRec);
		return new Item(store, newRec);
	}

	@Override
	public Store store() {
		return store;
	}

	@Override
	public ChangeItem change() {
		return new ChangeItem(this);
	}

	@FieldData(name = "category", type = "RELATION", related = Category.class, mandatory = false)
	public Category getCategory() {
		return new Category(store, rec == 0 ? 0 : store.getInt(rec, 4));
	}

	@FieldData(name = "number", type = "INTEGER", mandatory = false)
	public int getNumber() {
		return rec == 0 ? Integer.MIN_VALUE : store.getInt(rec, 8);
	}

	@FieldData(name = "item_specials", type = "ARRAY", related = Item_specialsArray.class, mandatory = false)
	public Item_specialsArray getItem_specials() {
		return new Item_specialsArray(this, -1);
	}

	public Item_specialsArray getItem_specials(int index) {
		return new Item_specialsArray(this, index);
	}

	public Item_specialsArray addItem_specials() {
		return getItem_specials().add();
	}

	@FieldData(name = "relation", type = "ARRAY", related = RelationArray.class, mandatory = false)
	public RelationArray getRelation() {
		return new RelationArray(this, -1);
	}

	public RelationArray getRelation(int index) {
		return new RelationArray(this, index);
	}

	public RelationArray addRelation() {
		return getRelation().add();
	}

	@FieldData(name = "position", type = "INTEGER", mandatory = false)
	public int getPosition() {
		return rec == 0 ? Integer.MIN_VALUE : store.getInt(rec, 20);
	}

	public static class IndexItems extends TreeIndex implements Iterable<Item> {
		public IndexItems(Store store) {
			super(store, null, 192, 25);
		}

		public IndexItems(Store store, int key1) {
			super(store, new Key() {
				@Override
				public int compareTo(int recNr) {
					if (recNr < 0)
						return -1;
					assert store.validate(recNr);
					Item rec = new Item(store, recNr);
					int o = 0;
					o = RedBlackTree.compare(key1, rec.getNumber());
					return o;
				}

				@Override
				public IndexOperation oper() {
					return IndexOperation.EQ;
				}
			}, 192, 25);
		}

		private IndexItems(Store store, Key key, int flag, int field) {
			super(store, key, flag, field);
		}

		@Override
		public IndexItems copy() {
			return new IndexItems(store, key, flag, field);
		}

		@Override
		public Item field(String name) {
			try {
				int r = new IndexItems(store, Integer.parseInt(name)).search();
				return r <= 0 ? null : new IterRecord(store, r);
			} catch (NumberFormatException e) {
				return null;
			}
		}

		@Override
		public Item start() {
			int r = first();
			return r <= 0 ? null : new IterRecord(store, r);
		}

		private class IterRecord extends Item {
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
			return store.getInt(0, 20);
		}

		@Override
		protected void changeTop(int value) {
			store.setInt(0, 20, value);
		}

		@Override
		protected int compareTo(int a, int b) {
			Item recA = new Item(store, a);
			Item recB = new Item(store, b);
			int o = 0;
			o = compare(recA.getNumber(), recB.getNumber());
			return o;
		}

		@Override
		public Iterator<Item> iterator() {
			return new Iterator<>() {
				int nextRec = search();

				@Override
				public boolean hasNext() {
					return nextRec > 0;
				}

				@Override
				public Item next() {
					int n = nextRec;
					nextRec = toNext(nextRec);
					return n <= 0 ? null : new Item(store, n);
				}
			};
		}
	}

	@Override
	public void output(Write write, int iterate) {
		if (rec == 0 || iterate <= 0)
			return;
		write.field("category", getCategory());
		write.field("number", getNumber());
		Item_specialsArray fldItem_specials = getItem_specials();
		if (fldItem_specials != null) {
			write.sub("item_specials");
			for (Item_specialsArray sub : fldItem_specials)
				sub.output(write, iterate);
			write.endSub();
		}
		RelationArray fldRelation = getRelation();
		if (fldRelation != null) {
			write.sub("relation");
			for (RelationArray sub : fldRelation)
				sub.output(write, iterate);
			write.endSub();
		}
		write.field("position", getPosition());
		write.endRecord();
	}

	@Override
	public String keys() {
		StringBuilder res = new StringBuilder();
		if (rec == 0)
			return "";
		res.append("Number").append("=").append(getNumber());
		return res.toString();
	}

	@Override
	public String toString() {
		Write write = new Write(new StringBuilder());
		output(write, 4);
		return write.toString();
	}

	public static Item parse(Parser parser, Store store) {
		Item rec = null;
		while (parser.getSub()) {
			rec = parseKey(parser, store);
			if (parser.isDelete()) {
				if (rec != null)
					try (ChangeItem record = new ChangeItem(rec)) {
						store.free(record.rec());
					}
				continue;
			}
			if (rec == null) {
				try (ChangeItem record = new ChangeItem(store, 0)) {
					int number = parser.getInt("number");
					record.setNumber(number);
					record.parseFields(parser);
					return record;
				}
			} else {
				try (ChangeItem record = new ChangeItem(rec)) {
					record.parseFields(parser);
				}
			}
		}
		return rec;
	}

	public static Item parseKey(Parser parser, Store store) {
		int number = parser.getInt("number");
		int nextRec = new IndexItems(store, number).search();
		parser.finishRelation();
		return nextRec <= 0 ? null : new Item(store, nextRec);
	}

	@Override
	public Object java() {
		switch (field) {
		case 1:
			return getCategory();
		case 2:
			return getNumber();
		case 5:
			return getPosition();
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
			return FieldType.OBJECT;
		case 2:
			return FieldType.INTEGER;
		case 3:
			return FieldType.ARRAY;
		case 4:
			return FieldType.ARRAY;
		case 5:
			return FieldType.INTEGER;
		default:
			return null;
		}
	}

	@Override
	public String name() {
		switch (field) {
		case 1:
			return "category";
		case 2:
			return "number";
		case 3:
			return "item_specials";
		case 4:
			return "relation";
		case 5:
			return "position";
		default:
			return null;
		}
	}

	@Override
	public Item start() {
		return new Item(store, rec, 1);
	}

	@Override
	public Item next() {
		return field >= 5 ? null : new Item(store, rec, field + 1);
	}

	@Override
	public Item copy() {
		return new Item(store, rec, field);
	}
}
