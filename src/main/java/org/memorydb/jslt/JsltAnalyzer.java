package org.memorydb.jslt;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.memorydb.jslt.MatchStep.Jump;
import org.memorydb.jslt.MatchStep.Step;
import org.memorydb.jslt.MatchStep.Ttype;
import org.memorydb.jslt.ResultType.Type;
import org.memorydb.structure.Store;

public class JsltAnalyzer {
	private interface Setter {
		void set(MatchingArray elm, int pos);
	}

	private Map<String, Map<Integer, Setter>> jumpto = new TreeMap<>();
	private Macro macro;
	private enum VarType { SINGLE, MULTIPLE };
	private Map<String, VarType> vars = new HashMap<>(); // encountered variables
	private int index = 0;

	public static void analyze(Store data) {
		JsltAnalyzer analyze = new JsltAnalyzer();
		for (Macro macro : new Macro.IndexMacros(data)) {
			analyze.macro = macro;
			analyze.analyze();
		}
	}

	private void analyze() {
		index = 0;
		int parms = -1;
		for (Entry<String, Map<Integer, Setter>> entry : jumpto.entrySet())
			if (entry.getValue().size() != 0)
				throw new RuntimeException("Not previously resolved jump:" + entry.getKey());
		for (Alternative alt : macro.getAlternatives()) {
			// showAlt(alt);
			findMultiVars(alt);
			resolve("NextAlternative");
			int size = alt.getParameters().size();
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
					testType(parm, "NextAlternative");
			else if (checkContinuesAsIs(alt) >= 0) {
				for (ParametersArray parm : alt.getParameters()) {
					if (parm.getVariable().rec() != 0)
						testType(parm, "NextAlternative");
					else {
						setParm(parm, "NextAlternative");
						testConst(parm, "NextAlternative", false);
					}
				}
				int cont = checkContinuesAsIs(alt);
				if (cont != 0) {
					MatchingArray stackAdd = addStep(MatchingArray.Type.STACK);
					stackAdd.setStack(cont);
				}
			} else {
				MatchingArray stackSkip = addStep(MatchingArray.Type.STACK);
				stackSkip.setStack(alt.getParameters().size());
				for (ParametersArray parm : alt.getParameters()) {
					setParm(parm, "NextAlternative");
					testConst(parm, "NextAlternative", false);
				}
			}
			MatchingArray call = addStep(MatchingArray.Type.ALT);
			call.setAltnr(alt.rec());
			jump(call, (e, s) -> e.setAfalse(s), "NextAlternative");
		}
		resolve("NextAlternative");
		resolve("NextParmSize");
		MatchingArray ret = addStep(MatchingArray.Type.JUMP);
		ret.setJump(Jump.MISS);
	}

	private void findMultiVars(Alternative alt) { // all variables that are filled multiple types: by multiple or by mentioning more than once
		vars.clear();
		for (ParametersArray parm : alt.getParameters())
			findMultiVars(parm, false);
	}

	private void findMultiVars(Match match, boolean inMulti) {
		if (match.getType() == Match.Type.MULTIPLE)
			findMultiVars(match.getMmatch(), true);
		else if (match.getType() == Match.Type.ARRAY)
			for (Match sub: match.getMarray())
				findMultiVars(sub, inMulti);
		Variable var = match.getVariable();
		if (var.rec != 0) {
			VarType v = VarType.SINGLE;
			if (vars.containsKey(var.getName())) {
				v = vars.get(var.getName());
				if (v == VarType.SINGLE)
					v = VarType.MULTIPLE;
			}
			if (inMulti)
				v = VarType.MULTIPLE;
			vars.put(var.getName(), v);
		}
	}

	private void testType(Match match, String miss) {
		Type type = match.getVariable().getType();
		if (type != Type.NULL) {
			setParm(match, miss);
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
			jump(typeTest, (e, s) -> e.setTtfalse(s), miss);
		}
	}

	private void setParm(Match match, String miss) {
		MatchingArray setParm = addStep(MatchingArray.Type.PARM);
		setParm.setParm(match.index());
		jump(setParm, (e, s) -> e.setPfalse(s), miss);
	}

	private boolean testConst(Match match, String miss, boolean stringMatch) {
		switch (match.getType()) {
		case ARRAY:
			defineArray(match, miss);
			break;
		case BOOLEAN:
			MatchingArray testBool = addStep(MatchingArray.Type.TEST_BOOLEAN);
			testBool.setMboolean(match.isBoolean());
			jump(testBool, (e, s) -> e.setMbfalse(s), miss);
			break;
		case CONSTANT:
			MatchingArray parm = addStep(MatchingArray.Type.TEST_PARM);
			parm.setTparm(match.getConstant());
			jump(parm, (e, s) -> e.setTpfalse(s), miss);
			break;
		case FLOAT:
			MatchingArray testFloat = addStep(MatchingArray.Type.TEST_FLOAT);
			testFloat.setMfloat(match.getFloat());
			jump(testFloat, (e, s) -> e.setMffalse(s), miss);
			break;
		case MACRO:
			MatchingArray call = addStep(MatchingArray.Type.CALL);
			call.setMacro(match.getMacro());
			for (MparmsArray p: match.getMparms())
				JsltParser.move(call.addParms(), p);
			jump(call, (e, s) -> e.setMfalse(s), miss);
			break;
		case MULTIPLE:
			defineMultiple(match, miss, stringMatch);
			return true;
		case NULL:
			throw new RuntimeException("Not defined yet");
		case NUMBER:
			MatchingArray testNumber = addStep(MatchingArray.Type.TEST_NUMBER);
			testNumber.setMnumber(match.getNumber());
			jump(testNumber, (e, s) -> e.setMnfalse(s), miss);
			break;
		case OBJECT:
			throw new RuntimeException("Not defined yet");
		case ANY:
			MatchingArray step = addStep(MatchingArray.Type.STEP);
			step.setStep(Step.FORWARD);
			jump(step, (e, s) -> e.setMissed(s), miss);
			break;
		case STRING:
			if (stringMatch) {
				MatchingArray testStr = addStep(MatchingArray.Type.MATCH_STRING);
				testStr.setMstring(match.getString());
				jump(testStr, (e, s) -> e.setMsfalse(s), miss);
				return true;
			} else {
				MatchingArray testStr = addStep(MatchingArray.Type.TEST_STRING);
				testStr.setTstring(match.getString());
				jump(testStr, (e, s) -> e.setMtsfalse(s), miss);
				if (match.getVariable().rec != 0 && vars.get(match.getVariable().getName()) == VarType.SINGLE) {
					MatchingArray write = addStep(MatchingArray.Type.VAR_WRITE);
					write.setVwrite(match.getVariable());
				}
			}
			break;
		default:
			break;
		}
		return false;
	}

	private void defineArray(Match match, String miss) {
		if (match.getVariable().rec > 0) {
			addStep(MatchingArray.Type.PUSH);
			index++;
			array(match, "Cleanup");
			resolve("Cleanup");
			MatchingArray writeVar = addStep(MatchingArray.Type.VAR_WRITE);
			writeVar.setVwrite(match.getVariable());
			writeVar.setVwrange(index);
			MatchingArray pop = addStep(MatchingArray.Type.POP);
			pop.setPopread(false);
			index--;
		} else {
			array(match, miss);
		}
	}

	private void array(Match match, String miss) { // when miss != NextAlternative: matching half array should reset cur
		MatchingArray start = addStep(MatchingArray.Type.STEP);
		start.setStep(Step.START);
		jump(start, (e, s) -> e.setMissed(s), miss);
		boolean last = false;
		int startIndex = index;
		MatchingArray next = null;
		for (Match sub : match.getMarray()) {
			if (sub.getVariable().rec > 0) {
				last = sub.testLast();
				Match.Type subType = sub.getType();
				int write = Integer.MIN_VALUE;
				if (subType != Match.Type.ANY) {
					addStep(MatchingArray.Type.PUSH);
					write = index;
					index++;
					testConst(sub, miss, true);
				}
				MatchingArray writeVar = addStep(MatchingArray.Type.VAR_WRITE);
				if (!last && subType == Match.Type.ANY)
					testConst(sub, miss, true);
				writeVar.setVwrite(sub.getVariable());
				writeVar.setVwrange(last ? - 1: write);
				while (index > startIndex) {
					MatchingArray pop = addStep(MatchingArray.Type.POP);
					pop.setPopread(false);
					index--;
				}
				if (last)
					next = null;
			} else if (sub.getType() != Match.Type.ANY) {
				if (!last && !testConst(sub, miss, true)) {
					next = addStep(MatchingArray.Type.STEP);
					next.setStep(Step.FORWARD);
				} else
					next = null;
			} else {
				if (!last) {
					next = addStep(MatchingArray.Type.STEP);
					next.setStep(Step.FORWARD);
				} else
					next = null;
			}
		}
		if (next != null) {
			next.setMissed(next.index() + 2);
			MatchingArray skip = addStep(MatchingArray.Type.JUMP);
			skip.setJump(Jump.CONTINUE);
			jump(skip, (e, s) -> e.setPosition(s), miss);
		}
	}

	private void defineMultiple(Match match, String miss, boolean stringMatch) {
		int startIndex = index;
		if (!stringMatch) {
			MatchingArray start = addStep(MatchingArray.Type.STEP);
			start.setStep(Step.START);
			if (match.getVariable().rec != 0 && vars.get(match.getVariable().getName()) == VarType.SINGLE) {
				addStep(MatchingArray.Type.PUSH);
				index++;
				testConst(match, miss, true);
				MatchingArray writeVar = addStep(MatchingArray.Type.VAR_WRITE);
				writeVar.setVwrite(match.getVariable());
				writeVar.setVwrange(index);
				return;
			}
		}
		boolean multiple = match.getMmax() != 1;
		int again = macro.getMatching().size();
		if (match.testLast()) { // eager matching
			testConst(match.getMmatch(), "After", true);
			if (multiple) {
				MatchingArray redo = addStep(MatchingArray.Type.JUMP);
				redo.setJump(Jump.CONTINUE);
				redo.setPosition(again);
			}
			resolve("After");
			return;
		}
		// lazy matching till the next array elements are matching
		addStep(MatchingArray.Type.PUSH);
		MatchingArray call = addStep(MatchingArray.Type.JUMP);
		call.setJump(Jump.CALL);
		int jumpIndex = index + 1;
		jump(call, (e, s) -> e.setPosition(s), "AfterMatch");
		MatchingArray pop = addStep(MatchingArray.Type.POP);
		pop.setPopread(true);
		if (!testConst(match.getMmatch(), miss, true)) {
			MatchingArray step = addStep(MatchingArray.Type.STEP);
			step.setStep(Step.FORWARD);
			jump(step, (e, s) -> e.setMissed(s), miss);
		}
		if (multiple) {
			MatchingArray redo = addStep(MatchingArray.Type.JUMP);
			redo.setJump(Jump.CONTINUE);
			redo.setPosition(again);
		} else {
			MatchingArray redo = addStep(MatchingArray.Type.JUMP);
			redo.setJump(Jump.CALL);
			jump(redo, (e, s) -> e.setPosition(s), "AfterMatch");
		}
		while (index > startIndex) {
			pop = addStep(MatchingArray.Type.POP);
			pop.setPopread(false);
			index--;
		}
		resolve("AfterMatch");
		index = jumpIndex;
	}

	/* package private */ static void showMatching(Macro macro) {
		MatchingArray matching = macro.getMatching();
		for (MatchingArray elm : matching) {
			System.out.print(elm.index() + ":[" + elm.rec() + "] " + elm);
		}
		System.out.println();
	}

	/* package private */ static void showAlt(Alternative alt) {
		StringBuilder bld = new StringBuilder();
		bld.append(alt.up().getName()).append("(");
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

	private void jump(MatchingArray elm, Setter setter, String name) {
		validateName(name);
		jumpto.get(name).put(elm.index(), setter);
	}

	private void resolve(String name) {
		validateName(name);
		Map<Integer, Setter> elms = jumpto.get(name);
		for (Entry<Integer, Setter> s : elms.entrySet())
			s.getValue().set(macro.getMatching(s.getKey()), macro.getMatching().size());
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
			int idx = parm.index();
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
		if (match.getVariable().rec() != 0)
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
