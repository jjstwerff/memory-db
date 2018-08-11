package org.memorydb.jslt;

import org.memorydb.file.Parser;
import org.memorydb.structure.Store;
import org.memorydb.structure.ChangeInterface;

/**
 * Automatically generated record class for table Macro
 */
public class ChangeMacro extends Macro implements ChangeInterface {
	public ChangeMacro(Store store) {
		super(store, store.allocate(Macro.RECORD_SIZE));
		setName(null);
		store.setInt(rec, 8, 0); // SET alternatives
	}

	public ChangeMacro(Macro current) {
		super(current.getStore(), current.getRec());
		new IndexMacros().remove(getRec());
	}

	public void setName(String value) {
		store.setInt(rec, 4, store.putString(value));
	}

	/* package private */ void parseFields(Parser parser) {
		if (parser.hasSub("alternatives")) {
			new Alternative(store).parse(parser, this);
		}
	}

	@Override
	public void close() {
		new IndexMacros().insert(getRec());
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
		case 2:
			return addAlternatives();
		default:
			return null;
		}
	}
}