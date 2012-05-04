package generator.classdoc;
/*
 * Project: classdoc
 * File:    PackageDocImpl.java
 *
 * Created on 1. April 2001, 12:25
 */

import com.sun.javadoc.*;

import generator.SimpleLogger;

import java.lang.reflect.*;
import java.util.*;

/**
 * Package documentation.
 *
 * @author Jens Gulden - <a href="http://www.jensgulden.de/" target="_top">www.jensgulden.de</a>
 * @version 1.0
 */
class PackageDocImpl extends DocImpl implements PackageDoc
{
  /***************************************************************************
   ***************************************************************************
   *
   * Static buffered factory
   *
   ***************************************************************************
   **************************************************************************/

  /***************************************************************************
   *
   * Static variables
   *
   **************************************************************************/

  /**
   * Instance buffer for static buffered factory.
   */
  private static Hashtable buffer=new Hashtable();

  /***************************************************************************
   *
   * Buffered factory methods get()
   *
   **************************************************************************/

  /**
   * Create a PackageDoc that describes a package. If previously an instance for
   * the same class has been created, this instance is returned again.
   */
  static PackageDocImpl get(String name) // in place of a constructor
  {
    PackageDocImpl p=(PackageDocImpl)buffer.get(name);
    if (p==null)
    {
      p=new PackageDocImpl();
      p.init(name);
      buffer.put(name,p);
    }
    return p;
  }

  /***************************************************************************
   ***************************************************************************
   *
   * Instance
   *
   ***************************************************************************
   **************************************************************************/

  /***************************************************************************
   *
   * Constructor
   *
   **************************************************************************/

  /**
   * This class is a static buffered factory, that means instances can only
   * be retrieved via get(..).
   * The constructor is prevented from outer access by making it private.
   */
  private PackageDocImpl()
  {
    //nop
  }

  /***************************************************************************
   *
   * Instance variables
   *
   **************************************************************************/

  /**
   * All created documentations of the classes in the package documented by this.
   * @see #registerClass
   */
  private Hashtable classDocs;

  /**
   * Initialize this.
   */
  void init(String name)
  {
    super.init(name);
    classDocs=new Hashtable();
  }

  /**
   * Register a class's documentation as member of this package.
   * Called when ClassDocImpl creates a new instance.
   * #see ClassDocImpl.get(java.lang.Class)
   * #see ClassDocImpl.get(java.lang.String)
   */
  void registerClass(ClassDocImpl cd)
  {
    classDocs.put(cd.qualifiedName(),cd);
  }

  /***************************************************************************
   *
   * additional implementation of interface Doc
   *
   **************************************************************************/

  /**
   * Returns the name of this Doc item.
   * @return the name
   */
  public String name() // overwrites DocImpl.name()
  {
    return name;
  }

  /***************************************************************************
   *
   * implementation of interface PackageDoc
   *
   **************************************************************************/
  public ClassDoc[] allClasses(boolean ac)
  {
  	ClassDoc[] cd=new ClassDoc[classDocs.size()];
    DocletImpl.hashtable2array(classDocs,cd);
    return cd;
  }
  /**
   * Get ordinary classes (excluding Exceptions and Errors)
   * in this package.
   * @return included classes in this package.
   */
  public ClassDoc[] ordinaryClasses()
  {
    Object[] cd=allClasses();
    cd=DocletImpl.minusArray(cd,exceptions());
    cd=DocletImpl.minusArray(cd,errors());
    cd=DocletImpl.minusArray(cd,interfaces());
    ClassDoc[] c=new ClassDoc[cd.length];
    for (int i=0;i<c.length;i++)
    {
      c[i]=(ClassDoc)cd[i];
    }
    return c;
  }

  /**
   * Lookup for a class within this package.
   * @return ClassDoc of found class, or null if not found.
   */
  public ClassDoc findClass(String name)
  {
    return (ClassDoc)classDocs.get(name);
  }

  /**
   * Get all classes (including Exceptions and Errors)
   *  and interfaces. Returns: all included classes and interfaces in this package.
   */
  public ClassDoc[] allClasses()
  {
    ClassDoc[] cd=new ClassDoc[classDocs.size()];
    DocletImpl.hashtable2array(classDocs,cd);
    return cd;
  }

  /**
   * Get Error classes in this package.
   * @return included Errors in this package.
   */
  public ClassDoc[] errors()
  {
    return filterClasses("isError");
  }

  /**
   * Get Interfaces in this package.
   * @return included Errors in this package.
   */
  public ClassDoc[] interfaces()
  {
    return filterClasses("isInterface");
  }

  /**
   * Get Exception classes in this package.
   * @return included Exceptions in this package.
   */
  public ClassDoc[] exceptions()
  {
    return filterClasses("isException");
  }

  /***************************************************************************
   *
   * private tools
   *
   **************************************************************************/

  /**
   * Return all those ClassDocs where the names filterMethod returns true.
   */
  private ClassDoc[] filterClasses(String filterMethod)
  {
    try
    {
      // call method which determines if a class should be in result
      // by dynamically calling it via reflect api
      // ...this is a really ugly way and almost unreadable,
      // but prevents copying this code multiple times
      Method m=ClassDoc.class.getMethod(filterMethod,new Class[0]); // get boolean method
      Vector v=new Vector();
      ClassDoc[] cd=allClasses();
      for (int i=0;i<cd.length;i++)
      {
        if (((Boolean)m.invoke(cd[i],new Object[0])).booleanValue()) // call boolean method
        {
          v.addElement(cd[i]);
        }
      }
      ClassDoc[] r=new ClassDoc[v.size()];
      v.copyInto(r);
      return r;
    }
    catch (Exception e)
    {
      SimpleLogger.error("cannot filter classes on method "+filterMethod,e);
      return null;
    }
  }

public AnnotationTypeDoc[] annotationTypes() {
	// TODO Auto-generated method stub
	return null;
}

public AnnotationDesc[] annotations() {
	// TODO Auto-generated method stub
	return null;
}

public ClassDoc[] enums() {
	// TODO Auto-generated method stub
	return null;
}
}
