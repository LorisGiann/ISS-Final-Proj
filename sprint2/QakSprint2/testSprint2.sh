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
    "testSprint2.TestSprint2_integration_halt.test_accepted"
    "testSprint2.TestSprint2_integration_led.test_accepted"

    "testSprint2.TestSprint2_halt_unit_test.test_halt_while_forward"
    "testSprint2.TestSprint2_halt_unit_test.test_halt_before_forward"
    "testSprint2.TestSprint2_halt_unit_test.test_halt_while_turn"
    "testSprint2.TestSprint2_halt_unit_test.test_halt_before_turn"
    "testSprint2.TestSprint2_halt_unit_test.test_halt_while_pickup"
    "testSprint2.TestSprint2_halt_unit_test.test_halt_before_pickup"
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