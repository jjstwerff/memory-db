package org.memorydb.json;

import org.junit.Test;
import org.memorydb.file.Write;
import org.memorydb.json.Part.Type;
import org.memorydb.structure.NormalCheck;
import org.memorydb.structure.RecordInterface;
import org.memorydb.structure.RecordInterface.FieldType;
import org.memorydb.structure.Store;

import junit.framework.Assert;

public class TestJson extends NormalCheck {
	@Test
	public void testTypes() {
		Assert.assertEquals("type=NUMBER, number=123", parse("123"));
		Assert.assertEquals("type=FLOAT, float=123.456", parse("123.456"));
		Assert.assertEquals("type=STRING, value=123 4", parse("\"123 4\""));
		Assert.assertEquals("type=BOOLEAN, boolean=true", parse("true"));
		Assert.assertEquals("type=STRING, value=Ꮥⲣéçïḁ₤", parse("\"Ꮥⲣéçïḁ₤\""));
		Assert.assertEquals("type=ARRAY, array=[\n  type=NUMBER, number=123\n]", parse("[123]"));
		Assert.assertEquals("type=ARRAY, array=[\n  type=NUMBER, number=123\n  type=FLOAT, float=23.45\n  type=STRING, value=s\"\n]", parse("[123, 23.45, \"s\\\"\"]"));
		Assert.assertEquals(
				"type=OBJECT, object=[\n  name=a, type=NUMBER, number=1\n  name=b, type=FLOAT, float=12.3\n  name=c, type=STRING, value=d\n  name=d, type=BOOLEAN, boolean=false\n]",
				parse("{\"a\":1, \"b\":12.3, \"c\":\"d\", \"d\":false}"));
		Assert.assertEquals("type=OBJECT, object=[\n  name=a, type=ARRAY, array=[\n    type=NUMBER, number=1\n    type=FLOAT, float=2.2\n  ]\n]", parse("{\"a\":[1, 2.2]}"));
		Assert.assertEquals("type=ARRAY, array=[\n  type=OBJECT, object=[\n    name=a, type=NUMBER, number=1\n  ]\n"
				+ "  type=OBJECT, object=[\n    name=a, type=FLOAT, float=1.2\n  ]\n  type=NUMBER, number=3\n  type=NULL\n]", parse("[{\"a\":1}, {\"a\":1.2}, 3, null]"));
		Assert.assertEquals("type=OBJECT, object=[\n  name=a, type=OBJECT, object=[\n    name=a, type=NUMBER, number=1\n  ]\n]", parse("{\"a\":{\"a\":1}}"));
		Assert.assertEquals(
				"type=ARRAY, array=[\n  type=ARRAY, array=[\n    type=NUMBER, number=1\n    type=NUMBER, number=2\n  ]\n"
						+ "  type=ARRAY, array=[\n    type=NUMBER, number=1\n    type=NUMBER, number=3\n  ]\n"
						+ "  type=ARRAY, array=[\n    type=NUMBER, number=1\n    type=OBJECT, object=[\n      name=a, type=NUMBER, number=4\n    ]\n  ]\n]",
				parse("[[1, 2], [1, 3], [1, {\"a\":4}]]"));
	}

	@Test
	public void testIterate() {
		Assert.assertEquals("123", iterate("123"));
		Assert.assertEquals("\"123 4\"", iterate("\"123 4\""));
		Assert.assertEquals("true", iterate("true"));
		Assert.assertEquals("[123]", iterate("[123]"));
		Assert.assertEquals("{\"a\":1,\"b\":12.3,\"c\":\"d\",\"d\":false}", iterate("{\"a\":1, \"b\":12.3, \"c\":\"d\", \"d\":false}"));
		Assert.assertEquals("[[1,2],[1,3],[1,{\"a\":4}]]", iterate("[[1, 2], [1, 3], [1, {\"a\":4}]]"));
		Assert.assertEquals("{\"a\":[true]}", iterate("{\"a\":[true]}"));
	}

	private String parse(String json) {
		Json res = json(json);
		StringBuilder wr = new StringBuilder();
		res.output(new Write(wr), 10);
		if (wr.length() == 0)
			return "";
		return wr.toString().substring(0, wr.length() - 1);
	}

	private String iterate(String text) {
		JsonWriter write = new JsonWriter();
		iterate(write, null, json(text));
		return write.getWriter().toString();
	}

	private void iterate(JsonWriter write, String name, RecordInterface rec) {
		if (rec.type() != FieldType.OBJECT) {
			nonObject(write, rec);
			return;
		}
		if (name == null)
			write.startObject();
		else
			write.startObject(name);
		RecordInterface elm = rec.start();
		while (elm != null) {
			if (elm.type() == FieldType.OBJECT)
				iterate(write, elm.name(), (RecordInterface) elm.java());
			else
				nonObject(write, elm);
			elm = elm.next();
		}
		write.end();
	}

	private void nonObject(JsonWriter write, RecordInterface rec) {
		if (rec.type() == FieldType.ARRAY) {
			if (rec.name() == null)
				write.startArray();
			else
				write.startArray(rec.name());
			RecordInterface elm = rec.start();
			while (elm != null) {
				iterate(write, null, elm);
				elm = elm.next();
			}
			write.end();
		} else
			write.write(rec.name(), rec.java());
	}

	private Json json(String json) {
		JsonReader read = new JsonReader(json);
		Store store = new Store(3);
		Json res = new Json(store, store.allocate(Json.RECORD_SIZE));
		try (ChangeJson ch = new ChangeJson(res)) {
			readJson(read, read.getType(), ch);
		}
		read.finish();
		return res;
	}

	private void readJson(JsonReader read, JsonType type, ChangePart ch) {
		switch (type) {
		case ARRAY:
			ch.setType(Type.ARRAY);
			ArrayArray arr = ch.getArray();
			for (JsonType ntp : read.startArray()) {
				ArrayArray elm = arr.add();
				readJson(read, ntp, elm);
			}
			read.end();
			break;
		case FALSE:
			ch.setType(Type.BOOLEAN);
			ch.setBoolean(false);
			break;
		case NULL:
			ch.setType(Type.NULL);
			break;
		case NUMBER:
			if (read.isRoundNumber()) {
				ch.setType(Type.NUMBER);
				ch.setNumber(read.parseLong());
			} else {
				ch.setType(Type.FLOAT);
				ch.setFloat(read.parseDouble());
			}
			break;
		case OBJECT:
			ch.setType(Type.OBJECT);
			for (JsonType ntp : read.startObject()) {
				try (ChangeField elm = new ChangeField(ch, 0)) {
					elm.setName(read.getField().toString());
					readJson(read, ntp, elm);
				}
			}
			read.end();
			break;
		case STRING:
			ch.setType(Type.STRING);
			ch.setValue(read.parseString());
			break;
		case TRUE:
			ch.setType(Type.BOOLEAN);
			ch.setBoolean(true);
			break;
		default:
			break;
		}
	}
}
