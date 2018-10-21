package org.memorydb.jslt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.memorydb.file.Scanner;
import org.memorydb.file.Scanner.State;
import org.memorydb.jslt.Match.Type;
import org.memorydb.jslt.Operator.Function;
import org.memorydb.jslt.Operator.Operation;
import org.memorydb.jslt.Source.IndexListeners;
import org.memorydb.structure.InputOutputException;
import org.memorydb.structure.Store;

public class JsltParser {
	private JsltParser() {
		// nothing
	}

	public static void parse(String jslt, Store jsltStore) {
		try {
			Path tempFile = Files.createTempFile("test", ".txt");
			Files.write(tempFile, jslt.getBytes());
			new Parser(new Scanner(tempFile), jsltStore).parse();
		} catch (IOException e) {
			throw new InputOutputException(e);
		}
	}

	static class Parser {
		private List<ChangeOperator> stack = new ArrayList<>();
		private Scanner scanner;
		private Store store;
		private ChangeOperator spot;
		private ChangeMatch match;
		private Macro slice;

		public Parser(Scanner scanner, Store jsltStore) {
			this.scanner = scanner;
			this.store = jsltStore;
		}

		void parse() {
			try (ChangeMacro cSlice = new ChangeMacro(store)) {
				cSlice.setName("slice");
				this.slice = cSlice;
			}
			while (scanner.matches("def"))
				parseDef();
			try (ChangeMacro macro = new ChangeMacro(store)) {
				macro.setName("main");
				try (ChangeAlternative alt = new ChangeAlternative(macro, 0)) {
					alt.setNr(0);
					spot = alt.getCode().add();
				}
				parseExpr();
			}
		}

		private void parseDef() {
			String id = scanner.parseIdentifier();
			Macro search = new Macro(store);
			Alternative alt;
			int rec = search.new IndexMacros(id).search();
			if (rec > 0) {
				search.setRec(rec);
				int altNr = 0;
				for (Alternative a : search.getAlternatives()) {
					if (a.getNr() > altNr)
						altNr = a.getNr();
				}
				try (ChangeAlternative nalt = new ChangeAlternative(search, 0)) {
					nalt.setNr(altNr);
					spot = nalt.getCode().add();
					alt = nalt;
				}
			} else
				try (ChangeMacro macro = new ChangeMacro(store)) {
					macro.setName(id);
					try (ChangeAlternative nalt = new ChangeAlternative(macro, 0)) {
						nalt.setNr(0);
						spot = nalt.getCode().add();
						alt = nalt;
					}
					search = macro;
				}
			scanner.expect("(");
			if (!scanner.matches(")")) {
				parseParameter(alt);
				while (scanner.matches(","))
					parseParameter(alt);
				scanner.expect(")");
			}
			scanner.expect(":");
			parseExpr();
		}

		private void parseParameter(Alternative alt) {
			ParametersArray par = alt.getParameters().add();
			match = par;
			parseMatch();
			if (scanner.matches("if")) {
				try (ChangeExpr data = new ChangeExpr(store)) {
					remember();
					spot = data;
					parseExpr();
					restore();
					par.setIf(data);
				}
			}
		}

		private void parseMatch() {
			parseMPart();
			while (scanner.matches("+"))
				parseMPart();
		}

		private void parseMPart() {
			if (scanner.matches("true")) {
				match.setType(Type.BOOLEAN);
				match.setBoolean(true);
			} else if (scanner.matches("false")) {
				match.setType(Type.BOOLEAN);
				match.setBoolean(false);
			} else if (scanner.matches("null"))
				match.setType(Type.NULL);
			else if (scanner.matches("[")) {
				parseMArray();
			} else if (scanner.matches("{")) {
				parseMObject();
			} else if (scanner.matches("\"")) {
				parseString(true);
				copySpot(match, spot);
			} else if (scanner.matches("'")) {
				parseString(false);
				copySpot(match, spot);
			} else if (scanner.hasNumber() || scanner.peek("-")) {
				parseNumber();
				copySpot(match, spot);
			} else if (scanner.hasIdentifier()) {
				try (ChangeVariable var = new ChangeVariable(store)) {
					String id = scanner.parseIdentifier();
					match.setType(Type.VARIABLE);
					switch (id) {
					case "STRING":
						var.setType(ResultType.Type.STRING);
						break;
					case "NUMBER":
						var.setType(ResultType.Type.NUMBER);
						break;
					case "FLOAT":
						var.setType(ResultType.Type.FLOAT);
						break;
					case "BOOLEAN":
						var.setType(ResultType.Type.BOOLEAN);
						break;
					case "ARRAY":
						var.setType(ResultType.Type.ARRAY);
						break;
					case "OBJECT":
						var.setType(ResultType.Type.OBJECT);
						break;
					default:
						var.setName(id);
						var.setType(ResultType.Type.NULL);
						break;
					}
					if (var.getType() != ResultType.Type.NULL) {
						scanner.expect(":");
						var.setName(scanner.parseIdentifier());
					}
					match.setVariable(var);
				}
			} else
				scanner.error("Syntax error");
		}

		private void parseMArray() {
			ChangeMatch m = match;
			match.setType(Type.ARRAY);
			if (scanner.matches("]"))
				return;
			MarrayArray array = match.getMarray();
			match = array.add();
			parseMPart();
			while (scanner.matches(",")) {
				match = array.add();
				parseMPart();
			}
			match = m;
			scanner.expect("]");
		}

		private void parseMObject() {
			ChangeMatch m = match;
			match.setType(Type.OBJECT);
			if (scanner.matches("}"))
				return;
			MobjectArray obj = match.getMobject();
			MobjectArray add = obj.add();
			add.setName(scanner.parseIdentifier());
			scanner.expect(":");
			match = add;
			parseMPart();
			while (scanner.matches(",")) {
				add = obj.add();
				add.setName(scanner.parseIdentifier());
				scanner.expect(":");
				match = add;
				parseMPart();
			}
			match = m;
			scanner.expect("}");
		}

		private void copySpot(ChangeMatch m, ChangeOperator s) {
			switch (s.getOperation()) {
			case NUMBER:
				m.setType(Type.NUMBER);
				m.setNumber(s.getNumber());
				break;
			case STRING:
				m.setType(Type.STRING);
				m.setString(s.getString());
				break;
			case FLOAT:
				m.setType(Type.FLOAT);
				m.setFloat(s.getFloat());
				break;
			default:
				scanner.error("Parameter error");
			}
		}

		private void parseExpr() {
			parseCond();
			while (true) {
				if (!scanner.matches("in"))
					return;
				try (ChangeExpr temp = new ChangeExpr(store)) {
					move(temp, spot);
					spot.setOperation(Operation.FOR);
					spot.setForExpr(temp);
				}
				try (ChangeExpr data = new ChangeExpr(store)) {
					remember();
					spot = data;
					parseCond();
					restore();
					spot.setFor(data);
				}
			}
		}

		private void parseCond() {
			parseOr();
			while (true) {
				if (!scanner.matches("?"))
					return;
				try (ChangeExpr expr = new ChangeExpr(store)) {
					move(expr, spot);
					spot.setOperation(Operation.CONDITION);
					spot.setConExpr(expr);
				}
				try (ChangeExpr cTrue = new ChangeExpr(store)) {
					remember();
					spot = cTrue;
					parseOr();
					restore();
					spot.setConTrue(cTrue);
				}
				scanner.expect(":");
				try (ChangeExpr cFalse = new ChangeExpr(store)) {
					remember();
					spot = cFalse;
					parseOr();
					restore();
					spot.setConFalse(cFalse);
				}
			}
		}

		private void parseOr() {
			parseAnd();
			while (true) {
				Function fn = null;
				if (scanner.matches("or") || scanner.matches("||"))
					fn = Function.OR;
				else
					return;
				try (ChangeExpr parm1 = new ChangeExpr(store)) {
					move(parm1, spot);
					spot.setOperation(Operation.FUNCTION);
					spot.setFunction(fn);
					spot.setFnParm1(parm1);
				}
				try (ChangeExpr parm2 = new ChangeExpr(store)) {
					remember();
					spot = parm2;
					parseAnd();
					restore();
					spot.setFnParm2(parm2);
				}
			}
		}

		private void parseAnd() {
			parseTest();
			while (true) {
				Function fn = null;
				if (scanner.matches("and") || scanner.matches("&&"))
					fn = Function.AND;
				else
					return;
				try (ChangeExpr parm1 = new ChangeExpr(store)) {
					move(parm1, spot);
					spot.setOperation(Operation.FUNCTION);
					spot.setFunction(fn);
					spot.setFnParm1(parm1);
				}
				try (ChangeExpr parm2 = new ChangeExpr(store)) {
					remember();
					spot = parm2;
					parseTest();
					restore();
					spot.setFnParm2(parm2);
				}
			}
		}

		private void parseTest() {
			parseAdd();
			while (true) {
				Function fn = null;
				if (scanner.matches("<=")) {
					fn = Function.LE;
				} else if (scanner.matches(">=")) {
					fn = Function.GE;
				} else if (scanner.matches("==")) {
					fn = Function.EQ;
				} else if (scanner.matches("!=")) {
					fn = Function.NE;
				} else if (scanner.matches(">")) {
					fn = Function.GT;
				} else if (scanner.matches("<")) {
					fn = Function.LT;
				}
				if (fn == null)
					return;
				try (ChangeExpr parm1 = new ChangeExpr(store)) {
					move(parm1, spot);
					spot.setOperation(Operation.FUNCTION);
					spot.setFunction(fn);
					spot.setFnParm1(parm1);
				}
				try (ChangeExpr parm2 = new ChangeExpr(store)) {
					remember();
					spot = parm2;
					parseAdd();
					restore();
					spot.setFnParm2(parm2);
				}
			}
		}

		private void parseAdd() {
			parseMult();
			while (true) {
				Function fn = null;
				if (scanner.matches("+")) {
					fn = Function.ADD;
				} else if (scanner.matches("-")) {
					fn = Function.MIN;
				}
				if (fn == null)
					return;
				try (ChangeExpr parm1 = new ChangeExpr(store)) {
					move(parm1, spot);
					spot.setOperation(Operation.FUNCTION);
					spot.setFunction(fn);
					spot.setFnParm1(parm1);
				}
				try (ChangeExpr parm2 = new ChangeExpr(store)) {
					remember();
					spot = parm2;
					parseMult();
					restore();
					spot.setFnParm2(parm2);
				}
			}
		}

		private void parseMult() {
			parseSingle();
			while (true) {
				Function fn = null;
				if (scanner.matches("*")) {
					fn = Function.MUL;
				} else if (scanner.matches("/")) {
					fn = Function.DIV;
				} else if (scanner.matches("%")) {
					fn = Function.MOD;
				}
				if (fn == null)
					return;
				try (ChangeExpr parm1 = new ChangeExpr(store)) {
					move(parm1, spot);
					spot.setOperation(Operation.FUNCTION);
					spot.setFunction(fn);
					spot.setFnParm1(parm1);
				}
				try (ChangeExpr parm2 = new ChangeExpr(store)) {
					remember();
					spot = parm2;
					parseSingle();
					restore();
					spot.setFnParm2(parm2);
				}
			}
		}

		private void parseSingle() {
			parseConst();
			while (scanner.peek("[") || scanner.peek(".")) {
				if (scanner.peek("..[")) {
					scanner.matches("..");
					try (ChangeExpr struc = new ChangeExpr(store)) {
						move(struc, spot);
						spot.setOperation(Operation.FILTER);
						spot.setFilterDeep(true);
						spot.setFilter(struc);
					}
					try (ChangeExpr data = new ChangeExpr(store)) {
						spot.setFilterExpr(data);
						spot = data;
					}
				} if (scanner.matches(".")) {
					if (scanner.matches(".")) {
						try (ChangeExpr struc = new ChangeExpr(store)) {
							move(struc, spot);
							spot.setOperation(Operation.FILTER);
							spot.setFilterDeep(true);
							spot.setFilter(struc);
						}
					}
					String id = scanner.parseIdentifier();
					if (scanner.matches("(")) {
						if (id.equals("length")) {
							try (ChangeExpr struc = new ChangeExpr(store)) {
								move(struc, spot);
								spot.setOperation(Operation.FUNCTION);
								spot.setFunction(Function.LENGTH);
								spot.setType(ResultType.Type.NUMBER);
								spot.setFnParm1(struc);
							}
						} else if (id.equals("name")) {
							try (ChangeExpr struc = new ChangeExpr(store)) {
								move(struc, spot);
								spot.setOperation(Operation.FUNCTION);
								spot.setFunction(Function.NAME);
								spot.setType(ResultType.Type.STRING);
								spot.setFnParm1(struc);
							}
						} else if (id.equals("type")) {
							try (ChangeExpr struc = new ChangeExpr(store)) {
								move(struc, spot);
								spot.setOperation(Operation.FUNCTION);
								spot.setFunction(Function.TYPE);
								spot.setType(ResultType.Type.STRING);
								spot.setFnParm1(struc);
							}
						} else if (id.equals("first")) {
							try (ChangeExpr struc = new ChangeExpr(store)) {
								move(struc, spot);
								spot.setOperation(Operation.FUNCTION);
								spot.setFunction(Function.FIRST);
								spot.setType(ResultType.Type.BOOLEAN);
								spot.setFnParm1(struc);
							}
						} else if (id.equals("last")) {
							try (ChangeExpr struc = new ChangeExpr(store)) {
								move(struc, spot);
								spot.setOperation(Operation.FUNCTION);
								spot.setFunction(Function.LAST);
								spot.setType(ResultType.Type.BOOLEAN);
								spot.setFnParm1(struc);
							}
						} else if (id.equals("index")) {
							try (ChangeExpr struc = new ChangeExpr(store)) {
								move(struc, spot);
								spot.setOperation(Operation.FUNCTION);
								spot.setFunction(Function.INDEX);
								spot.setType(ResultType.Type.NUMBER);
								spot.setFnParm1(struc);
							}
							if (!scanner.peek(")")) {
								try (ChangeExpr idx = new ChangeExpr(store)) {
									remember();
									spot = idx;
									parseExpr();
									restore();
									spot.setFnParm2(idx);
								}
							}
						}
						scanner.expect(")");
					} else {
						try (ChangeExpr struc = new ChangeExpr(store)) {
							move(struc, spot);
							spot.setOperation(Operation.FUNCTION);
							spot.setFunction(Function.ELEMENT);
							spot.setFnParm1(struc);
						}
						try (ChangeExpr parm = new ChangeExpr(store)) {
							parm.setOperation(Operation.STRING);
							parm.setString(id);
							spot.setFnParm2(parm);
						}
					}
				} else
					parseElements();
			}
		}

		private void parseElements() {
			scanner.expect("[");
			parseElmPart(true);
			while (scanner.matches(",")) {
				parseElmPart(false);
			}
			scanner.expect("]");
		}

		/**
		 * @return if this is a single element instead of an array result
		 */
		private void parseElmPart(boolean first) {
			if (!first) {
				// currently an ELEMENT function... move it to a slice
				ChangeExpr arr = spot.getFnParm1().change();
				ChangeExpr expr = spot.getFnParm2().change();
				spot.setOperation(Operation.CALL);
				spot.setMacro(slice);
				CallParmsArray callParms = spot.getCallParms();
				move(callParms.add(), arr);
				move(callParms.add(), expr);
				CallParmsArray till = callParms.add();
				till.setOperation(Operation.NUMBER);
				till.setNumber(Long.MAX_VALUE);
				CallParmsArray add = callParms.add();
				add.setOperation(Operation.NUMBER);
				add.setNumber(1);
			} else if (first && (scanner.peek("/") || scanner.peek("\\"))) {
				boolean desc = scanner.matches("\\");
				if (!desc)
					scanner.matches("/");
				SortParmsArray callParms;
				boolean extra = false;
				if (spot.getOperation() != Operation.SORT) {
					try (ChangeExpr struc = new ChangeExpr(store)) {
						move(struc, spot);
						spot.setOperation(Operation.SORT);
						callParms = spot.getSortParms();
						spot.setSort(struc);
					}
				} else {
					extra = true;
					callParms = spot.getSortParms();
				}
				remember();
				if (desc || extra) {
					spot = callParms.add();
					spot.setOperation(Operation.BOOLEAN);
					spot.setBoolean(desc);
				}
				spot = callParms.add();
				parseExpr();
				restore();
			} else if (first && scanner.matches("?")) {
				try (ChangeExpr temp = new ChangeExpr(store)) {
					move(temp, spot);
					spot.setOperation(Operation.FILTER);
					spot.setFilterDeep(false);
					spot.setFilter(temp);
				}
				try (ChangeExpr data = new ChangeExpr(store)) {
					remember();
					spot = data;
					parseExpr();
					restore();
					spot.setFilterExpr(data);
				}
			} else {
				try (ChangeExpr struc = new ChangeExpr(store)) {
					move(struc, spot);
					spot.setOperation(Operation.FUNCTION);
					spot.setFunction(Function.ELEMENT);
					spot.setFnParm1(struc);
				}
				try (ChangeExpr parm = new ChangeExpr(store)) {
					remember();
					spot = parm;
					if (!scanner.peek(":"))
						parseExpr();
					else {
						parm.setOperation(Operation.NUMBER);
						parm.setNumber(Long.MIN_VALUE);
					}
					restore();
					spot.setFnParm2(parm);
				}
			}
			if (spot.getOperation() == Operation.CALL) {
				CallParmsArray callParms = spot.getCallParms();
				remember();
				try (ChangeExpr from = new ChangeExpr(store)) {
					spot = from;
					if (!scanner.peek(":"))
						parseExpr();
					else {
						from.setOperation(Operation.NUMBER);
						from.setNumber(Long.MIN_VALUE);
					}
					move(callParms.add(), from);
				}
				if (!scanner.matches(":")) {
					CallParmsArray add = callParms.add();
					add.setOperation(Operation.NUMBER);
					add.setNumber(Long.MAX_VALUE);
					add = callParms.add();
					add.setOperation(Operation.NUMBER);
					add.setNumber(1);
					return;
				}
				try (ChangeExpr to = new ChangeExpr(store)) {
					spot = to;
					if (!scanner.peek(":") && !scanner.peek("]"))
						parseExpr();
					else {
						to.setOperation(Operation.NUMBER);
						to.setNumber(Long.MIN_VALUE);
					}
					move(callParms.add(), to);
				}
				if (scanner.matches(":")) {
					try (ChangeExpr step = new ChangeExpr(store)) {
						spot = step;
						parseExpr();
						move(callParms.add(), step);
					}
				} else {
					CallParmsArray add = callParms.add();
					add.setOperation(Operation.NUMBER);
					add.setNumber(1);
				}
				restore();
				return;
			}
			if (scanner.matches(":")) {
				CallParmsArray callParms;
				try (ChangeExpr struc = new ChangeExpr(spot.getFnParm1()); ChangeExpr from = new ChangeExpr(spot.getFnParm2())) {
					spot.setOperation(Operation.CALL);
					callParms = spot.getCallParms();
					spot.setMacro(slice);
					move(callParms.add(), struc);
					move(callParms.add(), from);
				}
				remember();
				try (ChangeExpr to = new ChangeExpr(store)) {
					spot = to;
					if (!scanner.peek(":") && !scanner.peek("]"))
						parseExpr();
					else {
						to.setOperation(Operation.NUMBER);
						to.setNumber(Long.MIN_VALUE);
					}
					move(callParms.add(), to);
				}
				if (scanner.matches(":")) {
					try (ChangeExpr step = new ChangeExpr(store)) {
						spot = step;
						parseExpr();
						move(callParms.add(), step);
					}
				} else {
					CallParmsArray add = callParms.add();
					add.setOperation(Operation.NUMBER);
					add.setNumber(1);
				}
				restore();
			}
		}

		private void parseConst() {
			if (scanner.matches("not") || scanner.matches("!")) {
				spot.setOperation(Operation.FUNCTION);
				spot.setFunction(Function.NOT);
				try (ChangeExpr parm1 = new ChangeExpr(store)) {
					remember();
					spot = parm1;
					parseSingle();
					restore();
					spot.setFnParm1(parm1);
				}
			} else if (scanner.matches("(")) {
				parseExpr();
				scanner.expect(")");
			} else if (scanner.matches("true")) {
				spot.setOperation(Operation.BOOLEAN);
				spot.setBoolean(true);
			} else if (scanner.matches("@")) {
				spot.setOperation(Operation.CURRENT);
			} else if (scanner.matches("$")) {
				parseListener();
			} else if (scanner.matches("false")) {
				spot.setOperation(Operation.BOOLEAN);
				spot.setBoolean(false);
			} else if (scanner.matches("null"))
				spot.setOperation(Operation.NULL);
			else if (scanner.matches("pow")) {
				parseFn("POW", 2);
			} else if (scanner.matches("string"))
				parseFn("STRING", 1);
			else if (scanner.matches("number"))
				parseFn("NUMBER", 1);
			else if (scanner.matches("float"))
				parseFn("FLOAT", 1);
			else if (scanner.matches("boolean"))
				parseFn("BOOLEAN", 1);
			else if (scanner.matches("["))
				parseArray();
			else if (scanner.matches("{"))
				parseObject();
			else if (scanner.matches("\""))
				parseString(true);
			else if (scanner.matches("'"))
				parseString(false);
			else if (scanner.hasNumber() || scanner.peek("-"))
				parseNumber();
			else
				scanner.error("Syntax error");
		}

		private void parseListener() {
			spot.setOperation(Operation.READ);
			spot.setListenSource("$");
			int listen = 0;
			Source source = new Source(spot.getStore());
			Source.IndexSources index = source.new IndexSources("$");
			int search = index.search();
			if (search <= 0) {
				try (ChangeSource c = new ChangeSource(spot.getStore())) {
					c.setName("$");
					source = c;
				}
			} else
				source.setRec(search);
			IndexListeners listeners = source.getListeners();
			for (Listener l : listeners)
				if (l.getNr() >= listen)
					listen = l.getNr() + 1;
			spot.setListemNr(listen);
			try (ChangeListener l = new ChangeListener(source, 0)) {
				l.setNr(listen);
				l.setOperation(Operation.CURRENT);
			}
		}

		private void parseFn(String string, int parms) {
			spot.setOperation(Operation.FUNCTION);
			spot.setFunction(Function.valueOf(string));
			switch (string) {
			case "POW":
			case "FLOAT":
				spot.setType(ResultType.Type.FLOAT);
				break;
			case "STRING":
				spot.setType(ResultType.Type.STRING);
				break;
			case "NUMBER":
				spot.setType(ResultType.Type.NUMBER);
				break;
			case "BOOLEAN":
				spot.setType(ResultType.Type.BOOLEAN);
				break;
			default:
			}
			scanner.expect("(");
			try (ChangeExpr f = new ChangeExpr(store)) {
				remember();
				spot = f;
				parseExpr();
				restore();
				spot.setFnParm1(f);
			}
			if (parms > 1) {
				scanner.expect(",");
				try (ChangeExpr s = new ChangeExpr(store)) {
					remember();
					spot = s;
					parseExpr();
					restore();
					spot.setFnParm2(s);
				}
			}
			scanner.expect(")");
		}

		private void parseArray() {
			spot.setOperation(Operation.ARRAY);
			spot.setType(ResultType.Type.ARRAY);
			if (scanner.matches("]"))
				return;
			ArrayArray array = spot.getArray();
			remember();
			spot = array.add();
			parseExpr();
			while (scanner.matches(",")) {
				spot = array.add();
				parseExpr();
			}
			restore();
			scanner.expect("]");
		}

		private void parseObject() {
			spot.setOperation(Operation.OBJECT);
			spot.setType(ResultType.Type.OBJECT);
			if (scanner.matches("}"))
				return;
			ObjectArray obj = spot.getObject();
			remember();
			ObjectArray add = obj.add();
			try (ChangeExpr expr = new ChangeExpr(store)) {
				spot = expr;
				if (scanner.hasIdentifier()) {
					spot.setOperation(Operation.STRING);
					spot.setString(scanner.parseIdentifier());
				} else
					parseExpr();
				add.setName(expr);
			}
			scanner.expect(":");
			spot = add;
			parseExpr();
			while (scanner.matches(",")) {
				add = obj.add();
				try (ChangeExpr expr = new ChangeExpr(store)) {
					spot = expr;
					if (scanner.hasIdentifier()) {
						spot.setOperation(Operation.STRING);
						spot.setString(scanner.parseIdentifier());
					} else
						parseExpr();
					add.setName(expr);
				}
				scanner.expect(":");
				spot = add;
				parseExpr();
			}
			restore();
			scanner.expect("}");
		}

		private void parseString(boolean doubleQuote) {
			boolean append = false;
			StringBuilder bld = new StringBuilder();
			spot.setOperation(Operation.APPEND);
			spot.setType(ResultType.Type.STRING);
			AppendArray arr = spot.getAppend();
			char ch = scanner.getChar();
			while (ch != (doubleQuote ? '\"' : '\'')) {
				if (ch == '&') {
					State state = scanner.getState();
					if (scanner.getChar() == '{') {
						append = true;
						AppendArray add = arr.add();
						add.setOperation(Operation.STRING);
						add.setString(bld.toString());
						bld.setLength(0);
						remember();
						spot = arr.add();
						parseExpr();
						restore();
						scanner.expect("}");
						ch = scanner.getChar();
					} else {
						bld.append(ch);
						scanner.setState(state);
						ch = scanner.getChar();
					}
					continue;
				}
				if (ch == '\\') {
					ch = scanner.getChar();
					switch (ch) {
					case '\\':
					case '&':
					case '\"':
					case '\'':
						break;
					case 'b':
						ch = 7;
						break;
					case 'n':
						ch = 10;
						break;
					case 'r':
						ch = 13;
						break;
					case 'f':
						ch = 11;
						break;
					default:
						scanner.error("Unknown escaped token");
					}
				}
				bld.append(ch);
				ch = scanner.getChar();
				if (ch == '\n' || ch == '\0')
					scanner.error("Open string");
			}
			if (append) {
				AppendArray add = arr.add();
				add.setOperation(Operation.STRING);
				add.setString(bld.toString());
				bld.setLength(0);
			} else {
				spot.setOperation(Operation.STRING);
				spot.setString(bld.toString());
			}
		}

		private void parseNumber() {
			State state = scanner.getState();
			long parseLong = scanner.parseLong();
			if (scanner.peek(".") || scanner.peek("e")) {
				scanner.setState(state);
				spot.setOperation(Operation.FLOAT);
				spot.setFloat(scanner.parseFloat());
				spot.setType(ResultType.Type.FLOAT);
			} else {
				spot.setOperation(Operation.NUMBER);
				spot.setNumber(parseLong);
				spot.setType(ResultType.Type.NUMBER);
			}
		}

		/**
		 * Shallow copy of the jslt operator
		 */
		private void move(ChangeOperator into, ChangeOperator from) {
			Operation operation = from.getOperation();
			into.setOperation(operation);
			if (operation != null)
				switch (operation) {
				case ARRAY:
					into.moveArray(from);
					break;
				case BOOLEAN:
					into.setBoolean(from.isBoolean());
					break;
				case CALL:
					into.setMacro(from.getMacro());
					into.moveCallParms(from);
					break;
				case CONDITION:
					break;
				case FLOAT:
					into.setFloat(from.getFloat());
					break;
				case FOR:
					into.setFor(from.getFor());
					into.setForExpr(from.getForExpr());
					break;
				case FUNCTION:
					into.setFunction(from.getFunction());
					into.setFnParm1(from.getFnParm1());
					into.setFnParm2(from.getFnParm2());
					break;
				case SORT:
					into.setSort(from.getSort());
					into.getSortParms().moveArray(from.getSortParms());
					break;
				case NUMBER:
					into.setNumber(from.getNumber());
					break;
				case OBJECT:
					into.moveObject(from);
					break;
				case STRING:
					into.setString(from.getString());
					break;
				default:
					break;
				}
		}

		private void remember() {
			stack.add(spot);
		}

		private void restore() {
			spot = stack.get(stack.size() - 1);
			stack.remove(stack.size() - 1);
		}
	}
}
