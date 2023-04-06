package acmecollege.ejb;

import javax.ejb.Singleton;
import javax.transaction.Transactional;

import acmecollege.entity.Course;


@Singleton
public class CourseService extends ACMECollegeService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
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
