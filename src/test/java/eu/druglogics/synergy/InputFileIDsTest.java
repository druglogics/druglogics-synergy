package eu.druglogics.synergy;

import org.junit.Assert;
import org.junit.Test;

public class InputFileIDsTest {

    @Test
    public void test_contains() {
        Assert.assertNull(InputFileIDs.contains("a_file_with_no_associated_file_ids"));
        Assert.assertNull(InputFileIDs.contains("topology"));

        Assert.assertEquals(InputFileIDs.contains("toy_ags_training_data.tab"), "training");
        Assert.assertEquals(InputFileIDs.contains("toy_ags_perturbations.tab"), "perturbations");
        Assert.assertEquals(InputFileIDs.contains("toy_ags_modeloutputs.tab"), "modeloutputs");
        Assert.assertEquals(InputFileIDs.contains("toy_ags_config.tab"), "config");
        Assert.assertEquals(InputFileIDs.contains("toy_ags_drugpanel.tab"), "drugpanel");
        Assert.assertEquals(InputFileIDs.contains("/home/user/toy_ags_network.sif"), "network");

        Assert.assertEquals("training", InputFileIDs.TRAINING_DATA_FILE.getFileID());
        Assert.assertEquals("perturbations", InputFileIDs.PERTURBATIONS_FILE.getFileID());
        Assert.assertEquals("modeloutputs", InputFileIDs.MODEL_OUTPUTS_FILE.getFileID());
        Assert.assertEquals("config", InputFileIDs.CONFIGURATION_FILE.getFileID());
        Assert.assertEquals("drugpanel", InputFileIDs.DRUG_PANEL_FILE.getFileID());
        Assert.assertEquals("network", InputFileIDs.NETWORK_FILE.getFileID());
    }
}
