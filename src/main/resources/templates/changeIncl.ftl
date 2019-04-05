package ${project.package};

import org.memorydb.file.Parser;
<#if table.parent??><#if table.parent.included?size gt 0>
import org.memorydb.structure.RecordInterface;
</#if>
</#if>
<#list table.changeImports as import>
import ${import};
</#list>
import org.memorydb.structure.ChangeInterface;

/**
 * Automatically generated record class for interface ${table.name}
 */
public interface Change${table.name} extends <#if table.includes?size == 0>ChangeInterface, </#if>${table.name}<#list table.includes as incl>, Change${incl.name}</#list> {
	public default void default${table.name}() {
<#list table.fields as field><#if !field.whenCond??><#if field.default??>
		set${field.name?cap_first}(${field.default});
</#if><#if field.type == "SET">
		store.setInt(rec, ${field.pos / 8}, 0); // SET ${field.name}
</#if><#if field.type == "ARRAY">
		store.setInt(rec, ${field.pos / 8}, 0); // ARRAY ${field.name}
		store.setInt(rec, ${(field.pos / 8) + 4}, 0);
</#if></#if></#list>
	}
<#list table.fields as fld>
<#if fld.type == "ARRAY">

	default void move${fld.name?cap_first}(Change${table.name} other) {
		store().setInt(rec(), ${table.name?lower_case}Position() + ${fld.pos / 8}, store().getInt(other.rec(), other.${table.name?lower_case}Position() + ${fld.pos / 8}));
		store().setInt(other.rec(), other.${table.name?lower_case}Position() + ${fld.pos / 8}, 0);
	}
</#if><#if fld.setter??>

	default void set${fld.name?cap_first}(${fld.setType} value) {
		${fld.setter}
	}
</#if></#list>

	default void parse${table.name}(Parser parser) {
${table.otherFields}<#rt>
	}

	default boolean set${table.name}(int field, Object value) {
<#assign max = table.fields?size>
<#list table.includes as incl>
<#assign nmax = max + incl.fields?size>
		if (field > ${max} && field <= ${nmax})
			return set${incl.name}(field - ${max}, value);
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

	default ChangeInterface add${table.name}(int field) {
<#assign max = table.fields?size>
<#list table.includes as incl>
<#assign nmax = max + incl.fields?size>
		if (field > ${max} && field <= ${nmax})
			return add${incl.name}(field - ${max});
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