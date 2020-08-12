# Input Data for AGS publication (cascade 1.0)

- The `network.sif` file is derived from the GINSIM model of the paper: [Discovery of Drug Synergies in Gastric Cancer Cells Predicted by Logical Modeling](https://doi.org/10.1371/journal.pcbi.1004426) - node names have been changed to suit the CASCADE model.
For more information about the network file check the respective [CASCADE repository](https://bitbucket.org/asmundf/cascade/src/master/).
- The file `steadystate` has the AGS Gold (Literature-derived) steady state as stated in the paper above (S4 Table).
The nodes `Prosurvival` and `Antisurvival` were removed as they do not represent *real* biological entities.
The modeloutput 'Antisurvival' nodes `CASP9` and `FOXO_f` as well as the 'Prosurvival' node `RSK_f` were added for completeness.
- The file `random_train` defines a simple rule to produce (random) proliferating models from the simulations.
- You can use one of the 2 above files for simulations by changing the `training` file, e.g.: `cat steadystate > training`.
