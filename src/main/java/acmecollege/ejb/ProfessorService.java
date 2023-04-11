/**
 * File:  ProfessorService.java Course materials (23W) CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman
 * 
 * Updated by:  Group 01
 *   041026625, Chad, Rocheleau (as from ACSIS)
 *   41020857, Lucas, Ross (as from ACSIS)
 *   041028658, Jacob, Scott (as from ACSIS)
 * 
 */
package acmecollege.ejb;

import javax.ejb.Singleton;
import javax.transaction.Transactional;

import acmecollege.entity.Professor;

/**
 * This class provides the specialized Services required by the ProfessorResource. Generic service 
 * methods are inherited from ACMECollegeService
 * @author paisl
 *
 */
@Singleton
public class ProfessorService extends ACMECollegeService {

	private static final long serialVersionUID = 1L;

	/**
	 * Service that updates a Professor
	 * 
	 * @param updates The Professor that contains information with which to update the Professor
	 * @param id The id of the Professor to update
	 * @return The updated Professor or null if the operation was not performed.
	 */
	 @Transactional
	    public Professor updateProfessorById(Professor updates,  int id) {
	    	Professor entityToUpdate = getById(Professor.class, Professor.QUERY_PROFESSOR_BY_ID, id);
	    	if (entityToUpdate != null) {
	    		entityToUpdate.setProfessor(updates.getFirstName(), updates.getLastName(), updates.getDepartment());
	            em.merge(entityToUpdate);  
	            em.flush();
	    	}
	        return entityToUpdate;
	    }
}
