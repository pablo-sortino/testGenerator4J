package generator.classdoc;
/*
 * Project: classdoc
 * File:    TagImpl.java
 *
 * Created on 2. April 2001, 21:21
 */

import com.sun.javadoc.*;

/**
 * Tag of javadoc comment.
 *
 * NOT USED IN THIS VERSION
 *
 * @author Jens Gulden - <a href="http://www.jensgulden.de/" target="_top">www.jensgulden.de</a>
 * @version 1.0
 */
class TagImpl implements Tag
{
  /**
   * Text associated with this tag.
   */
  private String text;
  
  /**
   * Create a new Tag.
   */
  TagImpl(String text)
  {
    this.text=text;
  }

  /***************************************************************************
   *
   * implementation of interface Tag
   *
   **************************************************************************/
  
  /**
   * Return the name of this tag. 
   */
  public String name()
  {
    return "text";
  }
  
  /**
   * Return the first sentence of the comment as tags. 
   * Includes inline tags (i.e. {&#64link  reference } tags) but not regular tags. 
   * Each section of plain text is represented as a Tag of kind "Text".
   * Inline tags are represented as a SeeTag of kind "link".
   * First sentence is determined by java.text.BreakIterator#getSentenceInstance(Locale).
   * @return an array of Tag s representing the first sentence of the comment
   */
  public Tag[] firstSentenceTags()
  {
    return new Tag[0];
  }
  
  /**
   * Convert this object to a string.
   */
  public String toString()
  {
    return text();
  }
  
  /**
   * Return the kind of this tag. 
   */
  public String kind()
  {
    return "text";
  }
  
  /**
   * Return the text of this tag, that is, portion beyond tag name. 
   */
  public String text()
  {
    return text;
  }
  
  /**
   * For documentation comment with embedded @link tags, return the array of
   * Tags consisting of SeeTag(s) and text containing Tag(s).
   * Within a comment string "This is an example of inline tags for a
   * documentaion comment  {@link Doc commentlabel }",
   * where inside the inner braces, the first "Doc" carries exctly the same
   * syntax as a SeeTag and the second "commentlabel" is label for the Html
   * Link, will return an array of Tag(s) with first element as Tag with
   * comment text "This is an example of inline tags for a documentation
   * comment" and second element as SeeTag with referenced class as "Doc"
   * and the label for the Html Link as "commentlabel".
   * @return Tag[] Array of tags with inline SeeTags.
   * @see ParamTag
   * @see ThrowsTag
   */
  public Tag[] inlineTags()
  {
    return new Tag[0]; // dummy
  }

/* (non-Javadoc)
 * @see com.sun.javadoc.Tag#holder()
 */
public Doc holder() {
	// TODO Auto-generated method stub
	return null;
}

/* (non-Javadoc)
 * @see com.sun.javadoc.Tag#position()
 */
public SourcePosition position() {
	// TODO Auto-generated method stub
	return null;
}
}