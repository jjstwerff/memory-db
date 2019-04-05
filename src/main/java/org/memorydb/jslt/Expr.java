package org.memorydb.jslt;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.structure.RecordData;
import org.memorydb.structure.Store;

/**
 * Automatically generated record class for table Expr
 */
@RecordData(name = "Expr")
public class Expr implements Operator {
	/* package private */ final Store store;
	protected final int rec;
	private final int field;
	/* package private */ static final int RECORD_SIZE = 22;

	public Expr(Store store, int rec) {
		rec = store.correct(rec);
		this.store = store;
		this.rec = rec;
		this.field = 0;
	}

	public Expr(Store store, int rec, int field) {
		rec = store.correct(rec);
		this.store = store;
		this.rec = rec;
		this.field = field;
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

	public static Expr parse(Parser parser, Store store) {
		Expr rec = null;
		while (parser.getSub()) {
			rec = parseKey(parser, store);
			if (parser.isDelete()) {
				if (rec != null) {
					ChangeExpr record = new ChangeExpr(rec);
					store.free(record.rec());
				}
				continue;
			}
			if (rec == null) {
				ChangeExpr record = new ChangeExpr(store, 0);

				record.parseFields(parser);
				return record;
			} else {
				ChangeExpr record = new ChangeExpr(rec);
				record.parseFields(parser);
			}
		}
		return rec;
	}

	public static Expr parseKey(Parser parser, Store store) {
		int nextRec = 0;
		parser.finishRelation();
		return nextRec <= 0 ? null : new Expr(store, nextRec);
	}

	@Override
	public Object java() {
		return Operator.super.getOperator(field);
	}

	@Override
	public FieldType type() {
		return Operator.super.typeOperator(field);
	}

	@Override
	public String name() {
		return Operator.super.nameOperator(field);
	}

	@Override
	public Expr start() {
		return new Expr(store, rec, 1);
	}

	@Override
	public Expr next() {
		return field >= 30 ? null : new Expr(store, rec, field + 1);
	}

	@Override
	public Expr copy() {
		return new Expr(store, rec, field);
	}
}
