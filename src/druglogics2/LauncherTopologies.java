package druglogics2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import gitsbe.*;
import drabme.*;

public class LauncherTopologies {

	final static String repoTopology = "topologyNetworks";

	public static void main (String[] args) throws IOException {

		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss") ;
		Calendar cal = Calendar.getInstance();

		// Check if location of BNReduction.sh is defined in environment variable BNET_HOME 
		if (System.getenv("BNET_HOME") == null)
		{
			System.out.println("Set environment variable BNET_HOME to point to location of BNReduction.sh");
			System.out.println("BNReduction can be obtained from https://github.com/alanavc/BNReduction") ;
			return ;
		}

		// Configure Gitsbe based on command line arguments, set up example run if no parameters are specified
		/*
		 * The command line should also specify:
		 * -r (for generating a 'random' cell line parameterization)
		 * -p n	(for repeating analysis n times)
		 * -v n (for setting verbosity level)
		 * 
		 * Command-line arguments should have higher priority than arguments supplied by config file
		 */

		// Gitsbe input
		String filenameNetwork = "" ;
		String filenameConfig = "" ;
		String filenameSteadyState = "" ;
		String directoryOutput ;
		String filenameTopologies = "" ;

		// Drabme input
		String projectName ;
		String directoryModels ;
		String filenameDrugs = "" ;
		String filenameModelOutputs = "" ;
		String filenameCombinations = "" ;
		String directoryTmpGitsbe ;
		String directoryTmpDrabme ;

		if (args.length == 0)
		{

		}
		if (args.length == 9)
		{
			// Gitsbe input
			filenameNetwork = args[1];
			filenameConfig = args[2];
			filenameSteadyState = args[3] ;
			filenameTopologies = args[4];
			directoryOutput = args[5] ;

			// Drabme input
			projectName = args[0] ;
			filenameDrugs = args[6];
			filenameModelOutputs = args[7];
			filenameCombinations = args[8];
		}

		if ((args.length == 1) || (args.length == 2))
		{
			String directoryInput = args[0] ;

			if ((directoryInput.length() > 0) && !(new File (directoryInput).isAbsolute()))
				directoryInput = new File (System.getProperty("user.dir"), directoryInput).getAbsolutePath() ;

			// Create project name unless specified
			if (args.length == 2)
				projectName = args[1].trim() ;
			else
				projectName = new File (directoryInput).getName() ;

			directoryOutput = new File(System.getProperty("user.dir"), projectName + File.separator + dateFormat.format(cal.getTime())).getAbsolutePath();

			System.out.println("Finding input files in directory: " + directoryInput) ;
			// Load all files in input file directory, with filename identifying input type
			// TODO: more robust way needed to check the files
			File[] files = new File(directoryInput).listFiles();
			for(File f : files)
				System.out.println(f.getName());
			for (int i = 0; i < files.length; i++) {
				String filename = files[i].getName() ;
				if (filename.toLowerCase().contains("steadystate"))
				{
					if (filenameSteadyState.length() > 0)
					{
						System.out.println("Aborting, multiple steady state files detected: " + filename + ", " + filenameSteadyState);
						return ;
					}
					filenameSteadyState = new File (directoryInput, filename).getAbsolutePath() ;
				}
				else if (filename.toLowerCase().contains("config"))
				{	
					if (filenameConfig.length() > 0)
					{
						System.out.println("Aborting, multiple config files detected: " + filename + ", " + filenameConfig);
						return ;
					}

					filenameConfig = new File (directoryInput, filename).getAbsolutePath() ;
				}
				else if (filename.toLowerCase().contains("network") && filename.contains(".sif")) 
				{
					if (filenameNetwork.length() > 0)
					{
						System.out.println("Aborting, multiple network files detected: " + filename + ", " + filenameNetwork);
						return ;
					}
					filenameNetwork = new File(directoryInput, filename).getAbsolutePath() ;
				}
				else if (filename.toLowerCase().contains("drugpanel")) 
				{
					if (filenameDrugs.length() > 0)
					{
						System.out.println("Aborting, multiple drug definition files detected: " + filename + ", " + filenameDrugs);
						return ;
					}
					filenameDrugs = new File(directoryInput, filename).getAbsolutePath() ;
				}
				else if (filename.toLowerCase().contains("modeloutputs")) 
				{
					if (filenameModelOutputs.length() > 0)
					{
						System.out.println("Aborting, multiple model output files detected: " + filename + ", " + filenameModelOutputs);
						return ;
					}
					filenameModelOutputs = new File (directoryInput, filename).getAbsolutePath() ;
				}
				else if (filename.toLowerCase().contains("perturbations")) 
				{
					if (filenameCombinations.length() > 0)
					{
						System.out.println("Aborting, multiple perturbation files detected: " + filename + ", " + filenameCombinations);
						return ;
					}
					filenameCombinations = new File (directoryInput, filename).getAbsolutePath() ;
				}
				else if (filename.toLowerCase().contains("topologies"))
				{
					if (filenameTopologies.length() > 0)
					{
						System.out.println("Aborting, multiple topologies files detected: " + filename + ", " + filenameCombinations);
						return ;
					}
					filenameTopologies = new File (directoryInput, filename).getAbsolutePath() ;
				}

			}  

			System.out.println(
					"\nGitsbe input files:" +
							"\nConfig:      \t" + filenameConfig +
							"\nNetwork:     \t" + filenameNetwork +
							"\nSteadystate:\t" + filenameSteadyState +
							"\nTopologies: \t" + filenameTopologies +
							"\n\nDrabme input files" +
							"\nDrugs:        \t" + filenameDrugs + 
							"\nPerturbations:\t" + filenameCombinations +
							"\nModel outputs:\t" + filenameModelOutputs) ;
			System.out.println ("\nOutput directory: " + directoryOutput + "\n") ;		


		}

		else {
			/* ARGUMENTS: 
			 * OPTION 1: 	ARG 0 = input files directory 			ARG 1 = project name (optional)
			 * 
			 * OPTION 2:  	ARG 0 = project name
			 * 				ARG 1 = filename network file
			 * 				ARG 2 = filename config file 
			 * 				ARG 3 = filename steady states file
			 * 				ARG 4 = filename topologies
			 * 				ARG 5 = output directory
			 * 				ARG 6 = filename drugs file
			 * 				ARG 7 = filename model outputs file
			 * 				ARG 8 = filename drug combination file
			 */
			System.out.println("No user arguments supplied") ;
			System.out.println("Usage alternative 1: specify directory with input files:\n" +
					"druglogics <input files directory> [project name]\n") ;
			System.out.println("Usage alternative 2: specify input files as command line arguments:\n" + 
					"druglogics <project name> <filename network> <filename config file> <filename steady states file> <filename topologies>" +
					"<output directory> <filename drugs> <filename model outputs> " +
					"<filename combinations>") ;
			System.out.println("\n Testrun: setting up run with example files:");
			return ;
		}



		//Read network file and store the data in a list
		ArrayList<String> network = new ArrayList<String>();
		ArrayList<String> hypotheses = new ArrayList<String>();
		if(!(filenameNetwork.isEmpty())){
			FileReader fileNetwork = new FileReader(filenameNetwork);
			BufferedReader readerNetwork = new BufferedReader(fileNetwork);
			try{
				while(readerNetwork.ready()){
					network.add(readerNetwork.readLine());
				}
			} catch (IOException e){
				e.printStackTrace();
			}finally {
				if(readerNetwork != null)
					readerNetwork.close();
			}
		}
		
		//Topology file contains causal relations: read topology file and check if each topology is found in the network
		if(!(filenameTopologies.isEmpty())){

			//Create a new directory to store the different networks with the topologies to test
			File directoryTopologies = new File(System.getProperty("user.dir"), projectName + File.separator + repoTopology);
			if(directoryTopologies.exists() && directoryTopologies.isDirectory())
				System.out.println("Existing repository for topologies");
			else
				directoryTopologies.mkdir();

			//Create new directory where the output files will be stored:
			File directoryout = new File(directoryOutput);
			if(directoryout.exists() && directoryout.isDirectory())
				System.out.println("Existing repository for this specific set");
			else
				directoryout.mkdir();

			//Copy original network file into the topologies directory
			File fileNetwork = new File(filenameNetwork);
			CopyOption[] options = new CopyOption[]{
					StandardCopyOption.REPLACE_EXISTING
			}; 

			Path from = Paths.get(filenameNetwork);
			Path to = Paths.get(directoryTopologies.getAbsolutePath() + File.separator + fileNetwork.getName());
			Path to_tmp = Paths.get(directoryTopologies.getAbsolutePath() + File.separator + removeExtension(fileNetwork.getName()) + "_trimmed.sif");
			try {
				Files.copy(from, to, options);
				Files.copy(from, to_tmp, options);
			} catch (IOException e) {
				e.printStackTrace();
			}

			// New file that will contain the network without the hypothetical relations
			String new_fileNetwork = directoryTopologies.getAbsolutePath() + File.separator + removeExtension(fileNetwork.getName()) + "_trimmed.sif";

			// Add the hypotheses (relations to test) in a list
			FileReader filetopologies = new FileReader(filenameTopologies);
			BufferedReader readerTopologies = new BufferedReader(filetopologies);
			try{
				while(readerTopologies.ready()) {
					hypotheses.add(readerTopologies.readLine());
				}
			} catch (IOException e){
					e.printStackTrace();
			}finally {
					if(readerTopologies != null)
						readerTopologies.close();
			}
			
			for(int j = 0; j < hypotheses.size(); j++){
				String topo = hypotheses.get(j).toString();
				for(int i = 0; i < network.size(); i++)
					if(!(network.get(i).equals(topo)))
						removeLineFromFile(new_fileNetwork, topo);
			}


			// Clear the arraylist containing the network
			network.clear();

			//Fill the above list with the new network
			// (e.g., without the relations that were redundant in the hypotheses and the initial network)
			if(!(filenameNetwork.isEmpty())){
				FileReader fileTrimmedNetwork = new FileReader(new_fileNetwork);
				BufferedReader readerNetwork = new BufferedReader(fileTrimmedNetwork);
				try{
					while(readerNetwork.ready()){
						network.add(readerNetwork.readLine());
					}
				} catch (IOException e){
					e.printStackTrace();
				}finally {
					if(readerNetwork != null)
						readerNetwork.close();
				}
			}

			// Create a new network file for each hypothesis
			for(int j = 0; j < hypotheses.size(); j++){
				BufferedWriter bw = null;
				FileWriter writerNetwork = null;
				bw = null;
				writerNetwork = null;
				try{
					fileNetwork = new File(directoryTopologies.getAbsolutePath() + File.separator + removeExtension(filenameNetwork) + "_" + hypotheses.get(j).toString().replaceAll("[\\W+]", "") + ".sif");
					writerNetwork = new FileWriter(fileNetwork.getAbsolutePath(), false); // this will overwrite any existing file with the same name and path.
					bw = new BufferedWriter(writerNetwork);	     
					bw.write(hypotheses.get(j).toString()+"\n"); // add the relation to test
					for(int i=0; i < network.size(); i++) // add all content of the network
						bw.append(network.get(i)+"\n");

				} catch (IOException e){
					e.printStackTrace();
				} finally {
					if(bw != null)
						bw.close();
					if(writerNetwork != null)
						writerNetwork.close();
				}
			}
			
			// Create a network file with all hypothesis
			
			//Copy original network file into the topologies directory
			BufferedWriter bw = null;
			FileWriter writerNetwork = null;
			bw = null;
			writerNetwork = null;
			try{
				fileNetwork = new File(directoryTopologies.getAbsolutePath() + File.separator + removeExtension(filenameNetwork) + "_allHypo.sif");
				writerNetwork = new FileWriter(fileNetwork.getAbsolutePath(), false); // this will overwrite any existing file with the same name and path.
				bw = new BufferedWriter(writerNetwork);	
				for(int i = 0; i < hypotheses.size(); i++)
					bw.append(hypotheses.get(i).toString()+"\n"); // add the relation to test
				for(int i=0; i < network.size(); i++) // add all content of the network
					bw.append(network.get(i)+"\n");

			} catch (IOException e){
				e.printStackTrace();
			} finally {
				if(bw != null)
					bw.close();
				if(writerNetwork != null)
					writerNetwork.close();
			}
			
			
			
			
			Gitsbe gitsbe ;
			Drabme drabme ;

			//Iterate over each file in the directory containing the different topologies
			for(File topologyFile : directoryTopologies.listFiles()){

				//Make sure it is a SIF file (based on extension only)
				//TODO: find a more robust way to check this
				if(topologyFile.getName().endsWith(".sif")){
					String output;
					output = new File(directoryOutput) + File.separator + topologyFile.getName().substring(0,topologyFile.getName().lastIndexOf('.'));

					// make sure path to output directory is absolute, since BNreduction will be run from another working directory
					if ((output.length() > 0) && !(new File (output).isAbsolute()))
						output = System.getProperty("user.dir") + File.separator + output;

					// Set tmp directories and models directory according to the topology tested.
					directoryTmpGitsbe = new File(output, "gitsbe_tmp").getAbsolutePath() ;
					directoryTmpDrabme = new File(output, "drabme_tmp").getAbsolutePath() ;
					directoryModels = new File(output, "models").getAbsolutePath() ;

					String filenameNetworkTopology = System.getProperty("user.dir") + File.separator + projectName+ File.separator + repoTopology + File.separator + topologyFile.getName();
					Thread t ;

					// Run Gitsbe
					System.out.println(filenameSteadyState);

					t = new Thread (new Gitsbe (
							projectName, 
							filenameNetworkTopology,
							filenameSteadyState, 
							filenameConfig,
							output
							)) ;

					t.start();
					try {
						t.join ();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// make sure path to tmp directory is absolute, since BNreduction will be run from another working directory
					if (!new File (directoryTmpDrabme).isAbsolute())
						directoryTmpDrabme = System.getProperty("user.dir") + File.separator + directoryTmpDrabme ;


					// Run Drabme
					int verbosity = 3;
					int combosize = 2;

					t = new Thread(new Drabme(verbosity, 
							projectName,
							directoryModels,
							filenameDrugs, 
							filenameCombinations, 
							filenameModelOutputs,
							output,
							directoryTmpDrabme,
							combosize));
					t.start();
					try {
						t.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();


					}

				}
			}
		}
	}

	public static String removeExtension(String s) {

		String separator = System.getProperty("file.separator");
		String filename;

		// Remove the path up to the filename.
		int lastSeparatorIndex = s.lastIndexOf(separator);
		if (lastSeparatorIndex == -1) {
			filename = s;
		} else {
			filename = s.substring(lastSeparatorIndex + 1);
		}

		// Remove the extension.
		int extensionIndex = filename.lastIndexOf(".");
		if (extensionIndex == -1)
			return filename;

		return filename.substring(0, extensionIndex);
	}


	public static void removeLineFromFile(String file, String lineToRemove) {

		try {

			File inFile = new File(file);

			if (!inFile.isFile()) {
				System.out.println("Parameter is not an existing file");
				return;
			}

			//Construct the new file that will later be renamed to the original filename.
			File tempFile = new File(inFile.getAbsolutePath() + ".tmp");
			BufferedReader br = new BufferedReader(new FileReader(file));
			PrintWriter pw = new PrintWriter(new FileWriter(tempFile));

			String line = null;

			//Read from the original file and write to the new
			//unless content matches data to be removed.
			while ((line = br.readLine()) != null) {

				if (!line.trim().equals(lineToRemove)) {

					pw.println(line);
					pw.flush();
				}
			}
			pw.close();
			br.close();

			//Delete the original file
			if (!inFile.delete()) {
				System.out.println("Could not delete file");
				return;
			}

			//Rename the new file to the filename the original file had.
			if (!tempFile.renameTo(inFile))
				System.out.println("Could not rename file");

		}
		catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
