package generator.classdoc;
/*
 * Project: classdoc
 * File:    FieldDocImpl.java
 *
 * Created on 1. April 2001, 15:25
 */

import com.sun.javadoc.*;
import com.sun.javadoc.Type;

import java.lang.reflect.*;
import java.util.*;

/**
 * Field documentation.
 *
 * @author Jens Gulden - <a href="http://www.jensgulden.de/" target="_top">www.jensgulden.de</a>
 * @version 1.0
 */
class FieldDocImpl extends MemberDocImpl implements FieldDoc
{
  /**
   * The field documented by this.
   */
  private Field field;
  
  /**
   * Initialize this.
   */
  void init(Field f,ClassDocImpl parent)
  {
    this.field=f;
    super.init(f,parent);
  }

  /**
   * Fill the hashtable with documentations of those classes that are used by this.
   * Being 'used' means here that a class appears as a field-type or method-return-type
   * or as a method/constructor parameter-type or as a thrown exception or as an implemented interface.
   */
  protected void getUsedClasses(Hashtable h) // overwrites ProgramElementDocImpl.getUsedClasses()
  {
    h.put(type(),classdoc.DUMMY);
      // type() returns Type, which is an interface
      // implemented by ClassDocImpl, so when getting data
      // out of the hashtable, the cast to ClassDocImpl will succeed
  }
  
  /**
   * Is this Doc item a field?
   * @return true if it represents a field
   */
  public boolean isField() // overwrites DocImpl.isField()
  {
    return true;
  }  
  
  /***************************************************************************
   *
   * implementation of interface FieldDoc
   *
   **************************************************************************/
  
  /**
   * Return the serialField tags in this FieldDoc item.
   * @return an array of SerialFieldTag containing all &#64serialField tags.
   */
  public SerialFieldTag[] serialFieldTags()
  {
    return new SerialFieldTag[0];
  }
  
  
  /**
   * Get type of this field. 
   */
  public Type type()
  {
    return ClassDocImpl.get(field.getType());
  }

/* (non-Javadoc)
 * @see com.sun.javadoc.FieldDoc#constantValue()
 */
public Object constantValue() {
	// TODO Auto-generated method stub
	return null;
}

/* (non-Javadoc)
 * @see com.sun.javadoc.FieldDoc#constantValueExpression()
 */
public String constantValueExpression() {
	// TODO Auto-generated method stub
	return null;
}

public AnnotationDesc[] annotations() {
	// TODO Auto-generated method stub
	return null;
}
}