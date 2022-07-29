#!/bin/bash


#open a browser window and type "localhost:8090", of execute
#   firefox "localhost:8090"
#activate your venv. If you use docker and have already run the container (named wenv) once
#   docker start wenv


JAR_OUPUT_PATH=build/libs

#first generate the jar files
gradle -PmainClass=it.unibo.ctxrobot.MainCtxrobotCustomKt jar
gradle -PmainClass=it.unibo.ctxserver.MainCtxserverCustomKt jar

SAVEIFS=$IFS   # Save current IFS (Internal Field Separator)
IFS=$'\n'      # Change IFS to newline char
declare -a TEST_METHODS=(
    "testSprint1.TestSprint1_wasteservice.test_2_accepted"
    "testSprint1.TestSprint1_wasteservice.test_1_accepted_1_rejected"
)

IFS=$SAVEIFS   # Restore original IFS

TEST_RESULTS=()

i=0
numFail=0
for t in ${TEST_METHODS[@]} ; do
    echo "---------------------------- TEST " ${TEST_METHODS[$i]} " ------------------------------"
    gradle test --tests ${TEST_METHODS[$i]}
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
        printf "${GREEN}gradle test --tests ${TEST_METHODS[$i]}${NC}\n"
    else
        printf "${RED}gradle test --tests ${TEST_METHODS[$i]}${NC}\n"
    fi
    ((i++))
done

echo ""