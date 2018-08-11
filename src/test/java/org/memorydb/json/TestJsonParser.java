package org.memorydb.json;

import org.junit.Assert;
import org.junit.Test;

public class TestJsonParser {
	private final StringBuilder into = new StringBuilder();

	private void eq(String expected, String actual) {
		Assert.assertEquals(expected, actual);
	}

	private void eq(Object expected, Object actual) {
		Assert.assertEquals(expected, actual);
	}

	private void eq(boolean expected, boolean actual) {
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testArray() {
		JsonReader read = new JsonReader("[1, 2, \"\\tsome\", null, true, [false]]");
		Assert.assertEquals(JsonType.ARRAY, read.next());
		read.startArray();
		eq(JsonType.NUMBER, read.next());
		eq(1, read.parseInt());
		eq(true, read.hasNext());
		eq(JsonType.NUMBER, read.next());
		eq(2, read.parseInt());
		eq(true, read.hasNext());
		eq(JsonType.STRING, read.next());
		read.parseString(into);
		eq("\tsome", into.toString());
		eq(true, read.hasNext());
		eq(JsonType.NULL, read.next());
		eq(true, read.hasNext());
		eq(JsonType.TRUE, read.next());
		eq("$[4]", read.getPath());
		eq(true, read.hasNext());
		eq(JsonType.ARRAY, read.next());
		read.startArray();
		eq(JsonType.FALSE, read.next());
		eq(false, read.hasNext());
		eq("$[5][0]", read.getPath());
		read.end();
		eq(false, read.hasNext());
		read.end();
		read.finish();
	}

	@Test
	public void testObject() {
		JsonReader read = new JsonReader("{\"name\":\"me\", \"s\\u0074reet\":\"n\\u006fwhere\", \"year\":2016, \"sufficient\":false, \"range\":[1,5]}");
		eq(JsonType.OBJECT, read.next());
		read.startObject();
		eq(JsonType.STRING, read.next());
		read.parseString(into);
		eq("name", read.getField().toString());
		eq("me", into.toString());
		into.setLength(0);
		eq(JsonType.STRING, read.next());
		read.parseString(into);
		eq("street", read.getField().toString());
		eq("nowhere", into.toString());
		eq(JsonType.NUMBER, read.next());
		eq(2016, read.parseInt());
		eq(JsonType.FALSE, read.next());
		eq(JsonType.ARRAY, read.next());
		read.startArray();
		eq("$.range[4][0]", read.getPath());
		eq(JsonType.NUMBER, read.next());
		eq(1, read.parseInt());
		eq(JsonType.NUMBER, read.next());
		eq(5, read.parseInt());
		eq(JsonType.END, read.next());
		read.end();
		eq(JsonType.END, read.next());
		read.end();
	}
}
