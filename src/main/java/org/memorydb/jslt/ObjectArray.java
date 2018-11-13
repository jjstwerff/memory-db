package org.memorydb.jslt;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.handler.CorruptionException;
import org.memorydb.structure.ChangeInterface;
import org.memorydb.structure.FieldData;
import org.memorydb.structure.InputOutputException;
import org.memorydb.structure.RecordData;
import org.memorydb.structure.RecordInterface;
import org.memorydb.structure.Store;

/**
 * Automatically generated record class for object
 */

@RecordData(name = "Field")
public class ObjectArray implements ChangeOperator, Iterable<ObjectArray> {
	private final Store store;
	private final Operator parent;
	private int idx;
	private int alloc;
	private int size;

	/* package private */ ObjectArray(Operator parent, int idx) {
		this.store = parent.getStore();
		this.parent = parent;
		this.idx = idx;
		if (parent.getRec() != 0) {
			this.alloc = store.getInt(parent.getRec(), parent.operatorPosition() + 1);
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

	/* package private */ ObjectArray(ObjectArray other, int idx) {
		this.store = other.store;
		this.parent = other.parent;
		this.idx = idx;
		this.alloc = other.alloc;
		this.size = other.size;
	}

	/* package private */ ObjectArray(Store store, int rec, int idx) {
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

	/* package private */ void setUpRecord(Operator record) {
		store.setInt(alloc, 8, record.getRec());
		if (record instanceof ObjectArray) {
			store.setByte(alloc, 12, 1);
			store.setInt(alloc, 13, record.getArrayIndex());
		}
		if (record instanceof OrderArray) {
			store.setByte(alloc, 12, 2);
			store.setInt(alloc, 13, record.getArrayIndex());
		}
		if (record instanceof SliceArray) {
			store.setByte(alloc, 12, 3);
			store.setInt(alloc, 13, record.getArrayIndex());
		}
		if (record instanceof ArrayArray) {
			store.setByte(alloc, 12, 4);
			store.setInt(alloc, 13, record.getArrayIndex());
		}
		if (record instanceof AppendArray) {
			store.setByte(alloc, 12, 5);
			store.setInt(alloc, 13, record.getArrayIndex());
		}
		if (record instanceof CallParmsArray) {
			store.setByte(alloc, 12, 6);
			store.setInt(alloc, 13, record.getArrayIndex());
		}
		if (record instanceof SortParmsArray) {
			store.setByte(alloc, 12, 7);
			store.setInt(alloc, 13, record.getArrayIndex());
		}
		if (record instanceof IfTrueArray) {
			store.setByte(alloc, 12, 8);
			store.setInt(alloc, 13, record.getArrayIndex());
		}
		if (record instanceof IfFalseArray) {
			store.setByte(alloc, 12, 9);
			store.setInt(alloc, 13, record.getArrayIndex());
		}
		if (record instanceof CodeArray) {
			store.setByte(alloc, 12, 10);
			store.setInt(alloc, 13, record.getArrayIndex());
		}
		if (record instanceof Expr)
			store.setByte(alloc, 12, 11);
		if (record instanceof Listener)
			store.setByte(alloc, 12, 12);
		if (record instanceof Level)
			store.setByte(alloc, 12, 13);
	}

	@Override
	public Operator getUpRecord() {
		if (alloc == 0)
			return null;
		switch (store.getByte(alloc, 12)) {
		case 1:
			return new ObjectArray(store, store.getInt(alloc, 8), store.getInt(alloc, 13));
		case 2:
			return new OrderArray(store, store.getInt(alloc, 8), store.getInt(alloc, 13));
		case 3:
			return new SliceArray(store, store.getInt(alloc, 8), store.getInt(alloc, 13));
		case 4:
			return new ArrayArray(store, store.getInt(alloc, 8), store.getInt(alloc, 13));
		case 5:
			return new AppendArray(store, store.getInt(alloc, 8), store.getInt(alloc, 13));
		case 6:
			return new CallParmsArray(store, store.getInt(alloc, 8), store.getInt(alloc, 13));
		case 7:
			return new SortParmsArray(store, store.getInt(alloc, 8), store.getInt(alloc, 13));
		case 8:
			return new IfTrueArray(store, store.getInt(alloc, 8), store.getInt(alloc, 13));
		case 9:
			return new IfFalseArray(store, store.getInt(alloc, 8), store.getInt(alloc, 13));
		case 10:
			return new CodeArray(store, store.getInt(alloc, 8), store.getInt(alloc, 13));
		case 11:
			return new Expr(store, store.getInt(alloc, 8));
		case 12:
			return new Listener(store, store.getInt(alloc, 8));
		case 13:
			return new Level(store, store.getInt(alloc, 8));
		default:
			throw new CorruptionException("Unknown upRecord type");
		}
	}

	@Override
	public Store getStore() {
		return store;
	}

	@Override
	public int getSize() {
		return size;
	}

	/* package private */ ObjectArray add() {
		if (parent.getRec() == 0)
			return this;
		idx = size;
		if (alloc == 0) {
			alloc = store.allocate(22 + 17);
			setUpRecord(parent);
		} else
			alloc = store.resize(alloc, (17 + (idx + 1) * 22) / 8);
		store.setInt(parent.getRec(), parent.operatorPosition() + 1, alloc);
		size = idx + 1;
		store.setInt(alloc, 4, size);
		setName(null);
		return this;
	}

	@Override
	public Iterator<ObjectArray> iterator() {
		return new Iterator<>() {
			int element = -1;

			@Override
			public boolean hasNext() {
				return alloc != 0 && element + 1 < size;
			}

			@Override
			public ObjectArray next() {
				if (alloc == 0 || element > size)
					throw new NoSuchElementException();
				element++;
				return new ObjectArray(ObjectArray.this, element);
			}
		};
	}

	@FieldData(
		name = "object",
		type = "ARRAY",
		related = ObjectArray.class,
		when = "OBJECT",
		mandatory = false
	)

	public Expr getName() {
		return new Expr(store, alloc == 0 || idx < 0 || idx >= size ? 0 : store.getInt(alloc, idx * 22 + 17));
	}

	public void setName(Expr value) {
		if (alloc != 0 && idx >= 0 && idx < size) {
			store.setInt(alloc, idx * 22 + 17, value == null ? 0 : value.getRec());
		}
	}

	@Override
	public void output(Write write, int iterate) throws IOException {
		if (alloc == 0 || iterate <= 0)
			return;
		Expr fldName = getName();
		if (fldName != null && fldName.getRec() != 0) {
			write.sub("name", true);
			fldName.output(write, iterate - 1);
			write.endSub();
		}
		outputOperator(write, iterate, false);
		write.endRecord();
	}

	@Override
	public String toString() {
		Write write = new Write(new StringBuilder());
		try {
			if (idx == -1)
				for (ObjectArray a : this) {
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
		setName(null);
		parseOperator(parser);
		if (parser.hasSub("name")) {
			setName(new Expr(store).parse(parser));
		}
	}

	@Override
	public int operatorPosition() {
		return idx * 22 + 17 + 4;
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
		if (field >= 1 && field <= 31)
			return nameOperator(field - 1);
		switch (field) {
		case 1:
			return "name";
		default:
			return null;
		}
	}

	@Override
	public FieldType type(int field) {
		if (field >= 1 && field <= 31)
			return typeOperator(field - 1);
		switch (field) {
		case 1:
			return FieldType.OBJECT;
		default:
			return null;
		}
	}

	@Override
	public Object get(int field) {
		if (field >= 1 && field <= 31)
			return getOperator(field - 1);
		switch (field) {
		case 1:
			return getName();
		default:
			return null;
		}
	}

	@Override
	public Iterable<? extends RecordInterface> iterate(int field, Object... key) {
		if (field >= 1 && field <= 31)
			return iterateOperator(field - 1);
		switch (field) {
		default:
			return null;
		}
	}

	@Override
	public boolean set(int field, Object value) {
		if (field >= 1 && field <= 31)
			return setOperator(field - 1, value);
		switch (field) {
		case 1:
			if (value instanceof Expr)
				setName((Expr) value);
			return value instanceof Expr;
		default:
			return false;
		}
	}

	@Override
	public ChangeInterface add(int field) {
		if (field >= 1 && field <= 31)
			return addOperator(field - 1);
		switch (field) {
		default:
			return null;
		}
	}
}