package generator.classdoc;
/*
 * Project: classdoc
 * File:    ClassDocImpl.java
 *
 * Created on 1. April 2001, 12:25
 */

import com.sun.javadoc.*;
import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.Type;
import com.sun.javadoc.TypeVariable;
import com.sun.javadoc.WildcardType;

import generator.SimpleLogger;

import java.lang.reflect.*;
import java.util.*;

/**
 * Class and type documentation.
 *
 * @author Jens Gulden - <a href="http://www.jensgulden.de/" target="_top">www.jensgulden.de</a>
 * @version 1.0
 *
 * @see ExecutableMemberDocImpl#getUsedClasses(java.util.Hashtable) on details about this implementing interface Type
 */
class ClassDocImpl extends ProgramElementDocImpl implements ClassDoc, Type
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
   * Create a ClassDoc that describes a class. If previously an instance for
   * the same class has been created, this instance is returned again.
   */
  static ClassDocImpl get(Class c)
  {
    if (c!=null) // allow returning null, caller doesn't need to validate c
    {
      return get(getClassName(c));
    }
    else
    {
      return null;
    }
  }

  /**
   * Create a ClassDoc that describes an inner class of class <code>outer</code>.
   * <code>classname</code> must be fully qualified. If previously an instance for
   * the same class has been created, this instance is returned again.
   */
  static ClassDocImpl get(String classname,ClassDocImpl outer) // in place of a constructor
  {
    ClassDocImpl cd=get(classname);
    cd.outerClassDoc=outer; // remember outer class
    return cd;
  }

  /**
   * Create a ClassDoc that describes a class. <code>classname</code>
   * must be fully qualified. If previously an instance for
   * the same class has been created, this instance is returned again.
   */
  static ClassDocImpl get(String classname) // in place of a constructor
  {
    ClassDocImpl p=(ClassDocImpl)buffer.get(classname);
    if (p==null)
    {
      p=new ClassDocImpl(); // only place where instaces are actually created
      buffer.put(classname,p); // must be before p.init() !
      p.initClassDoc(classname);
    }
    return p;
  }

  /**
   * Create an array of ClassDocs that describes the classes
   * in the class array, in the same order.
   * If previously an instance for a class has been created,
   * this instance is returned in the array.
   */
  static ClassDocImpl[] get(Class[] c)
  {
    ClassDocImpl[] cd=new ClassDocImpl[c.length];
    for (int i=0;i<c.length;i++)
    {
      cd[i]=get(c[i]);
    }
    return cd;
  }

  /**
   * Return all instances of ClassdocImpl that have been created.
   */
  static synchronized ClassDocImpl[] getAll()
  {
    ClassDocImpl[] cd=new ClassDocImpl[buffer.size()];
    int i=0;
    for (Enumeration e=buffer.elements();e.hasMoreElements();)
    {
      cd[i]=(ClassDocImpl)e.nextElement();
      i++;
    }
    return cd;
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
  private ClassDocImpl()
  {
    //nop
  }

  /***************************************************************************
   *
   * Instance variables
   *
   **************************************************************************/

  /**
   * The class for which this is the corresponding documentation.
   * This may be null if the class could not be loaded, in this case
   * _this_ object is still valid but generates an empty dummy documentation only.
   */
  private Class _class;

  /**
   * If this object represents the documentation of a class that is another's inner class,
   * the corresponding documentation is referenced by outerClassDoc.
   */
  private ClassDocImpl outerClassDoc=null; // might be set when outer class's doc initializes

  /**
   * Dimension of the class described by this.
   * 1 means a 1-dimensional array (XY[]), 2 a 2-dim. array (XY[][]) and so on.
   */
  private int dimension;

  /**
   * The documentation of the package to which the documented class belongs to.
   */
  private PackageDocImpl packageDoc;

  /**
   * The documented fields of this documentation's class.
   */
  private FieldDocImpl[] fields;

  /**
   * The documented constructors of this documentation's class.
   */
  private ConstructorDocImpl[] constructors;

  /**
   * The documented methods of this documentation's class.
   */
  private MethodDocImpl[] methods;

  /**
   * The documented inner classes of this documentation's class.
   */
  private ClassDocImpl[] innerClasses;

  /**
   * Again the documented inner classes of this documentation's class,
   * referencable by name.
   */
  private Hashtable classesHash;

  /**
   * The documentations of the interfaces that this documentation's class implements.
   */
  private ClassDocImpl[] interfaces;


  /***************************************************************************
   *
   * Initializer
   *
   **************************************************************************/

  /**
   * Initializes this ClassDocImpl so that it represents documentation
   * for the named class. the classname must be fully qualified.
   * Arrays are marked by appending as many '[]' as the array has dimensions.
   */
  private void initClassDoc(String classname)
  {
    // find out dimension
    dimension=0;
    while (classname.endsWith("[]"))
    {
      dimension++;
      classname=classname.substring(0,classname.length()-2);
    }

    _class = getPrimitiveClass(classname); // is it a primitive class?

    if (_class==null) // not primitive, load class
    {
      try
      {
        _class=Class.forName(classname); // surely 0-dimensional ([] have been removed above)
      	//classdoc.printWarningS("The class is "+classname);
	}
      catch (Throwable t)
      {
        // class cannot be found or loaded
    	  SimpleLogger.printWarningS("cannot load class "+classname+" - using dummy");
        _class=null; // mark this as invalid
      }
     //System.out.println("SpikeTestGen Warning: Classdoc cannot load Null Class "+classname);
    }

    // get modifiers of class
    int mod=0;
    if (_class!=null)
    {
      try
      {
        mod=_class.getModifiers();
      }
      catch (Throwable t) // e.g. SecurityExecption
      {
        _class=null; // mark this as invalid
      }
    }

    // initialize superclass DocImpl
    if (_class!=null)
    {
      name=classname; // ! HACK: in order to be able to at least guess an outer class's name in containingClass()
                      // if an error occurs while accessing it, name must be initialized here
      super.init(classname,(ClassDocImpl)containingClass(),mod);
    }
    else
    {
      super.init(classname,null,0);
    }

    // initialize this

    // get containing package
    String packageName=DocletImpl.packageName(qualifiedName());
    packageDoc=PackageDocImpl.get(packageName);
    if ((packageName.length()>0)||!isPrimitive()) // if package is "", avoid primitive types to appear there
    {
      packageDoc.registerClass(this); // register this as member of package
    }

    if ((_class!=null)&&(dimension==0))
    {
      // get fields
      Field[] f=_class.getDeclaredFields();
      Vector fieldsV=new Vector();
      for (int i=0;i<f.length;i++)
      {
        if (DocletImpl.matchModifiers(f[i].getModifiers()))
        {
          FieldDocImpl field=new FieldDocImpl();
          field.init(f[i],this);
          fieldsV.addElement(field);
        }
      }
      fields=new FieldDocImpl[fieldsV.size()];
      fieldsV.copyInto(fields);

      // get constructors
      Constructor[] cc=_class.getDeclaredConstructors();
      Vector constructorsV=new Vector();
      for (int i=0;i<cc.length;i++)
      {
        if (DocletImpl.matchModifiers(cc[i].getModifiers()))
        {
          ConstructorDocImpl constructor=new ConstructorDocImpl();
          constructor.init(cc[i],this);
          constructorsV.addElement(constructor);
        }
      }
      constructors=new ConstructorDocImpl[constructorsV.size()];
      constructorsV.copyInto(constructors);

      // get methods
      Method[] mm=_class.getDeclaredMethods();
      Vector methodsV=new Vector();
      for (int i=0;i<mm.length;i++)
      {
        if (DocletImpl.matchModifiers(mm[i].getModifiers()))
        {
          MethodDocImpl method=new MethodDocImpl();
          method.init(mm[i],this);
          methodsV.addElement(method);
        }
      }
      methods=new MethodDocImpl[methodsV.size()];
      methodsV.copyInto(methods);

      // get inner classes
      Class[] cl=_class.getDeclaredClasses();
      Vector innerClassesV=new Vector();
      classesHash=new Hashtable();
      for (int i=0;i<cl.length;i++)
      {
        if (DocletImpl.matchModifiers(cl[i].getModifiers()))
        {
          String cn=getClassName(cl[i]);
          ClassDocImpl innerClass=get(cn,this); // inner class with this as outer class
          innerClassesV.addElement(innerClass);
          classesHash.put(cn,innerClass);
        }
      }
      innerClasses=new ClassDocImpl[innerClassesV.size()];
      innerClassesV.copyInto(innerClasses);

      // get implemented interfaces
      cl=_class.getInterfaces();
      interfaces=new ClassDocImpl[cl.length];
      for (int i=0;i<cl.length;i++)
      {
        interfaces[i]=get(cl[i]);
      }
    }
    else // class is an array or is not valid
    {
      fields=new FieldDocImpl[0];
      constructors=new ConstructorDocImpl[0];
      methods=new MethodDocImpl[0];
      innerClasses=new ClassDocImpl[0];
      interfaces=new ClassDocImpl[0];
    }
  }
  

  /**
   * Get a primitive class by name.
   */
  public static Class getPrimitiveClass(String classname)
  {
    Class _class;
    // get class (of base-dimension)
    if (classname.equals("void"))
    {
      _class=void.class;
    }
    else if (classname.equals("int"))
    {
      _class=int.class;
    }
    else if (classname.equals("char"))
    {
      _class=char.class;
    }
    else if (classname.equals("short"))
    {
      _class=short.class;
    }
    else if (classname.equals("long"))
    {
      _class=long.class;
    }
    else if (classname.equals("boolean"))
    {
      _class=boolean.class;
    }
    else if (classname.equals("byte"))
    {
      _class=byte.class;
    }
    else if (classname.equals("float"))
    {
      _class=float.class;
    }
    else if (classname.equals("double"))
    {
      _class=double.class;
    }
    else
    {
      _class=null;
    }
    return _class;
  }

  /**
   * Fill the hashtable with documentations of those classes that are used by this.
   * Being 'used' means here that a class appears as a field-type or method-return-type
   * or as a method/constructor parameter-type or as a thrown exception or as an implemented interface.
   */
  protected void getUsedClasses(Hashtable h) // overwrites ProgramElementDocImpl.getUsedClasses()
  { // all classes inside code are ignored - this is not really all used classes
    // (but all used in the interfaces of the program elements)
    if (_class!=null)
    {
      // get used classes from fields
      FieldDocImpl[] f=fields;
      for (int i=0;i<f.length;i++)
      {
        f[i].getUsedClasses(h);
      }
      // get constructors
      ConstructorDocImpl[] c=constructors;
      for (int i=0;i<c.length;i++)
      {
        c[i].getUsedClasses(h);
      }
      // get methods
      MethodDocImpl[] m=methods;
      for (int i=0;i<m.length;i++)
      {
        m[i].getUsedClasses(h);
      }
      // get inner classes
      ClassDocImpl[] cl=innerClasses;
      for (int i=0;i<cl.length;i++)
      {
        cl[i].getUsedClasses(h);
      }
      // get implemented interfaces
      cl=interfaces;
      for (int i=0;i<cl.length;i++)
      {
	//System.out.println("calvin: interface"+cl[i].qualifiedName());
        h.put(cl[i].qualifiedName(),DocletImpl.DUMMY);
      }
    }
  }
  /**
   * Test if the class documented by this is a primitive class.
   */
  public boolean isPrimitive()
  {
    return (_class!=null)&&(_class.isPrimitive());
  }

  /***************************************************************************
   *
   * implementation of interface ClassDoc
   *
   **************************************************************************/

  /**
   * Get the fully qualified name.
   * Example: for the class java.util.Hashtable, return: java.util.Hashtable
   *          for the method bar() in class Foo in the unnamed package, return: Foo.bar()
   * @return the qualified name of the program element as a String.
   */
  public String qualifiedName() // overwrites DocImpl.qualifiedName()
  {
    return name;
  }

  /**
   * Returns the name of this Doc item.
   * @return the name
   */
  public String name() // overwrites DocImpl.qualifiedName()
  {
    String s=DocletImpl.unqualify(name);
    // if class could not be loaded, mark as dummy
    if (_class==null)
    {
      s+=" (dummy)";
    }
    return s;
  }

  /**
   * Return fields in class.
   * @return an array of FieldDoc for representing the visible fields in this class.
   */
  public FieldDoc[] fields()
  {
    return fields;
  }

  /**
   * Return inner classes within this class.
   * @return an array of ClassDoc for representing the visible classes defined in this class. Anonymous and local classes are not included.
   */
  public ClassDoc[] innerClasses()
  {
    return innerClasses;
  }

  /**
   * Return methods in class.
   * @return an array of MethodDoc for representing the visible methods in this class. Does not include constructors.
   */
 
 public ClassDoc[] innerClasses(boolean iC){ClassDoc[] ic = new ClassDoc[0]; return ic;}

 public ConstructorDoc[] constructors(boolean c){ ConstructorDoc[] cd = new ConstructorDoc[0]; return cd;}

 public FieldDoc[] fields(boolean f){FieldDoc[] fd=new FieldDoc[0]; return fd;} 

 public MethodDoc[] methods(boolean m){MethodDoc[] md=new MethodDoc[0];return md;} 

 public MethodDoc[] methods()
  {
    return methods;
  }

  /**
   * Return constructors in class.
   * @return an array of ConstructorDoc for representing the visible constructors in this class.
   */
  public ConstructorDoc[] constructors()
  {
    return constructors;
  }

  /**
   * Return interfaces implemented by this class or interfaces extended
   * by this interface. Includes only directly declared interfaces, not
   * inherited interfaces.
   * @return An array of ClassDoc representing the interfaces. Return an empty array if there are no interfaces.
   */
  public ClassDoc[] interfaces()
  {
    return interfaces;
  }

  /**
   * Return true if this class implements java.io.Externalizable .
   */
  public boolean isExternalizable()
  {
    if (_class!=null)
    {
      return java.io.Externalizable.class.isAssignableFrom(_class);
    }
    else
    {
      return false;
    }
  }

  /**
   * Return true if this class implements java.io.Serializable.
   * Since java.io.Externalizable extends java.io.Serializable, Externalizable objects are also Serializable.
   */
  public boolean isSerializable()
  {
    if (_class!=null)
    {
      return java.io.Serializable.class.isAssignableFrom(_class);
    }
    else
    {
      return false;
    }
  }

  /**
   * Return true if Serializable fields are explicitly defined with the special class member serialPersistentFields.
   * @see #serializableFields()
   * @see SerialFieldTag
   */
  public boolean definesSerializableFields()
  {
    return false; // cannot find out without source (or yes?)
  }

  /**
  * Return the Serializable fields of class. Return either a list of default fields documented by
   * serial tag or return a single FieldDoc for serialPersistentField member.
   * There should be a serialField tag for each Serializable field defined by an ObjectStreamField array component of serialPersistentField.
   * @see #definesSerializableFields()
   * @see SerialFieldTag
   */
  public FieldDoc[] serializableFields()
  {
    return new FieldDoc[0]; // cannot find out without source (or yes?)
  }

  /**
   * Return the serialization methods for this class.
   * @return an array of MethodDoc that represents the serialization methods for this class.
   */
  public MethodDoc[] serializationMethods()
  {
    return new MethodDoc[0]; // cannot find out without source (or yes?)
  }

  /**
   * Get the list of packages declared as imported.
   * These are called "type-import-on-demand declarations" in the JLS.
   * @return an array of PackageDoc representing the imported packages.
   */
  public PackageDoc[] importedPackages()
  {
    Hashtable h=new Hashtable();
    ClassDoc[] cd=importedClasses();
    //System.out.println("Hashtable size on top: "+h.size()+" Number of Classes: "+cd.length);
    for (int i=0;i<cd.length;i++)
    {
      h.put(cd[i].containingPackage(),DocletImpl.DUMMY); // auto-avoid doubles by using hashtable
    }
    // ready to copy results into array
    PackageDocImpl[] r=new PackageDocImpl[h.size()];
    //System.out.println("Table size "+h.size());
    int i=0;
    for (Enumeration e=h.keys();e.hasMoreElements();)
    {
      Object ob = e.nextElement();
      //System.out.println("PackageDoc[] importedPackages() "+ob.toString());
      PackageDocImpl pd=(PackageDocImpl)ob;
      //PackageDocImpl pd=(PackageDocImpl)e.nextElement();
      r[i++]=pd;
    }
    return r;
  }

  /**
   * Get the list of classes declared as imported.
   * These are called "single-type-import declarations" in the JLS.
   * @return an array of ClassDoc representing the imported classes.
   */
  public ClassDoc[] importedClasses()
  {
    // do not actually return imported classes but return all external used classes
    Hashtable h=new Hashtable(); // use Hashtable as buffer (entries stay dummies) - auto-avoid doubles
    getUsedClasses(h);
    // remove self from used class list (if in there)
    h.remove(this);
    // remove inner classes from used class list (if in there)
    ClassDoc[] cl=innerClasses();
    for (int i=0;i<cl.length;i++)
    {
      h.remove(cl[i]);
    }
    // remove outer classes from used class list (if in there)
    ClassDocImpl outer=outerClassDoc;
    while (outer!=null)
    {
      h.remove(outer);
      outer=outer.outerClassDoc;
    }

    for (Enumeration ef=h.keys();ef.hasMoreElements();)
    {
      Object name=ef.nextElement();
      if (h.get(name) == DocletImpl.DUMMY)
      {
        h.remove(name);
      }
    }

    // remove all primitive types
    for (Enumeration e=h.keys();e.hasMoreElements();)
    {
      ClassDocImpl cd=(ClassDocImpl)e.nextElement();
      if (cd.isPrimitive())
      {
        h.remove(cd);
      }
    }
    // ready to copy results into array
    ClassDocImpl[] r=new ClassDocImpl[h.size()];
    int i=0;
    for (Enumeration e=h.keys();e.hasMoreElements();)
    {
      ClassDocImpl cd=(ClassDocImpl)e.nextElement();
      r[i++]=cd;
    }
    return r;
  }

  /**
   * Find a class within the context of this class.
   *  Search order: qualified name, in this class (inner),
   *  in this package, in the class imports, in the package
   *  imports.
   *  Return the ClassDoc if found, null if not found.
   */
  public ClassDoc findClass(String s)
  {
    return (ClassDoc)classesHash.get(s);
  }

  /**
   * Return the superclass of this class.
   * @return the ClassDoc for the superclass of this class, null if there is no superclass.
   */
  public ClassDoc superclass()
  {
    if (_class!=null)
    {
      Class sup=_class.getSuperclass();
      if (sup!=null)
      {
        return get(sup);
      }
      else
      {
        return null;
      }
    }
    else
    {
      return null;
    }
  }

  /**
   * Test whether this class is a subclass of the specified class.
   * @param cd - the candidate superclass.
   * @return if cd is a superclass of this class.
   */
  public boolean subclassOf(ClassDoc cd)
  {
    ClassDoc superclass=superclass();
    if (superclass!=null)
    {
      return (cd.equals(superclass))
           ||superclass.subclassOf(cd);
    }
    else
    {
      return false;
    }
  }

  /***************************************************************************
   *
   * implementation of interface Type
   *
   **************************************************************************/

  /**
   * Return unqualified name of type excluding any dimension information.
   * For example, a two dimensional array of String returns 'String'.
   */
  public String typeName()
  {
    return DocletImpl.unqualify(qualifiedTypeName());
  }

  /**
   * Return this type as a class. Array dimensions are ignored.
   * @return a ClassDoc if the type is a Class. Return null if it is a primitive type.
   */
  public ClassDoc asClassDoc()
  {
    return ClassDocImpl.get(qualifiedTypeName());
  }

  /**
   * Return the type's dimension information, as a string.
   *  For example, a two dimensional array of String returns '[][]'.
   */
  public String dimension()
  {
    return DocletImpl.repeat("[]",dimension);
  }

  /**
   * Return qualified name of type excluding any dimension information.
   * For example, a two dimensional array of String returns 'java.lang.String'.
   */
  public String qualifiedTypeName()
  {
    return qualifiedName();
  }

  /**
   * Returns a string representation of the type.
   * Return name of type including any dimension information.
   * For example, a two dimensional array of String returns String[][].
   * @return name of type including any dimension information.
   */
  public java.lang.String toString()
  {
    return typeName()+dimension();
  }

  /***************************************************************************
   *
   * additiional implementation of interface Doc
   *
   **************************************************************************/

  /**
   * Is this Doc item a class.
   * Note: interfaces are not classes.
   * False until overridden.
   * @return true is it represents a class
   */
  public boolean isClass()
  {
    return !isInterface();
  }

  /**
   * Is this Doc item a error class?
   * False until overridden.
   * @return true is it represents a error
   */
  public boolean isError()
  {
    if (_class!=null)
    {
      return Error.class.isAssignableFrom(_class);
    }
    else
    {
      return false;
    }
  }

  /**
   * Is this Doc item a exception class?
   * False until overridden.
   * @return true is it represents a exception
   */
  public boolean isException()
  {
    if (_class!=null)
    {
      return Exception.class.isAssignableFrom(_class);
    }
    else
    {
      return false;
    }
  }

  /**
   * Is this Doc item a ordinary class (i.e. not an interface, exception,
   * or error)?
   * False until overridden.
   * @return true is it represents a class
   */
  public boolean isOrdinaryClass()
  {
    return (!isError())&&(!isException())&&(!isInterface());
  }

  /**
   * Is this Doc item a interface?
   * False until overridden.
   * @return true is it represents a interface
   */
  public boolean isInterface()
  {
    if (_class!=null)
    {
      return _class.isInterface();
    }
    else
    {
      return false;
    }
  }

  /***************************************************************************
   *
   * additional implementation of interface ProgramElementDoc
   *
   **************************************************************************/

  /**
   * Return true if this program element is public.
   * If classdoc is not running in -nofakepublic mode (default),
   * this returns also true if the class matches the chosen
   * modifiers to be documented (-packageprivate/-private/...).
   * This way, the Standard Doclet still generates all member information
   * which is usually ommitted by the Standard Doclet on non-public classes.
   * This does _not_ affect the textual representation of the class's modifier.
   * (I.e. a private class will still appear as 'private class XXX' in the documentation.)<br>
   *
   * Note that ommitting detail information on non-public classes is more or less a
   * 'bug' or an 'unwanted feature' in the Standard Doclet because it is usually up
   * to the implementing application of the doclet API which classes, fields,
   * methods etc. to present to the doclet to let them be documented.
   * So, when using other doclets than the Standard Doclet, you might want to
   * specify '-nofakepublic' on the command line.
   */
  public boolean isPublic() // overwrites ProgramElementDoc.isPublic()
  {
    return DocletImpl.matchModifiers(this.modifierSpecifier());
  }

  /**
   * Get the package that this program element is contained in. Returns: a PackageDoc for this element containing package.
   * If in the unnamed package, this PackageDoc will have the name "".
   */
  public PackageDoc containingPackage() // overwrites ProgramElementDocImpl.containingPackage
  {
    return packageDoc;
  }

  /**
   * Get the containing class of this program element.
   * @return a ClassDoc for this element's containing class. If this is a class with no outer class, return null.
   */
  public ClassDoc containingClass()
  {
    // sometimes outer class has been set explicitly already (during outer class init)
    if (outerClassDoc!=null)
    {
      return outerClassDoc;
    }
    // if not explicitly set, find out
    else if (_class!=null)
    {
      try
      {
        Class outer=_class.getDeclaringClass();
        if (outer!=null)
        {
          return get(outer);
        }
        else
        {
          return null;
        }
      }
      catch (Throwable t) // e.g. ClassIncompatibleChange while getting declaring class
      {
        String outerName=guessOuterClassName(name);
        if (outerName!=null)
        {
          return get(outerName); // this way, at least an invalidated document can be generated
        }
        else
        {
        	SimpleLogger.error("cannot get outer class of class "+qualifiedName());
          return null;
        }
      }
    }
    else
    {
      return null;
    }
  }

  /***************************************************************************
   *
   * Private tools
   *
   **************************************************************************/

  /**
   * Get an outer class's name from this class's name if this contains
   * a '$' in its name. This is usually the case except for some weird
   * byte-code scramblers.
   */
  private static String guessOuterClassName(String n)
  {
    int pos=n.lastIndexOf('$');
    if (pos!=-1)
    {
      return n.substring(0,pos);
    }
    else
    {
      return null;
    }
  }

  /***************************************************************************
   *
   * Package tools
   *
   **************************************************************************/

  /**
   * Get a class's fully qualified class name with '[]' appended for each
   * dimension if the class is an array.
   */
  static String getClassName(Class c)
  {
    int dim=0;
    while (c.isArray())
    {
      dim++;
      c=c.getComponentType();
    }
    String cn=c.getName()+DocletImpl.repeat("[]",dim);
    return cn;
  }

  /**
   * Get a class's fully qualified class name. If the class is an array,
   * only the name of the base-dimension is returned.
   */
  static String getClassNameNoDimensions(Class c)
  {
    while (c.isArray())
    {
      c=c.getComponentType();
    }
    return c.getName();
  }

public FieldDoc[] enumConstants() {
	// TODO Auto-generated method stub
	return null;
}

public Type[] interfaceTypes() {
	// TODO Auto-generated method stub
	return null;
}

public Type superclassType() {
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

@Override
public boolean isAnnotationType() {
	// TODO Auto-generated method stub
	return false;
}

@Override
public boolean isAnnotationTypeElement() {
	// TODO Auto-generated method stub
	return false;
}

@Override
public boolean isEnum() {
	// TODO Auto-generated method stub
	return false;
}

public boolean isEnumConstant() {
	// TODO Auto-generated method stub
	return false;
}

public AnnotationTypeDoc asAnnotationTypeDoc() {
	// TODO Auto-generated method stub
	return null;
}

public ParameterizedType asParameterizedType() {
	// TODO Auto-generated method stub
	return null;
}

public TypeVariable asTypeVariable() {
	// TODO Auto-generated method stub
	return null;
}

public WildcardType asWildcardType() {
	// TODO Auto-generated method stub
	return null;
}

public String simpleTypeName() {
	// TODO Auto-generated method stub
	return null;
}


}
