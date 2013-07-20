package model.elements;

import model.help.AttributeNotFoundException;
import model.help.ResumeModelHelper;
import model.interpreter.IResumeVisitor;

import org.isis.gme.bon.JBuilderModel;
import org.isis.gme.bon.JBuilderObject;

/**
 * ResumeInterpreter
 * 
 */

/**
 * TODO: description of Project
 * 
 * @author Christoph Petzold
 * 
 */
public class Project extends ResumeElement implements Comparable<Project> {

	private final String	ATTRIBUTE_DESCRIPTION	= "Description";
	private final String	ATTRIBUTE_CUSTOMER		= "Customer";
	private final String	ATOM_START_DATE			= "Start";
	private final String	ATOM_END_DATE			= "End";

	protected String		description				= "";
	protected String		customer				= "";
	protected Date			startDate				= new Date();
	protected Date			endDate					= new Date();

	@Override
	public void build(JBuilderObject modelObject) throws AttributeNotFoundException {
		super.build(modelObject);

		description = ResumeModelHelper.assignStringAttribute(modelObject, ATTRIBUTE_DESCRIPTION);
		customer = ResumeModelHelper.assignStringAttribute(modelObject, ATTRIBUTE_CUSTOMER);

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

	@Override
	public void accept(IResumeVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * Compared by start date.
	 * */
	@Override
	public int compareTo(Project otherProject) {
		return startDate.getDate().compareTo(otherProject.startDate.getDate());
	}

	@Override
	public String toString() {
		return "Project: " + name;
	}

}
