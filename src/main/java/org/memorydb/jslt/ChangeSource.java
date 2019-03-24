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
		super(current.getStore(), current.getRec());
		new IndexSources().remove(getRec());
	}

	public void setName(String value) {
		store.setInt(rec, 4, store.putString(value));
	}

	/* package private */ void parseFields(@SuppressWarnings("unused") Parser parser) {
		// empty
	}

	@Override
	public void close() {
		new IndexSources().insert(getRec());
	}

	@Override
	public boolean set(int field, Object value) {
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
		switch (field) {
		default:
			return null;
		}
	}
}