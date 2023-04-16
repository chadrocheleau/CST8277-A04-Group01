/**
 * File:  StudentService.java
 *
 * @author 041026625 Chad Rocheleau (as from ACSIS)
 * @author 041020857 Lucas Ross (as from ACSIS)
 * @author 041028658 Jacob Scott (as from ACSIS)
 * 
 */

package acmecollege.ejb;

import static acmecollege.utility.MyConstants.DEFAULT_KEY_SIZE;
import static acmecollege.utility.MyConstants.DEFAULT_PROPERTY_ALGORITHM;
import static acmecollege.utility.MyConstants.DEFAULT_PROPERTY_ITERATIONS;
import static acmecollege.utility.MyConstants.DEFAULT_SALT_SIZE;
import static acmecollege.utility.MyConstants.DEFAULT_USER_PASSWORD;
import static acmecollege.utility.MyConstants.DEFAULT_USER_PREFIX;
import static acmecollege.utility.MyConstants.PROPERTY_ALGORITHM;
import static acmecollege.utility.MyConstants.PROPERTY_ITERATIONS;
import static acmecollege.utility.MyConstants.PROPERTY_KEY_SIZE;
import static acmecollege.utility.MyConstants.PROPERTY_SALT_SIZE;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.ejb.Singleton;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import acmecollege.entity.CourseRegistration;
import acmecollege.entity.Professor;
import acmecollege.entity.SecurityRole;
import acmecollege.entity.SecurityUser;
import acmecollege.entity.Student;

/**
 * This class provides the specialized Services required by the StudentResource. Generic service 
 * methods are inherited from ACMECollegeService
 * @author paisl
 *
 */
@Singleton
public class StudentService extends ACMECollegeService {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Builds a new SecurityUser for a new Student and assigns default user name
	 * and password.
	 * 
	 * @param newStudent The new Student for which to build the SecurityUser
	 */
    @Transactional
    public void buildUserForNewStudent(Student newStudent) {
        SecurityUser userForNewStudent = new SecurityUser();
        String timeStamp = LocalDateTime.now().toString();
        String defaultUserName = DEFAULT_USER_PREFIX + "_" + newStudent.getFirstName() + "." + newStudent.getLastName() + timeStamp;
        userForNewStudent.setUsername(defaultUserName);
        // So long as duplicate user name doesn't exist in SecurityUser table then go ahead
//        if (!isDuplicated(userForNewStudent, SecurityUser.IS_DUPLICATE_QUERY_NAME, userForNewStudent.getUsername())) {
        	Map<String, String> pbAndjProperties = new HashMap<>();
            pbAndjProperties.put(PROPERTY_ALGORITHM, DEFAULT_PROPERTY_ALGORITHM);
            pbAndjProperties.put(PROPERTY_ITERATIONS, DEFAULT_PROPERTY_ITERATIONS);
            pbAndjProperties.put(PROPERTY_SALT_SIZE, DEFAULT_SALT_SIZE);
            pbAndjProperties.put(PROPERTY_KEY_SIZE, DEFAULT_KEY_SIZE);
            pbAndjPasswordHash.initialize(pbAndjProperties);
            String pwHash = pbAndjPasswordHash.generate(DEFAULT_USER_PASSWORD.toCharArray());
            userForNewStudent.setPwHash(pwHash);
            userForNewStudent.setStudent(newStudent);
            
            TypedQuery<SecurityRole> userRoleQuery;
            userRoleQuery = em.createNamedQuery(SecurityRole.FIND_USER_ROLE, SecurityRole.class);
            userRoleQuery.setParameter("param1", "USER_ROLE");
            SecurityRole userRole = userRoleQuery.getSingleResult();
            		
            userForNewStudent.getRoles().add(userRole);
            userRole.getUsers().add(userForNewStudent);
            em.persist(userForNewStudent);
//        }
        
    }


    /**
     * Service that sets a new Professor as the professor for a CourseRegistration for a specific
     * Student registered for a specific Course
     * 
     * @param studentId The id of the Student for which the CourseRegistration exists
     * @param courseId The id of the Course for which the CourseRegistration exists
     * @param newProfessor The new Professor information 
     * @return The Professor that was set as the Professor for this CourseRegistration for a specific
     * Student registered for a specific Course or null if the Student doesn't exist
     */
    @Transactional
    public Professor setProfessorForStudentCourse(int studentId, int courseId, Professor newProfessor) {
        Student studentToBeUpdated = em.find(Student.class, studentId);
        if (studentToBeUpdated != null) { // Student exists
            Set<CourseRegistration> courseRegistrations = studentToBeUpdated.getCourseRegistrations();
            courseRegistrations.forEach(c -> {
                if (c.getCourse().getId() == courseId) {
                    if (c.getProfessor() != null) { // Professor exists
                        Professor prof = em.find(Professor.class, c.getProfessor().getId());
                        prof.setProfessor(newProfessor.getFirstName(),
                        				  newProfessor.getLastName(),
                        				  newProfessor.getDepartment());
                        em.merge(prof);
                    }
                    else { // Professor does not exist
                        c.setProfessor(newProfessor);
                        em.merge(studentToBeUpdated);
                    }
                }
            });
            return newProfessor;
        }
        else return null;  // Student doesn't exists
    }

    /**
     * Service that updates a student
     * 
     * @param id - id of Student to update
     * @param studentWithUpdates Student with updated information
     * @return Student with updated information or null if no Student with id provided
     */
    @Transactional
    public Student updateStudentById(int id, Student studentWithUpdates) {
        Student studentToBeUpdated = getById(Student.class, Student.QUERY_STUDENT_BY_ID, id);
        if (studentToBeUpdated != null) {
            em.refresh(studentToBeUpdated);
            studentToBeUpdated.setFullName(studentWithUpdates.getFirstName(), studentWithUpdates.getLastName());
            em.merge(studentToBeUpdated);
            em.flush();
        }
        return studentToBeUpdated;
    }

    /**
     * Service that deletes a Student by Id. There may be a reference in SecurityUser to the Student. This method 
     * gets that SecurityUser and removes it as well so as to avoid dangling reference in SecurityUser table to the
     * Student being deleted.
     * 
     * @param id - student id to delete
     */
    @Transactional
    public Student deleteStudentById(int id) {
        Student student = getById(Student.class, Student.QUERY_STUDENT_BY_ID, id);
        if (student != null) {
            em.refresh(student);
            //get SecurityUser for Student being deleted so the reference to the Student is not left dangling
            TypedQuery<SecurityUser> findUser = em.createNamedQuery(SecurityRole.FIND_STUDENT_WITH_ROLE, SecurityUser.class);
            findUser.setParameter("param1", id);
            SecurityUser sUser = findUser.getSingleResult();
            em.remove(sUser);
            em.remove(student);
        }
        return student;
    }
}
