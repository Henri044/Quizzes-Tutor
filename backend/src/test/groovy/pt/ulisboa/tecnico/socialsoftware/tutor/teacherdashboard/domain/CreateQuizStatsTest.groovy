package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import spock.lang.Unroll

@DataJpaTest
class CreateQuizStatsTest extends SpockTest {
    def teacher
    def teacherDashboard
    def quizStats

    def setup() {
        createExternalCourseAndExecution()

        teacher = new Teacher(USER_1_NAME, false)
        userRepository.save(teacher)
        teacherDashboard = new TeacherDashboard(externalCourseExecution, teacher)
        teacherDashboardRepository.save(teacherDashboard)
    }
    def createQuizStatsAndPersist() {
        def quizStats = new QuizStats(externalCourseExecution, teacherDashboard)
        quizStatsRepository.save(quizStats)
        return quizStats
    }
    def setNumQuizzes() {
        def quizStats = new QuizStats(externalCourseExecution, teacherDashboard)
        quizStats.setNumQuizzes(4)
        quizStatsRepository.save(quizStats)
        return quizStats
    }

    def setUniqueQuizzesSolved() {
        def quizStats = new QuizStats(externalCourseExecution, teacherDashboard)
        quizStats.setUniqueQuizzesSolved(4)
        quizStatsRepository.save(quizStats)
        return quizStats
    }

    def setAverageQuizzesSolved() {
        def quizStats = new QuizStats(externalCourseExecution, teacherDashboard)
        quizStats.setAverageQuizzesSolved(4)
        quizStatsRepository.save(quizStats)
        return quizStats
    }

    def setAll() {
        def quizStats = new QuizStats(externalCourseExecution, teacherDashboard)
        quizStats.setAverageQuizzesSolved(4)
        quizStats.setUniqueQuizzesSolved(4)
        quizStats.setNumQuizzes(4)
        quizStatsRepository.save(quizStats)
        return quizStats
    }

    def "create an empty quizStats"() {

        when: "a new Quiz Stats is created"
        def quizStats = createQuizStatsAndPersist()

        then: "the new quiz stats is persisted"
        quizStatsRepository.count() == 1L
        def result = quizStatsRepository.findAll().get(0)
        result.getId() != 0
        result.getCourseExecution().getId() == externalCourseExecution.getId()
        result.getTeacherDashboard().getId() == teacherDashboard.getId()

        and: "the teacher has a reference for the dashboard"
        teacher.getDashboards().size() == 1
        teacher.getDashboards().contains(teacherDashboard)

        and: "the dashboard has a reference for the QuizStats"
        teacherDashboard.getQuizStats().size() == 1
        teacherDashboard.getQuizStats().contains(result)

    }

    def "set and get attribute of number of Quizzes"(){

        when: "a new Quiz Stats is created"
        quizStats = setNumQuizzes()

        then: "the number of quizzes must be 4"
        def result = quizStatsRepository.findAll().get(0)
        result.getNumQuizzes() == quizStats.getNumQuizzes()

    }

    def "set and get attribute of number of Unique quizzes Solved"(){

        when: "a new Quiz Stats is created"
        quizStats = setUniqueQuizzesSolved()

        then: "the number of unique quizzes solved must be 4"
        def result = quizStatsRepository.findAll().get(0)
        result.getUniqueQuizzesSolved() == quizStats.getUniqueQuizzesSolved()

    }

    def "set and get for averageQuizzesSolved"(){

        when: "a new Quiz Stats is created and averageQuizzesSolved is set to 4"
        quizStats = setAverageQuizzesSolved()

        then: "the number of quizzes must be 4"
        def result = quizStatsRepository.findAll().get(0)
        result.getAverageQuizzesSolved() == quizStats.getAverageQuizzesSolved()
    }

    def "toString test"(){

        when: "a new Quiz Stats is created, and set variables, to see String "
        quizStats = setAll()

        then: "the number of quizzes must be 4"
        def result = quizStatsRepository.findAll().get(0)
        result.toString().equals(quizStats.toString()) == true
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}

