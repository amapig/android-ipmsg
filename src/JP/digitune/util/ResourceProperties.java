/*
 *	Resource Properties Class
 *  		1997/10/13 (C) Copyright T.Kazawa (Digitune)
 */

package JP.digitune.util;

import java.util.Properties;
import java.util.ResourceBundle;
import java.util.MissingResourceException;

public class ResourceProperties extends Properties {
    ResourceBundle rc = null;

    public String getProperty(String key, String defaultVal) {
	String val = getProperty(key);
	return (val == null) ? defaultVal : val;
    }

    public ResourceProperties() {
	this(null);
    }

    public ResourceProperties(String r) {
	try {
	    rc = ResourceBundle.getBundle(r);
	} catch (MissingResourceException ex) {
	    rc = null;
	}
    }

    public String getProperty(String key)
	throws MissingResourceException {
	String val = (String)super.get(key);
	return ((val == null) && (rc != null)) ? rc.getString(key) : val;
    }
}
