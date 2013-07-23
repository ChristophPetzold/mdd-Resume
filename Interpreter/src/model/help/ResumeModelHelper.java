package model.help;

import java.util.Collection;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.isis.gme.bon.JBuilderAtom;
import org.isis.gme.bon.JBuilderModel;
import org.isis.gme.bon.JBuilderObject;

/**
 * 
 */

/**
 * The helper class provides some 'helpful' static methods, e.g. output-methods.
 * 
 * @author Christoph Petzold
 */
public class ResumeModelHelper {

	/**
	 * Output-Methods
	 ***********************************************************************/

	/**
	 * Popping up a message window, containing the Name attributes of the objects within the given vector.
	 * 
	 * @param vector
	 *            containing the objects to be displayed
	 * @param title
	 *            of the Pop up window
	 * */
	@SuppressWarnings({ "rawtypes", "unused" })
	public static void popUpVector(Collection vector, String title) {
		String msg = "";

		for (Object model : vector) {
			msg += "\n- " + ((JBuilderObject) model).getName();
		}

		popup(msg, title);
	}

	/**
	 * Popping up a window with the given message.
	 * 
	 * @param msg
	 *            to be displayed
	 * @param title
	 *            of the Pop up window
	 */
	public static void popup(String msg, String title) {
		JOptionPane.showMessageDialog(null, msg, title, JOptionPane.PLAIN_MESSAGE);
	}

	public static void err(String msg, String title) {
		JOptionPane.showMessageDialog(null, msg, title, JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Attribute extraction
	 ***********************************************************************/

	/**
	 * @param modelObject
	 * @throws AttributeNotFoundException
	 */
	public static String assignStringAttribute(JBuilderObject modelObject, String attributeName)
			throws AttributeNotFoundException {
		String retVal = modelObject.getStringAttribute(attributeName);

		if (retVal == null) {
			throw new AttributeNotFoundException(attributeName);
		}

		return retVal;
	}

	/**
	 * @param modelObject
	 * @throws AttributeNotFoundException
	 */
	public static int assignIntAttribute(JBuilderObject modelObject, String attributeName)
			throws AttributeNotFoundException {

		int[] ia = new int[1];

		if (!modelObject.getAttribute(attributeName, ia)) {
			throw new AttributeNotFoundException(attributeName);
		}

		return ia[0];
	}

	/**
	 * @return null if there are no atoms (with the given name) assigned to the model
	 * */
	public static JBuilderAtom getSingleAtom(JBuilderModel model, String name) {
		JBuilderAtom atom = null;

		Vector<JBuilderAtom> v = model.getAtoms(name);

		if (v.size() > 0) {
			atom = v.get(0);
		}

		return atom;
	}

	/**
	 * @return null if there are no atoms (with the given name) assigned to the model
	 * */
	public static JBuilderModel getSingleModel(JBuilderModel model, String name) {
		JBuilderModel subModel = null;

		Vector<JBuilderModel> v = model.getModels(name);

		if (v.size() > 0) {
			subModel = v.get(0);
		}

		return subModel;
	}

}
