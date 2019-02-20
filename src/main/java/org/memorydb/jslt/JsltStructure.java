package org.memorydb.jslt;

import org.memorydb.generate.Generate;
import org.memorydb.generate.Project;
import org.memorydb.generate.Record;
import org.memorydb.generate.Type;

public class JsltStructure {
	public static void main(String[] args) {
		Generate.project(getProject());
		System.out.println("Refresh the project to allow eclipse to see the last changes");
	}

	public static Project getProject() {
		Project project = new Project("Jslt", "org.memorydb.jslt", "src/main/java/org/memorydb/jslt");

		Record type = project.content("ResultType");
		type.field("type", Type.ENUMERATE, "ARRAY", "BOOLEAN", "FLOAT", "NUMBER", "NULL", "OBJECT", "STRING",
				"STRUCTURE").defaultValue("NULL").mandatory();
		type.field("record", Type.STRING);

		Record step = project.record("Step");
		Record field = project.record("Field");
		Record expr = project.table("Expr");
		Record macro = project.table("Macro");

		Record level = project.table("Level");
		level.field("level", Type.INTEGER); // 0 = root, 1 = next, -1 = top, -2 = top-1
		level.field("order", Type.ARRAY, step);
		level.field("slice", Type.ARRAY, step);

		Record listener = project.table("Listener");
		listener.field("nr", Type.INTEGER).isKey();
		listener.field("levels", level, "level");

		Record source = project.table("Source");
		source.field("name", Type.STRING);
		source.field("listeners", listener, "nr");
		source.index("sources", "name");

		Record oper = project.content("Operator");
		oper.field("operation", Type.ENUMERATE, "FUNCTION", "CONDITION", "NUMBER", "FLOAT", "STRING", "ARRAY", "OBJECT",
				"BOOLEAN", "APPEND", "NULL", "CALL", "FILTER", "SORT", "IF", "CURRENT", "RUNNING", "READ", "VARIABLE")
				.condition();
		oper.field("function", Type.ENUMERATE, "NEG", "ADD", "MIN", "MUL", "DIV", "MOD", "POW", "EQ", "NE", "LT", "GT",
				"LE", "GE", "AND", "OR", "NOT", "FIRST", "LAST", "INDEX", "LENGTH", "NUMBER", "FLOAT", "STRING",
				"BOOLEAN", "NAME", "TYPE", "ELEMENT", "PER", "FOR", "EACH", "LAYOUT").mandatory().when("FUNCTION");
		oper.field("fnParm1", Type.OBJECT, expr).when("FUNCTION");
		oper.field("fnParm2", Type.OBJECT, expr).when("FUNCTION");
		oper.field("conExpr", Type.OBJECT, expr).when("CONDITION");
		oper.field("conTrue", Type.OBJECT, expr).when("CONDITION");
		oper.field("conFalse", Type.OBJECT, expr).when("CONDITION");
		oper.field("number", Type.LONG).when("NUMBER");
		oper.field("float", Type.FLOAT).when("FLOAT");
		oper.field("string", Type.STRING).when("STRING");
		oper.field("array", Type.ARRAY, step).when("ARRAY");
		oper.field("append", Type.ARRAY, step).when("APPEND");
		oper.field("object", Type.ARRAY, field).when("OBJECT");
		oper.field("boolean", Type.BOOLEAN).when("BOOLEAN");
		oper.field("macro", Type.RELATION, macro).when("CALL");
		oper.field("callParms", Type.ARRAY, step).when("CALL");
		oper.field("filter", Type.OBJECT, expr).when("FILTER");
		oper.field("filterDeep", Type.BOOLEAN, expr).when("FILTER");
		oper.field("filterExpr", Type.OBJECT, expr).when("FILTER");
		oper.field("sort", Type.OBJECT, expr).when("SORT");
		oper.field("sortParms", Type.ARRAY, step).when("SORT");
		oper.field("if", Type.OBJECT, expr).when("IF");
		oper.field("ifTrue", Type.ARRAY, step).when("IF");
		oper.field("ifFalse", Type.ARRAY, step).when("IF");
		oper.field("listenSource", Type.STRING).when("READ");
		oper.field("listemNr", Type.INTEGER).when("READ");
		oper.field("varName", Type.STRING).when("VARIABLE");
		oper.field("varNr", Type.INTEGER).when("VARIABLE");
		oper.include(type);

		field.field("name", Type.OBJECT, expr);
		field.include(oper);

		step.include(oper);
		expr.include(oper);
		listener.include(oper);
		level.include(oper);

		Record variable = project.table("Variable");
		variable.field("name", Type.STRING).mandatory();
		variable.field("nr", Type.INTEGER);
		variable.field("multiple", Type.BOOLEAN);
		variable.include(type);

		Record subMatch = project.record("SubMatch");
		Record mobj = project.table("MatchObject");
		Record mfield = project.record("MatchField");

		Record match = project.content("Match");
		match.field("variable", Type.OBJECT, variable); // store current match into a variable
		match.field("type", Type.ENUMERATE, "ANY", "ARRAY", "BOOLEAN", "NULL", "FLOAT", "NUMBER", "STRING", "OBJECT",
				"CONSTANT", "MACRO", "MULTIPLE").condition();
		match.field("marray", Type.ARRAY, subMatch).when("ARRAY");
		match.field("boolean", Type.BOOLEAN).when("BOOLEAN");
		match.field("float", Type.FLOAT).when("FLOAT");
		match.field("number", Type.LONG).when("NUMBER");
		match.field("string", Type.STRING).when("STRING");
		match.field("mobject", Type.ARRAY, mfield).when("OBJECT");
		match.field("cparm", Type.STRING).when("CONSTANT"); // current parameter should hold the same value as the specified parameter
		match.field("constant", Type.INTEGER).when("CONSTANT");
		match.field("macro", Type.RELATION, macro).when("MACRO");
		match.field("mparms", Type.ARRAY, step).when("MACRO");
		match.field("mmatch", Type.OBJECT, mobj).when("MULTIPLE");
		match.field("mmin", Type.BYTE).when("MULTIPLE");
		match.field("mmax", Type.BYTE).when("MULTIPLE");

		mfield.field("name", Type.STRING);
		mfield.include(match);
		mobj.include(match);

		Record parameter = project.record("Parameter");
		parameter.include(match);
		subMatch.include(match);

		Record alt = project.table("Alternative");
		alt.field("nr", Type.INTEGER);
		alt.field("parameters", Type.ARRAY, parameter);
		alt.field("anyParm", Type.BOOLEAN); // match any remaining parameters after the given set
		alt.field("if", Type.OBJECT, expr);
		alt.field("code", Type.ARRAY, step);

		Record matchStep = project.content("MatchStep");
		matchStep.field("type", Type.ENUMERATE, "STACK", "PARM", "FIELD", "ALT", "TEST_CALL", "JUMP", //
				"TEST_STACK", "TEST_BOOLEAN", "TEST_STRING", "TEST_NUMBER", "TEST_FLOAT", //
				"TEST_TYPE", "POS_KEEP", "POS_FREE", "POS_TO", "VAR_WRITE", "VAR_START", "VAR_ADD", "ERROR")
				.condition();
		matchStep.field("stack", Type.INTEGER).when("STACK"); // increase or decrease the stack frame (variables/call)
		matchStep.field("parm", Type.INTEGER).when("PARM"); // switch to a specific original parameter
		matchStep.field("pfalse", Type.INTEGER).when("PARM"); // continue if this parameter is not given
		matchStep.field("field", Type.STRING).when("FIELD"); // switch to a specific field
		matchStep.field("ffalse", Type.INTEGER).when("FIELD"); // continue of not an object of field doesn't exists
		matchStep.field("altnr", Type.INTEGER).when("ALT"); // record Id of Alternative, perform if-condition & calculate
		matchStep.field("afalse", Type.INTEGER).when("ALT"); // goto position if if-condition is not met
		matchStep.field("avar", Type.OBJECT, variable).when("ALT"); // result into variable: null = return main
		matchStep.field("tstack", Type.INTEGER).when("TEST_STACK"); // test the number of elements on the stack
		matchStep.field("tsfalse", Type.INTEGER).when("TEST_STACK"); // where to continue if the stack is not this size
		matchStep.field("tmacro", Type.RELATION, macro).when("TEST_CALL"); // current element as extra parameter
		matchStep.field("tfalse", Type.INTEGER).when("TEST_CALL"); // jump when result is false
		matchStep.field("jump", Type.INTEGER).when("JUMP"); // continue at a specific location
		matchStep.field("mboolean", Type.BOOLEAN).when("TEST_BOOLEAN"); // specific boolean value
		matchStep.field("mbfalse", Type.INTEGER).when("TEST_BOOLEAN"); // continue when not matched
		matchStep.field("mstring", Type.STRING).when("TEST_STRING"); // specific string value
		matchStep.field("msfalse", Type.INTEGER).when("TEST_STRING"); // continue when not matched
		matchStep.field("mnumber", Type.LONG).when("TEST_NUMBER"); // specific long value
		matchStep.field("mnfalse", Type.INTEGER).when("TEST_NUMBER"); // continue when not matched
		matchStep.field("mfloat", Type.FLOAT).when("TEST_FLOAT"); // specific float value
		matchStep.field("mffalse", Type.INTEGER).when("TEST_FLOAT"); // continue when not matched
		matchStep.field("ttype", Type.ENUMERATE, "TYPE_NULL", "TYPE_BOOLEAN", "TYPE_STRING", "TYPE_NUMBER",
				"TYPE_FLOAT", "TYPE_ARRAY", "TYPE_OBJECT").when("TEST_TYPE"); // test parameter on a type
		matchStep.field("ttfalse", Type.INTEGER).when("TEST_TYPE"); // continue when not the specified type
		matchStep.field("pto", Type.INTEGER).when("POS_TO"); // jump back to a specific position on a parameter
		matchStep.field("vwrite", Type.OBJECT, variable).when("VAR_WRITE"); // write to a variable
		matchStep.field("vwfrom", Type.INTEGER).when("VAR_WRITE"); // from current element position
		matchStep.field("vwtill", Type.INTEGER).when("VAR_WRITE"); // to position
		matchStep.field("vstart", Type.OBJECT, variable).when("VAR_START"); // start a new variable as an array
		matchStep.field("vadd", Type.OBJECT, variable).when("VAR_ADD"); // add a new element to a variable
		matchStep.field("vafrom", Type.INTEGER).when("VAR_ADD"); // from position
		matchStep.field("vatill", Type.INTEGER).when("VAR_ADD"); // to position
		matchStep.field("error", Type.STRING).when("ERROR"); // write a specific error text
		matchStep.field("efrom", Type.INTEGER).when("ERROR"); // from position
		matchStep.field("etill", Type.INTEGER).when("ERROR"); // to position

		Record mStep = project.record("MStep");
		mStep.include(matchStep);

		macro.field("name", Type.STRING);
		macro.field("alternatives", alt, "nr");
		macro.field("matching", Type.ARRAY, mStep);
		macro.index("macros", "name");

		return project;
	}
}
