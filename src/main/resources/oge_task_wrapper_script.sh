#!/bin/sh

# Execute the script from the current directory
#$ -cwd

# Combine SGE error and output files.
#$ -j y

# Cluster queue to use
#$ -q %%QUEUE_NAME%%


. constants.sh

function setup_task_functions {
    # define no-op function to be overridden as needed by task script:
    plugin_task() { echo; }
    # include the plugin_task function for the appropriate task:
    . ${JOB_DIR}/script.sh

}

function run_task {
   plugin_task
}


case ${STATE} in
    task)
        setup_task_functions
        run_task
        ;;

    *)
        cd ${JOB_DIR}
        SUBMISSION=`qsub -N ${TAG}.submit -terse -v STATE=${INITIAL_STATE} oge_task_wrapper_script.sh`
        echo ${SUBMISSION}
        ;;
esac
