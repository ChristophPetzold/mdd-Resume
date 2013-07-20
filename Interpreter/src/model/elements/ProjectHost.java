package model.elements;

import model.help.AttributeNotFoundException;
import model.interpreter.IResumeVisitor;

import org.isis.gme.bon.JBuilderConnection;
import org.isis.gme.bon.JBuilderObject;

/**
 * ResumeInterpreter
 * 
 */

/**
 * TODO: description of ProjectHost
 * 
 * @author Christoph Petzold
 * 
 */
public class ProjectHost extends ResumeElement {

	protected String	institution	= "";
	protected String	project		= "";

	@Override
	public void build(JBuilderObject modelObject) throws AttributeNotFoundException {
		super.build(modelObject);

		JBuilderConnection c = (JBuilderConnection) modelObject;

		institution = c.getDestination().getObjID();
		project = c.getSource().getObjID();
	}

	@Override
	public void accept(IResumeVisitor visitor) {
		visitor.visit(this);
	}

	/**
	 * @return the institution
	 */
	public String getInstitution() {
		return institution;
	}

	/**
	 * @return the project
	 */
	public String getProject() {
		return project;
	}

}
