package generator.code;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;

public class ClassTestGenerator {
	
	public final void generate(final String outputDIR, final ClassDoc[] classesDoc) throws IOException {
		for (ClassDoc classDoc : classesDoc) {
			String originalClassName = classDoc.name();
			if ((classDoc.isAbstract()) || (classDoc.isInterface())
					|| (!classDoc.isPublic())
					|| (classDoc.toString().indexOf("$") != -1)) {
				log("Exluding from generation class: " + originalClassName);
			} else {
				log("Generating test for class: " + originalClassName);
				String testClassName =  creatClassTestName(originalClassName);
				createMethodsListFile(outputDIR, testClassName);
				generateTestClass(outputDIR, originalClassName, testClassName, classDoc);
				generateTestClassData(outputDIR, testClassName);
				Set<String> methodNameSet = new HashSet<String>();
				for (MethodDoc methodDoc : classDoc.methods()) {
					String originalMethodName = methodDoc.name();
					String testMethodName = createMethodTestName(originalMethodName);
					int i = 1;
					while (methodNameSet.contains(testMethodName)) {
						testMethodName = createMethodTestName(originalMethodName, i);
						i++;
					}
					if (methodDoc.isPublic()) {
						methodNameSet.add(testMethodName);
						generateTestMethod(outputDIR, originalClassName, testClassName,
								originalMethodName, testMethodName, methodDoc);
						generateMethodTestData(outputDIR, originalClassName, testClassName, methodDoc);
					}
				}
				generateTestClassEnd(outputDIR, testClassName);
				writeClassesListFile(outputDIR, testClassName);
				generateTestClassDataEnd(outputDIR);
			}
		}
	}
	
	private final void generateTestClassData(final String outputDIR, final String testClassName) throws IOException {
		File handle = new File(outputDIR, "data.xml");
        BufferedWriter out = new
        BufferedWriter(new FileWriter(handle, true));
		try {
			out.write("\t<class name=\""+ testClassName +"\">");
			out.newLine();
			out.write("\t\t<!-- global params -->");
			out.newLine();
			out.write("\t\t<params></params>");
			out.newLine();
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private final void generateTestClassDataEnd(final String outputDIR) throws IOException {
		File handle = new File(outputDIR, "data.xml");
        BufferedWriter out = new
        BufferedWriter(new FileWriter(handle, true));
		try {
			out.write("\t</class>");
			out.newLine();
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
			
	private static void writeClassesListFile(final String outputDIR, final String className) {
		try {
			File f = new File(outputDIR, "class_list.txt");
			BufferedWriter out = new BufferedWriter(new FileWriter(f, true));
			out.write(className);
			out.newLine();
			out.flush();
		    out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void createMethodsListFile(final String outputDIR, final String className) {
		try {
			File file = new File(outputDIR, "method_list_"+className+".txt");
			BufferedWriter out = new BufferedWriter(new FileWriter(file, false));
			out.write("# List of methos to test for class "+className+"\n");
			out.newLine();
			out.flush();
		    out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void writeMethodsListFile(final String outputDIR, final String className, final String methodName) {
		try {
			File f = new File(outputDIR, "method_list_"+className+".txt");
			BufferedWriter out = new BufferedWriter(new FileWriter(f, true));
			out.write(methodName);
			out.newLine();
			out.flush();
		    out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private final void generateTestClassEnd(final String outputDIR, final String testClassName) throws IOException {
		File handle = new File(outputDIR, testClassName + ".java");
        BufferedWriter out = new BufferedWriter(new FileWriter(handle, true));
		try {
			out.write("}");
			out.newLine();
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private final void generateTestClass(final String outputDIR, final String originalClassName, final String testClassName, final ClassDoc classDoc) throws IOException {
		File handle = new File(outputDIR, testClassName + ".java");
		if (handle.exists()) {
			handle.delete();
		}
		
        BufferedWriter out = new
        BufferedWriter(new FileWriter(handle, true));
		try {
			out.write("/**\n");
			out.write(" *\n");
			out.write(" * @author Pablo Sortino <psortino@gmail.com>\n");
			out.write(" */\n");
			out.newLine();
			
			out.write("import java.util.Vector;\n");
			out.write("import junit.framework.TestSuite;\n");
			out.write("import junit.framework.Test;\n");
			out.write("import junit.framework.TestCase;\n");
			out.write("import java.io.File;\n");
			out.write("import java.io.IOException;\n");
			out.write("import org.jtestcase.JTestCase;\n");
			out.write("import org.jtestcase.JTestCaseException;\n");
			out.write("import org.jtestcase.TestCaseInstance;\n");
			out.write("import java.io.FileNotFoundException;\n");
			out.write("import java.util.HashMap;\n");
			out.write("import java.util.Iterator;\n");
			out.write("import java.util.ArrayList;\n");
            out.write("import java.io.FileReader;\n");
            out.write("import java.io.BufferedReader;\n");			
			out.newLine();
			out.write("import " + classDoc.qualifiedName() + ";\n");
			out.newLine();
			
			out.write("/**\n");
			out.write(" * Test cases for class " + originalClassName + "\n");
			out.write(" */\n");
			out.write("public class " + testClassName + " extends TestCase  {\n");
			out.newLine();
			
			out.write("\tprivate JTestCase jtestcase = null;\n");
			out.newLine();
			
			out.write("\t/**\n");
			out.write("\t * Main method to run the example from command line.\n");
			out.write("\t * @param args command line parameters\n");
			out.write("\t */\n");
			out.write("\tpublic static void main(String[] args) {\n");
			out.write("\t\tjunit.textui.TestRunner.run(suite());\n");
			out.write("\t}\n");
			out.newLine();
			
			out.write("\t/**\n");
			out.write("\t * Read the XML file with the test data and build the JTestCase instance.\n");
			out.write("\t *\n");
			out.write("\t * @param name Test method name.\n");
			out.write("\t */\n");			
			out.write("\tpublic " + testClassName + "(final String name) {\n");
			out.write("\t\tsuper(name);\n");			
			out.write("\t\ttry {\n");
			if (outputDIR.endsWith("/") ) {
				out.write("\t\t\tString dataFile = \"" + outputDIR + "data.xml\";\n");
			} else {
				out.write("\t\t\tString dataFile = \"" + outputDIR + "/data.xml\";\n"); 
			}
			out.write("\t\t\tjtestcase = new JTestCase(dataFile, \"" + testClassName + "\");\n");
			out.write("\t\t} catch (Exception e) {\n");
			out.write("\t\t\te.printStackTrace();\n");
			out.write("\t\t}\n");
			out.write("\t}\n");
			out.newLine();
			
			out.write("\t/**\n");
			out.write("\t * Suite method that collects all test cases.\n");
			out.write("\t *\n");
			out.write("\t * @return The test suite\n");
			out.write("\t */\n");
			out.write("\tpublic static Test suite() {\n");
			out.write("\t\tString methodsListFilename = \"method_list_"+ testClassName +".txt\";\n");			
			out.write("\t\tArrayList<String> methodsList = readClassesFile(methodsListFilename);\n");            
			out.write("\t\tTestSuite suite = new TestSuite(\"All Packages\");\n");
            out.write("\t\tfor (Iterator<String> iterator = methodsList.iterator(); iterator.hasNext();) {\n");
            out.write("\t\t\tString methodName = (String) iterator.next();\n");
            out.write("\t\t\tSystem.out.println(\"Adding method \" + methodName);\n");
            out.write("\t\t\tsuite.addTest(new " + testClassName + "(methodName));\n");
            out.write("\t\t}\n");
            out.write("\t\treturn suite;\n");
            out.write("\t}\n");
            out.newLine();
            
            out.write("\tprivate static ArrayList<String> readClassesFile(String filename) {\n");
			out.write("\t\tArrayList<String> list = new ArrayList<String>();\n");
			out.write("\t\tBufferedReader bufferedReader = null;\n");
			out.write("\t\ttry {\n");
			out.write("\t\t\t// Construct the BufferedReader object\n");
			out.write("\t\t\tbufferedReader = new BufferedReader(new FileReader(new File(\""+outputDIR+"\", filename)));\n");
			out.write("\t\t\tString line = null;\n");
			out.write("\t\t\twhile ((line = bufferedReader.readLine()) != null) {\n");
			out.write("\t\t\t\tif (line != null && (line.length() > 0) && !line.contains(\"#\") ) {\n");
			out.write("\t\t\t\t\tlist.add(line.trim());\n");
			out.write("\t\t\t\t}\n");
			out.write("\t\t\t}\n");
			out.write("\t\t} catch (FileNotFoundException ex) {\n");
			out.write("\t\t\tex.printStackTrace();\n");
			out.write("\t\t} catch (IOException ex) {\n");
			out.write("\t\t\tex.printStackTrace();\n");
			out.write("\t\t} finally {\n");
			out.write("\t\t\t// Close the BufferedReader\n");
			out.write("\t\t\ttry {\n");
			out.write("\t\t\t\tif (bufferedReader != null) {\n");
			out.write("\t\t\t\t\tbufferedReader.close();\n");
			out.write("\t\t\t\t}\n");
			out.write("\t\t\t} catch (IOException ex) {\n");
			out.write("\t\t\t\tex.printStackTrace();\n");
			out.write("\t\t\t}\n");
			out.write("\t\t}\n");
			out.write("\t\treturn list;\n");
            out.write("\t}");
            out.newLine();
            out.newLine();
            
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private final void generateTestMethod(final String outputDIR, final String originalClassName, final String testClassName,
			final String originalMethodName, final String testMethodName, final MethodDoc methodDoc) throws IOException {
		
		writeMethodsListFile(outputDIR, creatClassTestName(originalClassName), testMethodName);
		
		File handle = new File(outputDIR, testClassName + ".java");
        BufferedWriter out = new
        BufferedWriter(new FileWriter(handle, true));
        out.write("\t/**"); out.newLine();
        out.write("\t * Tests for the method " + originalMethodName + ".\n");
        out.write("\t *\n");
        out.write("\t * @throws IOException\n");
        out.write("\t */\n");
        out.write("\t@org.junit.Test\n");
        out.write("\tpublic final void " + testMethodName + "() throws IOException, JTestCaseException {\n");
        out.write("\t\tfinal String METHOD = \"" + testMethodName + "\";\n");
        out.write("\t\tif (jtestcase == null) {\n");
        out.write("\t\t\tfail(\"couldn't read xml definition file\");\n");
        out.write("\t\t}\n");
        out.write("\t\tVector<TestCaseInstance> testCases = null;\n");
        out.write("\t\ttry {\n");
        out.write("\t\t\ttestCases = jtestcase.getTestCasesInstancesInMethod(METHOD);\n");
        out.write("\t\t} catch (JTestCaseException e) {\n");
        out.write("\t\t\te.printStackTrace();\n");
        out.write("\t\t\tfail(\"error parsing xml file for calculate method \" + METHOD);\n");
        out.write("\t\t}\n");
        
        out.write("\t\tArrayList<JTestCaseException> errorList = new ArrayList<JTestCaseException>();\n");
        out.write("\t\tString assertName = null;\n");
        
        out.write("\t\t// For each test case\n");
        out.write("\t\tfor (int i = 0; i < testCases.size(); i++) {\n");
        out.write("\t\t\t// Retrieve name of test case\n");
        out.write("\t\t\tTestCaseInstance testCase = (TestCaseInstance) testCases.elementAt(i);\n");
        out.write("\t\t\ttry {\n");
        out.write("\t\t\t\tSystem.out.println(\"[TestCaseMethod=\" + testCase.getMethod()\n");
        out.write("\t\t\t\t\t\t+ \", TestCaseName=\" + testCase.getTestCaseName() + \"]\");\n");
        out.newLine();

        //-------------------------- PARAMETERS
        out.write("\t\t\t\t// Get hashed params for this test case\n");
        out.write("\t\t\t\tHashMap<String,Object> params = testCase.getTestCaseParams();\n");

        String variable = "var";
        String argumentType = null;        
        int cont = 0;
        for (Parameter parameter : methodDoc.parameters()) {
        	variable = variable + (cont + 1);
        	//out.write("\t\t\tSystem.out.println(\""+variable+"=\" + params.get(\""+variable+"\").getClass().getName());\n");
        	
        	argumentType = parameter.typeName();
        	
            out.write("\t\t\t");
            if (argumentType.equals("int")) {
            	out.write(argumentType + " " + variable + " = ((Integer) params.get(\"" + variable + "\")).intValue();\n");
            } else if (argumentType.equals("double")) {
                out.write(argumentType + " " + variable + " = ((Double) params.get(\"" + variable + "\")).doubleValue();\n");
            } else if (argumentType.equals("float")) {
                out.write(argumentType + " " + variable + " = ((Float) params.get(\"" + variable + "\")).floatValue();\n");
                out.newLine(); out.newLine();
            } else if (argumentType.equals("long")) {
                out.write(argumentType + " " + variable + " = ((Long) params.get(\"" + variable + "\")).longValue();\n");
            } else if (argumentType.equals("short")) {
                out.write(argumentType + " " + variable + " = ((Short) params.get(\"" + variable + "\")).shortValue();\n");
            } else if (argumentType.equals("char")) {
                out.write("String temp = (String)params.get(\"" + variable + "\");\n");
                out.write("\t\t\t" + argumentType + " " + variable + ";");
                out.write("\t\t\tif ((temp.equals(\"NULL\")) || (temp.equals(\"null\"))) {\n");
                out.write("\t\t\t\t" + variable + " = \'\0\';\n");
                out.write("\t\t\t} else if (temp.equals(\"SPACE\")) {\n");
                out.write("\t\t\t\t" + variable + " = \' \';\n");
                out.write("\t\t\t} else {\n");
                out.write("\t\t\t\t" + variable + " = temp.trim().charAt(0);\n");
                out.write("\t\t\t}\n");
            } else if (argumentType.equals("String") || argumentType.equals("java.lang.String")) {
                out.write(argumentType + " " + variable + " = (String) params.get(\"" + variable + "\");\n");
                out.write("\t\t\tif ((" + variable + ".equals(\"NULL\")) || (" + variable + ".equals(\"null\"))) {\n");
                out.write("\t\t\t\t" + variable + " = null;\n");
                out.write("\t\t\t} else if (" + variable + ".equals(\"EMPTY\")) {\n");
                out.write("\t\t\t\t" + variable + " = \"\";\n");
                out.write("\t\t\t} else if (" + variable + ".equals(\"SPACE\")) {\n");
                out.write("\t\t\t\t" + variable + " = \" \";\n");
                out.write("\t\t\t}\n");
            } else if (argumentType.equals("boolean")) {
                out.write("java.lang.Boolean tempbool" + (cont + 1) + "= (java.lang.Boolean) params." + "get(\"" + variable + "\");\n");
                out.write("\t\t\tboolean " + variable + " = tempbool"  + (cont + 1) + ".booleanValue();\n");
            } else {
            	out.write(argumentType + " " + variable + " = (" + argumentType + ") params.get(\"" + variable + "\");\n");
            }
            variable = "var";
        	cont++;
		}
        out.newLine();
        
        //-------------------------- INSTANCE OBJECT TO TEST
        out.write("\t\t\t\t// Instance the object\n");
        
        
        boolean isStatic = methodDoc.isStatic(); 
        if (!isStatic) {
            out.write("\t\t\t\t" + originalClassName + " tempObject = null;\n");
        }

        int numOfParams = methodDoc.parameters().length;
        String methodReturnType = methodDoc.returnType().qualifiedTypeName()
        	+ methodDoc.returnType().dimension();
        
        String invokeMethod = null;
        
        if (!methodReturnType.equals("void")) {
            if (!isStatic) {
                if (methodReturnType.contains("$")) {
                    StringTokenizer t = new StringTokenizer(methodReturnType, "$");
                    Object mainClass = t.nextToken();
                    Object innerClass = t.nextToken();
                    out.write("\t\t\t\t" + mainClass.toString() + " tt = new " + mainClass.toString() + "();\n");
                    out.write("\t\t\t\t" + mainClass.toString() + "." + innerClass.toString() + " result = tt.new " + innerClass.toString() + "();\n");
                    invokeMethod = "result = tempObject." + originalMethodName + "(";
                } else  {
                	//instancio el objeto
                	out.write("\t\t\t\ttempObject = new " + originalClassName.toString() + "();\n");
                    invokeMethod = methodReturnType + " result = " + "tempObject." + originalMethodName + "(";
                }
            } else if (isStatic) {
                 if (methodReturnType.indexOf('$') != -1) {
                    StringTokenizer t = new StringTokenizer(methodReturnType, "$");
                    Object mainClass = t.nextToken();
                    Object innerClass = t.nextToken();
                    out.write("\t\t\t\t" + mainClass.toString() + " tt = new " + mainClass.toString() + "();\n");
                    out.write("\t\t\t\t" + mainClass.toString() + "." + innerClass.toString() + " result = tt.new " + innerClass.toString() + "();\n");
                    invokeMethod = "result = " + originalClassName + "." + originalMethodName + "(";
                } else if (methodReturnType.indexOf('$') == -1) {
                	//instancio el objeto
                	//out.write("\t\t\ttempObject = new " + originalClassName.toString() + "();\n");
                    invokeMethod = methodReturnType + " result = " + originalClassName + "." + originalMethodName + "(";
               }
            }
        } else if (methodReturnType.equals("void")) {
            if (!isStatic) {
            	//instancio el objeto
            	out.write("\t\t\t\ttempObject = new " + originalClassName.toString() + "();\n");
                invokeMethod = "tempObject." + originalMethodName + "(";
            } else if (isStatic) {
                invokeMethod = originalClassName + "." + originalMethodName + "(";
            }
        }

        variable = "var";

        if (numOfParams > 0) {
            for (int j = 0; j < numOfParams; j++) {
                if (j < (numOfParams - 1)) {
                    invokeMethod = invokeMethod + variable + (j + 1) + ", ";
                } else if (j == (numOfParams - 1)) {
                    invokeMethod = invokeMethod + variable + (j + 1) + ");";
                }
            }
        } else if (numOfParams == 0) {
            invokeMethod = invokeMethod + ");";
        }

        if (invokeMethod != null) {
        	out.write("\t\t\t\t" + invokeMethod);
        	out.newLine();
        }
        out.newLine();
        
        //-------------------------- ASSERTS
        out.write("\t\t\t\t// asserting result:\n");
        if (!methodReturnType.equals("void")) {
        	out.write("\t\t\t\tif (testCase.getTestCaseAssertValues() != null && !testCase.getTestCaseAssertValues().keySet().isEmpty()) {\n");
        	out.write("\t\t\t\t\tIterator<String[]> iterator = testCase.getTestCaseAssertValues().keySet().iterator();\n");
        	out.write("\t\t\t\t\twhile (iterator.hasNext()) {\n");
        	out.write("\t\t\t\t\t\tString[] assertKey = iterator.next();\n");
        	out.write("\t\t\t\t\t\tassertName = assertKey[0];\n");
        	out.write("\t\t\t\t\t\tboolean succeed = testCase." + "assertTestVariable(assertKey[0], ");
            if (methodReturnType.equals("int")) {
            	out.write("new Integer(result));\n");
            } else if (methodReturnType.equals("double")) {
            	out.write("new Double(result));\n");
            } else if (methodReturnType.equals("short")) {
            	out.write("new Short(result));\n");
            } else if (methodReturnType.equals("float")) {
            	out.write("new Float(result));\n");
            } else if (methodReturnType.equals("long")) {
            	out.write("new Long(result));\n");
            } else if (methodReturnType.equals("boolean")) {
            	out.write("new Boolean(result));\n");
            } else if (methodReturnType.equals("char")) {
            	out.write("new Character(result));\n");
            } else {
            	out.write("result);\n");
            }
            out.write("\t\t\t\t\t\tassertTrue(succeed);\n");
            out.write("\t\t\t\t\t}\n");
            out.write("\t\t\t\t}\n");
        }
        //cierro el catch por las excepciones que puedan surgir o tirar los metodos bajo test
        out.write("\t\t\t} catch (Throwable t) {\n");
        out.write("\t\t\t\tString msj = \"Error executing test case [\"\n"
        		+ "\t\t\t\t\t+ \"TestCaseClass=\" + this.getClass().getName()\n"
        		+ "\t\t\t\t\t+ \", TestCaseMethod=\" + testCase.getMethod()\n"
        		+ "\t\t\t\t\t+ \", TestCaseName=\" + testCase.getTestCaseName()+ \", assert=\" + assertName+ \"]\";\n");
        out.write("\t\t\t\terrorList.add(new JTestCaseException(msj, t));\n");
        out.write("\t\t\t}\n");
        out.write("\t\t}\n");
        out.write("\t\tif (!errorList.isEmpty()) {\n");
        out.write("\t\t\tthrow errorList.get(0);\n");
        out.write("\t\t}\n");
        out.write("\t}\n");
        out.newLine();
        out.flush();
	}

    private boolean isPrimitiveType (String typeName) {
    	if (typeName == null) {
    		return false;    		
    	}
    	if (typeName.equals("int") || typeName.equals("short")
    			|| typeName.equals("float") || typeName.equals("long")
    			|| typeName.equals("double") || typeName.equals("boolean")
    			|| typeName.equals("char") || typeName.equals("byte")) {
        	return true;
        }
    	return false;
    }
    
    public String createMethodTestName(final String originalMethodName, final int number){
    	return createMethodTestName(originalMethodName + number);
    }
    
    public String createMethodTestName(final String originalMethodName){
    	String testMethodName = "test" 
    		+ (""+originalMethodName.charAt(0)).trim().toUpperCase()
    		+ originalMethodName.substring(1);
    	return testMethodName;
    }
    
    public String creatClassTestName(final String originalClassName){
    	return originalClassName + "Test";
    }
	
    /**
     * The method writes the test cases in the data file for the
     * orginal methods having more than one argument.
     *
     * @param out
     *                          BufferedWriter Object.
     * @param paramConditions
     *                          Boundary condition of arguments.
     * @param thisCombination
     *                          Current combination of the values
     *                          of the arguments set in the rules.xml
     *                          file.
     * @param name
     *                          Test method name.
     * @param methodReturnType
     *                          Return type of the method.
     * @param numOfParams
     *                          No. of arguments.
     * @throws IOException
     *                          Throws IOException.
     */
    public final void generateMethodTestData(final String outputDIR, final String originalClassName, final String testClassName,
			final MethodDoc methodDoc) throws IOException {

    	String originalMethodName = methodDoc.name();
		String testMethodName = createMethodTestName(originalMethodName);
		
		String methodReturnType = methodDoc.returnType().qualifiedTypeName()
    		+ methodDoc.returnType().dimension();
		
		File handle = new File(outputDIR, "data.xml");
		BufferedWriter out = new BufferedWriter(new FileWriter(handle, true));
		out.write("\t\t<!-- test methods -->\n");
		
		
        String varNumber = null;
        out.write("\t\t<method name=\"" + testMethodName + "\" test-case=\"" + 1 + "\">\n");
        out.write("\t\t\t<params>\n");
        
        int cont = 0;
        for (Parameter parameter : methodDoc.parameters()) {
        	varNumber = "var" + (cont + 1);
        	
        	String parameterType = parameter.type().qualifiedTypeName()
    			+ parameter.type().dimension();
        	
        	
        	if (isPrimitiveType(parameterType)){
        		out.write("\t\t\t\t<param name=\"" + varNumber + "\" type=\"" + parameterType + "\">" + 1000 + "</param>\n");
        	} else {
        		out.write("\t\t\t\t<param name=\"" + varNumber + "\" type=\"\" use-jice=\"yes\">\n");
        		out.write(generateJiceElement("java_object_" + varNumber, parameterType));
        		out.write("\t\t\t\t</param>\n");
        	}
        	cont++;
		}
        
        out.write("\t\t\t</params>\n");
        
        out.write("\t\t\t<asserts>\n");

        if (isPrimitiveType(methodReturnType)){
        	out.write("\t\t\t\t\t<assert name=" + "\"testResult1\" type=\"" + methodReturnType + "\"");
            if (methodReturnType.equals("int")) {
                out.write(" action=\"NOTLT\">-2147483648</assert>\n");
            } else if (methodReturnType.equals("short")) {
                out.write(" action=\"NOTLT\">-32768</assert>\n");
            } else if (methodReturnType.equals("float")) {
            	out.write(" action=\"NOTLT\">-32768</assert>\n");
            } else if (methodReturnType.equals("long")) {
            	out.write(" action=\"NOTLT\">-32768</assert>\n");
            } else if (methodReturnType.equals("double")) {
            	out.write(" action=\"NOTLT\">-32768</assert>\n");
            } else if (methodReturnType.equals("String")) {
            	out.write(" action=\"ISNOTNULL\"></assert>\n");
            } else if (methodReturnType.equals("char")) {
            	out.write(" action=\"NOTEQUALS\">'\0'</assert>\n");
            } else if (methodReturnType.equals("void")) {
            	out.write(" action=\"EQUALS\">null</assert>\n");
            } else {
            	out.write(" action=\"NOTEQUALS\">null</assert>\n");
            }
            
        } else {
        	out.write("\t\t\t\t<assert name=\"testResult1\" type=\"\" use-jice=\"yes\" action=\"EQUALS\">\n");
        	out.write(generateJiceElement("assert_object", methodReturnType));
        	out.write("\t\t\t\t</assert>\n");
        }
        
        out.write("\t\t\t</asserts>\n");
        out.write("\t\t\t<exception>java.lang.NullPointerException</exception>\n");
        out.write("\t\t</method>\n");
        out.flush();
    }

	private String generateJiceElement(String varName, String parameterType) {
		StringBuilder b = new StringBuilder();
		b.append("\t\t\t\t\t<jice>\n");
		b.append("\t\t\t\t\t\t");
		b.append("<");
		b.append(varName);
		b.append(" xmlns=\"http://www.jicengine.org/jic/2.1\" class=\"");
		b.append(parameterType);
		b.append("\"");
		if (parameterType.endsWith("[]")) {
			b.append(" type=\"array\"");
		}
		b.append(">\n");
		b.append("\t\t\t\t\t\t");
		b.append("</");
		b.append(varName);
		b.append(">\n");
		b.append("\t\t\t\t\t</jice>\n");
		return b.toString();
	}
	
	public static void log(String msg) {
		System.out.println(msg);
	}
}
