package org.memorydb.jslt;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.structure.RecordData;
import org.memorydb.structure.RecordInterface;
import org.memorydb.structure.Store;

/**
 * Automatically generated record class for table Expr
 */
@RecordData(name = "Expr")
public class Expr implements Operator {
	/* package private */ final Store store;
	protected final int rec;
	/* package private */ static final int RECORD_SIZE = 22;

	public Expr(Store store) {
		this.store = store;
		this.rec = 0;
	}

	public Expr(Store store, int rec) {
		rec = store.correct(rec);
		this.store = store;
		this.rec = rec;
	}

	@Override
	public int rec() {
		return rec;
	}

	@Override
	public Expr copy(int newRec) {
		assert store.validate(newRec);
		return new Expr(store, newRec);
	}

	@Override
	public Store store() {
		return store;
	}

	@Override
	public int operatorPosition() {
		return 4;
	}

	@Override
	public ChangeExpr change() {
		return new ChangeExpr(this);
	}

	@Override
	public void output(Write write, int iterate) {
		if (rec == 0 || iterate <= 0)
			return;
		outputOperator(write, iterate);
		write.endRecord();
	}

	@Override
	public String keys() {
		StringBuilder res = new StringBuilder();
		if (rec == 0)
			return "";
		return res.toString();
	}

	@Override
	public String toString() {
		Write write = new Write(new StringBuilder());
		output(write, 4);
		return write.toString();
	}

	public static void parse(Parser parser, Store store) {
		while (parser.getSub()) {
			int nextRec = 0;
			if (parser.isDelete(nextRec)) {
				try (ChangeExpr record = new ChangeExpr(store, nextRec)) {
					store.free(record.rec());
				}
				continue;
			}
			if (nextRec == 0) {
				try (ChangeExpr record = new ChangeExpr(store, 0)) {

					record.parseFields(parser);
				}
			} else {
				try (ChangeExpr record = new ChangeExpr(store, nextRec)) {
					record.parseFields(parser);
				}
			}
		}
	}

	@Override
	public Expr parseKey(Parser parser) {
		int nextRec = 0;
		parser.finishRelation();
		return nextRec <= 0 ? null : new Expr(store, nextRec);
	}

	@Override
	public Object java() {
		int field = 0;
		return Operator.super.getOperator(field);
	}

	@Override
	public FieldType type() {
		int field = 0;
		return Operator.super.typeOperator(field);
	}

	@Override
	public String name() {
		int field = 0;
		return Operator.super.nameOperator(field);
	}

	@Override
	public Expr next() {
		return null;
	}

	@Override
	public Expr copy() {
		return new Expr(store, rec);
	}
}
