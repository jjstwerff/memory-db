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
	private int cur_pos; // current position

	public MatchMacro(JsltInterpreter inter, Operator code) {
		this.inter = inter;
		this.macro = code.getMacro();
		this.previousFrame = inter.getStackFrame();
		this.startFrame = inter.getStackSize();
		inter.setStackFrame(startFrame);
		CallParmsArray callParms = code.getCallParms();
		this.maxParameter = callParms.getSize();
		int i = 0;
		for (CallParmsArray parm : callParms)
			inter.setStack(i++, inter.inter(parm));
	}

	public Object match() {
		MatchingArray matching = macro.getMatching();
		int pos = -1;
		pos = matching.next(pos);
		while (true) {
			if (pos <= 0)
				throw new RuntimeException("Not correctly matching macro " + macro.getName());
			matching.setIdx(pos - 1);
			switch (matching.getType()) {
			case ALT:
				Alternative alt = new Alternative(macro.getStore(), matching.getAltnr());
				if (alt.getIf().getRec() <= 0 || (Boolean) inter.inter(alt.getIf())) {
					Object res = inter.inter(alt.getCode(0));
					Variable avar = matching.getAvar();
					if (avar.getRec() == 0) {
						inter.clearStack(startFrame);
						inter.setStackFrame(previousFrame);
						return res;
					}
					inter.setStack(startFrame + avar.getNr(), res);
					pos = matching.next(pos);
				} else
					pos = matching.getAfalse();
				break;
			case ERROR:
				inter.error(matching.getError());
				inter.clearStack(startFrame);
				inter.setStackFrame(previousFrame);
				return null;
			case FIELD:
				int f = -1;
				RecordInterface rec = cur instanceof RecordInterface ? (RecordInterface) cur : null;
				if (rec != null && rec.type() == FieldType.OBJECT)
					f = rec.scanName(matching.getField()); // search the field name
				if (f > 0 && rec.type(f) != null) {
					cur = rec.get(f); // get the field value
					pos = matching.next(pos); // continue with the next match
				} else
					pos = matching.getFfalse(); // Element doesn't have the specified field
				break;
			case FINISH:
				RecordInterface frec = cur instanceof RecordInterface ? (RecordInterface) cur : null;
				boolean problem = frec == null || cur_pos > 0 || frec.next(cur_pos) != -2;
				if (problem && matching.getNotfinished() != Integer.MIN_VALUE) {
					pos = matching.getNotfinished();
				} else
					pos = matching.next(pos);
				break;
			case JUMP:
				pos = matching.getJump();
				break;
			case PARM:
				if (matching.getParm() >= maxParameter)
					pos = matching.getPfalse();
				else {
					cur = inter.getStackElement(startFrame + matching.getParm());
					pos = matching.next(pos);
				}
				break;
			case PUSH:
				// validate that each element in a RecordImplementation
				// only a stack of objects, each split-off is a new object.. copy of current
				((Text) cur).freePos();
				pos = matching.next(pos);
				break;
			case POP:
				// 
				pos = matching.next(pos);
				break;
			case START:
				RecordInterface ri;
				if (cur instanceof String)
					ri = new StringArray((String) cur);
				else if (cur instanceof RecordInterface)
					ri = (RecordInterface) cur;
				else {
					pos = matching.getNotstarted();
					break;
				}
				cur_pos = ri.next(-1);
				pos = matching.next(pos);
				break;
			case STACK:
				inter.setStackFrame(inter.getStackFrame() + matching.getStack());
				pos = matching.next(pos);
				break;
			case TEST_BOOLEAN:
				if (cur instanceof Boolean && (Boolean) cur == matching.isMboolean())
					pos = matching.next(pos);
				else
					pos = matching.getMbfalse();
				break;
			case TEST_CALL:
				throw new RuntimeException("Not written yet");
			case TEST_FLOAT:
				if (cur instanceof Double && (Double) cur == matching.getMfloat())
					pos = matching.next(pos);
				else
					pos = matching.getMffalse();
				break;
			case TEST_NUMBER:
				if (cur instanceof Long && (Long) cur == matching.getMnumber())
					pos = matching.next(pos);
				else
					pos = matching.getMnfalse();
				break;
			case TEST_STACK:
				if (inter.getStackSize() - startFrame == matching.getTstack())
					pos = matching.next(pos);
				else
					pos = matching.getTsfalse();
				break;
			case TEST_STRING:
				if (cur instanceof String && matching.getMstring().equals((String) cur))
					pos = matching.next(pos);
				else
					pos = matching.getMsfalse();
				break;
			case TEST_TYPE:
				switch (matching.getTtype()) {
				case TYPE_ARRAY:
					if (cur instanceof RecordInterface && ((RecordInterface) cur).type() == FieldType.ARRAY)
						pos = matching.next(pos);
					else
						pos = matching.getTtfalse();
					break;
				case TYPE_BOOLEAN:
					if (cur instanceof Boolean)
						pos = matching.next(pos);
					else
						pos = matching.getTtfalse();
					break;
				case TYPE_FLOAT:
					if (cur instanceof Double)
						pos = matching.next(pos);
					else
						pos = matching.getTtfalse();
					break;
				case TYPE_NULL:
					if (cur == null)
						pos = matching.next(pos);
					else
						pos = matching.getTtfalse();
					break;
				case TYPE_NUMBER:
					if (cur instanceof Long)
						pos = matching.next(pos);
					else
						pos = matching.getTtfalse();
					break;
				case TYPE_OBJECT:
					if (cur instanceof RecordInterface && ((RecordInterface) cur).type() == FieldType.OBJECT)
						pos = matching.next(pos);
					else
						pos = matching.getTtfalse();
					break;
				case TYPE_STRING:
					if (cur instanceof String || cur instanceof Text)
						pos = matching.next(pos);
					else
						pos = matching.getTtfalse();
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
				pos = matching.next(pos);
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
