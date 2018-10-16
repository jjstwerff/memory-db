package org.memorydb.jslt;

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
 * Automatically generated record class for parameters
 */

@RecordData(name = "Parameter")
public class ParametersArray implements ChangeMatch, Iterable<ParametersArray> {
	private final Store store;
	private final Alternative parent;
	private int idx;
	private int alloc;
	private int size;

	/* package private */ ParametersArray(Alternative parent, int idx) {
		this.store = parent.getStore();
		this.parent = parent;
		this.idx = idx;
		if (parent.getRec() != 0) {
			this.alloc = store.getInt(parent.getRec(), 8);
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

	/* package private */ ParametersArray(ParametersArray other, int idx) {
		this.store = other.store;
		this.parent = other.parent;
		this.idx = idx;
		this.alloc = other.alloc;
		this.size = other.size;
	}

	/* package private */ ParametersArray(Store store, int rec, int idx) {
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

	/* package private */ void setUpRecord(Alternative record) {
		store.setInt(alloc, 8, record.getRec());
	}

	@Override
	public Alternative getUpRecord() {
		return new Alternative(store, store.getInt(alloc, 8));
	}

	@Override
	public Store getStore() {
		return store;
	}

	public int getSize() {
		return size;
	}

	/* package private */ ParametersArray add() {
		if (parent.getRec() == 0)
			return this;
		idx = size;
		if (alloc == 0) {
			alloc = store.allocate(13 + 12);
			setUpRecord(parent);
		} else
			alloc = store.resize(alloc, (12 + (idx + 1) * 13) / 8);
		store.setInt(parent.getRec(), 8, alloc);
		size = idx + 1;
		store.setInt(alloc, 4, size);
		setIf(null);
		return this;
	}

	@Override
	public Iterator<ParametersArray> iterator() {
		return new Iterator<>() {
			int element = -1;

			@Override
			public boolean hasNext() {
				return alloc != 0 && element + 1 < size;
			}

			@Override
			public ParametersArray next() {
				if (alloc == 0 || element > size)
					throw new NoSuchElementException();
				element++;
				return new ParametersArray(ParametersArray.this, element);
			}
		};
	}

	@FieldData(
		name = "parameters",
		type = "ARRAY",
		related = ParametersArray.class,
		mandatory = false
	)

	public Expr getIf() {
		return new Expr(store, alloc == 0 || idx < 0 || idx >= size ? 0 : store.getInt(alloc, idx * 13 + 12));
	}

	public void setIf(Expr value) {
		if (alloc != 0 && idx >= 0 && idx < size) {
			store.setInt(alloc, idx * 13 + 12, value == null ? 0 : value.getRec());
		}
	}

	@Override
	public void output(Write write, int iterate) throws IOException {
		if (alloc == 0 || iterate <= 0)
			return;
		Expr fldIf = getIf();
		if (fldIf != null && fldIf.getRec() != 0) {
			write.sub("if", true);
			fldIf.output(write, iterate - 1);
			write.endSub();
		}
		outputMatch(write, iterate, false);
		write.endRecord();
	}

	@Override
	public String toString() {
		Write write = new Write(new StringBuilder());
		try {
			if (idx == -1)
				for (ParametersArray a : this) {
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
		setIf(null);
		parseMatch(parser);
		if (parser.hasSub("if")) {
			setIf(new Expr(store).parse(parser));
		}
	}

	@Override
	public int matchPosition() {
		return idx * 13 + 12 + 4;
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
		if (field >= 1 && field <= 9)
			return nameMatch(field - 1);
		switch (field) {
		case 1:
			return "if";
		default:
			return null;
		}
	}

	@Override
	public FieldType type(int field) {
		if (field >= 1 && field <= 9)
			return typeMatch(field - 1);
		switch (field) {
		case 1:
			return FieldType.OBJECT;
		default:
			return null;
		}
	}

	@Override
	public Object get(int field) {
		if (field >= 1 && field <= 9)
			return getMatch(field - 1);
		switch (field) {
		case 1:
			return getIf();
		default:
			return null;
		}
	}

	@Override
	public Iterable<? extends RecordInterface> iterate(int field, Object... key) {
		if (field >= 1 && field <= 9)
			return iterateMatch(field - 1);
		switch (field) {
		default:
			return null;
		}
	}

	@Override
	public boolean set(int field, Object value) {
		if (field >= 1 && field <= 9)
			return setMatch(field - 1, value);
		switch (field) {
		case 1:
			if (value instanceof Expr)
				setIf((Expr) value);
			return value instanceof Expr;
		default:
			return false;
		}
	}

	@Override
	public ChangeInterface add(int field) {
		if (field >= 1 && field <= 9)
			return addMatch(field - 1);
		switch (field) {
		default:
			return null;
		}
	}
}