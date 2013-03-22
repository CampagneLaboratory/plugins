package org.campagnelab.gobyweb.clustergateway.submission;

import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.campagnelab.gobyweb.clustergateway.runtime.JobArea;
import org.campagnelab.gobyweb.io.AreaFactory;
import org.campagnelab.gobyweb.io.FileSetArea;
import org.campagnelab.gobyweb.plugins.Plugins;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.swing.plaf.FileChooserUI;
import java.io.File;
import java.io.IOException;

import static junit.framework.Assert.fail;
import static junit.framework.Assert.failNotEquals;

/**
 * Test local task executions
 *
 * @author manuele
 */
@RunWith(JUnit4.class)
public class TaskLocalSubmission {

    static Plugins plugins;
    static JobArea jobArea;
    static FileSetArea storageArea;
    static Actions actions;
    static final String sourceStorageAreaDir = "test-data/cluster-gateway/fileset-area-for-submission-test";
    static final String rootAreaDir = String.format("test-results-%d", System.currentTimeMillis());
    static final String storageAreaDir = String.format("%s/filesets", rootAreaDir);
    static final String jobAreaDir = String.format("%s/jobs", rootAreaDir);
    static final String owner = "ClusterGateway";


    @BeforeClass
    public static void configure() {
        plugins = new Plugins();
        plugins.replaceDefaultSchemaConfig(".");
        plugins.addServerConf("test-data/root-for-rnaselect");
        plugins.setWebServerHostname("localhost");
        plugins.reload();

        //prepare the storage area for testing
        try {
            //need to clone the storage area because results will be stored there too
            FileUtils.copyDirectory(new File(sourceStorageAreaDir).getAbsoluteFile(),
                    new File(storageAreaDir).getParentFile().getAbsoluteFile() );
            storageArea = AreaFactory.createFileSetArea(
                    storageAreaDir, "ClusterGateway",
                    AreaFactory.MODE.LOCAL);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            fail("failed to create the local storage area");
        }

        //create the reference to the job area
        try {
            jobArea = new JobArea(jobAreaDir, owner);
        } catch (IOException ioe) {
            fail("failed to create the local job area");
        }
    }

    @Test
    public void submit() {
        try {
            Submitter submitter  = new LocalSubmitter(plugins.getRegistry());
            actions = new Actions(submitter, storageArea, jobArea, plugins.getRegistry());
            actions.submitTask(
                   "RNASELECT_TASK",
                   new String[]{"TESTTAG1","TESTTAG2","TESTTAG3"});

        } catch (Exception e) {
            e.printStackTrace();
            fail("failed to submit a local task for RNASELECT_TASK configuration");
        }
    }

    @AfterClass
    public static void clean(){
        try {
            Files.deleteRecursively(new File(rootAreaDir));
        } catch (IOException e) {
            e.printStackTrace();
            fail("failed to delete the test area");
        }
    }
}