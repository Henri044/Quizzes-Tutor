package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.repository.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuestionStats;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.StudentStats;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuizStats;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto.TeacherDashboardDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.QuestionStatsRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.StudentStatsRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.TeacherDashboardRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.QuizStatsRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.TeacherRepository;


import java.util.*;import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class TeacherDashboardService {

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private TeacherDashboardRepository teacherDashboardRepository;

    @Autowired
    private QuizStatsRepository quizStatsRepository;
    @Autowired
    private QuestionStatsRepository questionStatsRepository;

    @Autowired
    private StudentStatsRepository studentStatsRepository;

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

        return dashboardOptional.
                map(TeacherDashboardDto::new).
                orElseGet(() -> createAndReturnTeacherDashboardDto(courseExecution, teacher));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TeacherDashboardDto createTeacherDashboard(int courseExecutionId, int teacherId) {
        CourseExecution courseExecution = courseExecutionRepository.findById(courseExecutionId)
                .orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND));
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new TutorException(USER_NOT_FOUND, teacherId));

        if (teacher.getDashboards().stream().anyMatch(dashboard -> dashboard.getCourseExecution().equals(courseExecution)))
            throw new TutorException(TEACHER_ALREADY_HAS_DASHBOARD);

        if (!teacher.getCourseExecutions().contains(courseExecution))
            throw new TutorException(TEACHER_NO_COURSE_EXECUTION);

        return createAndReturnTeacherDashboardDto(courseExecution, teacher);
    }

    private TeacherDashboardDto createAndReturnTeacherDashboardDto(CourseExecution courseExecution, Teacher teacher) {
        TeacherDashboard teacherDashboard = new TeacherDashboard(courseExecution, teacher);
        List<CourseExecution> threelastCourseexecutions = courseExecution.getCourse().getCourseExecutions().stream()
                .filter(ce -> ce.getEndDate() != null)
                .sorted(Comparator.comparing(CourseExecution::getEndDate)).limit(3)
                .collect(Collectors.toList());
        for (int i = 0;i<threelastCourseexecutions.size();i++){
            QuizStats quizStats1 = new QuizStats(teacherDashboard, threelastCourseexecutions.get(i));
            quizStatsRepository.save(quizStats1);
        }
        for (int i = 0;i<threelastCourseexecutions.size();i++){
            QuestionStats questionStats1 = new QuestionStats(teacherDashboard, threelastCourseexecutions.get(i));
            questionStatsRepository.save(questionStats1);
        }
        for (int i = 0;i<threelastCourseexecutions.size();i++){
            StudentStats studentStats1 = new StudentStats(teacherDashboard, threelastCourseexecutions.get(i));
            studentStatsRepository.save(studentStats1);
        }
        teacherDashboard.update();

        teacherDashboardRepository.save(teacherDashboard);

        return new TeacherDashboardDto(teacherDashboard);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void removeTeacherDashboard(Integer dashboardId) {
        if (dashboardId == null)
            throw new TutorException(DASHBOARD_NOT_FOUND, -1);

        TeacherDashboard teacherDashboard = teacherDashboardRepository.findById(dashboardId).orElseThrow(() -> new TutorException(DASHBOARD_NOT_FOUND, dashboardId));
        quizStatsRepository.deleteAll(teacherDashboard.getQuizStats());
        studentStatsRepository.deleteAll(teacherDashboard.getStudentStats());
        questionStatsRepository.deleteAll(teacherDashboard.getQuestionStats());
        teacherDashboard.remove();
        teacherDashboardRepository.delete(teacherDashboard);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateAllTeacherDashboards() {
        List<TeacherDashboard> allTeacherDashboards = teacherDashboardRepository.findAll();

        if (allTeacherDashboards.isEmpty()) {
            throw new TutorException(NO_DASHBOARDS_AVAILABLE, -1);
        }

        allTeacherDashboards.forEach(teacherDashboard -> {
            teacherDashboard.update();
            teacherDashboardRepository.save(teacherDashboard);
        });
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void updateTeacherDashboard(Integer dashboardId) {
        if (dashboardId == null)
            throw new TutorException(DASHBOARD_NOT_FOUND, -1);

        TeacherDashboard teacherDashboard = teacherDashboardRepository.findById(dashboardId).orElseThrow(() -> new TutorException(DASHBOARD_NOT_FOUND, dashboardId));
        teacherDashboard.update();
        teacherDashboardRepository.save(teacherDashboard);
    }
}
