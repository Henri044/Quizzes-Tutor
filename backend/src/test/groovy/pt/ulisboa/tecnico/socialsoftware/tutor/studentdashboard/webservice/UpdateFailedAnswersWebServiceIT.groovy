package pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.webservice

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.domain.StudentDashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.service.FailedAnswersSpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UpdateFailedAnswersWebServiceIT extends FailedAnswersSpockTest {
    @LocalServerPort
    private int port

    def response
    def quiz
    def quizQuestion

    def setup() {
        given:
        deleteAll()
        and:
        restClient = new RESTClient("http://localhost:" + port)
        and:
        createExternalCourseAndExecution()
        and:
        student = new Student(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.EXTERNAL)
        student.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        student.addCourse(externalCourseExecution)
        userRepository.save(student)
        and:
        dashboard = new StudentDashboard(externalCourseExecution, student)
        studentDashboardRepository.save(dashboard)
        and:
        def question = createQuestion()
        quiz = createQuiz()
        quizQuestion = createQuizQuestion(quiz, question)
        answerQuiz(true, false, true, quizQuestion, quiz)

    }

    def "student updates failed answers"() {
        given:
        externalUserLogin(USER_1_USERNAME, USER_1_PASSWORD)

        when:
        response = restClient.put(
                path: '/students/dashboards/' + dashboard.getId() + '/failedanswers',
                requestContentType: 'application/json'
        )

        then:
        response != null
        response.status == 200
        and:
        response.data.size() == 1
        def resultFailedAnswer = response.data.get(0)
        resultFailedAnswer.id != 0
        DateHandler.toLocalDateTime(resultFailedAnswer.collected).isAfter(DateHandler.now().minusMinutes(1))
        resultFailedAnswer.answered
        and:
        failedAnswerRepository.findAll().size() == 1
    }

    def "teacher cant update student's failed answers"() {
        given:
        demoTeacherLogin()

        when:
        response = restClient.put(
                path: '/students/dashboards/' + dashboard.getId() + '/failedanswers',
                requestContentType: 'application/json'
        )

        then:
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN
    }

    def "student cant update another students failed answers"() {
        given:
        def newStudent = new Student(USER_2_NAME, USER_2_USERNAME, USER_2_PASSWORD, false, AuthUser.Type.EXTERNAL)
        newStudent.authUser.setPassword(passwordEncoder.encode(USER_2_PASSWORD))
        userRepository.save(newStudent)
        externalUserLogin(USER_2_USERNAME, USER_2_PASSWORD)

        when:
        response = restClient.put(
                path: '/students/dashboards/' + dashboard.getId() + '/failedanswers',
                requestContentType: 'application/json'
        )

        then:
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN
    }

    def cleanup() {
        studentDashboardRepository.deleteAll()
        userRepository.deleteAll()
        courseRepository.deleteAll()
    }
}