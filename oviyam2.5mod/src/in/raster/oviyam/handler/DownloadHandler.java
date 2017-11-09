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

package in.raster.oviyam.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;
import org.dcm4che2.data.DicomElement;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.io.DicomInputStream;
import org.json.JSONException;
import org.json.JSONObject;

import in.raster.oviyam.ImageInfo;
import in.raster.oviyam.model.DownloadModel;
import in.raster.oviyam.model.InstanceModel;
import in.raster.oviyam.util.InstanceComparator;
import in.raster.oviyam.xml.handler.LanguageHandler;

/**
 * 
 * @author yogapraveen
 *
 */

public class DownloadHandler {
	
	private static Logger log = Logger.getLogger(DownloadHandler.class);
	
	private String patID;
	private String studyUID;
	private String seriesUID;
	private String dcmURL;
	private String wadoURL;
	private String instanceNumber;
	private String downloadFileType;
	private String downloadDirectory;
	private String multiframeWado;
	private DownloadModel downloadModel =null;
	
	List<String> filesListInDir = new ArrayList<String>();
    String dest = LanguageHandler.source.getAbsolutePath(); 
    String fname = dest.substring(0, dest.indexOf("oviyam2-1-config.xml")-1) + File.separator + "oviyam2";
    
    public DownloadHandler(DownloadModel downloadModel){
    	this.downloadModel = downloadModel;
    }
	
	public void processSeries() throws JSONException, IOException {
		JSONObject selectedSeries = downloadModel.getSelectedSeries();
		JSONObject patientInfo = downloadModel.getPatientInfo();
		patID = patientInfo.getString("patientID").trim();
		String patientName =  patientInfo.getString("patientName").trim().replace(" ", "-");
		studyUID =  patientInfo.getString("studyID").trim();
		dcmURL =  patientInfo.getString("dcmURL").trim();
		wadoURL =  patientInfo.getString("serverURL").trim();
		String studyDesc =  patientInfo.getString("studyDesc").trim().replace("/", "-");;
		String studyDate =  patientInfo.getString("studyDate").trim();
		seriesUID =  selectedSeries.getString("seriesID").trim();
		String seriesDesc =  selectedSeries.getString("seriesDesc").trim().replace("/", "-");;
		String seriesDate =  selectedSeries.getString("seriesDate").trim();
		instanceNumber =  selectedSeries.getString("instanceNumber").trim();
		downloadFileType = downloadModel.getDownloadFileType();
		String sessionID = downloadModel.getSessionID();
		
		filesListInDir = new ArrayList<String>();
		
		seriesDate =  seriesDate.replace("/", "-");
		studyDate = studyDate.replace("/", "-");
		patientName = patientName.replace("/", "-");
		
		downloadDirectory = fname + File.separator + "temp";
		downloadModel.setTempFilePath(downloadDirectory);
		downloadDirectory += File.separator + sessionID;
		downloadModel.setSessionIDFilePath(downloadDirectory);
		downloadDirectory += File.separator + patientName +"("+ patID + ")";
		downloadModel.setZipFileName(patientName + "(" + patID + ")");
		downloadModel.setZipFilePath(downloadDirectory);
		downloadModel.setDownloadPath(downloadDirectory);
		downloadDirectory += File.separator + patientName +"("+ patID + ")";
		downloadDirectory += File.separator + studyDesc + " ( " + studyDate + " ) ";
		downloadDirectory += File.separator + seriesDesc + " ( " + seriesDate + " )";
		
		wadoURL += "?requestType=WADO&studyUID=" + studyUID + "&seriesUID=" + seriesUID;
		
		if(downloadFileType.equalsIgnoreCase("JPEG")) {
			processDownload("JPEG");
		}else if(downloadFileType.equalsIgnoreCase("DICOM")) {
			processDownload("DICOM");
		} else {
			processDownload("JPEG");
			processDownload("DICOM");
		}
	}
	
	private void processDownload(String downloadFileType) throws IOException {
		try {
    		String downloadDirectory = this.downloadDirectory;
    		if(downloadFileType.equalsIgnoreCase("JPEG")) {
				downloadDirectory += File.separator + "JPEG";
			}else {
				downloadDirectory += File.separator + "DICOM";
			}
         
			File dowloadDirectoryFile = new File(downloadDirectory);
			if(!dowloadDirectoryFile.exists()) {
				dowloadDirectoryFile.mkdirs();
			}
    		
    		 ImageInfo imageInfo = new ImageInfo();
             imageInfo.callFindWithQuery(patID, studyUID, seriesUID, null, dcmURL);
             ArrayList<InstanceModel> instanceList = imageInfo.getInstancesList();
             Collections.sort(instanceList, new InstanceComparator());
             for(int i=0; i<instanceList.size(); i++) {
                InstanceModel im = (InstanceModel) instanceList.get(i);
                String objectUID = im.getSopIUID();
                String instanceNo = im.getInstanceNumber();
                if(instanceNo == null || instanceNo == "") {
                	instanceNo = String.valueOf(i);
                }
                int instanceNum = Integer.parseInt(instanceNo);
                String wadoUrl = null;
                multiframeWado = null;
                String downloadFile= downloadDirectory + File.separator + instanceNo ;
                
                wadoUrl = wadoURL + "&objectUID=" + objectUID;
                multiframeWado = wadoUrl; // for processing multiframe Image.
                if(downloadFileType.equals("DICOM")) {
                   wadoUrl += "&contentType=application/dicom"; 	
                } 
              // fetch wado file                
               fetchWADOFile(instanceNum, wadoUrl, downloadFile, downloadFileType);
    		}
            } catch (Exception e) {
            	log.error("ERROR WHILE CREATING WADO URL \n", e);
            }	
	}
	
	/**
	 * fetch Wado file according to the 
	 * @param instanceNo
	 * @param wadoUrl
	 * @param downloadFile
	 * @param downloadFileType
	 * @throws Exception
	 */
	private void fetchWADOFile(int instanceNo, String wadoUrl, String downloadFile, String downloadFileType) throws Exception {
		InputStream inputStream = null;
		OutputStream outputStream = null;
		URL url = new URL(wadoUrl);
		String multiframeWado = this.multiframeWado;
        inputStream = url.openStream();
		 if(!(instanceNumber.equalsIgnoreCase(""))) { 
         	int instNumber = Integer.parseInt(instanceNumber);
         	if(instNumber == instanceNo) {
         		if(downloadFileType.equalsIgnoreCase("JPEG")) {
         			 multiframeWado += "&contentType=application/dicom";
         	         url = new URL(multiframeWado);			// to get Number of frames in multiframe image.
         	         InputStream multiframeInputStream = url.openStream();
         	         DicomInputStream dicomInputStream = new DicomInputStream(multiframeInputStream);
         	         DicomObject dcmobj = dicomInputStream.readDicomObject();
         	         DicomElement totalFrameEle = dcmobj.get(Tag.NumberOfFrames);
         	         String totFrame = totalFrameEle != null ? new String(totalFrameEle.getBytes()) : "";
         	         int totalFrame = Integer.parseInt(totFrame.trim());
         	         File multiFolder =  new File(downloadFile);
         	         if(!multiFolder.exists()) { // create folder with instance number.
             			multiFolder.mkdirs();
         	         }
         	         for(int j=0;j<totalFrame;j++) {
             			String frameNo = String.valueOf(j+1);
                 		String multiFile = downloadFile + File.separator + frameNo + ".jpg";
                 		String multiFUrl = wadoUrl + "&frameNumber=" + frameNo.trim();
                 		url = new URL(multiFUrl);
                 		inputStream = url.openStream();
                 		outputStream = new FileOutputStream(multiFile);
             			
             	        byte[] buffer = new byte[2048];
             	        int length;
             	        while ((length = inputStream.read(buffer)) != -1) {
             	        	outputStream.write(buffer, 0, length);
             	        }
             	        outputStream.close();
         	         }
         	         dicomInputStream.close();
         		} else {
         			downloadFile += ".dcm";
         			outputStream = new FileOutputStream(downloadFile);
         			byte[] buffer = new byte[2048];
         			int length;
         			while ((length = inputStream.read(buffer)) != -1) {
         				outputStream.write(buffer, 0, length);
         			}
         			outputStream.close();
         		}
         	}
         } else {
         	if(downloadFileType.equalsIgnoreCase("JPEG")) {
         		downloadFile += ".jpg";
         	} else {
         		downloadFile += ".dcm";
         	}
         	outputStream = new FileOutputStream(downloadFile);
             byte[] buffer = new byte[2048];
           	int length;
           	while ((length = inputStream.read(buffer)) != -1) {
           		outputStream.write(buffer, 0, length);
           	}
             outputStream.close();
        }
	}
	/**
	 * 
	 * Creating a Zip file for the created folder.
	 * 
	 */
	public void  createZipFile() {
        try {
        	String zipDirName = downloadModel.getZipFilePath();
        	File dir = new File(zipDirName);
            populateFilesList(dir);
            zipDirName = isZipFileExist(zipDirName,0);
            zipDirName += ".zip";
            FileOutputStream fileOutputStream = new FileOutputStream(zipDirName);
            ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
            for(String filePath : filesListInDir){
                ZipEntry zipEntry = new ZipEntry(filePath.substring(dir.getAbsolutePath().length()+1, filePath.length()));
                zipOutputStream.putNextEntry(zipEntry);
                FileInputStream fileInputStream = new FileInputStream(filePath);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fileInputStream.read(buffer)) > 0) {
                    zipOutputStream.write(buffer, 0, length);
                }
                zipOutputStream.closeEntry();
                fileInputStream.close();
            }
            zipOutputStream.close();
            fileOutputStream.close();
            downloadModel.setZipFilePath(zipDirName);
        } catch (Exception e) {
            log.error("ERROR WHILE ZIPPING ",e);
        }
        
    }
	
	/**
	 * 
	 * gets Each file in the directory and stores in the filesListInDir list
	 * 
	 * @param dir
	 * @throws IOException
	 */
	
	 private void populateFilesList(File dir) throws IOException {
	        File[] files = dir.listFiles();
	        for(File file : files){
	            if(file.isFile()) filesListInDir.add(file.getAbsolutePath());
	            else populateFilesList(file);
	        }
	 }
	 
	 /**
	  * 
	  * checks whether a zip file name is already exists or not.
	  * If exist, then creates a new name.
	  * 
	  * @param zipDirName
	  * @param i
	  * @return zipDirName
	  */
	 private String isZipFileExist(String zipDirName, int i) {
	    	String newName=zipDirName;
	    	if(i!=0) {
	    		newName = zipDirName + "(" + String.valueOf(i) + ")";
	    	}
	    	String zipFileName = newName +".zip";
	    	File isZipFileExist = new File(zipFileName);
	    	if(isZipFileExist.exists()) {
	    		i++;
	    		newName = isZipFileExist(zipDirName, i);
	    	}else {
	    		return newName;
	    	}
	    	return newName;
	}
	
	 /**
	  * 
	  * deletes the created folder in which the images are stored 
	  * after the creation of the zip file.
	  * 
	  * @param dir
	  * @throws IOException
	  */
	 
	public void deleteFile(File dir) throws IOException {
	        File[] fileList = dir.listFiles();
	        if(fileList!=null) {
	        	for(File file : fileList){
	        		if(file.isFile()) {
	        			file.delete();  
	        		}
	        		else deleteFile(file);
	        	}
	        }
	        dir.delete();
	}
	
	/**
	 * 
	 * calculates the size of the Zip file.
	 * 
	 */
	
	public void calculateFileSize() {
		File zipFile = new File(downloadModel.getZipFilePath());
		double fileSize = 0;
		if(zipFile.exists()) {
			double bytes = zipFile.length();
			fileSize = (bytes/1024)/1024; 
		}
		String size = String.format("%.2f", fileSize) + "MB";
		downloadModel.setFileSize(size);
	}
}
