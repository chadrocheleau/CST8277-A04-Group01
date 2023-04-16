/**
 * File:  ClubMembershipService.java
 *
 * @author 041026625 Chad Rocheleau (as from ACSIS)
 * @author 041020857 Lucas Ross (as from ACSIS)
 * @author 041028658 Jacob Scott (as from ACSIS)
 * 
 */

package acmecollege.ejb;

import java.util.HashSet;
import java.util.Set;

import javax.ejb.Singleton;
import javax.transaction.Transactional;

import acmecollege.entity.ClubMembership;
import acmecollege.entity.Course;
import acmecollege.entity.CourseRegistration;
import acmecollege.entity.MembershipCard;
import acmecollege.entity.Professor;
import acmecollege.entity.StudentClub;

/**
 * This class provides all the resources available to the REST API for 
 * the ClubMembershipResource.
 * @author paisl
 *
 */
@Singleton
public class ClubMembershipService extends ACMECollegeService {

	private static final long serialVersionUID = 1L;

	/**
	 * Service that persists a ClubMembership
	 * @param newClubMembership The new ClubMembership to be persisted
	 * @param scId The id of the StudentClub required to persist a new ClubMembership
	 * @return The persisted ClubMembership or null if the operation failed.
	 */
	@Transactional
	public ClubMembership persistClubMembership(ClubMembership newClubMembership, int scId) {
		StudentClub studentClub = getById(StudentClub.class, StudentClub.SPECIFIC_STUDENT_CLUB_QUERY_NAME, scId);
		if (studentClub == null) { return null; } // If the student club doesn't exist then can't make ClubMembership
		newClubMembership.setStudentClub(studentClub);
		em.persist(newClubMembership);
		em.flush();
		return newClubMembership;
	}
	
	/**
	 * TODO Not sure about this one if it works or not. Was provided and needs to be investigated
	 * @param id
	 * @param clubMembershipWithUpdates
	 * @return
	 */
	@Transactional
	public ClubMembership updateClubMembership(int id, ClubMembership clubMembershipWithUpdates) {
		ClubMembership clubMembershipToBeUpdated = getById(ClubMembership.class, ClubMembership.FIND_BY_ID, id);
		if (clubMembershipToBeUpdated != null) {
			em.refresh(clubMembershipToBeUpdated);
			em.merge(clubMembershipWithUpdates);
			em.flush();
		}
		return clubMembershipToBeUpdated;
	}

	/**
	 * Service that deletes a ClubMembership. A ClubMembership may be referenced by
	 * a MembershipCard, if so then the reference needs to be removed from the 
	 * associated MembershipCard (reference to ClubMembership in MembershipCard is optional)
	 * @param clubMembershipId The id of the ClubMembership to be deleted
	 * @return The deleted ClubMembership or null if the ClubMembership doesn't exist
	 */
	@Transactional
	public ClubMembership deleteClubMembership(int clubMembershipId) {
		ClubMembership clubMembership = getById(ClubMembership.class, ClubMembership.FIND_BY_ID, clubMembershipId);
		if (clubMembership == null) { return clubMembership; }
		// There may be a MembershipCard that has this ClubMembership associated - must remove the reference before deleting
		MembershipCard card = clubMembership.getCard();
		if (card != null) { card.setClubMembership(null);}
		em.remove(clubMembership);
		em.flush();
		return clubMembership;
	}

}
