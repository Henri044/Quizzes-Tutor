package pt.ulisboa.tecnico.socialsoftware.tutor.auth.webservice

import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTestIT
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetDemoAuthWebServiceIT extends SpockTestIT {

    @LocalServerPort
    private int port

    def setup() {
        deleteAll()

        restClient = new RESTClient("http://localhost:" + port)
    }

    def "demo student login"() {
        when:
        def response = restClient.get(
                path: '/auth/demo/student',
                query: [
                        createNew: false,
                ],
        )

        then: "check response status"
        response.status == 200
        response.data.token != ""
        response.data.user.name == DEMO_STUDENT_NAME
        response.data.user.role == User.Role.STUDENT.toString()
    }

    def "demo admin login"() {
        when:
        def response = restClient.get(
                path: '/auth/demo/admin',
        )

        then: "check response status"
        response.status == 200
        response.data.token != ""
        response.data.user.name == DEMO_ADMIN_NAME
        response.data.user.role == User.Role.DEMO_ADMIN.toString()
    }

    def "demo teacher login"() {
        when:
        def response = restClient.get(
                path: '/auth/demo/teacher',
        )

        then: "check response status"
        response.status == 200
        response.data.token != ""
        response.data.user.name == DEMO_TEACHER_NAME
        response.data.user.role == User.Role.TEACHER.toString()
    }

}
