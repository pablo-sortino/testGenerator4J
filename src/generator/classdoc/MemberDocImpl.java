package generator.classdoc;
/*
 * Project: classdoc
 * File:    MemberDocImpl.java
 *
 * Created on 1. April 2001, 15:20
 */

import com.sun.javadoc.*;

import java.lang.reflect.*;

/**
 * Abstract member documentation.
 *
 * @author Jens Gulden - <a href="http://www.jensgulden.de/" target="_top">www.jensgulden.de</a>
 * @version 1.0
 */
abstract class MemberDocImpl extends ProgramElementDocImpl implements MemberDoc
{
  /**
   * The member documented by this.
   */
  protected Member member;
  
  /**
   * Initialize this.
   */
  void init(Member m,ClassDocImpl parent)
  {
    this.member=m;
    super.init(m.getName(),parent,m.getModifiers());
  }

  /***************************************************************************
   *
   * implementation of interface MemberDoc
   *
   **************************************************************************/
  
  /**
   * Returns true if this member was synthesized by the compiler. 
   */
  public boolean isSynthetic()
  {
    return (name().indexOf('$')!=-1); // assume that when a member contains a $ it is synthetic (???)
  }  
}
