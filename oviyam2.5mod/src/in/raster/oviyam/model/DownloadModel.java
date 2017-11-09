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
* Yogapraveen K
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

import org.json.JSONObject;

/**
 * 
 * @author yogapraveen
 *
 */

public class DownloadModel implements Serializable {
	
	
	private static final long serialVersionUID = 1L;
	
	// local Variables
	private String sessionID;
	private String downloadFileType;
	private String downloadFilePath;
	private String tempFilePath;
	private String sessionIDFilePath;
	private String zipFilePath;
	private String zipFileName;
	private String fileSize;
	private JSONObject selectedSeries;
	private JSONObject patientInfo;
	
	/**
	 *  Sets the SessionID to create the folder to store the File.
	 * @param sessionID
	 */
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}
	
	/**
	 * Getter for Session ID
	 * 
	 * @return sessionID
	 */
	public String getSessionID() {
		return sessionID;
	}
	
	/**
	 * Set Content Type
	 * @param downloadFileType
	 */
	public void setDownloadFileType(String downloadFileType) {
		this.downloadFileType =downloadFileType;
	}
	
	/**
	 * Getter for downloadFileType
	 * 
	 * @return downloadFileType
	 */
	public String getDownloadFileType() {
		return downloadFileType;
	}
		
	/**
	 * set path to download
	 * @param downloadFilePath
	 */
	public void setDownloadPath(String downloadFilePath) {
		this.downloadFilePath = downloadFilePath;
	}
	
	/**
	 * Getter for DownloadPath
	 * 
	 * @return downloadFilePath
	 */
	public String getDownloadPath() {
		return downloadFilePath;
	}
	
	/**
	 * set temporary folder file path.
	 * @param tempFilePath
	 */
	public void setTempFilePath(String tempFilePath) {
		this.tempFilePath = tempFilePath;
	}

	/**
	 * Getter for temporary folder file path
	 * @return
	 */
	public String getTempFilePath() {
		return tempFilePath;
	}
	
	/**
	 * Set SessionID File Path
	 * @param sessionFilePath
	 */
	public void setSessionIDFilePath(String sessionIDFilePath) {
		this.sessionIDFilePath = sessionIDFilePath;
	}
	
	/**
	 * Getter for SessionFile Path
	 * 
	 * @return sessionFilePath
	 */
	public String getSessionIDFilePath() {
		return sessionIDFilePath;
	}
	
	/**
	 *  Set the Zip File Path which is to be downloaded
	 * @param zipFilePath
	 */
	public void setZipFilePath(String zipFilePath) {
		this.zipFilePath = zipFilePath;
	}
	
	/**
	 * Getter for Zip File Path which is to be downloaded
	 * 
	 * @return zipFilePath
	 */
	public String getZipFilePath() {
		return zipFilePath;
	}
	
	/**
	 * Sets the name in which the file to be downloaded 
	 * @param zipFileName
	 */
	public void setZipFileName(String zipFileName) {
		this.zipFileName= zipFileName;
		this.zipFileName += ".zip";
	}
	
	/**
	 * Getter for DownloadFile Name
	 * 
	 * @return zipFileName
	 */
	public String getZipFileName() {
		return zipFileName;
	}
	
	/**
	 *  File Size of the file which is to be downloaded.
	 * @param fileSize
	 */
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	
	/**
	 * Getter for File Size
	 * 
	 * @return fileSize
	 */ 
	public String getFileSize() {
		return fileSize;
	}
	
	/**
	 *  Selected Series which has to processed one by one.
	 *  contains series and instance information.
	 * @param selectedSeries
	 */
	public void setSelectedSeries(JSONObject selectedSeries) {
		this.selectedSeries = selectedSeries;
	}
	
	/**
	 * Getter for Selected Series JSONObject
	 * 
	 * @return selectedSeries
	 */
	public JSONObject getSelectedSeries() {
		return selectedSeries;
	}
	
	/**
	 * Patient Info JSONObject contains patient and Study info.
	 * @param patientInfo
	 */
	public void setPatientInfo(JSONObject patientInfo) {
		this.patientInfo = patientInfo;
	}
	
	/**
	 * Getter for Patient Info JSONObject
	 * 
	 * @return patientInfo
	 */
	public JSONObject getPatientInfo() {
		return patientInfo;
	}
}
