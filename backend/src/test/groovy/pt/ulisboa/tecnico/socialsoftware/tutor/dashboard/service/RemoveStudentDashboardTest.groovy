package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain.StudentDashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import spock.lang.Unroll


@DataJpaTest
class RemoveStudentDashboardTest extends SpockTest {

    def student

    def setup() {
        createExternalCourseAndExecution()

        student = new Student(USER_1_NAME, false)
        userRepository.save(student)
    }

    def createDashboard() {
        def dashboard = new StudentDashboard(externalCourseExecution, student)
        studentDashboardRepository.save(dashboard)
        return dashboard
    }

    def "remove a dashboard"() {
        given: "a dashboard"
        def dashboard = createDashboard()

        when: "the user removes the dashboard"
        dashboardService.removeStudentDashboard(dashboard.getId())

        then: "the dashboard is removed"
        studentDashboardRepository.findAll().size() == 0L
        student.getDashboards().size() == 0
    }

    def "cannot remove a dashboard twice"() {
        given: "a removed dashboard"
        def dashboard = createDashboard()
        dashboardService.removeStudentDashboard(dashboard.getId())

        when: "the dashboard is removed for the second time"
        dashboardService.removeStudentDashboard(dashboard.getId())

        then: "an exception is thrown"        
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.DASHBOARD_NOT_FOUND
    }

    @Unroll
    def "cannot remove a dashboard that doesn't exist with the dashboardId=#dashboardId"() {
        when: "an incorrect dashboard id is removed"
        dashboardService.removeStudentDashboard(dashboardId)

        then: "an exception is thrown"        
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.DASHBOARD_NOT_FOUND

        where:
        dashboardId << [null, 10, -1]
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}