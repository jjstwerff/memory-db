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
 * Automatically generated record class for item_specials
 */

@RecordData(name = "Specials")
public class Item_specialsArray implements MemoryRecord, ChangeInterface, Iterable<Item_specialsArray> {
	private final Store store;
	private final Item parent;
	private final int idx;
	private int alloc;
	private int size;

	/* package private */ Item_specialsArray(Item parent, int idx) {
		this.store = parent.store();
		this.parent = parent;
		this.idx = idx;
		if (parent.rec() != 0) {
			this.alloc = store.getInt(parent.rec(), 12);
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

	/* package private */ Item_specialsArray(Item_specialsArray other, int idx) {
		this.store = other.store;
		this.parent = other.parent;
		this.idx = idx;
		this.alloc = other.alloc;
		this.size = other.size;
	}

	/* package private */ Item_specialsArray(Store store, int rec, int idx) {
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
	public Item_specialsArray copy(int newRec) {
		assert store.validate(newRec);
		return new Item_specialsArray(store, newRec, -1);
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
	public Item_specialsArray add() {
		if (parent.rec() == 0)
			return this;
		if (alloc == 0) {
			alloc = store.allocate(6 + 12);
			up(parent);
		} else
			alloc = store.resize(alloc, (12 + (size + 1) * 6) / 8);
		store.setInt(parent.rec(), 12, alloc);
		size++;
		store.setInt(alloc, 4, size);
		Item_specialsArray res = new Item_specialsArray(parent, size - 1);
		res.setSpecial(null);
		res.setKnown(false);
		res.setLevel((byte) 0);
		return res;
	}

	@Override
	public Iterator<Item_specialsArray> iterator() {
		return new Iterator<>() {
			int element = -1;

			@Override
			public boolean hasNext() {
				return alloc != 0 && element + 1 < size;
			}

			@Override
			public Item_specialsArray next() {
				if (alloc == 0 || element > size)
					throw new NoSuchElementException();
				element++;
				return new Item_specialsArray(Item_specialsArray.this, element);
			}

			@Override
			public void remove() {
				if (alloc == 0 || element > size || element < 0)
					throw new NoSuchElementException();
				store.copy(alloc, (element + 1) * 6 + 12, element * 6 + 12, size * 17);
				element--;
				size--;
				store.setInt(alloc, 4, size);
			}
		};
	}

	@FieldData(name = "item_specials", type = "ARRAY", related = Item_specialsArray.class, mandatory = false)
	public Special getSpecial() {
		return new Special(store, alloc == 0 || idx < 0 || idx >= size ? 0 : store.getInt(alloc, idx * 6 + 12));
	}

	public void setSpecial(Special value) {
		if (alloc != 0 && idx >= 0 && idx < size) {
			store.setInt(alloc, idx * 6 + 12, value == null ? 0 : value.rec());
		}
	}

	@FieldData(name = "item_specials", type = "ARRAY", related = Item_specialsArray.class, mandatory = false)
	public boolean isKnown() {
		return alloc == 0 || idx < 0 || idx >= size ? false : (store.getByte(alloc, idx * 6 + 16) & 1) > 0;
	}

	public void setKnown(boolean value) {
		if (alloc != 0 && idx >= 0 && idx < size) {
			store.setByte(alloc, idx * 6 + 16, (store.getByte(alloc, idx * 6 + 16) & 254) + (value ? 1 : 0));
		}
	}

	@FieldData(name = "item_specials", type = "ARRAY", related = Item_specialsArray.class, mandatory = false)
	public byte getLevel() {
		return alloc == 0 || idx < 0 || idx >= size ? 0 : store.getByte(alloc, idx * 6 + 17);
	}

	public void setLevel(byte value) {
		if (alloc != 0 && idx >= 0 && idx < size) {
			store.setByte(alloc, idx * 6 + 17, value);
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
			for (Item_specialsArray a : this) {
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
				Item_specialsArray old = new Item_specialsArray(store, recNr, idx);
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
		if (idx == -1)
			return null;
		switch (idx) {
		case 0:
			return "special";
		case 1:
			return "known";
		case 2:
			return "level";
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
			return FieldType.BOOLEAN;
		case 2:
			return FieldType.INTEGER;
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
			return getSpecial();
		case 1:
			return isKnown();
		case 2:
			return getLevel();
		default:
			return null;
		}
	}

	@Override
	public boolean java(Object value) {
		switch (idx) {
		case 0:
			if (value instanceof Special)
				setSpecial((Special) value);
			return value instanceof Special;
		case 1:
			if (value instanceof Boolean)
				setKnown((Boolean) value);
			return value instanceof Boolean;
		case 2:
			if (value instanceof Byte)
				setLevel((Byte) value);
			return value instanceof Byte;
		default:
			return false;
		}
	}

	@Override
	public Item_specialsArray index(int idx) {
		return idx < 0 || idx >= size ? null : new Item_specialsArray(parent, idx);
	}

	@Override
	public Item_specialsArray start() {
		return new Item_specialsArray(parent, 0);
	}

	@Override
	public Item_specialsArray next() {
		return idx + 1 >= size ? null : new Item_specialsArray(parent, idx + 1);
	}

	@Override
	public boolean testLast() {
		return idx == size - 1;
	}

	@Override
	public Item_specialsArray copy() {
		return new Item_specialsArray(parent, idx);
	}
}
