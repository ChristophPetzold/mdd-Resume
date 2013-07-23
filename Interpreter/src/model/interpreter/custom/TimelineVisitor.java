/**
 * 
 */
package model.interpreter.custom;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import model.elements.Date;
import model.elements.Education;
import model.elements.Employment;
import model.elements.Project;
import model.help.ResumeModelHelper;
import model.interpreter.core.FetchVisitor;

/**
 * This interpreter transforms the consumed model into a JSONP-Format. For each resume in the model one json file will
 * be created. These files can be used as input for the great "TimmelineJS"-Tool (see <a
 * href="http://timeline.verite.co/">http://timeline.verite.co/</a>).
 * 
 * @author Christoph Petzold
 * 
 */
public class TimelineVisitor extends FetchVisitor {

	private final String	JSON_FIELD_HEADLINE		= Messages.getString("TimelineVisitor.json.headline");				//$NON-NLS-1$
	private final String	JSON_FIELD_TYPE			= Messages.getString("TimelineVisitor.json.type");					//$NON-NLS-1$
	private final String	JSON_FIELD_TEXT			= Messages.getString("TimelineVisitor.json.text");					//$NON-NLS-1$
	private final String	JSON_FIELD_ASSET		= Messages.getString("TimelineVisitor.json.asset");				//$NON-NLS-1$
	private final String	JSON_FIELD_MEDIA		= Messages.getString("TimelineVisitor.json.media");				//$NON-NLS-1$
	private final String	JSON_FIELD_CREDIT		= Messages.getString("TimelineVisitor.json.credit");				//$NON-NLS-1$
	private final String	JSON_FIELD_CAPTION		= Messages.getString("TimelineVisitor.json.caption");				//$NON-NLS-1$
	private final String	JSON_FIELD_START		= Messages.getString("TimelineVisitor.json.startDate");			//$NON-NLS-1$
	private final String	JSON_FIELD_END			= Messages.getString("TimelineVisitor.json.endDate");				//$NON-NLS-1$
	private final String	JSON_FIELD_TAG			= Messages.getString("TimelineVisitor.json.tag");					//$NON-NLS-1$
	private final String	JSON_FIELD_THUMBNAIL	= Messages.getString("TimelineVisitor.json.thumbnail");			//$NON-NLS-1$

	private final String	TAG_EMPLOYMENT			= Messages.getString("TimelineVisitor.tag.employment");			//$NON-NLS-1$
	private final String	TAG_EDUCATION			= Messages.getString("TimelineVisitor.tag.education");				//$NON-NLS-1$
	private final String	TAG_EMPLOYMENT_PROJECT	= Messages.getString("TimelineVisitor.tag.project.emplpoyment");	//$NON-NLS-1$
	private final String	TAG_EDUCATION_PROJECT	= Messages.getString("TimelineVisitor.tag.project.education");		//$NON-NLS-1$

	private final String	JSON_DATE_PATTERN		= Messages.getString("TimelineVisitor.json.datePattern");			//$NON-NLS-1$
	private final String	JSON_SEP				= Messages.getString("TimelineVisitor.16");						//$NON-NLS-1$

	@Override
	public void perform(String destination) {

		String result = genreateJsonFile(destination);
		save(destination, result);
	}

	/**
	 * @param destination
	 */
	private String genreateJsonFile(String destination) {
		String jsonString = Messages.getString("TimelineVisitor.json.opening"); //$NON-NLS-1$

		jsonString += generateJsonIntro() + JSON_SEP;

		jsonString += generateJsonTimeEvents();

		// closing
		jsonString += Messages.getString("TimelineVisitor.json.closing"); //$NON-NLS-1$

		return cleanUp(jsonString);

	}

	/**
	 * @param institutionDates
	 * @return
	 */
	protected String cleanUp(String jsonString) {

		// ", , " -> ", "
		while (jsonString.contains(JSON_SEP + JSON_SEP)) {
			jsonString = jsonString.replaceAll(JSON_SEP + JSON_SEP, JSON_SEP);
		}

		jsonString = jsonString.replaceAll(JSON_SEP + "}", "}"); //$NON-NLS-1$ //$NON-NLS-2$
		jsonString = jsonString.replaceAll(JSON_SEP + "]", "]"); //$NON-NLS-1$ //$NON-NLS-2$

		return jsonString;
	}

	/**
	 * @return
	 */
	private String generateJsonIntro() {

		String intro = addJsonElement(JSON_FIELD_HEADLINE, this.person.getFullName());

		intro += addJsonElement(JSON_FIELD_TYPE, Messages.getString("TimelineVisitor.timeline.type")); //$NON-NLS-1$
		intro += addJsonElement(
				JSON_FIELD_TEXT,
				String.format(
						Messages.getString("TimelineVisitor.timeline.introduction.text"), person.getFullName(), person //$NON-NLS-1$
								.getBirthday().getDateString(
										Messages.getString("TimelineVisitor.timeline.introduction.birthDatePattern")), person.getBirthplace())); //$NON-NLS-1$
		intro += addJsonAsset(
				person.getPicturePath(),
				Messages.getString("TimelineVisitor.26"), //$NON-NLS-1$
				String.format(
						Messages.getString("TimelineVisitor.introduction.imgAlt"), person.getFirstName(), person.getName()), null); //$NON-NLS-1$

		return intro;
	}

	/**
	 * Generate a date block based on employment and education events
	 * 
	 * @return JSON-"Date"-block
	 */
	private String generateJsonTimeEvents() {

		String dates = "["; //$NON-NLS-1$
		dates += addEmploymentDates();
		dates += addEducationDates();
		dates += "]"; //$NON-NLS-1$

		return String.format(Messages.getString("TimelineVisitor.timeline.datesArray"), dates); //$NON-NLS-1$
	}

	/**
	 * Generate a list of JSON-dates based on employment events
	 * 
	 * @return list of JSON-dates
	 */
	private String addEmploymentDates() {
		String institutionDates = ""; //$NON-NLS-1$

		for (Employment emp : employmentInstitutions) {
			String text = ""; //$NON-NLS-1$

			text += String.format(
					Messages.getString("TimelineVisitor.employment.loacation"), emp.getAddress().toString()); //$NON-NLS-1$
			text += String.format(Messages.getString("TimelineVisitor.employment.position"), emp.getPosition()); //$NON-NLS-1$
			text += String.format(Messages.getString("TimelineVisitor.employment.description"), emp.getDescription()); //$NON-NLS-1$

			String asset = addJsonAsset(
					emp.getMediaUrl(),
					Messages.getString("TimelineVisitor.employment.asset.credit"), String.format(Messages.getString("TimelineVisitor.employment.asset.caption"), emp.getMediaUrl()), ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

			institutionDates += addJSONDate(emp.getStartDate(), emp.getEndDate(), emp.getName(), text, TAG_EMPLOYMENT,
					asset);

			institutionDates += JSON_SEP;
			institutionDates += addInstitutionProjects(emp.getModelId(), emp.getName(), TAG_EMPLOYMENT_PROJECT);
			institutionDates += JSON_SEP;
		}

		return institutionDates;
	}

	/**
	 * Generate a list of JSON-dates based on education events
	 * 
	 * @return list of JSON-dates
	 */
	private String addEducationDates() {
		String educationDates = ""; //$NON-NLS-1$

		for (Education edu : educationInstitutions) {
			String text = ""; //$NON-NLS-1$

			text += String
					.format(Messages.getString("TimelineVisitor.education.location"), edu.getAddress().toString()); //$NON-NLS-1$
			text += String.format(Messages.getString("TimelineVisitor.education.type"), edu.getType()); //$NON-NLS-1$
			text += String.format(Messages.getString("TimelineVisitor.education.description"), edu.getDescription()); //$NON-NLS-1$

			String asset = addJsonAsset(
					edu.getMediaUrl(),
					"", String.format(Messages.getString("TimelineVisitor.education.asset.caption"), edu.getMediaUrl()), ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

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

		String projectsJSON = ""; //$NON-NLS-1$

		// now we are looking for the projects which are done at the current institution (modelId)
		for (Project project : this.projects) {

			if (this.connections.containsKey(instituteId + "." + project.getModelId())) { //$NON-NLS-1$

				String projectJSON = ""; //$NON-NLS-1$

				String text = ""; //$NON-NLS-1$

				text += String.format(Messages.getString("TimelineVisitor.project.title"), project.getName()); //$NON-NLS-1$
				text += String.format(Messages.getString("TimelineVisitor.project.customer"), project.getCustomer()); //$NON-NLS-1$
				text += String.format(
						Messages.getString("TimelineVisitor.project.description"), project.getDescription()); //$NON-NLS-1$

				String asset = addJsonAsset(
						project.getMediaUrl(),
						"", String.format(Messages.getString("TimelineVisitor.project.asset.caption"), project.getMediaUrl()), //$NON-NLS-1$ //$NON-NLS-2$
						""); //$NON-NLS-1$

				projectJSON += addJSONDate(
						project.getStartDate(),
						project.getEndDate(),
						String.format(
								Messages.getString("TimelineVisitor.project.head"), project.getType(), instituteName), text, tag, asset); //$NON-NLS-1$

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
		String date = "{"; //$NON-NLS-1$

		date += addJsonElement(JSON_FIELD_START, start.getDateString(JSON_DATE_PATTERN));

		if (end.getDate() != null) {
			date += addJsonElement(JSON_FIELD_END, end.getDateString(JSON_DATE_PATTERN));
		}

		date += addJsonElement(JSON_FIELD_HEADLINE, headline);
		date += addJsonElement(JSON_FIELD_TEXT, text);
		date += addLastJsonElement(JSON_FIELD_TAG, tag);

		if (!asset.isEmpty()) {
			date += JSON_SEP + asset;
		}

		return date + "}"; //$NON-NLS-1$
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
		return String.format(Messages.getString("TimelineVisitor.json.elementBlock"), elementName, value); //$NON-NLS-1$
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

		String asset = ""; //$NON-NLS-1$

		asset += addJsonElement(JSON_FIELD_MEDIA, media);
		asset += addJsonElement(JSON_FIELD_CREDIT, credit);

		if (thumbnail != null) {
			asset += addJsonElement(JSON_FIELD_CAPTION, caption);
			asset += addLastJsonElement(JSON_FIELD_THUMBNAIL, thumbnail);
		} else {
			asset += addLastJsonElement(JSON_FIELD_CAPTION, caption);
		}

		return String.format(Messages.getString("TimelineVisitor.json.assetBlock"), asset); //$NON-NLS-1$
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
			String personsFolder = String.format(
					Messages.getString("TimelineVisitor.output.folder"), destination, File.separator, person.getName()); //$NON-NLS-1$
			File folder = new File(personsFolder);
			folder.mkdir();

			String fileName = String.format(Messages.getString("TimelineVisitor.output.filePath"), personsFolder,
					File.separator, Messages.getString("TimelineVisitor.outout.filename"),
					Messages.getString("TimelineVisitor.output.fileExtension")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			File file = new File(fileName);
			file.createNewFile();
			FileWriter fw = new FileWriter(file);

			fw.write(content);
			fw.close();

		} catch (IOException e) {
			ResumeModelHelper.err(e, "IO Exception - save"); //$NON-NLS-1$
		}
	}

}
