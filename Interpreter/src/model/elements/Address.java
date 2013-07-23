/**
 * 
 */
package model.elements;

import model.help.AttributeNotFoundException;
import model.help.ResumeModelHelper;

import org.isis.gme.bon.JBuilderObject;

/**
 * Model Data Object representing an address.
 * 
 * @author Christoph Petzold
 * 
 */
public class Address extends ResumeElement {

	private final String	ATTRIBUTE_STREET		= "Street";
	private final String	ATTRIBUTE_POSTAL_CODE	= "PostalCode";
	private final String	ATTRIBUTE_CITY			= "City";

	private String			street					= "";
	private String			postalCode				= "";
	private String			city					= "";

	@Override
	public void build(JBuilderObject modelObject) throws AttributeNotFoundException {
		super.build(modelObject);

		// extract attributes
		street = ResumeModelHelper.assignStringAttribute(modelObject, ATTRIBUTE_STREET);
		postalCode = ResumeModelHelper.assignStringAttribute(modelObject, ATTRIBUTE_POSTAL_CODE);
		city = ResumeModelHelper.assignStringAttribute(modelObject, ATTRIBUTE_CITY);

	}

	/**
	 * @return the street
	 */
	public String getStreet() {
		return street;
	}

	/**
	 * @return the postalCode
	 */
	public String getPostalCode() {
		return postalCode;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @return {@code street} + ", " + {@code postalCode} + " - " + {@code city}
	 */
	@Override
	public String toString() {
		return street + ", " + postalCode + " - " + city;
	}

}
