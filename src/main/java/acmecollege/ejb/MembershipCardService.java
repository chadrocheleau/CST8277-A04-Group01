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

import static acmecollege.utility.MyConstants.ADMIN_ROLE;
import static acmecollege.utility.MyConstants.PARAM1;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static acmecollege.utility.MyConstants.RESOURCE_PATH_ID_PATH;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Singleton;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

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
		Set<MembershipCard> cards = new HashSet<>();
        Student student = getById(Student.class, Student.QUERY_STUDENT_BY_ID, studentId);
		ClubMembership clubMembership = getById(ClubMembership.class, ClubMembership.FIND_BY_ID, clubMembershipId);
		
		if(student == null || clubMembership == null) {
			return null;
		}
		int clubId = clubMembership.getStudentClub().getId();
		cards = student.getMembershipCards();
		
		Iterator<MembershipCard> cardsIterator = cards.iterator();
		
		while (cardsIterator.hasNext()) {
			if (cardsIterator.next().getMembership().getStudentClub().getId() == clubId) { return null; }
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
    
    public MembershipCard deleteMembershipCardById(int cardId) {
    	MembershipCard membershipCard = getById(MembershipCard.class, MembershipCard.ID_CARD_QUERY_NAME, cardId);
    	em.remove(membershipCard);
    	return membershipCard;
    }

}
