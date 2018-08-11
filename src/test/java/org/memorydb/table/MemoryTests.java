package org.memorydb.table;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.junit.Assert;

import org.memorydb.file.Write;
import org.memorydb.jslt.JsltInterpreter;
import org.memorydb.jslt.JsltParser;
import org.memorydb.jslt.Macro;
import org.memorydb.jslt.Source;
import org.memorydb.jslt.Source.IndexSources;
import org.memorydb.structure.InputOutputException;
import org.memorydb.structure.NormalCheck;
import org.memorydb.structure.RecordInterface;
import org.memorydb.structure.Store;

public class MemoryTests extends NormalCheck {
	protected void compare(String test, String text) {
		Path basePath = Paths.get(getClass().getResource("/").getFile()).getParent().getParent();
		Path dpath = basePath.resolve("target/test-classes/testResults/").resolve(getClass().getName() + "." + test);
		Path apath = basePath.resolve("src/test/resources/testResults/").resolve(getClass().getName() + "." + test);
		try {
			String should = "";
			if (Files.exists(apath))
				should = new String(Files.readAllBytes(apath));
			if (!should.equals(text))
				write(text, dpath, apath);
			Assert.assertEquals(should, text);
		} catch (IOException e) {
			throw new InputOutputException(e);
		}
	}

	private void write(String text, Path dpath, Path apath) throws IOException {
		Files.write(dpath, text.getBytes(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
		Files.write(apath, text.getBytes(), StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
	}

	protected static void jslt(RecordInterface source, String jslt, String result, StringBuilder code) {
		Store jsltStore = new Store(3);
		JsltParser.parse(jslt, jsltStore);
		Macro macro = new Macro(jsltStore);
		macro.setRec(macro.new IndexMacros("main").search());
		Write write = new Write(new StringBuilder());
		try {
			macro.output(write, 20);
		} catch (IOException e) {
			throw new InputOutputException(e);
		}
		code.append("Test:\n").append(jslt).append("\n");
		code.append("Code:\n");
		code.append(write);
		Source s = new Source(jsltStore);
		IndexSources indexSources = s.new IndexSources("$");
		int search = indexSources.search();
		if (search > 0) {
			code.append("Source:");
			try {
				s.setRec(search);
				write = new Write(new StringBuilder());
				s.output(write, 20);
			} catch (IOException e) {
				throw new InputOutputException(e);
			}
			code.append(write);
		}
		code.append("\n");
		String into = JsltInterpreter.interpret(jsltStore, source);
		if (!into.equals(result)) {
			System.out.println(code.toString());
			Assert.assertEquals(result, into);
		}
	}
}
