package generator.classdoc;
/*
 * Project: classdoc
 * File:    classdoc.java
 *
 * Created on 1. April 2001, 09:55
 */

import com.sun.javadoc.*;

import java.lang.reflect.*;
import java.io.*;
import java.util.*;
import java.util.zip.*;

/**
 * classdoc - generate javadoc-style documentation from compiled java programs <i>without</i> source code.<br>
 *
 * Think of this as an alternative to Java's tool javadoc which generates
 * Java API documentation by parsing classes and source files to create
 * HTML (or other) files.<br>
 *
 * classdoc merely does the same, with the important difference that it does <i>not</i>
 * need source files. This way, you can create a complete overview on the classes
 * and their members (field, methods, ...) of any Java program.<br>
 *
 * classdoc implements the Java Doclet API so that any Doclet can be used to
 * generate output. By default, the jdk 1.3 Standard Java API doclet is used.<br>
 *
 * Make sure that you are running this on a jdk 1.3 (not just jre) and that
 * <jdk>/lib/tools.jar (containing the Doclet API classes and the Standard Doclet)
 * is in the classpath.<br>
 *
 * @author Jens Gulden - <a href="http://www.jensgulden.de/" target="_top">www.jensgulden.de</a>
 * @version 1.0
 */
public class classdoc implements DocErrorReporter
{
  /***************************************************************************
   *
   * Welcome / Usage
   *
   **************************************************************************/

  /**
   * Print out welcome and copyright message.
   */
  private static void hello()
  {
    System.out.println(
    "classdoc 1.0 - Copyright (C) Jens Gulden, mail@jensgulden.de\n"+
    "This software comes with NO WARRANTY: see file licence.txt for details."
    );
    
  }

  /**
   * Print out help information on classdoc.
   * Additionally, the Standard Doclet usage message is shown,
   * or, if parameter -1.1 is set, the 1.1-emulation doclet's usage message.
   */
  private static void usage()
  {
    System.out.println(
    "\nReverse-engineer javadoc-style documentation from .jar/.class files _without_ source code.\n"+
    "\n"+
    "usage: classdoc [options]\n"+
    "\n"+
    "- docpath <jarsOrDirs>    Jars or directories to be documented seperated by ';'\n"+
    "                          (note: these must also be included in the classpath)\n"+
    "- doclet <doclet-class>   Doclet class (optional, default: jdk 1.3 standard)\n"+
    "- 1.1                     Use javadoc 1.1 emulation.\n"+
    "- public                  Document public fields and members only.\n"+
    "- protected               Document public and protected fields and members (default).\n"+
    "- packageprivate          Document all fields and members except private ones.\n"+
    "- private                 Document all fields and members.\n"+
    "- nofakepublic            Do not pretend that any class is public, i.e. is to be documented completely.\n"+
    "- verbose                 Show detailled messages.\n"+
    "- help                    Print out help.\n"
    );
    // try to output help text for Standard Doclet
    try
    {
      Properties p=new Properties();
      String docprops;
      if (!oneone)
      {
        docprops="com/sun/tools/javadoc/resources/standard.properties";
      }
      else
      {
        docprops="com/sun/tools/javadoc/resources/oneone.properties";
      }
      p.load(self.getClass().getClassLoader().getResourceAsStream(docprops));
      String standardDocletUsage=p.getProperty("doclet.usage");
      if (standardDocletUsage!=null)
      {
        System.out.println(standardDocletUsage);
      }
      String standardDocletXUsage=p.getProperty("doclet.xusage");
      if (standardDocletXUsage!=null)
      {
        System.out.println(standardDocletXUsage);
      }
    }
    catch (Throwable t)
    {
      //nop
    }
    System.out.println(
    "\n"+
    "Notes:\n"+
    "- JDK 1.3 (or higher) is required.\n"+
    "- Make sure lib\\tools.jar is included in the classpath.\n"+
    "- Jars or directories to be documented must both be in classpath _and_ docpath.\n"+
    "- Refer to the Javadoc tool documentation for details on how to use doclets.\n"
    );
    System.exit(0);
  }

  /***************************************************************************
   *
   * Variables
   *
   **************************************************************************/

  /**
   * Constants for modifier mode.
   * @see #modifier
   */
  private final static int PUBLIC=3;
  private final static int PROTECTED=2;
  private final static int PACKAGEPRIVATE=1;
  private final static int PRIVATE=0;

  /**
   * Single instance of this class to implement interface DocErrorReporter.
   * @see #classdoc()
   */
  private static classdoc self;

  /**
   * Flag for verbose mode.
   */
  static boolean verbose=false;

  /**
   * Flag for 1.1 emulation mode.
   */
  private static boolean oneone=false;

  /**
   * Should any class be treated as public?
   * The jdk 1.3 Standard Doclet does not generate all documentation
   * if a class is not public.
   * @see ClassDocImpl.isPublic()
   */
  static boolean fakepublic=true; // accessed by ClassDocImpl

  /**
   * Modifier mode. This stores whether public, public/protected, ... information should be gathered.
   */
  private static int modifier=-1;

  /**
   * Dummy object (something not null).
   */
  static Object DUMMY=new Object();

  /***************************************************************************
   *
   * Constuctor
   *
   **************************************************************************/

  /**
   * Create instance of this class to be used as implementation of interface
   * DocErrorReporter and for ClassLoader access in usage().
   * @see #self
   */
  private classdoc()
  {
    //nop
  }


  /***************************************************************************
   *
   * Main program
   *
   **************************************************************************/

  /**
   * The main program.
   * @param args the command line arguments
   */
  public static void main(String args[])
  {
    // print welcome message
    //hello();

     // create an instance of DocErrorReporter
    self=new classdoc();

    // parse command line parameters
    String docpath=null;
    String docletName=null;
    boolean windowtitleIsSet=false;
    Vector opt=new Vector();
    if (args.length==0)
    {
      usage();
    }
    for (int i=0;i<args.length;i++)
    {
      if ((args[i].equals("-docpath"))&&(unique(docpath))&&(moreArgs(args,i)))
      {
        docpath=args[++i];
      }
      else if ((args[i].equals("-doclet"))&&(unique(docletName))&&(moreArgs(args,i)))
      {
        docletName=args[++i];
      }
      else if ((args[i].equals("-public"))||(args[i].equals("-protected"))||(args[i].equals("-packageprivate"))||(args[i].equals("-private")))
      {
        unique(modifier!=-1?"<public|protected|packageprivate|private>":null);
        if (args[i].equals("-public"))
        {
          modifier=PUBLIC;
        }
        else if (args[i].equals("-protected"))
        {
          modifier=PROTECTED;
        }
        else if (args[i].equals("-packageprivate"))
        {
          modifier=PACKAGEPRIVATE;
        }
        else if (args[i].equals("-private"))
        {
          modifier=PRIVATE;
        }
      }
      else if (args[i].equals("-1.1")&&unique(oneone?"1.1":null))
      {
        oneone=true;
      }
      else if (args[i].equals("-nofakepublic")&&unique(!fakepublic?"nofakepublic":null))
      {
        fakepublic=false;
      }
      else if (args[i].equals("-verbose"))
      {
        verbose=true;
      }
      else if (args[i].equals("-help")||args[i].equals("-?")||args[i].equals("/?")||args[i].equals("--help"))
      {
        usage(); // will call System.exit(0)
      }
      else if (args[i].startsWith("-")) // doclet parameters
      {
        String var=args[i];
        if (var.equals("-windowtitle"))
        {
          windowtitleIsSet=true;
        }
        Vector v=new Vector();
        while (((i+1)<args.length)&&(!args[i+1].startsWith("-")))
        {
          v.addElement(args[++i]);
        }
        String[] opts=new String[v.size()+1];
        opts[0]=var;
        for (int j=1;j<opts.length;j++)
        {
          opts[j]=(String)v.elementAt(j-1);
        }
        opt.addElement(opts);
      }
    }

    // maybe set default values, if not set on command line
    if (modifier==-1)
    {
      modifier=PROTECTED;
    }
    if (docletName==null)
    {
      if (!oneone)
      {
        docletName="com.sun.tools.doclets.standard.Standard"; // JDK 1.3 Standard Doclet
        // if no windowtitle has been set, use the following as default
        if (!windowtitleIsSet)
        {
          String[] s={"-windowtitle","Reverse-engineered Documentation (untitled)"};
          opt.addElement(s);
        }
      }
      else
      {
        docletName="com.sun.tools.doclets.oneone.OneOne"; // JDK 1.1 Emulation Doclet
      }
    }

    // create options-array to pass to doclet
    String[][] options=new String[opt.size()][];
    opt.copyInto(options);

    // load doclet class
    Class doclet;
    try
    {
      doclet=Class.forName(docletName);
    }
    catch (ClassNotFoundException cnfe)
    {
      error("cannot load doclet class "+docletName);
      return;
    }

    // validate own options
    if (docpath==null||docpath.equals(""))
    {
      error("no docpath set");
    }

    // let doclet validate its own options
    try
    {
      // dynamic version of "doclet.validOptions(options,self);"
      Class[] param={options.getClass(),DocErrorReporter.class};
      Method m = doclet.getMethod("validOptions",param);
      Object[] arg = {options,self};
      Boolean b = (Boolean)m.invoke(null,arg);
      if (!b.booleanValue()) // invalid options?
      {
        System.exit(1); // error message has been output by doclet through DocErrorReporter
      }
      // fallsthrough if options ok
    }
    catch (NoSuchMethodException e)
    {
      // 'gracefully' default to true
    }
    catch (Throwable ex) // some 'real' error occurred
    {
      if (ex instanceof InvocationTargetException)
      {
        ex=((InvocationTargetException)ex).getTargetException();
      }
      error("cannot validate doclet options ("+ex.getClass().getName()+")",ex);
    }

    // find all individual classes from docpath
    Vector todo=new Vector();
    StringTokenizer st=new StringTokenizer(docpath,File.pathSeparator);
    while (st.hasMoreTokens())
    {
      String path=st.nextToken();
      try
      {
        findIndividualClasses(path,todo);
      }
      catch (IOException e)
      {
        error("cannot get classes from "+path,e);
      }
    }
    String[] cl=new String[todo.size()];
    if (cl.length==0)
    {
      error("nothing to do");
    }
    todo.copyInto(cl);

    // create RootDoc object
    RootDocImpl rootDoc=new RootDocImpl();
    rootDoc.init("classdoc documentation",cl,options);

    // run doclet
    try
    {
      // dynamic version of "doclet.start(rootDoc)"
      Class[] param={RootDoc.class};
      Method m=doclet.getMethod("start",param);
      Object[] arg={rootDoc};
      m.invoke(null,arg); // start!
      // debugging alternative: static call to Standard Doclet
      /*
       try
      {
        com.sun.tools.doclets.standard.Standard.start(rootDoc);
      }
      catch (Throwable t)
      {
        throw new InvocationTargetException(t); // simulate exception inside m.invoke()
      }
      */
    }
    catch (InvocationTargetException ite)
    {
      Throwable ee=((InvocationTargetException)ite).getTargetException();
      ee.printStackTrace(System.err);
      error("cannot run doclet");
    }
    catch (Exception e)
    {
      error("class "+doclet+" appears not to be a Doclet - "+e.getClass().getName(),e);
    }
    System.exit(0); // cleanup all and exit
  }

  /**
   * Test if a modifier matches the specified -public/-protected/... mode.
   * @return true if the corresponding member should be included in the documentation.
   */
  static boolean matchModifiers(int mod)
  {
    if (modifier>PRIVATE)
    {
      if (modifier==PACKAGEPRIVATE)
      {
        return !Modifier.isPrivate(mod);
      }
      else if (modifier==PROTECTED)
      {
        return Modifier.isPublic(mod)||Modifier.isProtected(mod);
      }
      else // if (modifier==PUBLIC)
      {
        return Modifier.isPublic(mod);
      }
    }
    else
    {
      return true;
    }
  }


  /***************************************************************************
   *
   * Tools for main program
   *
   **************************************************************************/

  /**
   * Add all classes in given archive file or directory to vector (as classnames, not as filenames).
   */
  private static void findIndividualClasses(String jarOrDir,Vector classes) throws IOException
  {
    File f=new File(jarOrDir);
    if (f.isDirectory())
    {
      findIndividualClassesInDirectory("",f,classes);
    }
    else
    {
      findIndividualClassesInJar(f,classes);
    }
  }

  /**
   * Add all classes in a subdirectory of a corresponding parent package to vector.
   */
  private static void findIndividualClassesInDirectory(String packge,File dir,Vector classes) throws IOException
  {
    String[] l=dir.list();
    for (int i=0;i<l.length;i++)
    {
      registerIfClass(packge+l[i],classes);
      File f=new File(dir,l[i]);
      if (f.isDirectory())
      {
        findIndividualClassesInDirectory(packge+l[i]+".",f,classes);
      }
    }
  }

  /**
   * Add all classes in an archive file (jar/zip) to vector.
   */
  private static void findIndividualClassesInJar(File jar,Vector classes) throws IOException
  {
    ZipInputStream zip=new ZipInputStream(new FileInputStream(jar));
    ZipEntry entry=zip.getNextEntry();
    while (entry!=null)
    {
      String n=entry.getName();
      registerIfClass(n,classes);
      entry=zip.getNextEntry();
    }
    zip.close();
  }

  /**
   * Test if a filename represents a class, if yes, and add the corresponding classname to the vector.
   */
  private static void registerIfClass(String n,Vector classes)
  {
    if (n.endsWith(".class"))
    {
      String path=n.substring(0,n.length()-6);
      path=path.replace('/','.');
      path=path.replace('\\','.');
      log(path);
      classes.addElement(path);
    }
  }

  /**
   * Test if a parameter variable is already set. If yes, output error and exit progam.
   */
  private static boolean unique(String param)
  {
    if (param!=null)
    {
      error("parameter "+param+" cannot be set twice");
      return false; // actually never reached
    }
    else
    {
      return true;
    }
  }

  /**
   * Test if entry at index i is not the last in array args.
   */
  private static boolean moreArgs(String[] args,int i)
  {
    if (!(i<args.length-1))
    {
      error("argument is missing.");
      return false; // actually never reached
    }
    else
    {
      return true;
    }
  }


  /***************************************************************************
   *
   * Package tools
   *
   **************************************************************************/


  /**
   * Output a message to the console, if in verbose mode.
   * If in non-verbose mode, this does nothing.
   */
  static void log(String s)
  {
    if (verbose)
    {
      System.out.println(s);
    }
  }

  /**
   * Output an error message to the console and exit the program.
   */
  static void error(String s)
  {
    System.out.println("error: "+s);
    System.exit(1);
  }

  /**
   * Output an error message and an exception's/error's message and exit the program.
   */
  static void error(String s,Throwable t)
  {
    error(s+" - "+t.getMessage());
  }

  /**
   * Copies the elements of all hashtable entries into the array.
   * The array must have been initialized to the correct size before.
   * The keys of the hashtable entries play no role.
   */
  static void hashtable2array(Hashtable h,Object[] r)
  {
    int i=0;
    for (Enumeration e=h.elements();e.hasMoreElements();)
    {
      Object o=e.nextElement();
      r[i++]=o;
    }
  }

  /**
   * Returns an array which contains all elements that are contained in <code>a</code> but not in <code>b</code>.
   * In other words, the result is <code>a</code> without the elements that are both in <code>a</code> and <code>b</code>.
   */
  static Object[] minusArray(Object[] a,Object[] b)
  { // null must not be any element in the arrays
    int drop=0;
    Object[] temp=new Object[a.length]; // don't destroy original a
    System.arraycopy(a,0,temp,0,a.length);
    for (int i=0;i<temp.length;i++)
    {
      if (isInArray(b,a[i]))
      {
        temp[i]=null;
        drop++;
      }
    }
    Object[] r=new Object[temp.length-drop];
    int j=0;
    for (int i=0;i<temp.length;i++)
    {
      if (temp[i]!=null)
      {
        r[j++]=temp[i];
      }
    }
    return r;
  }

  /**
   * Tests if the object is contained in the array.
   */
  static boolean isInArray(Object[] a,Object o)
  {
    for (int i=0;i<a.length;i++)
    {
      if (a[i]==o)
      {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns a string that is s repeated r times.
   */
  static String repeat(String s,int r)
  {
    StringBuffer b=new StringBuffer();
    for (int i=0;i<r;i++)
    {
      b.append(s);
    }
    return b.toString();
  }

  /**
   * Removes any heading package information from a qualified name.
   */
  static String unqualify(String c)
  {
    int dotpos=c.lastIndexOf('.');
    if (dotpos!=-1)
    {
      return c.substring(dotpos+1);
    }
    else
    {
      return c;
    }
  }

  /**
   * Returns the heading package information from a qualified name.
   */
  static String packageName(String className)
  {
    int dotpos=className.lastIndexOf('.');
    if (dotpos!=-1)
    {
      return className.substring(0,dotpos);
    }
    else
    {
      return "";
    }
  }

  /**
   * Print a warning message.
   */
  static void printWarningS(String s)
  {
    System.out.println("warning: "+s);
  }

  /**
   * Print an error message.
   */
  static void printErrorS(String s)
  {
    System.out.println("error: "+s);
  }

  /**
   * Print a 'normal' message.
   */
  static void printNoticeS(String s)
  {
    log(s);
  }


  /***************************************************************************
   ***************************************************************************
   *
   * implementation of interface DocErrorReporter
   *
   ***************************************************************************
   **************************************************************************/

  /**
   * Print a warning message.
   * Same as printWarningS but callable via non-static instance of DocErrorReporter.
   */
  public void printWarning(String s)
  {
    printWarningS(s);
  }

  /**
   * Print an error message.
   * Same as printErrorS but callable via non-static instance of DocErrorReporter.
   */
  public void printError(String s)
  {
    printErrorS(s);
  }

  /**
   * Print a 'normal' message.
   * Same as printNoticeS but callable via non-static instance of DocErrorReporter.
   */
  public void printNotice(String s)
  {
    printNoticeS(s);
  }

/* (non-Javadoc)
 * @see com.sun.javadoc.DocErrorReporter#printError(com.sun.javadoc.SourcePosition, java.lang.String)
 */
public void printError(SourcePosition arg0, String arg1) {
	// TODO Auto-generated method stub
	
}

/* (non-Javadoc)
 * @see com.sun.javadoc.DocErrorReporter#printNotice(com.sun.javadoc.SourcePosition, java.lang.String)
 */
public void printNotice(SourcePosition arg0, String arg1) {
	// TODO Auto-generated method stub
	
}

/* (non-Javadoc)
 * @see com.sun.javadoc.DocErrorReporter#printWarning(com.sun.javadoc.SourcePosition, java.lang.String)
 */
public void printWarning(SourcePosition arg0, String arg1) {
	// TODO Auto-generated method stub
	
}
}
