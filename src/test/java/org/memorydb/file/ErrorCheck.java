package org.memorydb.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.hamcrest.Description;
import org.junit.Rule;
import org.junit.internal.matchers.TypeSafeMatcher;
import org.junit.rules.ExpectedException;

import org.memorydb.structure.NormalCheck;

import junit.framework.Assert;

public class ErrorCheck extends NormalCheck {
	@Rule
	public final ExpectedException exception = ExpectedException.none();

	private class Match extends TypeSafeMatcher<String> {
		private final String message;

		public Match(String message) {
			this.message = message;
		}

		@Override
		public void describeTo(Description arg) {
			arg.appendText("message equals ").appendText(message);
		}

		@Override
		public boolean matchesSafely(String item) {
			Assert.assertEquals(message, item);
			return item.equals(message);
		}
	}

	protected void expectError(Class<? extends Throwable> error, String message) {
		exception.expect(error);
		exception.expectMessage(new Match(message));
	}

	void expectMessage(String message) {
		exception.expect(StructureError.class);
		exception.expectMessage(new Match(message));
	}

	static String file(String fileName, String... lines) {
		String f = "/tmp/" + fileName;
		File file = new File(f);
		if (file.isFile())
			file.delete();
		try (BufferedWriter out = new BufferedWriter(new FileWriter(file))) {
			for (String line : lines)
				out.write(line + "\n");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return f;
	}
}
