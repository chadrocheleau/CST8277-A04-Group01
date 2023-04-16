/**
 * File:  ProfessorService.java
 *
 * @author 041026625 Chad Rocheleau (as from ACSIS)
 * @author 041020857 Lucas Ross (as from ACSIS)
 * @author 041028658 Jacob Scott (as from ACSIS)
 * 
 */
package acmecollege.ejb;

import java.util.Set;

import javax.ejb.Singleton;
import javax.transaction.Transactional;
import acmecollege.entity.ClubMembership;
import acmecollege.entity.CourseRegistration;
import acmecollege.entity.MembershipCard;
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
	 
	 /**
		 * Service that deletes a Professor. A Professor may be referenced by
		 * a CourseRegistration, if so then the reference needs to be removed from the 
		 * associated CourseRegistration (reference to Professor in CourseRegistration is optional)
		 * @param clubMembershipId The id of the ClubMembership to be deleted
		 * @return The deleted ClubMembership or null if the ClubMembership doesn't exist
		 */
		@Transactional
		public Professor deleteProfessor(int professorId) {
			Professor professor = getById(Professor.class, Professor.QUERY_PROFESSOR_BY_ID, professorId);
			if (professor == null) { return professor; }
			// There may be a MembershipCard that has this ClubMembership associated - must remove the reference before deleting
			Set<CourseRegistration> registrations = professor.getCourseRegistrations();
			if (!registrations.isEmpty()) { 
				registrations.forEach(registration -> {
					registration.setProfessor(null);
				});
			}
			em.remove(professor);
			em.flush();
			return professor;
		}

}
