package generator.classdoc;
/*
 * Project: classdoc
 * File:    MethodDocImpl.java
 *
 * Created on 1. April 2001, 15:33
 */

import com.sun.javadoc.*;
import com.sun.javadoc.Type;

import java.lang.reflect.*;
import java.util.*;

/**
 * Method documentation.
 *
 * @author Jens Gulden - <a href="http://www.jensgulden.de/" target="_top">www.jensgulden.de</a>
 * @version 1.0
 */
class MethodDocImpl extends ExecutableMemberDocImpl implements MethodDoc
{
  /**
   * Method documented by this.
   */
  private Method method;

  /**
   * Initialize this.
   */
  void init(Method m,ClassDocImpl parent)
  {
    this.method=m;
    super.init(m,parent);
  }

  /**
   * Fill the hashtable with documentations of those classes that are used by this.
   * Being 'used' means here that a class appears as a field-type or method-return-type
   * or as a method/constructor parameter-type or as a thrown exception or as an implemented interface.
   */
  protected void getUsedClasses(Hashtable h) // overwrites ProgramElementDocImpl.getUsedClasses()
  {
    super.getUsedClasses(h); // parameter types
    h.put(returnType(),classdoc.DUMMY); // Note:
      // returnType() returns object of type Type, which is an interface
      // implemented by ClassDocImpl, so when getting data
      // out of the hashtable, the cast to ClassDocImpl will succeed
  }

  /**
   * Is this Doc item a simple method (i.e. not a constructor)?
   * False until overridden.
   * @return true is it represents a method, false if it is anything else, including constructor, field, class, ...
   */
  public boolean isMethod() // overwrites DocImpl.isMethod()
  {
    return true;
  }

  /***************************************************************************
   *
   * implementation of interface MethodDoc
   *
   **************************************************************************/

  /**
   * Return the class that overrides this method.
   * @return a ClassDoc representing the superclass that overrides this method, null if this method is not overriden.
   */
  public ClassDoc overriddenClass()
  {
    try
    {
      Class sup=parentClass.getClass().getSuperclass();
      Method m=sup.getMethod(method.getName(),method.getParameterTypes());
      return ClassDocImpl.get(sup); // if reached here, method was found in superclass
    }
    catch (NoSuchMethodException e)
    {
      return null;
    }
  }

  /**
   * Get return type.
   * @return the return type of this method, null if it is a constructor.
   */
  public Type returnType()
  {
	return ClassDocImpl.get(method.getReturnType());
  }
  
  /*public String returnType(String outputDIR) {
  	if(method.getReturnType().toString().indexOf(" ")!=-1) {
		System.out.println(method.getReturnType());
		return method.getReturnType().toString();
	}
        return null;	 
  }*/  	

/* (non-Javadoc)
 * @see com.sun.javadoc.MethodDoc#overriddenMethod()
 */
public MethodDoc overriddenMethod() {
	// TODO Auto-generated method stub
	return null;
}

public Type overriddenType() {
	// TODO Auto-generated method stub
	return null;
}

public boolean overrides(MethodDoc arg0) {
	// TODO Auto-generated method stub
	return false;
}

}
