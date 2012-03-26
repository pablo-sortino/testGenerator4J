package generator.classdoc;
/*
 * Project: classdoc
 * File:    ExecutableMemberDocImpl.java
 *
 * Created on 1. April 2001, 15:38
 */

import com.sun.javadoc.*;
import com.sun.javadoc.Type;
import com.sun.javadoc.TypeVariable;

import java.lang.reflect.*;
import java.util.*;

/**
 *
 * @author Jens Gulden - <a href="http://www.jensgulden.de/" target="_top">www.jensgulden.de</a>
 * @version 1.0
 */
class ExecutableMemberDocImpl extends MemberDocImpl implements ExecutableMemberDoc
{
  /**
   * Initialize this.
   */
  void init(Constructor m,ClassDocImpl parent)
  {
    super.init(m,parent);
  }

  /**
   * Initialize this.
   */
  void init(Method m,ClassDocImpl parent)
  {
    super.init(m,parent);
  }

  /**
   * Fill the hashtable with documentations of those classes that are used by this.
   * Being 'used' means here that a class appears as a field-type or method-return-type
   * or as a method/constructor parameter-type or as a thrown exception or as an implemented interface.
   */
  protected void getUsedClasses(Hashtable h) // overwrites ProgramElementDocImpl.getUsedClasses()
  {
    Parameter[] p=parameters();
    for (int i=0;i<p.length;i++)
    {
      h.put(p[i].type(),classdoc.DUMMY); // Note:
        // type() returns Type, which is an interface
        // implemented by ClassDocImpl, so when getting data
        // out of the hashtable, the cast to ClassDocImpl will succeed
    }
    ClassDoc[] c=thrownExceptions();
    for (int i=0;i<c.length;i++)
    {
      h.put(c[i],classdoc.DUMMY);
    }
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
  public String qualifiedName() // overwrites DocImpl.name()
  {
    return super.qualifiedName()+signature();
  }

  /***************************************************************************
   *
   * implementation of interface ExecutableMemberDoc
   *
   **************************************************************************/

  /**
   * Return exceptions this method or constructor throws.
   * @return an array of Type[] representing the exceptions thrown by this method.
   */
  public ClassDoc[] thrownExceptions()
  {
    if (member instanceof Constructor)
    {
      return ClassDocImpl.get(((Constructor)member).getExceptionTypes());
    }
    else if (member instanceof Method)
    {
      return ClassDocImpl.get(((Method)member).getExceptionTypes());
    }
    else
    {
      return null; // can't actually happen (see init(...))
    }
  }

  /**
   * Get argument information.
   * @return an array of Parameter, one element per argument in the order the arguments are present.
   * @see Parameter
   */
  public Parameter[] parameters()
  {
    if (member instanceof Constructor)
    {
      return ParameterImpl.create(((Constructor)member).getParameterTypes());
    }
    else if (member instanceof Method)
    {
      return ParameterImpl.create(((Method)member).getParameterTypes());
    }
    else
    {
      return null; // can't actually happen (see init(...))
    }
  }

  /**
   * Return the param tags in this method.
   * @return an array of ParamTag containing all &#64param tags.
   */
  public ParamTag[] paramTags()
  {
    return new ParamTag[0];
  }

  /**
   * Get the signature. It is the parameter list, type is qualified.
   * For instance, for a method  mymethod(String x, int y) ,
   * it will return(java.lang.String,int) .
   */
  public String signature()
  {
    String s=ParameterImpl.signature(parameters());
    return s;
  }

  /**
   * Get flat signature. All types are not qualified.
   * Return a String, which is the flat signiture of this member.
   * It is the parameter list, type is not qualified.
   * For instance, for a method  mymethod(String x, int y), it will return(String, int) .
   */
  public String flatSignature()
  {
    String s=ParameterImpl.flatSignature(parameters());
    return s;
  }

  /**
   * Return the throws tags in this method.
   * @return an array of ThrowTag containing all &#64exception and &#64throws tags.
   */
  public ThrowsTag[] throwsTags()
  {
    return new ThrowsTag[0];
  }

public boolean isVarArgs() {
	// TODO Auto-generated method stub
	return false;
}

public Type[] thrownExceptionTypes() {
	// TODO Auto-generated method stub
	return null;
}

public ParamTag[] typeParamTags() {
	// TODO Auto-generated method stub
	return null;
}

public TypeVariable[] typeParameters() {
	// TODO Auto-generated method stub
	return null;
}

public AnnotationDesc[] annotations() {
	// TODO Auto-generated method stub
	return null;
}

  /*
   *
   *  methods isNative(), isSynchronized() are implemented by DocImpl
   *
   */
}
