# Introduction

druglogics2 is a Java launcher for launching Java modules Gitsbe and Drabme. druglogics2 will take as input prior knowledge and molecular observations, and produce synergy predictions for specified perturbations.

## Gitsbe

Gitsbe optimizes a logical model given an input topology and training data (steady state or perturbation data).

To automate model parameterization genetic algorithms are applied.

Input to Gitsbe: 
·         Interactions: Binary signed and directed interactions (Cytoscape SIF format)
·         Steady state: Boolean vector containing states of nodes in input interactions, where nodes that should be active are assigned the value 1, and nodes that should be inactive are assigned the value 0. For nodes whose state cannot be determined a dash (-) can optionally be used to explicitly declare that the node state is undetermined.
Output from Gitsbe:
·         Ensemble of models with a stable state matching input steady state

Interactions are first assembled to logical equations, based on a default equation relating a node with its regulators:

Target *= (A or B or C) and not (D or E or F),

where activating regulators A, B and C of a target are combined with logical ‘or’ operators, and inhibitory regulators D, E and F are combined with ‘and not’ operators to determine the state of the target node in the next time step.
 
Next, genetic algorithms are used to obtain a parameterization that produces a logical model with a stable state matching the specified input steady state. First, an initial generation of models is formulated, where a large number of mutations to the parameterization is introduced: Randomly selected equations are mutated from “and not” to “or not”, or vice versa. For each model a fitness score is computed: Each matching Boolean value in the vector of a stable state and the steady state increments the fitness score. Models without a stable state have a fitness of zero. Models with several stable states n obtain a final fitness after integrating all Boolean values in stable state vector and dividing by n.
 
For each generation a user-specified number of models were selected for populating the next generation. First, crossover was done where each selected model would exchange logical equations with other selected models (including itself, thus also enabling asexual reproduction). Then a number of mutations were introduced as described above. After a stable state was obtained the number of mutations introduced per generation was reduced by a user-specified factor. The large number of mutations in the initial phase ensured that a large variation in parameterization could be explored.
 
Evolution was halted either when a user-specified threshold fitness was passed, or in the case this fitness could not be reached evolution was halted when the user-specified maximum number of generations had been spanned.

## Drabme

After repeating evolution a specified number of times the ensemble of models produced by Gitsbe are automatically forwarded to the Drabme module by the druglogics2 launcher. Drabme evaluates synergies in the model.

Input to Drabme:
·         Ensemble of logical models, as produced by Gitsbe
·         Drug panel: List of drugs and the drug target node(s) in the model of each drug
·         Perturbation: The perturbations to be analyzed.
·         Model output nodes with weighted score to evaluate global output (i.e. ‘growth’)

Output from Drabme:
·         Drug synergy predictions, both model-wise and ensemble-wise

For each model all perturbations specified are simulated. For each perturbation the drug panel is consulted to fix the state of the specified node(s) to the value 0 (node state could also be fixed to the value 1 for a drug that activates a signaling entity, and is supported by the software). 

After simulating a perturbation the global output parameter ‘growth’ is computed by integrating a weighted score across the states of model output nodes. For example, if two output nodes A (weight 1) and B (weight -1) were found to have the states A=0, B=1 for a perturbation, the global output would evaluate to A_state x A_weight + B_state x B_weight = 0 x 1 + 1 x (-1) = -1. The global output (‘growth’) is then used to compute synergies.

Synergy is defined as the effect not expected from a reference model of drug combination responses. Both for in silico simulations and in vitro experiments an observed combination effect can be formally defined as the effect E observed for two drugs a and b where E(a,b) is the observed effect in a combination experiment, A(a,b) is the drug combination effect expected from each individual drug’s properties, and S(a,b) is any difference between the observed and the expected drug combination effect. In the case of excess effects observed for a combination S(a,b) is positive, and synergy is called, and conversely for attenuated effects S(a,b) is negative, and antagonism is called. Finally, for drug combinations where E(a,b) equals A(a,b) the drug combination effect can fully be explained by each drug independently.
 
In model simulations the expected drug combination response is defined as the minimum value of the two single perturbations with respect to global output ‘growth’ (similar to the “highest single agent” model for in vitro studies).

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
