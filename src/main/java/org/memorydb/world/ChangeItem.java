package org.memorydb.world;

import org.memorydb.file.Parser;
import org.memorydb.structure.Store;
import org.memorydb.structure.ChangeInterface;

/**
 * Automatically generated record class for table Item
 */
public class ChangeItem extends Item implements AutoCloseable, ChangeInterface {
	public ChangeItem(Store store, int rec) {
		super(store, rec == 0 ? store.allocate(Item.RECORD_SIZE) : rec);
		if (rec == 0) {
			setCategory(null);
			setNumber(Integer.MIN_VALUE);
			store.setInt(rec(), 12, 0); // ARRAY item_specials
			store.setInt(rec(), 16, 0);
			store.setInt(rec(), 16, 0); // ARRAY relation
			store.setInt(rec(), 20, 0);
			setPosition(Integer.MIN_VALUE);
		} else {
			new IndexItems(store).remove(rec());
		}
	}

	public ChangeItem(Item current) {
		super(current.store(), current.rec());
		new IndexItems(store).remove(rec());
	}

	public void setCategory(Category value) {
		store.setInt(rec, 4, value == null ? 0 : value.rec());
	}

	public void setNumber(int value) {
		store.setInt(rec, 8, value);
	}

	public void moveItem_specials(ChangeItem other) {
		store().setInt(rec(), 12, store().getInt(other.rec(), 12));
		store().setInt(other.rec(), 12, 0);
	}

	public void moveRelation(ChangeItem other) {
		store().setInt(rec(), 16, store().getInt(other.rec(), 16));
		store().setInt(other.rec(), 16, 0);
	}

	public void setPosition(int value) {
		store.setInt(rec, 20, value);
	}

	/* package private */ void parseFields(Parser parser) {
		if (parser.hasField("category")) {
			parser.getRelation("category", (recNr, idx) -> {
				Category relRec = Category.parseKey(parser, store());
				try (ChangeItem old = this.copy(recNr)) {
					old.setCategory(relRec);
				}
				return relRec != null;
			}, rec());
		}
		if (parser.hasSub("item_specials")) {
			Item_specialsArray sub = new Item_specialsArray(this, -1);
			while (parser.getSub())
				sub.add().parse(parser);
		}
		if (parser.hasSub("relation")) {
			RelationArray sub = new RelationArray(this, -1);
			while (parser.getSub())
				sub.add().parse(parser);
		}
		if (parser.hasField("position")) {
			setPosition(parser.getInt("position"));
		}
	}

	@Override
	public void close() {
		new IndexItems(store).insert(rec());
	}

	@Override
	public boolean java(Object value) {
		int field = 0;
		switch (field) {
		case 1:
			if (value instanceof Category)
				setCategory((Category) value);
			return value instanceof Category;
		case 2:
			if (value instanceof Integer)
				setNumber((Integer) value);
			return value instanceof Integer;
		case 5:
			if (value instanceof Integer)
				setPosition((Integer) value);
			return value instanceof Integer;
		default:
			return false;
		}
	}

	@Override
	public ChangeInterface add() {
		int field = 0;
		switch (field) {
		case 3:
			return addItem_specials();
		case 4:
			return addRelation();
		default:
			return null;
		}
	}

	@Override
	public ChangeItem copy(int newRec) {
		assert store.validate(newRec);
		return new ChangeItem(store, newRec);
	}
}
