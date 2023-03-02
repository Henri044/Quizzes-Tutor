package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.StudentStats
import spock.lang.Unroll
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest

    
@DataJpaTest
class CreateStudentStatsTest extends SpockTest {

	def teacher
	def teacherDashboard
	def studentStats


    def setup() {
        createExternalCourseAndExecution()

        teacher = new Teacher(USER_1_NAME,false)
        UserRepository.save(teacher)

        teacherDashboard = new TeacherDashboard(externalCourseExecution, teacher)
        TeacherDashboardRepository.save(teacherDashboard)
    }

    def createStudentStats() {
    	def studentStats = new StudentStats(externalCourseExecution, teacherDashboard)
        StudentStatsRepository.save(studentStats)
    }

	def "testing setNumStudents and getNumStudents"() {
		given: "a teacherDashboard and a teacher"

		when: "create a StudentStats and set the number of students"
		def studentsStats = createStudentStats()
		studentsStats.setNumStudents(4)

		then: "check if the number of students has been set correctly"
		def result = studentStatsRepository.findAll().get(0)
		studentsStats.getNumStudents() == result.getNumStudents()
		studentsStats.getNumStudents() == 4

	} 

	def "testing setNumMore75CorrectQuestions and getNumMore75CorrectQuestions"() {
		given : "a teacherDashboard and a teacher"

		when: "create a StudentStats and set the number of students with 75% more questions correctly"
		def studentsStats = createStudentStats()
		studentsStats.setNumMore75CorrectQuestions(4)

		then: "check if the variable numMore75CorrectQuestions has been set correctly"
		def result = studentStatsRepository.findAll().get(0)
		studentsStats.getNumMore75CorrectQuestions() == result.getNumMore75CorrectQuestions()
		studentsStats.getNumMore75CorrectQuestions() == 4
	} 

	def "testing setNumAtLeast3Quizzes and getNumAtLeast3Quizzes"() {
		given : "a teacherDashboard and a teacher"

		when: "create a StudentStats and set the number of students with at least 3 quizzes answended"
		def studentsStats = createStudentStats()
		studentsStats.setNumAtLeast3Quizzes(4)

		then: "check if the variable numAtLeast3Quizzes has been set correctly"
		def result = studentStatsRepository.findAll().get(0)
		studentsStats.getNumAtLeast3Quizzes() == result.getNumAtLeast3Quizzes()
		studentsStats.getNumAtLeast3Quizzes() == 4
	}


	def "testing toString"(){
		given: "a teacherDashboard and a teacher"

		when: "create a StudentStats and set the variables of the students"
		def studentsStats = createStudentStats()
		studentsStats.setNumMore75CorrectQuestions(4)
		studentsStats.setNumAtLeast3Quizzes(5)
		studentsStats.setNumStudents(6)
		def auxString = "StudentStats{" + "id=" + studentsStats.getId() +", numStudents=6, numMore75CorrectQuestions=4, numAtLeast3Quizzes=5}"

		then: "check if toString match the predicted string"
		def result = studentStatsRepository.findAll().get(0)
		studentsStats.toString().equals(result.toString()) == true
		studentsStats.toString().equals(auxString) == true
	}

    def "create an empty studentStats"() {

        given: "a teacherDashboard and a teacher"

        when: "creates and add a studentsStats to a teacherDashboard"
    	def studentsStats = createStudentStats()
    	teacherDashboard.addStudentStats(studentsStats)

    	then: "checks if the studentsStats has a teacherDashboard and viceversa"
    	studentStatsRepository.count() == 1L
    	def result = studentStatsRepository.findAll().get(0)
    	result.getId() != 0
    	result.getCourseExecution().getId() == externalCourseExecution.getId()
    	result.getTeacherDashboard().getId() == teacherDashboard.getId()

    	and:
    	teacherDashboard.getStudentStats().size() == 1
    	teacherDashboard.getStudentStats().contains(result)
    }

	@TestConfiguration
	    static class LocalBeanConfiguration extends BeanConfiguration {}
}