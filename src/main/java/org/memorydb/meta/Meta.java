package org.memorydb.meta;


import org.memorydb.structure.FieldData;
import org.memorydb.structure.Store;

/**
 * Automatically generated project class for Meta
 */
public class Meta implements AutoCloseable {
	private final Store store;

	public Meta(Store store) {
		this.store = store;
	}

	public Meta() {
		this.store = new Store(1);
	}

	public Store getStore() {
		return store;
	}

	@Override
	public String toString() {
		return store.toString();
	}

	@FieldData(
		name = "meta",
		type = "SET",
		keyNames = {"pack"},
		keyTypes = {"STRING"},
		related = Project.class
	)
	public Project getMeta(String key1) {
		Project rec = new Project(store);
		Project.IndexMeta idx = rec.new IndexMeta(key1);
		int res = idx.search();
		if (res == 0)
			return rec;
		return new Project(store, res);
	}

	public ChangeProject addProject() {
		return new ChangeProject(store);
	}

	@Override
	public void close() {
		// nothing
	}
}
