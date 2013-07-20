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
 * TODO: description of Employment
 * 
 * @author Christoph Petzold
 * 
 */
public class Employment extends Institution {

	private final String	ATTRIBUTE_POSITION	= "Position";

	protected String		position;

	@Override
	public void build(JBuilderObject modelObject) throws AttributeNotFoundException {
		super.build(modelObject);

		position = ResumeModelHelper.assignStringAttribute(modelObject, ATTRIBUTE_POSITION);

	}

	@Override
	public void accept(IResumeVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * @return the position of the employment
	 */
	public String getPosition() {
		return position;
	}

	@Override
	public String toString() {
		return "Employment " + super.toString();
	}

}
