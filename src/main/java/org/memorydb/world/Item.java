package org.memorydb.world;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.structure.FieldData;
import org.memorydb.structure.MemoryRecord;
import org.memorydb.structure.RecordData;
import org.memorydb.structure.RecordInterface;
import org.memorydb.structure.Store;

/**
 * Automatically generated record class for table Item
 */
@RecordData(name = "Item")
public class Item implements MemoryRecord, RecordInterface {
	/* package private */ final Store store;
	protected final int rec;
	/* package private */ static final int RECORD_SIZE = 37;

	public Item(Store store) {
		this.store = store;
		this.rec = 0;
	}

	public Item(Store store, int rec) {
		rec = store.correct(rec);
		this.store = store;
		this.rec = rec;
	}

	@Override
	public int rec() {
		return rec;
	}

	@Override
	public Item copy(int newRec) {
		assert store.validate(rec);
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

	@FieldData(name = "specials", type = "ARRAY", related = SpecialsArray.class, mandatory = false)
	public SpecialsArray getSpecials() {
		return new SpecialsArray(this, -1);
	}

	public SpecialsArray getSpecials(int index) {
		return new SpecialsArray(this, index);
	}

	public SpecialsArray addSpecials() {
		return getSpecials().add();
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

	@Override
	public Relations up() {
		return new Relations(store, rec == 0 ? 0 : store.getInt(rec, 33));
	}

	@Override
	public void output(Write write, int iterate) {
		if (rec == 0 || iterate <= 0)
			return;
		write.field("category", getCategory());
		write.field("number", getNumber());
		SpecialsArray fldSpecials = getSpecials();
		if (fldSpecials != null) {
			write.sub("specials");
			for (SpecialsArray sub : fldSpecials)
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
		res.append("Relations").append("{").append(up().keys()).append("}");
		return res.toString();
	}

	@Override
	public String toString() {
		Write write = new Write(new StringBuilder());
		output(write, 4);
		return write.toString();
	}

	public Item parse(Parser parser, Relations parent) {
		while (parser.getSub()) {
			int nextRec = parent.new IndexWith(this, ).search();
			if (parser.isDelete(nextRec)) {
				try (ChangeItem record = new ChangeItem(this)) {
					store.free(record.rec());
					record.rec(0);
				}
				continue;
			}
			if (nextRec == 0) {
				try (ChangeItem record = new ChangeItem(parent, 0)) {

					record.parseFields(parser);
					rec = record.rec;
				}
			} else {
				rec = nextRec;
				try (ChangeItem record = new ChangeItem(this)) {
					record.parseFields(parser);
				}
			}
		}
		return this;
	}

	public boolean parseKey(Parser parser) {
		Relations parent = up();
		int nextRec = parent.new IndexWith(this, ).search();
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
			return getCategory();
		case 2:
			return getNumber();
		case 5:
			return getPosition();
		default:
			return null;
		}
	}

	public Iterable<? extends RecordInterface> iterate(int field, Object... key) {
		switch (field) {
		case 3:
			return getSpecials();
		case 4:
			return getRelation();
		default:
			return null;
		}
	}

	@Override
	public FieldType type() {
		int field = 0;
		switch (field) {
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
		int field = 0;
		switch (field) {
		case 1:
			return "category";
		case 2:
			return "number";
		case 3:
			return "specials";
		case 4:
			return "relation";
		case 5:
			return "position";
		default:
			return null;
		}
	}

	@Override
	public Item next() {
		return null;
	}

	@Override
	public Item copy() {
		return new Item(store, rec);
	}
}
