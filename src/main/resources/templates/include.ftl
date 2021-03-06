package ${project.package};
<#assign hasIndexes=false>
<#list table.indexes as index><#if !table.parent??>
<#assign hasIndexes=true>
</#if></#list>
<#list table.fields as field>
<#if field.type == "SET"><#assign hasIndexes=true></#if>
</#list>
<#if hasIndexes>

import java.util.Iterator;
</#if>

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
<#if table.includes?size == 0>
import org.memorydb.structure.RecordInterface;
</#if>
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
	Store store();
<#list table.includes as incl>

	@Override
	default int ${incl.name?lower_case}Position() {
		return ${table.name?lower_case}Position() + ${table.includePos(incl, null)};
	}
</#list>

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

	@FieldData(name = "${field.name}", type = "${field.type}", <#if field.type == "ENUMERATE">enumerate = { <#list field.values as val>"${val}"<#if val?has_next>, </#if></#list> }, </#if><#if field.related??><#if field.type == "ARRAY">related = ${field.javaType}.class, <#else>related = ${field.related}.class, </#if></#if><#if field.isCondition()>condition = true, </#if><#if field.getWhen()??>when = "${field.getWhen()}", </#if>mandatory = ${field.isMandatory()?string("true", "false")})
	default ${field.javaType} <#if field.type == "BOOLEAN" || field.type == "NULL_BOOLEAN">is<#else>get</#if>${field.name?cap_first}() {
		${field.getter}
	}
<#if field.type == "ARRAY">

	default ${field.javaType} get${field.name?cap_first}(int index) {
<#if field.whenCond??>
		return get${table.condition.name?cap_first}() != ${table.condition.name?cap_first}.${field.whenCond} ? new ${field.name?cap_first}Array(store(), 0, -1) : new ${field.name?cap_first}Array(this, index);
<#else>
		return new ${field.name?cap_first}Array(this, index);
</#if>
	}

	default ${field.javaType} add${field.name?cap_first}() {
<#if field.whenCond??>
		return get${table.condition.name?cap_first}() != ${table.condition.name?cap_first}.${field.whenCond} ? new ${field.name?cap_first}Array(store(), 0, -1) : get${field.name?cap_first}().add();
<#else>
		return get${field.name?cap_first}().add();
</#if>
	}
<#elseif field.type == "SET"><#assign index=field.index>

	default ${field.related.name} get${field.name?cap_first}(<#list index.javaTypes[0..*index.javaTypes?size] as t>${t} key${t?index + 1}<#if t?has_next>, </#if></#list>) {
		int res = new Index${index.name?cap_first}(this<#list index.javaTypes[0..*index.javaTypes?size] as t>, key${t?index + 1}</#list>).search();
		return res <= 0 ? null : new ${field.related.name}(store(), res);
	}

	default Change${field.related.name?cap_first} add${field.name?cap_first}() {
		return new Change${field.related.name}(this, 0);
	}

	/* package private */ static class Index${index.name?cap_first} extends TreeIndex implements Iterable<${field.related.name}> {
		private final ${table.name} record;

		public Index${index.name?cap_first}(${table.name} record) {
			super(record.store(), null, ${index.flagPos}, ${index.fieldPos});
			this.record = record;
		}
<#list 1..index.javaTypes?size as i>

		public Index${index.name?cap_first}(${table.name} record<#list index.javaTypes[0..*i] as t>, ${t} key${t?index + 1}</#list>) {
			super(record.store(), new Key() {
				@Override
				public int compareTo(int recNr) {
					if (recNr < 0)
						return -1;
					assert record.store().validate(recNr);
					${field.related.name} rec = new ${field.related.name}(record.store(), recNr);
					int o = 0;
<#list index.javaTypes[0..*i] as t>
					o = RedBlackTree.compare(key${t?index + 1}, rec.${index.retrieve[t?index]});
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
			this.record = record;
		}
</#list>

		private Index${index.name?cap_first}(${table.name} record, Key key, int flag, int field) {
			super(record.store(), key, flag, field);
			this.record = record;
		}

		@Override
		public FieldType type() {
			return FieldType.OBJECT;
		}

		@Override
		public Index${index.name?cap_first} copy() {
			return new Index${index.name?cap_first}(record, key, flag, field);
		}

		@Override
		public ${field.related.name} field(String name) {
			int r = new Index${index.name?cap_first}(record, name).search();
			return r <= 0 ? null : new IterRecord(r);
		}

		@Override
		public ${field.related.name} start() {
			int r = first();
			return r <= 0 ? null : new IterRecord(r);
		}

		private class IterRecord extends ${field.related.name} {
			private IterRecord(int r) {
				super(record.store(), r);
			}

			@Override
			public IterRecord next() {
				int r = rec <= 0 ? 0 : toNext(rec);
				return r <= 0 ? null : new IterRecord(r);
			}
		}

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
			${field.related.name} recA = new ${field.related.name}(record.store(), a);
			${field.related.name} recB = new ${field.related.name}(record.store(), b);
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
		public Iterator<${field.related.name}> iterator() {
			return new Iterator<>() {
				int nextRec = search();

				@Override
				public boolean hasNext() {
					return nextRec > 0;
				}

				@Override
				public ${field.related.name} next() {
					int n = nextRec;
					nextRec = toNext(nextRec);
					return n <= 0 ? null : new ${field.related.name}(store, n);
				}
			};
		}
	}
</#if></#list>

	default void output${table.name}(Write write, int iterate) {
		if (rec() == 0 || iterate <= 0)
			return;
<#list table.fields as field><#if field.type == "RELATION"><#if field.name != "upRecord">
		write.field("${field.name}", get${field.name?cap_first}());
</#if><#elseif (field.type == "BOOLEAN" || field.type == "BYTE") && field.getWhen()??>
		if (get${table.condition.name?cap_first}() == ${table.condition.name?cap_first}.${field.getWhen()})
			write.field("${field.name}", <#if field.type == "BOOLEAN" || field.type == "NULL_BOOLEAN">is<#else>get</#if>${field.name?cap_first}());
<#elseif field.type != "SET" && field.type != "ARRAY" && field.type != "OBJECT">
		write.field("${field.name}", <#if field.type == "BOOLEAN" || field.type == "NULL_BOOLEAN">is<#else>get</#if>${field.name?cap_first}());
</#if><#if field.type == "SET">
		Index${field.name?cap_first} fld${field.name?cap_first} = get${field.name?cap_first}();
		if (fld${field.name?cap_first} != null) {
			write.sub("${field.name}");
			for (${field.related.name} sub : fld${field.name?cap_first})
				sub.output(write, iterate);
			write.endSub();
		}
<#elseif field.type == "ARRAY">
		${field.name?cap_first}Array fld${field.name?cap_first} = get${field.name?cap_first}();
		if (fld${field.name?cap_first} != null) {
			write.sub("${field.name}");
			for (${field.name?cap_first}Array sub : fld${field.name?cap_first})
				sub.output(write, iterate);
			write.endSub();
		}
<#elseif field.type == "OBJECT">
		${field.related.name} fld${field.name?cap_first} = get${field.name?cap_first}();
		if (fld${field.name?cap_first} != null && fld${field.name?cap_first}.rec() != 0) {
			write.sub("${field.name}");
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
<#elseif field.type == "BYTE">
			return FieldType.INTEGER;
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
