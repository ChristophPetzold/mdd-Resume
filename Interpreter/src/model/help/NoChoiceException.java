/**
 * 
 */
package model.help;

/**
 * TODO add type description
 * 
 * @author "Christoph Petzold"
 * 
 */
public class NoChoiceException extends Exception {

	private static final long	serialVersionUID	= 1465778640756954107L;

	public NoChoiceException() {
		super("No appropiate choice was made.");
	}

}
