package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.StudentDashboard;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.TeacherDashboard;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto.StudentDashboardDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto.TeacherDashboardDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto.StatsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.repository.StudentDashboardRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.repository.TeacherDashboardRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.repository.DifficultQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.CourseExecutionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.repository.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.StudentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.TeacherRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.UserRepository;

import java.sql.SQLException;
import java.util.*;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class DashboardService {

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private CourseExecutionService courseExecutionService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private DifficultQuestionRepository difficultQuestionRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @Autowired
    private StudentDashboardRepository studentDashboardRepository;

    @Autowired
    private TeacherDashboardRepository teacherDashboardRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TeacherDashboardDto getTeacherDashboard(int courseExecutionId, int teacherId) {
        CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId)
                .orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND));
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new TutorException(USER_NOT_FOUND, teacherId));

        if (!teacher.getCourseExecutions().contains(courseExecution))
            throw new TutorException(TEACHER_NO_COURSE_EXECUTION);

        Optional<TeacherDashboard> dashboardOptional = teacher.getDashboards().stream()
                .filter(dashboard -> dashboard.getCourseExecution().getId().equals(courseExecutionId))
                .findAny();

        int numberOfStudents = (int) studentRepository.findAll().stream().filter(student -> student.getCourseExecutions().contains(courseExecution)).count();

        return dashboardOptional.
                map(teacherDashboard -> { teacherDashboard.setNumberOfStudents(numberOfStudents); return new TeacherDashboardDto(teacherDashboard); }).
                orElseGet(() -> createAndReturnTeacherDashboardDto(courseExecution, teacher, numberOfStudents));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public StudentDashboardDto getStudentDashboard(int courseExecutionId, int studentId) {
        CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId)
                .orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new TutorException(USER_NOT_FOUND, studentId));

        if (!student.getCourseExecutions().contains(courseExecution))
            throw new TutorException(STUDENT_NO_COURSE_EXECUTION);

        Optional<StudentDashboard> dashboardOptional = student.getDashboards().stream()
                .filter(dashboard -> dashboard.getCourseExecution().getId().equals(courseExecutionId))
                .findAny();

        return dashboardOptional.map(StudentDashboardDto::new).orElseGet(() -> createAndReturnStudentDashboardDto(courseExecution, student));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public StudentDashboardDto createStudentDashboard(int courseExecutionId, int studentId) {
        CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId)
                .orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new TutorException(USER_NOT_FOUND, studentId));

        if (student.getDashboards().stream().anyMatch(dashboard -> dashboard.getCourseExecution().equals(courseExecution)))
            throw new TutorException(STUDENT_ALREADY_HAS_DASHBOARD);

        if (!student.getCourseExecutions().contains(courseExecution))
            throw new TutorException(STUDENT_NO_COURSE_EXECUTION);

        return createAndReturnStudentDashboardDto(courseExecution, student);
    }

    private StudentDashboardDto createAndReturnStudentDashboardDto(CourseExecution courseExecution, Student student) {
        StudentDashboard studentDashboard = new StudentDashboard(courseExecution, student);
        studentDashboardRepository.save(studentDashboard);

        return new StudentDashboardDto(studentDashboard);
    }

    private TeacherDashboardDto createAndReturnTeacherDashboardDto(CourseExecution courseExecution, Teacher teacher, Integer numberOfStudents) {
        TeacherDashboard teacherDashboard = new TeacherDashboard(courseExecution, teacher);
        teacherDashboard.setNumberOfStudents(numberOfStudents);
        teacherDashboardRepository.save(teacherDashboard);

        return new TeacherDashboardDto(teacherDashboard);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void removeDashboard(Integer dashboardId) {
        if (dashboardId == null)
            throw new TutorException(DASHBOARD_NOT_FOUND, -1);

        StudentDashboard studentDashboard = studentDashboardRepository.findById(dashboardId).orElseThrow(() -> new TutorException(DASHBOARD_NOT_FOUND, dashboardId));
        studentDashboard.remove();
        studentDashboardRepository.delete(studentDashboard);
    }

    @Retryable(
            value = {SQLException.class},
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public StatsDto getStats(int dashboardId) {
        StudentDashboard studentDashboard = studentDashboardRepository.findById(dashboardId).orElseThrow(() -> new TutorException(ErrorMessage.DASHBOARD_NOT_FOUND, dashboardId));
        Student student = studentDashboard.getStudent();
        CourseExecution courseExecution = studentDashboard.getCourseExecution();

        Set<QuizAnswer> answers = quizAnswerRepository.findByStudentAndCourseExecution(student.getId(), courseExecution.getId());

        StatsDto statsDto = new StatsDto();

        int totalQuizzes = (int) answers.stream()
                .filter(QuizAnswer::canResultsBePublic)
                .count();

        int totalAnswers = (int) answers.stream()
                .filter(QuizAnswer::canResultsBePublic)
                .map(QuizAnswer::getQuestionAnswers)
                .mapToLong(Collection::size)
                .sum();

        int uniqueQuestions = (int) answers.stream()
                .filter(QuizAnswer::canResultsBePublic)
                .map(QuizAnswer::getQuestionAnswers)
                .flatMap(Collection::stream)
                .map(QuestionAnswer::getQuizQuestion)
                .map(QuizQuestion::getQuestion)
                .map(Question::getId)
                .distinct().count();

        int correctAnswers = (int) answers.stream()
                .filter(QuizAnswer::canResultsBePublic)
                .map(QuizAnswer::getQuestionAnswers)
                .flatMap(Collection::stream)
                .filter(QuestionAnswer::isCorrect)
                .count();

        int uniqueCorrectAnswers = (int) answers.stream()
                .filter(quizAnswer -> quizAnswer.canResultsBePublic() && quizAnswer.getAnswerDate() != null)
                .sorted(Comparator.comparing(QuizAnswer::getAnswerDate).reversed())
                .map(QuizAnswer::getQuestionAnswers)
                .flatMap(Collection::stream)
                .collect(collectingAndThen(toCollection(() -> new TreeSet<>(comparingInt(questionAnswer -> questionAnswer.getQuizQuestion().getQuestion().getId()))),
                        ArrayList::new)).stream()
                .filter(QuestionAnswer::isCorrect)
                .count();

        Course course = courseExecution.getCourse();

        int totalAvailableQuestions = questionRepository.getAvailableQuestionsSize(course.getId());

        statsDto.setTotalQuizzes(totalQuizzes);
        statsDto.setTotalAnswers(totalAnswers);
        statsDto.setTotalUniqueQuestions(uniqueQuestions);
        statsDto.setTotalAvailableQuestions(totalAvailableQuestions);
        if (totalAnswers != 0) {
            statsDto.setCorrectAnswers(((float) correctAnswers) * 100 / totalAnswers);
            statsDto.setImprovedCorrectAnswers(((float) uniqueCorrectAnswers) * 100 / uniqueQuestions);
        }

        statsDto.setCreatedDiscussions(student.getDiscussions().size());
        return statsDto;
    }

}
