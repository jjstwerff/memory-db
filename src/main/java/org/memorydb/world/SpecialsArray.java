package org.memorydb.world;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.structure.ChangeInterface;
import org.memorydb.structure.FieldData;
import org.memorydb.structure.MemoryRecord;
import org.memorydb.structure.RecordData;
import org.memorydb.structure.RecordInterface;
import org.memorydb.structure.Store;

/**
 * Automatically generated record class for specials
 */

@RecordData(name = "Specials")
public class SpecialsArray implements MemoryRecord, ChangeInterface, Iterable<SpecialsArray> {
	private final Store store;
	private final Item parent;
	private final int idx;
	private int alloc;
	private int size;

	/* package private */ SpecialsArray(Item parent, int idx) {
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

	/* package private */ SpecialsArray(SpecialsArray other, int idx) {
		this.store = other.store;
		this.parent = other.parent;
		this.idx = idx;
		this.alloc = other.alloc;
		this.size = other.size;
	}

	/* package private */ SpecialsArray(Store store, int rec, int idx) {
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
	public void rec(int rec) {
		this.alloc = rec;
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
	public SpecialsArray add() {
		if (parent.rec() == 0)
			return this;
		idx = size;
		if (alloc == 0) {
			alloc = store.allocate(6 + 12);
			up(parent);
		} else
			alloc = store.resize(alloc, (12 + (idx + 1) * 6) / 8);
		store.setInt(parent.rec(), 12, alloc);
		size = idx + 1;
		store.setInt(alloc, 4, size);
		setSpecial(null);
		setKnown(false);
		setLevel((byte) 0);
		return this;
	}

	@Override
	public Iterator<SpecialsArray> iterator() {
		return new Iterator<>() {
			int element = -1;

			@Override
			public boolean hasNext() {
				return alloc != 0 && element + 1 < size;
			}

			@Override
			public SpecialsArray next() {
				if (alloc == 0 || element > size)
					throw new NoSuchElementException();
				element++;
				return new SpecialsArray(SpecialsArray.this, element);
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

	@FieldData(name = "specials", type = "ARRAY", related = SpecialsArray.class, mandatory = false)
	public Special getSpecial() {
		return new Special(store, alloc == 0 || idx < 0 || idx >= size ? 0 : store.getInt(alloc, idx * 6 + 12));
	}

	public void setSpecial(Special value) {
		if (alloc != 0 && idx >= 0 && idx < size) {
			store.setInt(alloc, idx * 6 + 12, value == null ? 0 : value.rec());
		}
	}

	@FieldData(name = "specials", type = "ARRAY", related = SpecialsArray.class, mandatory = false)
	public boolean isKnown() {
		return alloc == 0 || idx < 0 || idx >= size ? false : (store.getByte(alloc, idx * 6 + 16) & 1) > 0;
	}

	public void setKnown(boolean value) {
		if (alloc != 0 && idx >= 0 && idx < size) {
			store.setByte(alloc, idx * 6 + 16, (store.getByte(alloc, idx * 6 + 16) & 254) + (value ? 1 : 0));
		}
	}

	@FieldData(name = "specials", type = "ARRAY", related = SpecialsArray.class, mandatory = false)
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
			for (SpecialsArray a : this) {
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
				Special relRec = new Special(store);
				boolean found = relRec.parseKey(parser);
				setSpecial(relRec);
				return found;
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
	public void close() {
		// nothing
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
			return field < 1 || field > size ? null : new SpecialsArray(parent, field - 1);
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
	public RecordInterface next() {
		return null;
	}

	@Override
	public SpecialsArray copy() {
		return new SpecialsArray(parent, idx);
	}
}