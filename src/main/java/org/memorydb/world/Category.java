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
	private final int field;
	/* package private */ static final int RECORD_SIZE = 25;

	public Category(Store store, int rec) {
		rec = store.correct(rec);
		this.store = store;
		this.rec = rec;
		this.field = 0;
	}

	public Category(Store store, int rec, int field) {
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
	public Category copy(int newRec) {
		assert store.validate(newRec);
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

	@FieldData(name = "default_specials", type = "ARRAY", related = Default_specialsArray.class, mandatory = false)
	public Default_specialsArray getDefault_specials() {
		return new Default_specialsArray(this, -1);
	}

	public Default_specialsArray getDefault_specials(int index) {
		return new Default_specialsArray(this, index);
	}

	public Default_specialsArray addDefault_specials() {
		return getDefault_specials().add();
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
		Default_specialsArray fldDefault_specials = getDefault_specials();
		if (fldDefault_specials != null) {
			write.sub("default_specials");
			for (Default_specialsArray sub : fldDefault_specials)
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

	public static Category parse(Parser parser, Store store) {
		Category rec = null;
		while (parser.getSub()) {
			rec = parseKey(parser, store);
			if (parser.isDelete()) {
				if (rec != null)
					try (ChangeCategory record = new ChangeCategory(rec)) {
						store.free(record.rec());
					}
				continue;
			}
			if (rec == null) {
				try (ChangeCategory record = new ChangeCategory(store, 0)) {

					record.parseFields(parser);
					return record;
				}
			} else {
				try (ChangeCategory record = new ChangeCategory(rec)) {
					record.parseFields(parser);
				}
			}
		}
		return rec;
	}

	public static Category parseKey(Parser parser, Store store) {
		int nextRec = 0;
		parser.finishRelation();
		return nextRec <= 0 ? null : new Category(store, nextRec);
	}

	@Override
	public Object java() {
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

	@Override
	public FieldType type() {
		switch (field) {
		case 0:
			return FieldType.OBJECT;
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
		switch (field) {
		case 1:
			return "type";
		case 2:
			return "name";
		case 3:
			return "default_specials";
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
	public Category start() {
		return new Category(store, rec, 1);
	}

	@Override
	public Category next() {
		return field >= 6 ? null : new Category(store, rec, field + 1);
	}

	@Override
	public Category copy() {
		return new Category(store, rec, field);
	}
}
