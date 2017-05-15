package druglogics2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
			try {
				Files.copy(from, to, options);
			} catch (IOException e) {
				e.printStackTrace();
			}

			// Read the topologies and check if they are in the original network file
			FileReader filetopologies = new FileReader(filenameTopologies);
			BufferedReader readerTopologies = new BufferedReader(filetopologies);

			while(readerTopologies.ready()) {
				BufferedWriter bw = null;
				FileWriter writerNetwork = null;
				boolean isFound = false;
				String topo = readerTopologies.readLine().toString();
				for(int i = 0; i < network.size(); i++)
					if(network.get(i).equals(topo))
						isFound = true;	

				if(isFound == false){ // Causal relation not found in initial topology (filenameNetwork)
					bw = null;
					writerNetwork = null;
					try{
						//Create a new file with the network containing the new topology
						fileNetwork = new File(directoryTopologies.getAbsolutePath()+ File.separator +removeExtension(filenameNetwork)+"_"+topo.replaceAll("[\\W+]", "")+".sif");
						writerNetwork = new FileWriter(fileNetwork.getAbsolutePath(), true);
						bw = new BufferedWriter(writerNetwork);	        			
						bw.write(topo+"\n");
						for(int i=0; i < network.size(); i++)
							bw.append(network.get(i)+"\n");

					} catch (IOException e){
						e.printStackTrace();
					} finally {

						try{

							if(bw != null)
								bw.close();
							if(writerNetwork != null)
								writerNetwork.close();

						} catch (IOException ex){
							ex.printStackTrace();
						}
					}
				}
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
					System.out.println(filenameNetworkTopology);
					Thread t ;

					// Run Gitsbe
					System.out.println(filenameSteadyState);

					t = new Thread (new Gitsbe (
							projectName, 
							filenameNetworkTopology,
							filenameSteadyState, 
							filenameConfig,
							output,
							directoryTmpGitsbe)) ;

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
}
