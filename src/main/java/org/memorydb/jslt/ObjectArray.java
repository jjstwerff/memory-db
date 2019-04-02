package org.memorydb.jslt;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.handler.CorruptionException;
import org.memorydb.structure.ChangeInterface;
import org.memorydb.structure.FieldData;
import org.memorydb.structure.MemoryRecord;
import org.memorydb.structure.RecordData;
import org.memorydb.structure.RecordInterface;
import org.memorydb.structure.Store;

/**
 * Automatically generated record class for object
 */

@RecordData(name = "Field")
public class ObjectArray implements MemoryRecord, ChangeOperator, Iterable<ObjectArray> {
	private final Store store;
	private final Operator parent;
	private final int idx;
	private int alloc;
	private int size;

	/* package private */ ObjectArray(Operator parent, int idx) {
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
	public ObjectArray add() {
		if (parent.rec() == 0)
			return this;
		idx = size;
		if (alloc == 0) {
			alloc = store.allocate(22 + 17);
			up(parent);
		} else
			alloc = store.resize(alloc, (17 + (idx + 1) * 22) / 8);
		store.setInt(parent.rec(), parent.operatorPosition() + 1, alloc);
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

			@Override
			public void remove() {
				if (alloc == 0 || element > size || element < 0)
					throw new NoSuchElementException();
				store.copy(alloc, (element + 1) * 22 + 17, element * 22 + 17, size * 17);
				element--;
				size--;
				store.setInt(alloc, 4, size);
			}
		};
	}

	@FieldData(name = "object", type = "ARRAY", related = ObjectArray.class, when = "OBJECT", mandatory = false)
	public Expr getName() {
		return new Expr(store, alloc == 0 || idx < 0 || idx >= size ? 0 : store.getInt(alloc, idx * 22 + 17));
	}

	public void setName(Expr value) {
		if (alloc != 0 && idx >= 0 && idx < size) {
			store.setInt(alloc, idx * 22 + 17, value == null ? 0 : value.rec());
		}
	}

	@Override
	public void output(Write write, int iterate) {
		if (alloc == 0 || iterate <= 0)
			return;
		Expr fldName = getName();
		if (fldName != null && fldName.rec() != 0) {
			write.sub("name");
			fldName.output(write, iterate);
			write.endSub();
		}
		outputOperator(write, iterate);
		write.endRecord();
	}

	@Override
	public String toString() {
		Write write = new Write(new StringBuilder());
		if (idx == -1)
			for (ObjectArray a : this) {
				a.output(write, 4);
			}
		else
			output(write, 4);
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
	public String name() {
		int field = 0;
		if (idx == -1)
			return null;
		if (field >= 1 && field <= 29)
			return nameOperator(field - 1);
		switch (field) {
		case 1:
			return "name";
		default:
			return null;
		}
	}

	@Override
	public FieldType type() {
		int field = 0;
		if (idx == -1)
			return field < 1 || field > size ? null : FieldType.OBJECT;
		if (field >= 1 && field <= 29)
			return typeOperator(field - 1);
		switch (field) {
		case 1:
			return FieldType.OBJECT;
		default:
			return null;
		}
	}

	@Override
	public Object java() {
		int field = 0;
		if (idx == -1)
			return field < 1 || field > size ? null : new ObjectArray(parent, field - 1);
		if (field >= 1 && field <= 29)
			return getOperator(field - 1);
		switch (field) {
		case 1:
			return getName();
		default:
			return null;
		}
	}

	@Override
	public boolean java(Object value) {
		int field = 0;
		if (field >= 1 && field <= 29)
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
	public RecordInterface next() {
		return null;
	}

	@Override
	public ObjectArray copy() {
		return new ObjectArray(parent, idx);
	}
}