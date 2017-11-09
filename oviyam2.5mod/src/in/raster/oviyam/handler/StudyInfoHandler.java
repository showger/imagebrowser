package in.raster.oviyam.handler;

import in.raster.oviyam.PatientInfo;
import in.raster.oviyam.model.StudyModel;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import org.apache.log4j.Logger;

/**
 *
 * @author devishree
 */
public class StudyInfoHandler extends SimpleTagSupport {

	//Initialize logger
	private static Logger log = Logger.getLogger(StudyInfoHandler.class);

	//Attribute variables for this tag
	private String patientId;
	private String study;
	private String dcmURL;
	
	private SimpleDateFormat parseDateFmt = new SimpleDateFormat("yyyyMMdd");		
	private SimpleDateFormat readableDateFmt = new SimpleDateFormat("dd-MM-yyyy");
	

	/**
	 * Setter for property patientId
	 * @param patientId String object registers the patientId.
	 */
	public void setPatientId(String patientId) {
		if(patientId == null) {
			this.patientId = "";
		} else {
			this.patientId = patientId;
		}
	}

	/**
	 * Setter for property study
	 * @param study String object register the study.
	 */
	public void setStudy(String study) {
		this.study = study;
	}

	/**
	 * Setter for property dcmURL
	 * @param dcmURL String object regist the study.
	 */
	public void setDcmURL(String dcmURL) {
		this.dcmURL = dcmURL;
	}

	/**
	 * Overridden Tag handler method. Default processing of the tag.
	 * This method will send the Series information to generate a HTML page during its execution.
	 *
	 * @see javax.servlet.jsp.tasext.SimpleTagSupport#doTag()
	 */
	@Override
	public void doTag() throws JspException, IOException {
		
		PatientInfo patInfo = null;

		try {
			/**
			 * in.raster.oviyam5.PatientInfo used to query cFIND the study information of given
			 * patient.
			 */
			patInfo = new PatientInfo();
			patInfo.callFindWithQuery(patientId, null, dcmURL);
		} catch(Exception e) {
			log.error("Unable to create instance of SeriesInfo and access its callFindWithQuery()" , e);
			return;
		}

		try {
			Calendar today = Calendar.getInstance();			
			
			ArrayList<StudyModel> studyList = patInfo.getStudyList();
			Collections.sort(studyList,new StudyComparator());	

			for(int i=0; i<studyList.size(); i++) {
				StudyModel sm = studyList.get(i);

				getJspContext().setAttribute("studyId", sm.getStudyInstanceUID());
				String study_Date;
				try {
					study_Date = readableDateFmt.format(parseDateFmt.parse(sm.getStudyDate()));
				} catch(Exception ex) {
					study_Date = sm.getStudyDate();
				}
				
				getJspContext().setAttribute("studyDate", study_Date);
				
				if(i==0) {
					getJspContext().setAttribute("dateDesc", sm.getStudyDescription() + " (" + study_Date + ")");
					getJspContext().setAttribute("tooltip", "");
				} else {
					Calendar studyDate = sm.getParsedDate();				
					
					if(!sm.getStudyDate().equals("unknown")) {					
						try {														
							if(today!=null && studyDate!=null) {							
								switch (today.get(Calendar.YEAR)-studyDate.get(Calendar.YEAR)) {
									case 0:		
										switch(today.get(Calendar.MONTH)-studyDate.get(Calendar.MONTH)) {
											case 0:
												switch (today.get(Calendar.DATE)-studyDate.get(Calendar.DATE)) {
												case 0:
													getJspContext().setAttribute("dateDesc", "Today's study" + " (" + study_Date + ")");
													break;
												case 1:
													getJspContext().setAttribute("dateDesc", "Yesterday"  + " (" + study_Date + ")");
													break;
												case 7:
													getJspContext().setAttribute("dateDesc", "Last week" + " (" + study_Date + ")");
													break;
												default:
													getJspContext().setAttribute("dateDesc", (today.get(Calendar.DATE)-studyDate.get(Calendar.DATE)) + " Days ago" + " (" + study_Date + ")");
													break;
												}
												break;
											case 1:
												getJspContext().setAttribute("dateDesc", "Last month" + " (" + study_Date + ")");
												break;
											default:
												getJspContext().setAttribute("dateDesc", (today.get(Calendar.MONTH)-studyDate.get(Calendar.MONTH)) + " Months ago" + " (" + study_Date + ")");
												break;												
										}
										break;
									case 1:
										getJspContext().setAttribute("dateDesc", "Last year" + " (" + study_Date + ")");
										break;
									default:									
										getJspContext().setAttribute("dateDesc", (today.get(Calendar.YEAR)-studyDate.get(Calendar.YEAR)) + " Years ago" + " (" + study_Date + ")");
										break;
								}							
							} else {							
								getJspContext().setAttribute("dateDesc", sm.getStudyDate());
							}						
						} catch(Exception ex) {
							ex.printStackTrace();
							getJspContext().setAttribute("dateDesc", sm.getStudyDate());
						}
					} else {					
						getJspContext().setAttribute("dateDesc", sm.getStudyDate());
					}
					getJspContext().setAttribute("tooltip", sm.getStudyDescription());
				}		
				
				getJspContext().setAttribute("studyDesc", sm.getStudyDescription());
				getJspContext().setAttribute("modalityInStudy", sm.getModalitiesInStudy());
				getJspContext().setAttribute("numberOfImages", sm.getStudyRelatedInstances());
				getJspContext().setAttribute("numberOfSeries", sm.getStudyRelatedSeries());    
				getJspContext().setAttribute("selected", (i==0));
				/**
				 * Process the body of the tag and print it to the response. The numm argument
				 * means the output goes to the response rather than some other writer.
				 */
				getJspBody().invoke(null);
			}
		} catch(Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public class StudyComparator implements Comparator {		

		public int compare(Object o1, Object o2) {
			StudyModel sm1 = (StudyModel) o1;
			StudyModel sm2 = (StudyModel) o2;

			if(sm1.getStudyInstanceUID().equals(study)) {
				return -1;
			} else if(sm2.getStudyInstanceUID().equals(study)) {
				return 1;
			} else if(sm1.getParsedDate()!=null && sm2.getParsedDate()!=null) {
				return sm2.getParsedDate().compareTo(sm1.getParsedDate());
			} else {
				return 0;
			}
		}
	}
}
