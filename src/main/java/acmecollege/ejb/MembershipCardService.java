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

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.ejb.Singleton;
import javax.transaction.Transactional;
import acmecollege.entity.ClubMembership;
import acmecollege.entity.DurationAndStatus;
import acmecollege.entity.MembershipCard;
import acmecollege.entity.Student;
import acmecollege.entity.StudentClub;

/**
 * This class provides the specialized Services required by the MembershipCardResource. Generic service 
 * methods are inherited from ACMECollegeService
 * @author paisl
 *
 */
@Singleton
public class MembershipCardService extends ACMECollegeService {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Service that persists MembershipCards
	 * 
	 * @param studentId The id of the student for which the MembershipCard should be created
	 * @param clubMembershipId The id of the ClubMembership for which this MembershipCard should be associated with
	 * @return The MembershipCard that was created or null if the Card was not created.
	 */
	@Transactional
    public MembershipCard persistMembershipCard(int studentId, int scId) {
		Set<MembershipCard> cards = new HashSet<>();
        Student student = getById(Student.class, Student.QUERY_STUDENT_BY_ID, studentId);
		StudentClub studentClub = getById(StudentClub.class, StudentClub.STUDENT_CLUB_QUERY_BY_ID, scId);
		
		// Don't create MembershipCard if student or clubMembership doesn't exist
		if(student == null || studentClub == null) {
			return null;
		}
		// Don't create MembershipCard if student has a membership card for StudentClub already
		cards = student.getMembershipCards();
		Iterator<MembershipCard> cardsIterator = cards.iterator();
		while (cardsIterator.hasNext()) {
			if (cardsIterator.next().getMembership().getStudentClub().getId() == scId) { return null; }
		}
		// if all is good create the MembershipCard
		ClubMembership newClubMembership = new ClubMembership();
		newClubMembership.setStudentClub(studentClub);
		DurationAndStatus ds = new DurationAndStatus();
		ds.setDurationAndStatus(LocalDateTime.now(), LocalDateTime.now().plusYears(1), "+");
		newClubMembership.setDurationAndStatus(ds);
		
		MembershipCard newMembershipCard = new MembershipCard();
		newMembershipCard.setClubMembership(newClubMembership);
		newMembershipCard.setOwner(student);
		em.persist(newMembershipCard);
		em.flush();
        return newMembershipCard;
    }

	/**
	 * Service that gets a MembershipCard from the database
	 * 
	 * @param cardId The id of the MembershipCard to retrieve from the database
	 * @return the MembershipCard retrieved or null if no MembershipCard exists by that Id
	 */
    public MembershipCard getMembershipCardById(int cardId) {
    	MembershipCard membershipCard = getById(MembershipCard.class, MembershipCard.ID_CARD_QUERY_NAME, cardId);
        return membershipCard;
    }
    
    /**
     * Service that deletes a MembershipCard from the database
     * 
     * @param cardId The id of the MembershipCard to delete
     * @return the deleted MembershipCard or null if no MembershipCard exists by that Id
     */
    @Transactional
    public MembershipCard deleteMembershipCardById(int cardId) {
    	MembershipCard membershipCard = getById(MembershipCard.class, MembershipCard.ID_CARD_QUERY_NAME, cardId);
    	em.remove(membershipCard);
    	return membershipCard;
    }

}
