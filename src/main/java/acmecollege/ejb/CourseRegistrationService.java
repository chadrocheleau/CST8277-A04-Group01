/**
 * File:  CourseRegistrationService.java Course materials (23W) CST 8277
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

import javax.ejb.Singleton;
import javax.transaction.Transactional;

import acmecollege.entity.Course;
import acmecollege.entity.CourseRegistration;
import acmecollege.entity.CourseRegistrationPK;
import acmecollege.entity.Professor;
import acmecollege.entity.Student;

@Singleton
public class CourseRegistrationService extends ACMECollegeService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Persists a course registration. Needs it's own treatment
	 * to deal with getting the student, course, and possible professor entities associated with 
	 * the new CourseRegistration. 
	 * @param registration The registration to persist
	 * @param student The student to add to the course registration
	 * @param course The course to add to the course registration
	 * @param professor The professor to assign to the course registration
	 * @return
	 */
	@Transactional
	public CourseRegistration persistRegistration(CourseRegistration registration,
												  Student student,
											      Course course,
											      Professor professor) {
		// fix detached state of student and course before setting Registration fields
		registration.setStudent(em.merge(student));
		registration.setCourse(em.merge(course));		
		try {
			registration.setProfessor(em.merge(professor));	
		} catch (Exception e) {
			registration.setProfessor(professor);
		}
		em.persist(registration);
		em.flush();	
		return registration;
	}
	
	@Transactional
	public CourseRegistration deleteRegistrationById(CourseRegistrationPK id) {
		CourseRegistration registration = null;
		registration = em.find(CourseRegistration.class, id);
		em.remove(registration);
		em.flush();
		return registration;
	}
}
