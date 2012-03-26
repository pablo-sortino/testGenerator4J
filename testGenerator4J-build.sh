#! /bin/bash

#-------------------------------------------------------------------------------
#  TestGen4J 0.1.4-alpha
#  Script to generate and compile test code
#  Licensed under the Open Source License version 2.1
#  (See http://www.opensource.org/licenses/osl-2.1.php)
#-------------------------------------------------------------------------------

set +x
export JAVA_HOME=/usr/lib/jvm/java-6-sun
export TEST_DIR=$(echo ${1} | awk -F / '{split ($NF,a,".");print a[1]}').tests
export TESTGEN4J_HOME=/home/psortino/transito/TESIS/testGenerator4J
export JUNIT_HOME=$TESTGEN4J_HOME/lib/junit-4.5.jar


function generate() {

if [ ! -e ${TEST_DIR} ]; then
 mkdir $TEST_DIR
else
 echo "Over-write the earlier test code?" Type "yes" or "no"; read ans
  if [ ! ${ans} = "yes" ]; then
    exit 1
  else
    echo "Earlier Code will be Overwritten..."
  fi  
fi

echo "Generating the Test Code and the Test Data......"
$JAVA_HOME/bin/java -cp $1:bin/:$PATH:$CLASSPATH:lib/tools.jar:lib/classdoc.jar:lib/xerces.jar:. classdoc -doclet generator.code.DocletTestGenerator -d $TEST_DIR/ -docpath $1 > $TEST_DIR/TestGenerated.log
}

function compile() {
echo "Compiling the Test Code....."
$JAVA_HOME/bin/javac -cp $1:$JUNIT_HOME:bin/:$PATH:$CLASSPATH:lib/tools.jar:lib/classdoc.jar:lib/xerces.jar: $TEST_DIR/*.java
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

if [ -z "${JAVA_HOME}" ]; then
    echo "****************************************************"
    echo "JAVA_HOME variable is not set"
    echo "Usage:"
    echo "export JAVA_HOME=/your path/j2sdk"
    echo "****************************************************"
    exit 1
fi

if [ -z "${JUNIT_HOME}" ]; then
    echo "****************************************************"
    echo "JUNIT_HOME variable is not set"
    echo "Usage:"
    echo "export JUNIT_HOME=/your path/junit"
    echo "****************************************************"
    exit 1
fi

generate $*
compile $*
