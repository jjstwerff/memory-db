package org.memorydb.meta;

import java.util.Random;

import org.junit.Test;

import org.memorydb.structure.NormalCheck;

public class TestInserts extends NormalCheck {
	@Test
	public void testInserts() {
		try (Meta meta = new Meta()) {
			Project project = meta.addProject();
			Random rand = new Random(0);
			for (int i = 0; i < 5000; i++) {
				try (ChangeRecord rec = new ChangeRecord(project, 0)) {
					rec.setName(Long.toString(rand.nextLong()));
					//rec.setSize(rand.nextInt());
				}
			}
		}
	}

	@Test
	public void testRemoves() {
		try (Meta meta = new Meta()) {
			Project project = meta.addProject();
			Random rand = new Random(0);
			for (int i = 0; i < 5000; i++) {
				try (ChangeRecord rec = new ChangeRecord(project, 0)) {
					rec.setName(Long.toString(rand.nextLong()));
					//rec.setSize(rand.nextInt());
				}
				if (i > 1000) {
					int nr = rand.nextInt(1000);
					int c = 0;
					for (Record rec : project.getRecords()) {
						if (nr-- == 0) {
							project.getRecords().remove(rec.rec());
						}
						c++;
						assert c < 1100;
					}
				}
			}
		}
	}
}
