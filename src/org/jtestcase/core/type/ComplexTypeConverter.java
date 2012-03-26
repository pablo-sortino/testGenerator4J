/* 
 * This program is licensed under Common Public License Version 0.5.
 *
 * For License Information and conditions of use, see "LICENSE" in packaged
 * 
 */
package org.jtestcase.core.type;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import org.jdom.output.XMLOutputter;
import org.jicengine.JICException;
import org.jtestcase.core.model.AbstractType;
import org.xml.sax.SAXException;

/**
 * Converter for complex data types.
 * 
 * @author <a href="mailto:fausto_lelli@hotmail.com">Fausto Lelli</a>
 * 
 * $Id: ComplexTypeConverter.java,v 1.5 2005/11/22 09:54:31 faustothegrey Exp $
 */
public class ComplexTypeConverter

{
	/**
	 * Converter for the simple data types
	 */
	TypeConverter simpleTypeConverter;

	/**
	 * Converter for the simple data types
	 */
	JiceDelegator jiceDelegator;

	/**
	 * Standard constructor
	 */
	public ComplexTypeConverter() {
		super();
		simpleTypeConverter = new TypeConverter();
		jiceDelegator = new JiceDelegator();
	}

	/**
	 * Converts a complex type in an assert or param instance into an object.
	 * 
	 * @param typeInstance
	 *            assert or param instance
	 * @return the constructed object
	 * @throws TypeConversionException
	 *             in case of any errors
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public Object _convertType(AbstractType typeInstance)
			throws TypeConversionException, InstantiationException,
			IllegalAccessException {
		return _convertType(typeInstance, "");
	}

	/**
	 * Converts a complex type in an assert or param instance into an object of
	 * the given type.
	 * 
	 * @param typeInstance
	 *            assert or param instance
	 * @param type
	 *            type string
	 * @return the constructed object
	 * @throws TypeConversionException
	 *             in case of any errors
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public Object _convertType(AbstractType typeInstance, String type)
			throws TypeConversionException, InstantiationException,
			IllegalAccessException {

		// if this object should return a null, return a null instance
		// (null instances for simple types are managed by simple type converter

		if (typeInstance.getContent().equals(TypeAliases.OBJECT_NULL))
			return null;

		/*
		 * delegate to jice
		 */
		if (typeInstance.getJice() != null)
			try {
				XMLOutputter printer = new XMLOutputter();
				
				String data = printer.outputString(typeInstance.getJice());
				// string jice header tags
				data = data.substring(6);
				data = data.substring(0,data.length()-7);
				return jiceDelegator.getInstance(data);
			} catch (IOException e1) {

				e1.printStackTrace();
				throw new TypeConversionException(e1);
			} catch (SAXException e1) {

				e1.printStackTrace();
				throw new TypeConversionException(e1);
			} catch (JICException e1) {

				e1.printStackTrace();
				throw new TypeConversionException(e1);
			}

		// if this is a simple type, return the converted type converter
		Object simple = simpleTypeConverter._convertType(typeInstance, type);
		if (simple != null)
			return simple;

		// try to convert the param as a complex type
		Object complex = convertComplexParam(typeInstance, type);

		if (complex != null)
			return complex;

		// there is no match, try a instance the type using reflection
		Object reflection;

		try {
			Class clazz = Class.forName(typeInstance.getType());
			reflection = clazz.newInstance();
		} catch (ClassNotFoundException e) {
			throw new TypeConversionException(e);
		}

		if (reflection != null)
			return reflection;

		// fail miserably ....

		throw new TypeConversionException();

	}

	/**
	 * Converts a complex type in an assert or param instance into an object of
	 * the given type.
	 * 
	 * @param typeInstance
	 *            assert or param instance
	 * @param type
	 *            type string
	 * @return the constructed object
	 * @throws TypeConversionException
	 *             in case of any errors
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public Object convertComplexParam(AbstractType typeInstance, String type)
			throws TypeConversionException, InstantiationException,
			IllegalAccessException {
		Object retval = null;

		if ("".equals(type))
			type = typeInstance.getType();
		if ("java.util.Hashtable".equals(type))
			return convertHashtable(typeInstance);
		if ("java.util.HashMap".equals(type))
			return convertHashMap(typeInstance);
		if ("java.util.Vector".equals(type))
			return convertVector(typeInstance);

		return retval;
	}

	/**
	 * Converts a hashtable in an assert or param instance.
	 * 
	 * @param typeInstance
	 *            assert or param instance
	 * @return the constructed hashtable
	 * @throws TypeConversionException
	 *             in case of any errors
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private Hashtable convertHashtable(AbstractType typeInstance)
			throws TypeConversionException, InstantiationException,
			IllegalAccessException {
		Hashtable hashtable = new Hashtable();

		String key_type = typeInstance.getKey_type();
		String value_type = typeInstance.getValue_type();

		List innerparams = typeInstance.getNestedInstances();
		for (Iterator iter = innerparams.iterator(); iter.hasNext();) {

			AbstractType innerParam = (AbstractType) iter.next();
			// key type will become the key of the hashtable
			Object key = simpleTypeConverter._convertType(innerParam.getName(),
					key_type);

			// the param instance can be determined both on the
			Object value = _convertType(innerParam, value_type);

			// finally, add the two instance to the hasttable
			hashtable.put(key, value);
		}

		return hashtable;
	}

	/**
	 * Converts a hashmap in an assert or param instance.
	 * 
	 * @param typeInstance
	 *            assert or param instance
	 * @return the constructed hashmap
	 * @throws TypeConversionException
	 *             in case of any errors
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private HashMap convertHashMap(AbstractType typeInstance)
			throws TypeConversionException, InstantiationException,
			IllegalAccessException {
		HashMap hashmap = new HashMap();

		String key_type = typeInstance.getKey_type();
		String value_type = typeInstance.getValue_type();

		List innerparams = typeInstance.getNestedInstances();
		for (Iterator iter = innerparams.iterator(); iter.hasNext();) {

			AbstractType innerParam = (AbstractType) iter.next();
			// key type will become the key of the hashtable
			Object key = simpleTypeConverter._convertType(innerParam.getName(),
					key_type);

			// the param instance can be determined both on the
			Object value = _convertType(innerParam, value_type);

			// finally, add the two instance to the hasttable
			hashmap.put(key, value);
		}

		return hashmap;
	}

	/**
	 * Converts a vector in an assert or param instance.
	 * 
	 * @param typeInstance
	 *            assert or param instance
	 * @return the constructed vector
	 * @throws TypeConversionException
	 *             in case of any errors
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private Vector convertVector(AbstractType typeInstance)
			throws TypeConversionException, InstantiationException,
			IllegalAccessException {
		Vector vector = new Vector();

		String key_type = typeInstance.getKey_type();
		String value_type = typeInstance.getValue_type();

		List innerparams = typeInstance.getNestedInstances();
		for (Iterator iter = innerparams.iterator(); iter.hasNext();) {

			AbstractType innerParam = (AbstractType) iter.next();

			// the param instance can be determined both on the
			Object value = _convertType(innerParam, value_type);

			// finally, add the two instance to the hasttable
			vector.add(value);
		}

		return vector;
	}

	/**
	 * Sets the locale for date and time conversions
	 * 
	 * @param locale
	 *            the locale
	 */
	public void setLocale(Locale locale) {
		simpleTypeConverter.setLocale(locale);
	}

}
