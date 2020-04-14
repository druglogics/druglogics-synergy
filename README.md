# druglogics-synergy

This package is a Java launcher for sequentially executing the [Gitsbe](https://bitbucket.org/asmundf/gitsbe/src/master/) and [Drabme](https://bitbucket.org/asmundf/drabme/src/master/) packages. 
It gets all necessary input from a directory (prior knowledge and molecular observations) and produces synergy predictions for the specified perturbations.

## Install

Prerequisites: `maven 3.6.0` and `Java 8`.

Follow the installation guide for each respective package (and in that order):

- [Gitsbe](https://bblodfon.github.io/druglogics-doc/gitsbe-install.html)
- [Drabme](https://bblodfon.github.io/druglogics-doc/drabme-install.html)

Then run:
```
git clone https://bitbucket.org/asmundf/druglogics-synergy.git
mvn clean install
```

## Example

The recommended way to run this package is to use it’s `Launcher`. 
From the root directory of the repo run:

```
java -cp ./target/synergy-1.2.0-jar-with-dependencies.jar eu.druglogics.synergy.Launcher --project=test --inputDir=example_run_ags
```

or run the mvn profile directly (same input as the command above through the `pom.xml`):

```
mvn compile -P runExampleAGS
```

You can use the script `run_druglogics_synergy.sh` to run the above java command with input files from the `ags_cascade_1.0` and `ags_cascade_2.0` directories as well.
This script also offers the possibility to test various input configurations, namely changing the number of simulations, the attractor tool and the choice of training data.

## Input

Running the `druglogics-synergy` Launcher with no parameters, generates a usage message with the available options. 

The only required parameter is the `inputDir`, which is the directory that should contain all relevant input files for the Gitsbe and Drabme packages to execute. 
The input files are identified by a case-insensitive *substring* in their name (and thus must be unique).
Below is a list of identifiers for the input files in the given directory:

Input filename identifier | Example filename
------------------------- | ----------------
`training` | toy_ags_training_data.tab
`config` | toy_ags_config.tab
`network` | toy_ags_network.sif
`modeloutputs` | toy_ags_modeloutputs.tab
`drugpanel` | toy_ags_drugpanel.tab
`perturbations` | toy_ags_perturbations.tab

The only non-required parameters is the `project` (project name) that is used as the name of the directory where the output files will be stored.
