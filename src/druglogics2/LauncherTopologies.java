package druglogics2;

import static gitsbe.Util.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import gitsbe.*;
import drabme.*;

public class LauncherTopologies {

	// Global input
	public String projectName;
	public String directoryOutput;

	// Gitsbe input
	public String filenameNetwork = "";
	public String filenameConfig = "";
	public String filenameTrainingData = "";
	public String filenameTopologies = "";

	// Drabme input
	public String directoryModels;
	public String filenameDrugs = "";
	public String filenameModelOutputs = "";
	public String filenameCombinations = "";
	public String directoryTmpGitsbe;
	public String directoryTmpDrabme;

	public static void main(String[] args) {
		LauncherTopologies drugLogicsLauncher = new LauncherTopologies();
		drugLogicsLauncher.start(args);
	}

	public void start(String[] args) {

		if (environmentalVariableBNETisNULL())
			return;
		if (!setupAndValidateInput(args))
			return;

		try {
			runPipelineWithTopologies();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private boolean setupAndValidateInput(String[] args) {

		if (args.length == 12) {

			// Global input
			projectName = args[0];
			directoryOutput = args[1];
			filenameTopologies = args[2];

			// Gitsbe input
			filenameNetwork = args[3];
			filenameConfig = args[4];
			filenameTrainingData = args[5];
			directoryTmpGitsbe = args[1] + File.separator + args[6];

			// Drabme input
			directoryModels = args[1] + File.separator + args[7];
			filenameDrugs = args[8];
			filenameModelOutputs = args[9];
			filenameCombinations = args[10];
			directoryTmpDrabme = args[1] + File.separator + args[11];

		} else if ((args.length == 1) || (args.length == 2)) {
			String directoryInput = args[0];
			directoryInput = makeDirectoryPathAbsolute(directoryInput);

			// Create project name unless specified
			if (args.length == 2)
				projectName = args[1].trim();
			else
				projectName = new File(directoryInput).getName();

			DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
			Calendar calendarData = Calendar.getInstance();

			// Create output directory
			directoryOutput = new File(directoryInput, projectName + "_" + dateFormat.format(calendarData.getTime()))
					.getAbsolutePath();
			if (!createDirectory(directoryOutput))
				return false;
			directoryOutput = makeDirectoryPathAbsolute(directoryOutput);

			System.out.println("Finding input files in directory: " + directoryInput);
			File[] files = new File(directoryInput).listFiles();

			for (int i = 0; i < files.length; i++) {
				String filename = files[i].getName();
				if (!loadFileFromDirectory(filename, directoryInput))
					return false;
			}
			printInputFilesMessage();
		} else {
			printWrongNumberOfArgumentsMessage();
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
			if (filenameCombinations.length() > 0) {
				System.out.println(
						"Aborting, multiple perturbation files detected: " + filename + ", " + filenameCombinations);
				return false;
			}
			filenameCombinations = new File(directoryInput, filename).getAbsolutePath();
		} else if (filename.toLowerCase().contains("topologies")) {
			if (filenameTopologies.length() > 0) {
				System.out.println(
						"Aborting, multiple topologies files detected: " + filename + ", " + filenameCombinations);
				return false;
			}
			filenameTopologies = new File(directoryInput, filename).getAbsolutePath();
		}
		return true;
	}

	private void printInputFilesMessage() {
		System.out.println("\nGitsbe input files:" + "\nConfig:       \t" + filenameConfig + "\nNetwork:      \t"
				+ filenameNetwork + "\nTraining data:\t" + filenameTrainingData + "\nTopologies: \t"
				+ filenameTopologies + "\nModel outputs:\t" + filenameModelOutputs + "\n\nDrabme input files:"
				+ "\nDrugs:        \t" + filenameDrugs + "\nPerturbations:\t" + filenameCombinations
				+ "\nModel outputs:\t" + filenameModelOutputs);
		System.out.println("\nOutput directory: " + directoryOutput + "\n");
	}

	private void printWrongNumberOfArgumentsMessage() {
		System.out.println("To run druglogics, use these two alternatives:\n");
		System.out.println("Usage alternative 1: specify directory with input files:\n"
				+ "druglogics <input files directory> [project name]\n");
		System.out.println("Usage alternative 2: specify input files as command line arguments:\n"
				+ "druglogics <project name> <output directory> <filename topologies> <filename network> "
				+ "<filename config file> <filename training data> <gitsbe tmp directory> "
				+ "<directory models> <filename drugs> <filename model outputs> "
				+ "<filename drug combinations> <drabme tmp directory>");
	}

	public void runPipelineWithTopologies() throws IOException {

		if (!new File(filenameTopologies).exists() || isFileEmpty(filenameTopologies)) {
			System.out.println("ERROR: Topologies file doesn't exist or its empty!");
			System.exit(1);
		}

		// Read the network file and store the data in a list
		ArrayList<String> network = loadNetworkSifFile();

		// Create a new directory to store the different networks with the topologies to
		// test
		String directoryTopologies = new File(directoryOutput, "topologyNetworks").getAbsolutePath();
		createDirectory(directoryTopologies);

		// Copy original network file into the topologies directory and create a
		// duplicate trimmed network file
		String trimmedNetworkFile = directoryTopologies + File.separator + removeExtension(filenameNetwork)
				+ "_trimmed.sif";

		copyFileToDirectory(filenameNetwork, directoryTopologies);
		duplicateFile(filenameNetwork, trimmedNetworkFile);

		// Read the topologies file and store data in the hypotheses list
		ArrayList<String> hypotheses = loadTopologiesFile();

		// Remove from the trimmed network file the hypotheses interactions
		for (int j = 0; j < hypotheses.size(); j++) {
			String singleInteraction = hypotheses.get(j);
			for (int i = 0; i < network.size(); i++)
				if (network.get(i).equals(singleInteraction)) {
					removeLineFromFile(trimmedNetworkFile, singleInteraction);
				}
		}

		// Clear the arraylist containing the network
		network.clear();

		// Fill the above list with the new network
		// (e.g., without the relations that were redundant in the hypotheses and the
		// initial network)
		network = loadTrimmedNetworkFile(trimmedNetworkFile);

		createNewNetworkFileForEachHypothesis(hypotheses, network, directoryTopologies);
		createAllHypothesesNetworkFile(hypotheses, network, directoryTopologies);

		// Iterate over each file in the directory containing the different topologies
		for (File topologyFile : (new File(directoryTopologies)).listFiles()) {
				
				// Create the specified topology directory where gitsbe and drabme will run
				String topologyDirectoryOutput = new File(directoryOutput) + File.separator
						+ topologyFile.getName().substring(0, topologyFile.getName().lastIndexOf('.'));
				topologyDirectoryOutput = makeDirectoryPathAbsolute(topologyDirectoryOutput);

				if (!createDirectory(topologyDirectoryOutput)) {
					System.out.println("ERROR: Couldn't create the directory: " + topologyDirectoryOutput);
					break;
				}

				String filenameNetworkTopology = new File(directoryTopologies, topologyFile.getName())
						.getAbsolutePath();

				// Set tmp directories and models directory according to the topology tested
				directoryTmpGitsbe = new File(topologyDirectoryOutput, "gitsbe_tmp").getAbsolutePath();
				directoryTmpDrabme = new File(topologyDirectoryOutput, "drabme_tmp").getAbsolutePath();
				directoryModels = new File(topologyDirectoryOutput, "models").getAbsolutePath();

				directoryTmpGitsbe = makeDirectoryPathAbsolute(directoryTmpGitsbe);
				directoryTmpDrabme = makeDirectoryPathAbsolute(directoryTmpDrabme);

				Thread thread;

				// Run Gitsbe
				thread = new Thread(new Gitsbe(projectName, filenameNetworkTopology, filenameTrainingData,
						filenameModelOutputs, filenameConfig, topologyDirectoryOutput, directoryTmpGitsbe));
				execute(thread);

				// Run Drabme
				int verbosity = 3;
				int combosize = 2;

				thread = new Thread(
						new Drabme(verbosity, projectName, directoryModels, filenameDrugs, filenameCombinations,
								filenameModelOutputs, topologyDirectoryOutput, directoryTmpDrabme, false, combosize));
				execute(thread);
		}
	}

	private void execute(Thread thread) {
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void createAllHypothesesNetworkFile(ArrayList<String> hypotheses, ArrayList<String> network,
			String directoryTopologies) throws IOException {
		BufferedWriter bw = null;
		FileWriter writerNetwork = null;
		bw = null;
		writerNetwork = null;
		try {
			File fileNetwork = new File(
					directoryTopologies + File.separator + removeExtension(filenameNetwork) + "_allHypo.sif");
			writerNetwork = new FileWriter(fileNetwork.getAbsolutePath(), false); // this will overwrite any
																					// existing file with the same
																					// name and path.
			bw = new BufferedWriter(writerNetwork);
			for (int i = 0; i < hypotheses.size(); i++)
				bw.append(hypotheses.get(i).toString() + "\n"); // add the relation to test
			for (int i = 0; i < network.size(); i++) // add all content of the (trimmed of the hypotheses) network
				bw.append(network.get(i) + "\n");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bw != null)
				bw.close();
			if (writerNetwork != null)
				writerNetwork.close();
		}
	}

	private void createNewNetworkFileForEachHypothesis(ArrayList<String> hypotheses, ArrayList<String> network,
			String directoryTopologies) throws IOException {
		for (int j = 0; j < hypotheses.size(); j++) {
			BufferedWriter bw = null;
			FileWriter writerNetwork = null;
			bw = null;
			writerNetwork = null;
			try {
				File fileNetwork = new File(directoryTopologies + File.separator + removeExtension(filenameNetwork)
						+ "_" + hypotheses.get(j).toString().replaceAll("[\\W+]", "") + ".sif");
				writerNetwork = new FileWriter(fileNetwork.getAbsolutePath(), false); // this will overwrite any
																						// existing file with the
																						// same name and path.
				bw = new BufferedWriter(writerNetwork);
				bw.write(hypotheses.get(j).toString() + "\n"); // add the relation to test
				for (int i = 0; i < network.size(); i++) // add all content of the (trimmed of the hypotheses) network
					bw.append(network.get(i) + "\n");

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (bw != null)
					bw.close();
				if (writerNetwork != null)
					writerNetwork.close();
			}
		}
	}

	private ArrayList<String> loadTrimmedNetworkFile(String trimmedNetworkFile) {
		ArrayList<String> network = null;
		try {
			network = readLinesFromFile(trimmedNetworkFile, true);
		} catch (IOException e) {
			System.out.println("ERROR: Couldn't load the trimmed network file");
			e.printStackTrace();
			System.exit(1);
		}
		return network;
	}

	private ArrayList<String> loadTopologiesFile() {
		ArrayList<String> hypotheses = null;
		try {
			hypotheses = readLinesFromFile(filenameTopologies, true);
		} catch (IOException e) {
			System.out.println("ERROR: Couldn't load the topologies/hypotheses file");
			e.printStackTrace();
			System.exit(1);
		}
		return hypotheses;
	}

	private ArrayList<String> loadNetworkSifFile() {
		ArrayList<String> network = null;
		try {
			network = readLinesFromFile(filenameNetwork, true);
		} catch (IOException e) {
			System.out.println("ERROR: Couldn't load the network file");
			e.printStackTrace();
			System.exit(1);
		}
		return network;
	}
}
