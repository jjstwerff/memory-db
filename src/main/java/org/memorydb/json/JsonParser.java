package org.memorydb.json;

import org.memorydb.json.Part.Type;
import org.memorydb.structure.Store;

public class JsonParser {
	private JsonParser() {
		// nothing
	}

	public static Json parse(String source) {
		JsonReader read = new JsonReader(source);
		Store store = new Store(3);
		Json res = new Json(store, store.allocate(Json.RECORD_SIZE));
		try (ChangeJson ch = new ChangeJson(res)) {
			readJson(read, read.getType(), ch);
		}
		read.finish();
		return res;
	}

	private static void readJson(JsonReader read, JsonType type, ChangePart ch) {
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

	public static String write(Part part) {
		Write write = new Write();
		write.wr(part);
		return write.writer.getWriter().toString();
	}

	static class Write {
		JsonWriter writer = new JsonWriter();

		private void wr(Part part) {
			wr(null, part);
		}

		private void wr(String name, Part part) {
			Type type = part.getType();
			if (type == null)
				return;
			switch (type) {
			case ARRAY:
				if (name == null)
					writer.startArray();
				else
					writer.startArray(name);
				for (ArrayArray a : part.getArray())
					wr(a);
				writer.end();
				break;
			case BOOLEAN:
				writer.write(name, part.isBoolean());
				break;
			case FLOAT:
				writer.write(name, part.getFloat());
				break;
			case NULL:
				writer.write(name, null);
				break;
			case NUMBER:
				writer.write(name, part.getNumber());
				break;
			case OBJECT:
				if (name == null)
					writer.startObject();
				else
					writer.startObject(name);
				for (Field o : part.getObject()) {
					wr(o.getName(), o);
				}
				writer.end();
				break;
			case STRING:
				writer.write(name, part.getValue());
				break;
			default:
				break;
			}
		}
	}
}
