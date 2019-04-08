package org.memorydb.structure;

import org.junit.Test;
import org.memorydb.generate.MetaStructure;
import org.memorydb.generate.Project;
import org.memorydb.jslt.JsltStructure;
import org.memorydb.json.JsonStructure;
import org.memorydb.meta.ProjectTests;
import org.memorydb.world.WorldStructure;

public class TestStructure extends ProjectTests {
	@Test
	public void testMetaStructure() {
		Project project = MetaStructure.getProject();
		assertContent(this, "testMetaStructure.txt", project.toString());
		validate(project);
	}

	@Test
	public void testWorldStructure() {
		Project project = WorldStructure.getProject();
		assertContent(this, "testWorldStructure.txt", project.toString());
		validate(project);
	}

	@Test
	public void testJsonStructure() {
		Project project = JsonStructure.getProject();
		assertContent(this, "testJsonStructure.txt", project.toString());
		compare(project);
	}

	@Test
	public void testJsltStructure() {
		Project project = JsltStructure.getProject();
		assertContent(this, "testJsltStructure.txt", project.toString());
		validate(project);
	}
}
