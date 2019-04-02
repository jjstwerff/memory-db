package org.memorydb.jslt;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.handler.CorruptionException;
import org.memorydb.structure.ChangeInterface;
import org.memorydb.structure.MemoryRecord;
import org.memorydb.structure.RecordData;
import org.memorydb.structure.RecordInterface;
import org.memorydb.structure.Store;

/**
 * Automatically generated record class for sortParms
 */

@RecordData(name = "Step")
public class SortParmsArray implements MemoryRecord, ChangeOperator, Iterable<SortParmsArray> {
	private final Store store;
	private final Operator parent;
	private final int idx;
	private int alloc;
	private int size;

	/* package private */ SortParmsArray(Operator parent, int idx) {
		this.store = parent.store();
		this.parent = parent;
		this.idx = idx;
		if (parent.rec() != 0) {
			this.alloc = store.getInt(parent.rec(), parent.operatorPosition() + 5);
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

	/* package private */ SortParmsArray(SortParmsArray other, int idx) {
		this.store = other.store;
		this.parent = other.parent;
		this.idx = idx;
		this.alloc = other.alloc;
		this.size = other.size;
	}

	/* package private */ SortParmsArray(Store store, int rec, int idx) {
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

	private void up(Operator record) {
		store.setInt(alloc, 8, record.rec());
		if (record instanceof ObjectArray) {
			store.setByte(alloc, 12, 1);
			store.setInt(alloc, 13, record.getArrayIndex());
		}
		if (record instanceof ArrayArray) {
			store.setByte(alloc, 12, 2);
			store.setInt(alloc, 13, record.getArrayIndex());
		}
		if (record instanceof AppendArray) {
			store.setByte(alloc, 12, 3);
			store.setInt(alloc, 13, record.getArrayIndex());
		}
		if (record instanceof CallParmsArray) {
			store.setByte(alloc, 12, 4);
			store.setInt(alloc, 13, record.getArrayIndex());
		}
		if (record instanceof SortParmsArray) {
			store.setByte(alloc, 12, 5);
			store.setInt(alloc, 13, record.getArrayIndex());
		}
		if (record instanceof IfTrueArray) {
			store.setByte(alloc, 12, 6);
			store.setInt(alloc, 13, record.getArrayIndex());
		}
		if (record instanceof IfFalseArray) {
			store.setByte(alloc, 12, 7);
			store.setInt(alloc, 13, record.getArrayIndex());
		}
		if (record instanceof MparmsArray) {
			store.setByte(alloc, 12, 8);
			store.setInt(alloc, 13, record.getArrayIndex());
		}
		if (record instanceof CodeArray) {
			store.setByte(alloc, 12, 9);
			store.setInt(alloc, 13, record.getArrayIndex());
		}
		if (record instanceof Expr)
			store.setByte(alloc, 12, 10);
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
	public SortParmsArray add() {
		if (parent.rec() == 0)
			return this;
		idx = size;
		if (alloc == 0) {
			alloc = store.allocate(18 + 17);
			up(parent);
		} else
			alloc = store.resize(alloc, (17 + (idx + 1) * 18) / 8);
		store.setInt(parent.rec(), parent.operatorPosition() + 5, alloc);
		size = idx + 1;
		store.setInt(alloc, 4, size);
		return this;
	}

	@Override
	public Iterator<SortParmsArray> iterator() {
		return new Iterator<>() {
			int element = -1;

			@Override
			public boolean hasNext() {
				return alloc != 0 && element + 1 < size;
			}

			@Override
			public SortParmsArray next() {
				if (alloc == 0 || element > size)
					throw new NoSuchElementException();
				element++;
				return new SortParmsArray(SortParmsArray.this, element);
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
			for (SortParmsArray a : this) {
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
		if (field >= 0 && field <= 28)
			return nameOperator(field - 0);
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
		if (field >= 0 && field <= 28)
			return typeOperator(field - 0);
		switch (field) {
		default:
			return null;
		}
	}

	@Override
	public Object java() {
		int field = 0;
		if (idx == -1)
			return field < 1 || field > size ? null : new SortParmsArray(parent, field - 1);
		if (field >= 0 && field <= 28)
			return getOperator(field - 0);
		switch (field) {
		default:
			return null;
		}
	}

	@Override
	public boolean java(Object value) {
		int field = 0;
		if (field >= 0 && field <= 28)
			return setOperator(field - 0, value);
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
	public SortParmsArray copy() {
		return new SortParmsArray(parent, idx);
	}
}