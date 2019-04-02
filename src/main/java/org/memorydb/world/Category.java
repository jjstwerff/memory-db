package org.memorydb.world;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.structure.FieldData;
import org.memorydb.structure.MemoryRecord;
import org.memorydb.structure.RecordData;
import org.memorydb.structure.RecordInterface;
import org.memorydb.structure.Store;
import java.util.HashMap;
import java.util.Map;

/**
 * Automatically generated record class for table Category
 */
@RecordData(name = "Category")
public class Category implements MemoryRecord, RecordInterface {
	/* package private */ final Store store;
	protected final int rec;
	/* package private */ static final int RECORD_SIZE = 25;

	public Category(Store store) {
		this.store = store;
		this.rec = 0;
	}

	public Category(Store store, int rec) {
		rec = store.correct(rec);
		this.store = store;
		this.rec = rec;
	}

	@Override
	public int rec() {
		return rec;
	}

	@Override
	public Category copy(int newRec) {
		assert store.validate(rec);
		return new Category(store, newRec);
	}

	@Override
	public Store store() {
		return store;
	}

	@Override
	public ChangeCategory change() {
		return new ChangeCategory(this);
	}

	public enum Type {
		RACE, ORGANISATION, PROJECT, JOB, PERSON, BUILDING, ITEM, COUNTRY, TOWN, GROUP, ANIMAL, PLANT, VEHICLE, BLUEPRINT, PAPERS, POWER, ACTION, TERRAIN, NODE;

		private static Map<String, Type> map = new HashMap<>();

		static {
			for (Type tp : Type.values()) {
				map.put(tp.toString(), tp);
			}
		}

		public static Type get(String val) {
			return map.containsKey(val) ? map.get(val) : null;
		}
	}

	@FieldData(name = "type", type = "ENUMERATE", enumerate = { "RACE", "ORGANISATION", "PROJECT", "JOB", "PERSON", "BUILDING", "ITEM", "COUNTRY", "TOWN", "GROUP", "ANIMAL", "PLANT", "VEHICLE", "BLUEPRINT", "PAPERS", "POWER", "ACTION", "TERRAIN", "NODE" }, mandatory = false)
	public Category.Type getType() {
		int data = rec == 0 ? 0 : store.getByte(rec, 4) & 63;
		if (data <= 0 || data > Type.values().length)
			return null;
		return Type.values()[data - 1];
	}

	@FieldData(name = "name", type = "STRING", mandatory = false)
	public String getName() {
		return rec == 0 ? null : store.getString(store.getInt(rec, 5));
	}

	@FieldData(name = "specials", type = "ARRAY", related = SpecialsArray.class, mandatory = false)
	public SpecialsArray getSpecials() {
		return new SpecialsArray(this, -1);
	}

	public SpecialsArray getSpecials(int index) {
		return new SpecialsArray(this, index);
	}

	public SpecialsArray addSpecials() {
		return getSpecials().add();
	}

	@FieldData(name = "dependency", type = "ARRAY", related = DependencyArray.class, mandatory = false)
	public DependencyArray getDependency() {
		return new DependencyArray(this, -1);
	}

	public DependencyArray getDependency(int index) {
		return new DependencyArray(this, index);
	}

	public DependencyArray addDependency() {
		return getDependency().add();
	}

	@FieldData(name = "effect", type = "ARRAY", related = EffectArray.class, mandatory = false)
	public EffectArray getEffect() {
		return new EffectArray(this, -1);
	}

	public EffectArray getEffect(int index) {
		return new EffectArray(this, index);
	}

	public EffectArray addEffect() {
		return getEffect().add();
	}

	@FieldData(name = "description", type = "STRING", mandatory = false)
	public String getDescription() {
		return rec == 0 ? null : store.getString(store.getInt(rec, 21));
	}

	@Override
	public void output(Write write, int iterate) {
		if (rec == 0 || iterate <= 0)
			return;
		write.field("type", getType());
		write.field("name", getName());
		SpecialsArray fldSpecials = getSpecials();
		if (fldSpecials != null) {
			write.sub("specials");
			for (SpecialsArray sub : fldSpecials)
				sub.output(write, iterate);
			write.endSub();
		}
		DependencyArray fldDependency = getDependency();
		if (fldDependency != null) {
			write.sub("dependency");
			for (DependencyArray sub : fldDependency)
				sub.output(write, iterate);
			write.endSub();
		}
		EffectArray fldEffect = getEffect();
		if (fldEffect != null) {
			write.sub("effect");
			for (EffectArray sub : fldEffect)
				sub.output(write, iterate);
			write.endSub();
		}
		write.field("description", getDescription());
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

	public Category parse(Parser parser) {
		while (parser.getSub()) {
			int nextRec = 0;
			if (parser.isDelete(nextRec)) {
				try (ChangeCategory record = new ChangeCategory(this)) {
					store.free(record.rec());
					record.rec(0);
				}
				continue;
			}
			if (nextRec == 0) {
				try (ChangeCategory record = new ChangeCategory(store)) {

					record.parseFields(parser);
					rec = record.rec;
				}
			} else {
				rec = nextRec;
				try (ChangeCategory record = new ChangeCategory(this)) {
					record.parseFields(parser);
				}
			}
		}
		return this;
	}

	public boolean parseKey(Parser parser) {
		int nextRec = 0;
		parser.finishRelation();
		if (nextRec != 0)
			rec = nextRec;
		return nextRec != 0;
	}

	@Override
	public Object java() {
		int field = 0;
		switch (field) {
		case 1:
			return getType();
		case 2:
			return getName();
		case 6:
			return getDescription();
		default:
			return null;
		}
	}

	public Iterable<? extends RecordInterface> iterate(int field, Object... key) {
		switch (field) {
		case 3:
			return getSpecials();
		case 4:
			return getDependency();
		case 5:
			return getEffect();
		default:
			return null;
		}
	}

	@Override
	public FieldType type() {
		int field = 0;
		switch (field) {
		case 1:
			return FieldType.STRING;
		case 2:
			return FieldType.STRING;
		case 3:
			return FieldType.ARRAY;
		case 4:
			return FieldType.ARRAY;
		case 5:
			return FieldType.ARRAY;
		case 6:
			return FieldType.STRING;
		default:
			return null;
		}
	}

	@Override
	public String name() {
		int field = 0;
		switch (field) {
		case 1:
			return "type";
		case 2:
			return "name";
		case 3:
			return "specials";
		case 4:
			return "dependency";
		case 5:
			return "effect";
		case 6:
			return "description";
		default:
			return null;
		}
	}

	@Override
	public Category next() {
		return null;
	}

	@Override
	public Category copy() {
		return new Category(store, rec);
	}
}
