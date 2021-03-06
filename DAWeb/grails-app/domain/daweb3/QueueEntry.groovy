package daweb3
/*
 DA-NRW Software Suite | ContentBroker
 Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
 Universität zu Köln

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
/**
 * SIP to AIP or AIP - DIP monitoring. 
@Author Jens Peters
@Author Scuy
*/
import java.text.SimpleDateFormat;
import org.hibernate.criterion.CriteriaSpecification

class QueueEntry {
	
	int id
	String status
	String initialNode
	String created
	String modified
	Object obj
	String question
	String answer

    static constraints = {
		status(nullable:false)
		created(nullable:true)
		modified(nullable:true)
		question(nullable:true)
		answer(nullable:true)
	}
	
	static mapping = {
		table 'queue'
		version false
		id column: 'id'
		obj column: 'objects_id'
		// necessary because dateCreated and dateModified seem to be reserved by grails
		created column: 'date_created'
		modified column: 'date_modified'
	}
	
	
	static QueueEntry getAllQueueEntriesForShortNameAndUrn(String shortName, String urn) {
		return createCriteria().list  {
			createAlias('obj', 'o', CriteriaSpecification.INNER_JOIN)
			createAlias('o.user', 'user', CriteriaSpecification.INNER_JOIN)
			eq("user.shortName", shortName)
			eq("o.urn", urn)
		}
	}
	
	
	def getFormattedCreatedDate() {
			
		if (created!=null && created!="" && created!="NULL" && created.length()>5) {
			String sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.valueOf(created).longValue()*1000L) ) 
			return sdf
		}
		return "";
		
	}

	def getFormattedModifiedDate() {
		
		if (modified!=null && modified!="" && modified!="NULL" && modified.length()>5) {
			String sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(Long.valueOf(modified).longValue()*1000L) )
			return sdf
		}
		return ""
		
	}
	
	String toString() {
		return "Status (Queue)";
	}
	
	Integer getStatusAsInteger() {
		return Integer.parseInt(status);
	}
	
	/**
	 * @author jpeters
	 * Show Deletion button
	 * @return
	 */
	boolean showDeletionButton() {
		def checkfor = ["1","3","4"]
		def ch = status[-1]
		if (checkfor.contains(ch) && getStatusAsInteger()<401) return true;
		return false;
	}
	
	/**
	 * Show RecoverButton on workflow items
	 * @author jpeters 
	 * @return
	 */
	
	boolean showRecoverButton() {
		def checkfor = ["1","3"]
		def ch = status[-1]
		if (checkfor.contains(ch)) {
			if (getStatusAsInteger()>=123 & getStatusAsInteger()<=353) return true;
		}
		return false;
	}
	/**
	 * @author jpeters 
	 * Show Retry Button if state is 1
	 * @return
	 */
	
	boolean showRetryButton() {
		def checkfor = ["1"]
		def ch = status[-1]
		if (checkfor.contains(ch)) {
			return true;
		}
		return false;
	}
	
	String getIdAsString(){
		return id.toString()
	}
	
	/**
	 * @author jpeters shows retry button after some time (48 hours)
	 * 
	 */
	
	boolean showRetryButtonAfterSomeTime(){
		def checkfor = ["2"]
		def ch = status[-1]
		if (checkfor.contains(ch)) {
			if (modified!=null && modified!="" && modified!="NULL" && modified.length()>5) {
				long diff = new Date().getTime()-Long.valueOf(modified).longValue()*1000L;
				if (diff > 2 * 24 * 60 * 60 * 1000) {
					return true;
				}
			}
		}
		return false;
	}
	
	String getInformation() {
			if (getStatusAsInteger()<440) {
					def checkfor = ["2","0"]
					def ch = status[-1]
					if (checkfor.contains(ch)) {
						return "arbeitend (" + status +")"
					} else return "fehlerhaft (" + status +")"
			} 
			if (getStatusAsInteger()>=440 && getStatusAsInteger()<500 ) {
				def checkfor = ["2","0"]
				def ch = status[-1]
				if (checkfor.contains(ch)) {
					return "repliziere (" + status +")"
				} else return "Replikation fehlerhaft (" + status +")"
		}
		
			if (getStatusAsInteger()>500 && getStatusAsInteger()<600) {
					def checkfor = ["2","0"]
					def ch = status[-1]
					if (checkfor.contains(ch)) {
						return "archiviert. Verarbeite PIP (" + status +")"
					} else return "archiviert. Fehler beim PIP (" + status +")"
			}
			if (getStatusAsInteger()>640 && getStatusAsInteger()<=645 ) {
				return "Warten auf Rückfrage (" + status +")"
			}
			return status
	}
}
