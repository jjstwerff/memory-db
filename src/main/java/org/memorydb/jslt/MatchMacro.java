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

	public MatchMacro(JsltInterpreter inter, Operator code) {
		this.inter = inter;
		this.macro = code.getMacro();
		this.previousFrame = inter.getStackFrame();
		this.startFrame = inter.getStackSize();
		CallParmsArray callParms = code.getCallParms();
		this.maxParameter = callParms.getSize();
		int i = 0;
		for (CallParmsArray parm : callParms)
			inter.setStack(startFrame + i++, inter.inter(parm));
	}

	public Object match() {
		MatchingArray matching = macro.getMatching();
		int pos = -1;
		pos = matching.next(pos);
		while (true) {
			if (pos < 0)
				throw new RuntimeException("Not correctly matching macro " + macro.getName());
			matching.setIdx(pos);
			switch (matching.getType()) {
			case ALT:
				Alternative alt = new Alternative(macro.getStore(), matching.getAltnr());
				if ((Boolean) inter.inter(alt.getIf())) {
					Object res = inter.inter(alt.getCode());
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
				break;
			case FIELD:
				int f = -1;
				if (!(cur instanceof RecordInterface) || //
						((RecordInterface) cur).type() != FieldType.OBJECT || //
						(f = ((RecordInterface) cur).scanName(matching.getField())) < 0)
					pos = matching.getFfalse(); // Element doesn't have the specified field
				else {
					cur = ((RecordInterface) cur).get(f);
					pos = matching.next(pos);
				}
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
			case POS_FREE:
				((Text) cur).freePos();
				pos = matching.next(pos);
				break;
			case POS_KEEP:
				((Text) cur).addPos();
				pos = matching.next(pos);
				break;
			case POS_TO:
				((Text) cur).toPos(matching.getPto());
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
				break;
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
						pos = matching.getTfalse();
					break;
				case TYPE_BOOLEAN:
					if (cur instanceof Boolean)
						pos = matching.next(pos);
					else
						pos = matching.getTfalse();
					break;
				case TYPE_FLOAT:
					if (cur instanceof Double)
						pos = matching.next(pos);
					else
						pos = matching.getTfalse();
					break;
				case TYPE_NULL:
					if (cur == null)
						pos = matching.next(pos);
					else
						pos = matching.getTfalse();
					break;
				case TYPE_NUMBER:
					if (cur instanceof Long)
						pos = matching.next(pos);
					else
						pos = matching.getTfalse();
					break;
				case TYPE_OBJECT:
					if (cur instanceof RecordInterface && ((RecordInterface) cur).type() == FieldType.OBJECT)
						pos = matching.next(pos);
					else
						pos = matching.getTfalse();
					break;
				case TYPE_STRING:
					if (cur instanceof String || cur instanceof Text)
						pos = matching.next(pos);
					else
						pos = matching.getTfalse();
					break;
				}
				break;
			case VAR_ADD: {
				@SuppressWarnings("unchecked")
				List<Object> list = (List<Object>) inter.getStackElement(matching.getVadd().getNr());
				int from = matching.getVafrom(); // TODO handle TEXT substring
				int till = matching.getVafrom();
				if (from == Integer.MIN_VALUE && till == Integer.MIN_VALUE)
					list.add(cur);
				else if (till == Integer.MIN_VALUE)
					list.add(((Text) cur).substring(from));
				else
					list.add(((Text) cur).substring(from, till));
				pos = matching.next(pos);
				break;
			}
			case VAR_START:
				inter.setStack(startFrame + matching.getVadd().getNr(), new ArrayList<>());
				break;
			case VAR_WRITE:
				int from = matching.getVwfrom(); // TODO handle TEXT substring
				int till = matching.getVwfrom();
				Object obj;
				if (from == Integer.MIN_VALUE && till == Integer.MIN_VALUE)
					obj = cur;
				else if (till == Integer.MIN_VALUE)
					obj = ((Text) cur).substring(from);
				else
					obj = ((Text) cur).substring(from, till);
				inter.setStack(startFrame + matching.getVadd().getNr(), obj);
				break;
			}
		}
	}
}
