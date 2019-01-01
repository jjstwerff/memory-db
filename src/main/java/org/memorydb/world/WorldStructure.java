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

		Record item = project.record("Item"); // abstract item definition
		item.field("name", Type.STRING);
		item.field("type", Type.ENUMERATE, "TERRAIN", "NODE", "PERSON", "GOOD", "STRUCTURE", "VEHICLE", "ANIMAL");
		item.field("nr", Type.INTEGER);
		item.field("dependency", Type.ARRAY, dependency);
		item.field("effect", Type.ARRAY, effect);
		item.field("description", Type.STRING);

		dependency.field("item", Type.RELATION, item);
		dependency.field("number", Type.BYTE);
		dependency.field("optional", Type.BOOLEAN);

		effect.field("item", Type.RELATION, item);
		effect.field("number", Type.BYTE);

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

		Record good = project.record("Good"); // things in transport
		good.field("item", Type.RELATION, item);
		good.field("number", Type.INTEGER);
		good.field("quality", Type.BYTE);
		good.field("state", Type.ENUMERATE, "EQUIPPED", "STORED");

		Record nation = project.record("Nation");
		nation.field("name", Type.STRING);

		Record spot = project.record("Spot");

		Record guild = project.record("Guild");
		guild.field("name", Type.STRING);
		guild.field("seat", Type.RELATION, spot);
		guild.field("nation", Type.RELATION, nation);

		Record moment = project.record("Moment"); // Memory of a group or a location

		spot.field("position", Type.INTEGER);
		spot.field("height", Type.INTEGER);
		spot.field("goods", Type.ARRAY, good);
		spot.field("owner", Type.RELATION, guild); // who claims ownership on this spot
		spot.field("history", Type.ARRAY, moment);

		Record member = project.record("Member"); // Member list of a group
		member.field("item", Type.RELATION, item);
		member.field("number", Type.INTEGER);
		member.field("quality", Type.BYTE);
		member.field("gear", Type.ARRAY, good);

		Record step = project.record("Step"); // A step on the route
		step.field("position", Type.INTEGER);
		step.field("duration", Type.INTEGER); // duration to move to this position from the last

		Record group = project.record("Group"); // Group of people and vehicles moving along the work
		group.field("members", Type.ARRAY, member);
		group.field("goods", Type.ARRAY, good);
		group.field("route", Type.ARRAY, step);
		group.field("duration", Type.INTEGER); // duration already along the current route
		group.field("leader", Type.BYTE); // leader skill in the group
		group.field("size", Type.INTEGER); // size of the group
		group.field("capacity", Type.INTEGER); // carrying capacity of this group
		group.field("movement", Type.INTEGER); // actual movement speed
		group.field("owner", Type.RELATION, guild);
		group.field("history", Type.ARRAY, moment);

		moment.field("time", Type.INTEGER); // can be negative to indicate planning priority instead of historic fact
		moment.field("type", Type.ENUMERATE, "CREATE", "FIGHT", "VISITED", "SAW", "INTERACTION", "DESTROY", "CLAIM");
		moment.field("with", Type.RELATION, group);

		Record perk = project.record("Perk"); // Learned skills
		perk.field("skill", Type.RELATION, item);
		perk.field("double", Type.BOOLEAN);

		Record player = project.record("Player"); // Player driven character
		player.field("name", Type.STRING);
		player.field("group", Type.RELATION, group);
		player.field("perks", Type.ARRAY, perk);
		player.field("allience", Type.RELATION, guild);
		player.field("history", Type.ARRAY, moment);

		return project;
	}
}
