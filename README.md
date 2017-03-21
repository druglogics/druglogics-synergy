# To install pipeline #
## Clone git repos##
First, install all repositories gitsbe, drabme, and druglogics2. Use the aaf branch of gitsbe and drabme.
```
cd git

git clone https://bitbucket.org/asmundf/gitsbe.git -b gitsbe_aaf

git clone https://bitbucket.org/asmundf/drabme.git -b drabme_aaf

git clone https://bitbucket.org/asmundf/druglogics2.git
```
## Compile ##
```
cd druglogics2

mkdir build

javac -d build -cp ../drabme/lib/combinatoricslib-2.1.jar:../drabme/lib/commons-math3-3.4.1.jar ../gitsbe/src/gitsbe/*.java ../drabme/src/drabme/*.java src/druglogics2/*.java
```
## Run ags example ##
Before you can run the pipeline make sure to install BNReduction (bnet) with Macalay2 v1.6 (later versions won't work) and boost v1.55. Then, to run the pipeline, type:
```
java -cp build:../drabme/lib/combinatoricslib-2.1.jar:../drabme/lib/commons-math3-3.4.1.jar druglogics2.Launcher example_run_ags/
```
# Howto #
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