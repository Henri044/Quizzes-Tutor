package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuestionStats
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import spock.lang.Unroll
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course

@DataJpaTest
class QuestionStatsTest extends SpockTest {

    def numAvailable
    def answeredQuestionUnique
    def averageQuestionsAnswered
    def teacher
    def teacherDashboard
    def questionStats

    def setup() {
        createExternalCourseAndExecution()

        teacher = new Teacher("Olá", false)
        userRepository.save(teacher)

        teacherDashboard = new TeacherDashboard(externalCourseExecution, teacher)
        teacherDashboardRepository.save(teacherDashboard)

    }
    def createQuestionStatsAndPersist() {
        questionStats = new QuestionStats(externalCourseExecution, teacherDashboard)
        questionStatsRepository.save(questionStats)
    }

    def "Create an empty questionStats"() {

        given: "a teacherDashboard and a teacher"

        when: "a new questionStats is created"
        def questionStats = createQuestionStatsAndPersist()
        teacherDashboard.addQuestionStats(questionStats)

        then: "the new questionStats is correctly persisted"
        questionStatsRepository.count() == 1L
        def result = questionStatsRepository.findAll().get(0)

        result.getId() != 0
        result.getCourseExecution().getId() == externalCourseExecution.getId()
        result.getTeacherDashboard().getId() == teacherDashboard.getId()

        and: "The questionStats has a reference for the dashboard"
        teacherDashboard.getQuestionStats().size() == 1
        teacherDashboard.getQuestionStats().contains(result)

    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}


}