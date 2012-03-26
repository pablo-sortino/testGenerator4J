/* 
 * This program is licensed under Common Public License Version 0.5.
 *
 * For License Information and conditions of use, see "LICENSE" in packaged
 * 
 */
package org.jtestcase.core.type;

import java.util.HashMap;

/**
 * Defines all valid type definitions in the JTestCase XML
 * 
 * @author <a href="mailto:fausto_lelli@hotmail.com">Fausto Lelli</a>
 * 
 * $Id: TypeAliases.java,v 1.2 2005/10/28 19:46:26 faustothegrey Exp $
 */
public class TypeAliases {

    /**
     * All aliases in a hash map
     */
	private static HashMap aliases = new HashMap();

	/**
	 * Type "INTEGER"
	 */
	public static final String INTEGER = "integer";

	/**
	 * Type "SHORT"
	 */
	public static final String SHORT = "short";

	/**
	 * Type "CHAR"
	 */
	public static final String CHAR = "char";

	/**
	 * Type "BYTE"
	 */
	public static final String BYTE = "byte";

	/**
	 * Type "FLOAT"
	 */
	public static final String FLOAT = "float";
	
	/**
	 * Type "DOUBLE"
	 */
	public static final String DOUBLE = "double";

	/**
	 * Type "LONG"
	 */
	public static final String LONG = "long";

	/**
	 * Type "BOOLEAN"
	 */
	public static final String BOOLEAN = "boolean";

	/**
	 * Type "STRING"
	 */
	public static final String STRING = "string";

	/**
	 * Type "DATE"
	 */
	public static final String DATE = "date";

	/**
	 * Type "TIME"
	 */
	public static final String TIME = "time";

	/**
	 * Type "STRING_SPACE"
	 */
	public static final String STRING_SPACE = "STRING_SPACE";

	/**
	 * Type "STRING_EMPTY"
	 */
	public static final String STRING_EMPTY = "STRING_EMPTY";

	/**
	 * Type "NULL"
	 */
	public static final String OBJECT_NULL = "OBJECT_NULL";

	/**
	 * Standard constructor
	 *
	 */
	public TypeAliases() {
		setAliases();
	}

	/**
	 * Maps the type definition strings of the JTestCase XML to the type aliases. 
	 */
	private void setAliases() {
		if(aliases.size() == 0);
		aliases.put("java.lang.Integer", INTEGER);
		aliases.put("Integer",INTEGER);
		aliases.put("integer",INTEGER);
		aliases.put("int",INTEGER);
		aliases.put("Int",INTEGER);
		aliases.put("java.lang.Short", SHORT);
		aliases.put("short",SHORT);
		aliases.put("Short",SHORT);
		aliases.put("java.lang.Character", CHAR);
		aliases.put("Character",CHAR);
		aliases.put("character",CHAR);
		aliases.put("char",CHAR);
		aliases.put("Char",CHAR);
		aliases.put("CHAR",CHAR);
		aliases.put("java.lang.Byte", BYTE);
		aliases.put("Byte",BYTE);
		aliases.put("byte",BYTE);
		aliases.put("java.lang.Float",FLOAT);
		aliases.put("Float",FLOAT);
		aliases.put("float",FLOAT);
		aliases.put("java.lang.Double", DOUBLE);
		aliases.put("Double",DOUBLE);
		aliases.put("double",DOUBLE);
		aliases.put("java.lang.Long", LONG);
		aliases.put("Long",LONG);
		aliases.put("long",LONG);
		aliases.put("java.lang.Boolean", BOOLEAN);
		aliases.put("Boolean",BOOLEAN);
		aliases.put("boolean",BOOLEAN);
		aliases.put("java.lang.String", STRING);
		aliases.put("String",STRING);
		aliases.put("string",STRING);
		aliases.put("java.text.Date", DATE);
		aliases.put("java.util.Date", DATE);
		aliases.put("Date",DATE);
		aliases.put("date",DATE);
		aliases.put("time", TIME);
		aliases.put("STRING_EMPTY",STRING_EMPTY);
		aliases.put("STRING_SPACE",STRING_SPACE);

	}

	/**
	 * Gets the type from the alias.
	 * @param alias the alias of a type
	 * @return the type
	 * @throws TypeConversionException if there is no such type
	 */
	public String getType(String alias) throws TypeConversionException {
		if (aliases.containsKey(alias))
			return (String) aliases.get(alias);
		throw new TypeConversionException("there is no alias for type of '"
				+ alias + "'");
	}
	
	/**
	 * Checks if the type is a simple type
	 * @param alias the alias of a type
	 * @return true if it is a simple type
	 */
	public boolean isSimpleType(String alias)
	{
		return aliases.containsKey(alias);
	}
	

}
