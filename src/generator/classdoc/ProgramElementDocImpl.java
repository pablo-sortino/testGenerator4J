package generator.classdoc;
/*
 * Project: classdoc
 * File:    ProgramElementDocImpl.java
 *
 * Created on 1. April 2001, 12:27
 */

import com.sun.javadoc.*;

import java.lang.reflect.*;
import java.util.*;

/**
 * Abstract base class for program element documentaions.
 * Program elements are classes, fields, constructors, methods and their parameters.
 *
 * @author Jens Gulden - <a href="http://www.jensgulden.de/" target="_top">www.jensgulden.de</a>
 * @version 1.0
 */
abstract class ProgramElementDocImpl extends DocImpl implements ProgramElementDoc
{
  /**
   * Documentation of the class to which the program element belongs to.
   * This is null for non-inner classes.
   */
  protected ClassDocImpl parentClass;
  
  /**
   * The modifiers of the documented element (public/protected/...).
   * @see java.lang.reflect.Modifier
   */
  private int mod;
  
  /**
   * Initialize this.
   */
  void init(String name,ClassDocImpl parentClass,int mod)
  {
    this.parentClass=parentClass;
    this.mod=mod;
    super.init(name);
  }

  /**
   * Fill the hashtable with documentations of those classes that are used by this.
   * Being 'used' means here that a class appears as a field-type or method-return-type
   * or as a method/constructor parameter-type or as a thrown exception or as an implemented interface.
   */
  abstract protected void getUsedClasses(Hashtable h);
  
  /***************************************************************************
   *
   * implementation of interface ProgramElementDoc
   *
   **************************************************************************/
  
  /**
   * Get the package that this program element is contained in. Returns: a PackageDoc for this element containing package.
   * If in the unnamed package, this PackageDoc will have the name "".
   */
  public PackageDoc containingPackage()
  {
    return parentClass.containingPackage();
  }
  
  /**
   * Get the fully qualified name.
   * Example: for the class java.util.Hashtable, return: java.util.Hashtable
   *          for the method bar() in class Foo in the unnamed package, return: Foo.bar()
   * @return the qualified name of the program element as a String.
   */
  public String qualifiedName()
  {
    return parentClass.qualifiedName()+"."+name;
  }
  
  public String toString() // (not part of interface)
  {
    return qualifiedName();
  }
  
  /**
   * Get the containing class of this program element.
   * @return a ClassDoc for this element's containing class. If this is a class with no outer class, return null.
   */
  public ClassDoc containingClass()
  {
    return parentClass;
  }
  
  /**
   * Get the modifier specifier integer.
   * @see java.lang.reflect.Modifier
   */
  public int modifierSpecifier() // might be overwritten e.g. for faking public classes (see ClassDocImpl)
  {
    return mod;
  }
  
  /**
   * Get modifiers string.
   * Example, for: public abstract int foo() { ... } modifiers() would return: 'public abstract'
   */
  public String modifiers()
  {
    return Modifier.toString(modifierSpecifier());
  }
  
  /**
   * Return true if this program element is public 
   */
  public boolean isPublic()
  {
    return Modifier.isPublic(modifierSpecifier());
  }
  
  /**
   * Return true if this program element is protected 
   */
  public boolean isProtected()
  {
    return Modifier.isProtected(modifierSpecifier());
  }  
  
  /**
   * Return true if this program element is package private
   */
  public boolean isPackagePrivate()
  {
    return ((!isPrivate())&&(!isProtected())&&(!isPublic()));
  }
  
  /**
   * Return true if this program element is private
   */
  public boolean isPrivate()
  {
    return Modifier.isPrivate(modifierSpecifier());
  }

  /**
   * Return true if this program element is final
   */
  public boolean isFinal()
  {
    return Modifier.isFinal(modifierSpecifier());
  }

  /**
   * Return true if this program element is static
   */
  public boolean isStatic()
  {
    return Modifier.isStatic(modifierSpecifier());
  }
  
  /***************************************************************************
   *
   * partial implementation of interface ExecutableMemeberDoc
   *
   **************************************************************************/
  
  /**
   * Return true if this program element is synchronized
   */
  public boolean isSynchronized()
  {
    return Modifier.isSynchronized(modifierSpecifier());
  }
  
  /**
   * Return true if this program element is native
   */
  public boolean isNative()
  {
    return Modifier.isNative(modifierSpecifier());
  }  
  
  /***************************************************************************
   *
   * partial implementation of interface MethodDoc
   *
   **************************************************************************/
  
  /**
   * Return true if this program element is abstract
   */
  public boolean isAbstract()
  {
    return Modifier.isAbstract(modifierSpecifier());
  }
  
  /***************************************************************************
   *
   * partial implementation of interface FieldDoc
   *
   **************************************************************************/
  
  /**
   * Return true if this program element is volatile
   */
  public boolean isVolatile()
  {
    return Modifier.isVolatile(modifierSpecifier());
  }
  
  /**
   * Return true if this program element is transient
   */
  public boolean isTransient()
  {
    return Modifier.isTransient(modifierSpecifier());
  }  
}
