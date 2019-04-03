package org.memorydb.jslt;


import org.memorydb.structure.FieldData;
import org.memorydb.structure.Store;

/**
 * Automatically generated project class for Jslt
 */
public class Jslt implements AutoCloseable {
	private final Store store;

	public Jslt(Store store) {
		this.store = store;
	}

	public Jslt() {
		this.store = new Store(2);
	}

	public Store store() {
		return store;
	}

	@Override
	public String toString() {
		return store.toString();
	}

	@FieldData(name = "sources", type = "SET", related = Source.class)
	public Source getSources(String key1) {
		int res = new Source.IndexSources(store, key1).search();
		return res <= 0 ? null : new Source(store, res);
	}

	@FieldData(name = "macros", type = "SET", related = Macro.class)
	public Macro getMacros(String key1) {
		int res = new Macro.IndexMacros(store, key1).search();
		return res <= 0 ? null : new Macro(store, res);
	}

	public ChangeExpr addExpr() {
		return new ChangeExpr(store, 0);
	}

	public ChangeMacro addMacro() {
		return new ChangeMacro(store, 0);
	}

	public ChangeMatchObject addMatchObject() {
		return new ChangeMatchObject(store, 0);
	}

	public ChangeSource addSource() {
		return new ChangeSource(store, 0);
	}

	public ChangeVariable addVariable() {
		return new ChangeVariable(store, 0);
	}

	@Override
	public void close() {
		// nothing
	}
}
