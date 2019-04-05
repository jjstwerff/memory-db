package org.memorydb.json;

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
		this.store = new Store(0);
	}

	public Store store() {
		return store;
	}

	@Override
	public String toString() {
		return store.toString();
	}

	public ChangeJson addJson() {
		return new ChangeJson(store, 0);
	}

	@Override
	public void close() {
		// nothing
	}
}
