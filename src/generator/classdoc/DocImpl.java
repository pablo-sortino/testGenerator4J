package generator.classdoc;
/*
 * Project: classdoc
 * File:    DocImpl.java
 *
 * Created on 1. April 2001, 12:13
 */

import com.sun.javadoc.*;

/**
 * Abstract base class for all documentation elements.
 *
 * @author Jens Gulden - <a href="http://www.jensgulden.de/" target="_top">www.jensgulden.de</a>
 * @version 1.0
 */
class DocImpl implements Doc
{
  /**
   * Name of the documented element.
   */
  protected String name;
  
  /**
   * Raw comment text. This stays "" in this version.
   */
  protected String rawComment="";
  
  /** 
   * Initialize this.
   */
  void init(String name)
  {
    this.name=name;
  }
  
  /***************************************************************************
   *
   * implementation of interface Doc
   *
   **************************************************************************/
  
  /**
   * Returns the name of this Doc item.
   * @return the name
   */
  public String name()
  {
    return DocletImpl.unqualify(name);
  }
  
  /**
   * Compares this Object with the specified Object for order.  Returns a
   * negative integer, zero, or a positive integer as this Object is less
   * than, equal to, or greater than the given Object.
   * Included so that Doc item are java.lang.Comparable.
   * Specified by: compareTo in interface java.lang.Comparable
   * @parameter o - the Object to be compared.
   * @return a negative integer, zero, or a positive integer as this Object
   * is less than, equal to, or greater than the given Object.
   * @throws ClassCastException - the specified Object's type prevents it
   * from being compared to this Object.
   */
  public int compareTo(Object o)
  {
    return this.toString().compareTo(o.toString());
  }
  
  public int compareTo(String s)
  {
    return this.toString().compareTo(s);
  }
  
  public boolean equals(Object o)
  {
    return compareTo(o)==0;
  }
  
  public String toString() // (not part of interface)
  {
    return name();
  }
  
  /**
   * Return the full unprocessed text of the comment.
   * Tags are included as text.
   */
  public String getRawCommentText()
  {
    return rawComment;
  }
  
  /**
   * Is this Doc item a field?
   * False until overridden.
   * @return true if it represents a field
   */
  public boolean isField()
  {
    return false; // will be overwritten by field
  }
  
  /**
   * Set the full unprocessed text of the comment.
   * Tags are included as text.
   */
  public void setRawCommentText(String s)
  {
    rawComment=s;
  }
  
  /**
   * Is this Doc item a simple method (i.e. not a constructor)?
   * False until overridden.
   * @return true is it represents a method, false if it is anything else, including constructor, field, class, ...
   */
  public boolean isMethod()
  {
    return false; // will be overwritten by MethodDocImpl
  }
  
  /**
   * Is this Doc item a class.
   * Note: interfaces are not classes.
   * False until overridden.
   * @return true is it represents a class
   */
  public boolean isClass()
  {
    return false; // will be overwritten by ClassDocImpl
  }
  
  /**
   * Is this Doc item a error class?
   * False until overridden.
   * @return true is it represents a error
   */
  public boolean isError()
  {
    return false; // will be overwritten by ClassDocImpl
  }
  
  /**
   * Is this Doc item a exception class?
   * False until overridden.
   * @return true is it represents a exception
   */
  public boolean isException()
  {
    return false; // will be overwritten by ClassDocImpl
  }
  
  /**
   * Is this Doc item a constructor?
   * False until overridden.
   * @return true is it represents a constructor
   */
  public boolean isConstructor()
  {
    return false; // will be overwritten by ConstructorDocImpl
  }
  
  /**
   * Is this Doc item a ordinary class (i.e. not an interface, exception,
   * or error)?
   * False until overridden.
   * @return true is it represents a class
   */
  public boolean isOrdinaryClass()
  {
    return false; // will be overwritten by ClassDocImpl
  }
  
  /**
   * Is this Doc item a interface?
   * False until overridden.
   * @return true is it represents a interface
   */
  public boolean isInterface()
  {
    return false; // will be overwritten by ClassDocImpl
  }  
  
  /**
   * return true if this Doc is include in the active set. 
   */
  public boolean isIncluded()
  {
    return false;
  }
  
  /**
   * Return the see also tags in this Doc item.
   * @return an array of SeeTag containing all &#64see tags.
   */
  public SeeTag[] seeTags()
  {
    return new SeeTag[0];
  }
  
  /**
   * Return the text of the comment for this doc item.
   * Tags have been removed. 
   */
  public String commentText()
  {
    return rawComment;
  }
  
  /**
   * Return all tags in this Doc item.
   * @return an array of Tag containing all tags on this Doc item.
   */
  public Tag[] tags()
  {
    if (rawComment.length()>0) // never true in this version
    {
      Tag[] t={new TagImpl(rawComment)}; // allow single text as comment
      return t;
    }
    else
    {
      return new Tag[0];
    }
  }

  /**
   * Return tags of the specified kind in this Doc item. Parameters: tagname - name of the tag kind to search for.
   * @return an array of Tag containing all tags whose 'kind()' matches 'tagname'.
   */
  public Tag[] tags(String s)
  {
    if ((s!=null)&&s.equals("text"))
    {
      return tags();
    }
    else
    {
      return new Tag[0];
    }
  }  

  /**
   * Return the first sentence of the comment as tags. 
   *  Includes inline tags 
   *  (i.e. {&#64link  reference } tags) but not
   *  regular tags. 
   *  Each section of plain text is represented as a Tag of kind "Text".
   *  Inline tags are represented as a SeeTag of kind "link".
   *  First sentence is determined by 
   *  java.text.BreakIterator#getSentenceInstance(Locale) . Returns: an array of Tag s representing the 
   *  first sentence of the comment
   */
  public Tag[] firstSentenceTags()
  {
    return tags();
  }  
  
  /**
   * Return comment as tags. Includes inline tags 
   *  (i.e. {&#64link  reference } tags) but not
   *  regular tags. 
   *  Each section of plain text is represented as a Tag of kind "Text".
   *  Inline tags are represented as a SeeTag of kind "link".
   *  @return an array of Tag s representing the comment.
   */
  public Tag[] inlineTags()
  {
    return new Tag[0];
  }

/* (non-Javadoc)
 * @see com.sun.javadoc.Doc#position()
 */
public SourcePosition position() {
//	public SourcePosition position(){SourcePosition sp = null; return sp;} 

	// TODO Auto-generated method stub
	return null;
}

public boolean isAnnotationType() {
	// TODO Auto-generated method stub
	return false;
}

public boolean isAnnotationTypeElement() {
	// TODO Auto-generated method stub
	return false;
}

public boolean isEnum() {
	// TODO Auto-generated method stub
	return false;
}

public boolean isEnumConstant() {
	// TODO Auto-generated method stub
	return false;
}
  
}
