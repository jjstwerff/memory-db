package org.memorydb.jslt;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.structure.MemoryRecord;
import org.memorydb.structure.RecordData;
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
	public MatchingArray copy(int newRec) {
		assert store.validate(newRec);
		return new MatchingArray(store, newRec, -1);
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
		if (alloc == 0) {
			alloc = store.allocate(13 + 12);
			up(parent);
		} else
			alloc = store.resize(alloc, (12 + (size + 1) * 13) / 8);
		store.setInt(parent.rec(), 12, alloc);
		size++;
		store.setInt(alloc, 4, size);
		MatchingArray res = new MatchingArray(parent, size - 1);
		return res;
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
	public String name() {
		if (idx == -1)
			return null;
		if (idx >= 0 && idx <= 42)
			return nameMatchStep(idx - 0);
		return null;
	}

	@Override
	public FieldType type() {
		if (idx == -1)
			return FieldType.OBJECT;
		if (idx >= 0 && idx <= 42)
			return typeMatchStep(idx - 0);
		return null;
	}

	@Override
	public Object java() {
		if (idx == -1)
			return this;
		if (idx >= 0 && idx <= 42)
			return getMatchStep(idx - 0);
		return null;
	}

	@Override
	public boolean java(Object value) {
		if (idx >= 0 && idx <= 42)
			return setMatchStep(idx - 0, value);
		return false;
	}

	@Override
	public MatchingArray index(int idx) {
		return idx < 0 || idx >= size ? null : new MatchingArray(parent, idx);
	}

	@Override
	public MatchingArray start() {
		return new MatchingArray(parent, 0);
	}

	@Override
	public MatchingArray next() {
		return idx + 1 >= size ? null : new MatchingArray(parent, idx + 1);
	}

	@Override
	public boolean testLast() {
		return idx == size - 1;
	}

	@Override
	public MatchingArray copy() {
		return new MatchingArray(parent, idx);
	}
}
