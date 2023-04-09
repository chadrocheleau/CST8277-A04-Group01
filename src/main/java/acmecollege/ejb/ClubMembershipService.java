/**
 * File:  ClubMembershipService.java Course materials (23W) CST 8277
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

import static acmecollege.utility.MyConstants.PARAM1;

import javax.ejb.Singleton;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import acmecollege.entity.ClubMembership;
import acmecollege.entity.CourseRegistration;
import acmecollege.entity.CourseRegistrationPK;
import acmecollege.entity.MembershipCard;
import acmecollege.entity.Student;
import acmecollege.entity.StudentClub;

@Singleton
public class ClubMembershipService extends ACMECollegeService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Transactional
    public ClubMembership persistClubMembership(ClubMembership newClubMembership, int scId) {
		newClubMembership.setStudentClub(em.merge(getById(StudentClub.class, StudentClub.SPECIFIC_STUDENT_CLUB_QUERY_NAME, scId)));
		
		em.persist(newClubMembership);
		em.flush();
        return newClubMembership;
    }

    public ClubMembership getClubMembershipById(int cmId) {
        TypedQuery<ClubMembership> allClubMembershipQuery = em.createNamedQuery(ClubMembership.FIND_BY_ID, ClubMembership.class);
        allClubMembershipQuery.setParameter(PARAM1, cmId);
        return allClubMembershipQuery.getSingleResult();
    }
    
    @Transactional
    public ClubMembership updateClubMembership(int id, ClubMembership clubMembershipWithUpdates) {
    	ClubMembership clubMembershipToBeUpdated = getClubMembershipById(id);
        if (clubMembershipToBeUpdated != null) {
            em.refresh(clubMembershipToBeUpdated);
            em.merge(clubMembershipWithUpdates);
            em.flush();
        }
        return clubMembershipToBeUpdated;
    }
    
    @Transactional
	public ClubMembership deleteClubMembership(int clubMembershipId) {
    	ClubMembership clubMembership = getById(ClubMembership.class, ClubMembership.FIND_BY_ID, clubMembershipId);
    	if (clubMembership == null) {return clubMembership;}
    		MembershipCard card = clubMembership.getCard();
        	card.setClubMembership(null);
        	clubMembership.setCard(null);
            em.merge(card);
            em.merge(clubMembership);
    		em.remove(clubMembership);
    		em.flush();
    		return clubMembership;
    		
    	
    	
	}
    

}
