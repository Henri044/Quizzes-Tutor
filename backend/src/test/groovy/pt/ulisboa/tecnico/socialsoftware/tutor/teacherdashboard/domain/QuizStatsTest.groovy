package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuizStats
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import spock.lang.Unroll

@DataJpaTest
class QuizStatsTest extends SpockTest {
    def teacher
    def teacherDashboard
    def quizStats
    def student1
    def student2
    def quiz1
    def quiz2
    def quizAnswer1
    def quizAnswer2

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
    def setAllRemoved() {
        def quizStats = new QuizStats(externalCourseExecution, teacherDashboard)
        quizStats.setAverageQuizzesSolved(4)
        quizStats.setUniqueQuizzesSolved(4)
        quizStats.setNumQuizzes(5)
        quizStats.remove()
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
        result.getNumQuizzes() == 4

    }

    def "set and get attribute of number of Unique quizzes Solved"(){

        when: "a new Quiz Stats is created"
        quizStats = setUniqueQuizzesSolved()

        then: "the number of unique quizzes solved must be 4"
        def result = quizStatsRepository.findAll().get(0)
        result.getUniqueQuizzesSolved() == quizStats.getUniqueQuizzesSolved()
        result.getUniqueQuizzesSolved() == 4

    }

    def "set and get for averageQuizzesSolved"(){

        when: "a new Quiz Stats is created and averageQuizzesSolved is set to 4"
        quizStats = setAverageQuizzesSolved()

        then: "the number of quizzes must be 4"
        def result = quizStatsRepository.findAll().get(0)
        result.getAverageQuizzesSolved() == quizStats.getAverageQuizzesSolved()
        result.getAverageQuizzesSolved() == 4
    }

    def "toString test"(){

        when: "a new Quiz Stats is created, and set variables, to see String "
        quizStats = setAll()

        then: "the number of quizzes must be 4"
        def result = quizStatsRepository.findAll().get(0)
        def auxString = "QuizStats{" + "id=" + quizStats.getId() +", numQuizzes=4, uniqueQuizzesSolved=4, averaqeQuizzesSolved=4.0}"

        quizStats.toString().equals(auxString) == true
        result.toString().equals(quizStats.toString()) == true
    }
    def "remove test"(){
        when: "a new Quiz Stats is created, and set variables, and then removed"
        quizStats = setAllRemoved()

        then: "the quizz stat is removed"
        def result = teacherDashboardRepository.findAll().get(0)
        teacherDashboard.getQuizStats() == result.getQuizStats()
        teacherDashboard.getQuizStats() == new HashSet<QuizStats>()
    }

    def "update test"(){
        given: "all classes needed and a new QuizStats"
        def quizStats = createQuizStatsAndPersist()

        student1 = new Student(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, false, AuthUser.Type.TECNICO)
        student1.addCourse(externalCourseExecution)
        userRepository.save(student1)
        student2 = new Student(USER_3_NAME, USER_3_USERNAME, USER_3_EMAIL, false, AuthUser.Type.TECNICO)
        student2.addCourse(externalCourseExecution)
        userRepository.save(student2)

        quiz1 = new Quiz()
        quiz1.setKey(1)
        quiz1.setCourseExecution(externalCourseExecution)
        quizRepository.save(quiz1)
        quiz2 = new Quiz()
        quiz2.setKey(2)
        quiz2.setCourseExecution(externalCourseExecution)
        quizRepository.save(quiz2)

        quizAnswer1 = new QuizAnswer()
        quizAnswer1.setCreationDate(DateHandler.now())
        quizAnswer1.setAnswerDate(DateHandler.now())
        quizAnswer1.setStudent(student1)
        quizAnswer1.setQuiz(quiz1)
        quizAnswerRepository.save(quizAnswer1)
        quizAnswer2 = new QuizAnswer()
        quizAnswer2.setCreationDate(DateHandler.now())
        quizAnswer2.setAnswerDate(DateHandler.now())
        quizAnswer2.setStudent(student2)
        quizAnswer2.setQuiz(quiz2)
        quizAnswerRepository.save(quizAnswer1)


        when: "we change the statistics and call the update method"
        externalCourseExecution.addUser(student1)
        externalCourseExecution.addUser(student2)
        externalCourseExecution.addQuiz(quiz1)
        externalCourseExecution.addQuiz(quiz2)

        UserRepository.findAll().get(1).addQuizAnswer(quizAnswer1)
        UserRepository.findAll().get(2).addQuizAnswer(quizAnswer2)



        then: "the stats should be stored in QuizStats"
        quizStats.update()
        def result = quizStatsRepository.findAll().get(0)
        result.getNumQuizzes() == quizStats.getNumQuizzes()
        result.getNumQuizzes() == 2
        result.getUniqueQuizzesSolved() == quizStats.getUniqueQuizzesSolved()
        result.getUniqueQuizzesSolved() == 2
        result.getAverageQuizzesSolved() == quizStats.getAverageQuizzesSolved()
        result.getAverageQuizzesSolved() == 1
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}

