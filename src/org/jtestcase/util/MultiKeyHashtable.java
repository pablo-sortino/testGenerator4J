/* 
* This program is licensed under Common Public License Version 0.5.
*
* For License Information and conditions of use, see "LICENSE" in packaged
* 
*/

package org.jtestcase.util;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

/**
* This hashtable stores values using string array.
* @author <a href="mailto:yuqingwang_99@yahoo.com">Yuqing Wang</a>
* @author <a href="mailto:christian.koelle@bluewin.ch">Christian Kölle</a>
*/
public class MultiKeyHashtable {
    
   /**
    * The token for the composed key
    */
   protected static final String _hashToken = "[&]";

   /**
    * The map with all values
    */
   protected final Hashtable _map = new Hashtable();

   /**
    *  This method retrieves the value at String array keys.
    *  @param keys - String array, must not be null
    *  @exception NullPointerException thrown if keys are null
    *  @return Object the value or null if not found
    */
    public Object get( String[] keys ) {
        if( keys == null ) {
            throw new NullPointerException("null keys");
        }

        if (keys.length == 0) {
            throw new NullPointerException("no element in keys");
        }
 
        // System.out.println("-----get Key string: " + _composeHashKey(keys));
        return _map.get( _composeHashKey(keys) );
    }
    
    /**
    *  This method stores the value at String array keys.
    *  @param keys - must not be null
    *  @param value
    *  @exception NullPointerException thrown if key1 or key2 are null
    */
    public void put( String[] keys, Object value ) {
        if( keys == null ) {
            throw new NullPointerException("null keys");
        }

        if (keys.length == 0) {
            throw new NullPointerException("no element in keys");
        }
 
        // System.out.println("-----Put Key string: " + _composeHashKey(keys));
        _map.put(_composeHashKey(keys), value );
    }
    
    /**
     * Copies all of the mappings from the specified MultiKeyHashtable to 
     * this MultiKeyHashtable. 
     * These mappings will replace any mappings that this MultiKeyHashtable had 
     * for any of the keys currently in the specified MultiKeyHashtable. 
     * @param pHash The MultiKeyHashtable to be copied.
     */
    public void putAll(MultiKeyHashtable pHash) {
        Iterator keyIter = pHash.keySet().iterator();
        while (keyIter.hasNext()) {
            String[] keys = (String[]) keyIter.next();
            this.put(keys, pHash.get(keys));
        }
    }
    

    /**
    * Reset contents. Equals to Map::clear().
    */
    public void reset() {
            _map.clear();
    }

    /**
     * Returns an enumeration with the keys.
     * @return The keys in an Enumeration
     */
    public Set keySet() {
        HashSet keys = new HashSet();
        for (Enumeration e = _map.keys(); e.hasMoreElements();) {
            keys.add(_decomposeHashKey((String)e.nextElement()));
            System.out.println();
        }
        return keys;
    }
    
    /**
    * Given a String array, compose a String value that follows the style of hashkey used in
    * getNodeValuesHashedByNamedAttrs().
    * @param keys the array with the keys
    * @return the composed key
    */
    protected String _composeHashKey(String[] keys) {
         String key = "";

         for (int i=0; i<keys.length; i++) {
             key += _hashToken + keys[i];
         }

         return key;
    }

    /**
     * Given a String, create a string array with the keys as elements
     * @param keys the composed key
     * @return the array of individual keys
     */
     protected String[] _decomposeHashKey(String keys) {
         if (keys != null && keys.length()>0) {
             keys = keys.substring(3);
             Vector keyEnum = new Vector();
             boolean moreKeys = true;
             int i = 0;
             int end = 0;
             while (moreKeys) {
                 if (keys.indexOf(_hashToken) > 0) {
                     end = keys.indexOf(_hashToken);
                     keyEnum.add(i, keys.substring(0, end));
                     keys = keys.substring(end + 3);
                     i++;
                 } else {
                     keyEnum.add(i, keys);
                     moreKeys = false;
                 }
             }
             String[] allKeys = new String[keyEnum.size()];
             for (int j = 0; j < keyEnum.size(); j++) {
                 allKeys[j] = (String) keyEnum.get(j);
             }
             return allKeys;
         }
         return null;
     }
     
}
