package org.memorydb.jslt;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.handler.CorruptionException;
import org.memorydb.structure.FieldData;
import org.memorydb.structure.MemoryRecord;
import org.memorydb.structure.RecordData;
import org.memorydb.structure.Store;

/**
 * Automatically generated record class for mobject
 */

@RecordData(name = "MatchField")
public class MobjectArray implements MemoryRecord, ChangeMatch, Iterable<MobjectArray> {
	private final Store store;
	private final Match parent;
	private final int idx;
	private int alloc;
	private int size;

	/* package private */ MobjectArray(Match parent, int idx) {
		this.store = parent.store();
		this.parent = parent;
		this.idx = idx;
		if (parent.rec() != 0) {
			this.alloc = store.getInt(parent.rec(), parent.matchPosition() + 5);
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
	public MobjectArray copy(int newRec) {
		assert store.validate(newRec);
		return new MobjectArray(store, newRec, -1);
	}

	private void up(Match record) {
		store.setInt(alloc, 8, record.rec());
		if (record instanceof MobjectArray) {
			store.setByte(alloc, 12, 1);
			store.setInt(alloc, 13, record.index());
		}
		if (record instanceof MatchObject)
			store.setByte(alloc, 12, 2);
		if (record instanceof ParametersArray) {
			store.setByte(alloc, 12, 3);
			store.setInt(alloc, 13, record.index());
		}
		if (record instanceof MarrayArray) {
			store.setByte(alloc, 12, 4);
			store.setInt(alloc, 13, record.index());
		}
	}

	@Override
	public Match up() {
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
	public MobjectArray add() {
		if (parent.rec() == 0)
			return this;
		if (alloc == 0) {
			alloc = store.allocate(17 + 17);
			up(parent);
		} else
			alloc = store.resize(alloc, (17 + (size + 1) * 17) / 8);
		store.setInt(parent.rec(), parent.matchPosition() + 5, alloc);
		size++;
		store.setInt(alloc, 4, size);
		MobjectArray res = new MobjectArray(parent, size - 1);
		res.setName(null);
		return res;
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

			@Override
			public void remove() {
				if (alloc == 0 || element > size || element < 0)
					throw new NoSuchElementException();
				store.copy(alloc, (element + 1) * 17 + 17, element * 17 + 17, size * 17);
				element--;
				size--;
				store.setInt(alloc, 4, size);
			}
		};
	}

	@FieldData(name = "mobject", type = "ARRAY", related = MobjectArray.class, when = "OBJECT", mandatory = false)
	public String getName() {
		return alloc == 0 || idx < 0 || idx >= size ? null : store.getString(store.getInt(alloc, idx * 17 + 17));
	}

	public void setName(String value) {
		if (alloc != 0 && idx >= 0 && idx < size) {
			store.setInt(alloc, idx * 17 + 17, store.putString(value));
		}
	}

	@Override
	public void output(Write write, int iterate) {
		if (alloc == 0 || iterate <= 0)
			return;
		write.field("name", getName());
		outputMatch(write, iterate);
		write.endRecord();
	}

	@Override
	public String toString() {
		Write write = new Write(new StringBuilder());
		if (idx == -1)
			for (MobjectArray a : this) {
				a.output(write, 4);
			}
		else
			output(write, 4);
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
		return idx * 17 + 17 + 4;
	}

	@Override
	public String name() {
		if (idx == -1)
			return null;
		if (idx >= 1 && idx <= 16)
			return nameMatch(idx - 1);
		switch (idx) {
		case 0:
			return "name";
		default:
			return null;
		}
	}

	@Override
	public FieldType type() {
		if (idx == -1)
			return FieldType.OBJECT;
		if (idx >= 1 && idx <= 16)
			return typeMatch(idx - 1);
		switch (idx) {
		case 0:
			return FieldType.STRING;
		default:
			return null;
		}
	}

	@Override
	public Object java() {
		if (idx == -1)
			return this;
		if (idx >= 1 && idx <= 16)
			return getMatch(idx - 1);
		switch (idx) {
		case 0:
			return getName();
		default:
			return null;
		}
	}

	@Override
	public boolean java(Object value) {
		if (idx >= 1 && idx <= 16)
			return setMatch(idx - 1, value);
		switch (idx) {
		case 0:
			if (value instanceof String)
				setName((String) value);
			return value instanceof String;
		default:
			return false;
		}
	}

	@Override
	public MobjectArray index(int idx) {
		return idx < 0 || idx >= size ? null : new MobjectArray(parent, idx);
	}

	@Override
	public MobjectArray start() {
		return new MobjectArray(parent, 0);
	}

	@Override
	public MobjectArray next() {
		return idx + 1 >= size ? null : new MobjectArray(parent, idx + 1);
	}

	@Override
	public boolean testLast() {
		return idx == size - 1;
	}

	@Override
	public MobjectArray copy() {
		return new MobjectArray(parent, idx);
	}
}
