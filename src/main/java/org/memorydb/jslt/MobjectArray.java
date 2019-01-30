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
 * Automatically generated record class for mobject
 */

@RecordData(name = "MatchField")
public class MobjectArray implements ChangeMatch, Iterable<MobjectArray> {
	private final Store store;
	private final Match parent;
	private int idx;
	private int alloc;
	private int size;

	/* package private */ MobjectArray(Match parent, int idx) {
		this.store = parent.getStore();
		this.parent = parent;
		this.idx = idx;
		if (parent.getRec() != 0) {
			this.alloc = store.getInt(parent.getRec(), parent.matchPosition() + 1);
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

	/* package private */ MobjectArray(MobjectArray other, int idx) {
		this.store = other.store;
		this.parent = other.parent;
		this.idx = idx;
		this.alloc = other.alloc;
		this.size = other.size;
	}

	/* package private */ MobjectArray(Store store, int rec, int idx) {
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

	/* package private */ void setUpRecord(Match record) {
		store.setInt(alloc, 8, record.getRec());
		if (record instanceof MobjectArray) {
			store.setByte(alloc, 12, 1);
			store.setInt(alloc, 13, record.getArrayIndex());
		}
		if (record instanceof MatchObject)
			store.setByte(alloc, 12, 2);
		if (record instanceof ParametersArray) {
			store.setByte(alloc, 12, 3);
			store.setInt(alloc, 13, record.getArrayIndex());
		}
		if (record instanceof MarrayArray) {
			store.setByte(alloc, 12, 4);
			store.setInt(alloc, 13, record.getArrayIndex());
		}
	}

	@Override
	public Match getUpRecord() {
		if (alloc == 0)
			return null;
		switch (store.getByte(alloc, 12)) {
		case 1:
			return new MobjectArray(store, store.getInt(alloc, 8), store.getInt(alloc, 13));
		case 2:
			return new MatchObject(store, store.getInt(alloc, 8));
		case 3:
			return new ParametersArray(store, store.getInt(alloc, 8), store.getInt(alloc, 13));
		case 4:
			return new MarrayArray(store, store.getInt(alloc, 8), store.getInt(alloc, 13));
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

	/* package private */ MobjectArray add() {
		if (parent.getRec() == 0)
			return this;
		idx = size;
		if (alloc == 0) {
			alloc = store.allocate(13 + 17);
			setUpRecord(parent);
		} else
			alloc = store.resize(alloc, (17 + (idx + 1) * 13) / 8);
		store.setInt(parent.getRec(), parent.matchPosition() + 1, alloc);
		size = idx + 1;
		store.setInt(alloc, 4, size);
		setName(null);
		return this;
	}

	@Override
	public Iterator<MobjectArray> iterator() {
		return new Iterator<>() {
			int element = -1;

			@Override
			public boolean hasNext() {
				return alloc != 0 && element + 1 < size;
			}

			@Override
			public MobjectArray next() {
				if (alloc == 0 || element > size)
					throw new NoSuchElementException();
				element++;
				return new MobjectArray(MobjectArray.this, element);
			}
		};
	}

	@FieldData(
		name = "mobject",
		type = "ARRAY",
		related = MobjectArray.class,
		when = "OBJECT",
		mandatory = false
	)

	public String getName() {
		return alloc == 0 || idx < 0 || idx >= size ? null : store.getString(store.getInt(alloc, idx * 13 + 17));
	}

	public void setName(String value) {
		if (alloc != 0 && idx >= 0 && idx < size) {
			store.setInt(alloc, idx * 13 + 17, store.putString(value));
		}
	}

	@Override
	public void output(Write write, int iterate) throws IOException {
		if (alloc == 0 || iterate <= 0)
			return;
		write.field("name", getName());
		outputMatch(write, iterate);
		write.endRecord();
	}

	@Override
	public String toString() {
		Write write = new Write(new StringBuilder());
		try {
			if (idx == -1)
				for (MobjectArray a : this) {
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
		parseMatch(parser);
		if (parser.hasField("name")) {
			setName(parser.getString("name"));
		}
	}

	@Override
	public int matchPosition() {
		return idx * 13 + 17 + 4;
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
		if (field >= 1 && field <= 17)
			return nameMatch(field - 1);
		switch (field) {
		case 1:
			return "name";
		default:
			return null;
		}
	}

	@Override
	public FieldType type(int field) {
		if (field >= 1 && field <= 17)
			return typeMatch(field - 1);
		switch (field) {
		case 1:
			return FieldType.STRING;
		default:
			return null;
		}
	}

	@Override
	public Object get(int field) {
		if (field >= 1 && field <= 17)
			return getMatch(field - 1);
		switch (field) {
		case 1:
			return getName();
		default:
			return null;
		}
	}

	@Override
	public Iterable<? extends RecordInterface> iterate(int field, Object... key) {
		if (field >= 1 && field <= 17)
			return iterateMatch(field - 1);
		switch (field) {
		default:
			return null;
		}
	}

	@Override
	public boolean set(int field, Object value) {
		if (field >= 1 && field <= 17)
			return setMatch(field - 1, value);
		switch (field) {
		case 1:
			if (value instanceof String)
				setName((String) value);
			return value instanceof String;
		default:
			return false;
		}
	}

	@Override
	public ChangeInterface add(int field) {
		if (field >= 1 && field <= 17)
			return addMatch(field - 1);
		switch (field) {
		default:
			return null;
		}
	}
}