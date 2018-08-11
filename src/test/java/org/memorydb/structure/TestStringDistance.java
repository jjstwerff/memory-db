package org.memorydb.structure;

import org.junit.Test;

import junit.framework.Assert;

public class TestStringDistance {
	@Test
	public void testDistances() {
		StringDistance d = new StringDistance();
		Assert.assertEquals(3, d.distance("sitting", "kitten"));
		Assert.assertEquals(3, d.distance("Sunday", "Saturday"));
		Assert.assertEquals(1, d.distance("turnover", "tunrover"));
		Assert.assertEquals(2, d.distance("turnover", "rurnovet"));
	}
}
