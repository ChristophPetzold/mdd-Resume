/**
 * 
 */
package model.interpreter.custom;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * TODO add type description
 * 
 * @author "Christoph Petzold"
 * 
 */
public class Messages {
	public static final String		BUNDLE_NAME		= "model.interpreter.custom.messages";		//$NON-NLS-1$

	private static ResourceBundle	resourceBundle	= ResourceBundle.getBundle(BUNDLE_NAME);

	private Messages() {
	}

	public static String getString(String key) {
		try {
			return resourceBundle.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}

	/**
	 * @param resourceBundle
	 *            the resourceBundle to set
	 */
	public static void setResourceBundle(ResourceBundle newResourceBundle) {
		resourceBundle = newResourceBundle;
	}
}
