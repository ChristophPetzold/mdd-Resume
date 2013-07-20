package model.elements;

import model.help.AttributeNotFoundException;
import model.help.ResumeModelHelper;

import org.isis.gme.bon.JBuilderModel;
import org.isis.gme.bon.JBuilderObject;

/**
 * ResumeInterpreter
 * 
 */

/**
 * Institution is an abstract representation of any kind of of place a person is acting somehow.
 * 
 * @author Christoph Petzold
 * 
 */
public abstract class Institution extends ResumeElement implements Comparable<Institution> {

	private final String	ATTRIBUTE_DESCRIPTION	= "Description";
	private final String	ATTRIBUTE_LOCATION		= "Location";
	private final String	ATOM_START_DATE			= "Start";
	private final String	ATOM_END_DATE			= "End";

	protected String		description				= "[description]";
	protected String		location				= "[location]";
	protected Date			startDate				= new Date();
	protected Date			endDate					= new Date();

	public void build(JBuilderObject modelObject) throws AttributeNotFoundException {

		super.build(modelObject);

		description = ResumeModelHelper.assignStringAttribute(modelObject, ATTRIBUTE_DESCRIPTION);
		location = ResumeModelHelper.assignStringAttribute(modelObject, ATTRIBUTE_LOCATION);

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
	 * @return the location
	 */
	public String getLocation() {
		return location;
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
	 * Compared by start date.
	 * */
	public int compareTo(Institution otherInstitute) {
		return startDate.getDate().compareTo(otherInstitute.startDate.getDate());
	}

	@Override
	public String toString() {
		return "Institution: " + name + " (in " + location + ")";
	}
}
