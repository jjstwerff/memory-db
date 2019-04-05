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

	public Store store() {
		return store;
	}

	@Override
	public String toString() {
		return store.toString();
	}

	@FieldData(name = "meta", type = "SET", related = Project.class)
	public Project getMeta(String key1) {
		int res = new Project.IndexMeta(store, key1).search();
		return res <= 0 ? null : new Project(store, res);
	}

	public ChangeProject addProject() {
		return new ChangeProject(store, 0);
	}

	@Override
	public void close() {
		// nothing
	}
}
