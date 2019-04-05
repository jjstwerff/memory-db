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
 * Automatically generated record class for values
 */

@RecordData(name = "Value")
public class ValuesArray implements MemoryRecord, ChangeInterface, Iterable<ValuesArray> {
	private final Store store;
	private final Field parent;
	private final int idx;
	private int alloc;
	private int size;

	/* package private */ ValuesArray(Field parent, int idx) {
		this.store = parent.store();
		this.parent = parent;
		this.idx = idx;
		if (parent.rec() != 0) {
			this.alloc = store.getInt(parent.rec(), 13);
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

	/* package private */ ValuesArray(ValuesArray other, int idx) {
		this.store = other.store;
		this.parent = other.parent;
		this.idx = idx;
		this.alloc = other.alloc;
		this.size = other.size;
	}

	/* package private */ ValuesArray(Store store, int rec, int idx) {
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
	public ValuesArray copy(int rec) {
		assert store.validate(rec);
		return new ValuesArray(store, rec, -1);
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
	public ValuesArray add() {
		if (parent.rec() == 0)
			return this;
		if (alloc == 0) {
			alloc = store.allocate(8 + 12);
			up(parent);
		} else
			alloc = store.resize(alloc, (12 + (idx + 1) * 8) / 8);
		store.setInt(parent.rec(), 13, alloc);
		size++;
		store.setInt(alloc, 4, size);
		ValuesArray res = new ValuesArray(parent, size - 1);
		res.setValue(null);
		res.setDescription(null);
		return res;
	}

	@Override
	public Iterator<ValuesArray> iterator() {
		return new Iterator<>() {
			int element = -1;

			@Override
			public boolean hasNext() {
				return alloc != 0 && element + 1 < size;
			}

			@Override
			public ValuesArray next() {
				if (alloc == 0 || element > size)
					throw new NoSuchElementException();
				element++;
				return new ValuesArray(ValuesArray.this, element);
			}

			@Override
			public void remove() {
				if (alloc == 0 || element > size || element < 0)
					throw new NoSuchElementException();
				store.copy(alloc, (element + 1) * 8 + 12, element * 8 + 12, size * 17);
				element--;
				size--;
				store.setInt(alloc, 4, size);
			}
		};
	}

	@FieldData(name = "values", type = "ARRAY", related = ValuesArray.class, when = "ENUMERATE", mandatory = false)
	public String getValue() {
		return alloc == 0 || idx < 0 || idx >= size ? null : store.getString(store.getInt(alloc, idx * 8 + 12));
	}

	public void setValue(String value) {
		if (alloc != 0 && idx >= 0 && idx < size) {
			store.setInt(alloc, idx * 8 + 12, store.putString(value));
		}
	}

	@FieldData(name = "values", type = "ARRAY", related = ValuesArray.class, when = "ENUMERATE", mandatory = false)
	public String getDescription() {
		return alloc == 0 || idx < 0 || idx >= size ? null : store.getString(store.getInt(alloc, idx * 8 + 16));
	}

	public void setDescription(String value) {
		if (alloc != 0 && idx >= 0 && idx < size) {
			store.setInt(alloc, idx * 8 + 16, store.putString(value));
		}
	}

	@Override
	public void output(Write write, int iterate) {
		if (alloc == 0 || iterate <= 0)
			return;
		write.field("value", getValue());
		write.field("description", getDescription());
		write.endRecord();
	}

	@Override
	public String toString() {
		Write write = new Write(new StringBuilder());
		if (idx == -1)
			for (ValuesArray a : this) {
				a.output(write, 4);
			}
		else
			output(write, 4);
		return write.toString();
	}

	public void parse(Parser parser) {
		setValue(null);
		setDescription(null);
		if (parser.hasField("value")) {
			setValue(parser.getString("value"));
		}
		if (parser.hasField("description")) {
			setDescription(parser.getString("description"));
		}
	}

	@Override
	public String name() {
		int field = 0;
		if (idx == -1)
			return null;
		switch (field) {
		case 1:
			return "value";
		case 2:
			return "description";
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
			return FieldType.STRING;
		case 2:
			return FieldType.STRING;
		default:
			return null;
		}
	}

	@Override
	public Object java() {
		int field = 0;
		if (idx == -1)
			return field < 1 || field > size ? null : new ValuesArray(parent, field - 1);
		switch (field) {
		case 1:
			return getValue();
		case 2:
			return getDescription();
		default:
			return null;
		}
	}

	@Override
	public boolean java(Object value) {
		int field = 0;
		switch (field) {
		case 1:
			if (value instanceof String)
				setValue((String) value);
			return value instanceof String;
		case 2:
			if (value instanceof String)
				setDescription((String) value);
			return value instanceof String;
		default:
			return false;
		}
	}

	@Override
	public ValuesArray index(int idx) {
		return null;
	}

	@Override
	public ValuesArray start() {
		return null;
	}

	@Override
	public ValuesArray next() {
		return null;
	}

	@Override
	public ValuesArray copy() {
		return new ValuesArray(parent, idx);
	}
}
