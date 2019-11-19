# Input Data for AGS publication

- The `network.sif` file is derived from the GINSIM model of the paper: [Discovery of Drug Synergies in Gastric Cancer Cells Predicted by Logical Modeling](https://doi.org/10.1371/journal.pcbi.1004426) - node names have been changed to suit the CASCADE model.
- The file `steadystate` has the AGS Gold (Literature-derived) steady state as stated in the paper above (S4 Table).
- The file `steadystate_random` defined a simple rule to produce proliferating models from the ismulations.
- The 2 above files don't count as input to the simulation. They are there in case you want to change the `training_data` file, as: `cat steadystate >> training`.
