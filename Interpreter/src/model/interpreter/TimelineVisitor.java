/**
 * 
 */
package model.interpreter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import model.elements.Date;
import model.elements.Education;
import model.elements.Employment;
import model.elements.Project;
import model.help.ResumeModelHelper;

/**
 * This interpreter transforms the consumed model into a JSONP-Format. For each resume in the model one json file will
 * be created. These files can be used as input for the great "TimmelineJS"-Tool (see <a
 * href="http://timeline.verite.co/">http://timeline.verite.co/</a>).
 * 
 * @author Christoph Petzold
 * 
 */
public class TimelineVisitor extends FetchVisitor {

	private final String	JSON_FIELD_HEADLINE		= "headline";
	private final String	JSON_FIELD_TYPE			= "type";
	private final String	JSON_FIELD_TEXT			= "text";
	private final String	JSON_FIELD_ASSET		= "asset";
	private final String	JSON_FIELD_MEDIA		= "media";
	private final String	JSON_FIELD_CREDIT		= "credit";
	private final String	JSON_FIELD_CAPTION		= "caption";
	private final String	JSON_FIELD_START		= "startDate";
	private final String	JSON_FIELD_END			= "endDate";
	private final String	JSON_FIELD_TAG			= "tag";
	private final String	JSON_FIELD_THUMBNAIL	= "thumbnail";

	private final String	TAG_EMPLOYMENT			= "Employments";
	private final String	TAG_EDUCATION			= "Education";
	private final String	TAG_EMPLOYMENT_PROJECT	= "Employments projects";
	private final String	TAG_EDUCATION_PROJECT	= "Education projects";

	private final String	JSON_DATE_PATTERN		= "yyyy,MM,dd";
	private final String	JSON_SEP				= ", ";

	@Override
	public void perform(String destination) {

		String result = genreateJsonFile(destination);
		save(destination, result);
	}

	/**
	 * @param destination
	 */
	private String genreateJsonFile(String destination) {
		String jsonString = "storyjs_jsonp_data = { \"timeline\" : {";

		jsonString += generateJsonIntro() + JSON_SEP;

		jsonString += generateJsonTimeEvents();

		// closing
		jsonString += "}}";

		return cleanUp(jsonString);

	}

	/**
	 * @param institutionDates
	 * @return
	 */
	protected String cleanUp(String jsonString) {

		// ", , " -> ", "
		while (jsonString.contains(JSON_SEP + JSON_SEP))
			jsonString = jsonString.replaceAll(JSON_SEP + JSON_SEP, JSON_SEP);

		jsonString = jsonString.replaceAll(JSON_SEP + "}", "}");
		jsonString = jsonString.replaceAll(JSON_SEP + "]", "]");

		return jsonString;
	}

	/**
	 * @return
	 */
	private String generateJsonIntro() {

		String intro = addJsonElement(JSON_FIELD_HEADLINE, this.person.getFullName());

		intro += addJsonElement(JSON_FIELD_TYPE, "default");
		intro += addJsonElement(JSON_FIELD_TEXT, "This is the timeline of " + person.getFullName() + ". Born on "
				+ person.getBirthday().getDateString("dd. MMMMMMMMMM yyyy") + ". ");
		intro += addJsonAsset(person.getPicturePath(), "", person.getFirstName() + " " + person.getName(), null);

		return intro;
	}

	/**
	 * @return
	 */
	private String generateJsonTimeEvents() {

		String dates = "[";
		dates += addEmploymentDates();
		dates += addEducationDates();
		dates += "]";

		return "\"date\":" + dates;
	}

	/**
	 * @return
	 */
	private String addEmploymentDates() {
		String institutionDates = "";

		for (Employment emp : employmentInstitutions) {
			String text = "";

			text += "Location: " + emp.getLocation() + "<br/>";
			text += "Position: " + emp.getPosition() + "<br/>";
			text += emp.getDescription() + "<br/>";

			String asset = addJsonAsset("", "", "", "");

			institutionDates += addJSONDate(emp.getStartDate(), emp.getEndDate(), emp.getName(), text, TAG_EMPLOYMENT,
					asset);

			institutionDates += JSON_SEP;
			institutionDates += addInstitutionProjects(emp.getModelId(), emp.getName(), TAG_EMPLOYMENT_PROJECT);
			institutionDates += JSON_SEP;
		}

		return institutionDates;
	}

	/**
	 * @return
	 */
	private String addEducationDates() {
		String educationDates = "";

		for (Education edu : educationInstitutions) {
			String text = "";

			text += "Location: " + edu.getLocation() + "<br/>";
			text += "Type: " + edu.getType() + "<br/>";
			text += edu.getDescription() + "<br/>";

			String asset = addJsonAsset("", "", "", "");

			educationDates += addJSONDate(edu.getStartDate(), edu.getEndDate(), edu.getName(), text, TAG_EDUCATION,
					asset);

			educationDates += JSON_SEP;
			educationDates += addInstitutionProjects(edu.getModelId(), edu.getName(), TAG_EDUCATION_PROJECT);
			educationDates += JSON_SEP;
		}

		return educationDates;
	}

	/**
	 * @param modelId
	 * @return projects in JSON format
	 */
	private String addInstitutionProjects(String instituteId, String instituteName, String tag) {

		String projectsJSON = "";

		// now we are looking for the projects which are done at the current institution (modelId)
		for (Project project : this.projects) {

			if (this.connections.containsKey(instituteId + "." + project.getModelId())) {

				String projectJSON = "";

				String text = "";

				text += "Title: <i>" + project.getName() + "</i><br/>";
				text += "Customer: " + project.getCustomer() + "<br/>";
				text += project.getDescription() + "<br/>";

				String asset = addJsonAsset("", "", "", "");

				projectJSON += addJSONDate(project.getStartDate(), project.getEndDate(), project.getType() + " at "
						+ instituteName, text, tag, asset);

				projectsJSON += projectJSON + JSON_SEP;
			}
		}

		return projectsJSON;
	}

	/**
	 * @param start
	 * @param end
	 * @param headline
	 * @param text
	 * @param tag
	 * @param asset
	 *            in json format
	 * @return { <br>
	 *         "startDate": "{@code start date}", <br>
	 *         "endDate": "{@code end date}", <br>
	 *         "headline": "{@code headline}", <br>
	 *         "text": "{@code text}", <br>
	 *         "tag": "{@link #TAG_EMPLOYMENT}", <br>
	 *         "asset": "{@code asset}" <br>
	 *         }
	 */
	private String addJSONDate(Date start, Date end, String headline, String text, String tag, String asset) {
		String date = "{";

		date += addJsonElement(JSON_FIELD_START, start.getDateString(JSON_DATE_PATTERN));

		if (end.getDate() != null) {
			date += addJsonElement(JSON_FIELD_END, end.getDateString(JSON_DATE_PATTERN));
		}

		date += addJsonElement(JSON_FIELD_HEADLINE, headline);
		date += addJsonElement(JSON_FIELD_TEXT, text);
		date += addLastJsonElement(JSON_FIELD_TAG, tag);

		if (!asset.isEmpty())
			date += JSON_SEP + asset;

		return date + "}";
	}

	/**
	 * @param elementName
	 * @param value
	 * @return "elementName": "value",
	 */
	private String addJsonElement(String elementName, String value) {
		return addLastJsonElement(elementName, value) + JSON_SEP;
	}

	/**
	 * @param elementName
	 * @param value
	 * @return "elementName": "value"
	 */
	private String addLastJsonElement(String elementName, String value) {
		return "\"" + elementName + "\": \"" + value + "\"";
	}

	/**
	 * @param media
	 * @param credit
	 * @param caption
	 * @param thumbnail
	 * @return { <br>
	 *         "media": "{@code media}", <br>
	 *         "credit": "{@code credit}", <br>
	 *         "caption": "{@code caption}", <br>
	 *         "thumbnail": "{@code thumbnail}"<br>
	 *         }
	 */
	private String addJsonAsset(String media, String credit, String caption, String thumbnail) {

		String asset = "\"" + JSON_FIELD_ASSET + "\": {";

		asset += addJsonElement(JSON_FIELD_MEDIA, media);
		asset += addJsonElement(JSON_FIELD_CREDIT, credit);

		if (thumbnail != null) {
			asset += addJsonElement(JSON_FIELD_CAPTION, caption);
			asset += addLastJsonElement(JSON_FIELD_THUMBNAIL, thumbnail);
		} else {
			asset += addLastJsonElement(JSON_FIELD_CAPTION, caption);
		}

		asset += "}";

		return asset;
	}

	/**
	 * Save creates/overwrites a json-file named "resume.json" in the destination folder.
	 * 
	 * @param destination
	 *            file path
	 * @param contend
	 *            json-content to be written
	 * */
	private void save(String destination, String content) {
		try {

			// Create result file
			String personsFolder = destination + File.separator + person.getName();
			File folder = new File(personsFolder);
			folder.mkdir();

			File file = new File(personsFolder + File.separator + "resume.jsonp");
			file.createNewFile();
			FileWriter fw = new FileWriter(file);

			fw.write(content);
			fw.close();

		} catch (IOException e1) {
			ResumeModelHelper.err(e1.getMessage(), "IO Exception");
		}
	}

}
