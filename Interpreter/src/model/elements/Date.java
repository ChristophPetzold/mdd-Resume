package model.elements;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import model.help.AttributeNotFoundException;
import model.help.ResumeModelHelper;
import model.interpreter.IResumeVisitor;

import org.isis.gme.bon.JBuilderObject;

/**
 * ResumeInterpreter
 * 
 */

/**
 * TODO: description of Date
 * 
 * @author Christoph Petzold
 * 
 */
public class Date extends ResumeElement {

	private final String	ATTRIBUTE_DAY	= "Day";
	private final String	ATTRIBUTE_MONTH	= "Month";
	private final String	ATTRIBUTE_YEAR	= "Year";

	private java.util.Date	date;

	@Override
	public void build(JBuilderObject modelObject) throws AttributeNotFoundException {

		if (modelObject == null) {
			date = null;
			name = "-";
			return;
		}

		super.build(modelObject);

		name = modelObject.getName();

		Calendar cal = Calendar.getInstance(Locale.getDefault());
		cal.set(Calendar.YEAR, ResumeModelHelper.assignIntAttribute(modelObject, ATTRIBUTE_YEAR));
		cal.set(Calendar.MONTH, ResumeModelHelper.assignIntAttribute(modelObject, ATTRIBUTE_MONTH));

		/**
		 * FIXME: Getting wrong day values from the model (day-1)
		 * */
		int day = ResumeModelHelper.assignIntAttribute(modelObject, ATTRIBUTE_DAY) + 1;
		cal.set(Calendar.DAY_OF_MONTH, day);

		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);

		date = cal.getTime();

	}

	public java.util.Date getDate() {
		return date;
	}

	public String getDateString(String pattern) {
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(date);
	}

	@Override
	public String toString() {
		return name + ": " + date;
	}

	@Override
	public void accept(IResumeVisitor visitor) {
		visitor.visit(this);
	}

}
