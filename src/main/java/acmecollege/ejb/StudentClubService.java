/**
 * File:  StudentClubService.java Course materials (23W) CST 8277
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

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.ejb.Singleton;
import javax.transaction.Transactional;

import acmecollege.entity.ClubMembership;
import acmecollege.entity.MembershipCard;
import acmecollege.entity.StudentClub;

/**
 * This class provides the specialized Services required by the StudentClubResource. Generic service 
 * methods are inherited from ACMECollegeService
 * @author paisl
 *
 */
@Singleton
public class StudentClubService extends ACMECollegeService{

	private static final long serialVersionUID = 1L;

	/**
	 * Service that updates an existing StudentClub with new StudentClub information
	 * @param studentClubId The id of the StudentClub being updated
	 * @param updatingStudentClub the StudentClub containing information with which to update a StudentClub
	 * @return
	 */
    @Transactional
    public StudentClub updateStudentClub(int studentClubId, StudentClub updatingStudentClub) {
    	StudentClub studentClubToBeUpdated = getById(StudentClub.class, StudentClub.STUDENT_CLUB_QUERY_BY_ID , studentClubId);
        if (studentClubToBeUpdated != null 
        		&& !isDuplicated(updatingStudentClub, StudentClub.IS_DUPLICATE_QUERY_NAME, updatingStudentClub.getName())) {
            em.refresh(studentClubToBeUpdated);
            studentClubToBeUpdated.setName(updatingStudentClub.getName());
            em.merge(studentClubToBeUpdated);
            em.flush();
        }
        return studentClubToBeUpdated;
    }
    
 
    /**
     * Service that deletes a StudentClub. When deleting a StudentClub any ClubMemberships that it has need to have their
     * Possible relationship with MembershipCard removed so they can be deleted without causing Constraint violation against
     * MembershipCard table.
     * @param id the id of the StudentClub that needs to be removed.
     * @return
     */
    @Transactional
    public StudentClub deleteStudentClub(int id) {
    	StudentClub sc = getById(StudentClub.class, StudentClub.SPECIFIC_STUDENT_CLUB_QUERY_NAME, id);
        if (sc != null) {
            Set<ClubMembership> memberships = sc.getClubMemberships();
            List<ClubMembership> list = new LinkedList<>();
            memberships.forEach(list::add);
            list.forEach(m -> {
                if (m.getCard() != null) {
                    MembershipCard mc = getById(MembershipCard.class, MembershipCard.ID_CARD_QUERY_NAME, m.getCard().getId());
                    mc.setClubMembership(null);
                }
                m.setCard(null);
                em.merge(m);
            });
            em.remove(sc);
            return sc;
        }
        return null;
    }

}
