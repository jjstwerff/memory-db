package org.memorydb.world;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.structure.ChangeInterface;
import org.memorydb.structure.FieldData;
import org.memorydb.structure.IndexOperation;
import org.memorydb.structure.Key;
import org.memorydb.structure.MemoryRecord;
import org.memorydb.structure.RedBlackTree;
import org.memorydb.structure.RecordData;
import org.memorydb.structure.RecordInterface;
import org.memorydb.structure.Store;

/**
 * Automatically generated record class for relation
 */

@RecordData(name = "Relations")
public class RelationArray implements MemoryRecord, ChangeInterface, Iterable<RelationArray> {
	private final Store store;
	private final Item parent;
	private final int idx;
	private int alloc;
	private int size;

	/* package private */ RelationArray(Item parent, int idx) {
		this.store = parent.store();
		this.parent = parent;
		this.idx = idx;
		if (parent.rec() != 0) {
			this.alloc = store.getInt(parent.rec(), 16);
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

	/* package private */ RelationArray(RelationArray other, int idx) {
		this.store = other.store;
		this.parent = other.parent;
		this.idx = idx;
		this.alloc = other.alloc;
		this.size = other.size;
	}

	/* package private */ RelationArray(Store store, int rec, int idx) {
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

	private void up(Item record) {
		store.setInt(alloc, 8, record.rec());
	}

	@Override
	public Item up() {
		return new Item(store, store.getInt(alloc, 8));
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
	public RelationArray add() {
		if (parent.rec() == 0)
			return this;
		idx = size;
		if (alloc == 0) {
			alloc = store.allocate(20 + 12);
			up(parent);
		} else
			alloc = store.resize(alloc, (12 + (idx + 1) * 20) / 8);
		store.setInt(parent.rec(), 16, alloc);
		size = idx + 1;
		store.setInt(alloc, 4, size);
		store.setInt(rec, 0, 0); // SET type
		store.setInt(rec, 4, 0); // SET with
		setStarted(0);
		setStopped(0);
		store.setInt(rec, 16, 0); // ARRAY specials
		return this;
	}

	@Override
	public Iterator<RelationArray> iterator() {
		return new Iterator<>() {
			int element = -1;

			@Override
			public boolean hasNext() {
				return alloc != 0 && element + 1 < size;
			}

			@Override
			public RelationArray next() {
				if (alloc == 0 || element > size)
					throw new NoSuchElementException();
				element++;
				return new RelationArray(RelationArray.this, element);
			}

			@Override
			public void remove() {
				if (alloc == 0 || element > size || element < 0)
					throw new NoSuchElementException();
				store.copy(alloc, (element + 1) * 20 + 12, element * 20 + 12, size * 17);
				element--;
				size--;
				store.setInt(alloc, 4, size);
			}
		};
	}

	@FieldData(name = "relation", type = "ARRAY", related = RelationArray.class, mandatory = false)
	public IndexType getType() {
		return alloc == 0 || idx < 0 || idx >= size ? null : new IndexType(this, new State(store));
	}

	@FieldData(name = "relation", type = "ARRAY", related = RelationArray.class, mandatory = false)
	public IndexWith getWith() {
		return alloc == 0 || idx < 0 || idx >= size ? null : new IndexWith(this, new Item(store));
	}

	@FieldData(name = "relation", type = "ARRAY", related = RelationArray.class, mandatory = false)
	public int getStarted() {
		return alloc == 0 || idx < 0 || idx >= size ? Integer.MIN_VALUE : store.getInt(alloc, idx * 20 + 20);
	}

	public void setStarted(int value) {
		if (alloc != 0 && idx >= 0 && idx < size) {
			store.setInt(alloc, idx * 20 + 20, value);
		}
	}

	@FieldData(name = "relation", type = "ARRAY", related = RelationArray.class, mandatory = false)
	public int getStopped() {
		return alloc == 0 || idx < 0 || idx >= size ? Integer.MIN_VALUE : store.getInt(alloc, idx * 20 + 24);
	}

	public void setStopped(int value) {
		if (alloc != 0 && idx >= 0 && idx < size) {
			store.setInt(alloc, idx * 20 + 24, value);
		}
	}

	@FieldData(name = "relation", type = "ARRAY", related = RelationArray.class, mandatory = false)
	public SpecialsArray getSpecials() {
		return alloc == 0 || idx < 0 || idx >= size ? null : new SpecialsArray(this, -1);
	}

	@Override
	public void output(Write write, int iterate) {
		if (alloc == 0 || iterate <= 0)
			return;
		IndexType fldType = getType();
		if (fldType != null) {
			write.sub("type");
			for (State sub : fldType)
				sub.output(write, iterate);
			write.endSub();
		}
		IndexWith fldWith = getWith();
		if (fldWith != null) {
			write.sub("with");
			for (Item sub : fldWith)
				sub.output(write, iterate);
			write.endSub();
		}
		write.field("started", getStarted());
		write.field("stopped", getStopped());
		SpecialsArray fldSpecials = getSpecials();
		if (fldSpecials != null) {
			write.sub("specials");
			for (SpecialsArray sub : fldSpecials)
				sub.output(write, iterate);
			write.endSub();
		}
		write.endRecord();
	}

	@Override
	public String toString() {
		Write write = new Write(new StringBuilder());
		if (idx == -1)
			for (RelationArray a : this) {
				a.output(write, 4);
			}
		else
			output(write, 4);
		return write.toString();
	}

	public void parse(Parser parser) {
		store.setInt(rec, 0, 0); // SET type
		store.setInt(rec, 4, 0); // SET with
		setStarted(0);
		setStopped(0);
		store.setInt(rec, 16, 0); // ARRAY specials
		if (parser.hasSub("type")) {
			new State(store).parse(parser, record);
		}
		if (parser.hasSub("with")) {
			new Item(store).parse(parser, record);
		}
		if (parser.hasField("started")) {
			setStarted(parser.getInt("started"));
		}
		if (parser.hasField("stopped")) {
			setStopped(parser.getInt("stopped"));
		}
		if (parser.hasSub("specials")) {
			try (SpecialsArray sub = new SpecialsArray(record, -1)) {
				while (parser.getSub()) {
					sub.add();
					sub.parse(parser);
				}
			}
		}
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
		switch (field) {
		case 1:
			return "type";
		case 2:
			return "with";
		case 3:
			return "started";
		case 4:
			return "stopped";
		case 5:
			return "specials";
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
			return FieldType.ARRAY;
		case 2:
			return FieldType.ARRAY;
		case 3:
			return FieldType.INTEGER;
		case 4:
			return FieldType.INTEGER;
		case 5:
			return FieldType.ARRAY;
		default:
			return null;
		}
	}

	@Override
	public Object java() {
		int field = 0;
		if (idx == -1)
			return field < 1 || field > size ? null : new RelationArray(parent, field - 1);
		switch (field) {
		case 3:
			return getStarted();
		case 4:
			return getStopped();
		default:
			return null;
		}
	}

	@Override
	public boolean java(Object value) {
		int field = 0;
		switch (field) {
		case 3:
			if (value instanceof Integer)
				setStarted((Integer) value);
			return value instanceof Integer;
		case 4:
			if (value instanceof Integer)
				setStopped((Integer) value);
			return value instanceof Integer;
		default:
			return false;
		}
	}

	@Override
	public RecordInterface next() {
		return null;
	}

	@Override
	public RelationArray copy() {
		return new RelationArray(parent, idx);
	}
}