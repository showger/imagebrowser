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

package in.raster.oviyam.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

/**
 * 
 * @author Yogapraveen
 * 
 * Servlet implementation class DownloadServlet
 */
public class DownloadServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private static Logger log = Logger.getLogger(DownloadServlet.class);
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String requestedTime = request.getParameter("requestedTime");
		String download  = request.getParameter("download");
		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession(false);
		String downloadPath = (String) session.getAttribute(requestedTime + "_downloadPath");
		String sessionIDFilePath = (String) session.getAttribute(requestedTime + "_sessionIDFilePath");
		String fileName = (String) session.getAttribute(requestedTime + "_fileName");
		String tempFilePath = (String) session.getAttribute("tempFilePath");
		downloadPath = downloadPath.trim();
		sessionIDFilePath = sessionIDFilePath.trim();
		fileName = fileName.trim();
		File downloadFile = new File(downloadPath);
		try {
			if(download.equalsIgnoreCase("yes")) {
			    response.setContentType("APPLICATION/OCTET-STREAM");
			    response.setHeader("Content-Disposition","attachment; filename=\"" + fileName + "\"");
			    FileInputStream fileInputStream=new FileInputStream(downloadPath);
			    
			    int i;
			    while ((i=fileInputStream.read()) != -1) {
			      out.write(i);
			    }
			    fileInputStream.close();			   
			    log.info("DOWNLOAD PROCESS COMPLETED FOR FILE "+ downloadFile);
			} 
			
		}catch (Exception e) {
			log.error("ERROR IN FILE DOWNLOAD...",e);
		}finally {
			if(downloadFile.exists()){
		        downloadFile.delete();
		    }
		    deleteSessionFolders(tempFilePath);
		    log.info("SESSION DIRECTORY DELETED...");
		}
		
	}
	
	/**
	 * deletes the Files and folders which are created before numOfDays days.
	 * here values of numOfDays is 7(days)
	 * @param tempFilePath
	 * @throws IOException
	 */
	private void deleteSessionFolders(String tempFilePath) throws IOException {
		File tempFolder = new File(tempFilePath);
		File[] sessionFolders = tempFolder.listFiles();
		int numOfDays = 7;
		for(File folder : sessionFolders) {
			File[] zipFiles = folder.listFiles();
			for(File zipfile : zipFiles) {
				long diff = new Date().getTime() - zipfile.lastModified();
				if (diff > numOfDays * 24 * 60 * 60 * 1000) {
					zipfile.delete();
				}
			}
			if(folder.list().length<=0){
		        folder.delete();
		    }	
		}
        
}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
