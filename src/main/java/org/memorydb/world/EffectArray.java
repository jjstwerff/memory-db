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
 * Automatically generated record class for effect
 */

@RecordData(name = "Effect")
public class EffectArray implements MemoryRecord, ChangeInterface, Iterable<EffectArray> {
	private final Store store;
	private final Category parent;
	private final int idx;
	private int alloc;
	private int size;

	/* package private */ EffectArray(Category parent, int idx) {
		this.store = parent.store();
		this.parent = parent;
		this.idx = idx;
		if (parent.rec() != 0) {
			this.alloc = store.getInt(parent.rec(), 17);
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

	/* package private */ EffectArray(EffectArray other, int idx) {
		this.store = other.store;
		this.parent = other.parent;
		this.idx = idx;
		this.alloc = other.alloc;
		this.size = other.size;
	}

	/* package private */ EffectArray(Store store, int rec, int idx) {
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
	public EffectArray copy(int rec) {
		assert store.validate(rec);
		return new EffectArray(store, rec, -1);
	}

	private void up(Category record) {
		store.setInt(alloc, 8, record.rec());
	}

	@Override
	public Category up() {
		return new Category(store, store.getInt(alloc, 8));
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
	public EffectArray add() {
		if (parent.rec() == 0)
			return this;
		if (alloc == 0) {
			alloc = store.allocate(5 + 12);
			up(parent);
		} else
			alloc = store.resize(alloc, (12 + (idx + 1) * 5) / 8);
		store.setInt(parent.rec(), 17, alloc);
		size++;
		store.setInt(alloc, 4, size);
		EffectArray res = new EffectArray(parent, size - 1);
		res.setItem(null);
		res.setNumber((byte) 0);
		return res;
	}

	@Override
	public Iterator<EffectArray> iterator() {
		return new Iterator<>() {
			int element = -1;

			@Override
			public boolean hasNext() {
				return alloc != 0 && element + 1 < size;
			}

			@Override
			public EffectArray next() {
				if (alloc == 0 || element > size)
					throw new NoSuchElementException();
				element++;
				return new EffectArray(EffectArray.this, element);
			}

			@Override
			public void remove() {
				if (alloc == 0 || element > size || element < 0)
					throw new NoSuchElementException();
				store.copy(alloc, (element + 1) * 5 + 12, element * 5 + 12, size * 17);
				element--;
				size--;
				store.setInt(alloc, 4, size);
			}
		};
	}

	@FieldData(name = "effect", type = "ARRAY", related = EffectArray.class, mandatory = false)
	public Category getItem() {
		return new Category(store, alloc == 0 || idx < 0 || idx >= size ? 0 : store.getInt(alloc, idx * 5 + 12));
	}

	public void setItem(Category value) {
		if (alloc != 0 && idx >= 0 && idx < size) {
			store.setInt(alloc, idx * 5 + 12, value == null ? 0 : value.rec());
		}
	}

	@FieldData(name = "effect", type = "ARRAY", related = EffectArray.class, mandatory = false)
	public byte getNumber() {
		return alloc == 0 || idx < 0 || idx >= size ? 0 : store.getByte(alloc, idx * 5 + 16);
	}

	public void setNumber(byte value) {
		if (alloc != 0 && idx >= 0 && idx < size) {
			store.setByte(alloc, idx * 5 + 16, value);
		}
	}

	@Override
	public void output(Write write, int iterate) {
		if (alloc == 0 || iterate <= 0)
			return;
		write.strField("item", "{" + getItem().keys() + "}");
		write.field("number", getNumber());
		write.endRecord();
	}

	@Override
	public String toString() {
		Write write = new Write(new StringBuilder());
		if (idx == -1)
			for (EffectArray a : this) {
				a.output(write, 4);
			}
		else
			output(write, 4);
		return write.toString();
	}

	public void parse(Parser parser) {
		setItem(null);
		setNumber((byte) 0);
		if (parser.hasField("item")) {
			parser.getRelation("item", (recNr, idx) -> {
				Category relRec = Category.parseKey(parser, store());
				EffectArray old = new EffectArray(store, recNr, idx);
				old.setItem(relRec);
				return relRec != null;
			}, idx);
		}
		if (parser.hasField("number")) {
			setNumber((byte) parser.getInt("number"));
		}
	}

	@Override
	public String name() {
		int field = 0;
		if (idx == -1)
			return null;
		switch (field) {
		case 1:
			return "item";
		case 2:
			return "number";
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
			return FieldType.INTEGER;
		default:
			return null;
		}
	}

	@Override
	public Object java() {
		int field = 0;
		if (idx == -1)
			return field < 1 || field > size ? null : new EffectArray(parent, field - 1);
		switch (field) {
		case 1:
			return getItem();
		case 2:
			return getNumber();
		default:
			return null;
		}
	}

	@Override
	public boolean java(Object value) {
		int field = 0;
		switch (field) {
		case 1:
			if (value instanceof Category)
				setItem((Category) value);
			return value instanceof Category;
		case 2:
			if (value instanceof Byte)
				setNumber((Byte) value);
			return value instanceof Byte;
		default:
			return false;
		}
	}

	@Override
	public EffectArray index(int idx) {
		return null;
	}

	@Override
	public EffectArray start() {
		return null;
	}

	@Override
	public EffectArray next() {
		return null;
	}

	@Override
	public EffectArray copy() {
		return new EffectArray(parent, idx);
	}
}
