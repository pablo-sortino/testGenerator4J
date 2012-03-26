package generator.code;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;

public class DocletTestGenerator {

	/**
	 * Specifies the output directory.
	 */
	protected static final String OPTION_OUTPUT_DIR = "-d";

	/**
	 * String variable which stores the output directory.
	 */
	private static String outputDIR = null;
	public static String classListFilename = "class_list.txt";

	private DocletTestGenerator() {
		
	}
	
	private static void createResources(final String rootDirectory) {
		try {
			//create the main directory
			File directorio = new File(rootDirectory);
			if (directorio.exists()) {
				directorio.delete();
			}
			directorio.mkdir();
			
			//create xml data file
			File file = new File(rootDirectory, "data.xml");
			BufferedWriter out = new BufferedWriter(new FileWriter(file, false));
			out.write("<?xml version =\"1.0\" encoding = \"UTF-8\"?>");
            out.newLine();
            out.write("<tests xmlns:xsi=\"http://www.w3.org/ 2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"/usr/bin/jtestcase-2.2.0/config/jtestcase.xsd\">");
			out.newLine();
			out.flush();
		    out.close();
		    
		    //create class to test list
		    file = new File(rootDirectory, classListFilename);
			out = new BufferedWriter(new FileWriter(file, false));
			out.write("# List of classes to test\n");
			out.newLine();
			out.flush();
		    out.close();
		    
	    } catch (IOException e) {
	    	e.printStackTrace();
    	}
	}
	
	/**
	 * Main method of the doclet. Parses the information given by Classdoc in
	 * the form of class and method signatures.
	 * 
	 * @param root
	 *            Contains parsed information from Classdoc/Javadoc.
	 * @return Returns true if the operations in the method are valid.
	 */
	public static boolean start(final RootDoc root) {
		
		ClassDoc[] classes = null;
		PackageDoc[] packageDocs = root.specifiedPackages();
		boolean alreadyCreatedClassTestSuite = false;
		PackageTestSuiteGenerator ptsGenerator = new PackageTestSuiteGenerator();
		ClassTestGenerator classGenerator = new ClassTestGenerator();

		outputDIR = readOptions(root.options());
		
		createResources(outputDIR);
		
		for (PackageDoc packageDoc : packageDocs) {
			System.out.println("\nGenerating TestSuites and TestCases for package: \"" + packageDoc.toString() + "\"");
			classes = packageDoc.ordinaryClasses();
			if ((classes.length > 0) && (!alreadyCreatedClassTestSuite)) {
				alreadyCreatedClassTestSuite = true;
				ptsGenerator.startClassTestSuite(outputDIR, packageDocs, "PackageTestSuite");
			}
			try {
				classGenerator.generate(outputDIR, classes);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		generateTestDataEnd(outputDIR);
		return true;
	}
	
	
	private static final void generateTestDataEnd(final String outputDIR) {
		try {
			File handle = new File(outputDIR, "data.xml");
			BufferedWriter out = new BufferedWriter(new FileWriter(handle, true));
			out.write("</tests>");
			out.newLine();
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Read the complete doclet options.
	 * 
	 * @param options
	 *            Read doclet options.
	 * 
	 * @return Output directory.
	 */
	private static String readOptions(final String[][] options) {
		Properties p = new Properties(System.getProperties());
		String outputDir = null;

		for (int i = 0; i < options.length; i++) {
			String[] opt = options[i];
			if (opt[0].equals("-d")) {
				outputDir = opt[1];
			}
		}
		if (outputDir == null) {
			outputDir = p.getProperty("user.dir");
		}

		//convert the path to a unix path (reeplace \ with /)
		outputDir = outputDir.replaceAll("\\\\", "/");
		
		return outputDir;
	}
}
