# To install pipeline #
## Clone git repos##
First, install all repositories gitsbe, drabme, and druglogics2. 
```
git clone https://bitbucket.org/asmundf/gitsbe.git

git clone https://bitbucket.org/asmundf/drabme.git

git clone https://bitbucket.org/asmundf/druglogics2.git
```
If you want the latest development versions these would be the gitsbe_aaf and drabme_aaf branches. You can switch to a branch doing `git checkout gitsbe_aaf` and `git checkout drabme_aaf` from the repo directory.

## Compile ##
```
cd druglogics2

mkdir build

javac -d build -cp ../drabme/lib/combinatoricslib-2.1.jar:../drabme/lib/commons-math3-3.4.1.jar ../gitsbe/src/gitsbe/*.java ../drabme/src/drabme/*.java src/druglogics2/*.java

```

## Set up dependencies ##
Before you can run the pipeline make sure to install BNReduction (bnet) with Macalay2 v1.6 (later versions won't work) and boost v1.55. 

Set up Macalay2, boost and BNReduction:
```
sudo apt-get install libpari-gmp3
dpkg -i dep/Macaulay2-1.6-common.deb
dpkg -i dep/Macaulay2-1.6-amd64-Linux-Ubuntu-14.04.deb
unzip dep/bnet_reduction-master.zip -d dep
tar jxfv dep/boost_1_55_0.tar.bz2 -C dep/bnet_reduction-master/
cd dep/bnet_reduction-master/
make
cd ../..
```
To test BNReduction you can run 'Testing_BNReduction.sh' to see if all tests pass

Dependency files can be found in the dep folder of this repository. The location of the directory bnet, containing the file BNReduction.sh, must be in the environment variable BNET_HOME (be sure to replace "/path/to/bnet" with the actual location):
```
export BNET_HOME=/path/to/bnet
```
## Run ags example ##
To run the pipeline, from the druglogics2 root directory, type:
```
java -cp build:../drabme/lib/combinatoricslib-2.1.jar:../drabme/lib/commons-math3-3.4.1.jar druglogics2.Launcher example_run_ags/
```
# Howto #
## Input directory ##
The recommended way to launch the pipeline is by supplying one argument specifying a directory, e.g. "example_run_ags". The directory should contain all relevant input files, and not more than one copy of each. Input files are identified by filename, and should contain a specific string:

"steadystate"
"config"
"network"
"drugpanel"
"modeloutputs"
"perturbations"

Note that the filename only needs to contain this case insensitive identifier string in any place convenient, i.e. beginning, middle or end of filename.

It is also possible to launch the pipeline by specifying each input file as a separate argument

## Command line arguments ##

Usage: 
```
druglogics <project name> <filename network> <filename config file> <filename steady states file> <output directory> <directory models> <filename drugs> <filename model outputs> <directory tmp> [filename combinations]
```