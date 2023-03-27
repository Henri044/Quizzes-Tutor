package pt.ulisboa.tecnico.socialsoftware.tutor.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthTecnicoUser;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.domain.StudentDashboard;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.domain.DifficultQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.domain.FailedAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.domain.WeeklyScore;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.repository.StudentDashboardRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.repository.DifficultQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.repository.FailedAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.repository.WeeklyScoreRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository.TeacherDashboardRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Discussion;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.domain.Reply;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.repository.DiscussionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.discussion.repository.ReplyRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.CourseExecutionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.dto.CourseExecutionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.repository.AssessmentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.repository.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.repository.QuestionSubmissionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.repository.UserRepository;

import java.io.Serializable;

@Component
public class TutorPermissionEvaluator implements PermissionEvaluator {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionSubmissionRepository questionSubmissionRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private CourseExecutionService courseExecutionService;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private QuestionAnswerRepository questionAnswerRepository;

    @Autowired
    private DiscussionRepository discussionRepository;

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private StudentDashboardRepository studentDashboardRepository;

    @Autowired
    private TeacherDashboardRepository teacherDashboardRepository;

    @Autowired
    private WeeklyScoreRepository weeklyScoreRepository;

    @Autowired
    private FailedAnswerRepository failedAnswerRepository;

    @Autowired
    private DifficultQuestionRepository difficultQuestionRepository;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        AuthUser authUser = ((AuthUser) authentication.getPrincipal());
        int userId = authUser.getUser().getId();

        if (targetDomainObject instanceof CourseExecutionDto) {
            CourseExecutionDto courseExecutionDto = (CourseExecutionDto) targetDomainObject;
            String permissionValue = (String) permission;
            switch (permissionValue) {
                case "EXECUTION.CREATE":
                    return ((AuthTecnicoUser) authUser).getEnrolledCoursesAcronyms().contains(courseExecutionDto.getAcronym() + courseExecutionDto.getAcademicTerm());
                case "DEMO.ACCESS":
                    return courseExecutionDto.getName().equals("Demo Course");
                default:
                    return false;
            }
        }

        if (targetDomainObject instanceof Integer) {
            int id = (int) targetDomainObject;
            String permissionValue = (String) permission;
            switch (permissionValue) {
                case "DEMO.ACCESS":
                    CourseExecutionDto courseExecutionDto = courseExecutionService.getCourseExecutionById(id);
                    return courseExecutionDto.getName().equals("Demo Course");
                case "COURSE.ACCESS":
                    return userHasAnExecutionOfCourse(authUser, id);
                case "EXECUTION.ACCESS":
                    return userHasThisExecution(authUser, id);
                case "QUESTION.ACCESS":
                    Question question = questionRepository.findQuestionWithCourseById(id).orElse(null);
                    if (question != null) {
                        return userHasAnExecutionOfCourse(authUser, question.getCourse().getId());
                    }
                    return false;
                case "TOPIC.ACCESS":
                    Topic topic = topicRepository.findTopicWithCourseById(id).orElse(null);
                    if (topic != null) {
                        return userHasAnExecutionOfCourse(authUser, topic.getCourse().getId());
                    }
                    return false;
                case "ASSESSMENT.ACCESS":
                    Integer courseExecutionId = assessmentRepository.findCourseExecutionIdById(id).orElse(null);
                    if (courseExecutionId != null) {
                        return userHasThisExecution(authUser, courseExecutionId);
                    }
                    return false;
                case "QUIZ.ACCESS":
                    courseExecutionId = quizRepository.findCourseExecutionIdById(id).orElse(null);
                    if (courseExecutionId != null) {
                        return userHasThisExecution(authUser, courseExecutionId);
                    }
                    return false;
                case "TOURNAMENT.ACCESS":
                    courseExecutionId = tournamentRepository.findCourseExecutionIdByTournamentId(id).orElse(null);
                    if (courseExecutionId != null) {
                        return userHasThisExecution(authUser, courseExecutionId);
                    }
                    return false;
                case "TOURNAMENT.PARTICIPANT":
                    return userParticipatesInTournament(userId, id);
                case "TOURNAMENT.OWNER":
                    Tournament tournament = tournamentRepository.findById(id).orElse(null);
                    if (tournament != null) {
                        return tournament.isCreator(authUser.getUser());
                    }
                    return false;
                case "SUBMISSION.ACCESS":
                    QuestionSubmission questionSubmission = questionSubmissionRepository.findById(id).orElse(null);
                    if (questionSubmission != null) {
                        boolean hasCourseExecutionAccess = userHasThisExecution(authUser, questionSubmission.getCourseExecution().getId());
                        if (authUser.getUser().getRole() == User.Role.STUDENT) {
                            return hasCourseExecutionAccess && questionSubmission.getSubmitter().getId().equals(userId);
                        } else {
                            return hasCourseExecutionAccess;
                        }
                    }
                    return false;
                case "QUESTION_ANSWER.ACCESS":
                    QuestionAnswer questionAnswer = questionAnswerRepository.findById(id).orElse(null);
                    return questionAnswer != null && questionAnswer.getQuizAnswer().getStudent().getId().equals(userId);
                case "DISCUSSION.OWNER":
                    Discussion discussion = discussionRepository.findById(id).orElse(null);
                    return discussion != null && discussion.getStudent().getId().equals(userId);
                case "DISCUSSION.ACCESS":
                    discussion = discussionRepository.findById(id).orElse(null);
                    return discussion != null && authUser.getUser().isTeacher() && userHasThisExecution(authUser, discussion.getCourseExecution().getId());
                case "REPLY.ACCESS":
                    Reply reply = replyRepository.findById(id).orElse(null);
                    return reply != null && userHasThisExecution(authUser, reply.getDiscussion().getCourseExecution().getId());
                case "DASHBOARD.ACCESS":
                    StudentDashboard studentDashboard = studentDashboardRepository.findById(id).orElse(null);
                    return studentDashboard != null && studentDashboard.getStudent().getId().equals(userId);
                case "TEACHER.DASHBOARD.ACCESS":
                    TeacherDashboard teacherDashboard = teacherDashboardRepository.findById(id).orElse(null);
                    return teacherDashboard != null && teacherDashboard.getTeacher().getId().equals(userId);
                case "WEEKLYSCORE.ACCESS":
                    WeeklyScore weeklyScore = weeklyScoreRepository.findById(id).orElse(null);
                    return weeklyScore != null && weeklyScore.getDashboard().getStudent().getId().equals(userId);
                case "FAILEDANSWER.ACCESS":
                    FailedAnswer failedAnswer = failedAnswerRepository.findById(id).orElse(null);
                    return failedAnswer != null && failedAnswer.getDashboard().getStudent().getId().equals(userId);
                case "DIFFICULTQUESTION.ACCESS":
                    DifficultQuestion difficultQuestion = difficultQuestionRepository.findById(id).orElse(null);
                    return difficultQuestion != null && userHasThisExecution(authUser, difficultQuestion.getCourseExecution().getId());
                default:
                    return false;
            }
        }

        return false;
    }

    private boolean userHasThisExecution(AuthUser authUser, int courseExecutionId) {
        return authUser.getCourseExecutionsIds().contains(courseExecutionId);
    }

    private boolean userParticipatesInTournament(int userId, int tournamentId) {
        return userRepository.countUserTournamentPairById(userId, tournamentId) == 1;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable serializable, String s, Object o) {
        return false;
    }

    public boolean userHasAnExecutionOfCourse(AuthUser authUser, int courseId) {
        return courseExecutionRepository.getCourseExecutionsIdByCourseId(courseId)
                .stream()
                .anyMatch(courseExecutionId -> userHasThisExecution(authUser, courseExecutionId));
    }
}
