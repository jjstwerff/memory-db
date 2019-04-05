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
 * Automatically generated record class for mparms
 */

@RecordData(name = "Step")
public class MparmsArray implements MemoryRecord, ChangeOperator, Iterable<MparmsArray> {
	private final Store store;
	private final Match parent;
	private final int idx;
	private int alloc;
	private int size;

	/* package private */ MparmsArray(Match parent, int idx) {
		this.store = parent.store();
		this.parent = parent;
		this.idx = idx;
		if (parent.rec() != 0) {
			this.alloc = store.getInt(parent.rec(), parent.matchPosition() + 9);
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

	/* package private */ MparmsArray(MparmsArray other, int idx) {
		this.store = other.store;
		this.parent = other.parent;
		this.idx = idx;
		this.alloc = other.alloc;
		this.size = other.size;
	}

	/* package private */ MparmsArray(Store store, int rec, int idx) {
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
	public MparmsArray copy(int rec) {
		assert store.validate(rec);
		return new MparmsArray(store, rec, -1);
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
	public MparmsArray add() {
		if (parent.rec() == 0)
			return this;
		if (alloc == 0) {
			alloc = store.allocate(18 + 17);
			up(parent);
		} else
			alloc = store.resize(alloc, (17 + (idx + 1) * 18) / 8);
		store.setInt(parent.rec(), parent.matchPosition() + 9, alloc);
		size++;
		store.setInt(alloc, 4, size);
		MparmsArray res = new MparmsArray(parent, size - 1);
		return res;
	}

	@Override
	public Iterator<MparmsArray> iterator() {
		return new Iterator<>() {
			int element = -1;

			@Override
			public boolean hasNext() {
				return alloc != 0 && element + 1 < size;
			}

			@Override
			public MparmsArray next() {
				if (alloc == 0 || element > size)
					throw new NoSuchElementException();
				element++;
				return new MparmsArray(MparmsArray.this, element);
			}

			@Override
			public void remove() {
				if (alloc == 0 || element > size || element < 0)
					throw new NoSuchElementException();
				store.copy(alloc, (element + 1) * 18 + 17, element * 18 + 17, size * 17);
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
		outputOperator(write, iterate);
		write.endRecord();
	}

	@Override
	public String toString() {
		Write write = new Write(new StringBuilder());
		if (idx == -1)
			for (MparmsArray a : this) {
				a.output(write, 4);
			}
		else
			output(write, 4);
		return write.toString();
	}

	public void parse(Parser parser) {
		parseOperator(parser);
	}

	@Override
	public int operatorPosition() {
		return idx * 18 + 17;
	}

	@Override
	public String name() {
		int field = 0;
		if (idx == -1)
			return null;
		if (field >= 0 && field <= 28)
			return nameOperator(field - 0);
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
		if (field >= 0 && field <= 28)
			return typeOperator(field - 0);
		switch (field) {
		default:
			return null;
		}
	}

	@Override
	public Object java() {
		int field = 0;
		if (idx == -1)
			return field < 1 || field > size ? null : new MparmsArray(parent, field - 1);
		if (field >= 0 && field <= 28)
			return getOperator(field - 0);
		switch (field) {
		default:
			return null;
		}
	}

	@Override
	public boolean java(Object value) {
		int field = 0;
		if (field >= 0 && field <= 28)
			return setOperator(field - 0, value);
		switch (field) {
		default:
			return false;
		}
	}

	@Override
	public MparmsArray index(int idx) {
		return null;
	}

	@Override
	public MparmsArray start() {
		return null;
	}

	@Override
	public MparmsArray next() {
		return null;
	}

	@Override
	public MparmsArray copy() {
		return new MparmsArray(parent, idx);
	}
}
