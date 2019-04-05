package org.memorydb.jslt;

import java.util.ArrayList;
import java.util.List;

import org.memorydb.handler.Text;
import org.memorydb.structure.RecordInterface;
import org.memorydb.structure.RecordInterface.FieldType;

public class MatchMacro {
	private final Macro macro;
	private final JsltInterpreter inter;
	private final int previousFrame;
	private final int startFrame;
	private final int maxParameter;
	private Object cur; // currently selected object
	private RecordInterface data; // current data position

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
	}

	public Object match() {
		MatchingArray matching = macro.getMatching();
		MatchingArray elm = matching.start();
		while (true) {
			if (elm == null)
				throw new RuntimeException("Not correctly matching macro " + macro.getName());
			switch (matching.getType()) {
			case ALT:
				Alternative alt = new Alternative(macro.store(), matching.getAltnr());
				if (alt.getIf().rec() <= 0 || (Boolean) inter.inter(alt.getIf())) {
					Object res = inter.inter(alt.getCode(0));
					Variable avar = matching.getAvar();
					if (avar.rec() == 0) {
						inter.clearStack(startFrame);
						inter.setStackFrame(previousFrame);
						return res;
					}
					inter.setStack(startFrame + avar.getNr(), res);
					elm = elm.next();
				} else
					elm = matching.index(matching.getAfalse());
				break;
			case ERROR:
				inter.error(matching.getError());
				inter.clearStack(startFrame);
				inter.setStackFrame(previousFrame);
				return null;
			case FIELD:
				RecordInterface rec = cur instanceof RecordInterface ? (RecordInterface) cur : null;
				if (rec != null && rec.type() == FieldType.OBJECT)
					data = rec.field(matching.getField()); // search the field name
				if (data != null) {
					cur = rec.java(); // get the field value
					elm = elm.next(); // continue with the next match
				} else
					elm = matching.index(matching.getFfalse()); // Element doesn't have the specified field
				break;
			case FINISH:
				RecordInterface frec = cur instanceof RecordInterface ? (RecordInterface) cur : null;
				boolean problem = frec == null || data != null;
				if (problem && matching.getNotfinished() != Integer.MIN_VALUE) {
					elm = matching.index(matching.getNotfinished());
				} else
					elm = elm.next();
				break;
			case JUMP:
				elm = matching.index(matching.getJump());
				break;
			case PARM:
				if (matching.getParm() >= maxParameter)
					elm = matching.index(matching.getPfalse());
				else {
					cur = inter.getStackElement(startFrame + matching.getParm());
					elm = elm.next();
				}
				break;
			case PUSH:
				// validate that each element in a RecordImplementation
				// only a stack of objects, each split-off is a new object.. copy of current
				((Text) cur).freePos();
				elm = elm.next();
				break;
			case POP:
				// 
				elm = elm.next();
				break;
			case START:
				RecordInterface ri;
				if (cur instanceof String)
					ri = new StringArray((String) cur, -1);
				else if (cur instanceof RecordInterface)
					ri = (RecordInterface) cur;
				else {
					elm = matching.index(matching.getNotstarted());
					break;
				}
				data = ri.start();
				elm = elm.next();
				break;
			case STACK:
				inter.setStackFrame(inter.getStackFrame() + matching.getStack());
				elm = elm.next();
				break;
			case TEST_BOOLEAN:
				if (cur instanceof Boolean && (Boolean) cur == matching.isMboolean())
					elm = elm.next();
				else
					elm = matching.index(matching.getMbfalse());
				break;
			case TEST_CALL:
				throw new RuntimeException("Not written yet");
			case TEST_FLOAT:
				if (cur instanceof Double && (Double) cur == matching.getMfloat())
					elm = elm.next();
				else
					elm = matching.index(matching.getMffalse());
				break;
			case TEST_NUMBER:
				if (cur instanceof Long && (Long) cur == matching.getMnumber())
					elm = elm.next();
				else
					elm = matching.index(matching.getMnfalse());
				break;
			case TEST_STACK:
				if (inter.getStackSize() - startFrame == matching.getTstack())
					elm = elm.next();
				else
					elm = matching.index(matching.getTsfalse());
				break;
			case TEST_STRING:
				if (cur instanceof String && matching.getMstring().equals((String) cur))
					elm = elm.next();
				else
					elm = matching.index(matching.getMsfalse());
				break;
			case TEST_TYPE:
				switch (matching.getTtype()) {
				case TYPE_ARRAY:
					if (cur instanceof RecordInterface && ((RecordInterface) cur).type() == FieldType.ARRAY)
						elm = elm.next();
					else
						elm = matching.index(matching.getTtfalse());
					break;
				case TYPE_BOOLEAN:
					if (cur instanceof Boolean)
						elm = elm.next();
					else
						elm = matching.index(matching.getTtfalse());
					break;
				case TYPE_FLOAT:
					if (cur instanceof Double)
						elm = elm.next();
					else
						elm = matching.index(matching.getTtfalse());
					break;
				case TYPE_NULL:
					if (cur == null)
						elm = elm.next();
					else
						elm = matching.index(matching.getTtfalse());
					break;
				case TYPE_NUMBER:
					if (cur instanceof Long)
						elm = elm.next();
					else
						elm = matching.index(matching.getTtfalse());
					break;
				case TYPE_OBJECT:
					if (cur instanceof RecordInterface && ((RecordInterface) cur).type() == FieldType.OBJECT)
						elm = elm.next();
					else
						elm = matching.index(matching.getTtfalse());
					break;
				case TYPE_STRING:
					if (cur instanceof String || cur instanceof Text)
						elm = elm.next();
					else
						elm = matching.index(matching.getTtfalse());
					break;
				case SKIP:
					elm = elm.next();
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
					//list.add(((Text) cur).substring(from, till));
				}
				elm = elm.next();
				break;
			}
			case VAR_START:
				inter.setStack(startFrame + matching.getVadd().getNr(), new ArrayList<>());
				break;
			case VAR_WRITE:
				int range = matching.getVwrange();
				Object obj;
				if (range == Integer.MIN_VALUE)
					obj = cur;
				else
					throw new RuntimeException("Not implemented yet!");
				inter.setStack(startFrame + matching.getVadd().getNr(), obj);
				break;
			default:
				break;
			}
		}
	}
}
