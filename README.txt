Two ways to launch druglogics pipeline:
A) input directory
B) command line arguments


A) input directory
------------------
DrugLogics inputs:

1) input directory
2) optional: project name

DrugLogics outputs:
1) output directory on same directory level as input directory

Recommended directory structure:
Project directory
- subfolder with input files
- an output subfolder is created with date and time mark

Example:

AGS_xCELLigence project:
Directory "AGS_xCELLigence"

Content subdirectory "PCB2015":

input_config.tab
input_network.sif
input_steadystate.tab

Subdirectory "PCB2015_output_20170319_0615" created, output files placed here

to launch the pipeline:
"druglogics PCB2015 AGS/PCB2015"

Output folder "AGS/PCB2015_output_20170319_0615" created


B) command line arguments
-------------------------

Usage: 
druglogics <project name> <filename network> <filename config file> <filename steady states file> <output directory> <directory models> <filename drugs> <filename model outputs> <directory tmp> [filename combinations]
