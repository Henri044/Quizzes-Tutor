package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.StudentStats
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import spock.lang.Unroll
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler

    
@DataJpaTest
class UpdateStudentStatsTest extends SpockTest {

    def teacher
    def teacherDashboard
    def studentStats
    def numStudents;
    def numMore75CorrectQuestions;
    def numAtLeast3Quizzes;
    def student1
    def student2
    def question1
    def question2
    def question3
    def question4
    def question5
    def questionSubmission1
    def questionSubmission2
    def questionSubmission3
    def questionSubmission4
    def questionSubmission5
    def quiz1
    def quiz2
    def quiz3
    def quiz4
    def quizAnswer1
    def quizAnswer2
    def quizAnswer3
    def quizAnswer4

    def setup() {
        createExternalCourseAndExecution()

        teacher = new Teacher(USER_1_NAME,false)
        userRepository.save(teacher)

        teacherDashboard = new TeacherDashboard(externalCourseExecution, teacher)
        teacherDashboardRepository.save(teacherDashboard)

        student1 = new Student(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL, false, AuthUser.Type.TECNICO)
        student1.addCourse(externalCourseExecution)
        userRepository.save(student1)
        student2 = new Student(USER_3_NAME, USER_3_USERNAME, USER_3_EMAIL, false, AuthUser.Type.TECNICO)
        student2.addCourse(externalCourseExecution)
        userRepository.save(student2)


        QuestionSubmission questionSubmission1 = new QuestionSubmission();
        QuestionSubmission questionSubmission2 = new QuestionSubmission();
        QuestionSubmission questionSubmission3 = new QuestionSubmission();
        QuestionSubmission questionSubmission4 = new QuestionSubmission();
        QuestionSubmission questionSubmission5 = new QuestionSubmission();


        quiz1 = new Quiz()
        quiz1.setKey(1)
        quiz1.setCourseExecution(externalCourseExecution)
        quizRepository.save(quiz1)
        quiz2 = new Quiz()
        quiz2.setKey(2)
        quiz2.setCourseExecution(externalCourseExecution)
        quizRepository.save(quiz2)
        quiz3 = new Quiz()
        quiz3.setKey(1)
        quiz3.setCourseExecution(externalCourseExecution)
        quizRepository.save(quiz3)
        quiz4 = new Quiz()
        quiz4.setKey(2)
        quiz4.setCourseExecution(externalCourseExecution)
        quizRepository.save(quiz4)
        quizAnswer1 = new QuizAnswer()
        quizAnswer1.setCreationDate(DateHandler.now())
        quizAnswer1.setAnswerDate(DateHandler.now())
        quizAnswer1.setStudent(student1)
        quizAnswer1.setStudent(student2)
        quizAnswer1.setQuiz(quiz1)
        quizAnswerRepository.save(quizAnswer1)
        quizAnswer2 = new QuizAnswer()
        quizAnswer2.setCreationDate(DateHandler.now())
        quizAnswer2.setAnswerDate(DateHandler.now())
        quizAnswer1.setStudent(student1)
        quizAnswer2.setStudent(student2)
        quizAnswer2.setQuiz(quiz2)
        quizAnswerRepository.save(quizAnswer2)
        quizAnswer3 = new QuizAnswer()
        quizAnswer3.setCreationDate(DateHandler.now())
        quizAnswer3.setAnswerDate(DateHandler.now())
        quizAnswer3.setStudent(student1)
        quizAnswer3.setStudent(student2)
        quizAnswer3.setQuiz(quiz3)
        quizAnswerRepository.save(quizAnswer3)
        quizAnswer4 = new QuizAnswer()
        quizAnswer4.setCreationDate(DateHandler.now())
        quizAnswer4.setAnswerDate(DateHandler.now())
        quizAnswer4.setStudent(student1)
        quizAnswer4.setQuiz(quiz4)
        quizAnswerRepository.save(quizAnswer4)



    }

    def createStudentStats() {
        def studentStats = new StudentStats(externalCourseExecution, teacherDashboard)
        StudentStatsRepository.save(studentStats)
    }

    def createQuestion(){
        def newQuestion = new Question()
        newQuestion.setTitle("Question Title")
        newQuestion.setCourse(externalCourseExecution.getCourse())
        questionRepository.save(newQuestion)
        return newQuestion;
    }

    def "invoke the update method and verify it"() {

        given: "a studentStats"
        def studentsStats = createStudentStats()
        teacherDashboard.addStudentStats(studentsStats)

        when: "we change the statistics and we call the update method"

        question1 = createQuestion()
        question2 = createQuestion()
        question3 = createQuestion()
        question4 = createQuestion()
        question5 = createQuestion()

        /*questionSubmission1.setQuestion(question1)
        questionSubmission2.setQuestion(question2)
        questionSubmission3.setQuestion(question3)
        questionSubmission4.setQuestion(question4)
        questionSubmission5.setQuestion(question5)
        questionSubmissionRepository.save(questionSubmission1)
        questionSubmissionRepository.save(questionSubmission2)
        questionSubmissionRepository.save(questionSubmission3)
        questionSubmissionRepository.save(questionSubmission4)
        questionSubmissionRepository.save(questionSubmission5)*/

    
        externalCourseExecution.addUser(student1)
        externalCourseExecution.addUser(student2)
        externalCourseExecution.addQuestionSubmission(questionSubmission1)
        externalCourseExecution.addQuestionSubmission(questionSubmission2)
        externalCourseExecution.addQuestionSubmission(questionSubmission3)
        externalCourseExecution.addQuestionSubmission(questionSubmission4)
        externalCourseExecution.addQuestionSubmission(questionSubmission5)
        externalCourseExecution.addQuiz(quiz1)
        externalCourseExecution.addQuiz(quiz2)
        externalCourseExecution.addQuiz(quiz3)
        externalCourseExecution.addQuiz(quiz4)

        UserRepository.findAll().get(1).addQuizAnswer(quizAnswer1)
        UserRepository.findAll().get(2).addQuizAnswer(quizAnswer1)
        UserRepository.findAll().get(1).addQuizAnswer(quizAnswer2)
        UserRepository.findAll().get(2).addQuizAnswer(quizAnswer2)
        UserRepository.findAll().get(1).addQuizAnswer(quizAnswer3)
        UserRepository.findAll().get(2).addQuizAnswer(quizAnswer3)
        UserRepository.findAll().get(1).addQuizAnswer(quizAnswer4)

        then: "checks if number of students is equals to the update"
        studentsStats.update()
        def result = StudentStatsRepository.findAll().get(0).getNumStudents()
        result == studentsStats.getNumStudents()
        result == 2
    }   

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}  

