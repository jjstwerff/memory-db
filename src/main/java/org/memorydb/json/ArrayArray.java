package org.memorydb.json;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.handler.CorruptionException;
import org.memorydb.structure.RecordData;
import org.memorydb.structure.RecordInterface;
import org.memorydb.structure.Store;

/**
 * Automatically generated record class for array
 */

@RecordData(name = "Value")
public class ArrayArray implements ChangePart, Iterable<ArrayArray> {
	private final Store store;
	private final Part parent;
	private final int idx;
	private int alloc;
	private int size;

	/* package private */ ArrayArray(Part parent, int idx) {
		this.store = parent.store();
		this.parent = parent;
		this.idx = idx;
		if (parent.rec() != 0) {
			this.alloc = store.getInt(parent.rec(), parent.partPosition() + 1);
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

	/* package private */ ArrayArray(ArrayArray other, int idx) {
		this.store = other.store;
		this.parent = other.parent;
		this.idx = idx;
		this.alloc = other.alloc;
		this.size = other.size;
	}

	/* package private */ ArrayArray(Store store, int rec, int idx) {
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
	public ArrayArray copy(int newRec) {
		assert store.validate(newRec);
		return new ArrayArray(store, newRec, -1);
	}

	private void up(Part record) {
		store.setInt(alloc, 8, record.rec());
		if (record instanceof Field)
			store.setByte(alloc, 12, 1);
		if (record instanceof ArrayArray) {
			store.setByte(alloc, 12, 2);
			store.setInt(alloc, 13, record.index());
		}
		if (record instanceof Json)
			store.setByte(alloc, 12, 3);
	}

	@Override
	public Part up() {
		if (alloc == 0)
			return null;
		switch (store.getByte(alloc, 12)) {
		case 1:
			return new Field(store, store.getInt(alloc, 8));
		case 2:
			return new ArrayArray(store, store.getInt(alloc, 8), store.getInt(alloc, 13));
		case 3:
			return new Json(store, store.getInt(alloc, 8));
		default:
			throw new CorruptionException("Unknown upRecord type");
		}
	}

	@Override
	public Store store() {
		return store;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public ArrayArray add() {
		if (parent.rec() == 0)
			return this;
		alloc = alloc == 0 ? store.allocate(9 + 17) : store.resize(alloc, (17 + (idx + 1) * 9) / 8);
		store.setInt(parent.rec(), parent.partPosition() + 1, alloc);
		store.setInt(alloc, 4, size + 1);
		ArrayArray res = new ArrayArray(parent, size);
		size++;
		return res;
	}

	@Override
	public Iterator<ArrayArray> iterator() {
		return new Iterator<>() {
			int element = -1;

			@Override
			public boolean hasNext() {
				return alloc != 0 && element + 1 < size;
			}

			@Override
			public ArrayArray next() {
				if (alloc == 0 || element > size)
					throw new NoSuchElementException();
				element++;
				return new ArrayArray(ArrayArray.this, element);
			}
		};
	}

	@Override
	public void output(Write write, int iterate) {
		if (alloc == 0 || iterate <= 0)
			return;
		outputPart(write, iterate);
		write.endRecord();
	}

	@Override
	public String toString() {
		Write write = new Write(new StringBuilder());
		if (idx == -1)
			for (ArrayArray a : this) {
				a.output(write, 4);
			}
		else
			output(write, 4);
		return write.toString();
	}

	public void parse(Parser parser) {
		parsePart(parser);
	}

	@Override
	public int partPosition() {
		return idx * 9 + 17;
	}

	@Override
	public Type getType() {
		if (idx == -1)
			return parent.getType();
		int data = rec() == 0 ? 0 : store().getByte(rec(), partPosition() + 0) & 31;
		if (data <= 0 || data > Type.values().length)
			return null;
		return Type.values()[data - 1];
	}

	@Override
	public FieldType type() {
		return getFieldType();
	}

	@Override
	public String name() {
		return null;
	}

	@Override
	public boolean java(Object val) {
		return setPart(0, val);
	}

	@Override
	public RecordInterface copy() {
		return new ArrayArray(parent, idx);
	}
}