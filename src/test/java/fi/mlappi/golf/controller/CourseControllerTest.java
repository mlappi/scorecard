package fi.mlappi.golf.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import fi.mlappi.golf.model.Course;
import fi.mlappi.golf.model.Hole;
import fi.mlappi.golf.service.CourseService;

@ExtendWith(MockitoExtension.class)
class CourseControllerTest {

    @Mock
    private CourseService courseService;

    @InjectMocks
    private CourseController controller;

    @Test
    void coursesReturnsListViewWithCourses() {
        List<Course> courses = List.of(new Course(), new Course());
        when(courseService.getAllCourses()).thenReturn(courses);
        ModelMap model = new ModelMap();

        String view = controller.courses(model);

        assertThat(view).isEqualTo("list-courses");
        assertThat(model.get("courses")).isSameAs(courses);
    }

    @Test
    void addCreatesNewCourseAndReturnsFormView() {
        Course course = new Course();
        when(courseService.create()).thenReturn(course);
        ModelMap model = new ModelMap();

        String view = controller.add(model);

        assertThat(view).isEqualTo("new-course");
        assertThat(model.get("course")).isSameAs(course);
    }

    @Test
    void editLoadsCourseAndReturnsFormView() {
        Course course = new Course();
        when(courseService.find(10L)).thenReturn(course);
        ModelMap model = new ModelMap();

        ModelAndView view = controller.edit(model, 10L);

        assertThat(view.getViewName()).isEqualTo("new-course");
        assertThat(model.get("course")).isSameAs(course);
    }

    @Test
    void removeDeletesCourseAndRedirects() {
        ModelMap model = new ModelMap();

        String view = controller.remove(model, 5L);

        assertThat(view).isEqualTo("redirect:/course");
        verify(courseService).delete(5L);
    }

    @Test
    void saveNewCoursePersistsCourseAndHoles() {
        Course course = new Course();
        Hole h1 = new Hole();
        Hole h2 = new Hole();
        h1.setHole(1);
        h2.setHole(2);
        course.setHole(Arrays.asList(h1, h2));
        ModelMap model = new ModelMap();
        BindingResult result = new BeanPropertyBindingResult(course, "course");

        doAnswer(invocation -> {
            Course saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        }).when(courseService).save(course);
        when(courseService.save(any(Hole.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(courseService.getAllCourses()).thenReturn(Collections.singletonList(course));

        String view = controller.save(model, course, result);

        assertThat(view).isEqualTo("list-courses");
        assertThat(model.get("message")).isEqualTo("Uusi kenttä on lisätty onnistuneesti.");
        assertThat(model.get("idCourse")).isEqualTo(1L);
        assertThat(h1.getCourse()).isSameAs(course);
        assertThat(h2.getCourse()).isSameAs(course);
        verify(courseService, times(2)).save(course);
        verify(courseService).save(h1);
        verify(courseService).save(h2);
    }

    @Test
    void saveWithErrorsDoesNotPersist() {
        Course course = new Course();
        ModelMap model = new ModelMap();
        BindingResult result = new BeanPropertyBindingResult(course, "course");
        result.reject("invalid");
        when(courseService.getAllCourses()).thenReturn(Collections.emptyList());

        String view = controller.save(model, course, result);

        assertThat(view).isEqualTo("list-courses");
        verify(courseService, never()).save((Course) any());
        verify(courseService, never()).save((Hole) any());
    }
}
