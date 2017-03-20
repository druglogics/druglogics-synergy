package druglogics2;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import gitsbe.*;
import drabme.*;

public class Launcher {

	
	public static void main (String[] args) {

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
		
		
		args = new String[] {"example_run_ags_input"} ;
		
		// Gitsbe input
		String filenameNetwork = "" ;
		String filenameConfig = "" ;
		String filenameSteadyState = "" ;
		String directoryOutput ;
		
		// Drabme input
		String projectName ;
		String directoryModels ;
		String filenameDrugs = "" ;
		String filenameModelOutputs = "" ;
		String filenameCombinations = "" ;
		String directoryTmp ;
		
		if (args.length == 0)
		{
	
			
		}
		if (args.length == 10)
		{
			// Gitsbe input
			filenameNetwork = args[1];
			filenameConfig = args[2];
			filenameSteadyState = args[3] ;
			directoryOutput = args[4] ;
			
			// Drabme input
			projectName = args[0] ;
			directoryModels = args[4] + File.separator + args[5] ;
			filenameDrugs = args[6];
			filenameModelOutputs = args[7];
			filenameCombinations = args[8];
			directoryTmp = args[4] + File.separator + args[9] ;
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
			
			directoryOutput = new File(directoryInput, projectName + "_" + dateFormat.format(cal.getTime())).getAbsolutePath() ;
			
			directoryTmp = new File(directoryOutput, "tmp").getAbsolutePath() ;
			
			directoryModels = new File(directoryOutput, "models").getAbsolutePath() ;
					
			System.out.println("Finding input files in directory: " + directoryInput) ;
			// Load all files in input file directory, with filename identifying input type
		    File[] files = new File(directoryInput).listFiles();
			
		    for (int i = 0; i < files.length; i++) {
		    	String filename = files[i].getName().toLowerCase() ;
		    	
		    	if (filename.contains("steadystate"))
		    	{
		    		if (filenameSteadyState.length() > 0)
		    		{
		    			System.out.println("Aborting, multiple steady state files detected: " + filename + ", " + filenameSteadyState);
		    			return ;
		    		}
		    		filenameSteadyState = filename ;
		    	}
		    	else if (filename.contains("config"))
		    	{	
		    		if (filenameConfig.length() > 0)
		    		{
		    			System.out.println("Aborting, multiple config files detected: " + filename + ", " + filenameConfig);
		    			return ;
		    		}
		    	
		    		filenameConfig = filename ;
		    	}
		    	else if (filename.contains("network")) 
		    	{
		    		if (filenameNetwork.length() > 0)
		    		{
		    			System.out.println("Aborting, multiple network files detected: " + filename + ", " + filenameNetwork);
		    			return ;
		    		}
		    		filenameNetwork = filename ;
		    	}
		    	else if (filename.contains("drugs")) 
		    	{
		    		if (filenameDrugs.length() > 0)
		    		{
		    			System.out.println("Aborting, multiple drug definition files detected: " + filename + ", " + filenameDrugs);
		    			return ;
		    		}
		    		filenameDrugs = filename ;
		    	}
		    	else if (filename.contains("modeloutputs")) 
		    	{
		    		if (filenameModelOutputs.length() > 0)
		    		{
		    			System.out.println("Aborting, multiple model output files detected: " + filename + ", " + filenameModelOutputs);
		    			return ;
		    		}
		    		filenameModelOutputs = filename ;
		    	}
		    	else if (filename.contains("perturbations")) 
		    	{
		    		if (filenameCombinations.length() > 0)
		    		{
		    			System.out.println("Aborting, multiple perturbation files detected: " + filename + ", " + filenameCombinations);
		    			return ;
		    		}
		    		filenameCombinations = filename ;
		    	}
		    		
		    	
		    }  
		    
		    System.out.println(
		    		"\nGitsbe input files:" +
		    		"\nConfig:      \t" + filenameConfig +
		    		"\nNetwork:     \t" + filenameNetwork +
		    		"\nSteadystate:\t" + filenameSteadyState +
		    		"\n\nDrabme input files" +
		    		"\nDrugs:        \t" + filenameDrugs + 
		    		"\nPerturbations:\t" + filenameCombinations +
		    		"\nModel outputs:\t" + filenameModelOutputs) ;
		    System.out.println ("\nOutput directory: " + directoryOutput + "\n") ;		
			
			
		}
		
		else {
			System.out.println("No user argumetns supplied") ;
			System.out.println("Usage alternative 1: specify directory with input files:\n" +
								"druglogics <input files directory> [project name]\n") ;
			System.out.println("Usage alternative 2: specify input files as command line arguments:\n" + 
								"druglogics <project name> <filename network> <filename config file> <filename steady states file> " +
								"<output directory> <directory models> <filename drugs> <filename model outputs> " +
								"<filename combinations> <directory tmp> ") ;
			System.out.println("\nTestrun: setting up run with example files:");

//			args = new String[] {"toy_ags",						// args[0]
//								 "toy_ags_network.sif", 		// args[1]
//								 "toy_ags_config.tab", 			// args[2]
//								 "toy_ags_steadystate.tab",		// args[3]
//								 "example_run_ags",				// args[4]
//								 "models",						// args[5]
//								 "toy_ags_drugs.tab",			// args[6]
//								 "toy_ags_modeloutputs.tab",	// args[7]
//								 "toy_ags_perturbations.tab",	// args[8]
//								 "tmp"							// args[9]
//								 } ;
//			System.out.println("gitsbe " + args[1] + " " + args[2] + " " + args[3] + " " + args[4] + "\n\n")  ;
//			System.out.println("drabme " + args[0] + " " + args[5] + " " + args[6] + " " + args[7] + " " + args[8] + " " + args[9] + "\n\n")  ;
//			System.out.println("druglogics " + args [0] + " " + args[1] + " " + args[2] + " " + args[3] + " " 
//								+ args[4] + " " + args[5] + " " + args[6] + " " + args[7] + " " + args[8] + " " + args[9] + "\n\n");
			
			return ;
		}
		
		Gitsbe gitsbe ;
		Drabme drabme ;
		
		// -------------------------------------------------------------
//		directoryOutput = "barbara" ;
//		
//		// make sure path to output directory is absolute, since BNreduction will be run from another working directory
//		if ((directoryOutput.length() > 0) && !(new File (directoryOutput).isAbsolute()))
//			directoryOutput = System.getProperty("user.dir") + File.separator + directoryOutput ;
//
//		
//		filenameNetwork = directoryOutput + File.separator + "barbara_20170303_reversed.sif";
//		filenameConfig = directoryOutput + File.separator + "barbara_config_20170318_RANDOM.tab" ;
//		filenameSteadyStates = new String[] {directoryOutput + File.separator + "RANDOM_steadystate.tab"} ;
//		
//		projectName = "RANDOM" ;
//		
//		filenameModelOutputs = directoryOutput + File.separator + "modeloutputs_barbara_ags_20170318.txt" ;
//		filenameDrugs = directoryOutput + File.separator + "drugpanel_barbara_20170303_reversed.txt" ;
//		
//		filenameCombinations = directoryOutput + File.separator + "barbara_perturbations.tab" ;
//		directoryTmp = directoryOutput + File.separator + "tmp" ;
//		
//		directoryModels = directoryOutput + File.separator + "models" ;
		
		
		
		// -------------------------------------------------------------		
				
//		directoryOutput = "ags" ;
//
//		// make sure path to output directory is absolute, since BNreduction will be run from another working directory
//				if ((directoryOutput.length() > 0) && !(new File (directoryOutput).isAbsolute()))
//					directoryOutput = System.getProperty("user.dir") + File.separator + directoryOutput ;
//				
//		filenameNetwork = directoryOutput + File.separator + "20150827_ags_original.sif";
//		filenameConfig = directoryOutput + File.separator + "barbara_config_20170318_RANDOM.tab" ;
//		filenameSteadyStates = new String[] {directoryOutput + File.separator + "RANDOM_steadystate.tab"} ;
//		
//		projectName = "AGS_RANDOM" ;
//		filenameModelOutputs = directoryOutput + File.separator + "20150824_ags_modeloutputs.txt" ;
//		filenameDrugs = directoryOutput + File.separator + "20150824_ags_drugpanel.txt" ;
//		filenameCombinations = directoryOutput + File.separator + "barbara_perturbations.tab" ;
//		directoryTmp = directoryOutput + File.separator + "tmp" ;
//		
//		directoryModels = directoryOutput + File.separator + "models" ;
		
		// -------------------------------------------------------------		

//		directoryOutput = "barba_topo_ags_ss_20170319" ;
//		
//		// make sure path to output directory is absolute, since BNreduction will be run from another working directory
//				if ((directoryOutput.length() > 0) && !(new File (directoryOutput).isAbsolute()))
//					directoryOutput = System.getProperty("user.dir") + File.separator + directoryOutput ;
//				
//		filenameNetwork = directoryOutput + File.separator + "barbara_20170303_reversed.sif";
//		filenameConfig = directoryOutput + File.separator + "barbara_config_20170319.tab" ;
//		filenameSteadyStates = new String[] {directoryOutput + File.separator + "AGS_steadystate_barbara.tab"} ;
//		
//		projectName = "barbara_topo_ags_ss" ;
//		filenameModelOutputs = directoryOutput + File.separator + "modeloutputs_barbara_ags_20170318.txt" ;
//		filenameDrugs = directoryOutput + File.separator + "drugpanel_barbara_20170303_reversed.txt" ;
//		filenameCombinations = directoryOutput + File.separator + "barbara_perturbations.tab" ;
//		directoryTmp = directoryOutput + File.separator + "tmp" ;
//		
//		directoryModels = directoryOutput + File.separator + "models" ;
		
		// -------------------------------------------------------------		
		
		
		
		// append date/time to output directory name to avoid conflicts
//		directoryOutput += "_" + dateFormat.format(cal.getTime()) + File.separator ;
		
		// make sure path to output directory is absolute, since BNreduction will be run from another working directory
		if ((directoryOutput.length() > 0) && !(new File (directoryOutput).isAbsolute()))
			directoryOutput = System.getProperty("user.dir") + File.separator + directoryOutput ;
				

		Thread t ;

//		 Run Gitsbe
	
		
		t = new Thread (new Gitsbe (
				projectName, 
				filenameNetwork, 
				filenameSteadyState, 
				filenameConfig, 
				directoryOutput)) ;
		
		t.start();
		try {
			t.join ();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		

		// make sure path to tmp directory is absolute, since BNreduction will be run from another working directory
		if (!new File (directoryTmp).isAbsolute())
			directoryTmp = System.getProperty("user.dir") + File.separator + directoryTmp ;
		
		
		// Run Drabme
		
		int verbosity = 3;
		int combosize = 2;

	    t = new Thread(new Drabme(verbosity, 
	    							projectName,
	    							directoryModels,
	    							filenameDrugs, 
	    							filenameCombinations, 
	    							filenameModelOutputs,
	    							directoryOutput,
	    							directoryTmp,
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
