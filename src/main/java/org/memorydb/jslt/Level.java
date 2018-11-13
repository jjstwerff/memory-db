package org.memorydb.jslt;

import java.io.IOException;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.structure.FieldData;
import org.memorydb.structure.RecordData;
import org.memorydb.structure.RecordInterface;
import org.memorydb.structure.Store;

/**
 * Automatically generated record class for table Level
 */
@RecordData(
	name = "Level",
	keyFields = {"level"})
public class Level implements Operator {
	/* package private */ Store store;
	protected int rec;
	/* package private */ static final int RECORD_SIZE = 47;

	public Level(Store store) {
		this.store = store;
		this.rec = 0;
	}

	public Level(Store store, int rec) {
		rec = store.correct(rec);
		this.store = store;
		this.rec = rec;
	}

	@Override
	public int getRec() {
		return rec;
	}

	@Override
	public void setRec(int rec) {
		assert store.validate(rec);
		this.rec = rec;
	}

	@Override
	public Store getStore() {
		return store;
	}

	@Override
	public int operatorPosition() {
		return 29;
	}

	@Override
	public ChangeLevel change() {
		return new ChangeLevel(this);
	}

	@FieldData(
		name = "level",
		type = "INTEGER",
		mandatory = false
	)
	public int getLevel() {
		return rec == 0 ? Integer.MIN_VALUE : store.getInt(rec, 4);
	}

	@FieldData(
		name = "order",
		type = "ARRAY",
		related = OrderArray.class,
		mandatory = false
	)
	public OrderArray getOrder() {
		return new OrderArray(this, -1);
	}

	public OrderArray getOrder(int index) {
		return new OrderArray(this, index);
	}

	public OrderArray addOrder() {
		return getOrder().add();
	}

	@FieldData(
		name = "slice",
		type = "ARRAY",
		related = SliceArray.class,
		mandatory = false
	)
	public SliceArray getSlice() {
		return new SliceArray(this, -1);
	}

	public SliceArray getSlice(int index) {
		return new SliceArray(this, index);
	}

	public SliceArray addSlice() {
		return getSlice().add();
	}

	@Override
	@FieldData(
		name = "upRecord",
		type = "RELATION",
		related = Listener.class,
		mandatory = false
	)
	public Listener getUpRecord() {
		return new Listener(store, rec == 0 ? 0 : store.getInt(rec, 25));
	}

	@Override
	public void output(Write write, int iterate) throws IOException {
		if (rec == 0 || iterate <= 0)
			return;
		write.field("level", getLevel(), true);
		OrderArray fldOrder = getOrder();
		if (fldOrder != null) {
			write.sub("order", false);
			for (OrderArray sub : fldOrder)
				sub.output(write, iterate);
			write.endSub();
		}
		SliceArray fldSlice = getSlice();
		if (fldSlice != null) {
			write.sub("slice", false);
			for (SliceArray sub : fldSlice)
				sub.output(write, iterate);
			write.endSub();
		}
		outputOperator(write, iterate, false);
		write.endRecord();
	}

	@Override
	public String keys() throws IOException {
		StringBuilder res = new StringBuilder();
		if (rec == 0)
			return "";
		res.append("Listener").append("{").append(getUpRecord().keys()).append("}");
		res.append(", ");
		res.append("Level").append("=").append(getLevel());
		return res.toString();
	}

	@Override
	public String toString() {
		Write write = new Write(new StringBuilder());
		try {
			output(write, 4);
		} catch (IOException e) {
			return "";
		}
		return write.toString();
	}

	public Level parse(Parser parser, Listener parent) {
		while (parser.getSub()) {
			int level = parser.getInt("level");
			int nextRec = parent.new IndexLevels(this, level).search();
			if (parser.isDelete(nextRec)) {
				try (ChangeLevel record = new ChangeLevel(this)) {
					store.free(record.getRec());
					record.setRec(0);
				}
				continue;
			}
			if (nextRec == 0) {
				try (ChangeLevel record = new ChangeLevel(parent, 0)) {
					record.setLevel(level);
					record.parseFields(parser);
					rec = record.rec;
				}
			} else {
				rec = nextRec;
				try (ChangeLevel record = new ChangeLevel(this)) {
					record.parseFields(parser);
				}
			}
		}
		return this;
	}

	@Override
	public boolean parseKey(Parser parser) {
		Listener parent = getUpRecord();
		parser.getRelation("Listener", (int recNr) -> {
			parent.setRec(recNr);
			parent.parseKey(parser);
			return true;
		}, getRec());
		int level = parser.getInt("level");
		int nextRec = parent.new IndexLevels(this, level).search();
		parser.finishRelation();
		rec = nextRec;
		return nextRec != 0;
	}

	@Override
	public Object get(int field) {
		if (field >= 4 && field <= 34)
			return Operator.super.getOperator(field - 4);
		switch (field) {
		case 1:
			return getLevel();
		default:
			return null;
		}
	}

	@Override
	public Iterable<? extends RecordInterface> iterate(int field, Object... key) {
		if (field >= 4 && field <= 34)
			return Operator.super.iterateOperator(field - 4);
		switch (field) {
		case 2:
			return getOrder();
		case 3:
			return getSlice();
		default:
			return null;
		}
	}

	@Override
	public FieldType type(int field) {
		if (field >= 4 && field <= 34)
			return Operator.super.typeOperator(field - 4);
		switch (field) {
		case 1:
			return FieldType.INTEGER;
		case 2:
			return FieldType.ARRAY;
		case 3:
			return FieldType.ARRAY;
		default:
			return null;
		}
	}

	@Override
	public String name(int field) {
		if (field >= 4 && field <= 34)
			return Operator.super.nameOperator(field - 4);
		switch (field) {
		case 1:
			return "level";
		case 2:
			return "order";
		case 3:
			return "slice";
		default:
			return null;
		}
	}

	@Override
	public boolean exists() {
		return getRec() != 0;
	}
}
