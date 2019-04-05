package org.memorydb.world;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.handler.MutationException;
import org.memorydb.structure.ChangeInterface;
import org.memorydb.structure.FieldData;
import org.memorydb.structure.MemoryRecord;
import org.memorydb.structure.RecordData;
import org.memorydb.structure.Store;

/**
 * Automatically generated record class for relation_specials
 */

@RecordData(name = "Specials")
public class Relation_specialsArray implements MemoryRecord, ChangeInterface, Iterable<Relation_specialsArray> {
	private final Store store;
	private final RelationArray parent;
	private final int idx;
	private int alloc;
	private int size;

	/* package private */ Relation_specialsArray(RelationArray parent, int idx) {
		this.store = parent.store();
		this.parent = parent;
		this.idx = idx;
		if (parent.rec() != 0) {
			this.alloc = store.getInt(parent.rec(), parent.index() * 20 + 16);
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

	/* package private */ Relation_specialsArray(Relation_specialsArray other, int idx) {
		this.store = other.store;
		this.parent = other.parent;
		this.idx = idx;
		this.alloc = other.alloc;
		this.size = other.size;
	}

	/* package private */ Relation_specialsArray(Store store, int rec, int idx) {
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
	public Relation_specialsArray copy(int rec) {
		assert store.validate(rec);
		return new Relation_specialsArray(store, rec, -1);
	}

	private void up(RelationArray record) {
		store.setInt(alloc, 8, record.rec());
		store.setInt(alloc, 12, record.index());
	}

	@Override
	public RelationArray up() {
		return new RelationArray(store, store.getInt(alloc, 8), store.getInt(alloc, 12));
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
	public Relation_specialsArray add() {
		if (parent.rec() == 0)
			return this;
		if (alloc == 0) {
			alloc = store.allocate(6 + 16);
			up(parent);
		} else
			alloc = store.resize(alloc, (16 + (idx + 1) * 6) / 8);
		store.setInt(parent.rec(), parent.index() * 20 + 16, alloc);
		size++;
		store.setInt(alloc, 4, size);
		Relation_specialsArray res = new Relation_specialsArray(parent, size - 1);
		res.setSpecial(null);
		res.setKnown(false);
		res.setLevel((byte) 0);
		return res;
	}

	@Override
	public Iterator<Relation_specialsArray> iterator() {
		return new Iterator<>() {
			int element = -1;

			@Override
			public boolean hasNext() {
				return alloc != 0 && element + 1 < size;
			}

			@Override
			public Relation_specialsArray next() {
				if (alloc == 0 || element > size)
					throw new NoSuchElementException();
				element++;
				return new Relation_specialsArray(Relation_specialsArray.this, element);
			}

			@Override
			public void remove() {
				if (alloc == 0 || element > size || element < 0)
					throw new NoSuchElementException();
				store.copy(alloc, (element + 1) * 6 + 16, element * 6 + 16, size * 17);
				element--;
				size--;
				store.setInt(alloc, 4, size);
			}
		};
	}

	@FieldData(name = "relation_specials", type = "ARRAY", related = Relation_specialsArray.class, mandatory = false)
	public Special getSpecial() {
		return new Special(store, alloc == 0 || idx < 0 || idx >= size ? 0 : store.getInt(alloc, idx * 6 + 16));
	}

	public void setSpecial(Special value) {
		if (alloc != 0 && idx >= 0 && idx < size) {
			store.setInt(alloc, idx * 6 + 16, value == null ? 0 : value.rec());
		}
	}

	@FieldData(name = "relation_specials", type = "ARRAY", related = Relation_specialsArray.class, mandatory = false)
	public boolean isKnown() {
		return alloc == 0 || idx < 0 || idx >= size ? false : (store.getByte(alloc, idx * 6 + 20) & 1) > 0;
	}

	public void setKnown(boolean value) {
		if (alloc != 0 && idx >= 0 && idx < size) {
			store.setByte(alloc, idx * 6 + 20, (store.getByte(alloc, idx * 6 + 20) & 254) + (value ? 1 : 0));
		}
	}

	@FieldData(name = "relation_specials", type = "ARRAY", related = Relation_specialsArray.class, mandatory = false)
	public byte getLevel() {
		return alloc == 0 || idx < 0 || idx >= size ? 0 : store.getByte(alloc, idx * 6 + 21);
	}

	public void setLevel(byte value) {
		if (alloc != 0 && idx >= 0 && idx < size) {
			store.setByte(alloc, idx * 6 + 21, value);
		}
	}

	@Override
	public void output(Write write, int iterate) {
		if (alloc == 0 || iterate <= 0)
			return;
		write.strField("special", "{" + getSpecial().keys() + "}");
		write.field("known", isKnown());
		write.field("level", getLevel());
		write.endRecord();
	}

	@Override
	public String toString() {
		Write write = new Write(new StringBuilder());
		if (idx == -1)
			for (Relation_specialsArray a : this) {
				a.output(write, 4);
			}
		else
			output(write, 4);
		return write.toString();
	}

	public void parse(Parser parser) {
		setSpecial(null);
		setKnown(false);
		setLevel((byte) 0);
		if (parser.hasField("special")) {
			parser.getRelation("special", (recNr, idx) -> {
				Special relRec = Special.parseKey(parser, store());
				Relation_specialsArray old = new Relation_specialsArray(store, recNr, idx);
				old.setSpecial(relRec);
				return relRec != null;
			}, idx);
		}
		if (parser.hasField("known")) {
			Boolean valueKnown = parser.getBoolean("known");
			if (valueKnown == null)
				throw new MutationException("Mandatory 'known' field");
			setKnown(valueKnown);
		}
		if (parser.hasField("level")) {
			setLevel((byte) parser.getInt("level"));
		}
	}

	@Override
	public String name() {
		int field = 0;
		if (idx == -1)
			return null;
		switch (field) {
		case 1:
			return "special";
		case 2:
			return "known";
		case 3:
			return "level";
		default:
			return null;
		}
	}

	@Override
	public FieldType type() {
		int field = 0;
		if (idx == -1)
			return field < 1 || field > size ? null : FieldType.OBJECT;
		switch (field) {
		case 1:
			return FieldType.OBJECT;
		case 2:
			return FieldType.BOOLEAN;
		case 3:
			return FieldType.INTEGER;
		default:
			return null;
		}
	}

	@Override
	public Object java() {
		int field = 0;
		if (idx == -1)
			return field < 1 || field > size ? null : new Relation_specialsArray(parent, field - 1);
		switch (field) {
		case 1:
			return getSpecial();
		case 2:
			return isKnown();
		case 3:
			return getLevel();
		default:
			return null;
		}
	}

	@Override
	public boolean java(Object value) {
		int field = 0;
		switch (field) {
		case 1:
			if (value instanceof Special)
				setSpecial((Special) value);
			return value instanceof Special;
		case 2:
			if (value instanceof Boolean)
				setKnown((Boolean) value);
			return value instanceof Boolean;
		case 3:
			if (value instanceof Byte)
				setLevel((Byte) value);
			return value instanceof Byte;
		default:
			return false;
		}
	}

	@Override
	public Relation_specialsArray index(int idx) {
		return null;
	}

	@Override
	public Relation_specialsArray start() {
		return null;
	}

	@Override
	public Relation_specialsArray next() {
		return null;
	}

	@Override
	public Relation_specialsArray copy() {
		return new Relation_specialsArray(parent, idx);
	}
}
