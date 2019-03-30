package ${project.package};

<#list project.imports as import>
import ${import};
</#list>
<#if project.indexes?size gt 0>

import org.memorydb.structure.FieldData;
</#if>
import org.memorydb.structure.Store;

/**
 * Automatically generated project class for ${project.name}
 */
public class ${project.name} implements AutoCloseable {
	private final Store store;

	public ${project.name}(Store store) {
		this.store = store;
	}

	public ${project.name}() {
		this.store = new Store(${project.indexes?size});
	}

	public Store store() {
		return store;
	}

	@Override
	public String toString() {
		return store.toString();
	}
<#list project.indexes as index>

	@FieldData(name = "${index.name}", type = "SET", related = ${index.table.name}.class)
	public ${index.table.name} get${index.name?cap_first}(<#list index.javaTypes[0..*index.javaTypes?size] as t>${t} key${t?index + 1}<#if t?has_next>, </#if></#list>) {
		${index.table.name} rec = new ${index.table.name}(store);
		${index.table.name}.Index${index.name?cap_first} idx = rec.new Index${index.name?cap_first}(<#list index.javaTypes[0..*index.javaTypes?size] as t>key${t?index + 1}<#if t?has_next>, </#if></#list>);
		int res = idx.search();
		if (res == 0)
			return rec;
		return new ${index.table.name}(store, res);
	}
</#list>
<#list project.tableList as table><#if table.full && !table.parent??>

	public Change${table.name} add${table.name}() {
		return new Change${table.name}(store);
	}
</#if></#list>

	@Override
	public void close() {
		// nothing
	}
}
