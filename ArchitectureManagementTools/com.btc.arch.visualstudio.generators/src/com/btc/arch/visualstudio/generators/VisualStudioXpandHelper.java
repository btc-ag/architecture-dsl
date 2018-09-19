package com.btc.arch.visualstudio.generators;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;

import com.btc.arch.architectureDsl.Module;
import com.btc.arch.base.dependency.DependencyParseException;
import com.btc.arch.visualstudio.CSProjDependencyParser;

public class VisualStudioXpandHelper {
	public static String getProjectUUID(String projectFile)
			throws DependencyParseException, IOException {
		final CSProjDependencyParser parser = CSProjDependencyParser
				.createFromURI(URI.create("file:"
						+ projectFile.replace("\\", "/")));
		return parser.getProjectUUID();
	}
	
	public static String getRelativePathTo(Module sourceModule, Module targetModule, String releaseUnitName){
		String[] sourceModuleSubstrings = sourceModule.getName().split("\\.");
		String[] targetModuleSubstrings = targetModule.getName().split("\\.");
		int firstDifferentSubstring = 0;
		for (int i = 0; i < targetModuleSubstrings.length; i++) {
			if (!sourceModuleSubstrings[i].equals(targetModuleSubstrings[i])){
				firstDifferentSubstring = i;
				break;
			}
		}
		String relativePath = "";
		for (int i = firstDifferentSubstring; i < targetModuleSubstrings.length; i++) {
			relativePath = relativePath+targetModuleSubstrings[i]+"\\";
		}
		for (int i = firstDifferentSubstring; i < sourceModuleSubstrings.length; i++) {
			relativePath = "..\\"+relativePath;
		}
		return relativePath;
	}
	
	public static List<String> getCsFiles(String targetDir, String windowsPath){
		File directory = new File(targetDir+"\\"+windowsPath);
		File[] listOfFiles = directory.listFiles();
		List<String> csFiles = new ArrayList<String>();
		for (File file : listOfFiles) {
			if (file.getName().endsWith(".cs")){
				csFiles.add(file.getName());
			}
		}
		return csFiles;
	}
}
