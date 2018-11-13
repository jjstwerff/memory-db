package org.memorydb.jslt;

import java.io.IOException;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.structure.FieldData;
import org.memorydb.structure.IndexOperation;
import org.memorydb.structure.Key;
import org.memorydb.structure.RecordData;
import org.memorydb.structure.RecordInterface;
import org.memorydb.structure.RedBlackTree;
import org.memorydb.structure.Store;
import org.memorydb.structure.TreeIndex;

/**
 * Automatically generated record class for table Listener
 */
@RecordData(
	name = "Listener",
	keyFields = {"nr"})
public class Listener implements Operator {
	/* package private */ Store store;
	protected int rec;
	/* package private */ static final int RECORD_SIZE = 43;

	public Listener(Store store) {
		this.store = store;
		this.rec = 0;
	}

	public Listener(Store store, int rec) {
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
		return 25;
	}

	@Override
	public ChangeListener change() {
		return new ChangeListener(this);
	}

	@FieldData(
		name = "nr",
		type = "INTEGER",
		mandatory = false
	)
	public int getNr() {
		return rec == 0 ? Integer.MIN_VALUE : store.getInt(rec, 4);
	}

	@FieldData(
		name = "levels",
		type = "SET",
		keyNames = {"level"},
		keyTypes = {"INTEGER"},
		related = Level.class,
		mandatory = false
	)
	public IndexLevels getLevels() {
		return new IndexLevels(new Level(store));
	}

	public Level getLevels(int key1) {
		Level resultRec = new Level(store);
		IndexLevels idx = new IndexLevels(resultRec, key1);
		int res = idx.search();
		if (res == 0)
			return resultRec;
		return new Level(store, res);
	}

	public ChangeLevel addLevels() {
		return new ChangeLevel(this, 0);
	}

	/* package private */ class IndexLevels extends TreeIndex<Level> {

		public IndexLevels(Level record) {
			super(record, null, 128, 17);
		}

		public IndexLevels(Level record, int key1) {
			super(record, new Key() {
				@Override
				public int compareTo(int recNr) {
					if (recNr < 0)
						return -1;
					assert store.validate(recNr);
					record.setRec(recNr);
					int o = 0;
					o = RedBlackTree.compare(key1, record.getLevel());
					return o;
				}

				@Override
				public IndexOperation oper() {
					return IndexOperation.EQ;
				}
			}, 128, 17);
		}

		@Override
		protected int readTop() {
			return store.getInt(rec, 8);
		}

		@Override
		protected void changeTop(int value) {
			store.setInt(rec, 8, value);
		}

		@Override
		protected int compareTo(int a, int b) {
			Level recA = new Level(store, a);
			Level recB = new Level(store, b);
			int o = 0;
			o = compare(recA.getLevel(), recB.getLevel());
			return o;
		}
	}

	@Override
	@FieldData(
		name = "upRecord",
		type = "RELATION",
		related = Source.class,
		mandatory = false
	)
	public Source getUpRecord() {
		return new Source(store, rec == 0 ? 0 : store.getInt(rec, 21));
	}

	@Override
	public void output(Write write, int iterate) throws IOException {
		if (rec == 0 || iterate <= 0)
			return;
		write.field("nr", getNr(), true);
		IndexLevels fldLevels = getLevels();
		if (fldLevels != null) {
			write.sub("levels", false);
			for (Level sub : fldLevels)
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
		res.append("Source").append("{").append(getUpRecord().keys()).append("}");
		res.append(", ");
		res.append("Nr").append("=").append(getNr());
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

	public Listener parse(Parser parser, Source parent) {
		while (parser.getSub()) {
			int nr = parser.getInt("nr");
			int nextRec = parent.new IndexListeners(this, nr).search();
			if (parser.isDelete(nextRec)) {
				try (ChangeListener record = new ChangeListener(this)) {
					store.free(record.getRec());
					record.setRec(0);
				}
				continue;
			}
			if (nextRec == 0) {
				try (ChangeListener record = new ChangeListener(parent, 0)) {
					record.setNr(nr);
					record.parseFields(parser);
					rec = record.rec;
				}
			} else {
				rec = nextRec;
				try (ChangeListener record = new ChangeListener(this)) {
					record.parseFields(parser);
				}
			}
		}
		return this;
	}

	@Override
	public boolean parseKey(Parser parser) {
		Source parent = getUpRecord();
		int nr = parser.getInt("nr");
		int nextRec = parent.new IndexListeners(this, nr).search();
		parser.finishRelation();
		rec = nextRec;
		return nextRec != 0;
	}

	@Override
	public Object get(int field) {
		if (field >= 3 && field <= 33)
			return Operator.super.getOperator(field - 3);
		switch (field) {
		case 1:
			return getNr();
		default:
			return null;
		}
	}

	@Override
	public Iterable<? extends RecordInterface> iterate(int field, Object... key) {
		if (field >= 3 && field <= 33)
			return Operator.super.iterateOperator(field - 3);
		switch (field) {
		case 2:
			if (key.length > 0)
				return new IndexLevels(new Level(store), (int) key[0]);
			return getLevels();
		default:
			return null;
		}
	}

	@Override
	public FieldType type(int field) {
		if (field >= 3 && field <= 33)
			return Operator.super.typeOperator(field - 3);
		switch (field) {
		case 1:
			return FieldType.INTEGER;
		case 2:
			return FieldType.ARRAY;
		default:
			return null;
		}
	}

	@Override
	public String name(int field) {
		if (field >= 3 && field <= 33)
			return Operator.super.nameOperator(field - 3);
		switch (field) {
		case 1:
			return "nr";
		case 2:
			return "levels";
		default:
			return null;
		}
	}

	@Override
	public boolean exists() {
		return getRec() != 0;
	}
}
