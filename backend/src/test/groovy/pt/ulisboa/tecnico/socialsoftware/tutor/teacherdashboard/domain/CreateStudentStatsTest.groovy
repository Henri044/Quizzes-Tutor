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

        teacher = new Teacher("JoÃ£o",false)
        UserRepository.save(teacher)

        teacherDashboard = new TeacherDashboard(externalCourseExecution, teacher)
        TeacherDashboardRepository.save(teacherDashboard)
    }

    def createStudentStats() {
    	def studentStats = new StudentStats(externalCourseExecution, teacherDashboard)
        StudentStatsRepository.save(studentStats)
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
    	//teacherDashboard.getStudentStats() != null
    	teacherDashboard.getStudentStats().contains(result)
    }

	@TestConfiguration
	    static class LocalBeanConfiguration extends BeanConfiguration {}
}