package org.memorydb.world;

import org.memorydb.file.Parser;
import org.memorydb.structure.ChangeInterface;

/**
 * Automatically generated record class for table Item
 */
public class ChangeItem extends Item implements ChangeInterface {
	/* package private */ ChangeItem(Relations parent, int rec) {
		super(parent.store(), rec);
		if (rec == 0) {
			rec(store().allocate(Item.RECORD_SIZE));
		}
		setCategory(null);
		setNumber(0);
		store.setInt(rec(), 12, 0); // ARRAY specials
		store.setInt(rec(), 16, 0); // ARRAY relation
		setPosition(0);
		up(parent);
		if (rec != 0) {
			up().new IndexWith(this).remove(rec);
		}
	}

	/* package private */ ChangeItem(Item current) {
		super(current.store, current.rec);
		if (rec != 0) {
			up().new IndexWith(this).remove(rec);
		}
	}

	public void setCategory(Category value) {
		store.setInt(rec, 4, value == null ? 0 : value.rec());
	}

	public void setNumber(int value) {
		store.setInt(rec, 8, value);
	}

	public void moveSpecials(ChangeItem other) {
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

	private void up(Relations value) {
		store.setInt(rec, 33, value == null ? 0 : value.rec());
		store.setInt(rec, 38, value == null ? 0 : value.index());
	}

	/* package private */ void parseFields(Parser parser) {
		if (parser.hasField("category")) {
			parser.getRelation("category", (recNr, idx) -> {
				Category relRec = new Category(store);
				boolean found = relRec.parseKey(parser);
				rec(recNr);
				setCategory(relRec);
				return found;
			}, rec());
		}
		if (parser.hasField("number")) {
			setNumber(parser.getInt("number"));
		}
		if (parser.hasSub("specials")) {
			try (SpecialsArray sub = new SpecialsArray(this, -1)) {
				while (parser.getSub()) {
					sub.add();
					sub.parse(parser);
				}
			}
		}
		if (parser.hasSub("relation")) {
			try (RelationArray sub = new RelationArray(this, -1)) {
				while (parser.getSub()) {
					sub.add();
					sub.parse(parser);
				}
			}
		}
		if (parser.hasField("position")) {
			setPosition(parser.getInt("position"));
		}
	}

	@Override
	public void close() {
		up().new IndexWith(this).insert(rec());
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
			return addSpecials();
		case 4:
			return addRelation();
		default:
			return null;
		}
	}
}