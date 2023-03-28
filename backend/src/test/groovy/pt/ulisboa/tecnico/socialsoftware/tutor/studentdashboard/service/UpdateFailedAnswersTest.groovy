package pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.domain.StudentDashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import spock.lang.Unroll

@DataJpaTest
class UpdateFailedAnswersTest extends FailedAnswersSpockTest {
    def question
    def quiz
    def quizQuestion

    def setup() {
        createExternalCourseAndExecution()

        student = new Student(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.TECNICO)
        student.addCourse(externalCourseExecution)
        userRepository.save(student)

        dashboard = new StudentDashboard(externalCourseExecution, student)
        studentDashboardRepository.save(dashboard)

        question = createQuestion()
        quiz = createQuiz()
        quizQuestion = createQuizQuestion(quiz, question)
    }

    @Unroll
    def "create failed answer answered=#answered"() {
        given:
        def questionAnswer = answerQuiz(answered, false, true, quizQuestion, quiz)

        when:
        def result = failedAnswerService.updateFailedAnswers(dashboard.getId())

        then:
        result.size() == 1
        def resultFailedAnswer = result.get(0)
        resultFailedAnswer.getId() != 0
        DateHandler.toLocalDateTime(resultFailedAnswer.getCollected()).isAfter(DateHandler.now().minusMinutes(1))
        resultFailedAnswer.getAnswered() == answered
        and:
        failedAnswerRepository.count() == 1L
        def failedAnswer = failedAnswerRepository.findAll().get(0)
        failedAnswer.getId() != 0
        failedAnswer.getDashboard().id === dashboard.getId()
        failedAnswer.getQuestionAnswer().getId() == questionAnswer.getId()
        failedAnswer.getCollected().isAfter(DateHandler.now().minusMinutes(1))
        failedAnswer.getAnswered() == answered
        and:
        def dashboard = studentDashboardRepository.getById(dashboard.getId())
        dashboard.getFailedAnswers().contains(failedAnswer)
        dashboard.getLastCheckFailedAnswers().isAfter(DateHandler.now().minusSeconds(1))

        where:
        answered << [true, false]
    }

    @Unroll
    def "does not create failed answer with correct=#correct and completed=#completed"() {
        given:
        answerQuiz(true, correct, completed, quizQuestion, quiz)

        when:
        failedAnswerService.updateFailedAnswers(dashboard.getId())

        then:
        failedAnswerRepository.findAll().size() == 0L

        where:
        completed | correct
        false     | false
        false     | true
        true      | true
    }

    def "does not create failed answer for answer of IN_CLASS quiz where results date is later"() {
        given:
        def inClassQuiz = createQuiz(Quiz.QuizType.IN_CLASS.toString())
        inClassQuiz.setResultsDate(DateHandler.now().plusDays(1))
        def questionAnswer = answerQuiz(true, false, true, quizQuestion, inClassQuiz)

        when:
        def result = failedAnswerService.updateFailedAnswers(dashboard.getId())

        then:
        result.size() == 0
        and:
        failedAnswerRepository.findAll().size() == 0L
        and:
        def dashboard = studentDashboardRepository.getById(dashboard.getId())
        dashboard.getLastCheckFailedAnswers().isEqual(questionAnswer.getQuizAnswer().getCreationDate().minusSeconds(1))
    }

    def "create failed answer for answer of IN_CLASS quiz where results date is now"() {
        given:
        def inClassQuiz = createQuiz(Quiz.QuizType.IN_CLASS.toString())
        inClassQuiz.setResultsDate(DateHandler.now())
        answerQuiz(true, false, true, quizQuestion, inClassQuiz)

        when:
        def result = failedAnswerService.updateFailedAnswers(dashboard.getId())

        then:
        result.size() == 1
        and:
        failedAnswerRepository.findAll().size() == 1L
    }

    def "updates failed answers after last check"() {
        given:
        dashboard.setLastCheckFailedAnswers(DateHandler.now().minusDays(2))
        def questionAnswer1 = answerQuiz(true, false, true, quizQuestion, quiz, DateHandler.now().minusDays(2))
        and:
        def question2 = createQuestion()
        def quiz2 = createQuiz()
        def quizQuestion2 = createQuizQuestion(quiz2, question2)
        def questionAnswer2 = answerQuiz(true, false, true, quizQuestion2, quiz2)

        when:
        def result = failedAnswerService.updateFailedAnswers(dashboard.getId())

        then:
        result.size() == 2
        and:
        failedAnswerRepository.count() == 2L
        def questionAnswers = failedAnswerRepository.findAll()*.questionAnswer
        questionAnswers.contains(questionAnswer1)
        questionAnswers.contains(questionAnswer2)
        and:
        def dashboard = studentDashboardRepository.getById(dashboard.getId())
        dashboard.getFailedAnswers().size() == 2
        dashboard.getLastCheckFailedAnswers().isAfter(DateHandler.now().minusSeconds(1))
    }

    def "does not create failed answers for the same question"() {
        given:
        def questionAnswer = answerQuiz(true, false, true, quizQuestion, quiz, LOCAL_DATE_BEFORE.plusSeconds(20))
        and:
        def newQuiz = createQuiz()
        def newQuizQuestion = createQuizQuestion(newQuiz, question)

        when:
        def result = failedAnswerService.updateFailedAnswers(dashboard.getId())

        then:
        result.size() == 1
        and:
        failedAnswerRepository.count() == 1L
        and:
        def dashboard = studentDashboardRepository.getById(dashboard.getId())
        dashboard.getFailedAnswers().size() == 1
    }

    def "does not create the same failed answer twice"() {
        given:
        def questionAnswer = answerQuiz(true, false, true, quizQuestion, quiz, LOCAL_DATE_BEFORE.plusSeconds(20))
        def failedAnswer = createFailedAnswer(questionAnswer, DateHandler.now())

        when:
        def result = failedAnswerService.updateFailedAnswers(dashboard.getId())

        then:
        result.size() == 1
        and:
        failedAnswerRepository.count() == 1L
        and:
        def dashboard = studentDashboardRepository.getById(dashboard.getId())
        dashboard.getFailedAnswers().size() == 1
        dashboard.getFailedAnswers().contains(failedAnswer)
    }

    def "does not create failed answers if answered other student"() {
        given:
        def otherStudent = new Student(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, false, AuthUser.Type.TECNICO)
        otherStudent.addCourse(externalCourseExecution)
        userRepository.save(otherStudent)
        and:
        dashboard.setStudent(otherStudent)
        and:
        answerQuiz(true, false, true, quizQuestion, quiz)

        when:
        def result = failedAnswerService.updateFailedAnswers(dashboard.getId())

        then:
        result.size() == 0
        and:
        failedAnswerRepository.findAll().size() == 0L
        and: "the student dashboard's failed answers is empty"
        def dashboard = studentDashboardRepository.findById(dashboard.getId()).get()
        dashboard.getStudent().getId() === otherStudent.getId()
        dashboard.getCourseExecution().getId() === externalCourseExecution.getId()
        dashboard.getFailedAnswers().findAll().size() == 0L
        dashboard.getLastCheckFailedAnswers().isAfter(DateHandler.now().minusSeconds(1))
    }

    def "does not create failed answers if quiz does not belong to the course execution"() {
        given:
        def otherExternalCourseExecution = new CourseExecution(externalCourse, COURSE_1_ACRONYM, COURSE_2_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TODAY)
        courseExecutionRepository.save(otherExternalCourseExecution)
        and:
        dashboard.setCourseExecution(otherExternalCourseExecution)
        and:
        answerQuiz(true, false, true, quizQuestion, quiz)

        when:
        failedAnswerService.updateFailedAnswers(dashboard.getId())

        then: "no failed answer is updated in the database"
        failedAnswerRepository.findAll().size() == 0L
        and: "the student dashboard's failed answers is empty"
        def dashboard = studentDashboardRepository.findById(dashboard.getId()).get()
        dashboard.getStudent().getId() === student.getId()
        dashboard.getCourseExecution().getId() === otherExternalCourseExecution.getId()
        dashboard.getFailedAnswers().findAll().size() == 0L
        dashboard.getLastCheckFailedAnswers().isAfter(DateHandler.now().minusSeconds(1))
    }

    @Unroll
    def "cannot update failed answers with dashboardId=#dashboardId"() {
        when:
        failedAnswerService.updateFailedAnswers(dashboardId)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.DASHBOARD_NOT_FOUND
        and:
        failedAnswerRepository.count() == 0L

        where:
        dashboardId << [0, 100]
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
