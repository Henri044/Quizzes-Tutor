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

        teacher = new Teacher(USER_1_NAME, false)
        userRepository.save(teacher)

        teacherDashboard = new TeacherDashboard(externalCourseExecution, teacher)
        teacherDashboardRepository.save(teacherDashboard)

    }
    def createQuestionStatsAndPersist() {
        questionStats = new QuestionStats(externalCourseExecution, teacherDashboard)
        questionStatsRepository.save(questionStats)
        return questionStats
    }
    def setNumAvailable() {
        def questionStats = new QuestionStats(externalCourseExecution, teacherDashboard)
        questionStats.setNumAvailable(4)
        questionStatsRepository.save(questionStats)
        return questionStats
    }
    def setAnsweredQuestionUnique() {
        def questionStats = new QuestionStats(externalCourseExecution, teacherDashboard)
        questionStats.setAnsweredQuestionUnique(4)
        questionStatsRepository.save(questionStats)
        return questionStats
    }
    def setAverageQuestionsAnswered() {
        def questionStats = new QuestionStats(externalCourseExecution, teacherDashboard)
        questionStats.setAverageQuestionsAnswered(4)
        questionStatsRepository.save(questionStats)
        return questionStats
    }
    def setAll() {
        def questionStats = new QuestionStats(externalCourseExecution, teacherDashboard)
        questionStats.setNumAvailable(4)
        questionStats.setAnsweredQuestionUnique(4)
        questionStats.setAverageQuestionsAnswered(4)
        questionStatsRepository.save(questionStats)
        return questionStats
    }
    def setAllRemoved() {
        def questionStats = new QuestionStats(externalCourseExecution, teacherDashboard)
        questionStats.setNumAvailable(5)
        questionStats.setAnsweredQuestionUnique(4)
        questionStats.setAverageQuestionsAnswered(4)
        questionStats.remove()
        questionStatsRepository.save(questionStats)
        return questionStats
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

        and: "the teacher has a reference for the dashboard"
        teacher.getDashboards().size() == 1
        teacher.getDashboards().contains(teacherDashboard)

        and: "the dashboard has a reference for the QuestionStats"
        teacherDashboard.getQuestionStats().size() == 1
        teacherDashboard.getQuestionStats().contains(result)

    }
    def "testing setNumAvailable and getNumAvailable"() {

        given: "a teacherDashboard and a teacher"

        when: "create a QuestionStats and set the number of questions"
        def questionStats = setNumAvailable()

        then: "the number of questions must be 4"
        def result = questionStatsRepository.findAll().get(0)
        result.getNumAvailable() == questionStats.getNumAvailable()
        result.getNumAvailable() == 4

    }
    def "testing setAnsweredQuestionUnique and getAnsweredQuestionUnique"() {

        given : "a teacherDashboard and a teacher"

        when: "create a QuestionStats and set the number of unique questions answered"
        def questionStats = setAnsweredQuestionUnique()

        then: "the number of unique questions answered must be 4"
        def result = questionStatsRepository.findAll().get(0)
        result.getAnsweredQuestionUnique() == questionStats.getAnsweredQuestionUnique()
        result.getAnsweredQuestionUnique() == 4

    }
    def "testing setAverageQuestionsAnswered and getAverageQuestionsAnswered"() {

        given : "a teacherDashboard and a teacher"

        when: "create a QuestionStats and set the value of the average of questions answered"
        def questionStats = setAverageQuestionsAnswered()

        then: "the number of questions must be 4"
        def result = questionStatsRepository.findAll().get(0)
        result.getAverageQuestionsAnswered() == questionStats.getAverageQuestionsAnswered()
        result.getAverageQuestionsAnswered() == 4

    }
    def "testing toString"() {

        given: "a teacherDashboard and a teacher"

        when: "create a QuestionStats and set the variables of the questions"
        def questionStats = setAll()

        then: "check if toString match the predicted string"
        def result = questionStatsRepository.findAll().get(0)
        def auxString = "QuestionStats{" + "id = " + questionStats.getId() + ", Numero questoes disponiveis = 4" +
                ", Questoes unicas respondidas = 4" + ", Media questoes respondidas = 4.0" + '}'
        questionStats.toString().equals(auxString) == true
        result.toString().equals(questionStats.toString()) == true

    }
    def "remove test"() {

        given: "a teacherDashboard and a teacher"

        when: "a new QuestionStats is created, and set variables, and then removed"
        def questionStats = setAllRemoved()

        then: "the QuestionStat is removed"
        def result = teacherDashboardRepository.findAll().get(0)
        teacherDashboard.getQuestionStats() == result.getQuestionStats()
        teacherDashboard.getQuestionStats() == new HashSet<QuestionStats>()

    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}


}