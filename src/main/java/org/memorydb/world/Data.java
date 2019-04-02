package org.memorydb.world;


import org.memorydb.structure.FieldData;
import org.memorydb.structure.Store;

/**
 * Automatically generated project class for Data
 */
public class Data implements AutoCloseable {
	private final Store store;

	public Data(Store store) {
		this.store = store;
	}

	public Data() {
		this.store = new Store(1);
	}

	public Store store() {
		return store;
	}

	@Override
	public String toString() {
		return store.toString();
	}

	@FieldData(name = "Specials", type = "SET", related = Special.class)
	public Special getSpecials(String key1) {
		Special rec = new Special(store);
		Special.IndexSpecials idx = rec.new IndexSpecials(key1);
		int res = idx.search();
		if (res == 0)
			return rec;
		return new Special(store, res);
	}

	public ChangeCategory addCategory() {
		return new ChangeCategory(store);
	}

	public ChangeSpecial addSpecial() {
		return new ChangeSpecial(store);
	}

	@Override
	public void close() {
		// nothing
	}
}
