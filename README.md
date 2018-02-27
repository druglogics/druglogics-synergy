# How to install pipeline #

Requires Java 8

## Clone git repos##
First, install all repositories: gitsbe, drabme, druglogics2 and druglogics_dep. 
```
mkdir druglogics
git clone https://bitbucket.org/asmundf/gitsbe.git
git clone https://bitbucket.org/asmundf/drabme.git
git clone https://bitbucket.org/asmundf/druglogics2.git
git clone https://bitbucket.org/asmundf/druglogics_dep.git
```

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
cd ../druglogics_dep
dpkg -i dep/libpari-gmp3_2.5.0-2ubuntu1_amd64.deb
dpkg -i dep/Macaulay2-1.6-common.deb
dpkg -i dep/Macaulay2-1.6-amd64-Linux-Ubuntu-14.04.deb
unzip dep/bnet_reduction-master.zip -d dep
tar jxfv dep/boost_1_55_0.tar.bz2 -C dep/bnet_reduction-master/
cd dep/bnet_reduction-master/
make clean
make install
```
To test BNReduction you can run 'Testing_BNReduction.sh' to see if all tests pass:
```
./Testing_BNReduction.sh
```
The location of the directory bnet, containing the file BNReduction.sh, must be in the environment variable BNET_HOME:
```
export BNET_HOME=/pathTo/druglogics/druglogics_dep/dep/bnet_reduction-master
echo $BNET_HOME
```

#How to run the pipeline#
## Input directory ##
The recommended way to launch the pipeline is by supplying one argument specifying a directory, e.g. "example_run_ags". The directory should contain all relevant input files, and not more than one copy of each. Input files are identified by filename, and should contain a specific string:

"training"
"config"
"network"
"drugpanel"
"modeloutputs"
"perturbations"

Note that the filename only needs to contain this case insensitive identifier string in any place convenient, i.e. beginning, middle or end of filename.

## Run ags example ##
To run the pipeline with the preset example directory, from the druglogics2 root directory, type:
```
java -cp build:../drabme/lib/combinatoricslib-2.1.jar:../drabme/lib/commons-math3-3.4.1.jar druglogics2.Launcher example_run_ags/
```

It is also possible to launch the pipeline by specifying each input file as a separate argument

## Command line arguments ##

Usage: 
```
druglogics <project name> <filename network> <filename config file> <filename training data> <output directory> <directory models> <filename drugs> <filename model outputs> <filename combinations> <directory tmp>
```
