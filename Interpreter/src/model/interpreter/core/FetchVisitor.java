package model.interpreter.core;

import java.util.HashMap;
import java.util.Vector;

import model.elements.Education;
import model.elements.Employment;
import model.elements.Person;
import model.elements.Project;
import model.elements.ProjectHost;
import model.elements.ResumeElement;

/**
 * ResumeInterpreter
 * 
 */

/**
 * This visitor reads a given model and stores all information in several members. To write an own interpreter this
 * visitor can be overridden and therefore the functionality is to be implemented in the method {@link #perform(String)}
 * .
 * 
 * @author Christoph Petzold
 * 
 */
public abstract class FetchVisitor implements IResumeVisitor {

	protected Person						person;
	protected Vector<Education>				educationInstitutions;
	protected Vector<Employment>			employmentInstitutions;
	protected Vector<Project>				projects;
	protected Vector<ProjectHost>			projectConnections;

	/**
	 * Map: "Institution.Project" -> ProjectHost
	 */
	protected HashMap<String, ProjectHost>	connections;

	public FetchVisitor() {
		init();
	}

	public void visit(Person person) {
		this.person = person;
	}

	public void visit(Education education) {
		educationInstitutions.add(education);
	}

	public void visit(Employment employment) {
		employmentInstitutions.add(employment);
	}

	public void visit(Project project) {
		projects.add(project);
	}

	public void visit(ProjectHost connection) {
		connections.put(connection.getInstitution() + "." + connection.getProject(), connection);
	}

	public void visit(ResumeElement element) {
		// ohoh...
	}

	public void init() {
		educationInstitutions = new Vector<Education>();
		employmentInstitutions = new Vector<Employment>();
		projects = new Vector<Project>();
		projectConnections = new Vector<ProjectHost>();

		connections = new HashMap<String, ProjectHost>();
	}

}
