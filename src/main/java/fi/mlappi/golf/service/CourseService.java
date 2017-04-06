package fi.mlappi.golf.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.mlappi.golf.model.Course;
import fi.mlappi.golf.model.Hole;
import fi.mlappi.golf.repository.CourseRepository;

@Service("CourseService")
public class CourseService {
	private static final int[] holes = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18 };
	
	CourseRepository repository;

	@Autowired
	public CourseService(CourseRepository repository) {
		this.repository = repository;
	}
    
	public List<Course> getAllCourses() { 
    	List<Course> courses = new ArrayList<>();
    	Iterable<Course> iter = repository.findAll();
    	for (Course c : iter) {
    		courses.add(c);
		}
        return courses;
    }

    public Course create() {
		Course c = new Course();
		c.setName("");
		for (int i : holes) {
			Hole h = new Hole();
			h.setCourse(c);
			h.setHole(i);
			h.setHcp(i);
			c.getHole().add(h);
		}
		return c;    	
    }

    public Course save(Course course) {
    	return repository.save(course);
    }

	public Course find(Long id) {
		return repository.findOne(id);
	}

	public void delete(Long id) {
		repository.delete(id);
	}

}
