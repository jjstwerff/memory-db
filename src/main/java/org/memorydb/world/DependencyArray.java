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
 * Automatically generated record class for dependency
 */

@RecordData(name = "Dependency")
public class DependencyArray implements MemoryRecord, ChangeInterface, Iterable<DependencyArray> {
	private final Store store;
	private final Category parent;
	private final int idx;
	private int alloc;
	private int size;

	/* package private */ DependencyArray(Category parent, int idx) {
		this.store = parent.store();
		this.parent = parent;
		this.idx = idx;
		if (parent.rec() != 0) {
			this.alloc = store.getInt(parent.rec(), 13);
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

	/* package private */ DependencyArray(DependencyArray other, int idx) {
		this.store = other.store;
		this.parent = other.parent;
		this.idx = idx;
		this.alloc = other.alloc;
		this.size = other.size;
	}

	/* package private */ DependencyArray(Store store, int rec, int idx) {
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
	public DependencyArray copy(int newRec) {
		assert store.validate(newRec);
		return new DependencyArray(store, newRec, -1);
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
	public DependencyArray add() {
		if (parent.rec() == 0)
			return this;
		if (alloc == 0) {
			alloc = store.allocate(5 + 12);
			up(parent);
		} else
			alloc = store.resize(alloc, (12 + (size + 1) * 5) / 8);
		store.setInt(parent.rec(), 13, alloc);
		size++;
		store.setInt(alloc, 4, size);
		DependencyArray res = new DependencyArray(parent, size - 1);
		res.setOn(null);
		res.setNumber((byte) 0);
		return res;
	}

	@Override
	public Iterator<DependencyArray> iterator() {
		return new Iterator<>() {
			int element = -1;

			@Override
			public boolean hasNext() {
				return alloc != 0 && element + 1 < size;
			}

			@Override
			public DependencyArray next() {
				if (alloc == 0 || element > size)
					throw new NoSuchElementException();
				element++;
				return new DependencyArray(DependencyArray.this, element);
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

	@FieldData(name = "dependency", type = "ARRAY", related = DependencyArray.class, mandatory = false)
	public Category getOn() {
		return new Category(store, alloc == 0 || idx < 0 || idx >= size ? 0 : store.getInt(alloc, idx * 5 + 12));
	}

	public void setOn(Category value) {
		if (alloc != 0 && idx >= 0 && idx < size) {
			store.setInt(alloc, idx * 5 + 12, value == null ? 0 : value.rec());
		}
	}

	@FieldData(name = "dependency", type = "ARRAY", related = DependencyArray.class, mandatory = false)
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
		write.strField("on", "{" + getOn().keys() + "}");
		write.field("number", getNumber());
		write.endRecord();
	}

	@Override
	public String toString() {
		Write write = new Write(new StringBuilder());
		if (idx == -1)
			for (DependencyArray a : this) {
				a.output(write, 4);
			}
		else
			output(write, 4);
		return write.toString();
	}

	public void parse(Parser parser) {
		setOn(null);
		setNumber((byte) 0);
		if (parser.hasField("on")) {
			parser.getRelation("on", (recNr, idx) -> {
				Category relRec = Category.parseKey(parser, store());
				DependencyArray old = new DependencyArray(store, recNr, idx);
				old.setOn(relRec);
				return relRec != null;
			}, idx);
		}
		if (parser.hasField("number")) {
			setNumber((byte) parser.getInt("number"));
		}
	}

	@Override
	public String name() {
		if (idx == -1)
			return null;
		switch (idx) {
		case 0:
			return "on";
		case 1:
			return "number";
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
			return getOn();
		case 1:
			return getNumber();
		default:
			return null;
		}
	}

	@Override
	public boolean java(Object value) {
		switch (idx) {
		case 0:
			if (value instanceof Category)
				setOn((Category) value);
			return value instanceof Category;
		case 1:
			if (value instanceof Byte)
				setNumber((Byte) value);
			return value instanceof Byte;
		default:
			return false;
		}
	}

	@Override
	public DependencyArray index(int idx) {
		return idx < 0 || idx >= size ? null : new DependencyArray(parent, idx);
	}

	@Override
	public DependencyArray start() {
		return new DependencyArray(parent, 0);
	}

	@Override
	public DependencyArray next() {
		return idx + 1 >= size ? null : new DependencyArray(parent, idx + 1);
	}

	@Override
	public boolean testLast() {
		return idx == size - 1;
	}

	@Override
	public DependencyArray copy() {
		return new DependencyArray(parent, idx);
	}
}
