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

	public Store getStore() {
		return store;
	}

	@Override
	public String toString() {
		return store.toString();
	}

	@FieldData(
		name = "sources",
		type = "SET",
		keyNames = {"name"},
		keyTypes = {"STRING"},
		related = Source.class
	)
	public Source getSources(String key1) {
		Source rec = new Source(store);
		Source.IndexSources idx = rec.new IndexSources(key1);
		int res = idx.search();
		if (res == 0)
			return rec;
		return new Source(store, res);
	}

	@FieldData(
		name = "macros",
		type = "SET",
		keyNames = {"name"},
		keyTypes = {"STRING"},
		related = Macro.class
	)
	public Macro getMacros(String key1) {
		Macro rec = new Macro(store);
		Macro.IndexMacros idx = rec.new IndexMacros(key1);
		int res = idx.search();
		if (res == 0)
			return rec;
		return new Macro(store, res);
	}

	public ChangeExpr addExpr() {
		return new ChangeExpr(store);
	}

	public ChangeMacro addMacro() {
		return new ChangeMacro(store);
	}

	public ChangeSource addSource() {
		return new ChangeSource(store);
	}

	public ChangeVariable addVariable() {
		return new ChangeVariable(store);
	}

	@Override
	public void close() {
		// nothing
	}
}
