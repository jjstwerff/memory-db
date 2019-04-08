package org.memorydb.jslt;

import org.memorydb.file.Parser;
import org.memorydb.structure.ChangeInterface;
import org.memorydb.handler.MutationException;

/**
 * Automatically generated record class for table Alternative
 */
public class ChangeAlternative extends Alternative implements AutoCloseable, ChangeInterface {
	/* package private */ ChangeAlternative(Macro parent, int rec) {
		super(parent.store(), rec == 0 ? parent.store().allocate(Alternative.RECORD_SIZE) : rec);
		if (rec == 0) {
			setNr(Integer.MIN_VALUE);
			store.setInt(rec(), 8, 0); // ARRAY parameters
			setAnyParm(false);
			setIf(null);
			store.setInt(rec(), 17, 0); // ARRAY code
			up(parent);
		} else {
			up().new IndexAlternatives().remove(rec);
		}
	}

	/* package private */ ChangeAlternative(Alternative current) {
		super(current.store, current.rec);
		if (rec != 0) {
			up().new IndexAlternatives().remove(rec);
		}
	}

	public void setNr(int value) {
		store.setInt(rec, 4, value);
	}

	public void moveParameters(ChangeAlternative other) {
		store().setInt(rec(), 8, store().getInt(other.rec(), 8));
		store().setInt(other.rec(), 8, 0);
	}

	public void setAnyParm(boolean value) {
		store.setByte(rec, 12, (store.getByte(rec, 12) & 254) + (value ? 1 : 0));
	}

	public void setIf(Expr value) {
		store.setInt(rec, 13, value == null ? 0 : value.rec());
	}

	public void moveCode(ChangeAlternative other) {
		store().setInt(rec(), 17, store().getInt(other.rec(), 17));
		store().setInt(other.rec(), 17, 0);
	}

	private void up(Macro value) {
		store.setInt(rec, 30, value == null ? 0 : value.rec());
		store.setInt(rec, 35, value == null ? 0 : value.index());
	}

	/* package private */ void parseFields(Parser parser) {
		if (parser.hasSub("parameters")) {
			ParametersArray sub = new ParametersArray(this, -1);
			while (parser.getSub())
				sub.add().parse(parser);
		}
		if (parser.hasField("anyParm")) {
			Boolean valueAnyParm = parser.getBoolean("anyParm");
			if (valueAnyParm == null)
				throw new MutationException("Mandatory 'anyParm' field");
			setAnyParm(valueAnyParm);
		}
		if (parser.hasSub("if")) {
			setIf(Expr.parse(parser, store()));
		}
		if (parser.hasSub("code")) {
			CodeArray sub = new CodeArray(this, -1);
			while (parser.getSub())
				sub.add().parse(parser);
		}
	}

	@Override
	public void close() {
		up().new IndexAlternatives().insert(rec());
	}

	@Override
	public boolean java(Object value) {
		int field = 0;
		switch (field) {
		case 1:
			if (value instanceof Integer)
				setNr((Integer) value);
			return value instanceof Integer;
		case 3:
			if (value instanceof Boolean)
				setAnyParm((Boolean) value);
			return value instanceof Boolean;
		case 4:
			if (value instanceof Expr)
				setIf((Expr) value);
			return value instanceof Expr;
		default:
			return false;
		}
	}

	@Override
	public ChangeInterface add() {
		int field = 0;
		switch (field) {
		case 2:
			return addParameters();
		case 5:
			return addCode();
		default:
			return null;
		}
	}

	@Override
	public ChangeAlternative copy(int newRec) {
		assert store.validate(newRec);
		return new ChangeAlternative(up(), newRec);
	}
}
