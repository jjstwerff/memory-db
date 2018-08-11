package org.memorydb.json;

import org.junit.Test;

public class TestJsonWriter extends EasyCheck {
	StringBuilder into = new StringBuilder();

	@Test
	public void testArray() {
		JsonWriter write = new JsonWriter();
		write.startArray();
		write.write(1);
		write.write(2);
		write.write("\tsome");
		write.write(null);
		write.write(true);
		write.startArray();
		write.write(false);
		write.end(2);
		write.finish();
		eq("[1,2,\"\\tsome\",null,true,[false]]", write.getWriter().toString());
	}

	@Test
	public void testObject() {
		JsonWriter write = new JsonWriter();
		write.startObject();
		write.write("name", "me");
		write.write("strëe¿", "nowhere");
		write.write("year", 2016);
		write.write("sufficient", false);
		write.startArray("rânge");
		write.write(1);
		eq("$.rânge[4][1]", write.getPath());
		write.write(5);
		write.end(2);
		write.finish();
		eq("{\"name\":\"me\",\"strëe¿\":\"nowhere\",\"year\":2016,\"sufficient\":false,\"rânge\":[1,5]}", write.getWriter().toString());
	}

	@Test
	public void testCombine() {
		JsonWriter write = new JsonWriter();
		write.startObject();
		write.startArray("something");
		write.startObject();
		write.write("name", "someone");
		write.end();
		write.startObject();
		write.write("name", "second");
		write.end(3);
		write.finish();
		eq("{\"something\":[{\"name\":\"someone\"},{\"name\":\"second\"}]}", write.getWriter().toString());
	}
}
