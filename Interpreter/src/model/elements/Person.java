package model.elements;

import model.help.AttributeNotFoundException;
import model.help.ResumeModelHelper;
import model.interpreter.IResumeVisitor;

import org.isis.gme.bon.JBuilderModel;
import org.isis.gme.bon.JBuilderObject;

/**
 * ResumeInterpreter
 */

/**
 * @author Christoph Petzold
 */
public class Person extends ResumeElement {

	private final String	ATTRIBUTE_FIRST_NAME	= "FirstName";
	private final String	ATTRIBUTE_MAIDEN_NAME	= "MaidenName";
	private final String	ATTRIBUTE_PICTURE_PATH	= "PicturePath";
	private final String	ATOM_BIRTHDAY			= "Birthday";

	protected String		firstName				= "";
	protected String		maidenName				= "";
	protected String		picturePath				= "";
	protected Date			birthday				= new Date();

	/**
	 * Following the "Visitor Pattern" elements of the Resumé-Domain provide the must-have "Accept Method"
	 * 
	 * @param visitor
	 * */
	public void accept(IResumeVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public void build(JBuilderObject modelObject) throws AttributeNotFoundException {
		super.build(modelObject);

		firstName = ResumeModelHelper.assignStringAttribute(modelObject, ATTRIBUTE_FIRST_NAME);
		maidenName = ResumeModelHelper.assignStringAttribute(modelObject, ATTRIBUTE_MAIDEN_NAME);
		picturePath = ResumeModelHelper.assignStringAttribute(modelObject, ATTRIBUTE_PICTURE_PATH);
		birthday.build((JBuilderObject) ((JBuilderModel) modelObject).getAtoms(ATOM_BIRTHDAY).get(0));
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @return the maidenName
	 */
	public String getMaidenName() {
		return maidenName;
	}

	/**
	 * @return the picturePath
	 */
	public String getPicturePath() {
		return picturePath;
	}

	/**
	 * @return the birthday
	 */
	public Date getBirthday() {
		return birthday;
	}

	@Override
	public String toString() {

		String txt = "Person: ";

		if (!firstName.isEmpty()) {
			txt += firstName + " ";
		}

		txt += name;

		if (!maidenName.isEmpty()) {
			txt += " (formerly " + maidenName + ")";
		}

		return txt;
	}

}
