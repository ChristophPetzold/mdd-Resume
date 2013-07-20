package model.help;
/**
 * ResumeInterpreter
 * 
 */

/**
 * Exception for expected Attributes which could not be found.
 * 
 * @author Christoph Petzold
 * 
 */
public class AttributeNotFoundException extends Exception {

	private static final long	serialVersionUID	= -7486119766087669247L;

	/**
	 * @param attributeName
	 */
	public AttributeNotFoundException(String attributeName) {
		super(attributeName);
	}
}
