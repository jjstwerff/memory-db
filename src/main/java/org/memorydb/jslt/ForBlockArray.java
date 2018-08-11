package org.memorydb.jslt;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.structure.ChangeInterface;
import org.memorydb.structure.InputOutputException;
import org.memorydb.structure.RecordInterface;
import org.memorydb.structure.Store;

/**
 * Automatically generated record class for forBlock
 */

public class ForBlockArray implements ChangeOperator, Iterable<ForBlockArray> {
	private final Store store;
	private final Operator parent;
	private int idx;
	private int alloc;
	private int size;

	public ForBlockArray(Operator parent, int idx) {
		this.store = parent.getStore();
		this.parent = parent;
		this.idx = idx;
		if (parent.getRec() != 0) {
			this.alloc = store.getInt(parent.getRec(), parent.operatorPosition() + 5);
			if (alloc != 0) {
				setUpRecord(parent);
				this.size = store.getInt(alloc, 4);
			} else
				this.size = 0;
		} else {
			this.alloc = 0;
			this.size = 0;
		}
	}

	public ForBlockArray(ForBlockArray other, int idx) {
		this.store = other.store;
		this.parent = other.parent;
		this.idx = idx;
		this.alloc = other.alloc;
		this.size = other.size;
	}

	public ForBlockArray(Store store, int rec, int idx) {
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

	public void setUpRecord(Operator record) {
		store.setInt(alloc, 8, record.getRec());
		if (record instanceof ObjectArray) {
			store.setInt(alloc, 12, 1);
			store.setInt(alloc, 13, record.getArrayIndex());
		}
		if (record instanceof ArrayArray) {
			store.setInt(alloc, 12, 2);
			store.setInt(alloc, 13, record.getArrayIndex());
		}
		if (record instanceof CallParmsArray) {
			store.setInt(alloc, 12, 3);
			store.setInt(alloc, 13, record.getArrayIndex());
		}
		if (record instanceof ForBlockArray) {
			store.setInt(alloc, 12, 4);
			store.setInt(alloc, 13, record.getArrayIndex());
		}
		if (record instanceof SortParmsArray) {
			store.setInt(alloc, 12, 5);
			store.setInt(alloc, 13, record.getArrayIndex());
		}
		if (record instanceof SortBlockArray) {
			store.setInt(alloc, 12, 6);
			store.setInt(alloc, 13, record.getArrayIndex());
		}
		if (record instanceof IfTrueArray) {
			store.setInt(alloc, 12, 7);
			store.setInt(alloc, 13, record.getArrayIndex());
		}
		if (record instanceof IfFalseArray) {
			store.setInt(alloc, 12, 8);
			store.setInt(alloc, 13, record.getArrayIndex());
		}
		if (record instanceof CodeArray) {
			store.setInt(alloc, 12, 9);
			store.setInt(alloc, 13, record.getArrayIndex());
		}
		if (record instanceof Expr)
			store.setInt(alloc, 12, 10);
	}

	@Override
	public Operator getUpRecord() {
		if (alloc == 0)
			return null;
		switch (store.getInt(alloc, 12)) {
		case 1:
			return new ObjectArray(store, store.getInt(alloc, 8), store.getInt(alloc, 13));
		case 2:
			return new ArrayArray(store, store.getInt(alloc, 8), store.getInt(alloc, 13));
		case 3:
			return new CallParmsArray(store, store.getInt(alloc, 8), store.getInt(alloc, 13));
		case 4:
			return new ForBlockArray(store, store.getInt(alloc, 8), store.getInt(alloc, 13));
		case 5:
			return new SortParmsArray(store, store.getInt(alloc, 8), store.getInt(alloc, 13));
		case 6:
			return new SortBlockArray(store, store.getInt(alloc, 8), store.getInt(alloc, 13));
		case 7:
			return new IfTrueArray(store, store.getInt(alloc, 8), store.getInt(alloc, 13));
		case 8:
			return new IfFalseArray(store, store.getInt(alloc, 8), store.getInt(alloc, 13));
		case 9:
			return new CodeArray(store, store.getInt(alloc, 8), store.getInt(alloc, 13));
		case 10:
			return new Expr(store, store.getInt(alloc, 8));
		default:
			return null;
		}
	}

	@Override
	public Store getStore() {
		return store;
	}

	public int getSize() {
		return size;
	}

	public ForBlockArray add() {
		if (parent.getRec() == 0)
			return this;
		idx = size;
		if (alloc == 0) {
			alloc = store.allocate(14 + 17);
			setUpRecord(parent);
		} else
			alloc = store.resize(alloc, (17 + (idx + 1) * 14) / 8);
		store.setInt(parent.getRec(), parent.operatorPosition() + 5, alloc);
		size = idx + 1;
		store.setInt(alloc, 4, size);
		return this;
	}

	@Override
	public Iterator<ForBlockArray> iterator() {
		return new Iterator<>() {
			int element = -1;

			@Override
			public boolean hasNext() {
				return element + 1 < size;
			}

			@Override
			public ForBlockArray next() {
				if (element > size)
					throw new NoSuchElementException();
				element++;
				return new ForBlockArray(ForBlockArray.this, element);
			}
		};
	}

	@Override
	public void output(Write write, int iterate) throws IOException {
		if (alloc == 0 || iterate <= 0)
			return;
		outputOperator(write, iterate, true);
		write.endRecord();
	}

	@Override
	public String toString() {
		Write write = new Write(new StringBuilder());
		try {
			if (idx == -1)
				for (ForBlockArray a : this) {
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
		parseOperator(parser);
	}

	@Override
	public int operatorPosition() {
		return idx * 14 + 17;
	}

	@Override
	public boolean parseKey(Parser parser) {
		return false;
	}

	@Override
	public boolean exists() {
		return getRec() != 0;
	}

	@Override
	public String name(int field) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FieldType type(int field) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object get(int field) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<? extends RecordInterface> iterate(int field, Object... key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean set(int field, Object val) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ChangeInterface add(int field) {
		// TODO Auto-generated method stub
		return null;
	}
}