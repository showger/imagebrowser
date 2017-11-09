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

import in.raster.oviyam.ImageInfo;
import in.raster.oviyam.model.InstanceModel;
import in.raster.oviyam.delegate.ImageOrientation;
import in.raster.oviyam.util.InstanceComparator;
import in.raster.oviyam.xml.handler.LanguageHandler;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.InputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.net.URL;
import org.dcm4che2.io.DicomInputStream;
import org.dcm4che2.data.DicomElement;
import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.UID;

/**
 *
 * @author asgar
 */
public class InstanceServlet extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;


    /**
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String patID = request.getParameter("patientId");
        String studyUID = request.getParameter("studyUID");
        String seriesUID = request.getParameter("seriesUID");
        String dcmURL = request.getParameter("dcmURL");
        String wadoURL = request.getParameter("serverURL");

        ImageInfo imageInfo = new ImageInfo();
        imageInfo.callFindWithQuery(patID, studyUID, seriesUID, null, dcmURL);
        ArrayList<InstanceModel> instanceList = imageInfo.getInstancesList();
        Collections.sort(instanceList, new InstanceComparator());


        String fname = "";
        if( !(!wadoURL.equals("C-MOVE") && !wadoURL.equals("C-GET")) ) {
            String dest = LanguageHandler.source.getAbsolutePath();            
            fname = dest.substring(0, dest.indexOf("oviyam2-1-config.xml")-1) + File.separator + "oviyam2";
            fname += File.separator + studyUID;
        } else {
            wadoURL += "?requestType=WADO&contentType=application/dicom&studyUID=" + studyUID + "&seriesUID=" + seriesUID;
        }

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObj = null;

        InputStream is = null;
        DicomInputStream dis = null;

        try {
            for(int i=0; i<instanceList.size(); i++) {
                InstanceModel im = (InstanceModel) instanceList.get(i);
                jsonObj = new JSONObject();
                String objectUID = im.getSopIUID();
//                if(!im.getSopClassUID().equals(UID.RawDataStorage)) {
	                jsonObj.put("SopUID", objectUID);
	                jsonObj.put("InstanceNo", im.getInstanceNumber());
	                jsonObj.put("SopClassUID", im.getSopClassUID());
	
	                String UrlTmp = null;
	
	                try {
	                    if( !(!wadoURL.equals("C-MOVE") && !wadoURL.equals("C-GET")) ) {
	                        UrlTmp = fname + File.separator + objectUID;
	                        is = new FileInputStream(new File(UrlTmp));
	                    } else {
	                        UrlTmp = wadoURL + "&objectUID=" + objectUID + "&transferSyntax=1.2.840.10008.1.2.1";
	                        URL url = new URL(UrlTmp);
	                        is = url.openStream();
	                    }
	
	                    dis = new DicomInputStream(is);
	
	                    DicomObject dcmObj = dis.readDicomObject();
	                    DicomElement wcDcmElement = dcmObj.get(Tag.WindowCenter);
	                    DicomElement wwDcmElement = dcmObj.get(Tag.WindowWidth);
	                    DicomElement rowDcmElement = dcmObj.get(Tag.Rows);
	                    DicomElement colDcmElement = dcmObj.get(Tag.Columns);
	                    DicomElement imgOrientation = dcmObj.get(Tag.ImageOrientationPatient);
	                    DicomElement imgPosition = dcmObj.get(Tag.ImagePositionPatient);
	                    DicomElement sliceThick = dcmObj.get(Tag.SliceThickness);
	                    DicomElement frameOfRefUID = dcmObj.get(Tag.FrameOfReferenceUID);
	                    DicomElement pixelSpacingEle = dcmObj.get(Tag.PixelSpacing);
	                    DicomElement imgPixelSpacingEle = dcmObj.get(Tag.ImagerPixelSpacing);
	                    DicomElement totalFramesEle = dcmObj.get(Tag.NumberOfFrames);
	                    DicomElement rescaleSlope = dcmObj.get(Tag.RescaleSlope);
	                    DicomElement rescaleIntercept= dcmObj.get(Tag.RescaleIntercept);
	                    DicomElement bitsStored= dcmObj.get(Tag.BitsStored);
	                    DicomElement photometricInterpretation= dcmObj.get(Tag.PhotometricInterpretation);
	                    DicomElement pixelRep = dcmObj.get(Tag.PixelRepresentation);
	                    DicomElement frmTime = dcmObj.get(Tag.FrameTime);
	
	                    //To get the Image Type (LOCALIZER / AXIAL / OTHER)
	                    DicomElement imageType = dcmObj.get(Tag.ImageType);
	                    String image_type = "";
	                    if(imageType != null) {
	                        image_type = new String(imageType.getBytes());
	                        String[] imageTypes = image_type.split("\\\\");
	                        if(imageTypes.length >= 3) {
	                            image_type = imageTypes[2];
	                        }
	                    }
	
	                    //To get the Referenced SOP Instance UID
	                    DicomElement refImageSeq = dcmObj.get(Tag.ReferencedImageSequence);
	                    DicomElement refSOPInsUID = null;
	                    String referSopInsUid = "";
	                    
	                    if(refImageSeq != null) {
	                        if(refImageSeq.hasItems()) {
	                        	int cnt = refImageSeq.countItems();                        	
	                        	for (int j = 0; j < cnt; j++) {
	                        		DicomObject dcmObj1 = refImageSeq.getDicomObject(j);
	                        		refSOPInsUID = dcmObj1.get(Tag.ReferencedSOPInstanceUID);                        		
	                        		 if(referSopInsUid!="") {
	                        			 if(refSOPInsUID!=null) {
	                        				 referSopInsUid+="," + new String(refSOPInsUID.getBytes());
	                        			 }
	                        		 } else {
	                        			 referSopInsUid = refSOPInsUID != null ? new String(refSOPInsUID.getBytes()) : "";
	                        		 }                                
								}
	                        }
	                    }                    
	
	                    String windowCenter = wcDcmElement != null ? new String(wcDcmElement.getBytes()) : "";
	                    String windowWidth = wwDcmElement != null ? new String(wwDcmElement.getBytes()) : "";
	                    int nativeRows = rowDcmElement != null ? rowDcmElement.getInt(false) : 0;
	                    int nativeColumns = colDcmElement != null ? colDcmElement.getInt(false) : 0;
	                    String imgOrient = imgOrientation != null ? new String(imgOrientation.getBytes()) : "";
	                    String sliceThickness = sliceThick != null ? new String(sliceThick.getBytes()) : "";
	                    String forUID = frameOfRefUID != null ? new String(frameOfRefUID.getBytes()) : "";
	                    String resclaeslp = rescaleSlope!=null ? new String(rescaleSlope.getBytes()) : "1.0";
	                    String resclaeintercept = rescaleIntercept!=null ? new String(rescaleIntercept.getBytes()) : "0.0";
	                    int bitsStored1 = bitsStored!=null ? bitsStored.getInt(false) : 12; 
	                    boolean monochrome = photometricInterpretation!=null ? new String(photometricInterpretation.getBytes()).trim().equalsIgnoreCase("MONOCHROME1") ? true : false : false;	                    
	                    int pixRep = pixelRep!=null ? pixelRep.getInt(false) : -1;
	                    double frameTime = frmTime!=null ? frmTime.getDouble(false) : 0;
	
	                    String sliceLoc = "";
	                    String imagePosition = "";
	                    if(imgPosition != null) {
	                        imagePosition =  new String(imgPosition.getBytes());
	                        sliceLoc = imagePosition.substring(imagePosition.lastIndexOf("\\")+1);
	                    }
	
	                    String pixelSpacing = pixelSpacingEle != null ? new String(pixelSpacingEle.getBytes()) : "";
	                    String imgPixelSpacing = imgPixelSpacingEle != null ? new String(imgPixelSpacingEle.getBytes()) : "";
	                    String totalFrames = totalFramesEle != null ? new String(totalFramesEle.getBytes()) : "";
	
	
	                    jsonObj.put("windowCenter", windowCenter.replaceAll("\\\\", "|"));
	                    jsonObj.put("windowWidth", windowWidth.replaceAll("\\\\", "|"));
	                    jsonObj.put("nativeRows", nativeRows);
	                    jsonObj.put("nativeColumns", nativeColumns);
	
	                    if(imgOrient.length() > 0)
	                        jsonObj.put("imageOrientation", ImageOrientation.getOrientation(imgOrient));
	                    else
	                        jsonObj.put("imageOrientation", imgOrient);
	
	                    jsonObj.put("sliceLocation", sliceLoc);
	                    jsonObj.put("sliceThickness", sliceThickness);
	                    jsonObj.put("frameOfReferenceUID", forUID.replaceAll("\u0000", ""));
	
	                    jsonObj.put("imagePositionPatient", imagePosition);
	                    jsonObj.put("imageOrientPatient", imgOrient);
	                    jsonObj.put("pixelSpacing", pixelSpacing);
	                    jsonObj.put("imagerPixelSpacing", imgPixelSpacing);
	                    jsonObj.put("refSOPInsUID", referSopInsUid.replaceAll("\u0000", ""));
	                    jsonObj.put("imageType", image_type);
	                    jsonObj.put("numberOfFrames", totalFrames);
	                    
	                    jsonObj.put("rescaleSlope", resclaeslp);
	                    jsonObj.put("rescaleIntercept", resclaeintercept);
	                    jsonObj.put("BitsStored", bitsStored1);
	                    jsonObj.put("monochrome1", monochrome);
	                    jsonObj.put("PixelRepresentation", pixRep);
	                    jsonObj.put("frameTime", frameTime);
	
	                    dis.close();
	                    is.close();
	                } catch(Exception e) {
	                    is.close();
	                    dis.close();                  
	                    e.printStackTrace();
	                }
	
	                jsonArray.put(jsonObj);
	            }            
//        	}

            PrintWriter out = response.getWriter();
            out.print(jsonArray);
            out.close();
        } catch(Exception ex) {            
            ex.printStackTrace();
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

}