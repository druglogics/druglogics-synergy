# To install pipeline #
## Clone git repos##
First, install all repositories gitsbe, drabme, and druglogics2. Use the aaf branch of gitsbe and drabme:
```
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
Before you can run the pipeline make sure to install BNReduction (bnet) with Macalay2 v1.6 (later versions won't work) and boost v1.55. To run the pipeline, from the druglogics2 root directory, type:
```
java -cp build:../drabme/lib/combinatoricslib-2.1.jar:../drabme/lib/commons-math3-3.4.1.jar druglogics2.Launcher example_run_ags/
```
# Howto #
## Input directory ##
The recommended way to launch the pipeline is by supplying one argument specifying a directory, e.g. "example_run_ags". The directory should contain all relevant input files, and not more than one copy of each. Input files are identified by filename, and should contain a specific string:

"steadystate"
"config"
"network"
"drugs"
"modeloutputs"
"perturbations"

Note that the filename only needs to contain this case insensitive identifier string in any place convenient, i.e. beginning, middle or end of filename.

It is also possible to launch the pipeline by specifying each input file as a separate argument

## Command line arguments ##

Usage: 
```
druglogics <project name> <filename network> <filename config file> <filename steady states file> <output directory> <directory models> <filename drugs> <filename model outputs> <directory tmp> [filename combinations]
```