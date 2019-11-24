# druglogics2

This module is a Java launcher for sequentially launching the Java modules 
[Gitsbe](https://bitbucket.org/asmundf/gitsbe/src/master/) and 
[Drabme](https://bitbucket.org/asmundf/drabme/src/master/). It gets all 
necessary input from a directory (prior knowledge and molecular observations) 
and produces synergy predictions for the specified perturbations.

## Install

Follow the installation notes in the next order:

- [Gitsbe](https://bitbucket.org/asmundf/gitsbe/src/develop/)
- [Drabme](https://bitbucket.org/asmundf/drabme/src/develop/)

Then run:
```
git clone https://bitbucket.org/asmundf/druglogics-synergy.git
mvn clean install
```

# Run the Pipeline

## Input directory

The pipeline is launched by supplying *one argument specifying a directory*, 
e.g. "example_run_ags". The directory should contain all relevant input files, 
and not more than one copy of each. Input files are identified by filename, and 
should contain a specific string:

Input filename identifier | Example filename
------------------------- | ----------------
training | toy_ags_training_data.tab
config | toy_ags_config.tab
network | toy_ags_network.sif
modeloutputs | toy_ags_modeloutputs.tab
drugpanel | toy_ags_drugpanel.tab
perturbations | toy_ags_perturbations.tab

Note that the filename only needs to contain this case insensitive identifier 
string in any place convenient, i.e. beginning, middle or end of filename.

## Run ags example
To run the pipeline with the preset example directory, from the druglogics2 
root directory, run (remember to change the `{version}` to the appropriate 
one, e.g. `1.0`):
```
java -cp ./target/synergy-{version}-jar-with-dependencies.jar eu.druglogics.synergy.Launcher --project=test --inputDir=example_run_ags
```

or run the mvn profile:
```
mvn compile -P runExampleAGS
```
