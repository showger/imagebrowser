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

package in.raster.oviyam.model;

import java.io.Serializable;
import java.util.Calendar;
import org.dcm4che.data.Dataset;
import org.dcm4che.dict.Tags;

/**
 *
 * @author asgar
 */
public class StudyModel implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//local variables
    private String patientID;
    private String patientName;
    private String patientGender;
    private String patientBirthDate;
    private String physicianName;
    private String studyDate;    
    private Calendar parsedDate = null;
    private String studyTime;
    private String studyDescription;
    private String modalitiesInStudy;
    //private String[] modality;
    private String studyRelatedInstances;
    private String accessionNumber;
    private String studyInstanceUID;
    private String studyRelatedSeries;

    //default constructor
    public StudyModel() { }

    //constructor
    public StudyModel(Dataset ds) {
        patientID = ds.getString(Tags.PatientID);
        patientName = ds.getString(Tags.PatientName).replace("^", " ");
        patientGender = ds.getString(Tags.PatientSex)!=null ? ds.getString(Tags.PatientSex) : "unknown";
        patientBirthDate = ds.getString(Tags.PatientBirthDate) != null ? ds.getString(Tags.PatientBirthDate) : "";
        physicianName = ds.getString(Tags.ReferringPhysicianName) != null ? ds.getString(Tags.ReferringPhysicianName) : "";
        studyDate = ds.getString(Tags.StudyDate)!=null ? ds.getString(Tags.StudyDate) : "[No study date]";
        parsedDate = Calendar.getInstance();        
        
        if(studyDate!=null && !studyDate.contains("[No study date]")) {
        	try {        		
        		parsedDate.set(Integer.parseInt(studyDate.substring(0,4)), Integer.parseInt(studyDate.substring(4,6))-1, Integer.parseInt(studyDate.substring(6,8)));        		
        	} catch(Exception ex) {
        		ex.printStackTrace();
        		parsedDate = null;
        	}
        } else {        	
        	parsedDate = null;
        }
        
        studyTime = ds.getString(Tags.StudyTime)!=null ? ds.getString(Tags.StudyTime) : "";
        studyDescription = ds.getString(Tags.StudyDescription) != null ? ds.getString(Tags.StudyDescription).replace("^", " ") : "[No study description]";

        String[] modalities = ds.getStrings(Tags.ModalitiesInStudy) != null ? ds.getStrings(Tags.ModalitiesInStudy) : null;
        if(modalities != null) {
            for(int i=0; i<modalities.length; i++) {
                if(i==0) {
                    modalitiesInStudy = modalities[i];
                } else {
                    modalitiesInStudy += "\\" + modalities[i];
                }
            }
        }

        studyRelatedInstances = ds.getString(Tags.NumberOfStudyRelatedInstances) != null ? ds.getString(Tags.NumberOfStudyRelatedInstances) : "";
        accessionNumber = ds.getString(Tags.AccessionNumber) != null ? ds.getString(Tags.AccessionNumber) : "";
        studyInstanceUID = ds.getString(Tags.StudyInstanceUID);
        studyRelatedSeries = ds.getString(Tags.NumberOfStudyRelatedSeries) != null ? ds.getString(Tags.NumberOfStudyRelatedSeries) : "";
    }

    public String getAccessionNumber() {
        return accessionNumber;
    }

    public String getModalitiesInStudy() {
        return modalitiesInStudy;
    }

    public String getPatientBirthDate() {
        return patientBirthDate;
    }

    public String getPatientGender() {
        return patientGender;
    }

    public String getPatientID() {
        return patientID;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getPhysicianName() {
        return physicianName;
    }

    public String getStudyDate() {
        return studyDate;
    }

    public String getStudyDescription() {
        return studyDescription;
    }

    public String getStudyInstanceUID() {
        return studyInstanceUID;
    }

    public String getStudyRelatedInstances() {
        return studyRelatedInstances;
    }

    public String getStudyRelatedSeries() {
        return studyRelatedSeries;
    }

    public String getStudyTime() {
        return studyTime;
    }
    
    public Calendar getParsedDate() {
    	return parsedDate;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StudyModel other = (StudyModel) obj;
        if ((this.studyInstanceUID == null) ? (other.studyInstanceUID != null) : !this.studyInstanceUID.equals(other.studyInstanceUID)) {
            return false;
        }
        return true;
    }

}