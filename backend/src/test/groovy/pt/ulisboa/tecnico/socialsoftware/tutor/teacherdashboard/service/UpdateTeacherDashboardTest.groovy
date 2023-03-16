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

    def "update all teacher dashboards with only one dashboard available"() {
        given: "only one teacher dashboard"

        and: "one quiz stats and three quizzes"
        QuizStats quizStats = createQuizStat(teacherDashboard)

        def quiz1 = createQuiz()
        def quiz2 = createQuiz()
        def quiz3 = createQuiz()

        externalCourseExecution.addQuiz(quiz1)
        externalCourseExecution.addQuiz(quiz2)
        externalCourseExecution.addQuiz(quiz3)

        def numQuizzesBeforeUpdate = quizStats.getNumQuizzes()

        when: "we update all the teacher dashboards"
        teacherDashboardService.updateAllTeacherDashboards()

        then: "the number of the quizzes must be updated"
        def QuizzesAfterUpdate = teacherDashboard.getQuizStats()

        numQuizzesBeforeUpdate != QuizzesAfterUpdate[0].getNumQuizzes()
        QuizzesAfterUpdate[0].getNumQuizzes() == 3
    }

    def "update all teacher dashboards with multiple dashboards available"() {
        given: "three teacher dashboard"
        def teacher1 = new Teacher(USER_1_NAME, false)
        def teacher2 = new Teacher(USER_1_NAME, false)
        def teacher3 = new Teacher(USER_1_NAME, false)

        userRepository.save(teacher1)
        userRepository.save(teacher2)
        userRepository.save(teacher3)

        def teacherDashboard1 = new TeacherDashboard(externalCourseExecution, teacher1)
        def teacherDashboard2 = new TeacherDashboard(externalCourseExecution, teacher2)
        def teacherDashboard3 = new TeacherDashboard(externalCourseExecution, teacher3)

        teacherDashboardRepository.save(teacherDashboard1)
        teacherDashboardRepository.save(teacherDashboard2)
        teacherDashboardRepository.save(teacherDashboard3)

        and: "two quizzes"
        def quiz1 = createQuiz()
        def quiz2 = createQuiz()

        externalCourseExecution.addQuiz(quiz1)
        externalCourseExecution.addQuiz(quiz2)

        and: "one quiz stats"
        def quizStats1 = createQuizStat(teacherDashboard1)

        def numQuizzesBeforeUpdate1 = quizStats1.getNumQuizzes()

        when: "we update all the teacher dashboards"
        teacherDashboardService.updateAllTeacherDashboards()

        then: "the number of the quizzes must be updated"
        def QuizzesAfterUpdate1 = teacherDashboard1.getQuizStats()

        numQuizzesBeforeUpdate1 != QuizzesAfterUpdate1[0].getNumQuizzes()
        QuizzesAfterUpdate1[0].getNumQuizzes() == 2

        when: "we add two more quizzes"
        def quiz3 = createQuiz()
        def quiz4 = createQuiz()

        externalCourseExecution.addQuiz(quiz3)
        externalCourseExecution.addQuiz(quiz4)

        and: "one more quiz stats"
        def quizStats2 = createQuizStat(teacherDashboard2)

        def numQuizzesBeforeUpdate2 = quizStats2.getNumQuizzes()

        and: "we update all the teacher dashboards"
        teacherDashboardService.updateAllTeacherDashboards()

        then: "we should have a total of four quizzes"
        def QuizzesAfterUpdate2 = teacherDashboard2.getQuizStats()

        numQuizzesBeforeUpdate2 != QuizzesAfterUpdate2[0].getNumQuizzes()
        QuizzesAfterUpdate2[0].getNumQuizzes() == 4

    }

    def "update all teacher dashboards with no dashboards available (after remove)"() {
        given: "only one teacher dashboard"

        when: "we remove the only teacher dashboard available"
        teacherDashboardService.removeTeacherDashboard(teacherDashboard.getId())

        and: "we try to update all teacher dashboards"
        teacherDashboardService.updateAllTeacherDashboards()

        then: "it should throw an exception"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == NO_DASHBOARDS_AVAILABLE
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}