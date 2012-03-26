/* 
 * This program is licensed under Common Public License Version 0.5.
 *
 * For License Information and conditions of use, see "LICENSE" in packaged
 * 
 */
package org.jtestcase.core.type;

/**
 * Exception for errors during type conversion.
 * 
 * @author <a href="mailto:fausto_lelli@hotmail.com">Fausto Lelli</a>
 * 
 * $Id: TypeConversionException.java,v 1.2 2005/11/05 11:21:47 faustothegrey Exp $
 */
public class TypeConversionException 
extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @see java.lang.Exception#Exception() 
	 */
	public TypeConversionException() {
		super();
	}

	/**
     * @see java.lang.Exception#Exception(java.lang.Throwable) 
	 */
    public TypeConversionException(Throwable e)
	{
		super(e);
	}
	
    /**
     * @see java.lang.Exception#Exception(java.lang.String) 
     */
	public TypeConversionException(String message){
		super(message);
	}
}
