package pt.ulisboa.tecnico.socialsoftware.tutor.execution;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.repository.AuthUserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.demo.DemoUtils;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.dto.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.repository.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.QuestionsXmlImport;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.TopicService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.CourseRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.StudentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.UserRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class CourseExecutionService {
    @Autowired
    private QuestionService questionService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthUserRepository authUserRepository;

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public CourseExecutionDto getCourseExecutionById(int courseExecutionId) {
        return courseExecutionRepository.findById(courseExecutionId)
                .map(CourseExecutionDto::new)
                .orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, courseExecutionId));
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<CourseExecutionDto> getCourseExecutions(User.Role role) {
        return courseExecutionRepository.findAll().stream()
                .filter(courseExecution -> role.equals(User.Role.ADMIN) ||
                        (role.equals(User.Role.DEMO_ADMIN) && courseExecution.getCourse().getName().equals(DemoUtils.COURSE_NAME)))
                .map(CourseExecutionDto::new)
                .sorted(Comparator
                        .comparing(CourseExecutionDto::getName)
                        .thenComparing(CourseExecutionDto::getAcademicTerm))
                .collect(Collectors.toList());
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public CourseExecutionDto createTecnicoCourseExecution(CourseExecutionDto courseExecutionDto) {
        courseExecutionDto.setCourseExecutionType(Course.Type.TECNICO);
        courseExecutionDto.setCourseType(Course.Type.TECNICO);

        Course course = getCourse(courseExecutionDto.getName(), Course.Type.TECNICO);

        CourseExecution courseExecution = course.getCourseExecution(courseExecutionDto.getAcronym(), courseExecutionDto.getAcademicTerm(), courseExecutionDto.getCourseExecutionType())
                .orElseGet(() -> createCourseExecution(course, courseExecutionDto));
        courseExecution.setStatus(CourseExecution.Status.ACTIVE);
        return new CourseExecutionDto(courseExecution);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void addUserToTecnicoCourseExecution(String username, int courseExecutionId) {
        CourseExecution courseExecution = this.courseExecutionRepository.findById(courseExecutionId).orElse(null);
        User user = this.authUserRepository.findAuthUserByUsername(username).map(AuthUser::getUser).orElse(null);

        if (user != null && courseExecution != null) {
            courseExecution.addUser(user);
            user.addCourse(courseExecution);
        }
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public CourseExecutionDto createExternalCourseExecution(CourseExecutionDto courseExecutionDto) {
        courseExecutionDto.setCourseExecutionType(Course.Type.EXTERNAL);

        Course course = getCourse(courseExecutionDto.getName(), courseExecutionDto.getCourseType());

        CourseExecution courseExecution = createCourseExecution(course, courseExecutionDto);

        courseExecution.setStatus(CourseExecution.Status.ACTIVE);
        return new CourseExecutionDto(courseExecution);
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void removeCourseExecution(int courseExecutionId) {
        CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId)
                .orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, courseExecutionId));

        courseExecution.remove();

        courseExecutionRepository.delete(courseExecution);
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<StudentDto> getStudents(int executionId) {
        CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElse(null);
        if (courseExecution == null) {
            return new ArrayList<>();
        }

        return courseExecution.getUsers().stream()
                .filter(user -> user.getRole().equals(User.Role.STUDENT))
                .sorted(Comparator.comparing(User::getName))
                .map(Student.class::cast)
                .map(student -> new StudentDto(student, courseExecution))
                .collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<UserDto> getTeachers(Integer courseExecutionId) {
        CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId).orElse(null);
        if (courseExecution == null) {
            return new ArrayList<>();
        }
        return courseExecution.getUsers().stream()
                .filter(user -> user.getRole().equals(User.Role.TEACHER))
                .map(UserDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<QuestionDto> importQuestions(InputStream inputStream, Integer executionId) {
        CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND));

        QuestionsXmlImport questionsXmlImport = new QuestionsXmlImport();

        return questionsXmlImport.importQuestions(inputStream, this.questionService, this.topicService, this.courseRepository, courseExecution);
    }

    private Course getCourse(String name, Course.Type type) {
        if (type == null)
            throw new TutorException(INVALID_TYPE_FOR_COURSE);

        return courseRepository.findByNameType(name, type.toString())
                .orElseGet(() -> courseRepository.save(new Course(name, type)));
    }

    private CourseExecution createCourseExecution(Course existingCourse, CourseExecutionDto courseExecutionDto) {
        CourseExecution courseExecution = new CourseExecution(existingCourse, courseExecutionDto.getAcronym(), courseExecutionDto.getAcademicTerm(), courseExecutionDto.getCourseExecutionType(), DateHandler.toLocalDateTime(courseExecutionDto.getEndDate()));
        courseExecutionRepository.save(courseExecution);
        return courseExecution;
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public CourseExecutionDto getDemoCourse() {
        CourseExecution courseExecution = this.courseExecutionRepository.findByFields(DemoUtils.COURSE_ACRONYM, DemoUtils.COURSE_ACADEMIC_TERM, Course.Type.TECNICO.toString()).orElse(null);

        if (courseExecution == null) {
            return createTecnicoCourseExecution(new CourseExecutionDto(DemoUtils.COURSE_NAME, DemoUtils.COURSE_ACRONYM, DemoUtils.COURSE_ACADEMIC_TERM));
        }
        return new CourseExecutionDto(courseExecution);
    }

    public CourseExecution getDemoCourseExecution() {
        return this.courseExecutionRepository.findByFields(DemoUtils.COURSE_ACRONYM, DemoUtils.COURSE_ACADEMIC_TERM, Course.Type.TECNICO.toString()).orElseGet(() -> {
            Course course = getCourse(DemoUtils.COURSE_NAME, Course.Type.TECNICO);
            CourseExecution courseExecution = new CourseExecution(course, DemoUtils.COURSE_ACRONYM, DemoUtils.COURSE_ACADEMIC_TERM, Course.Type.TECNICO, DateHandler.now().plusDays(1));
            return courseExecutionRepository.save(courseExecution);
        });
    }

    private CourseExecution getExternalCourseExecution(Integer courseExecutionId) {
        CourseExecution execution = courseExecutionRepository
                .findById(courseExecutionId)
                .orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, courseExecutionId));
        checkExternalExecution(execution);
        return execution;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public CourseExecutionDto deleteExternalInactiveUsers(Integer courseExecutionId, List<Integer> usersId) {
        CourseExecution courseExecution = getExternalCourseExecution(courseExecutionId);
        deleteUsersOfUserIds(usersId, courseExecution);
        return new CourseExecutionDto(courseExecution);
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<TopicDto> findAvailableTopicsByCourseExecution(int courseExecutionId) {
        CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, courseExecutionId));

        return courseExecution.findAvailableTopics().stream().sorted(Comparator.comparing(Topic::getName)).map(TopicDto::new).collect(Collectors.toList());
    }

    private void deleteUsersOfUserIds(List<Integer> usersId, CourseExecution courseExecution) {
        usersId = getExecutionFilteredIds(usersId, courseExecution);
        deleteUsers(usersId);
    }

    private void deleteUsers(List<Integer> usersId) {
        for (Integer id : usersId) {
            User user = userRepository
                    .findById(id)
                    .orElseThrow(() -> new TutorException((USER_NOT_FOUND)));
            user.remove();
            userRepository.delete(user);
        }
    }

    private List<Integer> getExecutionFilteredIds(List<Integer> usersId, CourseExecution courseExecution) {
        List<Integer> executionUserIdList = courseExecution.getUsers().stream()
                .map(User::getId)
                .collect(Collectors.toList());
        return usersId.stream()
                .filter(executionUserIdList::contains)
                .collect(Collectors.toList());
    }

    private void checkExternalExecution(CourseExecution courseExecution) {
        if (!courseExecution.getType().equals(Course.Type.EXTERNAL)) {
            throw new TutorException(COURSE_EXECUTION_NOT_EXTERNAL);
        }
    }

}
