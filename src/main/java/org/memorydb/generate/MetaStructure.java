package org.memorydb.generate;

public class MetaStructure {
	public static void main(String[] args) {
		Generate.project(getProject());
		System.out.println("Refresh the project to allow eclipse to see the last changes");
	}

	public static Project getProject() {
		Project project = new Project("Meta", "org.memorydb.meta", "src/main/java/org/memorydb/meta");

		Record value = project.record("Value");
		value.field("value", Type.STRING);
		value.field("description", Type.STRING);

		Record field = project.table("Field");
		field.field("name", Type.STRING).isKey();
		field.field("nr", Type.INTEGER);
		field.field("type", Type.ENUMERATE, "ARRAY", "BOOLEAN", "BYTE", "DATE", "ENUMERATE", "FLOAT", "INCLUDE",
				"INDEX", "INTEGER", "LONG", "NULL_BOOLEAN", "RELATION", "SUB", "STRING").condition().defaultValue("STRING");
		field.field("key", Type.BOOLEAN);
		field.field("mandatory", Type.BOOLEAN);
		field.field("minimum", Type.LONG);
		field.field("maximum", Type.LONG);
		field.field("format", Type.STRING);
		field.field("decimals", Type.BYTE);
		field.field("default", Type.STRING);
		field.field("condition", Type.STRING);
		field.field("description", Type.STRING);

		Record orderField = project.record("OrderField");
		orderField.field("field", Type.RELATION, field);

		Record record = project.table("Record");
		record.field("name", Type.STRING);
		record.field("fieldName", field, "name");
		record.field("fields", field, "nr");
		record.field("condition", Type.RELATION, field);
		record.field("description", Type.STRING);

		field.field("values", Type.ARRAY, value).when("ENUMERATE");
		field.field("related", Type.RELATION, record).when("RELATION");
		field.field("record", Type.RELATION, record).when("INCLUDE");
		field.field("content", Type.RELATION, record).when("ARRAY");
		field.field("child", Type.RELATION, record).when("SUB");
		field.field("to", Type.RELATION, record).when("INDEX");
		field.field("order", Type.ARRAY, orderField).when("INDEX");

		Record meta = project.table("Project");
		meta.field("records", record, "name");
		meta.field("main", Type.RELATION, record);
		meta.field("pack", Type.STRING);
		meta.field("directory", Type.STRING);
		meta.index("meta", "pack");

		return project;
	}
}
