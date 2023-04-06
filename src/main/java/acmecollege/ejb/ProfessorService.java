package acmecollege.ejb;

import javax.ejb.Singleton;
import javax.transaction.Transactional;

import acmecollege.entity.Professor;

@Singleton
public class ProfessorService extends ACMECollegeService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
