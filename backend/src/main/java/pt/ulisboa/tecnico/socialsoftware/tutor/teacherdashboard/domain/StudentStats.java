package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;

import javax.persistence.*;


@Entity
public class StudentStats implements DomainEntity{

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int numStudents;
    private int numMore75CorrectQuestions;
    private int numAtLeast3Quizzes;

    @OneToOne
    private CourseExecution courseExecution;

    @ManyToOne
    private TeacherDashboard teacherDashboard;

    public StudentStats() {
    }

    public StudentStats(CourseExecution courseExecution) {
        setCourseExecution(courseExecution);
    }

    public Integer getId() {
        return id;
    }

    public CourseExecution getCourseExecution() {
        return courseExecution;
    }

    public void setCourseExecution(CourseExecution courseExecution) {
        this.courseExecution = courseExecution;
    }

    public int getnumStudents(){ return numStudents; }
    
    public void setnumStudents(int num){
      this.numStudents = num;
    }

    public int getnumMore75CorrectQuestions(){ return numMore75CorrectQuestions; }
    
    public void setnumMore75CorrectQuestions(int num){
      this.numMore75CorrectQuestions = num;
    }
    
    public int getNumAtLeast3Quizzes(){
        return numAtLeast3Quizzes;
    }

    public void setNumAtLeast3Quizzes(int num){
        this.numAtLeast3Quizzes = num;
    }

    public void update() {

        int numIterator;
        int numIterator2;
        int totalQuestions;
        int correctQuestions;

        Set<Student> students = new Set<Student>();
        students = this.getCourseExecution().getStudents()

        
        this.setnumStudents(students.size());

        for (Student st : students){
            totalQuestions = st.getQuestionSubmissions().size();
            for (QuestionSubmission qs : st.getQuestionSubmissions()){
                if ((qs.getQuestion().getNumberOfCorrect()) == 1){
                    correctQuestions +=1;
                }
            }
            if (correctQuestions/totalQuestions > 0.75){
                numIterator2 += 1;
            }
            correctQuestions = 0;
        }
        this.setnumMore75CorrectQuestions(numIterator2);

        for (Student st : students){
            if ((st.getQuizAnswers().size() >= 3){
                numIterator += 1;
            }
        }
        this.setnumAtLeast3Quizzes(numIterator);
    }

    public void remove() {
        teacherDashboard.getStudentStats().remove(this);
    }

	public void accept(Visitor visitor) {
        // Only used for XML generation
	}

	@Override
    public String toString() {
        return "StudentStats{" +
                "id=" + id +
                ", numStudents=" + numStudents +
                ", numMore75CorrectQuestions=" + numMore75CorrectQuestions +
                ", numAtLeast3Quizzes=" + numAtLeast3Quizzes +
                '}';
    }

}
