package org.memorydb.table;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.memorydb.file.Write;
import org.memorydb.jslt.JsltInterpreter;
import org.memorydb.jslt.JsltParser;
import org.memorydb.jslt.Macro;
import org.memorydb.json.JsonParser;
import org.memorydb.structure.InputOutputException;
import org.memorydb.structure.NormalCheck;
import org.memorydb.structure.RecordInterface;
import org.memorydb.structure.Store;

public class MemoryTests extends NormalCheck {
	private int testNr;

	@Before
	public void before() {
		testNr = 1;
	}

	@Rule public TestName curTest = new TestName();

	protected void jslt(String jslt, String result) {
		jslt((RecordInterface) null, jslt, result);
	}

	protected void jslt(String source, String jslt, String result) {
		jslt(source == null ? null : JsonParser.parse(source), jslt, result);
	}

	protected void jslt(RecordInterface source, String jslt, String result) {
		Macro macro = parse(jslt);
		Write write = new Write(new StringBuilder());
		try {
			macro.output(write, 20);
		} catch (IOException e) {
			throw new InputOutputException(e);
		}
		StringBuilder code = new StringBuilder();
		code.append("Test:\n").append(jslt).append("\n\n");
		if (source != null)
			code.append("Source:\n").append(source).append("\n");
		code.append("Code:\n").append(write);
		compare(testNr++ + ".code", code.toString());
		jslt(source, macro, result);
	}

	protected void compare(String test, String text) {
		Path basePath = Paths.get(getClass().getResource("/").getFile()).getParent().getParent();
		String fileName = getClass().getName() + "_" + curTest.getMethodName() + "_" + test;
		Path dpath = basePath.resolve("target/test-classes/testResults/").resolve(fileName);
		Path apath = basePath.resolve("src/test/resources/testResults/").resolve(fileName);
		try {
			String should = "";
			if (Files.exists(apath)) {
				should = new String(Files.readAllBytes(apath));
				if (!should.equals(text))
					write(text, dpath, apath);
				Assert.assertEquals(should, text);
			} else
				write(text, dpath, apath);
		} catch (IOException e) {
			throw new InputOutputException(e);
		}
	}

	private void write(String text, Path dpath, Path apath) throws IOException {
		Files.write(dpath, text.getBytes(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
		Files.write(apath, text.getBytes(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
	}

	private static Macro parse(String jslt) {
		Store jsltStore = new Store(3);
		JsltParser.parse(jslt, jsltStore);
		Macro macro = new Macro(jsltStore);
		macro.setRec(macro.new IndexMacros("main").search());
		return macro;
	}

	private static void jslt(RecordInterface source, Macro macro, String result) {
		Store jsltStore = macro.getStore();
		String into = JsltInterpreter.interpret(jsltStore, source);
		if (!into.equals(result)) {
			Assert.assertEquals(result, into);
		}
	}
}
