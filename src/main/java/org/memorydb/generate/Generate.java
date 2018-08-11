package org.memorydb.generate;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;

public class Generate {
	private static final String TABLE = "table";
	private final Configuration ftl;
	private final Project project;

	private Generate(Project project) {
		ftl = new Configuration(Configuration.VERSION_2_3_23);
		ftl.setTemplateLoader(new ClassTemplateLoader(getClass(), "/"));
		this.project = project;
	}

	public static void project(Project project) {
		Generate gen = new Generate(project);
		gen.generate();
	}

	private void generate() {
		project.verify();
		template(ftl, "project", project, project.getName());
		for (Entry<String, Record> entry : project.getTables().entrySet()) {
			Record record = entry.getValue();
			if (!record.getIncluded().isEmpty()) {
				template(ftl, "include", project, record.getName(), TABLE, record);
				for (Field field : record.getFields())
					if (field.getType() == Type.ARRAY)
						template(ftl, "array", project, field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1) + "Array", "field", field);
				template(ftl, "changeIncl", project, "Change" + record.getName(), TABLE, record);
			}
			if (record.isFull()) {
				template(ftl, "record", project, record.getName(), TABLE, record);
				for (Field field : record.getFields())
					if (field.getType() == Type.ARRAY)
						template(ftl, "array", project, field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1) + "Array", "field", field);
				template(ftl, "change", project, "Change" + record.getName(), TABLE, record);
			}
		}
	}

	private static void template(Configuration ftl, String name, Project project, String file) {
		template(ftl, name, project, file, null, null);
	}

	private static void template(Configuration ftl, String name, Project project, String file, String dataName, Object data) {
		Map<String, Object> viewModel = new TreeMap<>();
		viewModel.put("project", project);
		if (dataName != null)
			viewModel.put(dataName, data);
		Path out = Paths.get(project.getDir() + File.separator + file + ".java");
		checkDirectory(out);
		try (BufferedWriter writer = Files.newBufferedWriter(out)) {
			ftl.getTemplate("/templates/" + name + ".ftl").process(viewModel, writer);
		} catch (Exception e) {
			throw new GenerateException(e);
		}
	}

	private static void checkDirectory(Path out) {
		Path parentDir = out.getParent();
		if (parentDir == null)
			throw new GenerateException("Could not read parent directory");
		try {
			if (!Files.exists(parentDir))
				Files.createDirectories(parentDir);
		} catch (Exception e) {
			throw new GenerateException(e);
		}
	}
}
