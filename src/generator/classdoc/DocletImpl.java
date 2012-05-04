package generator.classdoc;

import generator.SimpleLogger;
import java.lang.reflect.*;
import java.io.*;
import java.util.*;
import java.util.zip.*;

public class DocletImpl {


	/**
	 * Dummy object (something not null).
	 */
	static Object DUMMY = new Object();

	/**
	 * Test if a modifier matches the specified -public/-protected/... mode.
	 * 
	 * @return true if the corresponding member should be included in the
	 *         documentation.
	 */
	static boolean matchModifiers(int mod) {
		return Modifier.isPublic(mod) || Modifier.isProtected(mod);

	}

	/***************************************************************************
	 * 
	 * Tools for main program
	 * 
	 **************************************************************************/

	/**
	 * Add all classes in given archive file or directory to vector (as
	 * classnames, not as filenames).
	 */
	private static void findIndividualClasses(String jarOrDir, Vector classes)
			throws IOException {
		File f = new File(jarOrDir);
		if (f.isDirectory()) {
			findIndividualClassesInDirectory("", f, classes);
		} else {
			findIndividualClassesInJar(f, classes);
		}
	}

	/**
	 * Add all classes in a subdirectory of a corresponding parent package to
	 * vector.
	 */
	private static void findIndividualClassesInDirectory(String packge,
			File dir, Vector classes) throws IOException {
		String[] l = dir.list();
		for (int i = 0; i < l.length; i++) {
			registerIfClass(packge + l[i], classes);
			File f = new File(dir, l[i]);
			if (f.isDirectory()) {
				findIndividualClassesInDirectory(packge + l[i] + ".", f,
						classes);
			}
		}
	}

	/**
	 * Add all classes in an archive file (jar/zip) to vector.
	 */
	private static void findIndividualClassesInJar(File jar, Vector classes)
			throws IOException {
		ZipInputStream zip = new ZipInputStream(new FileInputStream(jar));
		ZipEntry entry = zip.getNextEntry();
		while (entry != null) {
			String n = entry.getName();
			registerIfClass(n, classes);
			entry = zip.getNextEntry();
		}
		zip.close();
	}

	/**
	 * Test if a filename represents a class, if yes, and add the corresponding
	 * classname to the vector.
	 */
	private static void registerIfClass(String n, Vector classes) {
		if (n.endsWith(".class")) {
			String path = n.substring(0, n.length() - 6);
			path = path.replace('/', '.');
			path = path.replace('\\', '.');
			SimpleLogger.log(path);
			classes.addElement(path);
		}
	}

	/***************************************************************************
	 * 
	 * Package tools
	 * 
	 **************************************************************************/

	public static RootDocImpl generateRootDoc(String docpath, String[][] options) {
		// find all individual classes from docpath
		Vector todo = new Vector();
		StringTokenizer st = new StringTokenizer(docpath, File.pathSeparator);
		while (st.hasMoreTokens()) {
			String path = st.nextToken();
			try {
				findIndividualClasses(path, todo);
			} catch (IOException e) {
				SimpleLogger.error("cannot get classes from " + path, e);
			}
		}
		String[] cl = new String[todo.size()];
		if (cl.length == 0) {
			SimpleLogger.error("nothing to do");
		}
		todo.copyInto(cl);

		// create RootDoc object
		RootDocImpl rootDoc = new RootDocImpl();
		rootDoc.init("classdoc documentation", cl, options);
		return rootDoc;
	}

	/**
	 * Copies the elements of all hashtable entries into the array. The array
	 * must have been initialized to the correct size before. The keys of the
	 * hashtable entries play no role.
	 */
	static void hashtable2array(Hashtable h, Object[] r) {
		int i = 0;
		for (Enumeration e = h.elements(); e.hasMoreElements();) {
			Object o = e.nextElement();
			r[i++] = o;
		}
	}

	/**
	 * Returns an array which contains all elements that are contained in
	 * <code>a</code> but not in <code>b</code>. In other words, the result is
	 * <code>a</code> without the elements that are both in <code>a</code> and
	 * <code>b</code>.
	 */
	static Object[] minusArray(Object[] a, Object[] b) { // null must not be any
															// element in the
															// arrays
		int drop = 0;
		Object[] temp = new Object[a.length]; // don't destroy original a
		System.arraycopy(a, 0, temp, 0, a.length);
		for (int i = 0; i < temp.length; i++) {
			if (isInArray(b, a[i])) {
				temp[i] = null;
				drop++;
			}
		}
		Object[] r = new Object[temp.length - drop];
		int j = 0;
		for (int i = 0; i < temp.length; i++) {
			if (temp[i] != null) {
				r[j++] = temp[i];
			}
		}
		return r;
	}

	/**
	 * Tests if the object is contained in the array.
	 */
	static boolean isInArray(Object[] a, Object o) {
		for (int i = 0; i < a.length; i++) {
			if (a[i] == o) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns a string that is s repeated r times.
	 */
	static String repeat(String s, int r) {
		StringBuffer b = new StringBuffer();
		for (int i = 0; i < r; i++) {
			b.append(s);
		}
		return b.toString();
	}

	/**
	 * Removes any heading package information from a qualified name.
	 */
	static String unqualify(String c) {
		int dotpos = c.lastIndexOf('.');
		if (dotpos != -1) {
			return c.substring(dotpos + 1);
		} else {
			return c;
		}
	}

	/**
	 * Returns the heading package information from a qualified name.
	 */
	static String packageName(String className) {
		int dotpos = className.lastIndexOf('.');
		if (dotpos != -1) {
			return className.substring(0, dotpos);
		} else {
			return "";
		}
	}

}
