package model.elements;

import model.help.AttributeNotFoundException;
import model.interpreter.IResumeVisitor;

import org.isis.gme.bon.JBuilderObject;

/**
 * @author Christoph Petzold
 * 
 */
public abstract class ResumeElement {

	protected String	name	= "[name]";
	protected String	modelId	= "";

	/**
	 * This method is to be used to fed the ResumeElement with information of the Model.
	 * 
	 * @param modelObject
	 * @throws AttributeNotFoundException
	 */
	public void build(JBuilderObject modelObject) throws AttributeNotFoundException {
		name = modelObject.getName();
		modelId = modelObject.getObjID();

	}

	/**
	 * Resume Elements are - according to the Visitor Pattern - visitable elements and therefore have to implement this
	 * accept method.
	 * 
	 * @param visitor
	 */
	public abstract void accept(IResumeVisitor visitor);

	/**
	 * @return Elements name attribute
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return ObjectId of the original Model Object
	 */
	public String getModelId() {
		return modelId;
	}

}