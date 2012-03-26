#! /bin/bash
#------------------------------------------------------------------------------
#  TestGen4J 0.1.4-alpha
#  Script to execute the test cases generated
#  Licensed under the Open Source License version 2.1
#  (See http://www.opensource.org/licenses/osl-2.1.php)
#------------------------------------------------------------------------------

set +x
export JAVA_HOME=/usr/lib/jvm/java-6-sun
export TEST_DIR=$(echo ${1} | awk -F / '{split ($NF,a,".");print a[1]}').tests
export TESTGEN4J_HOME=/home/psortino/transito/TESIS/testGenerator4J
export JUNIT_HOME=$TESTGEN4J_HOME/lib/junit-4.5.jar

function execute() {

if [ ! -e "${TEST_DIR}/PackageTestSuite.class" ]; then
echo "***************************************************************************************"
echo "Don't try to fool TestGen4J, First generate the test code and then run the test case"
echo "***************************************************************************************"
exit 1
fi

cp $TEST_DIR/*Test.class ./
cp ${TEST_DIR}/PackageTestSuite.class ./
cp $TEST_DIR/data.xml ./

if [ -e "${TEST_DIR}/failedData.xml" ]; then
cp $TEST_DIR/failedData.xml ./
fi

echo "Running the Test Cases, please wait....."
$JAVA_HOME/bin/java -cp $TESTGEN4J_HOME/bin/:$TESTGEN4J_HOME/lib/commons-io-1.4.jar:$TESTGEN4J_HOME/lib/dbunit-2.4.5.jar:$TESTGEN4J_HOME/lib/hsqldb.jar:$TESTGEN4J_HOME/lib/jaxen-core.jar:$TESTGEN4J_HOME/lib/jaxen-jdom.jar:$TESTGEN4J_HOME/lib/jdom-1.0.jar:$TESTGEN4J_HOME/lib/log4j-1.2.14.jar:$TESTGEN4J_HOME/lib/saxpath.jar:$TESTGEN4J_HOME/lib/slf4j-api-1.5.8.jar:$TESTGEN4J_HOME/lib/slf4j-log4j12-1.5.8.jar:$JUNIT_HOME:$1:$TEST_DIR/.: PackageTestSuite


rm -f *Test.class PackageTestSuite.class data.xml failedData.xml

}

if [ $# -ne 1 ]; then
    echo "****************************************************"
    echo "Error: Incorrect Command Line Arguments"
    echo "You must submit correct command line Arguments"
    echo "Usage:"
    echo "sh build.sh /your path/yourfile.jar"
    echo "****************************************************"
    exit 1
fi

execute $*
