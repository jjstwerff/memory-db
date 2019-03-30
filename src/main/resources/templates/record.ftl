package ${project.package};
<#assign hasIndexes=false>
<#list table.indexes as index><#if !table.parent??>
<#assign hasIndexes=true>
</#if></#list>
<#list table.fields as field>
<#if field.type == "SET"><#assign hasIndexes=true></#if>
</#list>

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
<#if table.fields?size gt 0>
import org.memorydb.structure.FieldData;
</#if>
<#if hasIndexes>
import org.memorydb.structure.IndexOperation;
import org.memorydb.structure.Key;
</#if>
<#if table.includes?size == 0>
import org.memorydb.structure.MemoryRecord;
</#if>
import org.memorydb.structure.RecordData;
import org.memorydb.structure.RecordInterface;
<#if hasIndexes>
import org.memorydb.structure.RedBlackTree;
</#if>
import org.memorydb.structure.Store;
<#if hasIndexes>
import org.memorydb.structure.TreeIndex;
</#if>
<#list table.imports as import>
import ${import};
</#list>

/**
 * Automatically generated record class for table ${table.name}
 */
@RecordData(name = "${table.name}"<#if table.description??>, description = "${table.description}"</#if>)
public class ${table.name} implements <#if table.includes?size == 0>MemoryRecord, RecordInterface<#else><#list table.includes as incl>${incl.name}<#if incl?has_next>, </#if></#list></#if> {
	/* package private */ Store store;
	protected int rec;
	/* package private */ static final int RECORD_SIZE = ${table.totalSize};

	public ${table.name}(Store store) {
		this.store = store;
		this.rec = 0;
	}

	public ${table.name}(Store store, int rec) {
		rec = store.correct(rec);
		this.store = store;
		this.rec = rec;
	}

	@Override
	public int rec() {
		return rec;
	}

	@Override
	public void rec(int rec) {
		assert store.validate(rec);
		this.rec = rec;
	}

	@Override
	public Store store() {
		return store;
	}
<#list table.includes as incl>

	@Override
	public int ${incl.name?lower_case}Position() {
		return ${table.includePos(incl, null)};
	}
</#list>

	@Override
	public Change${table.name?cap_first} change() {
		return new Change${table.name?cap_first}(this);
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

		public static ${field.name?cap_first} get(String val) {
			return map.containsKey(val) ? map.get(val) : null;
		}
	}
</#if>

	@FieldData(name = "${field.name}", type = "${field.type}", <#if field.type == "ENUMERATE">enumerate = { <#list field.values as val>"${val}"<#if val?has_next>, </#if></#list> }, </#if><#if field.related??><#if field.type == "ARRAY">related = ${field.javaType}.class, <#else>related = ${field.related}.class, </#if></#if><#if field.isCondition()>condition = true, </#if><#if field.getWhen()??>when = "${field.getWhen()}", </#if>mandatory = ${field.isMandatory()?string("true", "false")})
	public ${field.javaType} <#if field.type == "BOOLEAN" || field.type == "NULL_BOOLEAN">is<#else>get</#if>${field.name?cap_first}() {
		${field.getter}
	}
<#if field.type == "ARRAY">

	public ${field.javaType} get${field.name?cap_first}(int index) {
<#if field.whenCond??>
		return get${table.condition.name?cap_first}() != ${table.condition.name?cap_first}.${field.whenCond} ? new ${field.name?cap_first}Array(store, 0, -1) : new ${field.name?cap_first}Array(this, index);
<#else>
		return new ${field.name?cap_first}Array(this, index);
</#if>
	}

	public ${field.javaType} add${field.name?cap_first}() {
<#if field.whenCond??>
		return get${table.condition.name?cap_first}() != ${table.condition.name?cap_first}.${field.whenCond} ? new ${field.name?cap_first}Array(store, 0, -1) : get${field.name?cap_first}().add();
<#else>
		return get${field.name?cap_first}().add();
</#if>
	}
<#elseif field.type == "SET"><#assign index=field.index>

	public ${field.related.name} get${field.name?cap_first}(<#list index.javaTypes[0..*index.javaTypes?size] as t>${t} key${t?index + 1}<#if t?has_next>, </#if></#list>) {
		${field.related.name} resultRec = new ${field.related.name}(store);
		Index${index.name?cap_first} idx = new Index${index.name?cap_first}(resultRec<#list index.javaTypes[0..*index.javaTypes?size] as t>, key${t?index + 1}</#list>);
		int res = idx.search();
		if (res == 0)
			return resultRec;
		return new ${field.related.name}(store, res);
	}

	public Change${field.related.name?cap_first} add${field.name?cap_first}() {
		return new Change${field.related.name}(this, 0);
	}

	/* package private */ class Index${index.name?cap_first} extends TreeIndex<${field.related.name}> implements RecordInterface {

		public Index${index.name?cap_first}(${field.related.name} record) {
			super(record, null, ${index.flagPos}, ${index.fieldPos});
		}
<#list 1..index.javaTypes?size as i>

		public Index${index.name?cap_first}(${field.related.name} record<#list index.javaTypes[0..*i] as t>, ${t} key${t?index + 1}</#list>) {
			super(record, new Key() {
				@Override
				public int compareTo(int recNr) {
					if (recNr < 0)
						return -1;
					assert store.validate(recNr);
					record.rec(recNr);
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
		}
</#list>

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
			${field.related.name} recA = new ${field.related.name}(store, a);
			${field.related.name} recB = new ${field.related.name}(store, b);
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
		public Object java() {
			return new ${field.related.name}(store, 0);
		}

		@Override
		public RecordInterface next() {
			return null;
		}

		@Override
		public RecordInterface copy() {
			return null;
		}
	}
</#if></#list>
<#if table.parent??>

	@Override
	public ${table.parent.name?cap_first} up() {
		return new ${table.parent.name?cap_first}(store, rec == 0 ? 0 : store.getInt(rec, ${table.upPos}));
	}
</#if>
<#list table.indexes as index><#if !table.parent??>

	public class Index${index.name?cap_first} extends TreeIndex<${table.name}> implements RecordInterface {
		public Index${index.name?cap_first}() {
			super(${table.name}.this, null, ${index.flagPos}, ${index.fieldPos});
		}
<#list 1..index.javaTypes?size as i>

		public Index${index.name?cap_first}(<#list index.javaTypes[0..*i] as t>${t} key${t?index + 1}<#if t?has_next>, </#if></#list>) {
			super(${table.name}.this, new Key() {
				@Override
				public int compareTo(int recNr) {
					if (recNr < 0)
						return -1;
					assert store.validate(recNr);
					rec(recNr);
					int o = 0;
<#list index.javaTypes[0..*i] as t>
					o = RedBlackTree.compare(key${t?index + 1}, ${table.name}.this.${index.retrieve[t?index]});
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
		}
</#list>

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
			${table.name} recA = new ${table.name}(store, a);
			${table.name} recB = new ${table.name}(store, b);
			int o = 0;
<#list index.retrieve as retrieve>
			o = compare(recA.${retrieve}, recB.${retrieve});
<#if retrieve?has_next>
			if (o == 0)
				return 0;
</#if></#list> 
			return o;
		}

		@Override
		public Object java() {
			return new ${table.name}(store, 0);
		}

		@Override
		public RecordInterface next() {
			return null;
		}

		@Override
		public RecordInterface copy() {
			return null;
		}
	}
</#if></#list>

	@Override
	public void output(Write write, int iterate) {
		if (rec == 0 || iterate <= 0)
			return;
<#list table.fields as field><#if field.type == "RELATION"><#if field.name != "upRecord">
		write.field("${field.name}", get${field.name?cap_first}());
</#if><#elseif field.type == "BOOLEAN" && field.getWhen()??>
		if (get${table.condition.name?cap_first}() == ${table.condition.name?cap_first}.${field.getWhen()})
			write.field("${field.name}", is${field.name?cap_first}());
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
		if (fld${field.name?cap_first} != null && fld${field.name?cap_first}.getRec() != 0) {
			write.sub("${field.name}");
			fld${field.name?cap_first}.output(write, iterate);
			write.endSub();
		}
</#if></#list>
<#list table.includes as incl>
		output${incl.getName()}(write, iterate);
</#list>
		write.endRecord();
	}

	@Override
	public String keys() {
		StringBuilder res = new StringBuilder();
		if (rec == 0)
			return "";
<#if table.parent??>
		res.append("${table.parent.name}").append("{").append(up().keys()).append("}");
</#if>
<#if table.keyRetrieve??><#list table.keyRetrieve as key>
<#if table.parent?? || key?index gt 0>
		res.append(", ");
</#if>
		res.append("${table.keyNames[key?index]}").append("=").append(${key});
</#list></#if>
		return res.toString();
	}

	@Override
	public String toString() {
		Write write = new Write(new StringBuilder());
		output(write, 4);
		return write.toString();
	}

	public ${table.name} parse(Parser parser<#if table.parent??>, ${table.parent.name} parent</#if>) {
		while (parser.getSub()) {
${table.keyFields}<#rt>
			if (parser.isDelete(nextRec)) {
				try (Change${table.name} record = new Change${table.name}(this)) {
					store.free(record.rec());
					record.rec(0);
				}
				continue;
			}
			if (nextRec == 0) {
				try (Change${table.name} record = new Change${table.name}(<#if table.parent??>parent, 0<#else>store</#if>)) {
${table.setKeys}
					record.parseFields(parser);
					rec = record.rec;
				}
			} else {
				rec = nextRec;
				try (Change${table.name} record = new Change${table.name}(this)) {
					record.parseFields(parser);
				}
			}
		}
		return this;
	}

<#if table.includes?size &gt; 0>
	@Override
</#if>
	public boolean parseKey(Parser parser) {
${table.parseKeys}<#rt>
		parser.finishRelation();
		if (nextRec != 0)
			rec = nextRec;
		return nextRec != 0;
	}

	@Override
	public Object java() {
		int field = 0;
<#assign max = table.fields?size>
<#if max == 0 && table.includes?size == 1>
<#list table.includes as incl>
		return ${incl.name}.super.get${incl.name}(field);
</#list>
<#else>
<#list table.includes as incl>
<#assign nmax = max + incl.fields?size>
		if (field > ${max} && field <= ${nmax})
			return ${incl.name}.super.get${incl.name}(field - ${max});
<#assign max = nmax>
</#list>
</#if>
<#if max &gt; 0>
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
</#if>
	}

	public Iterable<? extends RecordInterface> iterate(int field, Object... key) {
<#assign max = table.fields?size>
<#list table.includes as incl>
<#assign nmax = max + incl.fields?size>
		if (field >= ${max} && field <= ${nmax})
			return ${incl.name}.super.iterate${incl.name}(field - ${max});
<#assign max = nmax>
</#list>
		switch (field) {
<#list table.fields as field>
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
	public FieldType type() {
		int field = 0;
<#assign max = table.fields?size>
<#if max == 0 && table.includes?size == 1>
<#list table.includes as incl>
		return ${incl.name}.super.type${incl.name}(field);
</#list>
<#else>
<#list table.includes as incl>
<#assign nmax = max + incl.fields?size>
		if (field > ${max} && field <= ${nmax})
			return ${incl.name}.super.type${incl.name}(field - ${max});
<#assign max = nmax>
</#list>
</#if>
<#if table.fields?size &gt; 0>
		switch (field) {
<#list table.fields as field>
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
</#if>
</#list>
		default:
			return null;
		}
</#if>
	}

	@Override
	public String name() {
		int field = 0;
<#assign max = table.fields?size>
<#if max == 0 && table.includes?size == 1>
<#list table.includes as incl>
		return ${incl.name}.super.name${incl.name}(field);
</#list>
<#else>
<#list table.includes as incl>
<#assign nmax = max + incl.fields?size>
		if (field > ${max} && field <= ${nmax})
			return ${incl.name}.super.name${incl.name}(field - ${max});
<#assign max = nmax>
</#list>
</#if>
<#if table.fields?size &gt; 0>
		switch (field) {
<#list table.fields as field>
		case ${1 + field?index}:
			return "${field.name}";
</#list>
		default:
			return null;
		}
</#if>
	}

	@Override
	public ${table.name?cap_first} next() {
		return null;
	}

	@Override
	public ${table.name?cap_first} copy() {
		return new ${table.name?cap_first}(store, rec);
	}
}
