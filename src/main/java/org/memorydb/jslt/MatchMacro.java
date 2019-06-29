package org.memorydb.jslt;

import java.util.ArrayList;
import java.util.List;

import org.memorydb.handler.Text;
import org.memorydb.structure.RecordInterface;
import org.memorydb.structure.RecordInterface.FieldType;

public class MatchMacro {
	private enum State {
		COMPLETE, INCOMPLETE, MISSED
	};

	private final Macro macro;
	private final JsltInterpreter inter;
	private final int previousFrame;
	private final int startFrame;
	private final int maxParameter;
	private final List<RecordInterface> stack; // stack of pointers to parameters
	private Object cur; // currently selected object
	private RecordInterface data; // current data position
	private State state;
	private boolean doCheck; // check on complete iterators
	private boolean debug; // show debug information

	public MatchMacro(JsltInterpreter inter, Operator code) {
		this.inter = inter;
		this.macro = code.getMacro();
		this.previousFrame = inter.getStackFrame();
		this.startFrame = inter.getStackSize();
		inter.setStackFrame(startFrame);
		CallParmsArray callParms = code.getCallParms();
		this.maxParameter = callParms.size();
		int i = 0;
		for (CallParmsArray parm : callParms)
			inter.setStack(i++, inter.inter(parm));
		this.stack = new ArrayList<>();
		this.state = State.INCOMPLETE;
		this.doCheck = true;
		this.debug = false;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public MatchMacro(JsltInterpreter inter, MatchingArray code, Object cur) {
		this.inter = inter;
		this.macro = code.getMacro();
		this.previousFrame = inter.getStackFrame();
		this.startFrame = inter.getStackSize();
		ParmsArray callParms = code.getParms();
		inter.setStackFrame(startFrame);
		inter.setStack(0, cur);
		this.maxParameter = 1 + callParms.size();
		int i = 1;
		for (ParmsArray parm : callParms)
			inter.setStack(i++, inter.inter(parm));
		this.stack = new ArrayList<>();
		this.state = State.INCOMPLETE;
		this.doCheck = false;
	}

	public Object match() {
		// System.out.println("match: " + macro.getName() + " startFrame:" + startFrame
		// + " stackFrame:" + inter.getStackFrame());
		int step = 0;
		MatchingArray code = macro.getMatching();
		MatchingArray matching = code.start();
		while (true) {
			if (step++ > 100000)
				return null;
			if (matching == null)
				throw new RuntimeException("Not correctly matching macro " + macro.getName());
			if (debug)
				System.out.print(" " + matching.index() + ":" + matching);
			switch (matching.getType()) {
			case ALT:
				Alternative alt = new Alternative(macro.store(), matching.getAltnr());
				if (alt.getIf().rec() <= 0 || (Boolean) inter.inter(alt.getIf())) {
					if (doCheck && cur != null && cur instanceof RecordInterface) {
						inter.error("Incomplete matching " + macro.getName());
						clearStack();
						return null;
					}
					Object res = inter.inter(alt.getCode(0));
					Variable avar = matching.getAvar();
					if (avar.rec() == 0) {
						clearStack();
						return res;
					}
					inter.setStack(startFrame + avar.getNr(), res);
					matching = matching.next();
				} else
					matching = code.index(matching.getAfalse());
				break;
			case ERROR:
				inter.error(matching.getError());
				clearStack();
				return null;
			case FIELD:
				RecordInterface rec = cur instanceof RecordInterface ? (RecordInterface) cur : null;
				if (rec != null && rec.type() == FieldType.OBJECT)
					data = rec.field(matching.getField()); // search the field name
				if (data != null) {
					cur = rec.java(); // get the field value
					matching = matching.next(); // continue with the next match
				} else
					matching = code.index(matching.getFfalse()); // Element doesn't have the specified field
				break;
			case STEP:
				RecordInterface ri = cur instanceof RecordInterface ? (RecordInterface) cur : null;
				boolean problem = false;
				switch (matching.getStep()) {
				case START:
					if (cur instanceof String)
						ri = new StringArray((String) cur, -1).start();
					else if (cur instanceof RecordInterface)
						ri = ((RecordInterface) cur).start();
					problem = ri == null;
					state = ri == null ? State.MISSED : ri.testLast() ? State.COMPLETE : State.INCOMPLETE;
					break;
				case FINISH:
					problem = ri != null; // the last next should have been the last
					break;
				case FORWARD:
					ri = ri == null ? null : ri.next();
					problem = ri == null;
					state = ri == null ? State.MISSED : ri.testLast() ? State.COMPLETE : State.INCOMPLETE;
					break;
				case BACK:
					// ri = ri == null ? null : ri.previous;
					problem = ri == null;
					break;
				}
				if (problem && matching.getMissed() != Integer.MIN_VALUE)
					matching = code.index(matching.getMissed());
				else
					matching = matching.next();
				cur = ri;
				break;
			case JUMP:
				switch (matching.getJump()) {
				case CALL:
					stack.add(matching);
					matching = code.index(matching.getPosition());
					break;
				case COMPLETE:
					state = State.COMPLETE;
					matching = stackPop();
					if (matching == null) {
						clearStack();
						return cur;
					}
					break;
				case INCOMPLETE:
					state = State.INCOMPLETE;
					matching = stackPop();
					if (matching == null) {
						clearStack();
						return cur;
					}
					break;
				case MISS:
					state = State.MISSED;
					matching = stackPop();
					if (matching == null) {
						if (doCheck) {
							inter.error("No matching macro named '" + macro.getName() + "' found");
							cur = null;
						}
						clearStack();
						return cur;
					} else
						matching = matching.next();
					break;
				case RETURN:
					matching = stackPop();
					if (matching == null) {
						clearStack();
						return cur;
					}
					break;
				case CONDITIONAL:
					if (state != State.MISSED)
						matching = code.index(matching.getPosition());
					else
						matching = matching.next();
					break;
				case CONTINUE:
					matching = code.index(matching.getPosition());
					break;
				case MISSED:
					if (state == State.MISSED)
						matching = code.index(matching.getPosition());
					else
						matching = matching.next();
					break;
				}
				break;
			case PARM: // switch to a specific original parameter
				if (matching.getParm() >= maxParameter)
					matching = code.index(matching.getPfalse());
				else {
					cur = inter.getStackElement(startFrame + matching.getParm());
					matching = matching.next();
				}
				break;
			case PUSH:
				if (cur != null && !(cur instanceof RecordInterface))
					throw new RuntimeException("Not a parameter pointer to put on stack: found:" + cur + //
							(cur == null ? "" : " of type " + cur.getClass().getSimpleName()));
				stack.add((RecordInterface) cur);
				matching = matching.next();
				break;
			case POP:
				if (matching.isPopread())
					cur = stack.get(stack.size() - 1);
				stack.remove(stack.size() - 1); // remove last element from stack
				matching = matching.next();
				break;
			case STACK: // add (when getStack() positive) or remove elements from the stack
				inter.setStackFrame(inter.getStackFrame() + matching.getStack());
				matching = matching.next();
				break;
			case TEST_BOOLEAN:
				Object test = cur instanceof RecordInterface ? ((RecordInterface) cur).java() : cur;
				if (test instanceof Boolean && (Boolean) test == matching.isMboolean())
					matching = matching.next();
				else
					matching = code.index(matching.getMbfalse());
				break;
			case CALL:
				MatchMacro m = new MatchMacro(inter, matching, cur);
				state = m.state;
				cur = m.match();
				if (state == State.MISSED && matching.getMfalse() != Integer.MIN_VALUE)
					matching = code.index(matching.getMfalse());
				else
					matching = matching.next();
				break;
			case TEST_FLOAT:
				test = cur instanceof RecordInterface ? ((RecordInterface) cur).java() : cur;
				if (test instanceof Double && (Double) test == matching.getMfloat())
					matching = matching.next();
				else
					matching = code.index(matching.getMffalse());
				break;
			case TEST_NUMBER:
				test = cur instanceof RecordInterface ? ((RecordInterface) cur).java() : cur;
				if (test instanceof Long && (Long) test == matching.getMnumber())
					matching = matching.next();
				else
					matching = code.index(matching.getMnfalse());
				break;
			case TEST_PARM: // validate that cur is equal to the given parameter
				Object parm = inter.getStackElement(startFrame + matching.getTparm());
				if ((cur != null || parm == null)
						&& ((cur instanceof RecordInterface && ((RecordInterface) cur).java().equals(parm)) || cur.equals(parm)))
					matching = matching.next();
				else
					matching = code.index(matching.getTpfalse());
				break;
			case TEST_STACK: // validate that there are a certain number of elements on the stack
				if (inter.getStackSize() - startFrame == matching.getTstack())
					matching = matching.next();
				else
					matching = code.index(matching.getTsfalse());
				break;
			case TEST_STRING:
				test = cur instanceof RecordInterface ? ((RecordInterface) cur).java() : cur;
				if (test instanceof String && matching.getTstring().equals(test))
					matching = matching.next();
				else
					matching = code.index(matching.getMtsfalse());
				break;
			case MATCH_STRING:
				boolean match = true;
				String s = matching.getMstring();
				RecordInterface to = cur instanceof RecordInterface ? (RecordInterface) cur : null;
				if (to == null)
					match = false;
				else
					for (int i = 0; i < s.length(); i++) {
						char ch = s.charAt(i);
						if (to == null)
							break;
						String st = (String) to.java();
						if (st == null || st.length() != 1 || st.charAt(0) != ch) {
							match = false;
							break;
						} else {
							to = to.next();
							cur = to;
						}
					}
				if (match)
					matching = matching.next();
				else
					matching = code.index(matching.getMsfalse());
				break;
			case TEST_TYPE: // test that the current selected element is of a specific type
				if (matching.getTtype() == null)
					throw new RuntimeException("Not correctly matching macro " + macro.getName());
				switch (matching.getTtype()) {
				case TYPE_ARRAY:
					if (cur instanceof RecordInterface && ((RecordInterface) cur).type() == FieldType.ARRAY)
						matching = matching.next();
					else
						matching = code.index(matching.getTtfalse());
					break;
				case TYPE_BOOLEAN:
					if (cur instanceof Boolean)
						matching = matching.next();
					else
						matching = code.index(matching.getTtfalse());
					break;
				case TYPE_FLOAT:
					if (cur instanceof Double)
						matching = matching.next();
					else
						matching = code.index(matching.getTtfalse());
					break;
				case TYPE_NULL:
					if (cur == null)
						matching = matching.next();
					else
						matching = code.index(matching.getTtfalse());
					break;
				case TYPE_NUMBER:
					if (cur instanceof Long)
						matching = matching.next();
					else
						matching = code.index(matching.getTtfalse());
					break;
				case TYPE_OBJECT:
					if (cur instanceof RecordInterface && ((RecordInterface) cur).type() == FieldType.OBJECT)
						matching = matching.next();
					else
						matching = code.index(matching.getTtfalse());
					break;
				case TYPE_STRING:
					if (cur instanceof String || cur instanceof Text)
						matching = matching.next();
					else
						matching = code.index(matching.getTtfalse());
					break;
				case SKIP:
					matching = matching.next();
					break;
				}
				break;
			case VAR_ADD: {
				@SuppressWarnings("unchecked")
				List<Object> list = (List<Object>) inter.getStackElement(matching.getVadd().getNr());
				int range = matching.getVarange(); // TODO handle TEXT substring
				if (range == Integer.MIN_VALUE)
					list.add(cur);
				else {
					throw new RuntimeException("Not implemented yet!");
					// list.add(((Text) cur).substring(from, till));
				}
				matching = matching.next();
				break;
			}
			case VAR_START:
				inter.setStack(startFrame + matching.getVadd().getNr(), new ArrayList<>());
				break;
			case VAR_WRITE:
				int range = matching.getVwrange();
				Object obj = null;
				if (range == Integer.MIN_VALUE) {
					if (cur instanceof RecordInterface)
						obj = ((RecordInterface) cur).java();
					else
						obj = cur;
				} else if (range < 0) {
					ListArray lst = new ListArray();
					if (cur instanceof RecordInterface) {
						RecordInterface elm = (RecordInterface) cur;
						while (elm != null) {
							lst.add(elm.java());
							elm = elm.next();
						}
						cur = null;
						obj = lst;
					}
				} else {
					int index = range;
					if (index >= stack.size())
						throw new RuntimeException("Reading outside active stack");
					RecordInterface elm = stack.get(index);
					if (elm instanceof StringArray) {
						String st = elm.toString();
						obj = cur == null ? st : st.substring(0, ((RecordInterface) cur).index() - elm.index());
					} else if (cur instanceof RecordInterface) {
						ListArray lst = new ListArray();
						while (elm != null && elm.index() < ((RecordInterface) cur).index()) {
							lst.add(elm.java());
							elm = elm.next();
						}
						obj = lst;
					}
				}
				inter.setStack(matching.getVwrite().getNr(), obj);
				matching = matching.next();
				break;
			default:
				break;
			}
		}
	}

	private void clearStack() {
		inter.clearStack(startFrame);
		inter.setStackFrame(previousFrame);
	}

	private MatchingArray stackPop() {
		if (stack.isEmpty())
			return null;
		RecordInterface res = stack.get(stack.size() - 1);
		stack.remove(stack.size() - 1); // remove last element from stack
		if (res instanceof MatchingArray)
			return (MatchingArray) res;
		throw new RuntimeException("Still parameter position on stack");
	}
}
