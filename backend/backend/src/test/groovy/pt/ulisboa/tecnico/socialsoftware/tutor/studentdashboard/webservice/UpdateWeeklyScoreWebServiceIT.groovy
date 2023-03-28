package pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.webservice

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTestIT

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UpdateWeeklyScoreWebServiceIT extends SpockTestIT {
    @LocalServerPort
    private int port

    def response

    def authUserDto
    def courseExecutionDto
    def dashboardDto

    def setup() {
        given:
        deleteAll()
        and:
        restClient = new RESTClient("http://localhost:" + port)
        and:
        courseExecutionDto = courseService.getDemoCourse()
        authUserDto = authUserService.demoStudentAuth(false).getUser()
        dashboardDto = studentDashboardService.getStudentDashboard(courseExecutionDto.getCourseExecutionId(), authUserDto.getId())
    }

    def "demo student gets its weekly scores"() {
        given:
        demoStudentLogin()

        when:
        response = restClient.put(
                path: '/students/dashboards/' + dashboardDto.getId() + '/weeklyscores',
                requestContentType: 'application/json'
        )

        then:
        response.status == 200
        and:
        response.data.size() == 1
        def resultWeeklyScore = response.data.get(0)
        resultWeeklyScore.questionsAnswered == 0
        resultWeeklyScore.questionsUniquelyAnswered == 0
        resultWeeklyScore.percentageCorrect == 0
        resultWeeklyScore.improvedCorrectAnswers == 0
        and:
        weeklyScoreRepository.findAll().size() == 1

        cleanup:
        weeklyScoreRepository.deleteAll()
        studentDashboardRepository.deleteAll()
    }

    def "demo teacher does not have access"() {
        given:
        demoTeacherLogin()

        when:
        response = restClient.put(
                path: '/students/dashboards/' + dashboardDto.getId() + '/weeklyscores',
                requestContentType: 'application/json'
        )

        then:
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN
        and:
        weeklyScoreRepository.findAll().size() == 0

        cleanup:
        weeklyScoreRepository.deleteAll()
        studentDashboardRepository.deleteAll()
    }

    def "student cant update another students failed answers"() {
        given:
        demoStudentLogin(true)

        when:
        response = restClient.put(
                path: '/students/dashboards/' + dashboardDto.getId() + '/weeklyscores',
                requestContentType: 'application/json'
        )

        then:
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN
        and:
        weeklyScoreRepository.findAll().size() == 0

        cleanup:
        weeklyScoreRepository.deleteAll()
        studentDashboardRepository.deleteAll()
    }

}
