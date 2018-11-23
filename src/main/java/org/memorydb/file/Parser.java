package org.memorydb.file;

import org.memorydb.structure.StringPointer;

public interface Parser extends AutoCloseable {
	String getString(String field);

	Boolean getBoolean(String field);

	int getInt(String field);

	long getLong(String field);

	double getDouble(String field);

	boolean hasSub(String string);

	boolean getSub();

	void getRelation(String string, ScanRelation scan, int rec);

	void getRelation(String string, ScanRelation scan, int rec, int idx);

	void finishRelation();

	@Override
	void close();

	/** Returns true if the last read sub record should be deleted */
	boolean isDelete(int rec);

	void error(String msg);

	boolean hasField(String string);

	StringPointer getStringPointer(String string);
}
