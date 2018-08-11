package org.memorydb.structure;

import org.junit.Test;
import org.memorydb.generate.MetaStructure;
import org.memorydb.generate.Project;
import org.memorydb.jslt.JsltStructure;
import org.memorydb.json.JsonStructure;
import org.memorydb.meta.ProjectTests;

import junit.framework.Assert;

public class TestStructure extends ProjectTests {
	@Test
	public void testMetaStructure() {
		Project project = MetaStructure.getProject();
		Assert.assertEquals(content(this, "testMetaStructure.txt"), project.toString());
		validate(project);
	}

	@Test
	public void testJsonStructure() {
		Project project = JsonStructure.getProject();
		Assert.assertEquals(content(this, "testJsonStructure.txt"), project.toString());
		validate(project);
	}

	@Test
	public void testJsltStructure() {
		Project project = JsltStructure.getProject();
		Assert.assertEquals(content(this, "testJsltStructure.txt"), project.toString());
		validate(project);
	}
}
