package org.memorydb.world;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.structure.ChangeInterface;
import org.memorydb.structure.FieldData;
import org.memorydb.structure.MemoryRecord;
import org.memorydb.structure.RecordData;
import org.memorydb.structure.Store;

/**
 * Automatically generated record class for relation
 */

@RecordData(name = "Relations")
public class RelationArray implements MemoryRecord, ChangeInterface, Iterable<RelationArray> {
	private final Store store;
	private final Item parent;
	private final int idx;
	private int alloc;
	private int size;

	/* package private */ RelationArray(Item parent, int idx) {
		this.store = parent.store();
		this.parent = parent;
		this.idx = idx;
		if (parent.rec() != 0) {
			this.alloc = store.getInt(parent.rec(), 16);
			if (alloc != 0) {
				up(parent);
				this.size = store.getInt(alloc, 4);
			} else
				this.size = 0;
		} else {
			this.alloc = 0;
			this.size = 0;
		}
		if (size > 0 && (idx < -1 || idx >= size))
			idx = -1;
	}

	/* package private */ RelationArray(RelationArray other, int idx) {
		this.store = other.store;
		this.parent = other.parent;
		this.idx = idx;
		this.alloc = other.alloc;
		this.size = other.size;
	}

	/* package private */ RelationArray(Store store, int rec, int idx) {
		this.store = store;
		this.alloc = rec;
		this.idx = idx;
		this.parent = up();
		this.size = alloc == 0 ? 0 : store.getInt(alloc, 4);
	}

	@Override
	public int rec() {
		return alloc;
	}

	@Override
	public int index() {
		return idx;
	}

	@Override
	public RelationArray copy(int newRec) {
		assert store.validate(newRec);
		return new RelationArray(store, newRec, -1);
	}

	private void up(Item record) {
		store.setInt(alloc, 8, record.rec());
	}

	@Override
	public Item up() {
		return new Item(store, store.getInt(alloc, 8));
	}

	@Override
	public Store store() {
		return store;
	}

	@Override
	public int size() {
		return size;
	}

	public void clear() {
		size = 0;
		store.setInt(alloc, 4, size);
	}

	@Override
	public RelationArray add() {
		if (parent.rec() == 0)
			return this;
		if (alloc == 0) {
			alloc = store.allocate(20 + 12);
			up(parent);
		} else
			alloc = store.resize(alloc, (12 + (size + 1) * 20) / 8);
		store.setInt(parent.rec(), 16, alloc);
		size++;
		store.setInt(alloc, 4, size);
		RelationArray res = new RelationArray(parent, size - 1);
		res.setType(null);
		res.setWith(null);
		res.setStarted(Integer.MIN_VALUE);
		res.setStopped(Integer.MIN_VALUE);
		store.setInt(rec(), 16, 0); // ARRAY relation_specials
		return res;
	}

	@Override
	public Iterator<RelationArray> iterator() {
		return new Iterator<>() {
			int element = -1;

			@Override
			public boolean hasNext() {
				return alloc != 0 && element + 1 < size;
			}

			@Override
			public RelationArray next() {
				if (alloc == 0 || element > size)
					throw new NoSuchElementException();
				element++;
				return new RelationArray(RelationArray.this, element);
			}

			@Override
			public void remove() {
				if (alloc == 0 || element > size || element < 0)
					throw new NoSuchElementException();
				store.copy(alloc, (element + 1) * 20 + 12, element * 20 + 12, size * 17);
				element--;
				size--;
				store.setInt(alloc, 4, size);
			}
		};
	}

	@FieldData(name = "relation", type = "ARRAY", related = RelationArray.class, mandatory = false)
	public State getType() {
		return new State(store, alloc == 0 || idx < 0 || idx >= size ? 0 : store.getInt(alloc, idx * 20 + 12));
	}

	public void setType(State value) {
		if (alloc != 0 && idx >= 0 && idx < size) {
			store.setInt(alloc, idx * 20 + 12, value == null ? 0 : value.rec());
		}
	}

	@FieldData(name = "relation", type = "ARRAY", related = RelationArray.class, mandatory = false)
	public Item getWith() {
		return new Item(store, alloc == 0 || idx < 0 || idx >= size ? 0 : store.getInt(alloc, idx * 20 + 16));
	}

	public void setWith(Item value) {
		if (alloc != 0 && idx >= 0 && idx < size) {
			store.setInt(alloc, idx * 20 + 16, value == null ? 0 : value.rec());
		}
	}

	@FieldData(name = "relation", type = "ARRAY", related = RelationArray.class, mandatory = false)
	public int getStarted() {
		return alloc == 0 || idx < 0 || idx >= size ? Integer.MIN_VALUE : store.getInt(alloc, idx * 20 + 20);
	}

	public void setStarted(int value) {
		if (alloc != 0 && idx >= 0 && idx < size) {
			store.setInt(alloc, idx * 20 + 20, value);
		}
	}

	@FieldData(name = "relation", type = "ARRAY", related = RelationArray.class, mandatory = false)
	public int getStopped() {
		return alloc == 0 || idx < 0 || idx >= size ? Integer.MIN_VALUE : store.getInt(alloc, idx * 20 + 24);
	}

	public void setStopped(int value) {
		if (alloc != 0 && idx >= 0 && idx < size) {
			store.setInt(alloc, idx * 20 + 24, value);
		}
	}

	@FieldData(name = "relation", type = "ARRAY", related = RelationArray.class, mandatory = false)
	public Relation_specialsArray getRelation_specials() {
		return alloc == 0 || idx < 0 || idx >= size ? null : new Relation_specialsArray(this, -1);
	}

	@Override
	public void output(Write write, int iterate) {
		if (alloc == 0 || iterate <= 0)
			return;
		write.strField("type", "{" + getType().keys() + "}");
		write.strField("with", "{" + getWith().keys() + "}");
		write.field("started", getStarted());
		write.field("stopped", getStopped());
		Relation_specialsArray fldRelation_specials = getRelation_specials();
		if (fldRelation_specials != null) {
			write.sub("relation_specials");
			for (Relation_specialsArray sub : fldRelation_specials)
				sub.output(write, iterate);
			write.endSub();
		}
		write.endRecord();
	}

	@Override
	public String toString() {
		Write write = new Write(new StringBuilder());
		if (idx == -1)
			for (RelationArray a : this) {
				a.output(write, 4);
			}
		else
			output(write, 4);
		return write.toString();
	}

	public void parse(Parser parser) {
		setType(null);
		setWith(null);
		setStarted(Integer.MIN_VALUE);
		setStopped(Integer.MIN_VALUE);
		store.setInt(rec(), 16, 0); // ARRAY relation_specials
		if (parser.hasField("type")) {
			parser.getRelation("type", (recNr, idx) -> {
				State relRec = State.parseKey(parser, store());
				RelationArray old = new RelationArray(store, recNr, idx);
				old.setType(relRec);
				return relRec != null;
			}, idx);
		}
		if (parser.hasField("with")) {
			parser.getRelation("with", (recNr, idx) -> {
				Item relRec = Item.parseKey(parser, store());
				RelationArray old = new RelationArray(store, recNr, idx);
				old.setWith(relRec);
				return relRec != null;
			}, idx);
		}
		if (parser.hasField("started")) {
			setStarted(parser.getInt("started"));
		}
		if (parser.hasField("stopped")) {
			setStopped(parser.getInt("stopped"));
		}
		if (parser.hasSub("relation_specials")) {
			Relation_specialsArray sub = new Relation_specialsArray(this, -1);
			while (parser.getSub())
				sub.add().parse(parser);
		}
	}

	@Override
	public String name() {
		if (idx == -1)
			return null;
		switch (idx) {
		case 0:
			return "type";
		case 1:
			return "with";
		case 2:
			return "started";
		case 3:
			return "stopped";
		case 4:
			return "relation_specials";
		default:
			return null;
		}
	}

	@Override
	public FieldType type() {
		if (idx == -1)
			return FieldType.OBJECT;
		switch (idx) {
		case 0:
			return FieldType.OBJECT;
		case 1:
			return FieldType.OBJECT;
		case 2:
			return FieldType.INTEGER;
		case 3:
			return FieldType.INTEGER;
		case 4:
			return FieldType.ARRAY;
		default:
			return null;
		}
	}

	@Override
	public Object java() {
		if (idx == -1)
			return this;
		switch (idx) {
		case 0:
			return getType();
		case 1:
			return getWith();
		case 2:
			return getStarted();
		case 3:
			return getStopped();
		default:
			return null;
		}
	}

	@Override
	public boolean java(Object value) {
		switch (idx) {
		case 0:
			if (value instanceof State)
				setType((State) value);
			return value instanceof State;
		case 1:
			if (value instanceof Item)
				setWith((Item) value);
			return value instanceof Item;
		case 2:
			if (value instanceof Integer)
				setStarted((Integer) value);
			return value instanceof Integer;
		case 3:
			if (value instanceof Integer)
				setStopped((Integer) value);
			return value instanceof Integer;
		default:
			return false;
		}
	}

	@Override
	public RelationArray index(int idx) {
		return idx < 0 || idx >= size ? null : new RelationArray(parent, idx);
	}

	@Override
	public RelationArray start() {
		return new RelationArray(parent, 0);
	}

	@Override
	public RelationArray next() {
		return idx + 1 >= size ? null : new RelationArray(parent, idx + 1);
	}

	@Override
	public boolean testLast() {
		return idx == size - 1;
	}

	@Override
	public RelationArray copy() {
		return new RelationArray(parent, idx);
	}
}
