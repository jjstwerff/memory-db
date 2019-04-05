package org.memorydb.jslt;

import java.util.Arrays;
import java.util.Comparator;

import org.memorydb.jslt.Operator.Operation;
import org.memorydb.structure.RecordInterface;

public class InterSort implements RecordInterface, Comparator<RecordInterface> {
	private final JsltInterpreter interpreter;
	private final RecordInterface data;
	private RecordInterface[] mapping;
	private SortParmsArray parms;
	private final int position;

	@Override
	public int compare(RecordInterface o1, RecordInterface o2) {
		boolean desc = false;
		for (int i = 0; i < parms.size(); i++) {
			SortParmsArray parm = new SortParmsArray(parms, i);
			if (parm.getOperation() == Operation.BOOLEAN) {
				desc = parm.isBoolean();
				continue;
			}
			interpreter.setCurrent(o1.java());
			Object r1 = interpreter.inter(parm);
			interpreter.setCurrent(o2.java());
			Object r2 = interpreter.inter(parm);
			int r = interpreter.compare(r1, r2);
			if (r != 0)
				return desc ? -r : r;
		}
		return 0;
	}

	public InterSort(JsltInterpreter interpreter, SortParmsArray parms, RecordInterface data) {
		this.mapping = new RecordInterface[data.size()];
		this.interpreter = interpreter;
		this.parms = parms;
		this.data = data;
		this.position = -1;
		RecordInterface rec = data.start();
		int pos = 0;
		while (rec != null) {
			mapping[pos++] = rec;
			rec = rec.next();
		}
		Arrays.sort(mapping, this);
	}

	public InterSort(RecordInterface[] mapping, int position) {
		this.interpreter = null;
		this.data = null;
		this.mapping = mapping;
		this.position = position;
	}

	@Override
	public InterSort start() {
		return mapping.length == 0 ? null : new InterSort(mapping, 0);
	}

	@Override
	public InterSort next() {
		return position >= mapping.length ? null : new InterSort(mapping, position + 1);
	}

	@Override
	public String name() {
		return position < 0 || position >= mapping.length ? null : mapping[position].name();
	}

	@Override
	public FieldType type() {
		return position >= mapping.length ? null : position < 0 ? FieldType.ARRAY : mapping[position].type();
	}

	@Override
	public Object java() {
		return position < 0 || position >= mapping.length ? null : mapping[position].java();
	}

	@Override
	public int size() {
		return position >= mapping.length ? 0 : position < 0 ? data.size() : mapping[position].size();
	}

	@Override
	public InterSort copy() {
		return new InterSort(mapping, position);
	}
}
