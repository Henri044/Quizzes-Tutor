package pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import spock.lang.Unroll

@DataJpaTest
class GetStudentDashboardTest extends SpockTest {
    def authUserDto
    def courseExecutionDto

    def setup() {
        courseExecutionDto = courseService.getDemoCourse()
        authUserDto = authUserService.demoStudentAuth(false).getUser()
    }

    def "get a dashboard when dashboard does not exist"() {
        when: "getting a dashboard"
        studentDashboardService.getStudentDashboard(courseExecutionDto.getCourseExecutionId(), authUserDto.getId())

        then: "an empty dashboard is created"
        studentDashboardRepository.count() == 1L
        def result = studentDashboardRepository.findAll().get(0)
        result.getId() != 0
        result.getLastCheckFailedAnswers() == null
        result.getLastCheckWeeklyScores() == null
        result.getCourseExecution().getId() == courseExecutionDto.getCourseExecutionId()
        result.getStudent().getId() == authUserDto.getId()

        and: "the student has a reference for the dashboard"
        def student = userRepository.getById(authUserDto.getId())
        student.getDashboards().size() == 1
        student.getDashboards().contains(result)
    }

    def "get a dashboard and it already exists"() {
        given: "an empty dashboard for the student"
        def dashboardDto = studentDashboardService.createStudentDashboard(courseExecutionDto.getCourseExecutionId(), authUserDto.getId())

        when: "the student's dashboard is retrieved"
        def getDashboardDto = studentDashboardService.getStudentDashboard(courseExecutionDto.getCourseExecutionId(), authUserDto.getId())

        then: "it is the same dashboard"
        dashboardDto.getId() == getDashboardDto.getId()
    }

    def "cannot get a dashboard for a user that does not belong to the course execution"() {
        given: "another course execution"
        createExternalCourseAndExecution()

        when: "get a dashboard"
        studentDashboardService.getStudentDashboard(externalCourseExecution.getId(), authUserDto.getId())

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.STUDENT_NO_COURSE_EXECUTION
    }

    @Unroll
    def "cannot get a dashboard with invalid courseExecutionId=#courseExecutionId"() {
        when:
        studentDashboardService.getStudentDashboard(courseExecutionId, authUserDto.getId())

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.COURSE_EXECUTION_NOT_FOUND

        where:
        courseExecutionId << [0, 100]
    }

    @Unroll
    def "cannot get a dashboard with invalid studentId=#studentId"() {
        when:
        studentDashboardService.getStudentDashboard(courseExecutionDto.getCourseExecutionId(), studentId)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USER_NOT_FOUND

        where:
        studentId << [0, 100]
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}