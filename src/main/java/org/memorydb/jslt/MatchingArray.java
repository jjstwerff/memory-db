package org.memorydb.jslt;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.structure.ChangeInterface;
import org.memorydb.structure.MemoryRecord;
import org.memorydb.structure.RecordData;
import org.memorydb.structure.RecordInterface;
import org.memorydb.structure.Store;

/**
 * Automatically generated record class for matching
 */

@RecordData(name = "MStep")
public class MatchingArray implements MemoryRecord, ChangeMatchStep, Iterable<MatchingArray> {
	private final Store store;
	private final Macro parent;
	private final int idx;
	private int alloc;
	private int size;

	/* package private */ MatchingArray(Macro parent, int idx) {
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

	private void up(Macro record) {
		store.setInt(alloc, 8, record.rec());
	}

	@Override
	public Macro up() {
		return new Macro(store, store.getInt(alloc, 8));
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
	public MatchingArray add() {
		if (parent.rec() == 0)
			return this;
		idx = size;
		if (alloc == 0) {
			alloc = store.allocate(13 + 12);
			up(parent);
		} else
			alloc = store.resize(alloc, (12 + (idx + 1) * 13) / 8);
		store.setInt(parent.rec(), 12, alloc);
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
	public void output(Write write, int iterate) {
		if (alloc == 0 || iterate <= 0)
			return;
		outputMatchStep(write, iterate);
		write.endRecord();
	}

	@Override
	public String toString() {
		Write write = new Write(new StringBuilder());
		if (idx == -1)
			for (MatchingArray a : this) {
				a.output(write, 4);
			}
		else
			output(write, 4);
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
	public String name() {
		int field = 0;
		if (idx == -1)
			return null;
		if (field >= 0 && field <= 33)
			return nameMatchStep(field - 0);
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
		if (field >= 0 && field <= 33)
			return typeMatchStep(field - 0);
		switch (field) {
		default:
			return null;
		}
	}

	@Override
	public Object java() {
		int field = 0;
		if (idx == -1)
			return field < 1 || field > size ? null : new MatchingArray(parent, field - 1);
		if (field >= 0 && field <= 33)
			return getMatchStep(field - 0);
		switch (field) {
		default:
			return null;
		}
	}

	@Override
	public boolean java(Object value) {
		int field = 0;
		if (field >= 0 && field <= 33)
			return setMatchStep(field - 0, value);
		switch (field) {
		default:
			return false;
		}
	}

	@Override
	public RecordInterface next() {
		return null;
	}

	@Override
	public MatchingArray copy() {
		return new MatchingArray(parent, idx);
	}
}