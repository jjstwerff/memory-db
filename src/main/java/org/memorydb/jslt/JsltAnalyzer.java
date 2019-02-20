package org.memorydb.jslt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.memorydb.jslt.MatchStep.Ttype;
import org.memorydb.jslt.ResultType.Type;
import org.memorydb.structure.Store;

public class JsltAnalyzer {
	private interface Setter {
		void set(int pos);
	}

	private Map<String, List<Setter>> jumpto = new TreeMap<>();
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
		for (Entry<String, List<Setter>> entry : jumpto.entrySet())
			if (entry.getValue().size() != 0)
				throw new RuntimeException("Not previously resolved jump:" + entry.getKey());
		for (Alternative alt : macro.getAlternatives()) {
			showAlt(alt);
			resolve("NextAlternative");
			int size = alt.getParameters().getSize();
			if (parms != size) {
				if (parms != -1)
					resolve("NextParmSize");
				if (!alt.isAnyParm()) {
					parms = size;
					MatchingArray stackTest = addStep(MatchingArray.Type.TEST_STACK);
					stackTest.setTstack(parms);
					jump(s -> stackTest.setTsfalse(s), "NextParmSize");
				}
			}
			if (checkPassAsIs(alt)) {
				int p = 0;
				for (ParametersArray parm : alt.getParameters()) {
					MatchingArray setParm = addStep(MatchingArray.Type.PARM);
					setParm.setParm(p++);
					Type type = parm.getVariable().getType();
					if (type != null) {
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
						jump(s -> typeTest.setTtfalse(s), "NextAlternative");
					}
				}
			}
			MatchingArray call = addStep(MatchingArray.Type.ALT);
			call.setAltnr(alt.getRec());
			jump(s -> call.setAfalse(s), "NextAlternative");
			int r = 0;
			for (MatchingArray elm: macro.getMatching()) {
				System.out.print(r++ + ":[" + elm.getRec() + "] " + elm);
			}
			System.out.println();
		}
		resolve("NextAlternative");
		resolve("NextParmSize");
		error("No matching macro named '" + macro.getName() + "' found");
		System.out.println(macro.getMatching());
	}

	private void showAlt(Alternative alt) {
		StringBuilder bld = new StringBuilder();
		bld.append(alt.getUpRecord().getName()).append("(");
		boolean first = true;
		for(ParametersArray p: alt.getParameters()) {
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
		err.setEfrom(0);
	}

	private void jump(Setter setter, String name) {
		validateName(name);
		jumpto.get(name).add(setter);
	}

	private void resolve(String name) {
		validateName(name);
		List<Setter> list = jumpto.get(name);
		for (Setter s : list)
			s.set(macro.getMatching().getSize());
		list.clear();
	}

	private void validateName(String name) {
		if (!jumpto.containsKey(name))
			jumpto.put(name, new ArrayList<>());
	}

	private boolean checkPassAsIs(Alternative alt) {
		for (ParametersArray parm : alt.getParameters())
			if (parm.getType() != Match.Type.ANY)
				return false;
		return true;
	}
}
