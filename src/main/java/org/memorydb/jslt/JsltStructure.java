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
		type.field("type", Type.ENUMERATE, "ARRAY", "BOOLEAN", "FLOAT", "NUMBER", "NULL", "OBJECT", "STRING", "STRUCTURE").defaultValue("NULL").mandatory();
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
		oper.field("operation", Type.ENUMERATE, "FUNCTION", "CONDITION", "NUMBER", "FLOAT", "STRING", "ARRAY", "OBJECT", "BOOLEAN", "APPEND", "NULL", "CALL", "FOR", "SORT", "IF",
				"CURRENT", "READ").condition();
		oper.field("function", Type.ENUMERATE, "NEG", "ADD", "MIN", "MUL", "DIV", "MOD", "POW", "EQ", "NE", "LT", "GT", "LE", "GE", "AND", "OR", "NOT", "FIRST", "LAST", "INDEX",
				"LENGTH", "NUMBER", "FLOAT", "FILTER", "STRING", "BOOLEAN", "NAME", "TYPE", "ELEMENT").mandatory().when("FUNCTION");
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
		oper.field("for", Type.OBJECT, expr).when("FOR");
		oper.field("forExpr", Type.OBJECT, expr).when("FOR");
		oper.field("sort", Type.OBJECT, expr).when("SORT");
		oper.field("sortParms", Type.ARRAY, step).when("SORT");
		oper.field("if", Type.OBJECT, expr).when("IF");
		oper.field("ifTrue", Type.ARRAY, step).when("IF");
		oper.field("ifFalse", Type.ARRAY, step).when("IF");
		oper.field("listenSource", Type.STRING, listener).when("READ");
		oper.field("listemNr", Type.INTEGER, listener).when("READ");
		oper.include(type);

		field.field("name", Type.OBJECT, expr);
		field.include(oper);

		step.include(oper);
		expr.include(oper);
		listener.include(oper);
		level.include(oper);

		Record variable = project.table("Variable");
		variable.field("name", Type.STRING).mandatory();
		variable.field("eager", Type.BOOLEAN);
		variable.field("extension", Type.BOOLEAN);
		variable.include(type);

		Record subMatch = project.record("SubMatch");
		Record mfield = project.record("MatchField");

		Record match = project.content("Match");
		match.field("type", Type.ENUMERATE, "ARRAY", "BOOLEAN", "NULL", "VARIABLE", "FLOAT", "NUMBER", "STRING", "ITERATE", "OBJECT").condition();
		match.field("marray", Type.ARRAY, subMatch).when("ARRAY");
		match.field("variable", Type.OBJECT, variable).when("VARIABLE");
		match.field("boolean", Type.BOOLEAN).when("BOOLEAN");
		match.field("float", Type.FLOAT).when("FLOAT");
		match.field("number", Type.LONG).when("NUMBER");
		match.field("string", Type.STRING).when("STRING");
		match.field("iterate", Type.ARRAY, subMatch).when("ITERATE");
		match.field("mobject", Type.ARRAY, mfield).when("OBJECT");

		mfield.field("name", Type.STRING);
		mfield.include(match);

		Record parameter = project.record("Parameter");
		parameter.field("if", Type.OBJECT, expr);
		parameter.include(match);
		subMatch.include(match);

		Record alt = project.table("Alternative");
		alt.field("nr", Type.INTEGER);
		alt.field("parameters", Type.ARRAY, parameter);
		alt.field("code", Type.ARRAY, step);

		macro.field("name", Type.STRING);
		macro.field("alternatives", alt, "nr");
		macro.index("macros", "name");

		return project;
	}
}
