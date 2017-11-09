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

package in.raster.oviyam.xml.handler;

import in.raster.oviyam.xml.model.Configuration;
import in.raster.oviyam.xml.model.Language;

import java.io.File;
import java.util.List;
import org.apache.log4j.Logger;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

/**
 * 8 Apr, 2013
 *
 * @author sathees
 */
public class LanguageHandler {

    //Initialize logger
    private static Logger log = Logger.getLogger(LanguageHandler.class);
    private Serializer serializer = null;
    public static File source = null;
    private Configuration config = null;

    public LanguageHandler(String tmpDir) {
        try {
            serializer = new Persister();
            source = new File(new XMLFileHandler().getXMLFilePath(tmpDir));            
            config = serializer.read(Configuration.class, source);            
        } catch (Exception ex) {
				log.error("Unable to read XML document", ex);            
        }
    }

    public void updateLanguage(String language) {
        try {
            List<Language> list = config.getLanguagesList();
            for (Language lang : list) {
                if (lang.getLanguage().equals(language)) {
                    lang.setSelected(true);
                } else {
                    lang.setSelected(false);
                }
            }
            config.setLanguagesList(list);
            serializer.write(config, source);
        } catch (Exception ex) {
            log.error("Unable to modify existing Language settings", ex);
        }
    }

    public List<Language> getLanguage() {
    	List<Language> list = config.getLanguagesList();
    	upgrade(list);
        return list;
    }

    public String getCurrentLanguage() {
        List<Language> langList = config.getLanguagesList();        
        
        for (Language lang : langList) {        	
            if (lang.isSelected()) {            	
                return lang.getLocaleID();            	
            }

        }
        return "";
    }
    
//    public void upgradePreviewConfig() {
//    	if(upgrade) {
//    		System.out.println("****UPGRADING******");
//    		List<Server> serversList = config.getServersList();			
//			for (Server serObj : serversList) {				
//				serObj.setPreviewStatus(true);
//			}
//			try {
//				serializer.write(config, source);
//				upgrade = false;
//			} catch (Exception e) {
//				log.error("Unable to upgrade preview configuration", e);
//			}
//    	}
//    }
    
    
    public void upgrade(List<Language> list) {
    	// For version 2.1 to 2.2
    	for (Language lang : list) {
    		if(lang.getLocaleID().equals("ja_JP")) {
    			return;
    		}
    	}
    	Language language = new Language();
    	language.setCountry("Japan");
    	language.setLanguage("Japanese");
    	language.setLocaleID("ja_JP");
    	language.setSelected(false);
    	list.add(language);    	
    	try {
			serializer.write(config,source);
		} catch (Exception e) {
			log.error("Unable to upgrade to version 2.2 : " + e.getMessage());
		}
    }
}