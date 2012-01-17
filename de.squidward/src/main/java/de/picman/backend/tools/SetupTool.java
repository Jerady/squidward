package de.picman.backend.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class SetupTool {

	private final static int TOP_LEVEL_SIZE = 100;

	private final static String BASE_DIRECTORY_PATH = "/usr/local/PICMAN";
	private final static File BASE_DIRECTORY = new File(BASE_DIRECTORY_PATH);

	public static void main(String[] args) {
		createDirectoryStructure();
	}

	public static boolean initDirectoryStructure() {
		boolean success = false;

		if (!doesBaseDirectoryExist()) {
			int choice = JOptionPane
					.showConfirmDialog(
							null,
							"Directory structure does not exit.\nDo you want me to create it?",
							"", JOptionPane.YES_NO_CANCEL_OPTION);

			if (choice == JOptionPane.YES_OPTION) {
				createBaseDirectory();
				createDirectoryStructure();
			}
		}
		else{
			int choice = JOptionPane
					.showConfirmDialog(
							null,
							"Directory structure do already exit.\nDo you want me to delete it?",
							"", JOptionPane.YES_NO_CANCEL_OPTION);

			if (choice == JOptionPane.YES_OPTION) {
				removeBaseDirectory();
			}
			
			
			
			
		}
	
		success = true;
		return success;
	}

	public static boolean removeBaseDirectory() {
		boolean success = false;
		if (BASE_DIRECTORY.exists()) {
			return BASE_DIRECTORY.delete();
		}
		return success;
	}

	public static boolean createBaseDirectory() {
		boolean success = false;
		if (!BASE_DIRECTORY.exists()) {
			return BASE_DIRECTORY.mkdir();
		}
		return success;
	}

	public static boolean doesBaseDirectoryExist() {
		return BASE_DIRECTORY.exists();
	}

	public static boolean createDirectoryStructure() {
		boolean success = false;

		List<String> directoriesStrings = new ArrayList<String>();
		
		for (int i = 0; i < TOP_LEVEL_SIZE; i++) {
			String topLevelDirName = BASE_DIRECTORY_PATH + "/"
					+ String.format("PIC%02d", i+1);
				directoriesStrings.add(topLevelDirName);
		}
		
		for (String string : directoriesStrings) {
			System.out.println(string);
		}

		return success;
	}

}
