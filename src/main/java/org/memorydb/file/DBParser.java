package org.memorydb.file;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.memorydb.file.Scanner.State;
import org.memorydb.structure.StringPointer;

/**
 * Intelligent parser of a record.
 * - read all record fields + positions.. skip sub records initially
 * - keep list of rule starts & indents & continues .. stop when no indent is found
 * TODO allow remove of records with scanner.peek("!");
 * TODO check for double records with the same key
 */

public class DBParser implements Parser {
	private final Scanner scanner; // token scanner for file format
	private final List<Line> lines;
	private final List<Level> levels;
	private final StringBuilder buffer;
	private final List<Todo> todo;
	private State lineState;
	private boolean delete;

	public DBParser(String fileName) {
		scanner = new Scanner(Paths.get(fileName));
		lines = new ArrayList<>();
		levels = new ArrayList<>();
		lineState = scanner.getState();
		levels.add(new Level(0, 0));
		buffer = new StringBuilder();
		todo = new ArrayList<>();
	}

	private static class Line {
		private State start;
		private int indent;
		private boolean continues;
		private boolean closedSub;

		@Override
		public String toString() {
			return "[start=" + start + ", indent=" + indent + ", continues=" + continues + ", closedSub=" + closedSub + "]";
		}
	}

	private static class Level {
		private Map<String, State> fields;
		private int indent;
		private int line;

		Level(int indent, int line) {
			fields = new LinkedHashMap<>();
			this.indent = indent;
			this.line = line;
		}

		@Override
		public String toString() {
			return "[fields=" + fields + ", indent=" + indent + ", line=" + line + "]";
		}
	}

	private static class Todo {
		private final State state; // position of the data in the scanner
		private final ScanRelation scan; // code to scan this data again
		private final int rec; // record to write to
		private final int idx; // possible index to write to

		Todo(State state, ScanRelation scan, int rec, int idx) {
			this.state = state;
			this.scan = scan;
			this.rec = rec;
			this.idx = idx;
		}

		@Override
		public String toString() {
			return state.toString();
		}
	}

	private void scanLines() {
		scanner.setState(lineState);
		while (scanner.peek("#") || scanner.peek("\n"))
			scanner.skipRemarks();
		lines.clear();
		while (true) {
			scanLine();
			while (!scanner.peek("\n") && !scanner.eof())
				scanner.skip();
			if (scanner.eof()) {
				lineState = scanner.getState();
				return;
			}
			scanner.newLine();
			while (scanner.peek("#") || scanner.peek(" #") || scanner.peek("\n"))
				scanner.skipRemarks();
			if (!scanner.peek("  ") && !scanner.peek("& ") && !scanner.peek("]")) {
				lineState = scanner.getState();
				return;
			}
		}
	}

	private void scanLine() {
		Line line = new Line();
		line.indent = 0;
		while (scanner.peek("  ")) {
			scanner.skip();
			scanner.skip();
			line.indent++;
		}
		line.continues = scanner.peek("& ") || scanner.peek("]");
		if (scanner.peek("& ")) {
			scanner.skip();
			scanner.skip();
		} else if (scanner.peek("]")) {
			scanner.skip();
			scanner.matches(",");
			scanner.skipWhiteSpace();
			line.closedSub = true;
		}
		line.start = scanner.getState();
		lines.add(line);
	}

	@Override
	public boolean getSub() {
		Level level = currentLevel();
		boolean found = false;
		Line cur = null;
		for (int l = level.line; l < lines.size(); l++) {
			cur = lines.get(l);
			if (cur.indent < level.indent) { // stop on an indent lower than the current record
				level.line = l; // remember that the last record was read
				if (!found)
					levelDown(level, cur);
				return found;
			}
			if (found && cur.indent > level.indent) // skip sub-record lines with more indent than the current record
				continue;
			if (l > level.line && !cur.continues) { // found the next record
				level.line = l;
				return found;
			}
			if (scanNext(level, cur))
				found = true;
		}
		if (cur == null)
			expectLast(level);
		return found;
	}

	private Level currentLevel() {
		Level level = levels.get(levels.size() - 1);
		if (level.indent == 0) // top level records.. scan for lines first
			scanLines();
		finishRecord();
		level.fields.clear();
		return level;
	}

	private void levelDown(Level level, Line cur) {
		scanner.setState(cur.start);
		if (!cur.closedSub)
			indentError(level);
		finishRecord();
		levels.remove(levels.size() - 1);
	}

	private boolean scanNext(Level level, Line cur) {
		scanner.setState(cur.start);
		if (cur.indent > level.indent || scanner.peek(" "))
			indentError(level);
		delete = scanner.matches("!");
		return scanFields(level);
	}

	private void expectLast(Level level) {
		if (!scanner.peek("]"))
			indentError(level);
	}

	private void indentError(Level level) {
		scanner.error("Expect indent of " + (level.indent * 2) + " or ']' at the start of the line");
	}

	/** Returns true if the last read sub record should be deleted */
	@Override
	public boolean isDelete(int rec) {
		if (!delete)
			return false;
		if (rec == 0)
			scanner.error("Could not find to be deleted record");
		return true;
	}

	private boolean scanFields(Level level) {
		boolean found = false;
		while (!scanner.eof() && !scanner.peek("\n")) {
			buffer.setLength(0);
			if (!scanner.field(buffer))
				break;
			String name = buffer.toString();
			if (level.fields.containsKey(name))
				scanner.error("Duplicate field: " + name);
			level.fields.put(name, scanner.getState());
			found = true;
			if (!scanner.matches("!"))
				scanField();
			if (scanner.peek("{"))
				scanner.error("Incorrect field data after field: " + name);
			scanner.matches(",");
		}
		return found;
	}

	private void scanField() {
		scanner.expect("=", "after a field");
		if (scanner.matches("{")) { // Parse but do not remember the key fields inside the relation
			scanFields(new Level(-1, -1));
			scanner.expect("}");
		} else {
			while (!scanner.peek(",") && !scanner.peek("{") && !scanner.peek("}") && !scanner.eof() && !scanner.peek("\n"))
				if (scanner.peek("\\,") || scanner.peek("\\{") || scanner.peek("\\}")) {
					scanner.skip();
					scanner.skip();
				} else
					scanner.skip();
		}
	}

	@Override
	public String getString(String field) {
		if (!getField(field) || scanner.getCharPos() < 0)
			return null;
		if (scanner.matches("!"))
			return null;
		scanner.expect("=");
		buffer.setLength(0);
		if (scanner.peek("\n"))
			scanner.multiLine(buffer, levels.get(levels.size() - 1).indent + 1);
		else
			scanner.data(buffer);
		return buffer.toString();
	}

	private boolean getField(String field) {
		if (levels.isEmpty())
			return false;
		Map<String, State> map = levels.get(levels.size() - 1).fields;
		if (!map.containsKey(field))
			return false;
		scanner.setState(map.get(field));
		map.remove(field);
		return true;
	}

	@Override
	public boolean hasField(String field) {
		if (levels.isEmpty())
			return false;
		return levels.get(levels.size() - 1).fields.containsKey(field);
	}

	@Override
	public Boolean getBoolean(String field) {
		String val = getString(field);
		if (val == null)
			return null;
		if (!val.equals("true") && !val.equals("false"))
			scanner.error("Expect 'true' or 'false' after a boolean field");
		return val.equals("true");
	}

	@Override
	public int getInt(String field) {
		String val = getString(field);
		if (val == null)
			return Integer.MIN_VALUE;
		try {
			if (val.startsWith("0x")) {
				return Integer.parseInt(val.substring(2), 16);
			} else if (val.startsWith("0o")) {
				return Integer.parseInt(val.substring(2), 8);
			} else if (val.startsWith("0b")) {
				return Integer.parseInt(val.substring(2), 2);
			} else
				return Integer.parseInt(val);
		} catch (NumberFormatException e) {
			scanner.error("Incorrect number");
		}
		return Integer.MIN_VALUE;
	}

	@Override
	public long getLong(String field) {
		String val = getString(field);
		if (val == null)
			return Long.MIN_VALUE;
		try {
			return Long.parseLong(val);
		} catch (NumberFormatException e) {
			scanner.error("Incorrect number");
		}
		return Long.MIN_VALUE;
	}

	@Override
	public double getDouble(String field) {
		String val = getString(field);
		if (val == null)
			return Double.NaN;
		try {
			return Double.parseDouble(val);
		} catch (NumberFormatException e) {
			scanner.error("Incorrect number");
		}
		return Double.NaN;
	}

	@Override
	public boolean hasSub(String field) {
		if (!getField(field))
			return false;
		if (!scanner.peek("=["))
			scanner.error("Expect a '[' at the start of a set");
		scanner.skip();
		scanner.skip();
		if (scanner.peek("]"))
			return false;
		scanner.newLine();
		int lnr = 0;
		for (Line l : lines) {
			if (l.start.getLineNr() >= scanner.getLinenr())
				break;
			lnr++;
		}
		levels.add(new Level(levels.get(levels.size() - 1).indent + 1, lnr));
		return true;
	}

	@Override
	public void getRelation(String field, ScanRelation scan, int rec) {
		getRelation(field, scan, rec, -1);
	}

	@Override
	public void getRelation(String field, ScanRelation scan, int rec, int idx) {
		if (!getField(field))
			return;
		scanner.expect("={");
		State start = scanner.getState();
		Level level = new Level(-1, -1);
		levels.add(level);
		scanFields(level);
		scanner.expect("}");
		if (!scan.scan(rec, idx)) {
			if (rec != -1)
				todo.add(new Todo(start, scan, rec, idx));
			else
				scanner.error("Could not find corresponding record");
		}
		levels.remove(levels.size() - 1);
	}

	@Override
	public void error(String msg) {
		scanner.error(msg);
	}

	@Override
	public void finishRelation() {
		finishRecord();
	}

	private void finishRecord() {
		if (levels.isEmpty())
			throw new StructureError("Finished a not started record");
		for (Entry<String, State> field : levels.get(levels.size() - 1).fields.entrySet()) {
			scanner.setState(field.getValue());
			scanner.error("Unknown field '" + field.getKey() + "'");
		}
	}

	@Override
	public void close() {
		if (!scanner.isError())
			for (Todo t : todo) {
				scanner.setState(t.state);
				Level level = new Level(-1, -1);
				levels.add(level);
				scanFields(level);
				if (!t.scan.scan(t.rec, t.idx))
					scanner.error("Could not find corresponding record");
				levels.remove(levels.size() - 1);
			}
		scanner.close();
	}

	@Override
	public StringPointer getStringPointer(String string) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		return scanner.toString();
	}
}
