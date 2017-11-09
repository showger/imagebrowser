/* ***** BEGIN LICENSE BLOCK *****
* Version: MPL 1.1/GPL 2.0/LGPL 2.1
*
* The contents of this file are subject to the Mozilla Public License Version
* 1.1 (the "License"); you may not use this file except in compliance with
* the License. You may obtain a copy of the License at
* http://www.mozilla.org/MPL/
*
* Software distributed under the License is distributed on an "AS IS" basis,
* WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
* for the specific language governing rights and limitations under the
* License.
*
* The Original Code is part of Oviyam, an web viewer for DICOM(TM) images
* hosted at http://skshospital.net/pacs/webviewer/oviyam_0.6-src.zip
*
* The Initial Developer of the Original Code is
* Raster Images
* Portions created by the Initial Developer are Copyright (C) 2014
* the Initial Developer. All Rights Reserved.
*
* Contributor(s):
* Babu Hussain A
* Devishree V
* Meer Asgar Hussain B
* Prakash J
* Suresh V
*
* Alternatively, the contents of this file may be used under the terms of
* either the GNU General Public License Version 2 or later (the "GPL"), or
* the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
* in which case the provisions of the GPL or the LGPL are applicable instead
* of those above. If you wish to allow use of your version of this file only
* under the terms of either the GPL or the LGPL, and not to allow others to
* use your version of this file under the terms of the MPL, indicate your
* decision by deleting the provisions above and replace them with the notice
* and other provisions required by the GPL or the LGPL. If you do not delete
* the provisions above, a recipient may use your version of this file under
* the terms of any one of the MPL, the GPL or the LGPL.
*
* ***** END LICENSE BLOCK ***** */

package in.raster.oviyam.servlet;

import in.raster.oviyam.PatientInfo;
import in.raster.oviyam.model.StudyModel;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author devishree
 */
public class OtherStudiesInfo extends HttpServlet {

    //Initialize the Logger.
    private static Logger log = Logger.getLogger(OtherStudiesInfo.class);
      

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String patID = request.getParameter("patientID");
        String studyUID = request.getParameter("studyUID");
        String dcmURL = request.getParameter("dcmURL");       
        
            
            PatientInfo patientInfo = new PatientInfo(); 
            patientInfo.callFindWithQuery(patID, "", dcmURL);
            JSONArray sArray = new JSONArray();
            

            ArrayList<StudyModel> studyList = patientInfo.getStudyList();
            StudyComparator comparator = new StudyComparator(studyUID);
            Collections.sort(studyList, comparator);
            
            SimpleDateFormat parseDateFmt = new SimpleDateFormat("yyyyMMdd");		
        	SimpleDateFormat readableDateFmt = new SimpleDateFormat("dd-MM-yyyy");
            
            try {
            	Calendar today = Calendar.getInstance();
            	
                for(StudyModel sm : studyList) {
                    if(!sm.getStudyInstanceUID().equals(studyUID)) {                    	
                		JSONObject jsonObj = new JSONObject();                	
                        jsonObj.put("pat_ID", patID);
                        jsonObj.put("pat_Name", sm.getPatientName());   
                        String study_Date;
        				try {
        					study_Date = readableDateFmt.format(parseDateFmt.parse(sm.getStudyDate()));
        				} catch(Exception ex) {
        					study_Date = sm.getStudyDate();
        				}
        				
        				jsonObj.put("studyDate", study_Date);
        				
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
    													jsonObj.put("dateDesc", "Today's study" + " (" + study_Date + ")");
    													break;
    												case 1:
    													jsonObj.put("dateDesc", "Yesterday"  + " (" + study_Date + ")");
    													break;
    												case 7:
    													jsonObj.put("dateDesc", "Last week" + " (" + study_Date + ")");
    													break;
    												default:
    													jsonObj.put("dateDesc", (today.get(Calendar.DATE)-studyDate.get(Calendar.DATE)) + " Days ago" + " (" + study_Date + ")");
    													break;
    												}
    												break;
    											case 1:
    												jsonObj.put("dateDesc", "Last month" + " (" + study_Date + ")");
    												break;
    											default:
    												jsonObj.put("dateDesc", (today.get(Calendar.MONTH)-studyDate.get(Calendar.MONTH)) + " Months ago" + " (" + study_Date + ")");
    												break;												
    										}
    										break;
    									case 1:
    										jsonObj.put("dateDesc", "Last year" + " (" + study_Date + ")");
    										break;
    									default:									
    										jsonObj.put("dateDesc", (today.get(Calendar.YEAR)-studyDate.get(Calendar.YEAR)) + " Years ago" + " (" + study_Date + ")");
    										break;
    								}		
        						}
        					} catch(Exception ex) {
    							ex.printStackTrace();
    							jsonObj.put("dateDesc", sm.getStudyDate());
    						}
        				} else {
        					jsonObj.put("dateDesc", sm.getStudyDate());
        				}
        				
                        jsonObj.put("studyDesc", sm.getStudyDescription());
                        jsonObj.put("modality", sm.getModalitiesInStudy());
                        jsonObj.put("totalIns", sm.getStudyRelatedInstances());
                        jsonObj.put("studyUID", sm.getStudyInstanceUID());                        
                        jsonObj.put("totalSeries", sm.getStudyRelatedSeries());
                        sArray.put(jsonObj);
                    }
                }
            } catch(Exception e) {
                log.error(e);
            }        
            PrintWriter out = response.getWriter();
            out.print(sArray);
            out.close();
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        doGet(request, response);
    } 
    
    private class StudyComparator implements Comparator<StudyModel> {		
    	String study = null;
    	
    	public StudyComparator(String studyUid) {
			this.study = studyUid;
		}	

		@Override
		public int compare(StudyModel sm1, StudyModel sm2) {
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