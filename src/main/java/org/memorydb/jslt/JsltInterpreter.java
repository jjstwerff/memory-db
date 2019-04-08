package org.memorydb.jslt;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.memorydb.handler.Dir;
import org.memorydb.handler.StringWriter;
import org.memorydb.handler.Text;
import org.memorydb.handler.Writer;
import org.memorydb.jslt.Operator.Function;
import org.memorydb.jslt.Operator.Operation;
import org.memorydb.structure.RecordInterface;
import org.memorydb.structure.RecordInterface.FieldType;
import org.memorydb.structure.Store;

public class JsltInterpreter {
	private final List<Object> stack = new ArrayList<>(100);
	private final List<MatchError> errors = new ArrayList<>(); // only initialize on actual errors
	private int index;
	private Object current;
	private boolean first;
	private boolean last;
	private String curName;
	private RecordInterface data;
	private int stackFrame = 0;
	private RecordInterface curFor = null;
	private Object running = null;
	private Dir dir = null;

	public static String interpret(Store jsltStore, RecordInterface data, Dir dir) {
		return interpret(jsltStore, data, dir, null);
	}

	public static String interpret(Store jsltStore, RecordInterface data, Dir dir, List<String> errors) {
		JsltInterpreter inter = new JsltInterpreter();
		inter.dir = dir;
		inter.data = data;
		Writer write = new StringWriter();
		int main = new Macro.IndexMacros(jsltStore, "main").search();
		for (CodeArray code : new Macro(jsltStore, main).getAlternatives(0).getCode())
			inter.show(write, inter.inter(code));
		if (errors != null)
			for (MatchError err : inter.errors)
				errors.add(err.getMessage());
		return write.toString();
	}

	public void setDir(Dir dir) {
		this.dir = dir;
	}

	public void setCurrent(Object current) {
		this.current = current;
	}

	public void setCurName(String curName) {
		this.curName = curName;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setFirst(boolean first) {
		this.first = first;
	}

	public void setLast(boolean last) {
		this.last = last;
	}

	public void setStack(int pos, Object val) {
		while (stackFrame + pos >= stack.size())
			stack.add(null);
		stack.set(stackFrame + pos, val);
	}

	public Object inter(Operator code) {
		if (code == null || code.getOperation() == null)
			return null;
		switch (code.getOperation()) {
		case ARRAY:
			return new InterArray(this, code.getArray(), -1);
		case OBJECT:
			return new InterObject(this, code.getObject(), -1);
		case APPEND:
			StringBuilder bld = new StringBuilder();
			for (AppendArray app : code.getAppend())
				bld.append(getString(inter(app)));
			return bld.toString();
		case BOOLEAN:
			return code.isBoolean();
		case CALL:
			return macro(code);
		case CONDITION:
			return inter((Boolean) inter(code.getConExpr()) ? code.getConTrue() : code.getConFalse());
		case FLOAT:
			return code.getFloat();
		case FUNCTION:
			return doFunction(code);
		case RUNNING:
			return running;
		case FILTER:
			return new InterFilter(this, code.getFilterExpr(), (RecordInterface) inter(code.getFilter()));
		case IF:
			return null; // TODO implement me
		case NULL:
			return null;
		case NUMBER:
			return code.getNumber();
		case SORT:
			return new InterSort(this, code.getSortParms(), (RecordInterface) inter(code.getSort()));
		case STRING:
			return code.getString();
		case VARIABLE:
			return stack.get(stackFrame + code.getVarNr());
		case CURRENT:
			if (current instanceof Operator)
				return inter((Operator) current);
			return current;
		case READ:
			if (code.getListenSource().startsWith("/"))
				return dir == null ? null : dir.file(code.getListenSource().substring(1));
			return data;
		default:
			return null;
		}
	}

	private Object macro(Operator code) {
		Macro macro = code.getMacro();
		if (!macro.getName().equals("slice"))
			return new MatchMacro(this, code).match();
		Object parmData = inter(new CallParmsArray(code.getCallParms(), 0));
		if (parmData instanceof RecordInterface)
			return new InterSlice(this, (RecordInterface) parmData, code.getCallParms());
		if (parmData instanceof String)
			return subString((String) parmData, code.getCallParms());
		if (parmData instanceof Text)
			return subString((Text) parmData, code.getCallParms());
		return null;
	}

	private String subString(Text text, CallParmsArray callParms) {
		int startPos = text.addPos();
		long[] parms = new long[callParms.size() - 1];
		for (int p = 0; p < callParms.size() - 1; p++)
			parms[p] = getNumber(inter(new CallParmsArray(callParms, p + 1)));
		StringBuilder res = new StringBuilder();
		int pos = 0;
		for (int p = 0; p < parms.length; p += 3) {
			long start = parms[p];
			long stop = parms[p + 1];
			long step = parms[p + 2];
			if (step == Long.MIN_VALUE)
				step = 1;
			if (start == Long.MIN_VALUE)
				start = 0;
			if (start > 0 && pos > start) {
				text.toPos(startPos);
				pos = 0;
			}
			while (pos < start) {
				text.readChar();
				pos++;
			}
			while (pos < stop) {
				res.append(text.readChar());
				for (int i = 1; i < step; i++) {
					text.readChar();
					pos++;
				}
				pos++;
			}
		}
		return res.toString();
	}

	private String subString(String str, CallParmsArray callParms) {
		long[] parms = new long[callParms.size() - 1];
		for (int p = 0; p < callParms.size() - 1; p++)
			parms[p] = getNumber(inter(new CallParmsArray(callParms, p + 1)));
		StringBuilder res = new StringBuilder();
		int size = str.length();
		for (int p = 0; p < parms.length; p += 3) {
			long start = parms[p];
			long stop = parms[p + 1];
			long step = parms[p + 2];
			if (step == Long.MIN_VALUE)
				step = 1;
			if (step < 0) {
				if (start == Long.MIN_VALUE)
					start = size - 1;
				if (start < 0)
					start += size;
				if (stop < 0 && stop != Long.MIN_VALUE)
					stop += size;
				if (stop == Long.MIN_VALUE)
					stop = -1;
				for (int i = (int) start; i > stop; i += step)
					res.append(str.charAt(i));
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
				for (int i = (int) start; i < stop; i += step)
					res.append(str.charAt(i));
			}
		}
		return res.toString();
	}

	private void iterate(Writer write, RecordInterface rec) {
		FieldType type = rec.type();
		if (type == FieldType.OBJECT)
			iterateObject(3, write, rec);
		else if (type == FieldType.ARRAY)
			iterateArray(3, write, rec);
	}

	private void show(Writer write, Object obj) {
		if (obj instanceof RecordInterface)
			iterate(write, (RecordInterface) obj);
		else
			write.element(obj);
	}

	private void iterateObject(int maxDepth, Writer write, RecordInterface rec) {
		if (maxDepth <= 0 || rec == null)
			return;
		write.startObject();
		RecordInterface elm = rec.start();
		while (elm != null) {
			write.field(elm.name());
			if (elm.type() == FieldType.OBJECT)
				iterateObject(maxDepth - 1, write, (RecordInterface) elm.java());
			else if (elm.type() == FieldType.ARRAY)
				iterateArray(maxDepth, write, (RecordInterface) elm.java());
			else
				write.element(elm.java());
			elm = elm.next();
		}
		write.endObject();
	}

	private void iterateArray(int maxDepth, Writer write, RecordInterface rec) {
		write.startArray();
		RecordInterface elm = rec.start();
		while (elm != null) {
			if (elm.type() == FieldType.OBJECT)
				iterateObject(maxDepth, write, (RecordInterface) elm.java());
			else if (elm.type() == FieldType.ARRAY)
				iterateArray(maxDepth, write, (RecordInterface) elm.java());
			else
				write.element(elm.java());
			elm = elm.next();
		}
		write.endArray();
	}

	private void iterate(int maxDepth, Writer write, String name, RecordInterface rec, int ops) {
		if (maxDepth <= 0)
			return;
		RecordInterface f = rec.start();
		if (f == null) {
			nonObject(maxDepth, write, rec, ops);
			return;
		}
		if (name != null)
			write.field(name);
		write.startObject();
		RecordInterface elm = rec.start();
		while (elm != null) {
			if (elm.type() == FieldType.OBJECT)
				iterate(maxDepth - 1, write, elm.name(), elm, ops);
			else
				nonObject(maxDepth, write, elm, ops);
			elm = elm.next();
		}
		write.endObject();
	}

	private void nonObject(int maxDepth, Writer write, RecordInterface elm, int ops) {
		String name = elm.name();
		if (name != null)
			write.field(name);
		if (elm.type() == FieldType.ARRAY) {
			write.startArray();
			while (!elm.testLast())
				iterate(maxDepth, write, null, elm, ops);
			write.endArray();
		} else {
			write.element(elm);
		}
	}

	int compare(Object t1, Object t2) {
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
		if (t1 instanceof RecordInterface && t2 instanceof RecordInterface) {
			RecordInterface r1 = (RecordInterface) t1;
			RecordInterface r2 = (RecordInterface) t2;
			if (r1.type() == FieldType.ARRAY && r2.type() == FieldType.ARRAY) {
				RecordInterface e1 = r1.start();
				RecordInterface e2 = r2.start();
				while (e1 != null || e2 != null) {
					int c = compare(e1.java(), e2.java());
					if (c != 0)
						return c;
					e1 = e1.next();
					e2 = e2.next();
				}
			}
		} else if (t1 instanceof RecordInterface)
			return compare(((RecordInterface)t1).java(), t2);
		return 0;
	}

	private Object doFunction(Operator code) {
		Object p1 = inter(code.getFnParm1());
		Object p2 = null;
		Function function = code.getFunction();
		if (function != Function.EACH && function != Function.FOR && function != Function.PER && function != Function.OR && function != Function.AND
				&& code.getFnParm2().rec() != 0)
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
				RecordInterface r = ((RecordInterface) p1);
				if (r.type() == FieldType.OBJECT)
					return p2 instanceof RecordInterface && ((RecordInterface) p2).type() == FieldType.OBJECT ? new MergeObject(r, (RecordInterface) p2)
							: null;
				return new AddArray(r, p2, p2 instanceof RecordInterface && ((RecordInterface) p2).type() == FieldType.ARRAY);
			}
			return null;
		case AND:
			if (!getBoolean(p1))
				return false;
			return inter(code.getFnParm2());
		case BOOLEAN:
			return getBoolean(p1);
		case PER:
			return new InterMap(this, code.getFnParm2(), p1);
		case FOR:
			if (p1 instanceof String)
				curFor = new StringArray((String) p1, -1);
			else
				curFor = (RecordInterface) p1;
			Object resObj = inter(code.getFnParm2());
			return resObj;
		case EACH:
			running = p1;
			RecordInterface elm = curFor.start();
			while (elm != null) {
				current = elm.java();
				RecordInterface remFor = curFor;
				running = inter(code.getFnParm2());
				curFor = remFor;
				elm = elm.next();
			}
			return running;
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
					if (s == Long.MIN_VALUE || Math.abs(s) > Integer.MAX_VALUE)
						return null;
					if (s < 0)
						s += rec.size();
					RecordInterface iElm = rec.index((int) s);
					return iElm == null ? null : iElm.java();
				} else if (p2 instanceof String) {
					String name = getString(p2);
					if (name == null)
						return null;
					RecordInterface field = rec.field(name);
					return field == null ? null : field.java();
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
			else if (p1 instanceof RecordInterface || p2 instanceof RecordInterface)
				return compare(p1, p2) == 0;
			else if (p1 == null)
				return p2 == null || (p2 instanceof Long && (Long) p2 == Long.MIN_VALUE) || (p2 instanceof Double && Double.isNaN((Double) p2));
			return false;
		case FIRST:
			return first;
		case FLOAT:
			return getFloat(p1);
		case GE:
			if (p1 instanceof Long)
				return (Long) p1 >= getNumber(p2);
			else if (p1 instanceof Double)
				return (Double) p1 >= getFloat(p2);
			else if (p1 instanceof String)
				return ((String) p1).compareTo(getString(p2)) >= 0;
			else if (p1 instanceof RecordInterface || p2 instanceof RecordInterface)
				return compare(p1, p2) >= 0;
			return null;
		case GT:
			if (p1 instanceof Long)
				return (Long) p1 > getNumber(p2);
			else if (p1 instanceof Double)
				return (Double) p1 > getFloat(p2);
			else if (p1 instanceof String)
				return ((String) p1).compareTo(getString(p2)) > 0;
			else if (p1 instanceof RecordInterface || p2 instanceof RecordInterface)
				return compare(p1, p2) > 0;
			return null;
		case INDEX:
			if (p1 instanceof String && p2 != null) {
				int indexOf = ((String) p1).indexOf(getString(p2));
				if (indexOf < 0)
					return null;
				return Long.valueOf(indexOf);
			}
			return Long.valueOf(index);
		case LAST:
			return last;
		case LE:
			if (p1 instanceof Long)
				return (Long) p1 <= getNumber(p2);
			else if (p1 instanceof Double)
				return (Double) p1 <= getFloat(p2);
			else if (p1 instanceof String)
				return ((String) p1).compareTo(getString(p2)) <= 0;
			else if (p1 instanceof RecordInterface || p2 instanceof RecordInterface)
				return compare(p1, p2) <= 0;
			return null;
		case LENGTH:
			if (p1 == null && code.getFnParm1().getOperation() == Operation.ARRAY)
				return Long.valueOf(code.getFnParm1().getArray().size());
			if (p1 instanceof String)
				return Long.valueOf(((String) p1).length());
			if (p1 instanceof RecordInterface)
				return Long.valueOf(((RecordInterface) p1).size());
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
				return ((RecordInterface) p1).type().toString();
			return null;
		case LT:
			if (p1 instanceof Long)
				return (Long) p1 < getNumber(p2);
			else if (p1 instanceof Double)
				return (Double) p1 < getFloat(p2);
			else if (p1 instanceof String)
				return ((String) p1).compareTo(getString(p2)) < 0;
			else if (p1 instanceof RecordInterface || p2 instanceof RecordInterface)
				return compare(p1, p2) <= 0;
			return null;
		case MIN:
			if (p1 instanceof Long) {
				long number = getNumber(p2);
				if ((Long) p1 == Long.MIN_VALUE || number == Long.MIN_VALUE)
					return null;
				return (Long) p1 - number;
			} else if (p1 instanceof Double)
				return (Double) p1 - getFloat(p2);
			else if (p1 instanceof RecordInterface)
				return new MinArray((RecordInterface) p1, p2);
			return null;
		case MOD:
			if (p1 instanceof Long) {
				long number = getNumber(p2);
				if (number == 0 || number == Long.MIN_VALUE)
					return null;
				return ((Long) p1) % number;
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
			} else if (p1 instanceof RecordInterface) {
				return new InterMult((RecordInterface) p1, null, getNumber(p2));
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
			else if (p1 instanceof RecordInterface || p2 instanceof RecordInterface)
				return compare(p1, p2) != 0;
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
		case LAYOUT:
			return format((RecordInterface) p1, p2);
		default:
			break;
		}
		return null;
	}

	private String format(RecordInterface layout, Object obj) {
		if (obj instanceof Long || obj instanceof Double)
			return formatNumber(layout, obj);
		int width = -1;
		String align = "L";
		RecordInterface fld = layout.start();
		while (fld != null) {
			switch (fld.name()) {
			case "width":
				if (fld instanceof Operator)
					width = ((Long) inter((Operator) fld)).intValue();
				else
					width = (int) getNumber(fld.java());
				break;
			case "align":
				align = getString(fld.java());
				break;
			}
			fld = fld.next();
		}
		String res = getString(obj);
		if (align.equals("H")) {
			if (res.length() > width)
				return res.substring(0, width - 3) + "...";
			return res;
		} else if (align.equals("T")) {
			if (res.length() > width)
				return "..." + res.substring(res.length() - width + 3, res.length());
			return res;
		}
		if (width < 0 || width <= res.length())
			return res;
		int l = width - res.length();
		switch (align) {
		case "L":
			return res + multi(l, " ");
		case "R":
			return multi(l, " ") + res;
		case "C":
			int part = l / 2;
			return multi(part, " ") + res + multi(l - part, " ");
		}
		return null;
	}

	private String formatNumber(RecordInterface layout, Object obj) {
		int width = -1;
		int precision = -1;
		@SuppressWarnings("unused")
		String align = "R";
		String type = "g";
		String separator = null;
		boolean alternative = false;
		boolean leadingZero = false;
		RecordInterface fld = layout.start();
		while (fld != null) {
			switch (fld.name()) {
			case "width":
				Object w = fld.java();
				if (w instanceof Operator)
					width = ((Long) inter((Operator) w)).intValue();
				else
					width = ((Long) w).intValue();
				break;
			case "precision":
				Object p = fld.java();
				if (p instanceof Operator)
					precision = ((Long) inter((Operator) p)).intValue();
				else
					precision = ((Long) p).intValue();
				break;
			case "align":
				align = (String) fld.java();
				break;
			case "type":
				type = (String) fld.java();
				break;
			case "separator":
				separator = (String) fld.java();
				break;
			case "leadingZero":
				leadingZero = true;
				break;
			case "alternative":
				alternative = true;
				break;
			}
			fld = fld.next();
		}
		switch (type) {
		case "b": {
			String res = Long.toBinaryString(getNumber(obj));
			if (width > res.length())
				res = multi(width - res.length(), leadingZero ? "0" : " ") + res;
			if (separator != null)
				return (alternative ? "0b" : "") + separate(separator, res, 4);
			return (alternative ? "0b" : "") + res;
		}
		case "x": {
			String res = Long.toHexString(getNumber(obj));
			if (width > res.length())
				res = multi(width - res.length(), leadingZero ? "0" : " ") + res;
			if (separator != null)
				return (alternative ? "0x" : "") + separate(separator, res, 4);
			return (alternative ? "0x" : "") + res;
		}
		case "o": {
			String res = Long.toOctalString(getNumber(obj));
			if (width > res.length())
				res = multi(width - res.length(), leadingZero ? "0" : " ") + res;
			if (separator != null)
				return (alternative ? "0o" : "") + separate(separator, res, 3);
			return (alternative ? "0o" : "") + res;
		}
		}
		DecimalFormat formatter = null;
		if (type.equals("e"))
			formatter = new DecimalFormat("0.#E0");
		else if (type.equals("g")) {
			if (obj instanceof Double) {
				int exp = Math.getExponent((Double) obj);
				if (exp < -6 || exp > 6)
					formatter = new DecimalFormat("0.#E0");
			}
		}
		if (formatter == null)
			formatter = (DecimalFormat) NumberFormat.getInstance();
		DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
		symbols.setGroupingSeparator(',');
		symbols.setDecimalSeparator('.');
		symbols.setExponentSeparator("e");
		if (separator != null)
			symbols.setGroupingSeparator(separator.charAt(0));
		formatter.setDecimalFormatSymbols(symbols);
		if (obj instanceof Double) {
			formatter.setDecimalSeparatorAlwaysShown(true);
			formatter.setMinimumFractionDigits(1);
		}
		formatter.setMaximumFractionDigits(9);
		if (precision > 0) {
			formatter.setMaximumFractionDigits(precision);
			formatter.setMinimumFractionDigits(precision);
		}
		if (width > 0)
			formatter.setMinimumIntegerDigits(width);
		if (separator == null)
			formatter.setGroupingSize(0);
		else
			formatter.setGroupingSize(3);
		if (obj instanceof Long)
			return formatter.format(obj);
		return formatter.format(obj);
	}

	private String separate(String separator, String res, int spaces) {
		StringBuilder r = new StringBuilder();
		int i = res.length() % spaces;
		int l = 0;
		if (i == 0)
			i += 4;
		for (; i < res.length(); i += spaces) {
			r.append(res.substring(l, i));
			r.append(separator);
			l = i;
		}
		r.append(res.substring(l, res.length()));
		return r.toString();
	}

	private static String multi(int pos, String token) {
		return new String(new char[pos]).replace("\0", token);
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

	long getNumber(Object val) {
		if (val instanceof Long)
			return (Long) val;
		if (val instanceof Double)
			return Math.round((Double) val);
		if (val instanceof String) {
			try {
				return Long.parseLong((String) val);
			} catch (NumberFormatException e) {
				// nothing.. return Long.MIN_VALUE
			}
		}
		return Long.MIN_VALUE;
	}

	/* package private */ String getString(Object val) {
		if (val == null)
			return "";
		if (val instanceof Long) {
			Long l = (Long) val;
			return l == Long.MIN_VALUE ? "" : Long.toString(l);
		} else if (val instanceof Double)
			return Double.toString((Double) val);
		else if (val instanceof Text) {
			StringBuilder bld = new StringBuilder();
			Text text = (Text) val;
			int pos = text.addPos();
			while (!text.end())
				bld.append(text.readChar());
			text.toPos(pos);
			return bld.toString();
		} else if (val instanceof RecordInterface) {
			Writer write = new StringWriter();
			write.append(true);
			iterate(write, (RecordInterface) val);
			return write.toString();
		}
		return val.toString();
	}

	boolean getBoolean(Object val) {
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
		if (val instanceof String) {
			try {
				return Double.parseDouble((String) val);
			} catch (NumberFormatException e) {
				// nothing.. return Double.NaN
			}
		}
		return Double.NaN;
	}

	public static FieldType type(Object lastField) {
		if (lastField == null)
			return FieldType.NULL;
		if (lastField instanceof Integer)
			return FieldType.INTEGER;
		if (lastField instanceof Long)
			return FieldType.LONG;
		if (lastField instanceof Double)
			return FieldType.FLOAT;
		if (lastField instanceof String)
			return FieldType.STRING;
		if (lastField instanceof Date)
			return FieldType.DATE;
		if (lastField instanceof Boolean)
			return FieldType.BOOLEAN;
		if (lastField instanceof RecordInterface)
			return ((RecordInterface) lastField).type();
		throw new RuntimeException("Unknown type");
	}

	public int getStackFrame() {
		return stackFrame;
	}

	public void setStackFrame(int stackFrame) {
		this.stackFrame = stackFrame;
		while (stackFrame > stack.size())
			stack.add(null);
	}

	public Object getStackElement(int elm) {
		if (elm < 0 || elm >= stack.size())
			return null;
		return stack.get(elm);
	}

	public int getStackSize() {
		return stack.size();
	}

	public void clearStack(int startFrame) {
		while (stack.size() > startFrame)
			stack.remove(stack.size() - 1);
	}

	public void error(String error) {
		errors.add(new MatchError(error));
	}
}
