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
<#if rfield.isMandatory()><#assign hasMandatory=true></#if>
</#list>

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
<#if table.included?size gt 0>
import org.memorydb.handler.CorruptionException;
</#if>
import org.memorydb.structure.ChangeInterface;
<#if hasMandatory>
import org.memorydb.handler.MutationException;
</#if>
<#if rec.fields?size gt 0>
import org.memorydb.structure.FieldData;
</#if>
<#if hasIndexes>
import org.memorydb.structure.IndexOperation;
import org.memorydb.structure.Key;
import org.memorydb.structure.RedBlackTree;
</#if>
import org.memorydb.structure.InputOutputException;
import org.memorydb.structure.RecordData;
import org.memorydb.structure.RecordInterface;
import org.memorydb.structure.Store;
<#list rec.imports as import>
import ${import};
</#list>

/**
 * Automatically generated record class for ${field.name}
 */

@RecordData(name = "${rec.name}"<#if field.description??>, description = "${rec.description}"</#if>)
public class ${field.name?cap_first}Array implements <#if rec.includes?size == 0>ChangeInterface, </#if><#list rec.includes as incl>Change${incl.name}, </#list>Iterable<${field.name?cap_first}Array> {
	private final Store store;
	private final ${table.name} parent;
	private int idx;
	private int alloc;
	private int size;

	/* package private */ ${field.name?cap_first}Array(${table.name} parent, int idx) {
		this.store = parent.getStore();
		this.parent = parent;
		this.idx = idx;
		if (parent.getRec() != 0) {
<#if table.included?size gt 0>
			this.alloc = store.getInt(parent.getRec(), parent.${table.name?lower_case}Position() + ${(field.pos / 8)});
<#else>
			this.alloc = store.getInt(parent.getRec(), ${(field.pos / 8)});
</#if>
			if (alloc != 0) {
				setUpRecord(parent);
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
		this.parent = getUpRecord();
		this.size = alloc == 0 ? 0 : store.getInt(alloc, 4);
	}

	@Override
	public int getRec() {
		return alloc;
	}

	@Override
	public int getArrayIndex() {
		return idx;
	}

	@Override
	public void setRec(int rec) {
		this.alloc = rec;
	}

	/* package private */ void setUpRecord(${table.name} record) {
		store.setInt(alloc, 8, record.getRec());
<#if table.included?size gt 0>
<#assign nr=1>
<#list table.included as incl><#if incl.full>
		if (record instanceof ${incl.name})
			store.setByte(alloc, 12, ${nr});<#assign nr=nr+1>
<#else><#list incl.linked as link>
		if (record instanceof ${link.upperName}Array) {
			store.setByte(alloc, 12, ${nr});<#assign nr=nr+1>
			store.setInt(alloc, 13, record.getArrayIndex());
		}
</#list></#if></#list>
</#if>
	}

	@Override
	public ${table.name} getUpRecord() {
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
<#else>
		return new ${table.name}(store, store.getInt(alloc, 8));
</#if>
	}

	@Override
	public Store getStore() {
		return store;
	}

	public int getSize() {
		return size;
	}

	/* package private */ ${field.name?cap_first}Array add() {
		if (parent.getRec() == 0)
			return this;
		idx = size;
		if (alloc == 0) {
			alloc = store.allocate(${rec.totalSize?c} + ${table.reserve()});
			setUpRecord(parent);
		} else
			alloc = store.resize(alloc, (${table.reserve()} + (idx + 1) * ${rec.totalSize?c}) / 8);
<#if table.included?size gt 0>
		store.setInt(parent.getRec(), parent.${table.name?lower_case}Position() + ${field.pos / 8}, alloc);
<#else>
		store.setInt(parent.getRec(), ${field.pos / 8}, alloc);
</#if>
		size = idx + 1;
		store.setInt(alloc, 4, size);
<#list rec.fields as field><#if field.default??>
		set${field.name?cap_first}(${field.default});
</#if><#if field.type == "SET" || field.type == "ARRAY">
		store.setInt(rec, ${field.pos / 8}, 0); // ${field.type} ${field.name}
</#if></#list>
		return this;
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
		};
	}
<#list rec.fields as rfld>
<#if rfld.type == "ENUMERATE">

	public enum ${rfld.name?cap_first} {
		<#list rfld.values as value>${value}<#if value?has_next>, </#if></#list>
	};
</#if>

	@FieldData(
		name = "${field.name}",
		type = "${field.type}",
<#if field.type == "ENUMERATE">
		enumerate = {<#list field.values as val>"${val}"<#if val?has_next>, </#if></#list>},
</#if><#if field.type == "SET">
		keyNames = {<#list field.index.keys as key>"${key.name}"<#if key?has_next>, </#if></#list>},
		keyTypes = {<#list field.index.keys as key>"${key.type}"<#if key?has_next>, </#if></#list>},
</#if>
<#if field.related??><#if field.type == "ARRAY">
		related = ${field.javaType}.class,
<#else>
		related = ${field.related}.class,
</#if></#if>
<#if field.isCondition()>
		condition = true,
</#if>
<#if field.getWhen()??>
		when = "${field.getWhen()}",
</#if>
<#if field.description??>
		description = "${field.description}",
</#if>
		mandatory = ${field.isMandatory()?string("true", "false")}
	)

	public ${rfld.javaType} <#if rfld.type == "BOOLEAN" || rfld.type == "NULL_BOOLEAN">is<#else>get</#if>${rfld.name?cap_first}() {
		${rfld.getGetter(table)}
	}
<#if rfld.setter??>

	public void set${rfld.name?cap_first}(${rfld.javaType} value) {
		${rfld.getSetter(table)}
	}
</#if></#list>

	@Override
	public void output(Write write, int iterate) throws IOException {
		if (alloc == 0 || iterate <= 0)
			return;
<#list rec.fields as rfld><#if rfld.type == "RELATION"><#if rfld.name != "upRecord">
		write.strField("${rfld.name}", "{" + get${rfld.name?cap_first}().keys() + "}", ${(rfld?index == 0)?c});
</#if><#elseif rfld.type != "SET" && rfld.type != "ARRAY" && rfld.type != "OBJECT">
		write.field("${rfld.name}", <#if rfld.type == "BOOLEAN" || rfld.type == "NULL_BOOLEAN">is<#else>get</#if>${rfld.name?cap_first}(), ${(rfld?index == 0)?c});
</#if><#if rfld.type == "SET">
		Index${rfld.name?cap_first} fld${rfld.name?cap_first} = get${rfld.name?cap_first}();
		if (fld${rfld.name?cap_first} != null) {
			write.sub("${rfld.name}", ${(rfld?index == 0)?c});
			for (${rfld.related.name} sub : fld${rfld.name?cap_first})
				sub.output(write, iterate - 1);
			write.endSub();
		}
<#elseif rfld.type == "ARRAY">
		${rfld.name?cap_first}Array fld${rfld.name?cap_first} = get${rfld.name?cap_first}();
		if (fld${rfld.name?cap_first} != null) {
			write.sub("${rfld.name}", ${(rfld?index == 0)?c});
			for (${rfld.name?cap_first}Array sub : fld${rfld.name?cap_first})
				sub.output(write, iterate - 1);
			write.endSub();
		}
<#elseif rfld.type == "OBJECT">
		${rfld.related.name} fld${rfld.name?cap_first} = get${rfld.name?cap_first}();
		if (fld${rfld.name?cap_first} != null && fld${rfld.name?cap_first}.getRec() != 0) {
			write.sub("${rfld.name}", ${(rfld?index == 0)?c});
			fld${rfld.name?cap_first}.output(write, iterate - 1);
			write.endSub();
		}
</#if></#list>
<#list rec.includes as incl>
		output${incl.name}(write, iterate, ${(rec.fields?size == 0)?c});
</#list>
		write.endRecord();
	}

	@Override
	public String toString() {
		Write write = new Write(new StringBuilder());
		try {
			if (idx == -1)
				for (${field.name?cap_first}Array a : this) {
					a.output(write, 4);
				}
			else
				output(write, 4);
		} catch (IOException e) {
			throw new InputOutputException(e);
		}
		return write.toString();
	}

	public void parse(Parser parser) {
<#list rec.fields as field><#if field.default??>
		set${field.name?cap_first}(${field.default});
</#if><#if field.type == "SET" || field.type == "ARRAY">
		store.setInt(rec, ${field.pos / 8}, 0); // ${field.type} ${field.name}
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
<#if rec.includes?size gt 0>

	@Override
	public boolean parseKey(Parser parser) {
		return false;
	}

	public void setIdx(int idx) {
		if (idx >= 0 && idx < size)
			this.idx = idx;
	}
</#if>

	@Override
	public void close() {
		// nothing
	}

	@Override
	public boolean exists() {
		return getRec() != 0;
	}

	@Override
	public String name(int field) {
<#assign max = rec.fields?size>
<#list rec.includes as incl>
<#assign nmax = max + incl.fields?size>
		if (field >= ${max} && field <= ${nmax})
			return name${incl.name}(field - ${max});
<#assign max = nmax>
</#list>
		switch (field) {
<#list rec.fields as field>
<#if field.name != "upRecord">
		case ${1 + field?index}:
			return "${field.name}";
</#if></#list>
		default:
			return null;
		}
	}

	@Override
	public FieldType type(int field) {
<#assign max = rec.fields?size>
<#list rec.includes as incl>
<#assign nmax = max + incl.fields?size>
		if (field >= ${max} && field <= ${nmax})
			return type${incl.name}(field - ${max});
<#assign max = nmax>
</#list>
		switch (field) {
<#list rec.fields as field>
<#if field.name != "upRecord">
		case ${1 + field?index}:
<#if field.type == 'SET' || field.type == 'ARRAY'>
			return FieldType.ITERATE;
<#elseif field.type == "RELATION">
			return FieldType.OBJECT;
<#elseif field.type == "ENUMERATE">
			return FieldType.STRING;
<#else>
			return FieldType.${field.type};
</#if></#if>
</#list>
		default:
			return null;
		}
	}

	@Override
	public Object get(int field) {
<#assign max = rec.fields?size>
<#list rec.includes as incl>
<#assign nmax = max + incl.fields?size>
		if (field >= ${max} && field <= ${nmax})
			return get${incl.name}(field - ${max});
<#assign max = nmax>
</#list>
		switch (field) {
<#list rec.fields as field>
<#if field.type == 'BOOLEAN'>
		case ${1 + field?index}:
			return is${field.name?cap_first}();
<#elseif field.type != "SET" && field.type != "ARRAY" && field.name != "upRecord">
		case ${1 + field?index}:
			return get${field.name?cap_first}();
</#if></#list>
		default:
			return null;
		}
	}

	@Override
	public Iterable<? extends RecordInterface> iterate(int field, Object... key) {
<#assign max = rec.fields?size>
<#list rec.includes as incl>
<#assign nmax = max + incl.fields?size>
		if (field >= ${max} && field <= ${nmax})
			return iterate${incl.name}(field - ${max});
<#assign max = nmax>
</#list>
		switch (field) {
<#list rec.fields as field>
<#if field.type == 'SET'>
		case ${1 + field?index}:
<#list 1..field.index.keys?size as i>
			if (key.length > ${index.javaTypes?size - i})
				return new Index${field.index.name?cap_first}(new ${field.related.name}(store)<#list field.index.javaTypes[0..*i] as t>, (${t}) key[${t?index}]</#list>);
</#list>
			return get${field.name?cap_first}();
<#elseif field.type == 'ARRAY'>
		case ${1 + field?index}:
			return get${field.name?cap_first}();
</#if></#list>
		default:
			return null;
		}
	}

	@Override
	public boolean set(int field, Object value) {
<#assign max = rec.fields?size>
<#list rec.includes as incl>
<#assign nmax = max + incl.fields?size>
		if (field >= ${max} && field <= ${nmax})
			return set${incl.name}(field - ${max}, value);
<#assign max = nmax>
</#list>
		switch (field) {
<#list rec.fields as field>
<#if field.type != "SET" && field.type != "ARRAY" && field.name != "upRecord">
		case ${field?index + 1}:
			if (value instanceof ${field.boxedType})
				set${field.name?cap_first}((${field.boxedType}) value);
			return value instanceof ${field.boxedType};
</#if></#list>
		default:
			return false;
		}
	}

	@Override
	public ChangeInterface add(int field) {
<#assign max = rec.fields?size>
<#list rec.includes as incl>
<#assign nmax = max + incl.fields?size>
		if (field >= ${max} && field <= ${nmax})
			return add${incl.name}(field - ${max});
<#assign max = nmax>
</#list>
		switch (field) {
<#list rec.fields as field>
<#if field.type == "SET" || field.type == "ARRAY">
		case ${field?index + 1}:
			return add${field.name?cap_first}();
</#if></#list>
		default:
			return null;
		}
	}
}