package org.memorydb.jslt;

import org.memorydb.file.Parser;
import org.memorydb.structure.Store;
import org.memorydb.structure.ChangeInterface;

/**
 * Automatically generated record class for table Source
 */
public class ChangeSource extends Source implements ChangeInterface {
	public ChangeSource(Store store) {
		super(store, store.allocate(Source.RECORD_SIZE));
		setName(null);
	}

	public ChangeSource(Source current) {
		super(current.store(), current.rec());
		new IndexSources().remove(rec());
	}

	public void setName(String value) {
		store.setInt(rec, 4, store.putString(value));
	}

	/* package private */ void parseFields(@SuppressWarnings("unused") Parser parser) {
		// empty
	}

	@Override
	public void close() {
		new IndexSources().insert(rec());
	}

	@Override
	public boolean java(Object value) {
		int field = 0;
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
	public ChangeInterface add() {
		int field = 0;
		switch (field) {
		default:
			return null;
		}
	}
}