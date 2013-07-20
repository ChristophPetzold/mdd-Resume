package model.interpreter;
import model.elements.Education;
import model.elements.Employment;
import model.elements.Person;
import model.elements.Project;
import model.elements.ProjectHost;
import model.elements.ResumeElement;

/**
 * @author Christoph Petzold
 * 
 */
public interface IResumeVisitor {

	public void visit(Person person);

	public void visit(Education education);

	public void visit(Employment employment);

	public void visit(Project project);

	public void visit(ProjectHost connection);

	public void visit(ResumeElement element);

	public void init();

	public void perform(String destination);
}
