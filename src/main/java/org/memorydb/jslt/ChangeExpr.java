package org.memorydb.jslt;

import org.memorydb.file.Parser;
import org.memorydb.structure.Store;
import org.memorydb.structure.ChangeInterface;

/**
 * Automatically generated record class for table Expr
 */
public class ChangeExpr extends Expr implements ChangeOperator {
	public ChangeExpr(Store store, int rec) {
		super(store, rec == 0 ? store.allocate(Expr.RECORD_SIZE) : rec);
		if (rec == 0) {
			defaultOperator();
		} else {
		}
	}

	public ChangeExpr(Expr current) {
		super(current.store(), current.rec());
	}

	/* package private */ void parseFields(Parser parser) {
		parseOperator(parser);
	}

	@Override
	public boolean java(Object value) {
		int field = 0;
		if (field > 0 && field <= 28)
			return ChangeOperator.super.setOperator(field - 0, value);
		switch (field) {
		default:
			return false;
		}
	}

	@Override
	public ChangeInterface add() {
		int field = 0;
		if (field > 0 && field <= 28)
			return ChangeOperator.super.addOperator(field - 0);
		switch (field) {
		default:
			return null;
		}
	}

	@Override
	public ChangeExpr copy(int newRec) {
		assert store.validate(newRec);
		return new ChangeExpr(store, newRec);
	}
}
