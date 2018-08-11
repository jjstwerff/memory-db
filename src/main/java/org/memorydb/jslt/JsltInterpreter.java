package org.memorydb.jslt;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.memorydb.handler.StringWriter;
import org.memorydb.handler.Writer;
import org.memorydb.jslt.Macro.IndexMacros;
import org.memorydb.jslt.Operator.Function;
import org.memorydb.jslt.Operator.Operation;
import org.memorydb.structure.RecordInterface;
import org.memorydb.structure.RecordInterface.FieldType;
import org.memorydb.structure.Store;

public class JsltInterpreter {
	private int index;
	private Object current;
	private boolean first;
	private boolean last;
	private String curName;
	private RecordInterface data;
	@SuppressWarnings("unused")
	private List<Pair> pairs = new ArrayList<>();

	public static String interpret(Store jsltStore, RecordInterface data) {
		JsltInterpreter inter = new JsltInterpreter();
		inter.data = data;
		Writer write = new StringWriter();
		Macro macro = new Macro(jsltStore);
		IndexMacros macros = macro.new IndexMacros("main");
		macro.setRec(macros.search());
		for (CodeArray code : macro.getAlternatives(0).getCode())
			inter.inter(write, code);
		return write.toString();
	}

	private Object inter(Operator code) {
		if (code.getOperation() == null)
			return null;
		switch (code.getOperation()) {
		case ARRAY:
			return null;
		case APPEND:
			return null;
		case BOOLEAN:
			return code.isBoolean();
		case CALL:
			return null;
		case CONDITION:
			return inter((Boolean) inter(code.getConExpr()) ? code.getConTrue() : code.getConFalse());
		case FLOAT:
			return code.getFloat();
		case FOR:
			return null;
		case FUNCTION:
			if (code.getFunction() == Function.ELEMENT) {
				Expr p1 = code.getFnParm1();
				Expr p2 = code.getFnParm2();
				if (p1.getOperation() == Operation.OBJECT) {
					String fname = getString(inter(p2));
					for (ObjectArray fld : p1.getObject()) {
						if (getString(inter(fld.getName())).equals(fname))
							return inter(fld);
					}
					return null;
				}
			}
			return doFunction(code);
		case IF:
			return null;
		case NULL:
			return null;
		case NUMBER:
			return code.getNumber();
		case OBJECT:
			return null;
		case SORT:
			return null;
		case STRING:
			return code.getString();
		case CURRENT:
			return null;
		case READ:
			return data;
		default:
			return null;
		}
	}

	private void inter(Writer result, Operator code) {
		if (code.getOperation() == null)
			return;
		switch (code.getOperation()) {
		case ARRAY:
			result.startArray();
			for (ArrayArray a : code.getArray())
				inter(result, a);
			result.endArray();
			break;
		case APPEND:
			result.append(true);
			for (AppendArray a : code.getAppend())
				inter(result, a);
			result.append(false);
			break;
		case BOOLEAN:
			result.element(code.isBoolean());
			break;
		case CALL:
			if (code.getMacro().getName().equals("slice"))
				slice(result, code);
			break;
		case CONDITION:
			if ((Boolean) inter(code.getConExpr())) {
				inter(result, code.getConTrue());
			} else {
				inter(result, code.getConFalse());
			}
			break;
		case FLOAT:
			result.element(code.getFloat());
			break;
		case FOR:
			doFor(result, code);
			return;
		case FUNCTION:
			if (code.getFunction() == Function.ADD)
				add(result, code);
			else if (code.getFunction() == Function.MIN)
				sub(result, code);
			else if (code.getFunction() == Function.ELEMENT && code.getFnParm1().getOperation() == Operation.ARRAY) {
				long number = getNumber(inter(code.getFnParm2()));
				if (number == Long.MIN_VALUE || Math.abs(number) > Integer.MAX_VALUE)
					result.element(null);
				else {
					ArrayArray array = code.getFnParm1().getArray();
					if (number < 0)
						number += array.getSize();
					array.setIdx((int) number);
					result.element(inter(array));
				}
			} else if (code.getFunction() == Function.ELEMENT && code.getFnParm1().getOperation() == Operation.OBJECT) {
				String fname = getString(inter(code.getFnParm2()));
				for (ObjectArray fld : code.getFnParm1().getObject()) {
					if (getString(inter(fld.getName())).equals(fname))
						inter(result, fld);
				}
			} else if (code.getFunction() == Function.ELEMENT && code.getFnParm1().getOperation() == Operation.CURRENT) {
				if (current instanceof Operator && ((Operator) current).getOperation() == Operation.OBJECT) {
					String fname = getString(inter(code.getFnParm2()));
					for (ObjectArray fld : ((Operator) current).getObject()) {
						if (getString(inter(fld.getName())).equals(fname))
							inter(result, fld);
					}
				} else if (current instanceof Operator && ((Operator) current).getOperation() == Operation.ARRAY) {
					long number = getNumber(inter(code.getFnParm2()));
					if (number == Long.MIN_VALUE || Math.abs(number) > Integer.MAX_VALUE)
						result.element(null);
					else {
						ArrayArray array = ((Operator) current).getArray();
						if (number < 0)
							number += array.getSize();
						array.setIdx((int) number);
						result.element(inter(array));
					}
				}
			} else {
				Object doFunction = doFunction(code);
				if (doFunction instanceof RecordInterface)
					iterate(result, (RecordInterface) doFunction);
				else
					result.element(doFunction);
			}
			break;
		case IF:
			break;
		case NULL:
			result.element(null);
			break;
		case NUMBER:
			result.element(code.getNumber());
			break;
		case OBJECT:
			result.startObject();
			for (ObjectArray o : code.getObject()) {
				result.field(inter(o.getName()).toString());
				inter(result, o);
			}
			result.endObject();
			break;
		case SORT:
			sort(result, code);
			break;
		case STRING:
			result.element(code.getString());
			break;
		case CURRENT:
			result.element(current);
			break;
		case READ:
			iterate(result, data);
			break;
		default:
			break;
		}
	}

	private void iterate(Writer write, RecordInterface rec) {
		if (rec.type() == FieldType.OBJECT)
			iterate(3, write, null, rec);
		else
			nonObject(3, write, rec, 0);
	}

	private void iterate(int maxDepth, Writer write, String name, RecordInterface rec) {
		if (maxDepth <= 0 || !rec.exists())
			return;
		int f = rec.next(-1);
		if (f == 0) {
			nonObject(maxDepth, write, rec, 0);
			return;
		}
		if (name != null)
			write.field(name);
		write.startObject();
		for (int field = f; field > 0; field = rec.next(field)) {
			if (rec.type(field) == FieldType.OBJECT)
				iterate(maxDepth-1, write, rec.name(field), (RecordInterface) rec.get(field));
			else
				nonObject(maxDepth, write, rec, field);
		}
		write.endObject();
	}

	private void nonObject(int maxDepth, Writer write, RecordInterface rec, int field) {
		String name = rec.name(field);
		FieldType type = rec.type(field);
		if (type == FieldType.ITERATE) {
			if (name != null)
				write.field(name);
			write.startArray();
			Iterable<? extends RecordInterface> iterate = rec.iterate(field);
			if (iterate != null) {
				for (RecordInterface sub : iterate) {
					if (sub.type() == FieldType.OBJECT)
						iterate(maxDepth, write, null, sub);
					else
						nonObject(maxDepth, write, sub, 0);
				}
			}
			write.endArray();
		} else {
			if (name != null)
				write.field(name);
			write.element(rec.get(field));
		}
	}

	@SuppressWarnings("unused")
	private class Pair {
		final Operator code;
		int op; // <0 out of scope, 1:default, 2:override, 3:remove

		public Pair(Operator code, int op) {
			this.code = code;
			this.op = op;
		}
	}

	private void iterate(int maxDepth, Writer write, String name, RecordInterface rec, int ops) {
		if (maxDepth <= 0 || !rec.exists())
			return;
		int f = rec.next(-1);
		if (f == 0) {
			nonObject(maxDepth, write, rec, 0, ops);
			return;
		}
		if (name != null)
			write.field(name);
		write.startObject();
		for (int field = f; field >= 0; field = rec.next(field)) {
			if (rec.type(field) == FieldType.OBJECT)
				iterate(maxDepth-1, write, rec.name(field), (RecordInterface) rec.get(field), ops);
			else
				nonObject(maxDepth, write, rec, field, ops);
		}
		write.endObject();
	}

	private void nonObject(int maxDepth, Writer write, RecordInterface rec, int field, int ops) {
		String name = rec.name(field);
		if (name != null)
			write.field(name);
		if (rec.type(field) == FieldType.ITERATE) {
			write.startArray();
			Iterable<? extends RecordInterface> iterate = rec.iterate(field);
			if (iterate != null) {
				for (RecordInterface sub : iterate)
					iterate(maxDepth, write, null, sub, ops);
			}
			write.endArray();
		} else {
			write.element(rec.get(field));
		}
	}

	private void doFor(Writer result, Operator code) {
		result.startArray();
		Expr forData = code.getFor();
		boolean firstForValue = true;
		int forIndex = 0;
		if (forData.getOperation() == Operation.ARRAY) {
			for (ArrayArray elm : forData.getArray()) {
				this.current = elm;
				this.first = firstForValue;
				this.index = forIndex;
				this.last = forIndex < elm.getSize();
				inter(result, code.getForExpr());
				firstForValue = false;
				forIndex++;
			}
		} else if (forData.getOperation() == Operation.OBJECT) {
			for (ObjectArray fld : forData.getObject()) {
				this.current = fld.change();
				this.first = firstForValue;
				this.index = forIndex;
				this.last = false;
				this.curName = getString(inter(fld.getName()));
				inter(result, code.getForExpr());
				firstForValue = false;
				forIndex++;
			}
		} else {
			Object expr = inter(forData);
			if (expr instanceof String) {
				String str = getString(expr);
				for (int c = 0; c < str.length(); c++) {
					current = "" + str.charAt(c);
					this.first = firstForValue;
					this.index = c;
					this.last = c == (str.length() - 1);
					inter(result, code.getForExpr());
					firstForValue = false;
				}
			} else if (expr instanceof Long) {
				long nr = getNumber(expr);
				for (int c = 0; c < nr; c++) {
					current = c;
					this.first = firstForValue;
					this.index = c;
					this.last = c == nr - 1;
					inter(result, code.getForExpr());
					firstForValue = false;
				}
			}
		}
		result.endArray();
	}

	private void slice(Writer result, Operator code) {
		// loop per 3 parameters
		result.startArray();
		for (int sparm = 1; sparm < code.getCallParms().getSize(); sparm += 3) {
			CallParmsArray parm = code.getCallParms(0);
			long start = getNumber(inter(code.getCallParms(sparm)));
			long stop = getNumber(inter(code.getCallParms(sparm + 1)));
			long step = getNumber(inter(code.getCallParms(sparm + 2)));
			if (step == Long.MIN_VALUE)
				step = 1;
			if (parm.getOperation() == Operation.ARRAY) {
				ArrayArray array = parm.getArray();
				int size = array.getSize();
				if (step < 0) {
					if (start == Long.MIN_VALUE)
						start = size - 1;
					if (start < 0)
						start += size;
					if (stop < 0 && stop != Long.MIN_VALUE)
						stop += size;
					if (stop == Long.MIN_VALUE)
						stop = -1;
					for (long i = start; i > stop; i += step) {
						array.setIdx((int) i);
						result.element(inter(array));
					}
				} else {
					if (start == Long.MIN_VALUE)
						start = 0;
					else if (start < 0)
						start += size;
					if (stop == Long.MAX_VALUE)
						stop = start + 1;
					if (stop < 0 && stop != Long.MIN_VALUE)
						stop += size;
					if (stop == Long.MIN_VALUE)
						stop = size;
					for (long i = start; i < stop; i += step) {
						array.setIdx((int) i);
						result.element(inter(array));
					}
				}
			} else {
				String str = getString(inter(parm));
				StringBuilder bld = new StringBuilder();
				int size = str.length();
				if (step < 0) {
					if (start < 0)
						start += size;
					if (stop < 0 && stop != Long.MIN_VALUE)
						stop += size;
					if (stop == Long.MIN_VALUE)
						stop = -1;
					for (long i = start; i > stop; i += step)
						bld.append(str.charAt((int) i));
				} else {
					if (start < 0)
						start += size;
					if (stop < 0)
						stop += size;
					for (long i = start; i < stop; i += step)
						bld.append(str.charAt((int) i));
				}
				result.element(bld.toString());
			}
		}
		result.endArray();
	}

	/*private Object slice(Operator code) {
		long start = getNumber(inter(code.getCallParms(1)));
		long stop = getNumber(inter(code.getCallParms(2)));
		long step = getNumber(inter(code.getCallParms(3)));
		String str = getString(inter(code.getCallParms(0)));
		StringBuilder bld = new StringBuilder();
		int size = str.length();
		if (step < 0) {
			if (start < 0)
				start += size;
			if (stop < 0 && stop != Long.MIN_VALUE)
				stop += size;
			if (stop == Long.MIN_VALUE)
				stop = -1;
			for (long i = start; i > stop; i += step)
				bld.append(str.charAt((int) i));
		} else {
			if (start < 0)
				start += size;
			if (stop < 0)
				stop += size;
			for (long i = start; i < stop; i += step)
				bld.append(str.charAt((int) i));
		}
		return bld.toString();
	}*/

	private void sort(Writer result, Operator code) {
		Expr sort = code.getSort();
		int size = code.getSortParms().getSize();
		List<ChangeOperator> list = new ArrayList<>();
		for (ArrayArray a : sort.getArray())
			list.add(a);
		list.sort((e1, e2) -> {
			for (int idx = 0; idx < size; idx++) {
				current = e1;
				Object r1 = inter(code.getSortParms(idx));
				current = e2;
				Object r2 = inter(code.getSortParms(idx));
				int r = compare(r1, r2);
				if (r != 0)
					return r;
			}
			return 0;
		});
		for (ChangeOperator e : list)
			inter(result, e);
	}

	private int compare(Object t1, Object t2) {
		if (t1 == null && t2 == null)
			return 0;
		if (t1 == null)
			return -1;
		if (t2 == null)
			return 1;
		if (t1 instanceof String)
			return getString(t1).compareTo(getString(t2));
		if (t1 instanceof Long)
			return Long.compare(getNumber(t1), getNumber(t2));
		if (t1 instanceof Double)
			return Double.compare(getFloat(t1), getFloat(t2));
		if (t1 instanceof Boolean || t2 instanceof Boolean) {
			if (getBoolean(t1) == getBoolean(t2))
				return 0;
			return getBoolean(t1) ? 1 : -1;
		}
		return 0;
	}

	private void add(Writer result, Operator code) {
		code.getType();
		Expr fnParm1 = code.getFnParm1();
		Expr fnParm2 = code.getFnParm2();
		if (fnParm1.getOperation() == Operation.ARRAY) {
			result.startArray();
			for (ArrayArray a : fnParm1.getArray())
				inter(result, a);
			if (fnParm2.getOperation() == Operation.ARRAY)
				for (ArrayArray a : fnParm2.getArray())
					inter(result, a);
			else
				inter(result, fnParm2);
			result.endArray();
			return;
		}
		if (fnParm1.getOperation() == Operation.OBJECT) {
			result.startObject();
			if (fnParm2.getOperation() == Operation.OBJECT) {
				Set<String> found = new TreeSet<>();
				ObjectArray obj2 = fnParm2.getObject();
				Field: for (ObjectArray a : fnParm1.getObject()) {
					String name = inter(a.getName()).toString();
					result.field(name);
					for (ObjectArray o : obj2) {
						String oth = inter(o.getName()).toString();
						if (name.equals(oth)) {
							found.add(name);
							inter(result, o);
							continue Field;
						}
					}
					inter(result, a);
				}
				for (ObjectArray a : obj2) {
					String name = inter(a.getName()).toString();
					if (!found.contains(name)) {
						result.field(inter(a.getName()).toString());
						result.element(inter(a));
					}
				}
			} else {
				return;
			}
			result.endObject();
			return;
		}
		result.element(doFunction(code));
	}

	private void sub(Writer result, Operator code) {
		result.element(doFunction(code));
	}

	private Object doFunction(Operator code) {
		Object p1 = inter(code.getFnParm1());
		Object p2 = null;
		Function function = code.getFunction();
		if (function != Function.FILTER && function != Function.OR && function != Function.AND && code.getFnParm2().getRec() != 0)
			p2 = inter(code.getFnParm2());
		switch (function) {
		case ADD:
			if (p1 instanceof String) {
				return ((String) p1) + getString(p2);
			} else if (p1 instanceof Long) {
				return ((Long) p1) + getNumber(p2);
			} else if (p1 instanceof Double) {
				return ((Double) p1) + getFloat(p2);
			} else if (p1 instanceof RecordInterface) {
				return dataAdd((RecordInterface) p1, p2);
			}
			return null;
		case AND:
			if (!getBoolean(p1))
				return false;
			return inter(code.getFnParm2());
		case BOOLEAN:
			return getBoolean(p1);
		case DIV:
			if (p1 instanceof Long) {
				long d = getNumber(p2);
				if (d == Long.MIN_VALUE || d == 0)
					return Long.MIN_VALUE;
				return ((Long) p1) / d;
			} else if (p1 instanceof Double) {
				double d = getFloat(p2);
				if (d == 0 || Double.isNaN(d))
					return null;
				return ((Double) p1) / d;
			}
			return null;
		case ELEMENT:
			if (p1 instanceof Operator && (((Operator) p1).getOperation() == Operation.OBJECT)) {
				Operator op = (Operator) p1;
				if (p2 instanceof Operator) {
					String fname = getString(inter((Operator) p2));
					for (ObjectArray fld : op.getObject()) {
						if (getString(inter(fld.getName())).equals(fname))
							return inter(fld);
					}
					return null;
				}
			} else if (p1 instanceof RecordInterface) {
				RecordInterface rec = (RecordInterface) p1;
				if (p2 instanceof Long) {
					long s = getNumber(p2);
					if (s == Long.MIN_VALUE || Math.abs(s) > Integer.MAX_VALUE || rec.name(1 + (int) s) != null || rec.type(1 + (int) s) == null)
						return null;
					if (s < 0)
						s += rec.size();
					return rec.get(1 + (int) s);
				} else if (p2 instanceof String) {
					String name = getString(p2);
					if (name == null)
						return null;
					int res = rec.scanName(name);
					if (res == -1)
						return null;
					return rec.get(res);
				}
			} else if (p1 instanceof String) {
				long s = getNumber(p2);
				if (s == Long.MIN_VALUE || Math.abs(s) > Integer.MAX_VALUE)
					return null;
				String str = getString(p1);
				if (s < 0)
					s += str.length();
				if (s < 0 || s >= str.length())
					return null;
				return "" + str.charAt((int) s);
			}
			return null;
		case EQ:
			if (p1 instanceof Long)
				return (Long) p1 == getNumber(p2);
			else if (p1 instanceof Double)
				return (Double) p1 == getFloat(p2);
			else if (p1 instanceof String)
				return ((String) p1).equals(getString(p2));
			else if (p1 instanceof Boolean)
				return ((Boolean) p1) == getBoolean(p2);
			return false;
		case FIRST:
			return first;
		case FILTER:
			/*
			if (res == Type.ARRAY) {
				result.setType(res);
				for (org.memorydb.json.ArrayArray a : p1.getArray()) {
					current = a;
					inter(p2, code.getFnParm2());
					if (p2.isBoolean())
						arrayAdd(result, a);
				}
			}
			*/
			return null;
		case FLOAT:
			return getFloat(p1);
		case GE:
			if (p1 instanceof Long)
				return (Long) p1 >= getNumber(p2);
			else if (p1 instanceof Double)
				return (Double) p1 >= getFloat(p2);
			else if (p1 instanceof String)
				return ((String) p1).compareTo(getString(p2)) >= 0;
			return null;
		case GT:
			if (p1 instanceof Long)
				return (Long) p1 > getNumber(p2);
			else if (p1 instanceof Double)
				return (Double) p1 > getFloat(p2);
			else if (p1 instanceof String)
				return ((String) p1).compareTo(getString(p2)) > 0;
			return null;
		case INDEX:
			return index;
		/*
		if (res == Type.STRING) {
			String str = p1.getValue();
			int indexOf = str.indexOf(getString(p2));
			if (indexOf < 0)
				result.setType(Type.NULL);
			else
				result.setNumber(indexOf);
		} else if (res == Type.ARRAY) {
			int i = 0;
			for (org.memorydb.json.ArrayArray a : p1.getArray()) {
				if (equal(a, p2)) {
					result.setNumber(i);
					break;
				}
				i++;
			}
		}*/
		case LAST:
			return last;
		case LE:
			if (p1 instanceof Long)
				return (Long) p1 <= getNumber(p2);
			else if (p1 instanceof Double)
				return (Double) p1 <= getFloat(p2);
			else if (p1 instanceof String)
				return ((String) p1).compareTo(getString(p2)) <= 0;
			return null;
		case LENGTH:
			if (p1 instanceof String)
				return ((String) p1).length();
			if (p1 instanceof RecordInterface)
				return ((RecordInterface) p1).size();
			return null;
		case NAME:
			return curName;
		case TYPE:
			if (p1 == null)
				return "NULL";
			else if (p1 instanceof Long)
				return "NUMBER";
			else if (p1 instanceof Double)
				return "FLOAT";
			else if (p1 instanceof String)
				return "STRING";
			else if (p1 instanceof Boolean)
				return "BOOLEAN";
			else if (p1 instanceof RecordInterface)
				return "OBJECT"; // TODO implement distinction between array or object
			return null;
		case LT:
			if (p1 instanceof Long)
				return (Long) p1 < getNumber(p2);
			else if (p1 instanceof Double)
				return (Double) p1 < getFloat(p2);
			else if (p1 instanceof String)
				return ((String) p1).compareTo(getString(p2)) < 0;
			return null;
		case MIN:
			if (p1 instanceof Long) {
				long number = getNumber(p2);
				if ((Long) p1 == Long.MIN_VALUE || number == Long.MIN_VALUE)
					return null;
				return (Long) p1 - number;
			} else if (p1 instanceof Double)
				return (Double) p1 - getFloat(p2);
			return null;
		case MOD:
			if (p1 instanceof Long) {
				long number = getNumber(p2);
				if (number == 0 || number == Long.MIN_VALUE)
					return null;
				return (Long) p1 % number;
			} else if (p1 instanceof Double) {
				double fl = getFloat(p2);
				if (fl == 0)
					return null;
				return (Double) p1 % fl;
			}
			return null;
		case MUL:
			if (p1 instanceof String) {
				long number = getNumber(p2);
				if (number == Long.MIN_VALUE || Math.abs(number) > Integer.MAX_VALUE)
					return null;
				return new String(new char[(int) number]).replace("\0", (String) p1);
			} else if (p1 instanceof Long) {
				long number = getNumber(p2);
				if ((Long) p1 == Long.MIN_VALUE || number == Long.MIN_VALUE)
					return null;
				return (Long) p1 * number;
			} else if (p1 instanceof Double) {
				return (Double) p1 * getFloat(p2);
			}
			return null;
		case NE:
			if (p1 instanceof Long)
				return (Long) p1 != getNumber(p2);
			else if (p1 instanceof Double)
				return (Double) p1 != getFloat(p2);
			else if (p1 instanceof String)
				return !((String) p1).equals(getString(p2));
			else if (p1 instanceof Boolean)
				return ((Boolean) p1) != getBoolean(p2);
			return false;
		case NEG:
			if (p1 instanceof Long)
				return -(Long) p1;
			else if (p1 instanceof Double)
				return -(Double) p1;
			return null;
		case NOT:
			return !getBoolean(p1);
		case NUMBER:
			return getNumber(p1);
		case OR:
			if (getBoolean(p1))
				return true;
			return inter(code.getFnParm2());
		case POW:
			if (p1 instanceof Long)
				return powLong((Long) p1, getNumber(p2));
			else if (p1 instanceof Double)
				return Math.pow((Double) p1, getFloat(p2));
			return null;
		case STRING:
			return getString(p1);
		default:
			break;
		}
		return null;
	}

	private static long powLong(long number, long power) {
		long res = 1;
		long sq = number;
		while (power > 0) {
			if (power % 2 == 1)
				res *= sq;
			sq = sq * sq;
			power /= 2;
		}
		return res;
	}

	/**
	 * @param result  
	 * @param a 
	 */
	private RecordInterface dataAdd(RecordInterface result, Object a) {
		// TODO implement me
		return null;
	}

	private long getNumber(Object val) {
		if (val instanceof Long)
			return (Long) val;
		return Long.MIN_VALUE;
	}

	private String getString(Object val) {
		if (val instanceof Long)
			return Long.toString((Long) val);
		else if (val instanceof Double)
			return Double.toString((Double) val);
		return val.toString();
	}

	private boolean getBoolean(Object val) {
		if (val instanceof Boolean)
			return (Boolean) val;
		if (val instanceof Long)
			return (Long) val != Long.MIN_VALUE;
		if (val instanceof Double)
			return !Double.isNaN((Double) val);
		return val != null;
	}

	private double getFloat(Object val) {
		if (val instanceof Long)
			return (Long) val;
		if (val instanceof Double)
			return (Double) val;
		return Double.NaN;
	}
}
