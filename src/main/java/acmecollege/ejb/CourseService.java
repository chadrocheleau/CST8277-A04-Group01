/**
 * File:  CourseService.java Course materials (23W) CST 8277
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

/**
 * This class provides the specialized Services required by the CourseResource. Generic service 
 * methods are inherited from ACMECollegeService
 * @author paisl
 *
 */
@Singleton
public class CourseService extends ACMECollegeService {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Service that updates a Course
	 * @param updates The Course containing information to update the Course with
	 * @param id The id of the Course being updated
	 * @return the Course with updates or null if updates weren't applied
	 */
	 @Transactional
	    public Course updateCourseById(Course updates,  int id) {
	    	Course courseToUpdate =  getById(Course.class, Course.COURSE_BY_ID_QUERY, id);
	    	if (courseToUpdate != null) {
	    		courseToUpdate.setCourse(updates.getCourseCode(),
	    				updates.getCourseTitle(),
	    				updates.getYear(),
	    				updates.getSemester(),
	    				updates.getCreditUnits(), 
	    				updates.getOnline());
	            em.merge(courseToUpdate);  
	            em.flush();
	    	}
	        return courseToUpdate;
	    }

}
