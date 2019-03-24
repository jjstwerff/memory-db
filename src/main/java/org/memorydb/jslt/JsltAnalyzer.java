package org.memorydb.jslt;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.memorydb.jslt.MatchStep.Ttype;
import org.memorydb.jslt.ResultType.Type;
import org.memorydb.structure.Store;

public class JsltAnalyzer {
	private interface Setter {
		void set(MatchingArray elm, int pos);
	}

	private Map<String, Map<Integer, Setter>> jumpto = new TreeMap<>();
	private Macro macro;

	public static void analyze(Store data) {
		JsltAnalyzer analyze = new JsltAnalyzer();
		Macro macros = new Macro(data);
		for (Macro macro : macros.new IndexMacros()) {
			analyze.macro = macro;
			analyze.analyze();
		}
	}

	private void analyze() {
		int parms = -1;
		for (Entry<String, Map<Integer, Setter>> entry : jumpto.entrySet())
			if (entry.getValue().size() != 0)
				throw new RuntimeException("Not previously resolved jump:" + entry.getKey());
		for (Alternative alt : macro.getAlternatives()) {
			// showAlt(alt);
			resolve("NextAlternative");
			int size = alt.getParameters().getSize();
			if (parms != size) {
				if (parms != -1)
					resolve("NextParmSize");
				if (!alt.isAnyParm()) {
					parms = size;
					MatchingArray stackTest = addStep(MatchingArray.Type.TEST_STACK);
					stackTest.setTstack(parms);
					jump(stackTest, (e, s) -> e.setTsfalse(s), "NextParmSize");
				}
			}
			if (checkPassAsIs(alt))
				for (ParametersArray parm : alt.getParameters())
					testType(parm);
			else if (checkContinuesAsIs(alt) >= 0) {
				for (ParametersArray parm : alt.getParameters()) {
					if (parm.getVariable().getRec() != 0)
						testType(parm);
					else {
						setParm(parm);
						testConst(parm, false);
					}
				}
				int cont = checkContinuesAsIs(alt);
				if (cont != 0) {
					MatchingArray stackAdd = addStep(MatchingArray.Type.STACK);
					stackAdd.setStack(cont);
				}
			} else {
				MatchingArray stackSkip = addStep(MatchingArray.Type.STACK);
				stackSkip.setStack(alt.getParameters().getSize());
				for (ParametersArray parm : alt.getParameters()) {
					setParm(parm);
					testConst(parm, true);
				}
			}
			MatchingArray call = addStep(MatchingArray.Type.ALT);
			call.setAltnr(alt.getRec());
			jump(call, (e, s) -> e.setAfalse(s), "NextAlternative");
		}
		resolve("NextAlternative");
		resolve("NextParmSize");
		error("No matching macro named '" + macro.getName() + "' found");
		showMatching(macro);
	}

	private void testType(Match match) {
		Type type = match.getVariable().getType();
		if (type != Type.NULL) {
			setParm(match);
			MatchingArray typeTest = addStep(MatchingArray.Type.TEST_TYPE);
			switch (type) {
			case ARRAY:
				typeTest.setTtype(Ttype.TYPE_ARRAY);
				break;
			case BOOLEAN:
				typeTest.setTtype(Ttype.TYPE_BOOLEAN);
				break;
			case FLOAT:
				typeTest.setTtype(Ttype.TYPE_FLOAT);
				break;
			case NULL:
				typeTest.setTtype(Ttype.TYPE_NULL);
				break;
			case NUMBER:
				typeTest.setTtype(Ttype.TYPE_NUMBER);
				break;
			case OBJECT:
				typeTest.setTtype(Ttype.TYPE_OBJECT);
				break;
			case STRING:
				typeTest.setTtype(Ttype.TYPE_STRING);
				break;
			case STRUCTURE:
				typeTest.setTtype(Ttype.TYPE_OBJECT);
				break;
			}
			jump(typeTest, (e, s) -> e.setTtfalse(s), "NextAlternative");
		}
	}

	private void setParm(Match match) {
		MatchingArray setParm = addStep(MatchingArray.Type.PARM);
		setParm.setParm(match.getArrayIndex());
		jump(setParm, (e, s) -> e.setPfalse(s), "NextAlternative");
	}

	private void testConst(Match match, boolean variables) {
		switch (match.getType()) {
		case ARRAY:
			MatchingArray testArr = addStep(MatchingArray.Type.TEST_TYPE);
			testArr.setTtype(Ttype.TYPE_ARRAY);
			jump(testArr, (e, s) -> e.setTtfalse(s), "NextAlternative");
			for (Match sub : match.getMarray()) {
				if (sub.getType() == Match.Type.ANY) {
					System.out.println("here");
					//MatchingArray posKeep = addStep(MatchingArray.Type.POS_KEEP);
					MatchingArray writeVar = addStep(MatchingArray.Type.VAR_WRITE); // write to variable
					continue;
				}
				// TODO get next value
				testConst(sub, variables);
			}
			break;
		case BOOLEAN:
			MatchingArray testBool = addStep(MatchingArray.Type.TEST_BOOLEAN);
			testBool.setMboolean(match.isBoolean());
			jump(testBool, (e, s) -> e.setMbfalse(s), "NextAlternative");
			break;
		case CONSTANT:
			throw new RuntimeException("Not defined yet");
		case FLOAT:
			MatchingArray testFloat = addStep(MatchingArray.Type.TEST_FLOAT);
			testFloat.setMfloat(match.getFloat());
			jump(testFloat, (e, s) -> e.setMffalse(s), "NextAlternative");
			break;
		case MACRO:
			throw new RuntimeException("Not defined yet");
		case MULTIPLE:
			throw new RuntimeException("Not defined yet");
		case NULL:
			throw new RuntimeException("Not defined yet");
		case NUMBER:
			MatchingArray testNumber = addStep(MatchingArray.Type.TEST_NUMBER);
			testNumber.setMnumber(match.getNumber());
			jump(testNumber, (e, s) -> e.setMnfalse(s), "NextAlternative");
			break;
		case OBJECT:
			throw new RuntimeException("Not defined yet");
		case STRING:
			MatchingArray testStr = addStep(MatchingArray.Type.TEST_STRING);
			testStr.setMstring(match.getString());
			jump(testStr, (e, s) -> e.setMsfalse(s), "NextAlternative");
			break;
		default:
			break;
		}
	}

	/* package private */ static void showMatching(Macro macro) {
		System.out.println("macro:" + macro.getName());
		MatchingArray matching = macro.getMatching();
		for (MatchingArray elm : matching) {
			System.out.print((elm.getArrayIndex() + 1) + ":[" + elm.getRec() + "] " + elm);
		}
		System.out.println();
	}

	/* package private */ static void showAlt(Alternative alt) {
		StringBuilder bld = new StringBuilder();
		bld.append(alt.getUpRecord().getName()).append("(");
		boolean first = true;
		for (ParametersArray p : alt.getParameters()) {
			if (first)
				first = false;
			else
				bld.append(", ");
			bld.append(p.getVariable().getName());
		}
		if (alt.isAnyParm()) {
			if (!first)
				bld.append(", ");
			bld.append("...");
		}
		bld.append(")");
		System.out.println(bld);
	}

	private MatchingArray addStep(MatchingArray.Type type) {
		MatchingArray match = macro.addMatching();
		match.setType(type);
		return match;
	}

	private void error(String string) {
		MatchingArray err = addStep(MatchingArray.Type.ERROR);
		err.setError(string);
		err.setErange(Integer.MIN_VALUE);
	}

	private void jump(MatchingArray elm, Setter setter, String name) {
		validateName(name);
		jumpto.get(name).put(elm.getArrayIndex(), setter);
	}

	private void resolve(String name) {
		validateName(name);
		Map<Integer, Setter> elms = jumpto.get(name);
		for (Entry<Integer, Setter> s : elms.entrySet())
			s.getValue().set(macro.getMatching(s.getKey()), macro.getMatching().getSize() + 1);
		elms.clear();
	}

	private void validateName(String name) {
		if (!jumpto.containsKey(name))
			jumpto.put(name, new TreeMap<>());
	}

	private boolean checkPassAsIs(Alternative alt) {
		for (ParametersArray parm : alt.getParameters())
			if (parm.getType() != Match.Type.ANY)
				return false;
		return true;
	}

	private int checkContinuesAsIs(Alternative alt) {
		int startContinues = 0;
		int any = 0;
		for (ParametersArray parm : alt.getParameters()) {
			int idx = parm.getArrayIndex();
			if (parm.getType() != Match.Type.ANY) {
				if (hasVariable(parm))
					return -1;
				startContinues = idx + 1;
			} else {
				if (startContinues + any != idx)
					return -1;
				any++;
			}
		}
		return startContinues;
	}

	private boolean hasVariable(Match match) {
		if (match.getVariable().getRec() != 0)
			return true;
		switch (match.getType()) {
		case ARRAY:
			for (MarrayArray arr : match.getMarray())
				if (hasVariable(arr))
					return true;
			return false;
		case MULTIPLE:
			if (hasVariable(match.getMmatch()))
				return true;
			return false;
		case OBJECT:
			for (MobjectArray arr : match.getMobject())
				if (hasVariable(arr))
					return true;
			return false;
		default:
			return false;
		}
	}
}
