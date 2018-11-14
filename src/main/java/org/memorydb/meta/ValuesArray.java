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
 * Automatically generated record class for values
 */

@RecordData(name = "Str")
public class ValuesArray implements ChangeInterface, Iterable<ValuesArray> {
	private final Store store;
	private final Field parent;
	private int idx;
	private int alloc;
	private int size;

	/* package private */ ValuesArray(Field parent, int idx) {
		this.store = parent.getStore();
		this.parent = parent;
		this.idx = idx;
		if (parent.getRec() != 0) {
			this.alloc = store.getInt(parent.getRec(), 18);
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

	/* package private */ ValuesArray add() {
		if (parent.getRec() == 0)
			return this;
		idx = size;
		if (alloc == 0) {
			alloc = store.allocate(4 + 12);
			setUpRecord(parent);
		} else
			alloc = store.resize(alloc, (12 + (idx + 1) * 4) / 8);
		store.setInt(parent.getRec(), 18, alloc);
		size = idx + 1;
		store.setInt(alloc, 4, size);
		setStr(null);
		return this;
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
		};
	}

	@FieldData(
		name = "values",
		type = "ARRAY",
		related = ValuesArray.class,
		mandatory = false
	)

	public String getStr() {
		return alloc == 0 || idx < 0 || idx >= size ? null : store.getString(store.getInt(alloc, idx * 4 + 12));
	}

	public void setStr(String value) {
		if (alloc != 0 && idx >= 0 && idx < size) {
			store.setInt(alloc, idx * 4 + 12, store.putString(value));
		}
	}

	@Override
	public void output(Write write, int iterate) throws IOException {
		if (alloc == 0 || iterate <= 0)
			return;
		write.field("str", getStr());
		write.endRecord();
	}

	@Override
	public String toString() {
		Write write = new Write(new StringBuilder());
		try {
			if (idx == -1)
				for (ValuesArray a : this) {
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
		setStr(null);
		if (parser.hasField("str")) {
			setStr(parser.getString("str"));
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
			return "str";
		default:
			return null;
		}
	}

	@Override
	public FieldType type(int field) {
		switch (field) {
		case 1:
			return FieldType.STRING;
		default:
			return null;
		}
	}

	@Override
	public Object get(int field) {
		switch (field) {
		case 1:
			return getStr();
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
			if (value instanceof String)
				setStr((String) value);
			return value instanceof String;
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