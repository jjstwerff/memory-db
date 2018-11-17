package org.memorydb.meta;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.structure.ChangeInterface;
import org.memorydb.structure.FieldData;
import org.memorydb.structure.InputOutputException;
import org.memorydb.structure.RecordData;
import org.memorydb.structure.RecordInterface;
import org.memorydb.structure.Store;

/**
 * Automatically generated record class for order
 */

@RecordData(name = "OrderField")
public class OrderArray implements ChangeInterface, Iterable<OrderArray> {
	private final Store store;
	private final Field parent;
	private int idx;
	private int alloc;
	private int size;

	/* package private */ OrderArray(Field parent, int idx) {
		this.store = parent.getStore();
		this.parent = parent;
		this.idx = idx;
		if (parent.getRec() != 0) {
			this.alloc = store.getInt(parent.getRec(), 94);
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

	/* package private */ OrderArray(OrderArray other, int idx) {
		this.store = other.store;
		this.parent = other.parent;
		this.idx = idx;
		this.alloc = other.alloc;
		this.size = other.size;
	}

	/* package private */ OrderArray(Store store, int rec, int idx) {
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

	/* package private */ void setUpRecord(Field record) {
		store.setInt(alloc, 8, record.getRec());
	}

	@Override
	public Field getUpRecord() {
		return new Field(store, store.getInt(alloc, 8));
	}

	@Override
	public Store getStore() {
		return store;
	}

	@Override
	public int getSize() {
		return size;
	}

	/* package private */ OrderArray add() {
		if (parent.getRec() == 0)
			return this;
		idx = size;
		if (alloc == 0) {
			alloc = store.allocate(4 + 12);
			setUpRecord(parent);
		} else
			alloc = store.resize(alloc, (12 + (idx + 1) * 4) / 8);
		store.setInt(parent.getRec(), 94, alloc);
		size = idx + 1;
		store.setInt(alloc, 4, size);
		setField(null);
		return this;
	}

	@Override
	public Iterator<OrderArray> iterator() {
		return new Iterator<>() {
			int element = -1;

			@Override
			public boolean hasNext() {
				return alloc != 0 && element + 1 < size;
			}

			@Override
			public OrderArray next() {
				if (alloc == 0 || element > size)
					throw new NoSuchElementException();
				element++;
				return new OrderArray(OrderArray.this, element);
			}
		};
	}

	@FieldData(
		name = "order",
		type = "ARRAY",
		related = OrderArray.class,
		when = "INDEX",
		mandatory = false
	)

	public Field getField() {
		return new Field(store, alloc == 0 || idx < 0 || idx >= size ? 0 : store.getInt(alloc, idx * 4 + 12));
	}

	public void setField(Field value) {
		if (alloc != 0 && idx >= 0 && idx < size) {
			store.setInt(alloc, idx * 4 + 12, value == null ? 0 : value.getRec());
		}
	}

	@Override
	public void output(Write write, int iterate) throws IOException {
		if (alloc == 0 || iterate <= 0)
			return;
		write.strField("field", "{" + getField().keys() + "}");
		write.endRecord();
	}

	@Override
	public String toString() {
		Write write = new Write(new StringBuilder());
		try {
			if (idx == -1)
				for (OrderArray a : this) {
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
		setField(null);
		if (parser.hasField("field")) {
			parser.getRelation("field", (int recNr) -> {
				Iterator<Field> iterator = null;				Field relRec = iterator.hasNext() ? iterator.next() : null;
				boolean found = relRec != null && relRec.parseKey(parser);
				setField(relRec);
				return found;
			}, idx);
		}
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
		switch (field) {
		case 1:
			return "field";
		default:
			return null;
		}
	}

	@Override
	public FieldType type(int field) {
		switch (field) {
		case 1:
			return FieldType.OBJECT;
		default:
			return null;
		}
	}

	@Override
	public Object get(int field) {
		switch (field) {
		case 1:
			return getField();
		default:
			return null;
		}
	}

	@Override
	public Iterable<? extends RecordInterface> iterate(int field, Object... key) {
		switch (field) {
		default:
			return null;
		}
	}

	@Override
	public boolean set(int field, Object value) {
		switch (field) {
		case 1:
			if (value instanceof Field)
				setField((Field) value);
			return value instanceof Field;
		default:
			return false;
		}
	}

	@Override
	public ChangeInterface add(int field) {
		switch (field) {
		default:
			return null;
		}
	}
}