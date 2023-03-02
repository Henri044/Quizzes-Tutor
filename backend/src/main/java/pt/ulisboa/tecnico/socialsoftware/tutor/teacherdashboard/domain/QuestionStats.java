package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsubmission.domain.QuestionSubmission;

import javax.persistence.*;
import java.util.*;

@Entity
public class QuestionStats implements DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Attributes
    private int numAvailable;
    private int answeredQuestionUnique;
    private float averageQuestionsAnswered;
    @OneToOne
    private CourseExecution courseExecution;
    @ManyToOne
    private TeacherDashboard teacherDashboard;

    public QuestionStats(){
    }

    public QuestionStats(CourseExecution courseExecution, TeacherDashboard teacherDashboard){
        setCourseExecution(courseExecution);
        setTeacherDashboard(teacherDashboard);
    }

    // Methods
    public void accept(Visitor visitor) {
        // Only used for XML generation
    }

    public Integer getId() {
        return id;
    }

    public CourseExecution getCourseExecution() {
        return courseExecution;
    }

    public void setCourseExecution(CourseExecution courseExecution){
        this.courseExecution = courseExecution;
    }

    public TeacherDashboard getTeacherDashboard() { return teacherDashboard; }

    public void setTeacherDashboard(TeacherDashboard teacherDashboard) {
        this.teacherDashboard = teacherDashboard;
    }

    public int getNumAvailable(){
        return numAvailable;
    }

   public void setNumAvailable(int numAvailable){
        this.numAvailable = numAvailable;
   }

    public int getAnsweredQuestionUnique() {
        return answeredQuestionUnique;
    }

    public void setAnsweredQuestionUnique(int answeredQuestionUnique) {
        this.answeredQuestionUnique = answeredQuestionUnique;
    }

    public float getAverageQuestionsAnswered(){
        return averageQuestionsAnswered;
    }

    public void setAverageQuestionsAnswered(float averageQuestionsAnswered){
        this.averageQuestionsAnswered = averageQuestionsAnswered;
    }

    public void remove() { teacherDashboard.getQuestionStats().remove(this); }

    public String toString(){
        return "QuestionStats{" +
                "Numero questoes disponiveis=" + numAvailable +
                "Questoes unicas respondidas=" + answeredQuestionUnique +
                "Media questoes respondidas=" + averageQuestionsAnswered +
                '}';
    }

    public void update() {
        //update var numAvailable
        int availableQuestions = 0;
        for (Question q : this.getCourseExecution().getCourse().getQuestions()) {
            if (q.getStatus() == Question.Status.AVAILABLE) {
                availableQuestions++;
            }
        }
        this.setNumAvailable(availableQuestions);

        //update var answeredQuestionUnique
        Set<QuestionSubmission> questionSubmissions = this.courseExecution.getQuestionSubmissions();
        Set<Integer> uniqueQuestionsIds = new HashSet<Integer>();

        for (QuestionSubmission qs : questionSubmissions) {
            if (!uniqueQuestionsIds.contains(qs.getQuestion().getId()) && qs.getQuestion().getNumberOfAnswers() > 0) {
                uniqueQuestionsIds.add(qs.getQuestion().getId());
            }
        }

        this.setAnsweredQuestionUnique(uniqueQuestionsIds.size());

        //update var averageQuestionsAnswered
        int totalStudents = this.courseExecution.getStudents().size();
        this.setAverageQuestionsAnswered((float) (uniqueQuestionsIds.size() / totalStudents));
    }
}