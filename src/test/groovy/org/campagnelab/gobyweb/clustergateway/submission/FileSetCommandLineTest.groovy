package org.campagnelab.gobyweb.clustergateway.submission

import org.campagnelab.gobyweb.clustergateway.registration.FileSetRegistration
import org.campagnelab.gobyweb.io.AreaFactory
import org.campagnelab.gobyweb.io.FileSetArea
import org.campagnelab.gobyweb.plugins.Plugins
import org.junit.BeforeClass
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.junit.*

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail

/**
 * @author manuele
 *
 * Date: 4/1/13
 * Time: 4:04 PM
 */

@RunWith(JUnit4.class)
public class FileSetCommandLineTest {

    static Plugins plugins;
    static FileSetArea storageArea;
    static String storageAreaDir = String.format("test-results/filesets");

    @BeforeClass
    public static void configure() {
        plugins = new Plugins();
        plugins.replaceDefaultSchemaConfig(".");
        plugins.addServerConf("test-data/root-for-rnaselect");
        plugins.setWebServerHostname("localhost");
        plugins.reload();
        //create the reference to the storage area
        try {

            storageArea = AreaFactory.createFileSetArea(
                    storageAreaDir, "junit");
        } catch (IOException ioe) {
            ioe.printStackTrace();
            fail("fail to create the local storage area");
        }
    }

    @Test
    public void register() {
        assertEquals(5, FileSetRegistration.process(buildFileRegistrationArgs(
                "GOBY_ALIGNMENTS: test-data/cluster-gateway/files-for-registration-test/fileSets/CASE_7/*.index "
                        + "test-data/cluster-gateway/files-for-registration-test/fileSets/CASE_7/*.entries "
                        + "test-data/cluster-gateway/files-for-registration-test/fileSets/CASE_7/*.header "
                        + "guess: "
                        + "test-data/cluster-gateway/files-for-registration-test/fileSets/CASE_7/*.compact-reads"
                )).size());

        assertEquals(5, FileSetRegistration.process(buildFileRegistrationArgs(
                "guess: test-data/cluster-gateway/files-for-registration-test/fileSets/CASE_7/*.index "
                        + "test-data/cluster-gateway/files-for-registration-test/fileSets/CASE_7/*.entries "
                        + "test-data/cluster-gateway/files-for-registration-test/fileSets/CASE_7/*.header "
                        + "guess: "
                        + "test-data/cluster-gateway/files-for-registration-test/fileSets/CASE_7/*.compact-reads"
        )).size());

        assertEquals(2, FileSetRegistration.process(buildFileRegistrationArgsNoSource(
                "COMPACT_READS: test-data/cluster-gateway/files-for-registration-test/fileSets/CASE_2/CASE2_FILE1.compact-reads" +
                " test-data/cluster-gateway/files-for-registration-test/fileSets/CASE_2/CASE2_FILE2.compact-reads")).size());

    }


    private static String[] buildFileRegistrationArgs(String filenames) {
        ("--fileset-area ${storageAreaDir} "+
                "--plugins-dir test-data/root-for-rnaselect " +
                "--owner junit "+
                "--action register " +
                filenames
        ).split(" ");

    }

    private static String[] buildFileRegistrationArgsNoSource(String filenames) {
        ("--fileset-area ${storageAreaDir} "+
                "--plugins-dir test-data/root-for-rnaselect " +
                "--owner junit "+
                "--action register " +
                filenames
        ).split(" ");

    }
}
