package org.memorydb.jslt;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.structure.MemoryRecord;
import org.memorydb.structure.RecordData;
import org.memorydb.structure.Store;

/**
 * Automatically generated record class for parameters
 */

@RecordData(name = "Parameter")
public class ParametersArray implements MemoryRecord, ChangeMatch, Iterable<ParametersArray> {
	private final Store store;
	private final Alternative parent;
	private final int idx;
	private int alloc;
	private int size;

	/* package private */ ParametersArray(Alternative parent, int idx) {
		this.store = parent.store();
		this.parent = parent;
		this.idx = idx;
		if (parent.rec() != 0) {
			this.alloc = store.getInt(parent.rec(), 8);
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

	/* package private */ ParametersArray(ParametersArray other, int idx) {
		this.store = other.store;
		this.parent = other.parent;
		this.idx = idx;
		this.alloc = other.alloc;
		this.size = other.size;
	}

	/* package private */ ParametersArray(Store store, int rec, int idx) {
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
	public ParametersArray copy(int rec) {
		assert store.validate(rec);
		return new ParametersArray(store, rec, -1);
	}

	private void up(Alternative record) {
		store.setInt(alloc, 8, record.rec());
	}

	@Override
	public Alternative up() {
		return new Alternative(store, store.getInt(alloc, 8));
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
	public ParametersArray add() {
		if (parent.rec() == 0)
			return this;
		if (alloc == 0) {
			alloc = store.allocate(13 + 12);
			up(parent);
		} else
			alloc = store.resize(alloc, (12 + (idx + 1) * 13) / 8);
		store.setInt(parent.rec(), 8, alloc);
		size++;
		store.setInt(alloc, 4, size);
		ParametersArray res = new ParametersArray(parent, size - 1);
		return res;
	}

	@Override
	public Iterator<ParametersArray> iterator() {
		return new Iterator<>() {
			int element = -1;

			@Override
			public boolean hasNext() {
				return alloc != 0 && element + 1 < size;
			}

			@Override
			public ParametersArray next() {
				if (alloc == 0 || element > size)
					throw new NoSuchElementException();
				element++;
				return new ParametersArray(ParametersArray.this, element);
			}

			@Override
			public void remove() {
				if (alloc == 0 || element > size || element < 0)
					throw new NoSuchElementException();
				store.copy(alloc, (element + 1) * 13 + 12, element * 13 + 12, size * 17);
				element--;
				size--;
				store.setInt(alloc, 4, size);
			}
		};
	}

	@Override
	public void output(Write write, int iterate) {
		if (alloc == 0 || iterate <= 0)
			return;
		outputMatch(write, iterate);
		write.endRecord();
	}

	@Override
	public String toString() {
		Write write = new Write(new StringBuilder());
		if (idx == -1)
			for (ParametersArray a : this) {
				a.output(write, 4);
			}
		else
			output(write, 4);
		return write.toString();
	}

	public void parse(Parser parser) {
		parseMatch(parser);
	}

	@Override
	public int matchPosition() {
		return idx * 13 + 12;
	}

	@Override
	public String name() {
		int field = 0;
		if (idx == -1)
			return null;
		if (field >= 0 && field <= 15)
			return nameMatch(field - 0);
		switch (field) {
		default:
			return null;
		}
	}

	@Override
	public FieldType type() {
		int field = 0;
		if (idx == -1)
			return field < 1 || field > size ? null : FieldType.OBJECT;
		if (field >= 0 && field <= 15)
			return typeMatch(field - 0);
		switch (field) {
		default:
			return null;
		}
	}

	@Override
	public Object java() {
		int field = 0;
		if (idx == -1)
			return field < 1 || field > size ? null : new ParametersArray(parent, field - 1);
		if (field >= 0 && field <= 15)
			return getMatch(field - 0);
		switch (field) {
		default:
			return null;
		}
	}

	@Override
	public boolean java(Object value) {
		int field = 0;
		if (field >= 0 && field <= 15)
			return setMatch(field - 0, value);
		switch (field) {
		default:
			return false;
		}
	}

	@Override
	public ParametersArray index(int idx) {
		return null;
	}

	@Override
	public ParametersArray start() {
		return null;
	}

	@Override
	public ParametersArray next() {
		return null;
	}

	@Override
	public ParametersArray copy() {
		return new ParametersArray(parent, idx);
	}
}
