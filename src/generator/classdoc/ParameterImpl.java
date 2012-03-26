package generator.classdoc;
/*
 * Project: classdoc
 * File:    ParameterImpl.java
 *
 * Created on 1. April 2001, 15:55
 */

import com.sun.javadoc.*;
import com.sun.javadoc.Type;

import java.lang.reflect.*;
import java.util.*;

/**
 * Parameter class for ExecutableMembers.
 *
 * @author Jens Gulden - <a href="http://www.jensgulden.de/" target="_top">www.jensgulden.de</a>
 * @version 1.0
 */
class ParameterImpl implements Parameter
{
  /***************************************************************************
   ***************************************************************************
   *
   * Static factory
   *
   ***************************************************************************
   **************************************************************************/
  
  /***************************************************************************
   *
   * Factory method
   *
   **************************************************************************/
  
  /**
   * Create an array of ParameterImpl for ExecutableMember.
   */
  static ParameterImpl[] create(Class[] c)
  {
    // pass 1: find names and determine whether a letter has to be attachet with a number or not
    String[] names=new String[c.length];
    Hashtable numbernames=new Hashtable();
    for (int i=0;i<c.length;i++)
    {
      String cn=classdoc.unqualify(ClassDocImpl.getClassNameNoDimensions(c[i]));
      // make short name from all upper case letters in name (then converted to lower case)
      String n="";
      for (int j=0;j<cn.length();j++)
      {
        char ch=cn.charAt(j);
        if (Character.isUpperCase(ch))
        {
          n+=String.valueOf(ch).toLowerCase();
        }
        if (n.length()==0) // no upper case letters in class name?
        {
          n=String.valueOf(cn.charAt(0)); // single letter
        }
      }
      names[i]=n; // remember short name
      // is this short name already in use by another parameter?
      for (int j=0;j<i;j++)
      {
        if (names[j].equals(n)) // yes
        {
          numbernames.put(n,new Integer(0)); // remember that this shortname needs to be numbered
        }
      }
    }
    // pass 2: create Parameters
    ParameterImpl[] p=new ParameterImpl[c.length];
    for (int i=0;i<c.length;i++)
    {
      String n=names[i]; // shortname for this parameter
      Integer in=(Integer)numbernames.get(n);
      if (in!=null) // needs to be numbered
      {
        int num=in.intValue()+1;
        numbernames.put(n,new Integer(num)); // count up in memory
        n=n+num;
      }
      p[i]=create(n,c[i]);
    }
    return p;
  }
  
  /**
   * Create single ParameterImpl. Only internal use.
   */
  private static ParameterImpl create(String name,Class c)
  {
    return new ParameterImpl(name,c);
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
   * Constructor, called from factory method only.
   * @see #create
   */
  private ParameterImpl(String name,Class c)
  {
    this.name=name;
    this._class=c;
  }
  
  /**
   * Name of the parameter.
   */
  private String name;
  
  /**
   * Type of the parameter.
   */
  private Class _class;  

  /***************************************************************************
   *
   * implementation of interface ParameterDoc
   *
   **************************************************************************/
  
  /**
   * Get local name of this parameter.
   * For example if parameter is the short 'index', returns "index". 
   */
  public String name()
  {
    return name;
  }
  
  /**
   * Get type name of this parameter.
   * For example if parameter is the short 'index', returns "short". 
   */
  public String typeName()
  {
    return ClassDocImpl.getClassName(_class);
  }
  
  /**
   * Returns a string representation of the parameter.
   * For example if parameter is the short 'index', returns "short index".
   * @return type name and parameter name of this parameter.
   */
  public String toString()
  {
    return typeName()+" "+name();
  }
  
  /**
   * Get the type of this parameter. 
   */
  public Type type()
  {
    return ClassDocImpl.get(_class);
  }  
  
  /***************************************************************************
   *
   * package tools
   *
   **************************************************************************/
  
  /**
   * Get the signature of a parameter array.
   * It is the parameter list, type is qualified.
   * For instance, for a method  mymethod(String x, int y) ,
   * it will return(java.lang.String,int) .
   */
  static String signature(Parameter[] p)
  {
    StringBuffer sb=new StringBuffer("(");
    for (int i=0;i<p.length;i++)
    {
      sb.append(((ParameterImpl)p[i]).signature()+((i<p.length-1)?",":""));
    }
    sb.append(")");
    return sb.toString();
  }
  
  /**
   * Get flat signature of a parameter array.
   * All types are not qualified.
   * Return a String, which is the flat signiture of this member.
   * It is the parameter list, type is not qualified.
   * For instance, for a method  mymethod(String x, int y), it will return(String, int) .
   */
  static String flatSignature(Parameter[] p)
  {
    StringBuffer sb=new StringBuffer("(");
    for (int i=0;i<p.length;i++)
    {
      sb.append(((ParameterImpl)p[i]).flatSignature()+((i<p.length-1)?",":""));
    }
    sb.append(")");
    return sb.toString();
  }
  
  /***************************************************************************
   *
   * private tools
   *
   **************************************************************************/
  
  /**
   * Get signature of parameter.
   */
  private String signature()
  {
    return typeName();
  }
  
  /**
   * Get flat signature of parameter.
   */
  private String flatSignature()
  {
    return classdoc.unqualify(typeName());
  }

public AnnotationDesc[] annotations() {
	// TODO Auto-generated method stub
	return null;
}

}
