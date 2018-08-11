package org.memorydb.meta;

import org.memorydb.file.Parser;
import org.memorydb.structure.Store;
import org.memorydb.structure.ChangeInterface;

/**
 * Automatically generated record class for table Project
 */
public class ChangeProject extends Project implements ChangeInterface {
	public ChangeProject(Store store) {
		super(store, store.allocate(Project.RECORD_SIZE));
		setName(null);
		setPack(null);
		store.setInt(rec, 12, 0); // SET indexes
		store.setInt(rec, 16, 0); // SET records
		setDir(null);
	}

	public ChangeProject(Project current) {
		super(current.getStore(), current.getRec());
		new IndexMeta().remove(getRec());
	}

	public void setName(String value) {
		store.setInt(rec, 4, store.putString(value));
	}

	public void setPack(String value) {
		store.setInt(rec, 8, store.putString(value));
	}

	public void setDir(String value) {
		store.setInt(rec, 20, store.putString(value));
	}

	/* package private */ void parseFields(Parser parser) {
		if (parser.hasField("pack")) {
			setPack(parser.getString("pack"));
		}
		if (parser.hasSub("indexes")) {
			new Index(store).parse(parser, this);
		}
		if (parser.hasSub("records")) {
			new Record(store).parse(parser, this);
		}
		if (parser.hasField("dir")) {
			setDir(parser.getString("dir"));
		}
	}

	@Override
	public void close() {
		new IndexMeta().insert(getRec());
	}

	@Override
	public boolean set(int field, Object value) {
		switch (field) {
		case 1:
			if (value instanceof String)
				setName((String) value);
			return value instanceof String;
		case 2:
			if (value instanceof String)
				setPack((String) value);
			return value instanceof String;
		case 5:
			if (value instanceof String)
				setDir((String) value);
			return value instanceof String;
		default:
			return false;
		}
	}

	@Override
	public ChangeInterface add(int field) {
		switch (field) {
		case 3:
			return addIndexes();
		case 4:
			return addRecords();
		default:
			return null;
		}
	}
}