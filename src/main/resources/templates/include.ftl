package ${project.package};
<#assign hasIndexes=false>
<#list table.indexes as index><#if !table.parent??>
<#assign hasIndexes=true>
</#if></#list>
<#list table.fields as field>
<#if field.type == "SET"><#assign hasIndexes=true></#if>
</#list>

import java.io.IOException;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.structure.FieldData;
import org.memorydb.structure.RecordData;
<#if hasIndexes>
import org.memorydb.structure.IndexOperation;
import org.memorydb.structure.Key;
</#if>
<#if table.includes?size == 0>
import org.memorydb.structure.MemoryRecord;
</#if>
<#if hasIndexes>
import org.memorydb.structure.RedBlackTree;
import org.memorydb.structure.TreeIndex;
</#if>
import org.memorydb.structure.RecordInterface;
import org.memorydb.structure.Store;
<#list table.imports as import>
import ${import};
</#list>

/**
 * Automatically generated record class for table ${table.name}
 */
@RecordData(name = "${table.name}"<#if table.description??>, description = "${table.description}"</#if>)
public interface ${table.name} extends <#if table.includes?size == 0>MemoryRecord, RecordInterface</#if><#list table.includes as incl>${incl.name}<#if incl?has_next>, </#if></#list> {
	int ${table.name?lower_case}Position();

	@Override
	Store getStore();
<#list table.includes as incl>

	@Override
	default int ${incl.name?lower_case}Position() {
		return ${table.name?lower_case}Position() + ${table.includePos(incl, null)};
	}
</#list>

<#if table.includes?size &gt; 0>
	@Override
</#if>
	boolean parseKey(Parser parser);

	default Change${table.name?cap_first} change${table.name}() {
<#list table.included as ext>
<#if ext.full>
		if (this instanceof ${ext.name})
			return new Change${ext.name}((${ext.name}) this);
<#else><#list ext.linked as link>
		if (this instanceof ${link.name?cap_first}Array)
			return (${link.name?cap_first}Array) this;
</#list></#if>
</#list>
		return null;
	}
<#list table.fields as field>
<#if field.type == "ENUMERATE">

	public enum ${field.name?cap_first} {
		<#list field.values as value>${value}<#if value?has_next>, </#if></#list>;

		private static Map<String, ${field.name?cap_first}> map = new HashMap<>();

		static {
			for (${field.name?cap_first} tp : ${field.name?cap_first}.values()) {
				map.put(tp.toString(), tp);
			}
		}

		public static ${field.name?cap_first} get(String value) {
			return map.get(value);
		}
	}
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
		mandatory = ${field.isMandatory()?string("true", "false")}
	)
	default ${field.javaType} <#if field.type == "BOOLEAN" || field.type == "NULL_BOOLEAN">is<#else>get</#if>${field.name?cap_first}() {
		${field.getter}
	}
<#if field.type == "ARRAY">

	default ${field.javaType} get${field.name?cap_first}(int index) {
<#if field.whenCond??>
		return get${table.condition.name?cap_first}() != ${table.condition.name?cap_first}.${field.whenCond} ? new ${field.name?cap_first}Array(getStore(), 0, -1) : new ${field.name?cap_first}Array(this, index);
<#else>
		return new ${field.name?cap_first}Array(this, index);
</#if>
	}

	default ${field.javaType} add${field.name?cap_first}() {
<#if field.whenCond??>
		return get${table.condition.name?cap_first}() != ${table.condition.name?cap_first}.${field.whenCond} ? new ${field.name?cap_first}Array(getStore(), 0, -1) : get${field.name?cap_first}().add();
<#else>
		return get${field.name?cap_first}().add();
</#if>
	}
<#elseif field.type == "SET"><#assign index=field.index>

	default ${field.related.name} get${field.name?cap_first}(<#list index.javaTypes[0..*index.javaTypes?size] as t>${t} key${t?index + 1}<#if t?has_next>, </#if></#list>) {
		${field.related.name} resultRec = new ${field.related.name}(getStore());
		Index${index.name?cap_first} idx = new Index${index.name?cap_first}(this, resultRec<#list index.javaTypes[0..*index.javaTypes?size] as t>, key${t?index + 1}</#list>);
		int res = idx.search();
		if (res == 0)
			return resultRec;
		return new ${field.related.name}(getStore(), res);
	}

	default Change${field.related.name?cap_first} add${field.name?cap_first}() {
		return new Change${field.related.name}(this, 0);
	}

	/* package private */ class Index${index.name?cap_first} extends TreeIndex<${field.related.name}> {
		private final ${table.name} ${table.name?lower_case};

		public Index${index.name?cap_first}(${table.name} ${table.name?lower_case}, ${field.related.name} record) {
			super(record, null, ${index.flagPos}, ${index.fieldPos});
			this.${table.name?lower_case} = ${table.name?lower_case};
		}
<#list 1..index.javaTypes?size as i>

		public Index${index.name?cap_first}(${table.name} ${table.name?lower_case}, ${field.related.name} record<#list index.javaTypes[0..*i] as t>, ${t} key${t?index + 1}</#list>) {
			super(record, new Key() {
				@Override
				public int compareTo(int recNr) {
					if (recNr < 0)
						return -1;
					assert record.getStore().validate(recNr);
					record.setRec(recNr);
					int o = 0;
<#list index.javaTypes[0..*i] as t>
					o = RedBlackTree.compare(key${t?index + 1}, record.${index.retrieve[t?index]});
<#if t?has_next>
					if (o != 0)
						return o;
</#if></#list>
					return o;
				}

				@Override
				public IndexOperation oper() {
					return IndexOperation.EQ;
				}
			}, ${index.flagPos}, ${index.fieldPos});
			this.${table.name?lower_case} = ${table.name?lower_case};
		}
</#list>

		@Override
		public int first() {
			return super.first();
		}

		@Override
		public int next(int val) {
			return super.next(val);
		}

		@Override
		protected int readTop() {
			return ${index.topGet};
		}

		@Override
		protected void changeTop(int value) {
			${index.topSet};
		}

		@Override
		protected int compareTo(int a, int b) {
			${field.related.name} recA = new ${field.related.name}(${table.name?lower_case}.getStore(), a);
			${field.related.name} recB = new ${field.related.name}(${table.name?lower_case}.getStore(), b);
			int o = 0;
<#list index.retrieve as retrieve>
			o = compare(recA.${retrieve}, recB.${retrieve});
<#if retrieve?has_next>
			if (o == 0)
				return o;
</#if></#list>
			return o;
		}

		@Override
		protected String toString(int rec) {
			return new ${field.related.name}(${table.name?lower_case}.getStore(), rec).toString();
		}
	}
</#if></#list>

	default void output${table.name}(Write write, int iterate, boolean first) throws IOException {
		if (getRec() == 0 || iterate <= 0)
			return;
<#list table.fields as field><#if field.type == "RELATION"><#if field.name != "upRecord">
		write.field("${field.name}", get${field.name?cap_first}(), <#if field?index == 0>first<#else>false</#if>);
</#if><#elseif field.type == "BOOLEAN" && field.getWhen()??>
		if (get${table.condition.name?cap_first}() == ${table.condition.name?cap_first}.${field.getWhen()})
			write.field("${field.name}", is${field.name?cap_first}(), <#if field?index == 0>first<#else>false</#if>);
<#elseif field.type != "SET" && field.type != "ARRAY" && field.type != "OBJECT">
		write.field("${field.name}", <#if field.type == "BOOLEAN" || field.type == "NULL_BOOLEAN">is<#else>get</#if>${field.name?cap_first}(), <#if field?index == 0>first<#else>false</#if>);
</#if><#if field.type == "SET">
		Index${field.name?cap_first} fld${field.name?cap_first} = get${field.name?cap_first}();
		if (fld${field.name?cap_first} != null) {
			write.sub("${field.name}", <#if field?index == 0>first<#else>false</#if>);
			for (${field.related.name} sub : fld${field.name?cap_first})
				sub.output(write, iterate);
			write.endSub();
		}
<#elseif field.type == "ARRAY">
		${field.name?cap_first}Array fld${field.name?cap_first} = get${field.name?cap_first}();
		if (fld${field.name?cap_first} != null) {
			write.sub("${field.name}", <#if field?index == 0>first<#else>false</#if>);
			for (${field.name?cap_first}Array sub : fld${field.name?cap_first})
				sub.output(write, iterate);
			write.endSub();
		}
<#elseif field.type == "OBJECT">
		${field.related.name} fld${field.name?cap_first} = get${field.name?cap_first}();
		if (fld${field.name?cap_first} != null && fld${field.name?cap_first}.getRec() != 0) {
			write.sub("${field.name}", <#if field?index == 0>first<#else>false</#if>);
			fld${field.name?cap_first}.output(write, iterate);
			write.endSub();
		}
</#if></#list>
	}

	default Object get${table.name}(int field) {
<#assign max = table.fields?size>
<#list table.includes as incl>
<#assign nmax = max + incl.fields?size>
		if (field >= ${max} && field <= ${nmax})
			return get${incl.name}(field - ${max});
<#assign max = nmax>
</#list>
		switch (field) {
<#list table.fields as field>
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

	default Iterable<? extends RecordInterface> iterate${table.name}(int field, <#if !table.fieldIndex>@SuppressWarnings("unused") </#if>Object... key) {
<#assign max = table.fields?size>
<#list table.includes as incl>
<#assign nmax = max + incl.fields?size>
		if (field >= ${max} && field <= ${nmax})
			return iterate${incl.name}(field - ${max});
<#assign max = nmax>
</#list>
		switch (field) {
<#list table.fields as field>
<#if field.type == 'SET'>
		case ${1 + field?index}:
<#list 1..field.index.keys?size as i>
			if (key.length > ${index.javaTypes?size - i})
				return new Index${field.index.name?cap_first}(this, new ${field.related.name}(getStore())<#list field.index.javaTypes[0..*i] as t>, (${t}) key[${t?index}]</#list>);
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

	default FieldType type${table.name}(int field) {
<#assign max = table.fields?size>
<#list table.includes as incl>
<#assign nmax = max + incl.fields?size>
		if (field >= ${max} && field <= ${nmax})
			return type${incl.name}(field - ${max});
<#assign max = nmax>
</#list>
		switch (field) {
<#list table.fields as field>
<#if field.name != "upRecord">
		case ${1 + field?index}:
<#if field.type == 'SET' || field.type == 'ARRAY'>
			return FieldType.ARRAY;
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

	default String name${table.name}(int field) {
<#assign max = table.fields?size>
<#list table.includes as incl>
<#assign nmax = max + incl.fields?size>
		if (field >= ${max} && field <= ${nmax})
			return name${incl.name}(field - ${max});
<#assign max = nmax>
</#list>
		switch (field) {
<#list table.fields as field>
<#if field.name != "upRecord">
		case ${1 + field?index}:
			return "${field.name}";
</#if></#list>
		default:
			return null;
		}
	}
}
