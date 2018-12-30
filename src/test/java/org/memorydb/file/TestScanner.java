package org.memorydb.file;

import java.nio.file.FileSystems;

import org.junit.Assert;
import org.junit.Test;

public class TestScanner extends ErrorCheck {
	@Test
	public void testScanning() {
		StringBuilder str = new StringBuilder();
		try (Scanner scanner = new Scanner(
				FileSystems.getDefault().getPath(file("scanner.txt", "this iç\tan exámple\n#comment line\n# second comment\n  with multiple lines\n  and ëxtends")))) {
			scanner.expect("this");
			scanner.expect("iç");
			str.setLength(0);
			scanner.field(str);
			Assert.assertEquals("an", str.toString());
			scanner.skipWhiteSpace();
			str.setLength(0);
			scanner.data(str);
			Assert.assertEquals("exámple", str.toString());
			scanner.skipRemarks();
			str.setLength(0);
			scanner.multiLine(str, 1);
			Assert.assertEquals("with multiple lines\nand ëxtends", str.toString());
		}
		try (Scanner scanner = new Scanner(FileSystems.getDefault().getPath(file("comment.txt", "# a line with a comment\nthis is an example")))) {
			scanner.skipRemarks();
			scanner.expect("this is an example");
		}
	}

	@Test
	public void testNewLineError() {
		expectMessage("Expecting a newline in /tmp/errors.txt:1/1");
		try (Scanner scanner = new Scanner(FileSystems.getDefault().getPath(file("errors.txt", "a line of data")))) {
			scanner.newLine();
		}
	}

	@Test
	public void testIdentifierError2() {
		StringBuilder str = new StringBuilder();
		try (Scanner scanner = new Scanner(FileSystems.getDefault().getPath(file("errors.txt", "# a comment line\n")))) {
			Assert.assertFalse(scanner.field(str));
		}
	}

	@Test
	public void testExpect() {
		expectMessage("Expecting: start in /tmp/errors.txt:1/1");
		try (Scanner scanner = new Scanner(FileSystems.getDefault().getPath(file("errors.txt", "")))) {
			scanner.expect("start");
		}
	}
}
