package generator.classdoc;
/*
 * Project: classdoc
 * File:    DocRootImpl.java
 *
 * Created on 1. April 2001, 10:32
 */

import com.sun.javadoc.*;

import generator.SimpleLogger;

import java.io.*;
import java.util.*;

/**
 * Root documentation.
 * An instance of this is passed to the doclet to start generating output.
 *
 * @author Jens Gulden - <a href="http://www.jensgulden.de/" target="_top">www.jensgulden.de</a>
 * @version 1.0
 */
public class RootDocImpl extends DocImpl implements RootDoc
{
  /**
   * Pre-parsed command line arguments.
   * @see #options
   */
  private String[][] args;

  /**
   * Names of all classes to be documented.
   */
  private String[] specifiedClasses;

  /**
   * Package names of all classes to be documented.
   */
  private Hashtable specifiedPackages;

  /**
   * Initialize this.
   */
  public void init(String name,String[] specifiedClasses,String[][] args)
  {
    this.specifiedClasses = specifiedClasses;
    super.init(name);
    this.args = args;
    specifiedPackages = new Hashtable();
    for (int i=0;i<specifiedClasses.length;i++)
    {
      String className = specifiedClasses[i];
      String packageName = DocletImpl.packageName(className);
      if (specifiedPackages.get(packageName) == null) // not yet registered
      {
        specifiedPackages.put(packageName,new Object());
      }
      ClassDocImpl cd = ClassDocImpl.get(className);
    }
  }

  /***************************************************************************
   *
   * implementation of interface RootDoc
   *
   **************************************************************************/

  /**
   * Command line options.
   *   For example, given:
   *    javadoc -foo this that -bar other ...
   *  This method will return:
   *    options()[0][0] = "-foo"
   *    options()[0][1] = "this"
   *    options()[0][2] = "that"
   *    options()[1][0] = "-bar"
   *    options()[1][1] = "other"
   * @return an array of arrays of String.
   */
  public String[][] options()
  {
    return args;
  }

  /**
   * Return a ClassDoc for the specified class/interface name
   * @param qualifiedName - qualified class name (i.e. includes package name).
   * @return a ClassDoc holding the specified class, null if this class is not referenced.
   */
  public ClassDoc classNamed(String name)
  {
    ClassDoc cd=ClassDocImpl.get(name);
    return cd;
  }

  /**
   * Classes and interfaces specified on the command line.
   */
  public ClassDoc[] specifiedClasses()
  {
    Vector v=new Vector();
    for (int i=0;i<specifiedClasses.length;i++)
    {
      ClassDoc cd=ClassDocImpl.get(specifiedClasses[i]);
      if (cd!=null)
      {
        v.addElement(cd);
      }
      else
      {
        printWarning("cannot load class "+specifiedClasses[i]+" - ignore");
      }
    }
    ClassDoc[] cd=new ClassDoc[v.size()];
    v.copyInto(cd);
    return cd;
  }

  /**
   * Classes and interfaces to be documented.
   */
  public ClassDoc[] classes()
  {
    return specifiedClasses();
  }

  /**
   * Return a PackageDoc for the specified package name
   * @param name - package name
   * @return a PackageDoc holding the specified package, null if this package is not referenced.
   */
  public PackageDoc packageNamed(String name)
  {
    return PackageDocImpl.get(name);
  }

  /**
  * Packages specified on the command line.
  */
  public PackageDoc[] specifiedPackages()
  {
    Vector pdV=new Vector();
    for (Enumeration e=specifiedPackages.keys();e.hasMoreElements();)
    {
      String pack=(String)e.nextElement();
      pdV.addElement(PackageDocImpl.get(pack));
    }
    PackageDoc[] pd=new PackageDoc[pdV.size()];
    pdV.copyInto(pd);
    return pd;
  }

  /***************************************************************************
   *
   * implementation of interface DocErrorReporter
   *
   **************************************************************************/

  /**
   * Print warning message.
   */
  public void printWarning(String s)
  {
	  SimpleLogger.printWarningS(s);
  }

  /**
   * Print error message.
   */
  public void printError(String s)
  {
	  SimpleLogger.printErrorS(s);
  }

  /**
   * Print notice message.
   */
  public void printNotice(String s)
  {
    SimpleLogger.printNoticeS(s);
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
