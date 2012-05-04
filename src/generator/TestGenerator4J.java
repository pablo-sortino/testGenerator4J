package generator;

import generator.classdoc.RootDocImpl;
import generator.classdoc.DocletImpl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Vector;

import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.RootDoc;

public class TestGenerator4J {

	/**
	 * Print out help information on classdoc. Additionally, the Standard Doclet
	 * usage message is shown, or, if parameter -1.1 is set, the 1.1-emulation
	 * doclet's usage message.
	 */
	private static void usage() {
		System.out
				.println("\nReverse-engineer javadoc-style documentation from .jar/.class files _without_ source code.\n"
						+ "\n"
						+ "usage: classdoc [options]\n"
						+ "\n"
						+ "- docpath <jarsOrDirs>    Jars or directories to be documented seperated by ';'\n"
						+ "                          (note: these must also be included in the classpath)\n"
						+ "- doclet <doclet-class>   Doclet class (optional, default: jdk 1.3 standard)\n"
						+ "- verbose                 Show detailled messages.\n"
						+ "- help                    Print out help.\n");
		System.out
				.println("\n"
						+ "Notes:\n"
						+ "- JDK 1.3 (or higher) is required.\n"
						+ "- Make sure lib\\tools.jar is included in the classpath.\n"
						+ "- Jars or directories to be documented must both be in classpath _and_ docpath.\n"
						+ "- Refer to the Javadoc tool documentation for details on how to use doclets.\n");
		System.exit(0);
	}

	/**
	 * Test if a parameter variable is already set. If yes, output error and
	 * exit progam.
	 */
	private static boolean unique(String param) {
		if (param != null) {
			SimpleLogger.error("parameter " + param + " cannot be set twice");
			return false; // actually never reached
		} else {
			return true;
		}
	}

	/**
	 * Test if entry at index i is not the last in array args.
	 */
	private static boolean moreArgs(String[] args, int i) {
		if (!(i < args.length - 1)) {
			SimpleLogger.error("argument is missing.");
			return false; // actually never reached
		} else {
			return true;
		}
	}

	/***************************************************************************
	 * 
	 * Main program
	 * 
	 **************************************************************************/

	/**
	 * The main program.
	 * 
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {

		// parse command line parameters
		String docpath = null;
		String docletName = null;
		boolean windowtitleIsSet = false;
		Vector opt = new Vector();
		if (args.length == 0) {
			usage();
		}
		for (int i = 0; i < args.length; i++) {
			if ((args[i].equals("-docpath")) && (unique(docpath))
					&& (moreArgs(args, i))) {
				docpath = args[++i];
			} else if ((args[i].equals("-doclet")) && (unique(docletName))
					&& (moreArgs(args, i))) {
				docletName = args[++i];
			} else if (args[i].equals("-verbose")) {
				SimpleLogger.setVerbose(true);
			} else if (args[i].equals("-help") || args[i].equals("-?")
					|| args[i].equals("/?") || args[i].equals("--help")) {
				usage(); // will call System.exit(0)
			} else if (args[i].startsWith("-")) {
				// doclet parameters
				String var = args[i];
				if (var.equals("-windowtitle")) {
					windowtitleIsSet = true;
				}
				Vector v = new Vector();
				while (((i + 1) < args.length)
						&& (!args[i + 1].startsWith("-"))) {
					v.addElement(args[++i]);
				}
				String[] opts = new String[v.size() + 1];
				opts[0] = var;
				for (int j = 1; j < opts.length; j++) {
					opts[j] = (String) v.elementAt(j - 1);
				}
				opt.addElement(opts);
			}
		}

		if (docletName == null) {
			docletName = "com.sun.tools.doclets.standard.Standard";
			// if no windowtitle has been set, use the following as default
			if (!windowtitleIsSet) {
				String[] s = { "-windowtitle",
						"Reverse-engineered Documentation (untitled)" };
				opt.addElement(s);
			}
		}

		// create options-array to pass to doclet
		String[][] options = new String[opt.size()][];
		opt.copyInto(options);

		// load doclet class
		Class doclet;
		try {
			doclet = Class.forName(docletName);
		} catch (ClassNotFoundException cnfe) {
			SimpleLogger.error("cannot load doclet class " + docletName);
			return;
		}

		// validate own options
		if (docpath == null || docpath.equals("")) {
			SimpleLogger.error("no docpath set");
		}

		// let doclet validate its own options
		try {
			// dynamic version of "doclet.validOptions(options,self);"
			Class[] param = { options.getClass(), DocErrorReporter.class };
			Method m = doclet.getMethod("validOptions", param);
			Object[] arg = { options, new RootDocImpl()};
			Boolean b = (Boolean) m.invoke(null, arg);
			if (!b.booleanValue()) {
				// invalid options?
				System.exit(1);
				// error message has been output by doclet
			}
			// fallsthrough if options ok
		} catch (NoSuchMethodException e) {
			// 'gracefully' default to true
		} catch (Throwable ex) {
			// some 'real' error occurred
			if (ex instanceof InvocationTargetException) {
				ex = ((InvocationTargetException) ex).getTargetException();
			}
			SimpleLogger.error("cannot validate doclet options ("
					+ ex.getClass().getName() + ")", ex);
		}

		// get packages, classes and methos from the docpath
		RootDocImpl rootDoc = DocletImpl.generateRootDoc(docpath, options);

		// run doclet
		try {
			// dynamic version of "doclet.start(rootDoc)"
			Class[] param = { RootDoc.class };
			Method m = doclet.getMethod("start", param);
			Object[] arg = { rootDoc };
			m.invoke(null, arg);
		} catch (InvocationTargetException ite) {
			Throwable ee = ((InvocationTargetException) ite)
					.getTargetException();
			ee.printStackTrace(System.err);
			SimpleLogger.error("cannot run doclet");
		} catch (Exception e) {
			SimpleLogger.error(
					"class " + doclet + " appears not to be a Doclet - "
							+ e.getClass().getName(), e);
		}
		// cleanup all and exit
		System.exit(0);
	}

}
