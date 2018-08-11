package org.memorydb.meta;

import java.util.Arrays;

import org.apache.commons.lang.NotImplementedException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import org.memorydb.meta.Field.Type;
import org.memorydb.table.MemoryTests;

public class TestTables extends MemoryTests {
	private static final int RESERVED = 3;

	private Record addRecord(Project records, String name, String className, int size, String description) {
		try (ChangeRecord rec = new ChangeRecord(records, 0)) {
			rec.setName(name);
			rec.setDescription(className);
			rec.setSize(size);
			rec.setDescription(description);
		}
		return records.getRecords(name);
	}

	private Field addField(Record rec, String name, Type type) {
		int pos = rec.getSize() > RESERVED ? rec.getSize() - RESERVED : 0;
		try (ChangeField fld = new ChangeField(rec, 0)) {
			fld.setName(name);
			fld.setType(type);
			fld.setPos(pos);
			fld.setDescription(null);
			fld.setMandatory(true);
		}
		switch (type) {
		case INTEGER:
		case RELATION:
		case ENUMERATE:
		case BOOLEAN:
		case STRING:
			pos += 1;
			break;
		case FLOAT:
		case LONG:
		case DATE:
			pos += 2;
			break;
		default:
			throw new RuntimeException("Unknown type " + type);
		}
		try (ChangeRecord record = new ChangeRecord(rec)) {
			record.setSize(RESERVED + pos);
		}
		return rec.getFieldOnName(name);
	}

	@SuppressWarnings("unused")
	private void allowNullField(Field fld) {
		try (ChangeField rec = new ChangeField(fld)) {
			rec.setMandatory(false);
		}
	}

	private void addIndex(Project meta, Record rec, String name, String... fields) {
		try (ChangeIndex idx = new ChangeIndex(meta, 0)) {
			idx.setRecord(rec);
			idx.setName(name);
			for (String fld : fields) {
				IndexFieldsArray ifld = idx.getIndexFields().add();
				ifld.setStr(fld);
			}
		}
	}

	/*
	private void addFallThrough(Record rec, String name, String[] fallThrough, String... fields) {
		Index idx = rec.addIndex();
		idx.setOnRecord(rec);
		idx.setName(name);
		for (String fld : fields) {
			IndexField idxFld = idx.addField();
			idxFld.setField(rec.getField(fld));
			idxFld.persist();
		}
		for (String fld : fallThrough) {
			IndexField ifld = idx.addField();
			Field field = rec.getField(fld);
			ifld.setField(field);
			if (field == null)
				throw new RuntimeException("Unknown field " + fld);
			ifld.setFallThrough(true);
			ifld.persist();
		}
		idx.persist();
	}
	*/

	/*
	private CalcRecord addCalcRecord(Record record, Object... data) {
		CalcRecord calc = CalcRecord.addCalcRecord(record);
		MType type = calc.readType();
		int i = 0;
		for (Object obj : data)
			calc.changePos(type.readFields()[i++].setObject(calc.readPos(), obj));
		return calc;
	}
	*/

	private String list(Project records, String recName, String fldName) {
		StringBuilder res = new StringBuilder();
		boolean first = true;
		Record rec = records.getRecords(recName);
		Field fld = rec.getFieldOnName(fldName);
		if (fld.getType() == Type.SET) {
			for (Object object : Arrays.asList("a", "b")) {
				if (first)
					first = false;
				else
					res.append(", ");
				res.append(object == null ? "null" : object.toString());
			}
		} else {
			throw new NotImplementedException();
		}
		return res.toString();
	}

	private void relation(Project recs, String table, String field, String to) {
		Field fld = recs.getRecords(table).getFieldOnName(field);
		try (ChangeField chg = new ChangeField(fld)) {
			chg.setRelated(recs.getRecords(to));
		}
	}

	@Test
	@Ignore("List method is not functional")
	public void testIndexes() throws Exception {
		try (Meta meta = new Meta()) {
			Project project = meta.addProject();
			//store.setRoot(records);
			Record rec = addRecord(project, "ta", null, 5, null);
			addField(rec, "fa", Type.INTEGER);
			addField(rec, "fb", Type.RELATION);
			addField(rec, "fc", Type.RELATION);
			addField(rec, "fd", Type.RELATION);
			addIndex(project, rec, "primary", "fa");
			rec = addRecord(project, "tb", null, 6, null);
			addField(rec, "fa", Type.INTEGER);
			addField(rec, "fb", Type.RELATION);
			addField(rec, "fc", Type.RELATION);
			addField(rec, "fd", Type.RELATION);
			addIndex(project, rec, "primary", "fa");
			rec = addRecord(project, "tc", null, 6, null);
			addField(rec, "fa", Type.INTEGER);
			addField(rec, "fb", Type.RELATION);
			addField(rec, "fc", Type.RELATION);
			addField(rec, "fd", Type.RELATION);
			addIndex(project, rec, "primary", "fa");
			Assert.assertEquals("", list(project, "tc", "relatedFields"));
			relation(project, "ta", "fb", "ta");
			relation(project, "ta", "fc", "tb");
			relation(project, "ta", "fd", "tc");
			relation(project, "tb", "fb", "ta");
			relation(project, "tb", "fc", "tb");
			relation(project, "tb", "fd", "tc");
			relation(project, "tc", "fb", "ta");
			relation(project, "tc", "fc", "tb");
			relation(project, "tc", "fd", "tc");
			compare("testIndexes", project.toString());
			Assert.assertEquals("fa, fb, fc, fd", list(project, "tc", "fields"));
			Assert.assertEquals("fa, fb, fc, fd", list(project, "tc", "fieldsOnName"));
			Assert.assertEquals("fd, fd, fd", list(project, "tc", "relatedFields"));
			relation(project, "ta", "fd", "tb");
			Assert.assertEquals("fc, fd, fc, fc", list(project, "tb", "relatedFields"));
			Assert.assertEquals("fd, fd", list(project, "tc", "relatedFields"));
		}
	}
	/*
		@Test
		public void testSession() {
			try (Store store = new Store()) {
				Records records = new Records();
				//store.setRoot(records);
				records.persist();
				Record rec = addRecord(records, "ta", null, 5, null);
				addField(rec, "fa", "Integer", 0);
				addField(rec, "fb", "Relation", 0);
				addField(rec, "fc", "Relation", 0);
				addField(rec, "fd", "Relation", 0);
				addIndex(rec, "primary", "fa");
				Assert.assertEquals("Records[0]: set=[\n" //
						+ "  Record[4]: name=ta, fields=[\n" //
						+ "    Field[17]: id=1, name=fa, type=Integer, position=2\n" //
						+ "    Field[34]: id=2, name=fb, type=Relation, position=3\n" //
						+ "    Field[51]: id=3, name=fc, type=Relation, position=4\n" //
						+ "    Field[68]: id=4, name=fd, type=Relation, position=5\n" //
						+ "  ], size=9, indexes=[\n" //
						+ "    Index[85]: name=primary, fields=[\n" //
						+ "      IndexField[93]: indexField=1, field={id=1}\n" //
						+ "    ]\n" //
						+ "  ]\n" //
						+ "]", store.toString());
				store.session("test indexes in session", null);
				Field addField = rec.addField();
				addField.setName("fbb");
				addField.setType("String");
				addField.setAllowNull(true);
				addField.persist();
				Assert.assertEquals("Records[-1]: set=[\n" //
						+ "  Record[-24]: name=ta, fields=[\n" //
						+ "    Field[-6]: id=5, name=fbb, type=String, allowNull=true\n" //
						+ "  ], indexes=[]\n" //
						+ "]", store.getSession().toString());
				Field field = rec.getField("fd");
				field.setType("Enum");
				field.setPosition(11);
				field.persist();
				field = rec.getField("fb");
				field.delete();
				Assert.assertEquals("Records[-1]: set=[\n" //
						+ "  Record[-24]: name=ta, fields=[\n" //
						+ "    Field[-56]: !id=2\n" //
						+ "    Field[-38]: id=4, type=Enum, position=11\n" //
						+ "    Field[-6]: id=5, name=fbb, type=String, allowNull=true\n" //
						+ "  ], indexes=[]\n" //
						+ "]", store.getSession().toString());
				Assert.assertEquals("Record[-24]: name=ta, fields=[\n" //
						+ "  Field[17]: id=1, name=fa, type=Integer, position=2\n" //
						+ "  Field[51]: id=3, name=fc, type=Relation, position=4\n" //
						+ "  Field[-38]: id=4, name=fd, type=Enum, position=11\n" //
						+ "  Field[-6]: id=5, name=fbb, type=String, allowNull=true\n" //
						+ "], size=9, indexes=[\n" //
						+ "  Index[85]: name=primary, fields=[\n" //
						+ "    IndexField[93]: indexField=1, field={id=1}\n" //
						+ "  ]\n" //
						+ "]", rec.toString());
				StringBuilder build = new StringBuilder();
				for (Field fld : rec.getFieldsOnName()) {
					build.append(fld.toString());
					build.append("\n");
				}
				Assert.assertEquals("Field[17]: id=1, name=fa, type=Integer, position=2\n" //
						+ "Field[-6]: id=5, name=fbb, type=String, allowNull=true\n" //
						+ "Field[51]: id=3, name=fc, type=Relation, position=4\n" //
						+ "Field[-38]: id=4, name=fd, type=Enum, position=11\n", build.toString());
				store.commit();
				Assert.assertEquals("Records[0]: set=[\n" // 
						+ "  Record[4]: name=ta, fields=[\n" //
						+ "    Field[17]: id=1, name=fa, type=Integer, position=2\n" //
						+ "    Field[51]: id=3, name=fc, type=Relation, position=4\n" //
						+ "    Field[68]: id=4, name=fd, type=Enum, position=11\n" //
						+ "    Field[100]: id=5, name=fbb, type=String, allowNull=true\n" //
						+ "  ], size=9, indexes=[\n" //
						+ "    Index[85]: name=primary, fields=[\n" //
						+ "      IndexField[93]: indexField=1, field={id=1}\n" //
						+ "    ]\n" //
						+ "  ]\n" //
						+ "]", store.toString());
				build = new StringBuilder();
				for (Field fld : records.getRecord("ta").getFieldsOnName()) {
					build.append(fld.toString());
					build.append("\n");
				}
				Assert.assertEquals("Field[17]: id=1, name=fa, type=Integer, position=2\n"
						+ "Field[100]: id=5, name=fbb, type=String, allowNull=true\n" //
						+ "Field[51]: id=3, name=fc, type=Relation, position=4\n" //
						+ "Field[68]: id=4, name=fd, type=Enum, position=11\n", build.toString());
			}
		}
	
		@Test
		public void testMetaStructure() {
			try (Store store = new Store()) {
				Records records = new Records();
				//store.setRoot(records);
				Assert.assertEquals("name=Records, size=4, fields=[\n" //
						+ "  name=set, type=SET, fieldNr=0, related=Record, pos=0\n" //
						+ "], keys=[\n" //
						+ "]", records.readType().toString());
				Assert.assertEquals("name=Record, size=13, parent={name=Records}, fields=[\n" //
						+ "  name=id, type=INTEGER, fieldNr=0, pos=0\n" //
						+ "  name=name, type=STRING, fieldNr=1, pos=1\n" //
						+ "  name=className, type=STRING, fieldNr=2, pos=2\n" //
						+ "  name=fields, type=SET, fieldNr=3, related=Field, pos=3\n" //
						+ "  name=fieldsOnName, type=SET, fieldNr=4, related=Field, sorting=[name], pos=4\n" //
						+ "  name=size, type=INTEGER, fieldNr=5, pos=5\n" //
						+ "  name=indexes, type=SET, fieldNr=6, related=Index, pos=6\n" //
						+ "  name=description, type=STRING, fieldNr=7, pos=7\n" //
						+ "  name=relatedFields, type=SET, fieldNr=8, related=Field, sorting=[onRecord,name], pos=8\n" //
						+ "  name=recordsPos, type=INTEGER, fieldNr=9, pos=9\n" //
						+ "], keys=[\n" //
						+ "  field={name=name}\n" //
						+ "]", Records.fldSet.readRelType().toString());
				Assert.assertEquals("name=Field, size=17, parent={name=Record}, fields=[\n" //
						+ "  name=id, type=INTEGER, fieldNr=0, pos=0\n" //
						+ "  name=onRecord, type=RELATION, fieldNr=1, related=Record, pos=1\n" //
						+ "  name=name, type=STRING, fieldNr=2, pos=2\n" //
						+ "  name=type, type=ENUM, fieldNr=3, pos=3\n" //
						+ "  name=position, type=INTEGER, fieldNr=4, pos=4\n" //
						+ "  name=toRecord, type=RELATION, fieldNr=5, related=Record, pos=5\n" //
						+ "  name=maximum, type=INTEGER, fieldNr=6, pos=6\n" //
						+ "  name=unsigned, type=BOOLEAN, fieldNr=7, pos=7\n" //
						+ "  name=allowNull, type=BOOLEAN, fieldNr=8, pos=8\n" //
						+ "  name=description, type=STRING, fieldNr=9, pos=9\n" //
						+ "], keys=[\n" //
						+ "  field={name=id}\n" //
						+ "  field={name=onRecord}\n" //
						+ "]", Record.fldFields.readRelType().toString());
			}
		}
	*/
}
