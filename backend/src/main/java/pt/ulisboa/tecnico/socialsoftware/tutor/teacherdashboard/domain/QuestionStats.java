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


    // Methods
    public void accept(Visitor visitor) {
        // Only used for XML generation
    }
}