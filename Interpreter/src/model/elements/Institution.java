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
 * Model Data Object representing an institution. <br>
 * (An Institution is an abstract representation of any kind of of place a person is acting somehow.)
 * 
 * @author Christoph Petzold
 * 
 */
public abstract class Institution extends ResumeElement implements Comparable<Institution> {

	private final String	ATTRIBUTE_DESCRIPTION	= "Description";
	private final String	ATTRIBUTE_MEDIA_URL		= "Medialink";

	private final String	ATOM_START_DATE			= "Start";
	private final String	ATOM_END_DATE			= "End";

	private final String	MODEL_ADDRESS			= "Address";

	protected String		description				= "[description]";
	protected Date			startDate				= new Date();
	protected Date			endDate					= new Date();
	protected Address		address					= new Address();
	protected String		mediaUrl				= "";

	public void build(JBuilderObject modelObject) throws AttributeNotFoundException {

		super.build(modelObject);

		// extract attributes
		description = ResumeModelHelper.assignStringAttribute(modelObject, ATTRIBUTE_DESCRIPTION);
		mediaUrl = ResumeModelHelper.assignStringAttribute(modelObject, ATTRIBUTE_MEDIA_URL);

		// extract atoms
		startDate.build(ResumeModelHelper.getSingleAtom((JBuilderModel) modelObject, ATOM_START_DATE));
		endDate.build(ResumeModelHelper.getSingleAtom((JBuilderModel) modelObject, ATOM_END_DATE));

		// extract sub models
		address.build(ResumeModelHelper.getSingleModel((JBuilderModel) modelObject, MODEL_ADDRESS));
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
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
	 * @return the address
	 */
	public Address getAddress() {
		return address;
	}

	/**
	 * @return the mediaUrl
	 */
	public String getMediaUrl() {
		return mediaUrl;
	}

	/**
	 * Compared by start date.
	 * */
	public int compareTo(Institution otherInstitute) {
		return startDate.getDate().compareTo(otherInstitute.startDate.getDate());
	}

	@Override
	public String toString() {
		return "Institution: " + name;
	}
}
