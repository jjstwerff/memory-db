package org.memorydb.json;

import org.memorydb.generate.Generate;
import org.memorydb.generate.Project;
import org.memorydb.generate.Record;
import org.memorydb.generate.Type;

public class JsonStructure {
	public static void main(String[] args) {
		Generate.project(getProject());
		System.out.println("Refresh the project to allow eclipse to see the last changes");
	}

	public static Project getProject() {
		Project project = new Project("Data", "org.memorydb.json", "src/main/java/org/memorydb/json");

		Record value = project.record("Value");

		Record field = project.table("Field");
		field.field("name", Type.STRING);

		Record part = project.content("Part");
		part.field("type", Type.ENUMERATE, "ARRAY", "BOOLEAN", "FLOAT", "NUMBER", "NULL", "OBJECT", "STRING").defaultValue("NULL").condition();
		part.field("array", Type.ARRAY, value).when("ARRAY");
		part.field("boolean", Type.BOOLEAN).when("BOOLEAN");
		part.field("float", Type.FLOAT).when("FLOAT");
		part.field("number", Type.LONG).when("NUMBER");
		part.field("object", field, "name").when("OBJECT");
		part.field("value", Type.STRING).when("STRING");

		Record json = project.table("Json");
		field.include(part);
		value.include(part);
		json.include(part);
		return project;
	}
}
