package org.memorydb.json;

import org.junit.Assert;

public class EasyCheck {
	void eq(String expected, String actual) {
		Assert.assertEquals(expected, actual);
	}

	public void eq(Object expected, Object actual) {
		Assert.assertEquals(expected, actual);
	}

	public void eq(boolean expected, boolean actual) {
		Assert.assertEquals(expected, actual);
	}
}
