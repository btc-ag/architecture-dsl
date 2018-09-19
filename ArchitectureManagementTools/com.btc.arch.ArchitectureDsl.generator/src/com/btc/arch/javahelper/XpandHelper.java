package com.btc.arch.javahelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.btc.arch.architectureDsl.DirectoryReference;
import com.btc.arch.architectureDsl.Module;
import com.btc.arch.architectureDsl.Framework;
import com.btc.arch.architectureDsl.Platform;
import com.btc.arch.architectureDsl.PlatformSet;

/**
 * This class contains helper method to be used only in Extensions defined for
 * the use in Xpand model-to-text generators.
 * 
 * @author SIGIESEC
 * 
 */
public class XpandHelper {
	// FIXME alles CAB-Spezifische sollte in ein eigenes Modul (und es sollte
	// keine statische Abhängigkeit von diesem geben)

	public static String getWindowsPath(Module module) {
		String modulePath = "";
		if (module.getName().startsWith("BTC.CAB."))
			modulePath += module.getName().replace(".", "\\");
		else if (module.getName().startsWith("Btc"))
			modulePath += addCharAtCamelCase(removeCABPrefix(module.getName()),
					'\\');
		else {
			modulePath = module.getName().replace(".", "\\");
		}
		return modulePath;
	}

	public static String toWindowsPath(String path) {
		return path.replace("/", "\\");
	}

	// TODO: Rename The distinction that leads to the removal of the CAB prefix is not Unix vs. Windows.
	public static String getUnixPathWithoutBtcCabPrefix(Module module) {
		String modulePath = "";
		if (module.getName().startsWith("BTC.CAB."))
			modulePath += removeCABPrefix(module.getName()).replace(".", "/");
		else if (module.getName().startsWith("Btc"))
			modulePath += addCharAtCamelCase(removeCABPrefix(module.getName()),
					'/');
		return modulePath;
	}

	public static String replaceSubstring(String string, String substring,
			String replacement) {
		return string.replace(substring, replacement);
	}

	public static String replaceDotByString(String string, String replacement) {
		return string.replace(".", replacement);
	}

	public static String removeCABPrefix(String string) {
		// remove current CAB prefix
		String resultString = string.replaceFirst("BTC.CAB.", "");
		// remove old CAB prefix
		resultString = resultString.replaceFirst("Btc", "");
		return resultString;
	}

	public static String addCharAtCamelCase(String string, char character) {
		String resultString = string.substring(0, 1);

		for (char currentCharacter : string.substring(1, string.length())
				.toCharArray()) {
			String test = String.valueOf(currentCharacter);
			if (test.equals(test.toUpperCase()))
				resultString = resultString.concat(character + test);
			else
				resultString = resultString.concat(test);
		}
		return resultString;
	}

	public static String addSlashAtCamelCase(String string) {
		return addCharAtCamelCase(string, '/');
	}

	public static List<String> getHeaderFilenames(Module module, String basepath) {
		String path = basepath + "\\" + getWindowsPath(module) + "\\include\\";
		List<String> resultList = new ArrayList<String>();

		File headerDirectory = new File(path);
		if (headerDirectory.isDirectory()) {
			for (File file : headerDirectory.listFiles()) {
				if (file.isFile())
					resultList.add(file.getName());
			}
		}
		return resultList;
	}

	public static List<String> getImplementationFilenames(Module module,
			String basepath) {
		String path = basepath + getWindowsPath(module) + "\\src\\";
		List<String> resultList = new ArrayList<String>();

		File implementationDirectory = new File(path);
		if (implementationDirectory.isDirectory()) {
			for (File file : implementationDirectory.listFiles()) {
				if (file.isFile())
					resultList.add(file.getName());
			}
		}
		return resultList;
	}

	public static String getDriveLetter(String path) {
		return path.substring(0, 2);
	}

	public static String getPathWithoutDriveLetter(String path) {
		return path.substring(2, path.length());
	}
	
	public static boolean containsFramework(List<Framework> frameworks, String frameworkName) {
		boolean result = false;
		for (Framework framework : frameworks) {
			if (framework.getName().equals(frameworkName))
				return true;
		}
		return result;
	}
	
	public static boolean containsPlatformEntry(List<DirectoryReference> directoryReferences, String platformName) {
		boolean result = false;
		for (DirectoryReference directoryReference : directoryReferences) {
			for (PlatformSet platformSet : directoryReference.getPlatformsets()){
				for (Platform platform : platformSet.getPlatforms()){
					if (platform.getName().equals(platformName))
						return true;
				}
			}
		}
		return result;
	}
	
	// TODO: Refactor containsPlatformEntry, getDirectory and getPrefix, so that the nested for loops are only implemented ones.
	public static boolean containsPlatformEntry(DirectoryReference directoryReference, String platformName) {
		boolean result = false;
		for (PlatformSet platformSet : directoryReference.getPlatformsets()){
			for (Platform platform : platformSet.getPlatforms()){
				if (platform.getName().equals(platformName))
					return true;
			}
		}
		return result;
	}
	
	public static String getDirectory(List<DirectoryReference> directoryReferences, String platformName) {
		for (DirectoryReference directoryReference : directoryReferences) {
			for (PlatformSet platformSet : directoryReference.getPlatformsets()){
				for (Platform platform : platformSet.getPlatforms()){
					if (platform.getName().equals(platformName))
						return directoryReference.getDirectory();
				}
			}
		}
		return null;
	}
	
	public static String getPrefix(List<DirectoryReference> directoryReferences, String platformName) {
		for (DirectoryReference directoryReference : directoryReferences) {
			for (PlatformSet platformSet : directoryReference.getPlatformsets()){
				for (Platform platform : platformSet.getPlatforms()){
					if (platform.getName().equals(platformName))
						return directoryReference.getPrefix();
				}
			}
		}
		return null;
	}
}
