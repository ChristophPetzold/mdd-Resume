package model.elements;

import model.help.AttributeNotFoundException;
import model.help.ResumeModelHelper;
import model.interpreter.IResumeVisitor;

import org.isis.gme.bon.JBuilderObject;

/**
 * ResumeInterpreter
 * 
 */

/**
 * TODO: description of Education
 * 
 * @author Christoph Petzold
 * 
 */
public class Education extends Institution {

	private final String	ATTRIBUTE_TYPE	= "Type";

	protected String		type			= "[type]";

	@Override
	public void build(JBuilderObject modelObject) throws AttributeNotFoundException {
		super.build(modelObject);

		type = ResumeModelHelper.assignStringAttribute(modelObject, ATTRIBUTE_TYPE);
	}

	/**
	 * @return Type of the educational institution
	 */
	public String getType() {
		return type;
	}

	@Override
	public void accept(IResumeVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public String toString() {
		return "Educational " + super.toString();
	}
}
