package model.interpreter.custom;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;

import model.elements.Date;
import model.elements.Education;
import model.elements.Employment;
import model.elements.Institution;
import model.elements.Project;
import model.help.ResumeModelHelper;
import model.interpreter.core.FetchVisitor;

import com.hp.gagawa.java.Document;
import com.hp.gagawa.java.Node;
import com.hp.gagawa.java.elements.Div;
import com.hp.gagawa.java.elements.Img;
import com.hp.gagawa.java.elements.Link;
import com.hp.gagawa.java.elements.Title;

/**
 * ResumeInterpreter
 * 
 */

/**
 * The Visitor consumes the GME Model Objects to generate a HTML-Page.
 * 
 * @author Christoph Petzold
 * 
 */
public class HTMLVisitor extends FetchVisitor {

	private final String	CSS_FILE		= Messages.getString("HTMLVisitor.cssFile");		//$NON-NLS-1$
	private final String	DATE_PATTERN	= Messages.getString("HTMLVisitor.datePattern");	//$NON-NLS-1$

	@Override
	public void perform(String destination) {

		// The collected events should be ordered chronologically (reverse)
		sortData();

		Document html = new Document(com.hp.gagawa.java.DocumentType.HTMLStrict);

		genrateHtmlHead(html);

		generateHtmlBody(html);

		// Save
		save(destination, html.write());
	}

	/**
	 * Generates HTML's body element
	 * 
	 * @param html
	 */
	protected void generateHtmlBody(Document html) {

		Div main = new Div().setCSSClass(Messages.getString("HTMLVisitor.cssClass.main")); //$NON-NLS-1$

		// Add "Personal Information"-Block
		main.appendChild(person2HTML());

		// Add "Employments"-Block
		Div employmentDiv = new Div().setCSSClass(Messages.getString("HTMLVisitor.cssClass.employments")); //$NON-NLS-1$
		Div employmentHead = new Div().setCSSClass(Messages.getString("HTMLVisitor.cssClass.employmentHead")); //$NON-NLS-1$
		employmentHead.appendText(Messages.getString("HTMLVisitor.employment.title")); //$NON-NLS-1$
		employmentDiv.appendChild(employmentHead);

		for (Employment emp : this.employmentInstitutions) {
			employmentDiv.appendChild(emploayment2HTML(emp));
		}
		main.appendChild(employmentDiv);

		// Add "Education"-Block
		Div educationDiv = new Div().setCSSClass(Messages.getString("HTMLVisitor.cssClass.education")); //$NON-NLS-1$
		Div educationHead = new Div().setCSSClass(Messages.getString("HTMLVisitor.cssClass.educationHead")); //$NON-NLS-1$
		educationHead.appendText(Messages.getString("HTMLVisitor.education.title")); //$NON-NLS-1$
		educationDiv.appendChild(educationHead);

		for (Education edu : this.educationInstitutions) {
			educationDiv.appendChild(education2HTML(edu));
		}
		main.appendChild(educationDiv);

		// add generated block to the body element
		html.body.appendChild(main);
	}

	/**
	 * Generate HTML's Head-Element
	 * 
	 * @param html
	 */
	protected void genrateHtmlHead(Document html) {

		// insert CSS link
		html.head
				.appendChild(new Link()
						.setRel(Messages.getString("HTMLVisitor.htmlLink.relCss")).setHref(CSS_FILE).setType(Messages.getString("HTMLVisitor.htmlLink.typeCss"))); //$NON-NLS-1$ //$NON-NLS-2$

		// set title
		Title title = new Title().appendText(String.format(
				Messages.getString("HTMLVisitor.resumeTitle"), person.getFullName())); //$NON-NLS-1$
		html.head.appendChild(title);
	}

	/**
	 * @return DIV element containing employment information
	 * */
	private Node emploayment2HTML(Employment emp) {
		return institution2HTML(emp,
				String.format(Messages.getString("HTMLVisitor.employmentTitle"), emp.getPosition(), emp.getName())); //$NON-NLS-1$
	}

	/**
	 * @return DIV element containing education information
	 * */
	private Node education2HTML(Education edu) {
		return institution2HTML(edu, edu.getType() + ": " + edu.getName()); //$NON-NLS-1$
	}

	private Node institution2HTML(Institution inst, String title) {
		Div institutionDiv = new Div().setCSSClass(Messages.getString("HTMLVisitor.cssClass.institution")); //$NON-NLS-1$

		Div date = getDurationDiv(inst.getStartDate(), inst.getEndDate(), " now"); //$NON-NLS-1$
		institutionDiv.appendChild(date);

		Div institutionText = new Div().setCSSClass(Messages.getString("HTMLVisitor.cssClass.institutionText")); //$NON-NLS-1$

		Div location = new Div()
				.setCSSClass(Messages.getString("HTMLVisitor.cssClass.institutionLocation")).appendText( //$NON-NLS-1$
						String.format(
								Messages.getString("HTMLVisitor.institution.address"), inst.getAddress().toString())); //$NON-NLS-1$ //$NON-NLS-2$
		Div titleDiv = new Div()
				.setCSSClass(Messages.getString("HTMLVisitor.cssClass.institutionTitle")).appendText(title); //$NON-NLS-1$
		Div description = new Div()
				.setCSSClass(Messages.getString("HTMLVisitor.cssClass.institutionDescription")).appendText(inst.getDescription()); //$NON-NLS-1$

		institutionText.appendChild(titleDiv);
		institutionText.appendChild(location);
		institutionText.appendChild(description);

		Node projects = project2HTML(inst.getModelId());
		if (projects != null) {
			Div projectsDiv = new Div().setCSSClass(Messages.getString("HTMLVisitor.cssClass.institutionProject")); //$NON-NLS-1$
			Div projectHead = new Div().setCSSClass(Messages.getString("HTMLVisitor.cssClass.projectHead")); //$NON-NLS-1$
			projectHead.appendText(Messages.getString("HTMLVisitor.project.title")); //$NON-NLS-1$
			projectsDiv.appendChild(projectHead);

			projectsDiv.appendChild(projects);
			institutionText.appendChild(projectsDiv);
		}

		institutionDiv.appendChild(institutionText);
		return institutionDiv;
	}

	/**
	 * @return DIV element containing person information
	 * */
	private Node person2HTML() {

		Div personDiv = new Div().setCSSClass(Messages.getString("HTMLVisitor.cssClass.person")); //$NON-NLS-1$

		Div text = new Div().setCSSClass(Messages.getString("HTMLVisitor.cssClass.personText")); //$NON-NLS-1$

		Div name = new Div().appendText(person.getFullName()).setCSSClass(
				Messages.getString("HTMLVisitor.cssClass.personName")); //$NON-NLS-1$
		text.appendChild(name);

		Div birthString = new Div().setCSSClass(Messages.getString("HTMLVisitor.cssClass.personBirthday")); //$NON-NLS-1$
		birthString.appendText(String.format(
				Messages.getString("HTMLVisitor.person.birth"), person.getBirthday().getDateString(DATE_PATTERN), //$NON-NLS-1$
				person.getBirthplace()));

		text.appendChild(birthString);
		personDiv.appendChild(text);

		Div pictureDiv = new Div().appendChild(
				new Img(Messages.getString("HTMLVisitor.personPictureAltText"), person.getPicturePath())).setCSSClass( //$NON-NLS-1$
				Messages.getString("HTMLVisitor.cssClass.personPicture")); //$NON-NLS-1$
		personDiv.appendChild(pictureDiv);

		return personDiv;
	}

	/**
	 * @param projectModelId
	 *            Model Object Id of project which is to be transformed
	 * 
	 * @return DIV element containing projects information
	 * */
	private Node project2HTML(String institutelId) {

		Div projectsDiv = new Div().setCSSClass(Messages.getString("HTMLVisitor.cssClass.projects")); //$NON-NLS-1$

		// count the number of projects, if there isn't any, the "Projects"-heading won't be written
		int count = 0;

		for (Project project : this.projects) {

			if (connections.containsKey(institutelId + "." + project.getModelId())) { //$NON-NLS-1$

				Div projectDiv = new Div().setCSSClass(Messages.getString("HTMLVisitor.cssClass.project")); //$NON-NLS-1$

				Node date = getDurationDiv(project.getStartDate(), project.getEndDate(),
						Messages.getString("HTMLVisitor.noEndDateReplacement")); //$NON-NLS-1$

				Div type = new Div()
						.setCSSClass(Messages.getString("HTMLVisitor.cssClass.projectType")).appendText(project.getType()); //$NON-NLS-1$
				Div title = new Div().setCSSClass(Messages.getString("HTMLVisitor.cssClass.projectTitle")) //$NON-NLS-1$
						.appendText(String.format(Messages.getString("HTMLVisitor.projectTitle"), project.getName())); //$NON-NLS-1$

				Div customer = new Div()
						.setCSSClass(Messages.getString("HTMLVisitor.cssClass.projectCustomer")).appendText( //$NON-NLS-1$
								String.format(Messages.getString("HTMLVisitor.project.customer"), project.getCustomer())); //$NON-NLS-1$

				Div description = new Div()
						.setCSSClass(Messages.getString("HTMLVisitor.cssClass.projectDescription")).appendText(project.getDescription()); //$NON-NLS-1$

				projectDiv.appendChild(date);

				Div projectText = new Div().setCSSClass(Messages.getString("HTMLVisitor.cssClass.projectText")); //$NON-NLS-1$
				projectText.appendChild(type);
				projectText.appendChild(title);
				projectText.appendChild(customer);
				projectText.appendChild(description);

				projectDiv.appendChild(projectText);

				projectsDiv.appendChild(projectDiv);

				count++;
			}
		}

		if (count > 0) {
			return projectsDiv;
		}

		return null;

	}

	private void sortData() {

		Collections.sort(this.employmentInstitutions, Collections.reverseOrder());
		Collections.sort(this.educationInstitutions, Collections.reverseOrder());
		Collections.sort(this.projects, Collections.reverseOrder());

	}

	/**
	 * Generate a Ele
	 * */
	private Div getDurationDiv(String title, Date start, Date end, String sep, String alt) {
		String dateString = title + start.getDateString(DATE_PATTERN);
		dateString += sep;
		if ((end == null) || (end.getDate() == null)) {
			dateString += alt;
		} else {
			dateString += end.getDateString(DATE_PATTERN);
		}

		return new Div().setCSSClass(Messages.getString("HTMLVisitor.cssClass.date")).appendText(dateString); //$NON-NLS-1$
	}

	private Div getDurationDiv(Date start, Date end, String alt) {
		return getDurationDiv("", start, end, " - ", alt); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * HTML-File will be saved in the given destination folder. Name of the file is the persons name.
	 * 
	 * @param destination
	 *            file path
	 * @param content
	 *            to be written
	 * */
	private void save(String destination, String content) {
		try {

			// Copy CSS file to output folder
			URL source = this.getClass().getClassLoader().getResource(CSS_FILE);

			String srcString = source.getFile().replace("/", File.separator).replace("%20", " ").substring(1); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			String destCSS = destination + File.separator + CSS_FILE;

			Files.copy(Paths.get(srcString), Paths.get(destCSS), StandardCopyOption.REPLACE_EXISTING);

			// Create result file
			String outputFile = String.format(
					Messages.getString("HTMLVisitor.outputFileFormat"), destination, File.separator, person.getName(), //$NON-NLS-1$
					Messages.getString("HTMLVisitor.outputFileExtension")); //$NON-NLS-1$

			File file = new File(outputFile);
			file.createNewFile();
			FileWriter fw = new FileWriter(file);

			fw.write(content);
			fw.close();

		} catch (IOException e1) {
			ResumeModelHelper.err(e1, Messages.getString("HTMLVisitor.exception.save.io")); //$NON-NLS-1$
		}
	}
}
