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
public class MembershipCardService extends ACMECollegeService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Transactional
    public MembershipCard persistMembershipCard(int studentId, int clubMembershipId) {
		
        Student student = getById(Student.class, Student.QUERY_STUDENT_BY_ID, studentId);
		ClubMembership clubMembership = getById(ClubMembership.class, ClubMembership.FIND_BY_ID, clubMembershipId);
		if(student == null || clubMembership == null) {
			return null;
		}
		MembershipCard newMembershipCard = new MembershipCard();
		newMembershipCard.setClubMembership(clubMembership);
		newMembershipCard.setOwner(student);
		em.persist(newMembershipCard);
		em.flush();
        return newMembershipCard;
    }

    public MembershipCard getMembershipCardById(int cmId) {
        TypedQuery<MembershipCard> membershipCardQuery = em.createNamedQuery(MembershipCard.ID_CARD_QUERY_NAME, MembershipCard.class);
        membershipCardQuery.setParameter(PARAM1, cmId);
        return membershipCardQuery.getSingleResult();
    }

}
