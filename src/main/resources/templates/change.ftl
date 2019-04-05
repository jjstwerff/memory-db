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
public class Change${table.name} extends ${table.name} implements <#if !table.object>AutoCloseable, </#if><#if table.includes?size == 0>ChangeInterface</#if><#list table.includes as incl>Change${incl.name}<#if incl?has_next>, </#if></#list> {
<#if table.parent??>
	/* package private */ Change${table.name}(${table.parent.name} parent, int rec) {
		super(parent.store(), rec == 0 ? parent.store().allocate(${table.name}.RECORD_SIZE) : rec);
		if (rec == 0) {
<#list table.includes as incl>
			default${incl.name}();
</#list>
<#list table.fields as field><#if field.default??>
			set${field.name?cap_first}(${field.default});
</#if><#if field.type == "SET" || field.type == "ARRAY">
			store.setInt(rec(), ${field.pos / 8}, 0); // ${field.type} ${field.name}
</#if></#list>
			up(parent);
		} else {
<#list table.indexes as index><#if table.parent.included?size gt 0>
			new ${table.parent.name}.Index${index.name?cap_first}(this).remove(rec);
<#else>
			up().new Index${index.name?cap_first}().remove(rec);
</#if></#list>
		}
	}

	/* package private */ Change${table.name}(${table.name} current) {
		super(current.store, current.rec);
		if (rec != 0) {
<#list table.indexes as index><#if table.parent.included?size gt 0>
			new ${table.parent.name}.Index${index.name?cap_first}(this).remove(rec);
<#else>
			up().new Index${index.name?cap_first}().remove(rec);
</#if></#list>
		}
	}
<#else>
	public Change${table.name}(Store store, int rec) {
		super(store, rec == 0 ? store.allocate(${table.name}.RECORD_SIZE) : rec);
		if (rec == 0) {
<#list table.fields as field><#if field.default??>
			set${field.name?cap_first}(${field.default});
</#if><#if field.type == "SET">
			store.setInt(rec, ${field.pos / 8}, 0); // SET ${field.name}
</#if><#if field.type == "ARRAY">
			store.setInt(rec, ${field.pos / 8}, 0); // ARRAY ${field.name}
			store.setInt(rec, ${(field.pos / 8) + 4}, 0);
</#if></#list>
<#list table.includes as incl>
			default${incl.name}();
</#list>
		} else {
<#list table.indexes as index>
			new Index${index.name?cap_first}(store).remove(rec());
</#list>
		}
	}

	public Change${table.name}(${table.name} current) {
		super(current.store(), current.rec());
<#list table.indexes as index>
		new Index${index.name?cap_first}(store).remove(rec());
</#list>
	}
</#if>
<#list table.fields as fld>
<#if fld.type == "ARRAY">

	public void move${fld.name?cap_first}(Change${table.name} other) {
		store().setInt(rec(), ${fld.pos / 8}, store().getInt(other.rec(), ${fld.pos / 8}));
		store().setInt(other.rec(), ${fld.pos / 8}, 0);
	}
</#if><#if fld.setter??>

	public void set${fld.name?cap_first}(${fld.setType} value) {
		${fld.setter}
	}
</#if></#list>
<#if table.parent??>

	private void up(${table.parent.name?cap_first} value) {
		store.setInt(rec, ${table.upPos}, value == null ? 0 : value.rec());
		store.setInt(rec, ${5 + table.upPos}, value == null ? 0 : value.index());
<#if table.parent.included?size gt 0>
		byte type = 0;
<#assign nr=1>
<#list table.parent.included as incl><#if incl.full>
		if (value instanceof ${incl.name})
			type = ${nr};<#assign nr=nr+1>
<#else><#list incl.linked as link>
		if (value instanceof ${link.upperName}Array)
			type = ${nr};<#assign nr=nr+1>
</#list></#if></#list>
		store.setByte(rec, ${4 + table.upPos}, type);
</#if>
	}
</#if>

	/* package private */ void parseFields(<#if table.noFields>@SuppressWarnings("unused") </#if>Parser parser) {
<#if table.noFields>
		// empty
</#if>
${table.otherFields}<#rt>
<#list table.includes as incl>
		parse${incl.name}(parser);
</#list>
	}

<#if !table.object>
	@Override
	public void close() {
<#list table.indexes as index><#if table.parent?? && table.parent.included?size gt 0>
		new Part.Index${index.name?cap_first}(<#if table.parent??>this</#if>).insert(rec());
<#else>
		<#if table.parent??>up().</#if>new Index${index.name?cap_first}(<#if !table.parent??>store</#if>).insert(rec());
</#if></#list>
<#if table.indexes?size == 0>
		// nothing yet
</#if>
	}

</#if>
	@Override
	public boolean java(Object value) {
		int field = 0;
<#assign max = table.fields?size>
<#list table.includes as incl>
<#assign nmax = max + incl.fields?size>
		if (field > ${max} && field <= ${nmax})
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
	public ChangeInterface add() {
		int field = 0;
<#assign max = table.fields?size>
<#list table.includes as incl>
<#assign nmax = max + incl.fields?size>
		if (field > ${max} && field <= ${nmax})
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

	@Override
	public Change${table.name} copy(int newRec) {
		assert store.validate(newRec);
		return new Change${table.name}(<#if table.parent??>up()<#else>store</#if>, newRec);
	}
}
