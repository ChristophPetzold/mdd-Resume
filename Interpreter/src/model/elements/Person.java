package model.elements;

import model.help.AttributeNotFoundException;
import model.help.ResumeModelHelper;
import model.interpreter.core.IResumeVisitor;

import org.isis.gme.bon.JBuilderModel;
import org.isis.gme.bon.JBuilderObject;

/**
 * ResumeInterpreter
 */

/**
 * Model Data Object representing a person.
 * 
 * @author Christoph Petzold
 */
public class Person extends ResumeElement {

	private final String	ATTRIBUTE_FIRST_NAME	= "FirstName";
	private final String	ATTRIBUTE_MAIDEN_NAME	= "MaidenName";
	private final String	ATTRIBUTE_PICTURE_PATH	= "PicturePath";
	private final String	ATOM_BIRTHDAY			= "Birthday";
	private final String	ATTRIBUTE_BIRTHPLACE	= "Birthplace";
	private final String	ATTRIBUTE_EMAIL			= "EMail";
	private final String	MODEL_ADDRESS			= "Address";

	protected String		firstName				= "";
	protected String		maidenName				= "";
	protected String		picturePath				= "";
	protected Date			birthday				= new Date();
	protected String		birthplace				= "";
	protected String		eMail					= "";
	protected Address		address					= new Address();

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

		// extract attributes
		firstName = ResumeModelHelper.assignStringAttribute(modelObject, ATTRIBUTE_FIRST_NAME);
		maidenName = ResumeModelHelper.assignStringAttribute(modelObject, ATTRIBUTE_MAIDEN_NAME);
		picturePath = ResumeModelHelper.assignStringAttribute(modelObject, ATTRIBUTE_PICTURE_PATH);
		birthplace = ResumeModelHelper.assignStringAttribute(modelObject, ATTRIBUTE_BIRTHPLACE);
		eMail = ResumeModelHelper.assignStringAttribute(modelObject, ATTRIBUTE_EMAIL);

		// extract atoms
		birthday.build(ResumeModelHelper.getSingleAtom((JBuilderModel) modelObject, ATOM_BIRTHDAY));

		// extract sub models
		address.build(ResumeModelHelper.getSingleModel((JBuilderModel) modelObject, MODEL_ADDRESS));
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

	/**
	 * @return the birthplace
	 */
	public String getBirthplace() {
		return birthplace;
	}

	public String getFullName() {
		String txt = "";

		if (!firstName.isEmpty()) {
			txt += firstName + " ";
		}

		txt += name;

		if (!maidenName.isEmpty()) {
			txt += " (formerly " + maidenName + ")";
		}

		return txt;
	}

	/**
	 * @return the eMail
	 */
	public String geteMail() {
		return eMail;
	}

	/**
	 * @return the address
	 */
	public Address getAddress() {
		return address;
	}

	@Override
	public String toString() {
		return "Person: " + getFullName();
	}

}
