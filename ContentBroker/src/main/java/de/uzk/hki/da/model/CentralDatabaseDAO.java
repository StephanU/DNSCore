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

package de.uzk.hki.da.model;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uzk.hki.da.core.HibernateUtil;


/**
 * @author Daniel M. de Oliveira
 */
public class CentralDatabaseDAO {

	/** The logger. */
	private static Logger logger = LoggerFactory
			.getLogger(CentralDatabaseDAO.class);

	/**
	 */
	public CentralDatabaseDAO() {}
	
	
	/**
	 * XXX locking synchronized
	 * 
	 * Sets the objects status to working status.
	 * IMPORTANT NOTE: Fetch objects from queue opens a new session.
	 *
	 * @param status the status
	 * @param workingStatus typically a numerical value with three digits, stored as string.
	 * working status third digit typically is 2 by convention.
	 * @param node the node
	 * @return the job
	 * @author Daniel M. de Oliveira
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	public Job fetchJobFromQueue(String status, String workingStatus, Node node) {
		Session session = HibernateUtil.openSession();
		session.beginTransaction();

		List<Job> joblist=null;
		try{
			
			joblist = (List<Job>) session
					.createQuery("SELECT j FROM Job j LEFT JOIN j.obj as o where j.status=?1 and j.initial_node=?2 and o.orig_name!=?3 order by j.date_modified asc ")
					.setParameter("1", status).setParameter("2", node.getName()).setParameter("3","integrationTest").setCacheable(false).setMaxResults(1).list();
			logger.debug("no job found for status {}.",status);
			
			if ((joblist == null) || (joblist.isEmpty())){
				session.close();
				return null;
			}
			
			Job job = joblist.get(0);
			// To circumvent lazy initialization issues
			for (ConversionInstruction ci:job.getConversion_instructions()){}
			for (Job j:job.getChildren()){}
			for (Package p:job.getObject().getPackages()){
				for (DAFile f:p.getFiles()){}
				for (Event e:p.getEvents()){}
			}
			// -
			logger.debug("fetched job with id {} and status {}", job.getId(), job.getStatus());

			job.setStatus(workingStatus);
			job.setDate_modified(String.valueOf(new Date().getTime()/1000L));
			session.merge(job);
			session.getTransaction().commit();
			session.close();
			
			logger.debug("fetched job with id {} and set status to {}", job.getId(), job.getStatus());
		
		}catch(Exception e){
			session.close();
			logger.error("Caught error in fetchJobFromQueue");
			
			throw new DAOException(e.getMessage(), e);
		}
		return joblist.get(0);
	}
	
	
	
	/**
	 * Gets the job.
	 *
	 * @param orig_name the orig_name
	 * @param csn the csn
	 * @return the job
	 */
	@SuppressWarnings("unchecked")
	public Job getJob(Session session, String orig_name, String csn) {
		List<Job> l = null;
	
		try {
			l = (List<Job>) session.createQuery(
					"SELECT j FROM Job j left join j.obj as o left join o.contractor as c where o.orig_name=?1 and c.short_name=?2"
					)
					
					
					
					
							.setParameter("1", orig_name)
							.setParameter("2", csn)
							.setReadOnly(true).list();
			
			return l.get(0);
		} catch (IndexOutOfBoundsException e) {
			logger.debug("search for a job with orig_name " + orig_name + " for contractor " +
						 csn + " returns null!");
			return null;
		}
	}
	
	
	
	/**
	 * Insert job into queue.
	 *
	 * @param contractorShortName the contractor short name
	 * @param origName the orig name
	 * @param initialNodeName the initial node name
	 * @return the job
	 */
	public Job insertJobIntoQueue(Session session, Contractor c,String origName,String initialNodeName,Object object){

		Job job = new Job();
		job.setObject(object);
		
		job.setStatus("110");
		job.setInitial_node(initialNodeName);
		job.setDate_created(String.valueOf(new Date().getTime()/1000L));
	
		session.save(job);
		
		return job;
	}
	
	
	
	/**
	 * Retrieves Object from the Object Table for a given orig_name and contractor short name.
	 *
	 * @param orig_name the orig_name
	 * @param csn the csn
	 * @return Object object or null if no object with the given combination of orig_name and
	 * contractor short name could be found
	 * @author Stefan Kreinberg
	 * @author Thomas Kleinke
	 */
	public Object getUniqueObject(Session session,String orig_name, String csn) {
		
		Contractor contractor = getContractor(session, csn);
		
		@SuppressWarnings("rawtypes")
		List l = null;
	
		try {
			l = session.createQuery("from Object where orig_name=?1 and contractor_id=?2")
							.setParameter("1", orig_name)
							.setParameter("2", contractor.getId())
							.list();

			if (l.size() > 1)
				throw new RuntimeException("Found more than one object with name " + orig_name +
									" for contractor " + csn + "!");
			
			Object o = (Object) l.get(0);
			o.setContractor(contractor);
			return o;
		} catch (IndexOutOfBoundsException e1) {
			try {
				logger.debug("Search for an object with orig_name " + orig_name + " for contractor " +
						csn + " returns null! Try to find objects with objectIdentifier " + orig_name);
			
				l = session.createQuery("from Object where identifier=?1 and contractor_id=?2")
					.setParameter("1", orig_name)
					.setParameter("2", contractor.getId())
					.list();

				if (l.size() > 1)
					throw new RuntimeException("Found more than one object with name " + orig_name +
								" for contractor " + csn + "!");
				
				Object o = (Object) l.get(0);
				o.setContractor(contractor);
				return o;
			} catch (IndexOutOfBoundsException e2) {
				logger.debug("Search for an object with objectIdentifier " + orig_name + " for contractor " +
						csn + " returns null!");	
			}	
			
		} catch (Exception e) {
			return null;
		}
		
		return null;
	}
	
	
	/**
	 * Gets the contractor.
	 *
	 * @param contractorShortName the contractor short name
	 * @return null if no contractor for short name could be found
	 */
	public Contractor getContractor(Session session, String contractorShortName) {
		logger.trace("CentralDatabaseDAO.getContractor(\"" + contractorShortName + "\")");
	
		@SuppressWarnings("rawtypes")
		List list;	
		list = session.createQuery("from Contractor where short_name=?1")
	
				.setParameter("1",contractorShortName).setReadOnly(true).list();
		
		if (list.isEmpty())
			return null;
	
		return (Contractor) list.get(0);
	}
	
	
	
	
	
	/**
	 * Gets the second stage scan policies.
	 *
	 * @return the second stage scan policies
	 */
	public List<SecondStageScanPolicy> getSecondStageScanPolicies(Session session) {
		@SuppressWarnings("unchecked")
		List<SecondStageScanPolicy> l = (List<SecondStageScanPolicy>) session
				.createQuery("from SecondStageScanPolicy").list();

		return l;
	}
	
	
	
	/**
	 * Gets the objects, which need audit.
	 *
	 * @param initial_node the initial_node
	 * @param archivedStatus the archived status
	 * @return the object need audit
	 */
	public Object getObjectNeedAudit(Session session,String initial_node, int archivedStatus) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR_OF_DAY, -24);

		
		@SuppressWarnings("rawtypes")
		List l = null;
	
		try {
			l = session.createQuery("from Object where initial_node=?1 and last_checked > :date and object_state >= ?2 order by last_checked asc")
					.setCalendar("date",cal)
					.setParameter("1", initial_node)
					.setParameter("2", archivedStatus)
							.setReadOnly(true).list();
			
			return (Object) l.get(0);
		} catch (IndexOutOfBoundsException e) {
			logger.debug("search for a Objects with initial_node " + initial_node + " returns null!");
			return null;
		}
	}
	
	
	
	/**
	 * Gets all nodes.
	 *
	 * @return all Nodes available
	 * @auhtor jens Peters
	 */
	public Set<Node> getAllNodes(Session session) {

		@SuppressWarnings("unchecked")
		List<Node> nodes = session
				.createQuery(
						"from Node").setReadOnly(true).list();

		Set<Node> resultSet = new HashSet<Node>();
		for (Node node : nodes) {

			if (node == null) {
				throw new Error(
						"Found an entry in Nodes List that has a null field for node");
			}

			if (node != null)
				resultSet.add(node);
		}

		return resultSet;
	}

	
	

	
	
	
	
	
	
	
	/**
	 * 
	 * Only needed for proper tests of convert action.
	 * Updates the jobs status to the current state of the database.
	 *
	 * @param job the job
	 * @return Job
	 */
	@SuppressWarnings("unused")
	public Job refreshJob(Job job) {
		Session session = HibernateUtil.openSession();
		session.beginTransaction();
		session.refresh(job);
		for (ConversionInstruction ci:job.getConversion_instructions()){}
		for (Job j:job.getChildren()){}
		for (Package p:job.getObject().getPackages()){
			for (DAFile f:p.getFiles()){}
			for (Event e:p.getEvents()){}
		}
		session.close();
		return job;
	}

	
	
	
	/**
	 * Only needed for proper tests of convert action.
	 *
	 * @param id the id
	 * @return Job
	 */
	public Job getJob(Session session, int id) {
		Job job = (Job) session.get(Job.class, id);
		return job;
	}
}
