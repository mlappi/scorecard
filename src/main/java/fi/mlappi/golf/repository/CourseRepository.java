package fi.mlappi.golf.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import fi.mlappi.golf.model.Course;

@Repository
public interface CourseRepository extends CrudRepository<Course, Long> {
	
	
}
