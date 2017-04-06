package fi.mlappi.golf.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import fi.mlappi.golf.model.Course;
import fi.mlappi.golf.service.CourseService;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class CourseController  {

	@Autowired
	CourseService courseService;
	
	@RequestMapping("/course")
	public String courses(ModelMap model) {
		log.debug("get all courses");
		List<Course> courses = courseService.getAllCourses();
		model.addAttribute("courses", courses);
		//model.put("message", model.get("message"));		
		return "list-courses";
	}


	@RequestMapping("/course/new")
	public String add(ModelMap model) {
		log.debug("add a new round");	
		model.addAttribute("course", courseService.create());		
		return "new-course";
	}

	@RequestMapping(value = "/course/edit/{id}")
	public ModelAndView edit(ModelMap model, @PathVariable("id") long id) {
		log.debug("edit course " + id);
		model.addAttribute("course", courseService.find(id));	
		
		return new ModelAndView("new-course");
	}

	@RequestMapping(value = "/course/remove/{id}")
	public String remove(ModelMap model, @PathVariable("id") long id) {
		log.debug("remove course " + id);
		courseService.delete(id);
		return "redirect:/course";
	}


	@RequestMapping(value = "/course/save", method = RequestMethod.POST)
	public String save(ModelMap model, @ModelAttribute("course") @Valid Course course, BindingResult result) {
		log.debug("save: " + course.toString());
		boolean newCourse = course.getId() == null ? true : false;
		if (!result.hasErrors()) {
			if(newCourse)
				model.put("message", "The new course has been successfully created.");				
			else
				model.put("message", "The course has been successfully updated.");
			model.put("idCourse", course.getId());
		}
		else {
			for (ObjectError e : result.getAllErrors()) {
				log.error(e.getDefaultMessage());
			}
		}
		return "list-courses";
	}
}
