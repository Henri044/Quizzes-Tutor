package pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.domain.DifficultQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.domain.RemovedDifficultQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.studentdashboard.domain.StudentDashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.Assessment
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.TopicConjunction
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.DASHBOARD_NOT_FOUND

@DataJpaTest
class UpdateDifficultQuestionsTest extends SpockTest {
    def student
    def dashboard
    def question
    def optionOK
    def optionKO
    def topic
    def quiz
    def quizQuestion
    def now

    def setup() {
        given:
        createExternalCourseAndExecution()
        and:
        student = new Student(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.EXTERNAL)
        student.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        student.addCourse(externalCourseExecution)
        userRepository.save(student)
        and:
        now = DateHandler.now()
        and:
        question = new Question()
        question.setKey(1)
        question.setTitle(QUESTION_1_TITLE)
        question.setContent(QUESTION_1_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        question.setNumberOfAnswers(2)
        question.setNumberOfCorrect(1)
        question.setCourse(externalCourse)
        def questionDetails = new MultipleChoiceQuestion()
        question.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        questionRepository.save(question)
        and:
        optionOK = new Option()
        optionOK.setContent(OPTION_1_CONTENT)
        optionOK.setCorrect(true)
        optionOK.setSequence(0)
        optionOK.setQuestionDetails(questionDetails)
        optionRepository.save(optionOK)
        and:
        optionKO = new Option()
        optionKO.setContent(OPTION_2_CONTENT)
        optionKO.setCorrect(false)
        optionKO.setSequence(1)
        optionKO.setQuestionDetails(questionDetails)
        optionRepository.save(optionKO)
        and:
        topic = new Topic()
        topic.setName(TOPIC_1_NAME)
        topic.setCourse(externalCourse)
        question.addTopic(topic)
        topicRepository.save(topic)
        and:
        def assessment = new Assessment()
        assessment.setTitle(ASSESSMENT_1_TITLE)
        assessment.setStatus(Assessment.Status.AVAILABLE)
        assessment.setSequence(1)
        assessment.setCourseExecution(externalCourseExecution)
        assessmentRepository.save(assessment)
        and:
        def topicConjunction = new TopicConjunction()
        topicConjunction.addTopic(topic)
        topicConjunction.setAssessment(assessment)
        topicConjunctionRepository.save(topicConjunction)
        and:
        quiz = new Quiz()
        quiz.setCourseExecution(externalCourseExecution)
        quiz.setType("PROPOSED")
        quiz.setAvailableDate(now.minusHours(1))
        quiz.setConclusionDate(now)
        quizRepository.save(quiz)
        and:
        quizQuestion = new QuizQuestion()
        quizQuestion.setQuiz(quiz)
        quizQuestion.setQuestion(question)
        quizQuestionRepository.save(quizQuestion)
        and:
        dashboard = new StudentDashboard(externalCourseExecution, student)
        studentDashboardRepository.save(dashboard)
    }

    def "create one difficult question that does not exist"() {
        given:
        def quizAnswer = new QuizAnswer()
        quizAnswer.setAnswerDate(now.minusMinutes(1))
        quizAnswer.setQuiz(quiz)
        quizAnswer.setStudent(student)
        quizAnswer.setCompleted(true)
        quizAnswerRepository.save(quizAnswer)
        and:
        def questionAnswer = new QuestionAnswer()
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswerRepository.save(questionAnswer)
        and:
        difficultQuestionService.updateCourseExecutionWeekDifficultQuestions(externalCourseExecution.id)

        when:
        def result = difficultQuestionService.updateDashboardDifficultQuestions(dashboard.getId())

        then:
        result.size() == 1
        def resultDifficultQuestion = result.get(0)
        resultDifficultQuestion.getPercentage() == 0
        resultDifficultQuestion.getQuestionDto().getId() == question.getId()
        and:
        difficultQuestionRepository.count() == 1L
        and:
        def difficultQuestion = difficultQuestionRepository.findAll().get(0)
        difficultQuestion.getCourseExecution() == externalCourseExecution
        difficultQuestion.getQuestion() == question
        difficultQuestion.getPercentage() == 0
        and:
        def resultDashboard = studentDashboardRepository.getById(dashboard.id)
        resultDashboard.getRemovedDifficultQuestions().size() == 0
    }

    def "does not create difficult question for a question without answers"() {
        given:
        difficultQuestionService.updateCourseExecutionWeekDifficultQuestions(externalCourseExecution.id)

        when:
        def result = difficultQuestionService.updateDashboardDifficultQuestions(dashboard.getId())

        then:
        result.size() == 0
        and:
        difficultQuestionRepository.count() == 0L
        and:
        def resultDashboard = studentDashboardRepository.getById(dashboard.id)
        resultDashboard.getRemovedDifficultQuestions().size() == 0
    }

    def "does not create difficult question if question is not in available assessment"() {
        given:
        question.getTopics().remove(topic)
        topic.getQuestions().remove(question)
        and:
        difficultQuestionService.updateCourseExecutionWeekDifficultQuestions(externalCourseExecution.id)

        when:
        def result = difficultQuestionService.updateDashboardDifficultQuestions(dashboard.getId())

        then:
        result.size() == 0
        and:
        difficultQuestionRepository.count() == 0L
        and:
        def resultDashboard = studentDashboardRepository.getById(dashboard.id)
        resultDashboard.getRemovedDifficultQuestions().size() == 0
    }

    def "delete and create a difficult question that continues to be difficult"() {
        given:
        def difficultQuestion = new DifficultQuestion(externalCourseExecution, question, 24)
        difficultQuestionRepository.save(difficultQuestion)
        and:
        def quizAnswer = new QuizAnswer()
        quizAnswer.setAnswerDate(now.minusMinutes(1))
        quizAnswer.setQuiz(quiz)
        quizAnswer.setStudent(student)
        quizAnswer.setCompleted(true)
        quizAnswerRepository.save(quizAnswer)
        and:
        def questionAnswer = new QuestionAnswer()
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswerRepository.save(questionAnswer)
        and:
        difficultQuestionService.updateCourseExecutionWeekDifficultQuestions(externalCourseExecution.id)

        when:
        def result = difficultQuestionService.updateDashboardDifficultQuestions(dashboard.getId())

        then:
        result.size() == 1
        and:
        difficultQuestionRepository.count() == 1L
        def resultDifficultQuestion = difficultQuestionRepository.findAll().get(0)
        resultDifficultQuestion.getId() != difficultQuestion.getId()
        resultDifficultQuestion.getQuestion() == question
        resultDifficultQuestion.getPercentage() == 0
        and:
        def resultDashboard = studentDashboardRepository.getById(dashboard.id)
        resultDashboard.getRemovedDifficultQuestions().size() == 0
    }

    def "delete difficult question that is not difficult anymore"() {
        given:
        def difficultQuestion = new DifficultQuestion(externalCourseExecution, question, 24)
        difficultQuestionRepository.save(difficultQuestion)
        and:
        difficultQuestionService.updateCourseExecutionWeekDifficultQuestions(externalCourseExecution.id)

        when:
        def result = difficultQuestionService.updateDashboardDifficultQuestions(dashboard.getId())

        then:
        result.size() == 0
        and:
        difficultQuestionRepository.count() == 0L
        and:
        def resultDashboard = studentDashboardRepository.getById(dashboard.id)
        resultDashboard.getRemovedDifficultQuestions().size() == 0
    }

    @Unroll
    def "does not delete removed difficult question that was removed in less than #daysAgo days ago"() {
        given:
        def difficultQuestion = new RemovedDifficultQuestion(question.id, now.minusDays(daysAgo))
        dashboard.addRemovedDifficultQuestion(difficultQuestion)
        and:
        difficultQuestionService.updateCourseExecutionWeekDifficultQuestions(externalCourseExecution.id)

        when:
        def result = difficultQuestionService.updateDashboardDifficultQuestions(dashboard.getId())

        then:
        result.size() == 0
        and:
        difficultQuestionRepository.count() == 0
        and:
        def dashboardResult = studentDashboardRepository.getById(dashboard.id)
        dashboardResult.getRemovedDifficultQuestions().size() == 1

        where:
        daysAgo << [0, 5, 6]
    }

    def "does not delete removed difficult question that was removed in less than 4 days ago even if it continues to be difficulty"() {
        given:
        def difficultQuestion = new RemovedDifficultQuestion(question.id, now.minusDays(4))
        dashboard.addRemovedDifficultQuestion(difficultQuestion)
        and:
        def quizAnswer = new QuizAnswer()
        quizAnswer.setAnswerDate(now.minusMinutes(1))
        quizAnswer.setQuiz(quiz)
        quizAnswer.setStudent(student)
        quizAnswer.setCompleted(true)
        quizAnswerRepository.save(quizAnswer)
        and:
        def questionAnswer = new QuestionAnswer()
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswerRepository.save(questionAnswer)
        and:
        difficultQuestionService.updateCourseExecutionWeekDifficultQuestions(externalCourseExecution.id)

        when:
        def result = difficultQuestionService.updateDashboardDifficultQuestions(dashboard.getId())

        then:
        result.size() == 0
        and:
        difficultQuestionRepository.count() == 1
        and:
        def dashboardResult = studentDashboardRepository.getById(dashboard.getId())
        dashboardResult.getRemovedDifficultQuestions().size() == 1
        def list = new ArrayList<>(dashboardResult.getRemovedDifficultQuestions())
        list.get(0).getQuestionId() == question.getId()
        list.get(0).getRemovedDate() == now.minusDays(4)
    }

    @Unroll
    def "delete removed difficult question that was removed in more than #daysAgo days ago"() {
        given:
        def removedDifficultQuestion = new RemovedDifficultQuestion(question.id, now.minusDays(daysAgo))
        dashboard.addRemovedDifficultQuestion(removedDifficultQuestion)
        and:
        difficultQuestionService.updateCourseExecutionWeekDifficultQuestions(externalCourseExecution.id)

        when:
        def result = difficultQuestionService.updateDashboardDifficultQuestions(dashboard.getId())

        then:
        result.size() == 0
        and:
        difficultQuestionRepository.count() == 0L
        and:
        def resultDashboard = studentDashboardRepository.getById(dashboard.id)
        resultDashboard.getRemovedDifficultQuestions().size() == 0

        where:
        daysAgo << [7, 15]
    }

    @Unroll
    def "create difficult question that continues to be difficult of a removed difficult question that was removed in more than 15 days ago"() {
        given:
        def removedDifficultQuestion = new RemovedDifficultQuestion(question.id, now.minusDays(15))
        dashboard.addRemovedDifficultQuestion(removedDifficultQuestion)
        and:
        def quizAnswer = new QuizAnswer()
        quizAnswer.setAnswerDate(now.minusMinutes(1))
        quizAnswer.setQuiz(quiz)
        quizAnswer.setStudent(student)
        quizAnswer.setCompleted(true)
        quizAnswerRepository.save(quizAnswer)
        and:
        def questionAnswer = new QuestionAnswer()
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswerRepository.save(questionAnswer)
        and:
        difficultQuestionService.updateCourseExecutionWeekDifficultQuestions(externalCourseExecution.id)

        when:
        def result = difficultQuestionService.updateDashboardDifficultQuestions(dashboard.getId())

        then:
        result.size() == 1
        and:
        difficultQuestionRepository.count() == 1L
        def resultDifficultQuestion = difficultQuestionRepository.findAll().get(0)
        resultDifficultQuestion.getQuestion() == question
        resultDifficultQuestion.getPercentage() == 0
        and:
        def resultDashboard = studentDashboardRepository.getById(dashboard.id)
        resultDashboard.getRemovedDifficultQuestions().size() == 0
    }

    @Unroll
    def "question is correctly computed as not difficulty with #numberOfIncorrect incorrect"() {
        given:
        answerQuiz(true)
        (1..numberOfIncorrect).each {
            answerQuiz(false)
        }
        and:
        difficultQuestionService.updateCourseExecutionWeekDifficultQuestions(externalCourseExecution.id)

        when:
        def result = difficultQuestionService.updateDashboardDifficultQuestions(dashboard.getId())

        then:
        result.size() == 0
        and:
        difficultQuestionRepository.count() == 0L
        and:
        def resultDashboard = studentDashboardRepository.getById(dashboard.id)
        resultDashboard.getRemovedDifficultQuestions().size() == 0

        where:
        numberOfIncorrect << [0, 1, 3]
    }

    @Unroll
    def "question is correctly computed as difficult"() {
        given:
        answerQuiz(false)
        answerQuiz(false)
        answerQuiz(false)
        answerQuiz(false)
        answerQuiz(true)
        and:
        difficultQuestionService.updateCourseExecutionWeekDifficultQuestions(externalCourseExecution.id)

        when:
        def result = difficultQuestionService.updateDashboardDifficultQuestions(dashboard.getId())

        then:
        result.size() == 1
        and:
        difficultQuestionRepository.count() == 1L
        def resultDifficultQuestion = difficultQuestionRepository.findAll().get(0)
        resultDifficultQuestion.getQuestion() == question
        resultDifficultQuestion.getPercentage() == 20
        and:
        def resultDashboard = studentDashboardRepository.getById(dashboard.id)
        resultDashboard.getRemovedDifficultQuestions().size() == 0
    }

    @Unroll
    def "cannot update difficult questions with invalid dashboardId=#dashboardId"() {
        given:
        difficultQuestionService.updateCourseExecutionWeekDifficultQuestions(externalCourseExecution.id)

        when:
        difficultQuestionService.updateDashboardDifficultQuestions(dashboardId)

        then:
        def exception = thrown(TutorException)
        exception.getErrorMessage() == DASHBOARD_NOT_FOUND
        difficultQuestionRepository.count() == 0L

        where:
        dashboardId << [0, 100]
    }

    def answerQuiz(correct, date = DateHandler.now()) {
        def quiz = new Quiz()
        quiz.setCourseExecution(externalCourseExecution)
        quiz.setType("PROPOSED")
        quiz.setAvailableDate(now.minusHours(1))
        quiz.setConclusionDate(now)
        quizRepository.save(quiz)

        def quizQuestion = new QuizQuestion()
        quizQuestion.setQuiz(quiz)
        quizQuestion.setQuestion(question)
        quizQuestionRepository.save(quizQuestion)

        def quizAnswer = new QuizAnswer()
        quizAnswer.setCompleted(true)
        quizAnswer.setCreationDate(date)
        quizAnswer.setAnswerDate(date)
        quizAnswer.setCompleted(true)
        quizAnswer.setStudent(student)
        quizAnswer.setQuiz(quiz)
        quizAnswerRepository.save(quizAnswer)

        def questionAnswer = new QuestionAnswer()
        questionAnswer.setTimeTaken(1)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswerRepository.save(questionAnswer)

        def answerDetails
        if (correct) answerDetails = new MultipleChoiceAnswer(questionAnswer, optionOK)
        else if (!correct) answerDetails = new MultipleChoiceAnswer(questionAnswer, optionKO)
        else {
            questionAnswerRepository.save(questionAnswer)
            return questionAnswer
        }
        questionAnswer.setAnswerDetails(answerDetails)
        answerDetailsRepository.save(answerDetails)
        return questionAnswer
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
