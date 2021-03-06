package ${project.package};
<#assign rec=field.related>
<#assign table=field.parent>
<#assign hasIndexes=false>
<#assign hasArrays=false>
<#assign hasMandatory=false>
<#list rec.indexes as index><#if !rec.parent??>
<#assign hasIndexes=true>
</#if></#list>
<#list rec.fields as rfield>
<#if rfield.type == "SET"><#assign hasIndexes=true></#if>
<#if rfield.type == "ARRAY"><#assign hasArrays=true></#if>
<#if rfield.isMandatory() || rfield.type == "BOOLEAN" || rfield.type == "ENUMERATE"><#assign hasMandatory=true></#if>
</#list>

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
<#if table.included?size gt 0>
import org.memorydb.handler.CorruptionException;
</#if>
<#if hasMandatory>
import org.memorydb.handler.MutationException;
</#if>
<#if rec.includes?size == 0>
import org.memorydb.structure.ChangeInterface;
</#if>
<#if rec.fields?size gt 0>
import org.memorydb.structure.FieldData;
</#if>
<#if hasIndexes>
import org.memorydb.structure.IndexOperation;
import org.memorydb.structure.Key;
</#if>
import org.memorydb.structure.MemoryRecord;
<#if hasIndexes>
import org.memorydb.structure.RedBlackTree;
</#if>
import org.memorydb.structure.RecordData;
import org.memorydb.structure.Store;
<#list rec.imports as import>
import ${import};
</#list>

/**
 * Automatically generated record class for ${field.name}
 */

@RecordData(name = "${rec.name}"<#if field.description??>, description = "${rec.description}"</#if>)
public class ${field.name?cap_first}Array implements MemoryRecord, <#if rec.includes?size == 0>ChangeInterface, </#if><#list rec.includes as incl>Change${incl.name}, </#list>Iterable<${field.name?cap_first}Array> {
	private final Store store;
	private final ${on} parent;
	private final int idx;
	private int alloc;
	private int size;

	/* package private */ ${field.name?cap_first}Array(${on} parent, int idx) {
		this.store = parent.store();
		this.parent = parent;
		this.idx = idx;
		if (parent.rec() != 0) {
<#if table.included?size gt 0>
			this.alloc = store.getInt(parent.rec(), parent.${table.name?lower_case}Position() + ${(field.pos / 8)});
<#elseif on != table.name>
			this.alloc = store.getInt(parent.rec(), parent.index() * ${table.size} + ${(field.pos / 8)});
<#else>
			this.alloc = store.getInt(parent.rec(), ${(field.pos / 8)});
</#if>
			if (alloc != 0) {
				up(parent);
				this.size = store.getInt(alloc, 4);
			} else
				this.size = 0;
		} else {
			this.alloc = 0;
			this.size = 0;
		}
		if (size > 0 && (idx < -1 || idx >= size))
			idx = -1;
	}

	/* package private */ ${field.name?cap_first}Array(${field.name?cap_first}Array other, int idx) {
		this.store = other.store;
		this.parent = other.parent;
		this.idx = idx;
		this.alloc = other.alloc;
		this.size = other.size;
	}

	/* package private */ ${field.name?cap_first}Array(Store store, int rec, int idx) {
		this.store = store;
		this.alloc = rec;
		this.idx = idx;
		this.parent = up();
		this.size = alloc == 0 ? 0 : store.getInt(alloc, 4);
	}

	@Override
	public int rec() {
		return alloc;
	}

	@Override
	public int index() {
		return idx;
	}

	@Override
	public ${field.name?cap_first}Array copy(int newRec) {
		assert store.validate(newRec);
		return new ${field.name?cap_first}Array(store, newRec, -1);
	}

	private void up(${on} record) {
		store.setInt(alloc, 8, record.rec());
<#if on != table.name>
		store.setInt(alloc, 12, record.index());
</#if>
<#if table.included?size gt 0>
<#assign nr=1>
<#list table.included as incl><#if incl.full>
		if (record instanceof ${incl.name})
			store.setByte(alloc, 12, ${nr});<#assign nr=nr+1>
<#else><#list incl.linked as link>
		if (record instanceof ${link.upperName}Array) {
			store.setByte(alloc, 12, ${nr});<#assign nr=nr+1>
			store.setInt(alloc, 13, record.index());
		}
</#list></#if></#list>
</#if>
	}

	@Override
	public ${on} up() {
<#if table.included?size gt 0>
		if (alloc == 0)
			return null;
		switch (store.getByte(alloc, 12)) {
<#assign nr=1>
<#list table.included as incl><#if incl.full>
		case ${nr}:<#assign nr=nr+1>
			return new ${incl.name}(store, store.getInt(alloc, 8));
<#else><#list incl.linked as link>
		case ${nr}:<#assign nr=nr+1>
			return new ${link.upperName}Array(store, store.getInt(alloc, 8), store.getInt(alloc, 13));
</#list></#if></#list>
		default:
			throw new CorruptionException("Unknown upRecord type");
		}
<#elseif on != table.name>
		return new ${on}(store, store.getInt(alloc, 8), store.getInt(alloc, 12));
<#else>
		return new ${on}(store, store.getInt(alloc, 8));
</#if>
	}

	@Override
	public Store store() {
		return store;
	}

	@Override
	public int size() {
		return size;
	}

	public void clear() {
		size = 0;
		store.setInt(alloc, 4, size);
	}

	@Override
	public ${field.name?cap_first}Array add() {
		if (parent.rec() == 0)
			return this;
		if (alloc == 0) {
			alloc = store.allocate(${rec.totalSize?c} + ${table.reserve()});
			up(parent);
		} else
			alloc = store.resize(alloc, (${table.reserve()} + (size + 1) * ${rec.totalSize?c}) / 8);
<#if table.included?size gt 0>
		store.setInt(parent.rec(), parent.${table.name?lower_case}Position() + ${field.pos / 8}, alloc);
<#elseif on != table.name>
		store.setInt(parent.rec(), parent.index() * ${table.size} + ${(field.pos / 8)}, alloc);
<#else>
		store.setInt(parent.rec(), ${field.pos / 8}, alloc);
</#if>
		size++;
		store.setInt(alloc, 4, size);
		${field.name?cap_first}Array res = new ${field.name?cap_first}Array(parent, size - 1);
<#list rec.fields as field><#if field.default??>
		res.set${field.name?cap_first}(${field.default});
</#if><#if field.type == "SET" || field.type == "ARRAY">
		store.setInt(rec(), ${field.pos / 8}, 0); // ${field.type} ${field.name}
</#if></#list>
		return res;
	}

	@Override
	public Iterator<${field.name?cap_first}Array> iterator() {
		return new Iterator<>() {
			int element = -1;

			@Override
			public boolean hasNext() {
				return alloc != 0 && element + 1 < size;
			}

			@Override
			public ${field.name?cap_first}Array next() {
				if (alloc == 0 || element > size)
					throw new NoSuchElementException();
				element++;
				return new ${field.name?cap_first}Array(${field.name?cap_first}Array.this, element);
			}

			@Override
			public void remove() {
				if (alloc == 0 || element > size || element < 0)
					throw new NoSuchElementException();
				store.copy(alloc, (element + 1) * ${rec.totalSize?c} + ${table.reserve()}, element * ${rec.totalSize?c} + ${table.reserve()}, size * 17);
				element--;
				size--;
				store.setInt(alloc, 4, size);
			}
		};
	}
<#list rec.fields as rfld>
<#if rfld.type == "ENUMERATE">

	public enum ${rfld.name?cap_first} {
		<#list rfld.values as value>${value}<#if value?has_next>, </#if></#list>
	};
</#if>

	@FieldData(name = "${field.name}", type = "${field.type}", <#if field.type == "ENUMERATE">enumerate = { <#list field.values as val>"${val}"<#if val?has_next>, </#if></#list> }, </#if><#if field.related??><#if field.type == "ARRAY">related = ${field.javaType}.class, <#else>related = ${field.related}.class, </#if></#if><#if field.isCondition()>condition = true, </#if><#if field.getWhen()??>when = "${field.getWhen()}", </#if><#if field.description??>description = "${field.description}", </#if>mandatory = ${field.isMandatory()?string("true", "false")})
	public ${rfld.javaType} <#if rfld.type == "BOOLEAN" || rfld.type == "NULL_BOOLEAN">is<#else>get</#if>${rfld.name?cap_first}() {
		${rfld.getGetter(table)}
	}
<#if rfld.setter??>

	public void set${rfld.name?cap_first}(${rfld.javaType} value) {
		${rfld.getSetter(table)}
	}
</#if></#list>

	@Override
	public void output(Write write, int iterate) {
		if (alloc == 0 || iterate <= 0)
			return;
<#list rec.fields as rfld><#if rfld.type == "RELATION"><#if rfld.name != "upRecord">
		write.strField("${rfld.name}", "{" + get${rfld.name?cap_first}().keys() + "}");
</#if><#elseif rfld.type == "BYTE" && rfld.when?? && table.condition??>
		if (get${table.condition.name?cap_first}() == ${table.condition.name?cap_first}.${rfld.when})
			write.field("${rfld.name}", get${rfld.name?cap_first}());
<#elseif rfld.type != "SET" && rfld.type != "ARRAY" && rfld.type != "OBJECT">
		write.field("${rfld.name}", <#if rfld.type == "BOOLEAN" || rfld.type == "NULL_BOOLEAN">is<#else>get</#if>${rfld.name?cap_first}());
</#if><#if rfld.type == "SET">
		Index${rfld.name?cap_first} fld${rfld.name?cap_first} = get${rfld.name?cap_first}();
		if (fld${rfld.name?cap_first} != null) {
			write.sub("${rfld.name}");
			for (${rfld.related.name} sub : fld${rfld.name?cap_first})
				sub.output(write, iterate);
			write.endSub();
		}
<#elseif rfld.type == "ARRAY">
		${rfld.name?cap_first}Array fld${rfld.name?cap_first} = get${rfld.name?cap_first}();
		if (fld${rfld.name?cap_first} != null) {
			write.sub("${rfld.name}");
			for (${rfld.name?cap_first}Array sub : fld${rfld.name?cap_first})
				sub.output(write, iterate);
			write.endSub();
		}
<#elseif rfld.type == "OBJECT">
		${rfld.related.name} fld${rfld.name?cap_first} = get${rfld.name?cap_first}();
		if (fld${rfld.name?cap_first} != null && fld${rfld.name?cap_first}.rec() != 0) {
			write.sub("${rfld.name}");
			fld${rfld.name?cap_first}.output(write, iterate);
			write.endSub();
		}
</#if></#list>
<#list rec.includes as incl>
		output${incl.name}(write, iterate);
</#list>
		write.endRecord();
	}

	@Override
	public String toString() {
		Write write = new Write(new StringBuilder());
		if (idx == -1)
			for (${field.name?cap_first}Array a : this) {
				a.output(write, 4);
			}
		else
			output(write, 4);
		return write.toString();
	}

	public void parse(Parser parser) {
<#list rec.fields as field><#if field.default??>
		set${field.name?cap_first}(${field.default});
</#if><#if field.type == "SET" || field.type == "ARRAY">
		store.setInt(rec(), ${field.pos / 8}, 0); // ${field.type} ${field.name}
</#if></#list>
<#list rec.includes as incl>
		parse${incl.name}(parser);
</#list>
${field.arrayFields}<#rt>
	}
<#list rec.includes as incl>

	@Override
	public int ${incl.name?lower_case}Position() {
		return ${rec.includePos(incl, table)};
	}
</#list>

	@Override
	public String name() {
		if (idx == -1)
			return null;
<#assign max = rec.fields?size>
<#list rec.includes as incl>
<#assign nmax = max + incl.fields?size>
		if (idx >= ${max} && idx <= ${nmax})
			return name${incl.name}(idx - ${max});
<#assign max = nmax>
</#list>
<#if rec.fields?has_content>
		switch (idx) {
<#list rec.fields as field>
<#if field.name != "upRecord">
		case ${field?index}:
			return "${field.name}";
</#if></#list>
		default:
			return null;
		}
<#else>
		return null;
</#if>
	}

	@Override
	public FieldType type() {
		if (idx == -1)
			return FieldType.OBJECT;
<#assign max = rec.fields?size>
<#list rec.includes as incl>
<#assign nmax = max + incl.fields?size>
		if (idx >= ${max} && idx <= ${nmax})
			return type${incl.name}(idx - ${max});
<#assign max = nmax>
</#list>
<#if rec.fields?has_content>
		switch (idx) {
<#list rec.fields as field>
<#if field.name != "upRecord">
		case ${field?index}:
<#if field.type == 'SET' || field.type == 'ARRAY'>
			return FieldType.ARRAY;
<#elseif field.type == "RELATION">
			return FieldType.OBJECT;
<#elseif field.type == "ENUMERATE">
			return FieldType.STRING;
<#elseif field.type == "BYTE">
			return FieldType.INTEGER;
<#else>
			return FieldType.${field.type};
</#if></#if>
</#list>
		default:
			return null;
		}
<#else>
		return null;
</#if>
	}

	@Override
	public Object java() {
		if (idx == -1)
			return this;
<#assign max = rec.fields?size>
<#list rec.includes as incl>
<#assign nmax = max + incl.fields?size>
		if (idx >= ${max} && idx <= ${nmax})
			return get${incl.name}(idx - ${max});
<#assign max = nmax>
</#list>
<#if rec.fields?has_content>
		switch (idx) {
<#list rec.fields as field>
<#if field.type == 'BOOLEAN'>
		case ${field?index}:
			return is${field.name?cap_first}();
<#elseif field.type != "SET" && field.type != "ARRAY" && field.name != "upRecord">
		case ${field?index}:
			return get${field.name?cap_first}();
</#if></#list>
		default:
			return null;
		}
<#else>
		return null;
</#if>
	}

	@Override
	public boolean java(Object value) {
<#assign max = rec.fields?size>
<#list rec.includes as incl>
<#assign nmax = max + incl.fields?size>
		if (idx >= ${max} && idx <= ${nmax})
			return set${incl.name}(idx - ${max}, value);
<#assign max = nmax>
</#list>
<#if rec.fields?has_content>
		switch (idx) {
<#list rec.fields as field>
<#if field.type != "SET" && field.type != "ARRAY" && field.name != "upRecord">
		case ${field?index}:
			if (value instanceof ${field.boxedType})
				set${field.name?cap_first}((${field.boxedType}) value);
			return value instanceof ${field.boxedType};
</#if></#list>
		default:
			return false;
		}
<#else>
		return false;
</#if>
	}

	@Override
	public ${field.name?cap_first}Array index(int idx) {
		return idx < 0 || idx >= size ? null : new ${field.name?cap_first}Array(parent, idx);
	}

	@Override
	public ${field.name?cap_first}Array start() {
		return new ${field.name?cap_first}Array(parent, 0);
	}

	@Override
	public ${field.name?cap_first}Array next() {
		return idx + 1 >= size ? null : new ${field.name?cap_first}Array(parent, idx + 1);
	}

	@Override
	public boolean testLast() {
		return idx == size - 1;
	}

	@Override
	public ${field.name?cap_first}Array copy() {
		return new ${field.name?cap_first}Array(parent, idx);
	}
}
