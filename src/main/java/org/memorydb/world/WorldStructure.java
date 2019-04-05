package org.memorydb.world;

import org.memorydb.generate.Generate;
import org.memorydb.generate.Project;
import org.memorydb.generate.Record;
import org.memorydb.generate.Type;

public class WorldStructure {
	public static void main(String[] args) {
		Generate.project(getProject());
		System.out.println("Refresh the project to allow eclipse to see the last changes");
	}

	public static Project getProject() {
		Project project = new Project("Data", "org.memorydb.world", "src/main/java/org/memorydb/world");

		Record dependency = project.record("Dependency"); // building cost / prerequisites

		Record effect = project.record("Effect"); // effect on the rules

		Record special = project.table("Special"); // attributes to items / persons to describe how people perceive them
		special.field("name", Type.STRING);
		special.field("opposite", Type.STRING);
		special.field("description", Type.STRING);
		special.field("taste", Type.BOOLEAN);
		special.index("Specials", "name");

		Record specials = project.record("Specials");
		specials.field("special", Type.RELATION, special);
		specials.field("known", Type.BOOLEAN);
		specials.field("level", Type.BYTE);

		Record category = project.table("Category"); // different types of things in the world with general specials (actual instances can differ)
		category.field("type", Type.ENUMERATE, "RACE", "ORGANISATION", "PROJECT", "JOB", "PERSON", "BUILDING", "ITEM", "COUNTRY", "TOWN", "GROUP", "ANIMAL", "PLANT", "VEHICLE",
				"BLUEPRINT", "PAPERS", "POWER", "ACTION", "TERRAIN", "NODE");
		category.field("name", Type.STRING);
		category.field("default_specials", Type.ARRAY, specials);
		category.field("dependency", Type.ARRAY, dependency);
		category.field("effect", Type.ARRAY, effect);
		category.field("description", Type.STRING);

		dependency.field("on", Type.RELATION, category);
		dependency.field("number", Type.BYTE);

		effect.field("item", Type.RELATION, category);
		effect.field("number", Type.BYTE);

		Record relation = project.table("State"); // possible states between things in the world
		relation.field("type", Type.ENUMERATE, "OWNER", "FAMILY", "ROMANCE", "MEMBER", "FRIENDSHIP", "FAVOR", "FACT", "JOB", "POLITICS");
		relation.field("level", Type.BYTE);
		relation.field("name", Type.STRING);
		relation.index("relations", "name");

		/* FUTURE... map of the world
		Record point = project.record("Point"); // single point on the map
		point.field("type", Type.BYTE); // terrain type of this area.. is a TERRAIN style item
		point.field("height", Type.INTEGER); // height in the center of the tile
		point.field("water", Type.BYTE); // direction of the water flow
		point.field("amount", Type.BYTE); // amount of the water flow, special: negative = dry river bed

		Record tile = project.record("map"); // map tile of 32 x 26
		tile.field("position", Type.INTEGER); // mixed bits of x and y position
		tile.field("map", Type.ARRAY, point);

		Record world = project.record("world");
		world.field("map", tile, "position");
		*/

		Record relations = project.record("Relations");

		Record item = project.table("Item"); // actual things in the world (items, persons, organizations, nations)
		item.field("category", Type.RELATION, category);
		item.field("number", Type.INTEGER);
		item.field("item_specials", Type.ARRAY, specials);
		item.field("relation", Type.ARRAY, relations);
		item.field("position", Type.INTEGER); // position on the world
		item.index("items", "number");

		relations.field("type", Type.RELATION, relation);
		relations.field("with", Type.RELATION, item);
		relations.field("started", Type.INTEGER); // actual started or planned to start
		relations.field("stopped", Type.INTEGER); // actual stopped or planned to stop
		relations.field("relation_specials", Type.ARRAY, specials);

		/* FUTURE.. movement of groups in action
		Record step = project.record("Step"); // A step on the route
		step.field("position", Type.INTEGER);
		step.field("duration", Type.INTEGER); // duration to move to this position from the last
		*/

		Record player = project.record("Player"); // Player driven character
		player.field("name", Type.STRING);
		player.field("character", Type.RELATION, item); // current character played in the world

		return project;
	}
}
