package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import spock.lang.Unroll

@DataJpaTest
class GetTeacherDashboardTest extends SpockTest {
    def authUserDto
    def courseExecutionDto

    def setup() {
        courseExecutionDto = courseService.getDemoCourse()
        authUserDto = authUserService.demoTeacherAuth().getUser()
    }

    def "get a dashboard when dashboard does not exist"() {
        when: "getting a dashboard"
        dashboardService.getTeacherDashboard(courseExecutionDto.getCourseExecutionId(), authUserDto.getId())

        then: "an empty dashboard is created"
        teacherDashboardRepository.count() == 1L
        def result = teacherDashboardRepository.findAll().get(0)
        result.getId() != 0
        result.getCourseExecution().getId() == courseExecutionDto.getCourseExecutionId()
        result.getTeacher().getId() == authUserDto.getId()

        and: "the teacher has a reference for the dashboard"
        def teacher = userRepository.getById(authUserDto.getId())
        teacher.getDashboards().size() == 1
        teacher.getDashboards().contains(result)
    }

    def "get a dashboard and it already exists"() {
        given: "an empty dashboard for the student"
        def dashboardDto = dashboardService.createTeacherDashboard(courseExecutionDto.getCourseExecutionId(), authUserDto.getId())

        when: "a second dashboard is created"
        def getDashboardDto = dashboardService.getTeacherDashboard(courseExecutionDto.getCourseExecutionId(), authUserDto.getId())

        then: "it is the same dashboard"
        dashboardDto.getId() == getDashboardDto.getId()
    }

    def "cannot get a dashboard for a user that does not belong to the course execution"() {
        given: "another course execution"
        createExternalCourseAndExecution()

        when: "get a dashboard"
        dashboardService.getTeacherDashboard(externalCourseExecution.getId(), authUserDto.getId())

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.TEACHER_NO_COURSE_EXECUTION
    }

    @Unroll
    def "cannot get a dashboard with invalid courseExecutionId=#courseExecutionId"() {
        when:
        dashboardService.getTeacherDashboard(courseExecutionId, authUserDto.getId())

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.COURSE_EXECUTION_NOT_FOUND

        where:
        courseExecutionId << [0, 100]
    }

    @Unroll
    def "cannot get a dashboard with invalid teacherId=#teacherId"() {
        when:
        dashboardService.getTeacherDashboard(courseExecutionDto.getCourseExecutionId(), teacherId)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.USER_NOT_FOUND

        where:
        teacherId << [0, 100]
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
