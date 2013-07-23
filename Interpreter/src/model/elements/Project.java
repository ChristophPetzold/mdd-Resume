package model.elements;

import model.help.AttributeNotFoundException;
import model.help.ResumeModelHelper;
import model.interpreter.core.IResumeVisitor;

import org.isis.gme.bon.JBuilderModel;
import org.isis.gme.bon.JBuilderObject;

/**
 * ResumeInterpreter
 * 
 */

/**
 * Model Data Object representing a project.
 * 
 * @author Christoph Petzold
 * 
 */
public class Project extends ResumeElement implements Comparable<Project> {

	private final String	ATTRIBUTE_DESCRIPTION	= "Description";
	private final String	ATTRIBUTE_CUSTOMER		= "Customer";
	private final String	ATTRIBUTE_TYPE			= "TypeOfProject";
	private final String	ATTRIBUTE_MEDIA_URL		= "Medialink";

	private final String	ATOM_START_DATE			= "Start";
	private final String	ATOM_END_DATE			= "End";

	protected String		description				= "";
	protected String		customer				= "";
	protected String		type					= "";
	protected Date			startDate				= new Date();
	protected Date			endDate					= new Date();
	protected String		mediaUrl				= "";

	@Override
	public void build(JBuilderObject modelObject) throws AttributeNotFoundException {
		super.build(modelObject);

		// extract attributes
		description = ResumeModelHelper.assignStringAttribute(modelObject, ATTRIBUTE_DESCRIPTION);
		customer = ResumeModelHelper.assignStringAttribute(modelObject, ATTRIBUTE_CUSTOMER);
		type = ResumeModelHelper.assignStringAttribute(modelObject, ATTRIBUTE_TYPE);
		mediaUrl = ResumeModelHelper.assignStringAttribute(modelObject, ATTRIBUTE_MEDIA_URL);

		// extract atoms
		startDate.build(ResumeModelHelper.getSingleAtom((JBuilderModel) modelObject, ATOM_START_DATE));
		endDate.build(ResumeModelHelper.getSingleAtom((JBuilderModel) modelObject, ATOM_END_DATE));

	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the customer
	 */
	public String getCustomer() {
		return customer;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @return the mediaUrl
	 */
	public String getMediaUrl() {
		return mediaUrl;
	}

	@Override
	public void accept(IResumeVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * Compared by start date.
	 * */
	@Override
	public int compareTo(Project otherProject) {
		java.util.Date self = startDate.getDate();
		java.util.Date other = otherProject.startDate.getDate();
		int result = self.compareTo(other);

		// ResumeModelHelper.popup(startDate.getDateString("dd.MM.yy") +(result>0?" > ":(result==0?" = ": " < "))+
		// otherProject.startDate.getDateString("dd.MM.yy"), "Project compare");

		return result;
	}

	@Override
	public String toString() {
		return "Project: " + name;
	}

}
