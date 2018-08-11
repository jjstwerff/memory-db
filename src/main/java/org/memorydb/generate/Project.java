package org.memorydb.generate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Project {
	private final String name;
	private final String pack;
	private final List<Index> indexes;
	private final Map<String, Record> tables;
	private String dir;
	private int main;

	public Project(String name, String pack, String dir) {
		this.name = name;
		this.pack = pack;
		this.dir = dir;
		this.indexes = new ArrayList<>();
		this.tables = new TreeMap<>();
		this.main = 0;
	}

	public final String getName() {
		return name;
	}

	public final String getPackage() {
		return pack;
	}

	public List<Index> getIndexes() {
		return indexes;
	}

	public void addIndex(Index index) {
		indexes.add(index);
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public Record table(String tableName) {
		Record res = new Record(tableName, this, true);
		if (tables.containsKey(tableName))
			throw new GenerateException("Duplicate table: " + tableName);
		tables.put(tableName, res);
		return res;
	}

	public Record record(String recordName) {
		Record res = new Record(recordName, this, false);
		if (tables.containsKey(recordName))
			throw new GenerateException("Duplicate table: " + recordName);
		tables.put(recordName, res);
		return res;
	}

	public Record content(String contentName) {
		return record(contentName).content();
	}

	public Map<String, Record> getTables() {
		return tables;
	}

	public Collection<Record> getTableList() {
		return tables.values();
	}

	public void verify() {
		for(Record record: tables.values())
			record.verify();
	}

	@Override
	public String toString() {
		StringBuilder bld = new StringBuilder();
		bld.append("Records\n");
		for (Entry<String, Record> rec : tables.entrySet())
			rec.getValue().dump(bld);
		bld.append("\nIndexes\n");
		for (Index idx : indexes)
			idx.dump(bld);
		return bld.toString();
	}

	public int mainIndex() {
		return main++;
	}

	public Collection<String> getImports() {
		Set<String> imports = new TreeSet<>();
		for (Index i: indexes) {
			for (String f: i.getJavaTypes()) {
				if (f.equals("LocalDateTime"))
					imports.add("java.time.LocalDateTime");
			}
		}
		return imports;
	}
}
