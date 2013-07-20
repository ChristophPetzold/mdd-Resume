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
		Div employmentHead = new Div().setCSSClass("employment_head");
		employmentHead.appendText("Employments");
		employmentDiv.appendChild(employmentHead);

		for (Employment emp : this.employmentInstitutions) {
			employmentDiv.appendChild(emploayment2HTML(emp));
		}
		main.appendChild(employmentDiv);

		//
		// Education
		Div educationDiv = new Div().setCSSClass("education");
		Div educationHead = new Div().setCSSClass("education_head");
		educationHead.appendText("Education");
		educationDiv.appendChild(educationHead);
		
		
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

		Div date = getDurationDiv(inst.getStartDate(), inst.getEndDate(), " now");
		institutionDiv.appendChild(date);

		Div institutionText = new Div().setCSSClass("institution_text");

		Div location = new Div().setCSSClass("institution_location").appendText("(" + inst.getLocation() + ")");
		Div titleDiv = new Div().setCSSClass("institution_title").appendText(title);
		Div description = new Div().setCSSClass("institution_description").appendText(inst.getDescription());

		institutionText.appendChild(titleDiv);
		institutionText.appendChild(location);
		institutionText.appendChild(description);

		Div projectsDiv = new Div().setCSSClass("institution_project");
		Div projectHead = new Div().setCSSClass("project_head");
		projectHead.appendText("Projects");
		projectsDiv.appendChild(projectHead);
		
		
		// Projects ...
//		for (ProjectHost connection : projectConnections) {
//
//			if (connection.getInstitution().compareTo(inst.getModelId()) == 0) {
				projectsDiv.appendChild(project2HTML(inst.getModelId()));
//			}
//
//		}

		institutionText.appendChild(projectsDiv);
		institutionDiv.appendChild(institutionText);
		return institutionDiv;
	}

	

	/**
	 * @return DIV element containing person information
	 * */
	private Node person2HTML() {

		Div personDiv = new Div().setCSSClass("person");

		Div text = new Div().setCSSClass("person_text");

		Div name = new Div().appendText(person.getFirstName() + " " + person.getName()).setCSSClass("person_name");
		text.appendChild(name);

		Div birthday = getDurationDiv("Birthday: ", person.getBirthday(), null, "", "").setCSSClass("birthday");

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
	private Node project2HTML(String institutelId) {

		Div projectsDiv = new Div().setCSSClass("projects");
				
		for (Project project : this.projects) {
			
			if(connections.containsKey(institutelId+"."+project.getModelId())){
			
			//if (project.getModelId().compareTo(projectModelId) == 0) {

				Div projectDiv = new Div().setCSSClass("project");
				
				Div date = getDurationDiv(project.getStartDate(), project.getEndDate(), " now");
				
				Div type = new Div().setCSSClass("project_type").appendText(project.getType());
				Div title = new Div().setCSSClass("project_title").appendText("\""+project.getName()+"\"");
				
				Div customer = new Div().setCSSClass("project_customer").appendText(
						"Customer: " + project.getCustomer());

				Div description = new Div().setCSSClass("project_description").appendText(project.getDescription());

				projectDiv.appendChild(date);

				Div projectText = new Div().setCSSClass("project_text");
				projectText.appendChild(type);
				projectText.appendChild(title);
				projectText.appendChild(customer);
				projectText.appendChild(description);

				projectDiv.appendChild(projectText);

				projectsDiv.appendChild(projectDiv);
			}
		}

		
		return projectsDiv;
	}

	private void sortData() {

		Collections.sort(this.employmentInstitutions, Collections.reverseOrder());
		Collections.sort(this.educationInstitutions, Collections.reverseOrder());
		Collections.sort(this.projects, Collections.reverseOrder());
		
	}

	private Div getDurationDiv(String title, Date start, Date end, String sep, String alt) {
		String dateString = title + start.getDateString(DATE_PATTERN);
		dateString += sep;
		if ((end == null) || (end.getDate() == null)) {
			dateString += alt;
		} else {
			dateString += end.getDateString(DATE_PATTERN);
		}

		return new Div().setCSSClass("date").appendText(dateString);
	}
	
	private Div getDurationDiv(Date start, Date end, String alt){		
		return getDurationDiv("",  start,  end,  " - ",  alt);
	}
	
	/**
	 * @param destination
	 *            file path
	 * @param content
	 *            to be written
	 * */
	private void save(String destination, String content) throws URISyntaxException {
		try {

			// Copy CSS file to output folder
			URL source = this.getClass().getClassLoader().getResource(CSS_FILE);

			String srcString = source.getFile().replace("/", File.separator).replace("%20", " ").substring(1);
			String destCSS = destination + File.separator + CSS_FILE;

			Files.copy(Paths.get(srcString), Paths.get(destCSS), StandardCopyOption.REPLACE_EXISTING);

			
			// Create result file
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
