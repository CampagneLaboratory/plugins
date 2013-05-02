package org.campagnelab.gobyweb.clustergateway.submission;

import org.apache.commons.io.FileUtils;
import org.campagnelab.gobyweb.io.AreaFactory;
import org.campagnelab.gobyweb.io.FileSetArea;
import org.campagnelab.gobyweb.io.JobArea;
import org.campagnelab.gobyweb.plugins.Plugins;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.io.IOException;

import static junit.framework.Assert.fail;

/**
 * Test local execution for alignment analyses
 *
 * @author manuele
 */
@RunWith(JUnit4.class)
public class AnalysisLocalSubmission {

    static Plugins plugins;
    static JobArea jobArea;
    static FileSetArea storageArea;
    static Actions actions;
    static final String rootAreaDir = "test-results";
    static final String storageAreaDir = String.format("%s/filesets", rootAreaDir);
    static final String jobAreaDir = String.format("%s/jobs", rootAreaDir);
    static final String owner = "PluginsSDK";
    static String referenceSA =  new File(storageAreaDir).getAbsolutePath();


    @BeforeClass
    public static void configure() {
        plugins = new Plugins();
        plugins.replaceDefaultSchemaConfig(".");
        plugins.addServerConf("test-data/root-for-rnaselect");
        plugins.setWebServerHostname("localhost");
        plugins.reload();
        //prepare the storage area for testing
        try {

            storageArea = AreaFactory.createFileSetArea(
                    referenceSA, owner);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            fail("failed to create the local storage area");
        }

        //create the reference to the job area
        try {
            jobArea = AreaFactory.createJobArea(new File(jobAreaDir).getAbsoluteFile().getAbsolutePath(), owner);
        } catch (IOException ioe) {
            fail("failed to create the local job area");
        }
    }


    @Test(expected = UnsupportedOperationException.class)
    public void submit() {
        try {
            Submitter submitter = new LocalSubmitter(plugins.getRegistry());
            submitter.setSubmissionHostname("");
            submitter.setRemoteArtifactRepositoryPath("");
            actions = new Actions(submitter, referenceSA, jobArea, plugins.getRegistry());
            actions.submitJob(
                    "CONTAMINANT_EXTRACT",
                    ClusterGateway.toInputParameters(new String[]{}));
        }catch (UnsupportedOperationException uo) {
            throw uo;
        } catch (Exception e) {
            e.printStackTrace();
            fail("failed to submit a local alignment analysis for CONTAMINANT_EXTRACT configuration");
        }
    }
}