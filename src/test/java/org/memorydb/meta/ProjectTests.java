package org.memorydb.meta;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;

import org.memorydb.generate.Generate;
import org.memorydb.generate.Project;
import org.memorydb.structure.NormalCheck;

import junit.framework.Assert;

public class ProjectTests extends NormalCheck {
	protected String content(Object mainTest, String name) {
		try {
			String resName = "/" + mainTest.getClass().getCanonicalName() + "/" + name;
			URL resource = mainTest.getClass().getResource(resName);
			if (resource == null)
				throw new RuntimeException("Could not find resource " + resName);
			return FileUtils.readFileToString(new File(resource.getFile()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected String resource(Object mainTest, String name) {
		String resName = "/" + mainTest.getClass().getCanonicalName() + "/" + name;
		return mainTest.getClass().getResource(resName).getFile();
	}

	protected void validate(Project project) {
		try {
			Path temp = Files.createTempDirectory("generate_");
			project.setDir(temp + "/" + project.getDir());
			Generate.project(project);
			Files.walk(temp).filter(Files::isRegularFile).forEach(f -> {
				try {
					Assert.assertEquals("File " + f.getFileName(), new String(Files.readAllBytes(f.subpath(temp.getNameCount(), f.getNameCount()))), new String(Files.readAllBytes(f)));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
			FileUtils.deleteDirectory(temp.toFile());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
