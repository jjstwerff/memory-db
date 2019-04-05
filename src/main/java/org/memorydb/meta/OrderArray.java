package org.memorydb.meta;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.structure.ChangeInterface;
import org.memorydb.structure.FieldData;
import org.memorydb.structure.MemoryRecord;
import org.memorydb.structure.RecordData;
import org.memorydb.structure.Store;

/**
 * Automatically generated record class for order
 */

@RecordData(name = "OrderField")
public class OrderArray implements MemoryRecord, ChangeInterface, Iterable<OrderArray> {
	private final Store store;
	private final Field parent;
	private final int idx;
	private int alloc;
	private int size;

	/* package private */ OrderArray(Field parent, int idx) {
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
	public OrderArray copy(int rec) {
		assert store.validate(rec);
		return new OrderArray(store, rec, -1);
	}

	private void up(Field record) {
		store.setInt(alloc, 8, record.rec());
	}

	@Override
	public Field up() {
		return new Field(store, store.getInt(alloc, 8));
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
	public OrderArray add() {
		if (parent.rec() == 0)
			return this;
		if (alloc == 0) {
			alloc = store.allocate(4 + 12);
			up(parent);
		} else
			alloc = store.resize(alloc, (12 + (idx + 1) * 4) / 8);
		store.setInt(parent.rec(), 17, alloc);
		size++;
		store.setInt(alloc, 4, size);
		OrderArray res = new OrderArray(parent, size - 1);
		res.setField(null);
		return res;
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

			@Override
			public void remove() {
				if (alloc == 0 || element > size || element < 0)
					throw new NoSuchElementException();
				store.copy(alloc, (element + 1) * 4 + 12, element * 4 + 12, size * 17);
				element--;
				size--;
				store.setInt(alloc, 4, size);
			}
		};
	}

	@FieldData(name = "order", type = "ARRAY", related = OrderArray.class, when = "INDEX", mandatory = false)
	public Field getField() {
		return new Field(store, alloc == 0 || idx < 0 || idx >= size ? 0 : store.getInt(alloc, idx * 4 + 12));
	}

	public void setField(Field value) {
		if (alloc != 0 && idx >= 0 && idx < size) {
			store.setInt(alloc, idx * 4 + 12, value == null ? 0 : value.rec());
		}
	}

	@Override
	public void output(Write write, int iterate) {
		if (alloc == 0 || iterate <= 0)
			return;
		write.strField("field", "{" + getField().keys() + "}");
		write.endRecord();
	}

	@Override
	public String toString() {
		Write write = new Write(new StringBuilder());
		if (idx == -1)
			for (OrderArray a : this) {
				a.output(write, 4);
			}
		else
			output(write, 4);
		return write.toString();
	}

	public void parse(Parser parser) {
		setField(null);
		if (parser.hasField("field")) {
			parser.getRelation("field", (recNr, idx) -> {
				Field relRec = Field.parseKey(parser, up().up());
				OrderArray old = new OrderArray(store, recNr, idx);
				old.setField(relRec);
				return relRec != null;
			}, idx);
		}
	}

	@Override
	public String name() {
		int field = 0;
		if (idx == -1)
			return null;
		switch (field) {
		case 1:
			return "field";
		default:
			return null;
		}
	}

	@Override
	public FieldType type() {
		int field = 0;
		if (idx == -1)
			return field < 1 || field > size ? null : FieldType.OBJECT;
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
			return field < 1 || field > size ? null : new OrderArray(parent, field - 1);
		switch (field) {
		case 1:
			return getField();
		default:
			return null;
		}
	}

	@Override
	public boolean java(Object value) {
		int field = 0;
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
	public OrderArray index(int idx) {
		return null;
	}

	@Override
	public OrderArray start() {
		return null;
	}

	@Override
	public OrderArray next() {
		return null;
	}

	@Override
	public OrderArray copy() {
		return new OrderArray(parent, idx);
	}
}
