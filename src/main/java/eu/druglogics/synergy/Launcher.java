package eu.druglogics.synergy;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static eu.druglogics.gitsbe.util.Util.*;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import eu.druglogics.gitsbe.*;
import eu.druglogics.drabme.*;

public class Launcher {

	// Global input
	private String projectName;
	private String directoryOutput;

	// Gitsbe input
	private String filenameNetwork = "";
	private String filenameTrainingData = "";
	private String filenameModelOutputs = "";
	private String filenameConfig = "";
	private String directoryTmpGitsbe;

	// Drabme input
	private String filenameDrugs = "";
	private String filenamePerturbations = "";
	private String directoryModels;
	private String directoryTmpDrabme;

	public static void main(String[] args) {
		Launcher drugLogicsLauncher = new Launcher();
		drugLogicsLauncher.start(args);
	}

	private void start(String[] args) {
		try {
			checkBNET();
		} catch (Exception e) {
			e.printStackTrace();
			abort();
		}

		setupAndValidateInput(args);

		runGitsbe();
		runDrabme();
	}

	private void setupAndValidateInput(String[] args) {

		try {
			CommandLineArgs arguments = new CommandLineArgs();
			JCommander.newBuilder().addObject(arguments).build().parse(args);

			projectName = arguments.getProjectName();
			String directoryInput = arguments.getDirectoryInput();

			// projectName is not required, but we set it either way
			if (projectName == null) {
				projectName = getFileName(directoryInput);
			}

			DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");

			directoryOutput = new File(directoryInput, projectName + "_" +
					dateFormat.format(Calendar.getInstance().getTime())).getAbsolutePath();
			directoryTmpGitsbe = new File(directoryOutput, "gitsbe_tmp").getAbsolutePath();
			directoryTmpDrabme = new File(directoryOutput, "drabme_tmp").getAbsolutePath();
			directoryModels = new File(directoryOutput, "models").getAbsolutePath();

			System.out.println("Finding input files in directory: " + directoryInput);
			File[] files = new File(directoryInput).listFiles();

			for (File file : files) {
				if (!file.isDirectory()) {
					String filename = file.getName();
					loadFileFromDirectory(filename, directoryInput);
				}
			}

			printInputFilesMessage();

		} catch (ParameterException parEx) {
			System.out.println("\nOptions preceded by an asterisk are required.");
			parEx.getJCommander().setProgramName("eu.druglogics.druglogics2.Launcher");
			parEx.usage();
			abort();
		}
	}

	/**
	 * Loads file from input directory and checks for duplicates based on the
	 * existence of specified strings in the name of the file. Returns true if there
	 * is no duplication and file was loaded correctly
	 */
	private void loadFileFromDirectory(String filename, String directoryInput) {
		boolean abort = false;

		String fileID = InputFileIDs.contains(filename);
		String filenameWithFullPath = new File(directoryInput, filename).getAbsolutePath();

		if (fileID != null) {
			switch (fileID) {
				case "training":
					if (filenameTrainingData.length() > 0) {
						System.err.println("Aborting, multiple training data files detected: "
                                + filenameWithFullPath + ", " + filenameTrainingData);
						abort = true;
					} else {
						filenameTrainingData = filenameWithFullPath;
					}
					break;
				case "perturbations":
					if (filenamePerturbations.length() > 0) {
						System.err.println("Aborting, multiple perturbation files detected: "
                                + filenameWithFullPath + ", " + filenamePerturbations);
						abort = true;
					} else {
						filenamePerturbations = filenameWithFullPath;
					}
					break;
				case "modeloutputs":
					if (filenameModelOutputs.length() > 0) {
						System.err.println("Aborting, multiple model output files detected: "
                                + filenameWithFullPath + ", " + filenameModelOutputs);
						abort = true;
					} else {
						filenameModelOutputs = filenameWithFullPath;
					}
					break;
				case "config":
					if (filenameConfig.length() > 0) {
						System.err.println("Aborting, multiple config files detected: "
                                + filenameWithFullPath + ", " + filenameConfig);
						abort = true;
					} else {
						filenameConfig = filenameWithFullPath;
					}
					break;
				case "drugpanel":
					if (filenameDrugs.length() > 0) {
						System.err.println("Aborting, multiple drug definition "
                                + "files detected: " + filenameWithFullPath + ", " + filenameDrugs);
						abort = true;
					} else {
						filenameDrugs = filenameWithFullPath;
					}
					break;
				case "network":
					if (filenameNetwork.length() > 0) {
						System.err.println("Aborting, multiple network files "
                                + "detected: " + filenameWithFullPath + ", " + filenameNetwork);
						abort = true;
					} else {
						filenameNetwork = filenameWithFullPath;
					}
					break;
			}
		}

		if (abort) {
			abort();
		}
	}

	private void printInputFilesMessage() {
		System.out.println("\nGitsbe input files:" + "\nConfig:       \t" + filenameConfig
				+ "\nNetwork:      \t" + filenameNetwork +  "\nTraining data:\t"
                + filenameTrainingData + "\nModel outputs:\t" + filenameModelOutputs
                + "\n\nDrabme input files:" + "\nDrugs:        \t" + filenameDrugs
                + "\nPerturbations:\t" + filenamePerturbations + "\nModel outputs:\t"
                + filenameModelOutputs);
		System.out.println("\nOutput directory: " + directoryOutput + "\n");
	}

	private void runDrabme() {
		Thread thread = new Thread(new Drabme(
				projectName,
				filenameDrugs,
				filenamePerturbations,
				filenameModelOutputs,
				filenameConfig,
				directoryModels,
				directoryOutput,
				directoryTmpDrabme
		));
		execute(thread);
	}

	private void runGitsbe() {
		Thread thread = new Thread(new Gitsbe(
				projectName,
				filenameNetwork,
				filenameTrainingData,
				filenameModelOutputs,
				filenameConfig,
				directoryOutput,
				directoryTmpGitsbe
		));
		execute(thread);
	}

	private void execute(Thread thread) {
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
			abort();
		}
	}

}