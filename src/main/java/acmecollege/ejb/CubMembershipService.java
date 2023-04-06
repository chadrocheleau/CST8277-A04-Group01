package acmecollege.ejb;

import static acmecollege.utility.MyConstants.PARAM1;

import javax.ejb.Singleton;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import acmecollege.entity.ClubMembership;

@Singleton
public class CubMembershipService extends ACMECollegeService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Transactional
    public ClubMembership persistClubMembership(ClubMembership newClubMembership) {
        em.persist(newClubMembership);
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
    

}
