package org.memorydb.jslt;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.memorydb.jslt.Operator.Operation;
import org.memorydb.structure.RecordInterface;

public class InterSort implements RecordInterface, Comparator<Integer> {
	private final JsltInterpreter interpreter;
	private final RecordInterface data;
	private int[] mapping;
	private SortParmsArray parms;

	@Override
	public int compare(Integer o1, Integer o2) {
		boolean desc = false;
		for (int i = 0; i < parms.getSize(); i++) {
			try (SortParmsArray parm = new SortParmsArray(parms, i)) {
				if (parm.getOperation() == Operation.BOOLEAN) {
					desc = parm.isBoolean();
					continue;
				}
				interpreter.setCurrent(data.get(o1));
				Object r1 = interpreter.inter(parm);
				interpreter.setCurrent(data.get(o2));
				Object r2 = interpreter.inter(parm);
				int r = interpreter.compare(r1, r2);
				if (r != 0)
					return desc ? -r : r;
			}
		}
		return Integer.compare(o1, o2);
	}

	public InterSort(JsltInterpreter interpreter, SortParmsArray parms, RecordInterface data) {
		this.mapping = new int[data.getSize()];
		this.interpreter = interpreter;
		this.parms = parms;
		this.data = data;
		Set<Integer> set = new TreeSet<>(this);
		int field = -1;
		while ((field = data.next(field)) > 0)
			set.add(field);
		int i = 0;
		for (Integer f : set)
			mapping[i++] = f;
	}

	@Override
	public int next(int field) {
		field++;
		if (field >= mapping.length)
			return -2;
		return field;
	}

	@Override
	public String name(int field) {
		return null;
	}

	@Override
	public FieldType type(int field) {
		if (field >= mapping.length || field < 0)
			return null;
		return data.type(mapping[field]);
	}

	@Override
	public Object get(int field) {
		if (field >= mapping.length || field < 0)
			return null;
		return data.get(mapping[field]);
	}

	@Override
	public Iterable<? extends RecordInterface> iterate(int field, Object... key) {
		return null;
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public int getSize() {
		return data.getSize();
	}

	@Override
	public FieldType type() {
		return FieldType.ARRAY;
	}
}
