package org.memorydb.jslt;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.handler.CorruptionException;
import org.memorydb.structure.MemoryRecord;
import org.memorydb.structure.RecordData;
import org.memorydb.structure.Store;

/**
 * Automatically generated record class for marray
 */

@RecordData(name = "SubMatch")
public class MarrayArray implements MemoryRecord, ChangeMatch, Iterable<MarrayArray> {
	private final Store store;
	private final Match parent;
	private final int idx;
	private int alloc;
	private int size;

	/* package private */ MarrayArray(Match parent, int idx) {
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

	/* package private */ MarrayArray(MarrayArray other, int idx) {
		this.store = other.store;
		this.parent = other.parent;
		this.idx = idx;
		this.alloc = other.alloc;
		this.size = other.size;
	}

	/* package private */ MarrayArray(Store store, int rec, int idx) {
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
	public MarrayArray copy(int rec) {
		assert store.validate(rec);
		return new MarrayArray(store, rec, -1);
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
	public MarrayArray add() {
		if (parent.rec() == 0)
			return this;
		if (alloc == 0) {
			alloc = store.allocate(13 + 17);
			up(parent);
		} else
			alloc = store.resize(alloc, (17 + (idx + 1) * 13) / 8);
		store.setInt(parent.rec(), parent.matchPosition() + 5, alloc);
		size++;
		store.setInt(alloc, 4, size);
		MarrayArray res = new MarrayArray(parent, size - 1);
		return res;
	}

	@Override
	public Iterator<MarrayArray> iterator() {
		return new Iterator<>() {
			int element = -1;

			@Override
			public boolean hasNext() {
				return alloc != 0 && element + 1 < size;
			}

			@Override
			public MarrayArray next() {
				if (alloc == 0 || element > size)
					throw new NoSuchElementException();
				element++;
				return new MarrayArray(MarrayArray.this, element);
			}

			@Override
			public void remove() {
				if (alloc == 0 || element > size || element < 0)
					throw new NoSuchElementException();
				store.copy(alloc, (element + 1) * 13 + 17, element * 13 + 17, size * 17);
				element--;
				size--;
				store.setInt(alloc, 4, size);
			}
		};
	}

	@Override
	public void output(Write write, int iterate) {
		if (alloc == 0 || iterate <= 0)
			return;
		outputMatch(write, iterate);
		write.endRecord();
	}

	@Override
	public String toString() {
		Write write = new Write(new StringBuilder());
		if (idx == -1)
			for (MarrayArray a : this) {
				a.output(write, 4);
			}
		else
			output(write, 4);
		return write.toString();
	}

	public void parse(Parser parser) {
		parseMatch(parser);
	}

	@Override
	public int matchPosition() {
		return idx * 13 + 17;
	}

	@Override
	public String name() {
		int field = 0;
		if (idx == -1)
			return null;
		if (field >= 0 && field <= 15)
			return nameMatch(field - 0);
		switch (field) {
		default:
			return null;
		}
	}

	@Override
	public FieldType type() {
		int field = 0;
		if (idx == -1)
			return field < 1 || field > size ? null : FieldType.OBJECT;
		if (field >= 0 && field <= 15)
			return typeMatch(field - 0);
		switch (field) {
		default:
			return null;
		}
	}

	@Override
	public Object java() {
		int field = 0;
		if (idx == -1)
			return field < 1 || field > size ? null : new MarrayArray(parent, field - 1);
		if (field >= 0 && field <= 15)
			return getMatch(field - 0);
		switch (field) {
		default:
			return null;
		}
	}

	@Override
	public boolean java(Object value) {
		int field = 0;
		if (field >= 0 && field <= 15)
			return setMatch(field - 0, value);
		switch (field) {
		default:
			return false;
		}
	}

	@Override
	public MarrayArray index(int idx) {
		return null;
	}

	@Override
	public MarrayArray start() {
		return null;
	}

	@Override
	public MarrayArray next() {
		return null;
	}

	@Override
	public MarrayArray copy() {
		return new MarrayArray(parent, idx);
	}
}
