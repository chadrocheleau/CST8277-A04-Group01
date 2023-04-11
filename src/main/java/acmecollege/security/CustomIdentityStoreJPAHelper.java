/**
 * File:  CustomIdentityStoreJPAHelper.java Course materials (23W) CST 8277
 * 
 * @author Teddy Yap
 * @author Mike Norman
 * 
 * Updated by:  Group 01
 *   041026625, Chad, Rocheleau (as from ACSIS)
 *   41020857, Lucas, Ross (as from ACSIS)
 *   041028658, Jacob, Scott (as from ACSIS)
 * 
 */
package acmecollege.security;

import static acmecollege.utility.MyConstants.PARAM1;
import static acmecollege.utility.MyConstants.PU_NAME;

import static java.util.Collections.emptySet;

import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import acmecollege.entity.SecurityRole;
import acmecollege.entity.SecurityUser;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.NamedQuery;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

@SuppressWarnings("unused")

/**
 * This class contains helper methods to be used for basic authentication
 * of SecurityUsers for the ACMECollegeSystem application
 * @author paisl
 *
 */
@Singleton
public class CustomIdentityStoreJPAHelper {

    private static final Logger LOG = LogManager.getLogger();

    @PersistenceContext(name = PU_NAME)
    protected EntityManager em;

    /**
     * Finds a SecurityUser by provided user name. Used when need
     * to verify that logged in user matches the security user
     * @param username the user name of the SecurityUser
     * @return the SecurityUser with associated user name or null if doesn't exist.
     */
    public SecurityUser findUserByName(String username) {
        LOG.debug("find a SecurityUser by name = {}", username);
        SecurityUser user = null;
        TypedQuery<SecurityUser> userByNameQuery;
        try {
        	user = em.createNamedQuery(SecurityUser.USER_BY_NAME, SecurityUser.class)
					  .setParameter("param1", username).getSingleResult();
        } catch (NoResultException e) {
        	LOG.debug("Couldn't find a SecurityUser by name = {}", username );
        }
        return user;
    }

    /**
     * Get list of Roles associated with user with user name provided
     * @param username The user name of the SecurityUser for which the list of roles is 
     * to be retrieved
     * @return The list of roles that the SecurityUser with user name has.
     */
    public Set<String> findRoleNamesForUser(String username) {
        LOG.debug("find Roles For Username={}", username);
        Set<String> roleNames = emptySet();
        SecurityUser securityUser = findUserByName(username);
        if (securityUser != null) {
            roleNames = securityUser.getRoles().stream().map(s -> s.getRoleName()).collect(Collectors.toSet());
        }
        return roleNames;
    }

    /**
     * Saves a SecurityUser
     * @param user The SecurityUser to save to the database
     */
    @Transactional
    public void saveSecurityUser(SecurityUser user) {
        LOG.debug("adding new user={}", user);
        em.persist(user);
    }

    /**
     * Saves a SecurityRole 
     * @param role The SecurityRole to save to the database
     */
    @Transactional
    public void saveSecurityRole(SecurityRole role) {
        LOG.debug("adding new role={}", role);
        em.persist(role);
    }
}