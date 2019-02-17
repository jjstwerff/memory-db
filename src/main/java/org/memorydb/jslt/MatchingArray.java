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
 * Automatically generated record class for matching
 */

@RecordData(name = "MStep")
public class MatchingArray implements ChangeMatchStep, Iterable<MatchingArray> {
	private final Store store;
	private final Macro parent;
	private int idx;
	private int alloc;
	private int size;

	/* package private */ MatchingArray(Macro parent, int idx) {
		this.store = parent.getStore();
		this.parent = parent;
		this.idx = idx;
		if (parent.getRec() != 0) {
			this.alloc = store.getInt(parent.getRec(), 12);
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

	/* package private */ MatchingArray(MatchingArray other, int idx) {
		this.store = other.store;
		this.parent = other.parent;
		this.idx = idx;
		this.alloc = other.alloc;
		this.size = other.size;
	}

	/* package private */ MatchingArray(Store store, int rec, int idx) {
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

	/* package private */ void setUpRecord(Macro record) {
		store.setInt(alloc, 8, record.getRec());
	}

	@Override
	public Macro getUpRecord() {
		return new Macro(store, store.getInt(alloc, 8));
	}

	@Override
	public Store getStore() {
		return store;
	}

	@Override
	public int getSize() {
		return size;
	}

	public void clear() {
		size = 0;
		store.setInt(alloc, 4, size);
	}

	/* package private */ MatchingArray add() {
		if (parent.getRec() == 0)
			return this;
		idx = size;
		if (alloc == 0) {
			alloc = store.allocate(13 + 12);
			setUpRecord(parent);
		} else
			alloc = store.resize(alloc, (12 + (idx + 1) * 13) / 8);
		store.setInt(parent.getRec(), 12, alloc);
		size = idx + 1;
		store.setInt(alloc, 4, size);
		return this;
	}

	@Override
	public Iterator<MatchingArray> iterator() {
		return new Iterator<>() {
			int element = -1;

			@Override
			public boolean hasNext() {
				return alloc != 0 && element + 1 < size;
			}

			@Override
			public MatchingArray next() {
				if (alloc == 0 || element > size)
					throw new NoSuchElementException();
				element++;
				return new MatchingArray(MatchingArray.this, element);
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
	public void output(Write write, int iterate) throws IOException {
		if (alloc == 0 || iterate <= 0)
			return;
		outputMatchStep(write, iterate);
		write.endRecord();
	}

	@Override
	public String toString() {
		Write write = new Write(new StringBuilder());
		try {
			if (idx == -1)
				for (MatchingArray a : this) {
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
		parseMatchStep(parser);
	}

	@Override
	public int matchstepPosition() {
		return idx * 13 + 12;
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
		if (field >= 0 && field <= 33)
			return nameMatchStep(field - 0);
		switch (field) {
		default:
			return null;
		}
	}

	@Override
	public FieldType type(int field) {
		if (field >= 0 && field <= 33)
			return typeMatchStep(field - 0);
		switch (field) {
		default:
			return null;
		}
	}

	@Override
	public Object get(int field) {
		if (field >= 0 && field <= 33)
			return getMatchStep(field - 0);
		switch (field) {
		default:
			return null;
		}
	}

	@Override
	public Iterable<? extends RecordInterface> iterate(int field, Object... key) {
		if (field >= 0 && field <= 33)
			return iterateMatchStep(field - 0);
		switch (field) {
		default:
			return null;
		}
	}

	@Override
	public boolean set(int field, Object value) {
		if (field >= 0 && field <= 33)
			return setMatchStep(field - 0, value);
		switch (field) {
		default:
			return false;
		}
	}

	@Override
	public ChangeInterface add(int field) {
		if (field >= 0 && field <= 33)
			return addMatchStep(field - 0);
		switch (field) {
		default:
			return null;
		}
	}
}