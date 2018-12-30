package org.memorydb.structure;

import org.junit.Ignore;
import org.junit.Test;
import org.memorydb.generate.MetaStructure;
import org.memorydb.generate.Project;
import org.memorydb.jslt.JsltStructure;
import org.memorydb.json.JsonStructure;
import org.memorydb.meta.ProjectTests;

public class TestStructure extends ProjectTests {
	@Test
	public void testMetaStructure() {
		Project project = MetaStructure.getProject();
		assertContent(this, "testMetaStructure.txt", project.toString());
		validate(project);
	}

	@Test
	@Ignore("The generated json code is deliberately different. Allow to override methods in the future.")
	public void testJsonStructure() {
		Project project = JsonStructure.getProject();
		assertContent(this, "testJsonStructure.txt", project.toString());
		validate(project);
	}

	@Test
	public void testJsltStructure() {
		Project project = JsltStructure.getProject();
		assertContent(this, "testJsltStructure.txt", project.toString());
		validate(project);
	}
}
