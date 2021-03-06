package org.memorydb.jslt;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.handler.CorruptionException;
import org.memorydb.structure.MemoryRecord;
import org.memorydb.structure.RecordData;
import org.memorydb.structure.Store;

/**
 * Automatically generated record class for array
 */

@RecordData(name = "Step")
public class ArrayArray implements MemoryRecord, ChangeOperator, Iterable<ArrayArray> {
	private final Store store;
	private final Operator parent;
	private final int idx;
	private int alloc;
	private int size;

	/* package private */ ArrayArray(Operator parent, int idx) {
		this.store = parent.store();
		this.parent = parent;
		this.idx = idx;
		if (parent.rec() != 0) {
			this.alloc = store.getInt(parent.rec(), parent.operatorPosition() + 1);
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

	private void up(Operator record) {
		store.setInt(alloc, 8, record.rec());
		if (record instanceof ObjectArray) {
			store.setByte(alloc, 12, 1);
			store.setInt(alloc, 13, record.index());
		}
		if (record instanceof ArrayArray) {
			store.setByte(alloc, 12, 2);
			store.setInt(alloc, 13, record.index());
		}
		if (record instanceof AppendArray) {
			store.setByte(alloc, 12, 3);
			store.setInt(alloc, 13, record.index());
		}
		if (record instanceof CallParmsArray) {
			store.setByte(alloc, 12, 4);
			store.setInt(alloc, 13, record.index());
		}
		if (record instanceof SortParmsArray) {
			store.setByte(alloc, 12, 5);
			store.setInt(alloc, 13, record.index());
		}
		if (record instanceof IfTrueArray) {
			store.setByte(alloc, 12, 6);
			store.setInt(alloc, 13, record.index());
		}
		if (record instanceof IfFalseArray) {
			store.setByte(alloc, 12, 7);
			store.setInt(alloc, 13, record.index());
		}
		if (record instanceof MparmsArray) {
			store.setByte(alloc, 12, 8);
			store.setInt(alloc, 13, record.index());
		}
		if (record instanceof CodeArray) {
			store.setByte(alloc, 12, 9);
			store.setInt(alloc, 13, record.index());
		}
		if (record instanceof ParmsArray) {
			store.setByte(alloc, 12, 10);
			store.setInt(alloc, 13, record.index());
		}
		if (record instanceof Expr)
			store.setByte(alloc, 12, 11);
	}

	@Override
	public Operator up() {
		if (alloc == 0)
			return null;
		switch (store.getByte(alloc, 12)) {
		case 1:
			return new ObjectArray(store, store.getInt(alloc, 8), store.getInt(alloc, 13));
		case 2:
			return new ArrayArray(store, store.getInt(alloc, 8), store.getInt(alloc, 13));
		case 3:
			return new AppendArray(store, store.getInt(alloc, 8), store.getInt(alloc, 13));
		case 4:
			return new CallParmsArray(store, store.getInt(alloc, 8), store.getInt(alloc, 13));
		case 5:
			return new SortParmsArray(store, store.getInt(alloc, 8), store.getInt(alloc, 13));
		case 6:
			return new IfTrueArray(store, store.getInt(alloc, 8), store.getInt(alloc, 13));
		case 7:
			return new IfFalseArray(store, store.getInt(alloc, 8), store.getInt(alloc, 13));
		case 8:
			return new MparmsArray(store, store.getInt(alloc, 8), store.getInt(alloc, 13));
		case 9:
			return new CodeArray(store, store.getInt(alloc, 8), store.getInt(alloc, 13));
		case 10:
			return new ParmsArray(store, store.getInt(alloc, 8), store.getInt(alloc, 13));
		case 11:
			return new Expr(store, store.getInt(alloc, 8));
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

	public void clear() {
		size = 0;
		store.setInt(alloc, 4, size);
	}

	@Override
	public ArrayArray add() {
		if (parent.rec() == 0)
			return this;
		if (alloc == 0) {
			alloc = store.allocate(18 + 17);
			up(parent);
		} else
			alloc = store.resize(alloc, (17 + (size + 1) * 18) / 8);
		store.setInt(parent.rec(), parent.operatorPosition() + 1, alloc);
		size++;
		store.setInt(alloc, 4, size);
		ArrayArray res = new ArrayArray(parent, size - 1);
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

			@Override
			public void remove() {
				if (alloc == 0 || element > size || element < 0)
					throw new NoSuchElementException();
				store.copy(alloc, (element + 1) * 18 + 17, element * 18 + 17, size * 17);
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
			for (ArrayArray a : this) {
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
		return idx * 18 + 17;
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
	public ArrayArray index(int idx) {
		return idx < 0 || idx >= size ? null : new ArrayArray(parent, idx);
	}

	@Override
	public ArrayArray start() {
		return new ArrayArray(parent, 0);
	}

	@Override
	public ArrayArray next() {
		return idx + 1 >= size ? null : new ArrayArray(parent, idx + 1);
	}

	@Override
	public boolean testLast() {
		return idx == size - 1;
	}

	@Override
	public ArrayArray copy() {
		return new ArrayArray(parent, idx);
	}
}
