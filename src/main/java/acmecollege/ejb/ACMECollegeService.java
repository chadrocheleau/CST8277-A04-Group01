/**
 * File:  ACMECollegeService.java
 * Course materials (23W) CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman
 * 
 * Updated by:  Group 01
 *   041026625, Chad, Rocheleau (as from ACSIS)
 *   041020857, Lucas, Ross (as from ACSIS)
 *   041028658, Jacob, Scott (as from ACSIS)
 *
 */
package acmecollege.ejb;

import static acmecollege.entity.StudentClub.ALL_STUDENT_CLUBS_QUERY_NAME;
import static acmecollege.entity.StudentClub.SPECIFIC_STUDENT_CLUB_QUERY_NAME;
import static acmecollege.entity.StudentClub.IS_DUPLICATE_QUERY_NAME;
import static acmecollege.entity.Student.ALL_STUDENTS_QUERY_NAME;
import static acmecollege.utility.MyConstants.DEFAULT_KEY_SIZE;
import static acmecollege.utility.MyConstants.DEFAULT_PROPERTY_ALGORITHM;
import static acmecollege.utility.MyConstants.DEFAULT_PROPERTY_ITERATIONS;
import static acmecollege.utility.MyConstants.DEFAULT_SALT_SIZE;
import static acmecollege.utility.MyConstants.DEFAULT_USER_PASSWORD;
import static acmecollege.utility.MyConstants.DEFAULT_USER_PREFIX;
import static acmecollege.utility.MyConstants.PARAM1;
import static acmecollege.utility.MyConstants.PROPERTY_ALGORITHM;
import static acmecollege.utility.MyConstants.PROPERTY_ITERATIONS;
import static acmecollege.utility.MyConstants.PROPERTY_KEY_SIZE;
import static acmecollege.utility.MyConstants.PROPERTY_SALT_SIZE;
import static acmecollege.utility.MyConstants.PU_NAME;
import static acmecollege.utility.MyConstants.USER_ROLE;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import acmecollege.entity.ClubMembership;
import acmecollege.entity.CourseRegistration;
import acmecollege.entity.MembershipCard;
import acmecollege.entity.Professor;
import acmecollege.entity.SecurityRole;
import acmecollege.entity.SecurityUser;
import acmecollege.entity.Student;
import acmecollege.entity.StudentClub;

@SuppressWarnings("unused")

/**
 * Stateless Singleton EJB Bean - ACMECollegeService. This Class contains
 * Generic methods that can be used by all Service Beans that provide services
 * to specific Entity Resources.
 */
@Singleton
public class ACMECollegeService implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private static final Logger LOG = LogManager.getLogger();
    
    @PersistenceContext(name = PU_NAME)
    protected EntityManager em;
    
    @Inject
    protected Pbkdf2PasswordHash pbAndjPasswordHash;
    
    // ********** Generic Methods to be used for any type of entity *********************

    /**
     * Generic method that is used to get all Entities from the database
     * @param <T> The type of the entity to be retrieved
     * @param entity the entity to look for in the database and create list of
     * @param namedQuery The Named Query to use when getting this Entity list
     * @return The list of Entities retrieved from the database
     * TODO test what happens when no results are found... May need error handling here
     */
    public <T> List<T> getAll(Class<T> entity, String namedQuery) {
        TypedQuery<T> allQuery = em.createNamedQuery(namedQuery, entity);
        return allQuery.getResultList();
    }
    
    /**
     * Generic method that is used to get a specific Entity from the database 
     * @param <T> The type of the entity to be retrieved
     * @param entity the entity retrieved from the database
     * @param namedQuery The Named Query to use when getting this Entity by id
     * @param id The id of the Entity to retrieve
     * @return the Entity or null if Entity not found
     */
    public <T> T getById(Class<T> entity, String namedQuery, int id) {
    	T returnedEntity = null;
        TypedQuery<T> entityQuery = em.createNamedQuery(namedQuery, entity);
        entityQuery.setParameter(PARAM1, id);
        
        try {
        	returnedEntity = entityQuery.getSingleResult();
        } catch (NoResultException e) {
        	LOG.debug("Entity not found with ID: " + id);
        }
        
        return returnedEntity;
    }
    
    /**
     * Generic method to persist an Entity
     * @param <T> The type of the entity to persist
     * @param entity the Entity to persist
     * @return The Entity that has been persisted.
     * TODO test what happens when an invalid Entity is passed... May need error handling here.
     */
    @Transactional
    public <T> T persistEntity(T entity) {
        em.persist(entity);
        return entity;
    }
    
    /**
     * Generic method to delete an Entity from the database
     * @param <T> The type of the entity to delete
     * @param entityClass The entity class of the entity that is being deleted
     * @param namedQuery The named Query to use to get the Entity that needs to be deleted
     * @param id the Id of the Entity being deleted
     * @return The entity deleted or null if operation was not performed
     */
    @Transactional
    public <T> T deleteById(Class<T> entityClass, String namedQuery, int id) {
    	T entityToDelete = getById(entityClass, namedQuery, id);
    	if (entityToDelete != null) {
    		em.refresh(entityToDelete);
            em.remove(entityToDelete);
    	}
    	return entityToDelete;
    }

   
    
    // Please study & use the methods below in your test suites
    /**
     * 
     * @param <T>
     * @param newEntity
     * @param namedQuery
     * @param parameter
     * @return
     */
    public <T> boolean isDuplicated(T newEntity, String namedQuery, String parameter) {
        TypedQuery<Long> entityQuery = em.createNamedQuery(namedQuery, Long.class);
        entityQuery.setParameter(PARAM1, parameter);
        return (entityQuery.getSingleResult() >= 1);
    }

   
    
    
}