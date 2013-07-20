package model.interpreter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;

import model.elements.Date;
import model.elements.Education;
import model.elements.Employment;
import model.elements.FetchVisitor;
import model.elements.Institution;
import model.elements.Project;
import model.elements.ProjectHost;
import model.help.ResumeModelHelper;

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

	private final String	CSS_FILE		= "resume.css";
	private final String	DATE_PATTERN	= "yyyy / MM";

	@Override
	public void perform(String destination) {

		sortData();

		Document html = new Document(com.hp.gagawa.java.DocumentType.HTMLStrict);

		// Content: Head
		// Content: Head - CSS link
		html.head.appendChild(new Link().setRel("stylesheet").setHref(CSS_FILE).setType("text/css"));

		// Content: Head
		Title title = new Title();
		title.appendText("Resume of " + person.getFirstName() + " " + person.getName());
		html.head.appendChild(title);

		// Content: Body
		Div main = new Div().setCSSClass("main");

		// Content: Body - Person data
		main.appendChild(person2HTML());

		// Content: Body - Institutions
		//
		// Employments
		Div employmentDiv = new Div().setCSSClass("employments");

		for (Employment emp : this.employmentInstitutions) {
			employmentDiv.appendChild(emploayment2HTML(emp));
		}
		main.appendChild(employmentDiv);

		//
		// Education
		Div educationDiv = new Div().setCSSClass("education");
		// Content: Body - Institutions
		for (Education edu : this.educationInstitutions) {
			educationDiv.appendChild(education2HTML(edu));
		}
		main.appendChild(educationDiv);

		// Save
		html.body.appendChild(main);
		try {
			save(destination, html.write());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @return DIV element containing employment information
	 * */
	private Node emploayment2HTML(Employment emp) {
		return institution2HTML(emp, emp.getPosition() + " at " + emp.getName());
	}

	/**
	 * @return DIV element containing education information
	 * */
	private Node education2HTML(Education edu) {
		return institution2HTML(edu, edu.getType() + ": " + edu.getName());
	}

	private Node institution2HTML(Institution inst, String title) {
		Div institutionDiv = new Div().setCSSClass("institution");

		Div date = getDurationDiv(inst.getStartDate(), inst.getEndDate(), " - ", " now");
		institutionDiv.appendChild(date);

		Div institutionText = new Div().setCSSClass("institution_text");

		Div location = new Div().setCSSClass("institution_location").appendText("(" + inst.getLocation() + ")");
		Div titleDiv = new Div().setCSSClass("institution_title").appendText(title);
		Div description = new Div().setCSSClass("institution_description").appendText(inst.getDescription());

		institutionText.appendChild(titleDiv);
		institutionText.appendChild(location);
		institutionText.appendChild(description);

		Div projectsDiv = new Div().setCSSClass("institution_project");
		// Projects ...
		for (ProjectHost connection : projectConnections) {

			if (connection.getInstitution().compareTo(inst.getModelId()) == 0) {
				projectsDiv.appendChild(project2HTML(connection.getProject()));
			}

		}

		institutionText.appendChild(projectsDiv);
		institutionDiv.appendChild(institutionText);
		return institutionDiv;
	}

	private Div getDurationDiv(Date start, Date end, String sep, String alt) {
		String dateString = start.getDateString(DATE_PATTERN);
		dateString += sep;
		if ((end == null) || (end.getDate() == null)) {
			dateString += alt;
		} else {
			dateString += end.getDateString(DATE_PATTERN);
		}

		return new Div().setCSSClass("date").appendText(dateString);
	}

	/**
	 * @return DIV element containing person information
	 * */
	private Node person2HTML() {

		Div personDiv = new Div().setCSSClass("person");

		Div text = new Div().setCSSClass("person_text");

		Div name = new Div().appendText(person.getFirstName() + " " + person.getName()).setCSSClass("person_name");
		text.appendChild(name);

		Div birthday = getDurationDiv(person.getBirthday(), null, "", "");

		text.appendChild(birthday);
		personDiv.appendChild(text);

		Div pictureDiv = new Div().appendChild(new Img("profile", person.getPicturePath())).setCSSClass(
				"person_picture");
		personDiv.appendChild(pictureDiv);

		return personDiv;
	}

	/**
	 * @param projectModelId
	 *            Model Object Id of project which is to be transformed
	 * 
	 * @return DIV element containing projects information
	 * */
	private Node project2HTML(String projectModelId) {

		Div projectDiv = new Div().setCSSClass("project");

		for (Project project : projects) {
			if (project.getModelId().compareTo(projectModelId) == 0) {

				Div date = getDurationDiv(project.getStartDate(), project.getEndDate(), " - ", " now");

				Div customer = new Div().setCSSClass("project_customer").appendText(
						"Customer: " + project.getCustomer());

				Div title = new Div().setCSSClass("project_title").appendText(project.getName());

				Div description = new Div().setCSSClass("project_description").appendText(project.getDescription());

				projectDiv.appendChild(date);

				Div projectText = new Div().setCSSClass("project_text");
				projectText.appendChild(title);
				projectText.appendChild(customer);
				projectText.appendChild(description);

				projectDiv.appendChild(projectText);

				return projectDiv;
			}
		}

		ResumeModelHelper.err(projectModelId, "Could not find Project");

		return null;
	}

	private void sortData() {

		Collections.sort(this.employmentInstitutions, Collections.reverseOrder());
		Collections.sort(this.educationInstitutions, Collections.reverseOrder());
		Collections.sort(this.employmentInstitutions, Collections.reverseOrder());
	}

	/**
	 * @param destination
	 *            file path
	 * @param content
	 *            to be written
	 * */
	private void save(String destination, String content) throws URISyntaxException {
		try {

			ResumeModelHelper.popup(destination, "Output Folder");

			// Copy CSS file to output folder
			URL source = this.getClass().getClassLoader().getResource(CSS_FILE);

			String srcString = source.getFile().replace("/", File.separator).substring(1);
			ResumeModelHelper.popup("srcString = " + srcString, "DEBUG");

			String destCSS = destination + File.separator + CSS_FILE;

			Files.copy(Paths.get(srcString), Paths.get(destCSS), StandardCopyOption.REPLACE_EXISTING);

			File file = new File(destination + File.separator + person.getName() + ".html");
			file.createNewFile();
			FileWriter fw = new FileWriter(file);

			fw.write(content);
			fw.close();

		} catch (IOException e1) {
			ResumeModelHelper.err(e1.getMessage(), "IO Exception");
		}
	}
}
