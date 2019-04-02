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
	public void rec(int rec) {
		this.alloc = rec;
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
		idx = size;
		if (alloc == 0) {
			alloc = store.allocate(5 + 12);
			up(parent);
		} else
			alloc = store.resize(alloc, (12 + (idx + 1) * 5) / 8);
		store.setInt(parent.rec(), 13, alloc);
		size = idx + 1;
		store.setInt(alloc, 4, size);
		setOn(null);
		setNumber((byte) 0);
		return this;
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
				Category relRec = new Category(store);
				boolean found = relRec.parseKey(parser);
				setOn(relRec);
				return found;
			}, idx);
		}
		if (parser.hasField("number")) {
			setNumber((byte) parser.getInt("number"));
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
			return "on";
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
			return field < 1 || field > size ? null : new DependencyArray(parent, field - 1);
		switch (field) {
		case 1:
			return getOn();
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
				setOn((Category) value);
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
	public RecordInterface next() {
		return null;
	}

	@Override
	public DependencyArray copy() {
		return new DependencyArray(parent, idx);
	}
}