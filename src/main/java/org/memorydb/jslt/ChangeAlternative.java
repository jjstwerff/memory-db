package org.memorydb.jslt;

import org.memorydb.file.Parser;
import org.memorydb.structure.ChangeInterface;

/**
 * Automatically generated record class for table Alternative
 */
public class ChangeAlternative extends Alternative implements ChangeInterface {
	/* package private */ ChangeAlternative(Macro parent, int rec) {
		super(parent.getStore(), rec);
		if (rec == 0) {
			setRec(getStore().allocate(Alternative.RECORD_SIZE));
		}
		setNr(0);
		store.setInt(getRec(), 8, 0); // ARRAY parameters
		setIf(null);
		store.setInt(getRec(), 16, 0); // ARRAY code
		setUpRecord(parent);
		if (rec != 0) {
			getUpRecord().new IndexAlternatives(this).remove(rec);
		}
	}

	/* package private */ ChangeAlternative(Alternative current) {
		super(current.store, current.rec);
		if (rec != 0) {
			getUpRecord().new IndexAlternatives(this).remove(rec);
		}
	}

	public void setNr(int value) {
		store.setInt(rec, 4, value);
	}

	public void moveParameters(ChangeAlternative other) {
		getStore().setInt(getRec(), 8, getStore().getInt(other.getRec(), 8));
		getStore().setInt(other.getRec(), 8, 0);
	}

	public void setIf(Expr value) {
		store.setInt(rec, 12, value == null ? 0 : value.getRec());
	}

	public void moveCode(ChangeAlternative other) {
		getStore().setInt(getRec(), 16, getStore().getInt(other.getRec(), 16));
		getStore().setInt(other.getRec(), 16, 0);
	}

	public void setUpRecord(Macro value) {
		store.setInt(rec, 29, value == null ? 0 : value.getRec());
	}

	/* package private */ void parseFields(Parser parser) {
		if (parser.hasSub("parameters")) {
			try (ParametersArray sub = new ParametersArray(this, -1)) {
				while (parser.getSub()) {
					sub.add();
					sub.parse(parser);
				}
			}
		}
		if (parser.hasSub("if")) {
			setIf(new Expr(store).parse(parser));
		}
		if (parser.hasSub("code")) {
			try (CodeArray sub = new CodeArray(this, -1)) {
				while (parser.getSub()) {
					sub.add();
					sub.parse(parser);
				}
			}
		}
	}

	@Override
	public void close() {
		getUpRecord().new IndexAlternatives(this).insert(getRec());
	}

	@Override
	public boolean set(int field, Object value) {
		switch (field) {
		case 1:
			if (value instanceof Integer)
				setNr((Integer) value);
			return value instanceof Integer;
		case 3:
			if (value instanceof Expr)
				setIf((Expr) value);
			return value instanceof Expr;
		default:
			return false;
		}
	}

	@Override
	public ChangeInterface add(int field) {
		switch (field) {
		case 2:
			return addParameters();
		case 4:
			return addCode();
		default:
			return null;
		}
	}
}