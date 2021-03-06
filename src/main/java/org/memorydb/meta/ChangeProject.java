package org.memorydb.meta;

import org.memorydb.file.Parser;
import org.memorydb.structure.Store;
import org.memorydb.structure.ChangeInterface;

/**
 * Automatically generated record class for table Project
 */
public class ChangeProject extends Project implements AutoCloseable, ChangeInterface {
	public ChangeProject(Store store, int rec) {
		super(store, rec == 0 ? store.allocate(Project.RECORD_SIZE) : rec);
		if (rec == 0) {
			store.setInt(rec(), 4, 0); // SET records
			setMain(null);
			setPack(null);
			setDirectory(null);
		} else {
			new IndexMeta(store).remove(rec());
		}
	}

	public ChangeProject(Project current) {
		super(current.store(), current.rec());
		new IndexMeta(store).remove(rec());
	}

	public void setMain(Record value) {
		store.setInt(rec, 8, value == null ? 0 : value.rec());
	}

	public void setPack(String value) {
		store.setInt(rec, 12, store.putString(value));
	}

	public void setDirectory(String value) {
		store.setInt(rec, 16, store.putString(value));
	}

	/* package private */ void parseFields(Parser parser) {
		if (parser.hasSub("records")) {
			Record.parse(parser, this);
		}
		if (parser.hasField("main")) {
			parser.getRelation("main", (recNr, idx) -> {
				Record relRec = Record.parseKey(parser, this);
				try (ChangeProject old = this.copy(recNr)) {
					old.setMain(relRec);
				}
				return relRec != null;
			}, rec());
		}
		if (parser.hasField("directory")) {
			setDirectory(parser.getString("directory"));
		}
	}

	@Override
	public void close() {
		new IndexMeta(store).insert(rec());
	}

	@Override
	public boolean java(Object value) {
		int field = 0;
		switch (field) {
		case 2:
			if (value instanceof Record)
				setMain((Record) value);
			return value instanceof Record;
		case 3:
			if (value instanceof String)
				setPack((String) value);
			return value instanceof String;
		case 4:
			if (value instanceof String)
				setDirectory((String) value);
			return value instanceof String;
		default:
			return false;
		}
	}

	@Override
	public ChangeInterface add() {
		int field = 0;
		switch (field) {
		case 1:
			return addRecords();
		default:
			return null;
		}
	}

	@Override
	public ChangeProject copy(int newRec) {
		assert store.validate(newRec);
		return new ChangeProject(store, newRec);
	}
}
