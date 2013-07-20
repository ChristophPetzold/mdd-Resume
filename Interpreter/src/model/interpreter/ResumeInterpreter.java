package model.interpreter;

import java.io.File;
import java.util.Collection;
import java.util.Vector;

import javax.swing.JFileChooser;

import model.elements.ResumeElement;
import model.help.AttributeNotFoundException;
import model.help.ResumeModelHelper;

import org.isis.gme.bon.BONComponent;
import org.isis.gme.bon.JBuilder;
import org.isis.gme.bon.JBuilderModel;
import org.isis.gme.bon.JBuilderObject;

/**
 * Interpreter for Resumé-Models. TODO: refine description ... features
 * 
 * @author Christoph Petzold
 */
public class ResumeInterpreter implements BONComponent {

	private String			inputPath;

	private IResumeVisitor	visitor;

	/**
	 * BON-Methods
	 ***************************************************************/

	/**
	 *
	 */
	@Override
	public void invokeEx(JBuilder builder, JBuilderObject focus, @SuppressWarnings("rawtypes") Collection selected,
			int param) {

		setDestinationPath();

		// TODO choose interpretation

		visitor = new HTMLVisitor();

		transformModel(builder);
	}

	
	@Override
	public void registerCustomClasses() {
		// TODO Auto-generated method stub

	}

	/**
	 * Helper Methods
	 ***************************************************************/

	private void transformModel(JBuilder builder) {
		@SuppressWarnings("unchecked")
		Vector<JBuilderModel> roots = builder.getRootFolder().getRootModels();

		// for each Root Model - a Resume - we will create a separate output
		for (JBuilderModel model : roots) {
			
			visitor.init();
			fetchElements(model);

		}

	}

	/**
	 * @param model
	 */
	@SuppressWarnings("unchecked")
	private void fetchElements(JBuilderModel model) {

		for (JBuilderObject obj : (Vector<JBuilderObject>) model.getChildren()) {

			String className = obj.getKindName();
			ResumeElement element;

			try {
				element = (ResumeElement) Class.forName("model.elements." + className).newInstance();
				element.build(obj);
				element.accept(visitor);
			} catch (InstantiationException e) {
				ResumeModelHelper.err(e.toString(), "InstantiationException");
			} catch (AttributeNotFoundException e) {
				ResumeModelHelper.err(e.getMessage(), "AttributeNotFoundException");
			} catch (IllegalAccessException e) {
				ResumeModelHelper.err(e.toString(), "IllegalAccessException");
			} catch (ClassNotFoundException e) {
				ResumeModelHelper.err(e.toString(), "ClassNotFoundException");
			}

		}

		visitor.perform(inputPath);

	}

	/**
	 * Provide a dialog to choose the output folder.
	 * */
	private void setDestinationPath() {

		JFileChooser fileDialog = new JFileChooser();

		// we just want the folder, the filename(s) will be derived
		fileDialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		fileDialog.setDialogTitle("Interpreter Output");

		fileDialog.setVisible(true);
		final int result = fileDialog.showOpenDialog(null);

		if (result == JFileChooser.APPROVE_OPTION) {
			File inputFile = fileDialog.getSelectedFile();
			inputPath = inputFile.getPath();

		}

		fileDialog.setVisible(false);

	}

}
