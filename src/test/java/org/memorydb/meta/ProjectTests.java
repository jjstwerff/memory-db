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
	protected void assertContent(Object mainTest, String name, String data) {
		try {
			String resName = "/" + mainTest.getClass().getCanonicalName() + "/" + name;
			URL resource = mainTest.getClass().getResource(resName);
			if (resource == null)
				throw new RuntimeException("Could not find resource " + resName);
			File file = new File(resource.getFile());
			String current = FileUtils.readFileToString(file);
			if (!current.equals(data)) {
				FileUtils.write(file, data);
				Assert.assertEquals(current, data);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected String resource(Object mainTest, String name) {
		String resName = "/" + mainTest.getClass().getCanonicalName() + "/" + name;
		return mainTest.getClass().getResource(resName).getFile();
	}

	protected void compare(Project project) {
		try {
			Path temp = Files.createTempDirectory("generate_");
			String dir = project.getDir();
			project.setDir(temp + "/" + dir);
			Generate.project(project);
			ProcessBuilder pb = new ProcessBuilder("meld", project.getDir(), dir);
			pb.inheritIO();
			pb.start();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected void validate(Project project) {
		try {
			Path temp = Files.createTempDirectory("generate_");
			project.setDir(temp + "/" + project.getDir());
			Generate.project(project);
			Files.walk(temp).filter(Files::isRegularFile).forEach(f -> {
				try {
					Path subpath = f.subpath(temp.getNameCount(), f.getNameCount());
					String expected = new String(Files.readAllBytes(subpath));
					String actual = new String(Files.readAllBytes(f));
					if (!expected.equals(actual))
						Assert.assertEquals("File " + f.getFileName(), expected, actual);
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
