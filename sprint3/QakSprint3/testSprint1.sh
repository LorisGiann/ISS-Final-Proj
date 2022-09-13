#!/bin/bash


#open a browser window and type "localhost:8090", of execute
#   firefox "localhost:8090"
#activate your venv. If you use docker and have already run the container (named wenv) once
#   docker start wenv


JAR_OUPUT_PATH=build/libs

#first generate the jar files (but if an argument is present compilation is skipped)
if [ -z "$1" ] ; then
    gradle -PmainClass=it.unibo.ctxrobot.MainCtxrobotKt jar
    gradle -PmainClass=it.unibo.ctxserver.MainCtxserverKt jar
    gradle -PmainClass=it.unibo.ctxalarm.MainCtxalarmKt jar
else
  echo "skipping compilation"
fi

SAVEIFS=$IFS   # Save current IFS (Internal Field Separator)
IFS=$'\n'      # Change IFS to newline char
declare -a TEST_METHODS=(
    #"testSprint1.TestSprint1_mover.test_from_H_to_H"
    "testSprint1.TestSprint1_mover.test_from_H_to_I"
    "testSprint1.TestSprint1_mover.test_from_PB_to_I"
    "testSprint1.TestSprint1_mover.test_new_pos_no_chenage_route_from_GB_to_H_then_I"
    "testSprint1.TestSprint1_mover.test_new_pos_chenage_route_from_PB_to_H_then_I"

    "testSprint1.TestSprint1_wasteservice.test_2_accepted"
    "testSprint1.TestSprint1_wasteservice.test_1_accepted_1_rejected"

    "testSprint1.TestSprint1_integration_test.test_accepted"
    "testSprint1.TestSprint1_integration_test.test_rejected"
    "testSprint1.TestSprint1_integration_test.test_2_accepted_while_in_operation"
    "testSprint1.TestSprint1_integration_test.test_1_accepted_1_rejected_while_in_operation"
    "testSprint1.TestSprint1_integration_test.test_2_accepted_while_returning_home"
    "testSprint1.TestSprint1_integration_test.test_1_accepted_1_rejected_while_returning_home"
)

IFS=$SAVEIFS   # Restore original IFS

TEST_RESULTS=()

i=0
numFail=0
for t in ${TEST_METHODS[@]} ; do
    echo "---------------------------- TEST " ${TEST_METHODS[$i]} " ------------------------------"
    gradle test --tests ${TEST_METHODS[$i]} -i
    TEST_RESULTS[$i]=$?
    if [[ ${TEST_RESULTS[$i]} -ne 0 ]] ; then
        ((numFail++))
    fi
    ((i++))
    echo "---------------------------------------------------------------------------------------"
done

RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m' # No Color

if [[ $numFail -ne 0 ]] ; then
    printf "${RED}$numFail out of $i TESTS FAILED!${NC}\n"
else
    printf "${GREEN}All TESTS SUCCESSFUL!${NC}\n"
fi

echo ""
i=0
for t in ${TEST_METHODS[@]} ; do
    if [[ ${TEST_RESULTS[$i]} -eq 0 ]] ; then
        printf "${GREEN}gradle test --tests ${TEST_METHODS[$i]} -i ${NC}\n"
    else
        printf "${RED}gradle test --tests ${TEST_METHODS[$i]} -i ${NC}\n"
    fi
    ((i++))
done

echo ""