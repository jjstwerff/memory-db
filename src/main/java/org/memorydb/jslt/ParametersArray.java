package org.memorydb.jslt;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.structure.ChangeInterface;
import org.memorydb.structure.InputOutputException;
import org.memorydb.structure.RecordData;
import org.memorydb.structure.RecordInterface;
import org.memorydb.structure.Store;

/**
 * Automatically generated record class for parameters
 */

@RecordData(name = "Parameter")
public class ParametersArray implements ChangeMatch, Iterable<ParametersArray> {
	private final Store store;
	private final Alternative parent;
	private int idx;
	private int alloc;
	private int size;

	/* package private */ ParametersArray(Alternative parent, int idx) {
		this.store = parent.getStore();
		this.parent = parent;
		this.idx = idx;
		if (parent.getRec() != 0) {
			this.alloc = store.getInt(parent.getRec(), 8);
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
		this.parent = getUpRecord();
		this.size = alloc == 0 ? 0 : store.getInt(alloc, 4);
	}

	@Override
	public int getRec() {
		return alloc;
	}

	@Override
	public int getArrayIndex() {
		return idx;
	}

	@Override
	public void setRec(int rec) {
		this.alloc = rec;
	}

	/* package private */ void setUpRecord(Alternative record) {
		store.setInt(alloc, 8, record.getRec());
	}

	@Override
	public Alternative getUpRecord() {
		return new Alternative(store, store.getInt(alloc, 8));
	}

	@Override
	public Store getStore() {
		return store;
	}

	@Override
	public int getSize() {
		return size;
	}

	/* package private */ ParametersArray add() {
		if (parent.getRec() == 0)
			return this;
		idx = size;
		if (alloc == 0) {
			alloc = store.allocate(9 + 12);
			setUpRecord(parent);
		} else
			alloc = store.resize(alloc, (12 + (idx + 1) * 9) / 8);
		store.setInt(parent.getRec(), 8, alloc);
		size = idx + 1;
		store.setInt(alloc, 4, size);
		return this;
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
		};
	}

	@Override
	public void output(Write write, int iterate) throws IOException {
		if (alloc == 0 || iterate <= 0)
			return;
		outputMatch(write, iterate);
		write.endRecord();
	}

	@Override
	public String toString() {
		Write write = new Write(new StringBuilder());
		try {
			if (idx == -1)
				for (ParametersArray a : this) {
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
		parseMatch(parser);
	}

	@Override
	public int matchPosition() {
		return idx * 9 + 12;
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
	public boolean exists() {
		return getRec() != 0;
	}

	@Override
	public String name(int field) {
		if (field >= 0 && field <= 14)
			return nameMatch(field - 0);
		switch (field) {
		default:
			return null;
		}
	}

	@Override
	public FieldType type(int field) {
		if (field >= 0 && field <= 14)
			return typeMatch(field - 0);
		switch (field) {
		default:
			return null;
		}
	}

	@Override
	public Object get(int field) {
		if (field >= 0 && field <= 14)
			return getMatch(field - 0);
		switch (field) {
		default:
			return null;
		}
	}

	@Override
	public Iterable<? extends RecordInterface> iterate(int field, Object... key) {
		if (field >= 0 && field <= 14)
			return iterateMatch(field - 0);
		switch (field) {
		default:
			return null;
		}
	}

	@Override
	public boolean set(int field, Object value) {
		if (field >= 0 && field <= 14)
			return setMatch(field - 0, value);
		switch (field) {
		default:
			return false;
		}
	}

	@Override
	public ChangeInterface add(int field) {
		if (field >= 0 && field <= 14)
			return addMatch(field - 0);
		switch (field) {
		default:
			return null;
		}
	}
}