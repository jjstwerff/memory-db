package org.memorydb.jslt;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.structure.MemoryRecord;
import org.memorydb.structure.RecordData;
import org.memorydb.structure.Store;

/**
 * Automatically generated record class for code
 */

@RecordData(name = "Step")
public class CodeArray implements MemoryRecord, ChangeOperator, Iterable<CodeArray> {
	private final Store store;
	private final Alternative parent;
	private final int idx;
	private int alloc;
	private int size;

	/* package private */ CodeArray(Alternative parent, int idx) {
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

	/* package private */ CodeArray(CodeArray other, int idx) {
		this.store = other.store;
		this.parent = other.parent;
		this.idx = idx;
		this.alloc = other.alloc;
		this.size = other.size;
	}

	/* package private */ CodeArray(Store store, int rec, int idx) {
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
	public CodeArray copy(int newRec) {
		assert store.validate(newRec);
		return new CodeArray(store, newRec, -1);
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
	public CodeArray add() {
		if (parent.rec() == 0)
			return this;
		if (alloc == 0) {
			alloc = store.allocate(18 + 12);
			up(parent);
		} else
			alloc = store.resize(alloc, (12 + (size + 1) * 18) / 8);
		store.setInt(parent.rec(), 17, alloc);
		size++;
		store.setInt(alloc, 4, size);
		CodeArray res = new CodeArray(parent, size - 1);
		return res;
	}

	@Override
	public Iterator<CodeArray> iterator() {
		return new Iterator<>() {
			int element = -1;

			@Override
			public boolean hasNext() {
				return alloc != 0 && element + 1 < size;
			}

			@Override
			public CodeArray next() {
				if (alloc == 0 || element > size)
					throw new NoSuchElementException();
				element++;
				return new CodeArray(CodeArray.this, element);
			}

			@Override
			public void remove() {
				if (alloc == 0 || element > size || element < 0)
					throw new NoSuchElementException();
				store.copy(alloc, (element + 1) * 18 + 12, element * 18 + 12, size * 17);
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
		outputOperator(write, iterate);
		write.endRecord();
	}

	@Override
	public String toString() {
		Write write = new Write(new StringBuilder());
		if (idx == -1)
			for (CodeArray a : this) {
				a.output(write, 4);
			}
		else
			output(write, 4);
		return write.toString();
	}

	public void parse(Parser parser) {
		parseOperator(parser);
	}

	@Override
	public int operatorPosition() {
		return idx * 18 + 12;
	}

	@Override
	public String name() {
		if (idx == -1)
			return null;
		if (idx >= 0 && idx <= 28)
			return nameOperator(idx - 0);
		return null;
	}

	@Override
	public FieldType type() {
		if (idx == -1)
			return FieldType.OBJECT;
		if (idx >= 0 && idx <= 28)
			return typeOperator(idx - 0);
		return null;
	}

	@Override
	public Object java() {
		if (idx == -1)
			return this;
		if (idx >= 0 && idx <= 28)
			return getOperator(idx - 0);
		return null;
	}

	@Override
	public boolean java(Object value) {
		if (idx >= 0 && idx <= 28)
			return setOperator(idx - 0, value);
		return false;
	}

	@Override
	public CodeArray index(int idx) {
		return idx < 0 || idx >= size ? null : new CodeArray(parent, idx);
	}

	@Override
	public CodeArray start() {
		return new CodeArray(parent, 0);
	}

	@Override
	public CodeArray next() {
		return idx + 1 >= size ? null : new CodeArray(parent, idx + 1);
	}

	@Override
	public boolean testLast() {
		return idx == size - 1;
	}

	@Override
	public CodeArray copy() {
		return new CodeArray(parent, idx);
	}
}
