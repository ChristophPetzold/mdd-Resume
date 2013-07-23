package model.interpreter.core;

import java.io.File;
import java.util.Collection;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import model.elements.ResumeElement;
import model.help.AttributeNotFoundException;
import model.help.NoChoiceException;
import model.help.ResumeModelHelper;
import model.interpreter.custom.HTMLVisitor;
import model.interpreter.custom.TimelineVisitor;

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

	@Override
	public void invokeEx(JBuilder builder, JBuilderObject focus, @SuppressWarnings("rawtypes") Collection selected,
			int param) {

		try {
			interpeterChoice();
			setDestinationPath();
		} catch (NoChoiceException e) {
			ResumeModelHelper.err(e.getMessage(), "Abort");
			return;
		}

		String language = "en";
		String country = "UK";
		Locale currentLocale;
		ResourceBundle messages;
		currentLocale = new Locale(language, country);
		messages = ResourceBundle.getBundle("messages.msg", currentLocale);

		ResumeModelHelper.popup(messages.getString("greetings"), "greetings");

		transformModel(builder);

	}

	/**
	 * Open an input dialog where the interpreter can be chosen. <br>
	 * TODO: messy - need some kind of registry
	 * 
	 * @throws NoChoiceException
	 */
	protected void interpeterChoice() throws NoChoiceException {
		String[] possibilities = { "HTML Résumé", "JSON for Timeline" };
		String s = (String) JOptionPane.showInputDialog(null, "Choose Interpreter:\n", "Interpreter Choice",
				JOptionPane.PLAIN_MESSAGE, null, possibilities, possibilities[0]);

		// If a string was returned, say so.
		if ((s != null) && (s.length() > 0)) {

			if (s.compareTo(possibilities[0]) == 0) {
				visitor = new HTMLVisitor();
			}

			if (s.compareTo(possibilities[1]) == 0) {
				visitor = new TimelineVisitor();
			}

			return;
		}

		throw new NoChoiceException();
	}

	@Override
	public void registerCustomClasses() {
		// Hence we are not introducing new Model-Entities we don not need to register custom Classes.
	}

	/**
	 * Helper Methods
	 ***************************************************************/

	/**
	 * For each individual Résumé contained in the given model the chosen interpreter will be called.
	 * */
	private void transformModel(JBuilder builder) {
		@SuppressWarnings("unchecked")
		Vector<JBuilderModel> roots = builder.getRootFolder().getRootModels();

		// for each Root Model - a Resume - we will create a separate output
		for (JBuilderModel model : roots) {
			visitor.init();

			fetchElements(model);

			visitor.perform(inputPath);
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
				ResumeModelHelper.err(e.getStackTrace().toString(), "InstantiationException");
			} catch (AttributeNotFoundException e) {
				ResumeModelHelper.err(e.getMessage(), "AttributeNotFoundException");
			} catch (IllegalAccessException e) {
				ResumeModelHelper.err(e.getStackTrace().toString(), "IllegalAccessException");
			} catch (ClassNotFoundException e) {
				ResumeModelHelper.err(e.getStackTrace().toString(), "ClassNotFoundException");
			}
		}
	}

	/**
	 * Provide a dialog to choose the output folder.
	 * 
	 * @throws NoChoiceException
	 * */
	private void setDestinationPath() throws NoChoiceException {

		JFileChooser fileDialog = new JFileChooser();

		// we just want the folder, the filename(s) will be derived
		fileDialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		fileDialog.setDialogTitle("Interpreter Output");

		final int result = fileDialog.showOpenDialog(null);

		if (result == JFileChooser.APPROVE_OPTION) {
			File inputFile = fileDialog.getSelectedFile();
			this.inputPath = inputFile.getPath();
		} else {
			throw new NoChoiceException();
		}
	}

}
