package org.memorydb.world;

import org.memorydb.file.Parser;
import org.memorydb.structure.Store;
import org.memorydb.structure.ChangeInterface;
import org.memorydb.handler.MutationException;

/**
 * Automatically generated record class for table Category
 */
public class ChangeCategory extends Category implements AutoCloseable, ChangeInterface {
	public ChangeCategory(Store store, int rec) {
		super(store, rec == 0 ? store.allocate(Category.RECORD_SIZE) : rec);
		if (rec == 0) {
			setType(null);
			setName(null);
			store.setInt(rec, 9, 0); // ARRAY default_specials
			store.setInt(rec, 13, 0);
			store.setInt(rec, 13, 0); // ARRAY dependency
			store.setInt(rec, 17, 0);
			store.setInt(rec, 17, 0); // ARRAY effect
			store.setInt(rec, 21, 0);
			setDescription(null);
		} else {
		}
	}

	public ChangeCategory(Category current) {
		super(current.store(), current.rec());
	}

	public void setType(Category.Type value) {
		if (value == null)
			throw new MutationException("Mandatory 'type' field");
		store.setByte(rec, 4, (store.getByte(rec, 4) & 192) + 1 + value.ordinal());
	}

	public void setName(String value) {
		store.setInt(rec, 5, store.putString(value));
	}

	public void moveDefault_specials(ChangeCategory other) {
		store().setInt(rec(), 9, store().getInt(other.rec(), 9));
		store().setInt(other.rec(), 9, 0);
	}

	public void moveDependency(ChangeCategory other) {
		store().setInt(rec(), 13, store().getInt(other.rec(), 13));
		store().setInt(other.rec(), 13, 0);
	}

	public void moveEffect(ChangeCategory other) {
		store().setInt(rec(), 17, store().getInt(other.rec(), 17));
		store().setInt(other.rec(), 17, 0);
	}

	public void setDescription(String value) {
		store.setInt(rec, 21, store.putString(value));
	}

	/* package private */ void parseFields(Parser parser) {
		if (parser.hasField("type")) {
			String valueType = parser.getString("type");
			Type type = Type.get(valueType);
			if (valueType != null && type == null)
				parser.error("Cannot parse '" + valueType + "' for field Category.type");
			setType(valueType == null ? null : type);
		}
		if (parser.hasField("name")) {
			setName(parser.getString("name"));
		}
		if (parser.hasSub("default_specials")) {
			Default_specialsArray sub = new Default_specialsArray(this, -1);
			while (parser.getSub())
				sub.add().parse(parser);
		}
		if (parser.hasSub("dependency")) {
			DependencyArray sub = new DependencyArray(this, -1);
			while (parser.getSub())
				sub.add().parse(parser);
		}
		if (parser.hasSub("effect")) {
			EffectArray sub = new EffectArray(this, -1);
			while (parser.getSub())
				sub.add().parse(parser);
		}
		if (parser.hasField("description")) {
			setDescription(parser.getString("description"));
		}
	}

	@Override
	public void close() {
		// nothing yet
	}

	@Override
	public boolean java(Object value) {
		int field = 0;
		switch (field) {
		case 1:
			if (value instanceof Category.Type)
				setType((Category.Type) value);
			return value instanceof Category.Type;
		case 2:
			if (value instanceof String)
				setName((String) value);
			return value instanceof String;
		case 6:
			if (value instanceof String)
				setDescription((String) value);
			return value instanceof String;
		default:
			return false;
		}
	}

	@Override
	public ChangeInterface add() {
		int field = 0;
		switch (field) {
		case 3:
			return addDefault_specials();
		case 4:
			return addDependency();
		case 5:
			return addEffect();
		default:
			return null;
		}
	}

	@Override
	public ChangeCategory copy(int newRec) {
		assert store.validate(newRec);
		return new ChangeCategory(store, newRec);
	}
}
