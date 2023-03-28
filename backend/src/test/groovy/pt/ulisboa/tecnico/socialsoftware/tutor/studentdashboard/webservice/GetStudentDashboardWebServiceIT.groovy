package pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.webservice

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTestIT

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetStudentDashboardWebServiceIT extends SpockTestIT {
    @LocalServerPort
    private int port

    def response

    def courseExecutionDto

    def setup() {
        given:
        deleteAll()
        abd:
        restClient = new RESTClient("http://localhost:" + port)
        and:
        courseExecutionDto = courseService.getDemoCourse()
    }

    def "demo student gets a new dashboard"() {
        given:
        demoStudentLogin()

        when:
        response = restClient.get(
                path: '/students/dashboards/executions/' + courseExecutionDto.getCourseExecutionId(),
                requestContentType: 'application/json'
        )

        then:
        response.status == 200
        and:
        response.data.id != null
        and:
        studentDashboardRepository.findAll().size() == 1

        cleanup:
        studentDashboardRepository.deleteAll()
    }

    def "demo student gets existing dashboard"() {
        given:
        demoStudentLogin()
        and:
        def student = authUserService.demoStudentAuth(false).getUser()
        def dashboardDto = studentDashboardService.createStudentDashboard(courseExecutionDto.getCourseExecutionId(), student.getId())

        when:
        response = restClient.get(
                path: '/students/dashboards/executions/' + courseExecutionDto.getCourseExecutionId(),
                requestContentType: 'application/json'
        )

        then:
        response.status == 200
        and:
        response.data.id == dashboardDto.id
        and:
        studentDashboardRepository.findAll().size() == 1

        cleanup:
        studentDashboardRepository.deleteAll()
    }

    def "demo teacher does not have access"() {
        given:
        demoTeacherLogin()

        when:
        response = restClient.get(
                path: '/students/dashboards/executions/' + courseExecutionDto.getCourseExecutionId(),
                requestContentType: 'application/json'
        )

        then:
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN
        and:
        studentDashboardRepository.findAll().size() == 0

        cleanup:
        studentDashboardRepository.deleteAll()
    }

}
