SET JAVA_HOME="C:\Archivos de programa\Java\jdk1.5.0_02"
SET TESTGENERATOR4J_HOME=D:\psortino\devel\javaProjects\testGenerator4J
SET TESTGENERATOR4J_CP=%TESTGENERATOR4J_HOME%\lib\classdoc.jar;%TESTGENERATOR4J_HOME%\lib\commons-io-1.4.jar;%TESTGENERATOR4J_HOME%\lib\jaxen-core.jar;%TESTGENERATOR4J_HOME%\lib\jaxen-jdom.jar;%TESTGENERATOR4J_HOME%\lib\jdom-1.0.jar;%TESTGENERATOR4J_HOME%\lib\junit-4.5.jar;%TESTGENERATOR4J_HOME%\lib\log4j-1.2.14.jar;%TESTGENERATOR4J_HOME%\lib\saxpath.jar;%TESTGENERATOR4J_HOME%\lib\slf4j-api-1.5.8.jar;%TESTGENERATOR4J_HOME%\lib\slf4j-log4j12-1.5.8.jar;%TESTGENERATOR4J_HOME%\lib\tools-1.5.0.jar;%TESTGENERATOR4J_HOME%\lib\tools.jar;%TESTGENERATOR4J_HOME%\lib\xerces.jar;%TESTGENERATOR4J_HOME%\lib\xmlutil.jar;%TESTGENERATOR4J_HOME%\lib\xtype.jar;%TESTGENERATOR4J_HOME%\bin\
SET TEST_DIR=%TESTGENERATOR4J_HOME%\%1.tests

echo "Running the Test Cases, please wait....."
%JAVA_HOME%\bin\java -cp %TESTGENERATOR4J_CP%;%CLASSPATH%;%1;%TEST_DIR%;. PackageTestSuite

echo DONE.
