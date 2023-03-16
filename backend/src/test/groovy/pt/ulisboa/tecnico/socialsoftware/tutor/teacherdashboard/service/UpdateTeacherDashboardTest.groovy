package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuestionStats
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuizStats
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.StudentStats
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.DASHBOARD_NOT_FOUND
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.NO_DASHBOARDS_AVAILABLE

@DataJpaTest
class UpdateTeacherDashboardTest extends SpockTest {

    def teacherDashboard
    def teacher
    def quiz

    def setup() {

        createExternalCourseAndExecution()

        teacher = new Teacher(USER_1_NAME, false)

        userRepository.save(teacher)

        teacherDashboard = new TeacherDashboard(externalCourseExecution, teacher)
        teacherDashboardRepository.save(teacherDashboard)

    }

    def createQuizStat(TeacherDashboard teacherDashboard) {
        def quizStat = new QuizStats(teacherDashboard, externalCourseExecution)
        quizStatsRepository.save(quizStat)
        return quizStat
    }

    def createStudentStat() {
        def studentStat = new StudentStats(teacherDashboard, externalCourseExecution)
        studentStatsRepository.save(studentStat)
        return studentStat
    }

    def createQuiz(type = Quiz.QuizType.PROPOSED.toString()) {
        // Quiz
        quiz = new Quiz()
        quiz.setTitle("Quiz Title")
        quiz.setType(type)
        quiz.setCourseExecution(externalCourseExecution)
        quiz.setCreationDate(DateHandler.now())
        quiz.setAvailableDate(DateHandler.now())
        quizRepository.save(quiz)
        return quiz
    }

    def createStudent(username) {
        def student = new Student(USER_1_USERNAME, username, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
        student.addCourse(externalCourseExecution)
        userRepository.save(student)
        return student;
    }

    def "update teacher dashboard with full statistics"() {
        given: "3 students, 2 quizzes and 2 questions"

        def studentStats = createStudentStat()
        def quizStats = createQuizStat(teacherDashboard)

        def student1 = createStudent("student1")
        def student2 = createStudent("student2")
        def student3 = createStudent("student3")

        def quiz1 = createQuiz()
        def quiz2 = createQuiz()

        when: "we update data"

        externalCourseExecution.addUser(student1)
        externalCourseExecution.addUser(student2)
        externalCourseExecution.addUser(student3)
        externalCourseExecution.addQuiz(quiz1)
        externalCourseExecution.addQuiz(quiz2)

        then: "teacherDashboard get updated data"

        def numStudents = studentStats.getNumStudents()
        def numQuizzes = quizStats.getNumQuizzes()
        teacherDashboardService.updateTeacherDashboard(teacherDashboard.getId())
        def updatedNumStudents =  (teacherDashboardRepository.getById(teacherDashboard.id)).getStudentStats()
        def updatedNumQuizzes = (teacherDashboardRepository.getById(teacherDashboard.id)).getQuizStats()
        numStudents != updatedNumStudents[0].getNumStudents()
        updatedNumStudents[0].getNumStudents() == 3
        numQuizzes != updatedNumQuizzes[0].getNumQuizzes()
        updatedNumQuizzes[0].getNumQuizzes() == 2
    }

    @Unroll
    def "cannot update teacherDashboard with invalid dashboardId=#dashboardId"() {

        given: "2 students"

        def studentStats = createStudentStat()
        def student1 = createStudent("student1")
        def student2 = createStudent("student2")

        externalCourseExecution.addUser(student1)
        externalCourseExecution.addUser(student2)

        when: "we update the data in teacherDashboard with incorrect dashboardId"
        def numStudents = studentStats.getNumStudents()
        teacherDashboardService.updateTeacherDashboard(dashboardId)

        then: "throws an Exception"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == DASHBOARD_NOT_FOUND
        def updatedNumStudents =  (teacherDashboardRepository.getById(teacherDashboard.id)).getStudentStats()
        numStudents == updatedNumStudents[0].getNumStudents()
        updatedNumStudents[0].getNumStudents() == 0

        where:
        dashboardId << [0, 100]
    }

    def "cannot update teacherDashboard with null dashboardId"() {

        given: "2 students"

        def studentStats = createStudentStat()
        def student1 = createStudent("student1")
        def student2 = createStudent("student2")

        externalCourseExecution.addUser(student1)
        externalCourseExecution.addUser(student2)

        when: "we update the data in teacherDashboard with null dashboardId"
        def numStudents = studentStats.getNumStudents()
        teacherDashboardService.updateTeacherDashboard(null)

        then: "throws an Exception"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == DASHBOARD_NOT_FOUND
        def updatedNumStudents =  (teacherDashboardRepository.getById(teacherDashboard.id)).getStudentStats()
        numStudents == updatedNumStudents[0].getNumStudents()
        updatedNumStudents[0].getNumStudents() == 0
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}