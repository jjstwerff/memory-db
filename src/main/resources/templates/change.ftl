package ${project.package};

import org.memorydb.file.Parser;
<#if table.parent??><#else>
import org.memorydb.structure.Store;
</#if>
import org.memorydb.structure.ChangeInterface;
<#list table.changeImports as import>
import ${import};
</#list>

/**
 * Automatically generated record class for table ${table.name}
 */
public class Change${table.name} extends ${table.name} implements <#if table.includes?size == 0>ChangeInterface</#if><#list table.includes as incl>Change${incl.name}<#if incl?has_next>, </#if></#list> {
<#if table.parent??>
	/* package private */ Change${table.name}(${table.parent.name} parent, int rec) {
		super(parent.getStore(), rec);
		if (rec == 0) {
			setRec(getStore().allocate(${table.name}.RECORD_SIZE));
<#list table.includes as incl>
			default${incl.name}();
</#list>
		}
<#list table.fields as field><#if field.default??>
		set${field.name?cap_first}(${field.default});
</#if><#if field.type == "SET" || field.type == "ARRAY">
		store.setInt(getRec(), ${field.pos / 8}, 0); // ${field.type} ${field.name}
</#if></#list>
		if (rec != 0) {
<#list table.indexes as index><#if table.parent.included?size gt 0>
			new ${table.parent.name}.Index${index.name?cap_first}(getUpRecord(), this).remove(rec);
<#else>
			getUpRecord().new Index${index.name?cap_first}(this).remove(rec);
</#if></#list>
		}
	}

	/* package private */ Change${table.name}(${table.name} current) {
		super(current.store, current.rec);
		if (rec != 0) {
<#list table.indexes as index><#if table.parent.included?size gt 0>
			new ${table.parent.name}.Index${index.name?cap_first}(getUpRecord(), this).remove(rec);
<#else>
			getUpRecord().new Index${index.name?cap_first}(this).remove(rec);
</#if></#list>
		}
	}
<#else>
	public Change${table.name}(Store store) {
		super(store, store.allocate(${table.name}.RECORD_SIZE));
<#list table.fields as field><#if field.default??>
		set${field.name?cap_first}(${field.default});
</#if><#if field.type == "SET">
		store.setInt(rec, ${field.pos / 8}, 0); // SET ${field.name}
</#if><#if field.type == "ARRAY">
		store.setInt(rec, ${field.pos / 8}, 0); // ARRAY ${field.name}
		store.setInt(rec, ${(field.pos / 8) + 4}, 0);
</#if></#list>
	}

	public Change${table.name}(${table.name} current) {
		super(current.getStore(), current.getRec());
<#list table.indexes as index>
		new Index${index.name?cap_first}().remove(getRec());
</#list>
	}
</#if>
<#list table.fields as fld>
<#if fld.type == "ARRAY">

	public void move${fld.name?cap_first}(Change${table.name} other) {
		getStore().setInt(getRec(), ${fld.pos / 8}, getStore().getInt(other.getRec(), ${fld.pos / 8}));
		getStore().setInt(other.getRec(), ${fld.pos / 8}, 0);
	}
</#if><#if fld.setter??>

	public void set${fld.name?cap_first}(${fld.setType} value) {
		${fld.setter}
	}
</#if></#list>

	/* package private */ void parseFields(<#if table.noFields>@SuppressWarnings("unused") </#if>Parser parser) {
<#if table.noFields>
		// empty
</#if>
${table.otherFields}<#rt>
<#list table.includes as incl>
		parse${incl.name}(parser);
</#list>
	}

	@Override
	public void close() {
<#list table.indexes as index><#if table.parent?? && table.parent.included?size gt 0>
		new Part.Index${index.name?cap_first}(getUpRecord(), <#if table.parent??>this</#if>).insert(getRec());
<#else>
		<#if table.parent??>getUpRecord().</#if>new Index${index.name?cap_first}(<#if table.parent??>this</#if>).insert(getRec());
</#if></#list>
<#if table.indexes?size == 0>
		// nothing yet
</#if>
	}

	@Override
	public boolean set(int field, Object value) {
<#assign max = table.fields?size>
<#list table.includes as incl>
<#assign nmax = max + incl.fields?size>
		if (field >= ${max} && field <= ${nmax})
			return Change${incl.name}.super.set${incl.name}(field - ${max}, value);
<#assign max = nmax>
</#list>
		switch (field) {
<#list table.fields as field>
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
<#assign max = table.fields?size>
<#list table.includes as incl>
<#assign nmax = max + incl.fields?size>
		if (field >= ${max} && field <= ${nmax})
			return Change${incl.name}.super.add${incl.name}(field - ${max});
<#assign max = nmax>
</#list>
		switch (field) {
<#list table.fields as field>
<#if field.type == "SET" || field.type == "ARRAY">
		case ${field?index + 1}:
			return add${field.name?cap_first}();
</#if></#list>
		default:
			return null;
		}
	}
}