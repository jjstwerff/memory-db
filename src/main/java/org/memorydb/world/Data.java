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
		this.store = new Store(3);
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
		int res = new Special.IndexSpecials(store, key1).search();
		return res <= 0 ? null : new Special(store, res);
	}

	@FieldData(name = "relations", type = "SET", related = State.class)
	public State getRelations(String key1) {
		int res = new State.IndexRelations(store, key1).search();
		return res <= 0 ? null : new State(store, res);
	}

	@FieldData(name = "items", type = "SET", related = Item.class)
	public Item getItems(int key1) {
		int res = new Item.IndexItems(store, key1).search();
		return res <= 0 ? null : new Item(store, res);
	}

	public ChangeCategory addCategory() {
		return new ChangeCategory(store, 0);
	}

	public ChangeItem addItem() {
		return new ChangeItem(store, 0);
	}

	public ChangeSpecial addSpecial() {
		return new ChangeSpecial(store, 0);
	}

	public ChangeState addState() {
		return new ChangeState(store, 0);
	}

	@Override
	public void close() {
		// nothing
	}
}
