/* 
 * This program is licensed under Common Public License Version 0.5.
 *
 * For License Information and conditions of use, see "LICENSE" in packaged
 * 
 */
package org.jtestcase.core.type;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Locale;

import org.jtestcase.core.model.AbstractType;


/**
 * Helper class for type conversions.
 * 
 * @author <a href="mailto:ckoelle@sourceforge.net">Christian Koelle</a>
 * 
 * $Id: TypeConverter.java,v 1.3 2005/10/28 19:46:26 faustothegrey Exp $
 */
public class TypeConverter {

	/**
	 * Helper class for type aliases
	 */
	private TypeAliases typeAliases = new TypeAliases();

	/**
	 * Locale used for date and time conversions
	 */
	private Locale locale = null;

	/**
	 * Sets the locale for date and time conversions
	 * 
	 * @param locale
	 *            the locale
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	/**
	 * Converts a string value into an object of the given type.
	 * 
	 * @param value
	 *            value string
	 * @param type
	 *            type string
	 * @return the constructed object
	 * @throws TypeConversionException
	 *             in case of any errors
	 */
	protected Object _convertType(String value, String type)
			throws TypeConversionException {
		try {
			return createSimpleTypeInstance(value, type);
		} catch (Exception e) {
			throw new TypeConversionException(e);
		}
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
	 */
	protected Object _convertType(AbstractType typeInstance, String type)
			throws TypeConversionException {
		try {
			return objectMap(typeInstance, type);
		} catch (Exception e) {
			throw new TypeConversionException(e);
		}
	}

	/**
	 * Converts a complex type in an assert or param instance into an object.
	 * 
	 * @param typeInstance
	 *            assert or param instance
	 * @return the constructed object
	 * @throws TypeConversionException
	 *             in case of any errors
	 */
	protected Object _convertType(AbstractType typeInstance)
			throws TypeConversionException {

		return _convertType(typeInstance, "");
	}

	/**
	 * Converts a complex type in an assert or param instance into an object of
	 * the given type.
	 * 
	 * @param abstractType
	 *            assert or param instance
	 * @param type
	 *            type string
	 * @return the constructed object
	 * @throws TypeConversionException
	 *             in case of any errors
	 */
	private Object objectMap(AbstractType abstractType, String type)
			throws TypeConversionException

	{
		if ("".equals(type))
			type = abstractType.getType();

		// TODO: review login behind this 
		// if this is not a simple type return null
		if (!typeAliases.isSimpleType(type))
			return null;

		String val = abstractType.getContent();

		try {
			return createSimpleTypeInstance(val, type);
		} catch (ParseException e) {
			throw new TypeConversionException(e);
		}
	}

	/**
	 * Converts a string value into an object of the given type.
	 * 
	 * @param val
	 *            value string
	 * @param type
	 *            type string
	 * @return the constructed object
	 * @throws ParseException
	 *             in case of errors in parsing the string value
	 * @throws TypeConversionException
	 *             in case of any errors
	 */
	private Object createSimpleTypeInstance(String val, String type)
			throws ParseException, TypeConversionException {
		Object newVal = null;

		DateFormat lDateFormat = null;
		DateFormat lTimeFormat = null;

		if (locale != null) {
			lDateFormat = DateFormat.getDateInstance(DateFormat.SHORT, locale);
			lTimeFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT,
					DateFormat.DEFAULT, locale);
		} else {
			lDateFormat = DateFormat.getDateInstance();
			lTimeFormat = DateFormat.getDateTimeInstance();
		}

		String concreteType;
		try {
			concreteType = typeAliases.getType(type);
		} catch (TypeConversionException e) {
			throw new TypeConversionException("Not type alias for " + type);
		}

		if (concreteType.equals(TypeAliases.INTEGER)) {
			try {
				newVal = new Integer(val);
			} catch (NumberFormatException e) {
				throw new TypeConversionException(
						"Error conveting to integer: " + type);
			}
		} else if (concreteType.equals(TypeAliases.SHORT)) {
			try {
				newVal = new Short(val);
			} catch (NumberFormatException e) {
				throw new TypeConversionException("Error converting to short: "
						+ type);
			}
		} else if (concreteType.equals(TypeAliases.LONG)) {
			try {
				newVal = new Long(val);
			} catch (NumberFormatException e) {
				throw new TypeConversionException("Error converting to long: "
						+ type);
			}
		} else if (concreteType.equals(TypeAliases.CHAR)) {
			newVal = new Character(val.charAt(0));
		} else if (concreteType.equals(TypeAliases.BYTE)) {
			try {
				newVal = new Byte(val);
			} catch (NumberFormatException e) {
				throw new TypeConversionException("Error converting to byte: "
						+ type);
			}
		} else if (concreteType.equals(TypeAliases.DOUBLE)) {
			try {
				newVal = new Double(val);
			} catch (NumberFormatException e) {
				throw new TypeConversionException(
						"Error converting to double: " + type);
			}
		} else if (concreteType.equals(TypeAliases.FLOAT)) {
			try {
				newVal = new Float(val);
			} catch (NumberFormatException e) {
				throw new TypeConversionException(
						"Error converting to float : " + type);
			}
		} else if (concreteType.equals(TypeAliases.BOOLEAN)) {
			admittedValuesForBoolean(val);
			newVal = new Boolean(val);
		} else if (concreteType.equals(TypeAliases.DATE)) {
			try {
				newVal = lDateFormat.parse(val);
			} catch (ParseException e) {
				throw new TypeConversionException("Error parsing date: " + type);
			}
		} else if (concreteType.equals(TypeAliases.TIME)) {
			try {
				newVal = lTimeFormat.parse(val);
			} catch (ParseException e) {
				throw new TypeConversionException("Error parsing timedate : "
						+ type);
			}
		} else if (concreteType.equals(TypeAliases.STRING)) {
			if (val.equals(TypeAliases.STRING_EMPTY)) {
				newVal = "";
			} else if (val.equals(TypeAliases.STRING_SPACE)) {
				newVal = " ";
			} else {
				// default string type to literal value
				newVal = val;
			}
		}
		return newVal;

	}

	private void admittedValuesForBoolean(String booleanValue)
			throws TypeConversionException
	// admitted value for boolean types are false,true (ignoring case)
	// all else throws exception
	{
		if ("false".equalsIgnoreCase(booleanValue)
				|| "true".equalsIgnoreCase(booleanValue))
			return;
		else
			throw new TypeConversionException("Error converting to boolean : "
					+ booleanValue);
	}

}
