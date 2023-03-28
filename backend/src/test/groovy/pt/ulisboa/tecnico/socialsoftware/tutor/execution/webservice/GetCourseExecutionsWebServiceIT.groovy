package pt.ulisboa.tecnico.socialsoftware.tutor.execution.webservice

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTestIT
import pt.ulisboa.tecnico.socialsoftware.tutor.demo.DemoUtils
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetCourseExecutionsWebServiceIT extends SpockTestIT {
    @LocalServerPort
    private int port

    def response

    def setup() {
        given:
        deleteAll()
        and:
        restClient = new RESTClient("http://localhost:" + port)
        and: 'an external course execution'
        externalCourse = new Course("Software Engineering", Course.Type.EXTERNAL)
        courseRepository.save(externalCourse)
        externalCourseExecution = new CourseExecution(externalCourse, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(externalCourseExecution)
    }

    def "get the course executions that admin user is allowed to see"() {
        given: 'a demon admin'
        demoAdminLogin()

        when: 'the web service is invoked'
        response = restClient.get(
                path: '/executions/',
                requestContentType: 'application/json'
        )

        then: 'the request returns OK'
        response.status == 200
        and: 'the response contains one course execution'
        response.data != null
        response.data.size() == 1
        response.data.get(0).name == DemoUtils.COURSE_NAME
        and: 'there are two courses in the database'
        courseRepository.findAll().size() == 2
        courseExecutionRepository.findAll().size() == 2

        cleanup:
        courseRepository.delete(externalCourse)
    }

    def "demo student is not allowed"() {
        given: 'a demon student'
        demoStudentLogin()

        when: 'the web service is invoked'
        response = restClient.get(
                path: '/executions/',
                requestContentType: 'application/json'
        )

        then: "the request returns 403"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN

        cleanup:
        courseRepository.delete(externalCourse)
    }

}