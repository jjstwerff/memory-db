package org.memorydb.generate;

public class MetaStructure {
	public static void main(String[] args) {
		Generate.project(getProject());
		System.out.println("Refresh the project to allow eclipse to see the last changes");
	}

	public static Project getProject() {
		Project project = new Project("Meta", "org.memorydb.meta", "src/main/java/org/memorydb/meta");

		Record str = project.record("Str");
		str.field("str", Type.STRING);

		Record index = project.table("Index");
		index.field("name", Type.STRING);
		index.field("indexFields", Type.ARRAY, str);
		index.field("fieldPos", Type.INTEGER);
		index.field("flagPos", Type.INTEGER);
		index.field("parentPos", Type.INTEGER);
		index.field("primary", Type.BOOLEAN);

		Record field = project.table("Field");
		field.field("name", Type.STRING);
		field.field("type", Type.ENUMERATE, "ARRAY", "BOOLEAN", "BYTE", "DATE", "ENUMERATE", "FLOAT", "INTEGER", "LONG", "NULL_BOOLEAN", "RELATION", "SET", "SUB", "STRING")
				.defaultValue("STRING");
		field.field("auto", Type.BOOLEAN);
		field.field("pos", Type.INTEGER).mandatory();
		field.field("index", Type.RELATION, index);
		field.field("values", Type.ARRAY, str);
		field.field("key", Type.BOOLEAN);
		field.field("mandatory", Type.BOOLEAN);
		field.field("default", Type.STRING);
		field.field("description", Type.STRING);

		Record freeBits = project.table("FreeBits");
		freeBits.field("size", Type.INTEGER);
		freeBits.field("pos", Type.INTEGER);

		Record recField = project.record("RecField");
		recField.field("field", Type.RELATION, field);

		Record setIndex = project.table("SetIndex");
		setIndex.field("index", Type.RELATION, index);

		Record record = project.table("Record");
		record.field("name", Type.STRING);
		record.field("fields", Type.ARRAY, recField);
		record.field("fieldOnName", field, "name");
		record.field("setIndexes", setIndex, "index.name");
		record.field("freeBits", freeBits, "size");
		record.field("parent", Type.RELATION, record);
		record.field("size", Type.INTEGER).mandatory();
		record.field("related", Type.BOOLEAN);
		record.field("full", Type.BOOLEAN);
		record.field("description", Type.STRING);
		index.field("record", Type.RELATION, record);
		field.field("related", Type.RELATION, record);

		Record meta = project.table("Project");
		meta.field("name", Type.STRING);
		meta.field("pack", Type.STRING);
		meta.field("indexes", index, "name");
		meta.field("records", record, "name");
		meta.field("dir", Type.STRING);
		meta.index("meta", "name");

		return project;
	}
}
