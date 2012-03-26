package generator.classdoc;
/*
 * Project: classdoc
 * File:    ConstructorDocImpl.java
 *
 * Created on 1. April 2001, 15:44
 */

import com.sun.javadoc.*;

import java.lang.reflect.*;

/**
 * Constructor documentation.
 *
 * @author Jens Gulden - <a href="http://www.jensgulden.de/" target="_top">www.jensgulden.de</a>
 * @version 1.0
 */
class ConstructorDocImpl extends ExecutableMemberDocImpl implements ConstructorDoc
{
  /**
   * The constructor documented by this.
   */
  private Constructor constructor;
  
  /**
   * Initialize this.
   */
  void init(Constructor c,ClassDocImpl parent)
  {
    this.constructor=c;
    super.init(constructor,parent);
  }

  /***************************************************************************
   *
   * implementation of interface ConstructorDoc already done in superclass
   *
   **************************************************************************/
  
  public boolean isConstructor() // overwrites DocImpl.isConstructor()
  {
    return true;
  }  
}
