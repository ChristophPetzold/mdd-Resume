package model.elements;
import java.util.HashMap;
import java.util.Vector;

import model.interpreter.IResumeVisitor;

/**
 * ResumeInterpreter
 * 
 */

/**
 * TODO: description of SortVisitor
 * 
 * @author Christoph Petzold
 * 
 */
public abstract class FetchVisitor implements IResumeVisitor {

	protected Person				person;
	protected Vector<Education>		educationInstitutions;
	protected Vector<Employment>	employmentInstitutions;
	protected Vector<Project>		projects;
	protected Vector<ProjectHost>	projectConnections;

	
	/**
	 * Map: "Institution.Project" -> ProjectHost
	 */
	protected HashMap<String, ProjectHost> connections;
	
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
		connections.put(connection.getInstitution()+"."+connection.getProject(), connection);		
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
