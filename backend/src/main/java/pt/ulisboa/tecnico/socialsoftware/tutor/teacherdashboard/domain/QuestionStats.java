package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import javax.persistence.*;

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

    public QuestionStats(CourseExecution courseExecution){
        setCourseExecution(courseExecution);
    }

    // Methods
    public void accept(Visitor visitor) {
        // Only used for XML generation
    }

    public CourseExecution getCourseExecution() {
        return courseExecution;
    }

    public void setCourseExecution(CourseExecution courseExecution){
        this.courseExecution = courseExecution;
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

    public String toString(){
        return "QuestionStats{" +
                "Numero questoes disponiveis=" + numAvailable +
                "Questoes unicas respondidas=" + answeredQuestionUnique +
                "Media questoes respondidas=" + averageQuestionsAnswered +
                '}';
    }
}