package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuestionStats
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import spock.lang.Unroll
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course

@DataJpaTest
class UpdateQuestionStatsTest extends SpockTest {

    def teacher
    def teacherDashboard
    def questionStats
    def student1
    def student2
    def course

    def createQuestion() {
        def newQuestion = new Question()
        newQuestion.setTitle("Question Title")
        newQuestion.setCourse(externalCourseExecution.getCourse())
        def questionDetails = new MultipleChoiceQuestion()
        newQuestion.setQuestionDetails(questionDetails)
        questionRepository.save(newQuestion)

        def option = new Option()
        option.setContent("Option Content")
        option.setCorrect(true)
        option.setSequence(0)
        option.setQuestionDetails(questionDetails)
        optionRepository.save(option)
        def optionKO = new Option()
        optionKO.setContent("Option Content")
        optionKO.setCorrect(false)
        optionKO.setSequence(1)
        optionKO.setQuestionDetails(questionDetails)
        optionRepository.save(optionKO)

        return newQuestion;
    }

    def setup() {
        createExternalCourseAndExecution()

        teacher = new Teacher(USER_1_NAME, false)
        userRepository.save(teacher)

        teacherDashboard = new TeacherDashboard(externalCourseExecution, teacher)
        teacherDashboardRepository.save(teacherDashboard)

        student1 = new Student(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, false, AuthUser.Type.TECNICO)
        student1.addCourse(externalCourseExecution)
        userRepository.save(student1)
        student2 = new Student(USER_3_NAME, USER_3_USERNAME, USER_3_EMAIL, false, AuthUser.Type.TECNICO)
        student2.addCourse(externalCourseExecution)
        userRepository.save(student2)
    }

    def createQuestionStatsAndPersist() {
        questionStats = new QuestionStats(externalCourseExecution, teacherDashboard)
        questionStatsRepository.save(questionStats)
    }

    def "Update test for the var numAvailable"() {

        given: "5 questions"
        def questionStats = createQuestionStatsAndPersist()

        Question q1 = createQuestion()
        Question q2 = createQuestion()
        Question q3 = createQuestion()
        Question q4 = createQuestion()
        Question q5 = createQuestion()

        QuestionSubmission qs1 = new QuestionSubmission();
        qs1.setQuestion(q1);

        when: "only 4 of them are available"
        q1.setStatus(Question.Status.AVAILABLE)
        q2.setStatus(Question.Status.AVAILABLE)
        q3.setStatus(Question.Status.AVAILABLE)
        q4.setStatus(Question.Status.AVAILABLE)
        q5.setStatus(Question.Status.DISABLED)

        course = externalCourseExecution.getCourse();
        course.addQuestion(q1)
        course.addQuestion(q2)
        course.addQuestion(q3)
        course.addQuestion(q4)
        course.addQuestion(q5)

        externalCourseExecution.setCourse(course)
        courseRepository.save(course)
        externalCourseExecution.addUser(student1)
        externalCourseExecution.addUser(student2)


        then: "you have only 4 questions available"
        questionStats.update()

        def result = questionStatsRepository.findAll().get(0)

        result.getNumAvailable() == 4

    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}