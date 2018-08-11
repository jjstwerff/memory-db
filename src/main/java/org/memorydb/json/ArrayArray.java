package org.memorydb.json;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.handler.CorruptionException;
import org.memorydb.structure.ChangeInterface;
import org.memorydb.structure.InputOutputException;
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
	private int idx;
	private int alloc;
	private int size;

	/* package private */ ArrayArray(Part parent, int idx) {
		this.store = parent.getStore();
		this.parent = parent;
		this.idx = idx;
		if (parent.getRec() != 0) {
			this.alloc = store.getInt(parent.getRec(), parent.partPosition() + 1);
			if (alloc != 0) {
				setUpRecord(parent);
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
		this.parent = getUpRecord();
		this.size = alloc == 0 ? 0 : store.getInt(alloc, 4);
	}

	@Override
	public int getRec() {
		return idx < 0 || idx >= size ? 0 : alloc;
	}

	@Override
	public int getArrayIndex() {
		return idx;
	}

	@Override
	public void setRec(int rec) {
		this.alloc = rec;
	}

	/* package private */ void setUpRecord(Part record) {
		store.setInt(alloc, 8, record.getRec());
		if (record instanceof Field)
			store.setByte(alloc, 12, 1);
		if (record instanceof ArrayArray) {
			store.setByte(alloc, 12, 2);
			store.setInt(alloc, 13, record.getArrayIndex());
		}
		if (record instanceof Json)
			store.setByte(alloc, 12, 3);
	}

	@Override
	public Part getUpRecord() {
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
	public Store getStore() {
		return store;
	}

	public int getSize() {
		return size;
	}

	/* package private */ ArrayArray add() {
		if (parent.getRec() == 0)
			return this;
		idx = size;
		if (alloc == 0) {
			alloc = store.allocate(9 + 17);
			setUpRecord(parent);
		} else
			alloc = store.resize(alloc, (17 + (idx + 1) * 9) / 8);
		store.setInt(parent.getRec(), parent.partPosition() + 1, alloc);
		size = idx + 1;
		store.setInt(alloc, 4, size);
		return this;
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
	public void output(Write write, int iterate) throws IOException {
		if (alloc == 0 || iterate <= 0)
			return;
		outputPart(write, iterate, true);
		write.endRecord();
	}

	@Override
	public String toString() {
		Write write = new Write(new StringBuilder());
		try {
			if (idx == -1)
				for (ArrayArray a : this) {
					a.output(write, 4);
				}
			else
				output(write, 4);
		} catch (IOException e) {
			throw new InputOutputException(e);
		}
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
	public boolean parseKey(Parser parser) {
		return false;
	}

	public void setIdx(int idx) {
		if (idx >= 0 && idx < size)
			this.idx = idx;
	}

	@Override
	public void close() {
		// nothing
	}

	@Override
	public Object get(int field) {
		return getPart(field);
	}

	@Override
	public Iterable<? extends RecordInterface> iterate(int field, Object... key) {
		return iteratePart(field, key);
	}

	@Override
	public FieldType type() {
		return typePart();
	}

	@Override
	public FieldType type(int field) {
		return typePart(field);
	}

	@Override
	public String name(int field) {
		return namePart(field);
	}

	@Override
	public boolean exists() {
		return getRec() != 0;
	}

	@Override
	public boolean set(int field, Object val) {
		return setPart(field, val);
	}

	@Override
	public ChangeInterface add(int field) {
		return addPart(field);
	}
}