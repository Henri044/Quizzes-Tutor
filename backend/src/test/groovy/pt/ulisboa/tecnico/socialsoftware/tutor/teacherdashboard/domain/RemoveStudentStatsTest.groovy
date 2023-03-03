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
class RemoveStudentStatsTest extends SpockTest {

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


    def setAllRemoved() {
        def studentStats = new StudentStats(externalCourseExecution, teacherDashboard)
		studentStats.setNumMore75CorrectQuestions(4)
		studentStats.setNumAtLeast3Quizzes(5)
		studentStats.setNumStudents(6)
        studentStats.remove()
		StudentStatsRepository.save(studentStats)
        return studentStats
    }

    def "remove test"(){
        when: "a new StudentStat is created, and set variables, and then removed"
        studentStats = setAllRemoved()

        then: "the Student Stat is removed"
        def result = teacherDashboardRepository.findAll().get(0)
        teacherDashboard.getStudentStats() == result.getStudentStats()
        teacherDashboard.getStudentStats() == new HashSet<StudentStats>()
		teacherDashboard.getStudentStats().size() == 0

    }


	@TestConfiguration
	    static class LocalBeanConfiguration extends BeanConfiguration {}
}