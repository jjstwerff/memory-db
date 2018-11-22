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
		store.setInt(rec, 4, 0); // SET records
		setMain(null);
		setPack(null);
		setDirectory(null);
	}

	public ChangeProject(Project current) {
		super(current.getStore(), current.getRec());
		new IndexMeta().remove(getRec());
	}

	public void setMain(Record value) {
		store.setInt(rec, 8, value == null ? 0 : value.getRec());
	}

	public void setPack(String value) {
		store.setInt(rec, 12, store.putString(value));
	}

	public void setDirectory(String value) {
		store.setInt(rec, 16, store.putString(value));
	}

	/* package private */ void parseFields(Parser parser) {
		if (parser.hasSub("records")) {
			new Record(store).parse(parser, this);
		}
		if (parser.hasField("main")) {
			parser.getRelation("main", (int recNr) -> {
				int nr = Record.parseKey(parser, this);
				if (nr == 0)
					return false;
				setRec(recNr);
				setMain(new Record(store, nr));
				return true;
			}, getRec());
		}
		if (parser.hasField("directory")) {
			setDirectory(parser.getString("directory"));
		}
	}

	@Override
	public void close() {
		new IndexMeta().insert(getRec());
	}

	@Override
	public boolean set(int field, Object value) {
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
	public ChangeInterface add(int field) {
		switch (field) {
		case 1:
			return addRecords();
		default:
			return null;
		}
	}
}