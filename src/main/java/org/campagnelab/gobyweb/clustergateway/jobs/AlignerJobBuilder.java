package org.campagnelab.gobyweb.clustergateway.jobs;

import org.campagnelab.gobyweb.filesets.protos.MetadataFileReader;
import org.campagnelab.gobyweb.filesets.protos.MetadataFileWriter;
import org.campagnelab.gobyweb.io.AreaFactory;
import org.campagnelab.gobyweb.io.FileSetArea;
import org.campagnelab.gobyweb.io.JobArea;
import org.campagnelab.gobyweb.plugins.xml.aligners.AlignerConfig;

import java.io.IOException;
import java.io.File;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Builder for aligner jobs
 *
 * @author manuele
 */
public class AlignerJobBuilder extends JobBuilder {

    private final AlignerConfig alignerConfig;
    private final FileSetArea fileSetArea;
    private int chunkSize;
    private int numParts;
    private String genomeID;
    private final String inputReadsTag;
    private final String[] attributesFromReadsMetadata = new String[]{
            "PAIRED_END_ALIGNMENT", "BISULFITE_SAMPLE", "COLOR_SPACE", "ORGANISM", "READS_PLATFORM",
            "PAIRED_END_DIRECTIONS", "LIB_PROTOCOL_PRESERVE_STRAND", "READS_LABEL", "BASENAME", "INPUT_READ_LENGTH"
    };

    /**
     * Creates an aligner job builder.
     * @param alignerConfig the source aligner configuration
     * @param jobArea the job area where the job will be submitted
     * @param filesetAreaReference the fileset area from which reads metadata are fetched
     * @param owner the owner of the job
     * @param inputSlots the input slots passed on the command line
     * @throws IOException
     */
    public AlignerJobBuilder(AlignerConfig alignerConfig, JobArea jobArea, String filesetAreaReference,
                             String owner, Set<InputSlotValue> inputSlots) throws IOException {
        super(alignerConfig);
        this.alignerConfig = alignerConfig;
        // create the fileset area according to the location of the job area
        if (jobArea.isLocal()) {
            //we can use the reference name as it is because we have the same visibility
            this.fileSetArea = AreaFactory.createFileSetArea(filesetAreaReference, owner);
        } else {
            if (filesetAreaReference.startsWith("/")) {
                //the fileset area is local to the job area
                String remoteReferenceName = String.format("%s@%s:%s", jobArea.getUserName(), jobArea.getHostName(), filesetAreaReference);
                this.fileSetArea = AreaFactory.createFileSetArea(remoteReferenceName, owner);
            } else {
                //the fileset area must be remote also for the job area
                this.fileSetArea = AreaFactory.createFileSetArea(filesetAreaReference, owner);
            }
        }
        //input slots are validated elsewhere, we do not need to do it here
        InputSlotValue inputReads = inputSlots.iterator().next();
        this.inputReadsTag = inputReads.getValues().get(0);
    }


    private void populateJobEnvironment(JobRuntimeEnvironment environment)
            throws IOException {

        //replacements from the aligner configuration
        environment.put("PLUGIN_ID", alignerConfig.getId());
        environment.put("SUPPORTS_GOBY_READS", alignerConfig.supportsGobyReads);
        environment.put("SUPPORTS_GOBY_ALIGNMENTS", alignerConfig.supportsGobyAlignments);
        environment.put("SUPPORTS_FASTQ_READS", alignerConfig.supportsFastqReads);
        environment.put("SUPPORTS_FASTA_READS", alignerConfig.supportsFastaReads);
        environment.put("SUPPORTS_BAM_ALIGNMENTS", alignerConfig.supportsBAMAlignments);
        environment.put("SUPPORTS_PAIRED_END_ALIGNMENTS", alignerConfig.supportsPairedEndAlignments);
        environment.put("SUPPORTS_BISULFITE_CONVERTED_READS", alignerConfig.supportsBisulfiteConvertedReads);
        environment.put("ALIGNER", alignerConfig.getId());
        // TODO transcript alignments have been removed with the introduction of the plugin system, check that the
        // TODO variable TRANSCRIPT_ALIGN_FEWER_JOBS is not used by plugins scripts, then remove this.
          /*
            * If "true", Transcript Alignment this will make NUMBER_OF_ALIGN_PARTS jobs
            *     and each of those will run NUMBER _OF_TRANSCRIPT_PARTS alignments.
            *     While each job runs longer, this is probably the better solution.
            * If "false", this will make NUM_READS * NUMBER_OF_TRANSCRIPT_PARTS jobs
            *     and each of those will run ONE alignment
            */
        environment.put("TRANSCRIPT_ALIGN_FEWER_JOBS", "true");
        //variables from the sample metadata
        File metadataFile = fileSetArea.getMetadataFile(this.inputReadsTag, MetadataFileWriter.PB_FILENAME);
        MetadataFileReader reader = new MetadataFileReader(metadataFile);
        Map<String, String> storedAttributes = reader.getAttributes();
        for (String attribute : attributesFromReadsMetadata) {
            if (storedAttributes.containsKey(attribute))
                environment.put(attribute, storedAttributes.get(attribute));
        }
        //variables from the command line options
        environment.put("CHUNK_SIZE", this.chunkSize);
        environment.put("NUMBER_OF_ALIGN_PARTS", this.numParts);
        environment.put("GENOME_REFERENCE_ID", this.genomeID);
        // Increase total number of parts for CONCAT and POST
        environment.put("NUMBER_OF_PARTS", this.numParts + 2);
        if (this.genomeID.startsWith("Transcript-"))
            environment.put("INITIAL_STATE", "pre_transcript_align");
        else
            environment.put("INITIAL_STATE", "pre_align");
    }

    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }


    public void setNumParts(int numParts) {
        this.numParts = numParts;
    }

    public void setGenomeID(String genomeID) {
        this.genomeID = genomeID;
    }

    /**
     * Adds aligner-specific settings to the job.
     * @param executableJob
     */
    @Override
    protected void customizeJob(ExecutableJob executableJob) throws IOException {
        this.populateJobEnvironment(executableJob.getEnvironment());
        // Last use 4, bwa use 2. Was 4, large concats probably take more memory so increased to 6
        // 2011-09-27 Was 6, but gsnap jobs have been partially or fully failing, upped to 8.

        // TODO These settings have not been used since the plugin system. Check that they are not used and remove.
        executableJob.setMemoryInGigs(8);
        if (executableJob.getEnvironment().containsKey("BISULFITE_SAMPLE"))  {
            executableJob.setMemoryOverheadInGigs(16);
            executableJob.setAsParallel();
        } else
            executableJob.setMemoryOverheadInGigs(2);
    }
}