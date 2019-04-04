package eu.druglogics.druglogics2;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static eu.druglogics.gitsbe.util.Util.*;
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

		if (environmentalVariableBNETisNULL())
			return;
		if (!setupAndValidateInput(args))
			return;

		runGitsbe();
		runDrabme();
	}

	private boolean setupAndValidateInput(String[] args) {

		if (args.length == 10) {
			// Gitsbe input
			filenameNetwork = args[1];
			filenameConfig = args[2];
			filenameTrainingData = args[3];
			directoryOutput = args[4];

			// Drabme input
			projectName = args[0];
			directoryModels = args[4] + File.separator + args[5];
			filenameDrugs = args[6];
			filenameModelOutputs = args[7];
			filenamePerturbations = args[8];
			directoryTmpDrabme = args[4] + File.separator + args[9];
		} else if ((args.length == 1) || (args.length == 2)) {
			String directoryInput = args[0];
			directoryInput = makeDirectoryPathAbsolute(directoryInput);

			// Create project name unless specified
			if (args.length == 2)
				projectName = args[1].trim();
			else
				projectName = new File(directoryInput).getName();

			DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");

			directoryOutput = new File(directoryInput, projectName + "_" + dateFormat.format(Calendar.getInstance().getTime()))
					.getAbsolutePath();
			directoryTmpGitsbe = new File(directoryOutput, "gitsbe_tmp").getAbsolutePath();
			directoryTmpDrabme = new File(directoryOutput, "drabme_tmp").getAbsolutePath();

			directoryOutput = makeDirectoryPathAbsolute(directoryOutput);
			directoryTmpGitsbe = makeDirectoryPathAbsolute(directoryTmpGitsbe);
			directoryTmpDrabme = makeDirectoryPathAbsolute(directoryTmpDrabme);
			directoryModels = new File(directoryOutput, "models").getAbsolutePath();

			System.out.println("Finding input files in directory: " + directoryInput);
			File[] files = new File(directoryInput).listFiles();

			for (File file : files) {
				if (!file.isDirectory()) {
					String filename = file.getName();
					if (!loadFileFromDirectory(filename, directoryInput))
						return false;
				}
			}

			printInputFilesMessage();
		} else {
			printNoArgumentsMessage();
			return false;
		}

		return true;
	}

	/**
	 * Loads file from input directory and checks for duplicates based on the
	 * existence of specified strings in the name of the file. Returns true if there
	 * is no duplication and file was loaded correctly
	 */
	private boolean loadFileFromDirectory(String filename, String directoryInput) {
		if (filename.toLowerCase().contains("training")) {
			if (filenameTrainingData.length() > 0) {
				System.out.println(
						"Aborting, multiple training data files detected: " + filename + ", " + filenameTrainingData);
				return false;
			}
			filenameTrainingData = new File(directoryInput, filename).getAbsolutePath();
		} else if (filename.toLowerCase().contains("config")) {
			if (filenameConfig.length() > 0) {
				System.out.println("Aborting, multiple config files detected: " + filename + ", " + filenameConfig);
				return false;
			}
			filenameConfig = new File(directoryInput, filename).getAbsolutePath();
		} else if (filename.toLowerCase().contains("network")) {
			if (filenameNetwork.length() > 0) {
				System.out.println("Aborting, multiple network files detected: " + filename + ", " + filenameNetwork);
				return false;
			}
			filenameNetwork = new File(directoryInput, filename).getAbsolutePath();
		} else if (filename.toLowerCase().contains("drugpanel")) {
			if (filenameDrugs.length() > 0) {
				System.out.println(
						"Aborting, multiple drug definition files detected: " + filename + ", " + filenameDrugs);
				return false;
			}
			filenameDrugs = new File(directoryInput, filename).getAbsolutePath();
		} else if (filename.toLowerCase().contains("modeloutputs")) {
			if (filenameModelOutputs.length() > 0) {
				System.out.println(
						"Aborting, multiple model output files detected: " + filename + ", " + filenameModelOutputs);
				return false;
			}
			filenameModelOutputs = new File(directoryInput, filename).getAbsolutePath();
		} else if (filename.toLowerCase().contains("perturbations")) {
			if (filenamePerturbations.length() > 0) {
				System.out.println(
						"Aborting, multiple perturbation files detected: " + filename + ", " + filenamePerturbations);
				return false;
			}
			filenamePerturbations = new File(directoryInput, filename).getAbsolutePath();
		}
		return true;
	}

	private void printInputFilesMessage() {
		System.out.println("\nGitsbe input files:" + "\nConfig:       \t" + filenameConfig + "\nNetwork:      \t"
				+ filenameNetwork + "\nTraining data:\t" + filenameTrainingData + "\nModel outputs:\t"
				+ filenameModelOutputs + "\n\nDrabme input files:" + "\nDrugs:        \t" + filenameDrugs
				+ "\nPerturbations:\t" + filenamePerturbations + "\nModel outputs:\t" + filenameModelOutputs);
		System.out.println("\nOutput directory: " + directoryOutput + "\n");
	}

	private void printNoArgumentsMessage() {
		System.out.println("No user arguments supplied");
		System.out.println("Usage alternative 1: specify directory with input files:\n"
				+ "druglogics <input files directory> [project name]\n");
		System.out.println("Usage alternative 2: specify input files as command line arguments:\n"
				+ "druglogics <project name> <filename network> <filename config file> <filename training data> "
				+ "<output directory> <directory models> <filename drugs> <filename model outputs> "
				+ "<filename combinations> <directory tmp> ");
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
			System.exit(1);
		}
	}

}