package org.memorydb.jslt;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.memorydb.structure.RecordInterface;

public class MergeObject implements RecordInterface {
	private final List<RecordInterface> elms;
	private final int field;

	public MergeObject(RecordInterface o1, RecordInterface o2) {
		Map<String, RecordInterface> names = new LinkedHashMap<>();
		RecordInterface elm = o1.start();
		while (elm != null) {
			names.put(elm.name(), elm);
			elm = elm.next();
		}
		elm = o2.start();
		while (elm != null) {
			names.put(elm.name(), elm);
			elm = elm.next();
		}
		elms = new ArrayList<>(names.values());
		this.field = -1;
	}

	private MergeObject(List<RecordInterface> elms, int field) {
		this.elms = elms;
		this.field = field;
	}

	@Override
	public MergeObject start() {
		return field >= 0 || elms.size() == 0 ? null : new MergeObject(elms, 0);
	}

	@Override
	public MergeObject next() {
		return field < 0 || field + 1 >= elms.size() ? null : new MergeObject(elms, field + 1);
	}

	@Override
	public String name() {
		return field < 0 ? null : elms.get(field).name();
	}

	@Override
	public FieldType type() {
		return field < 0 ? FieldType.OBJECT : elms.get(field).type();
	}

	@Override
	public int size() {
		return elms.size();
	}

	@Override
	public Object java() {
		return field < 0 ? null : elms.get(field).java();
	}

	@Override
	public String toString() {
		StringBuilder bld = new StringBuilder();
		bld.append("{");
		int pos = 0;
		for (RecordInterface elm: elms) {
			if (pos++ != 0)
				bld.append(",");
			bld.append(elm.name()).append(":").append(elm.java());
		}
		bld.append("}");
		return bld.toString();
	}

	@Override
	public RecordInterface index(int idx) {
		return null;
	}

	@Override
	public RecordInterface copy() {
		return new MergeObject(elms, field);
	}
}
