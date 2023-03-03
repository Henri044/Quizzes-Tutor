package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuizStats;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuestionStats;


@Entity
public class TeacherDashboard implements DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private CourseExecution courseExecution;

    @ManyToOne
    private Teacher teacher;

    @OneToMany
    private Set<QuestionStats> question = new HashSet<QuestionStats>();

    @OneToMany
    private Set<QuizStats> quizStats = new HashSet<QuizStats>() ;

    public TeacherDashboard() {
    }

    public TeacherDashboard(CourseExecution courseExecution, Teacher teacher) {
        setCourseExecution(courseExecution);
        setTeacher(teacher);
    }

    public void remove() {
        teacher.getDashboards().remove(this);
        teacher = null;
    }

    public void addQuizStats(QuizStats quizStat) { quizStats.add(quizStat); }

    public Integer getId() {
        return id;
    }

    public CourseExecution getCourseExecution() {
        return courseExecution;
    }

    public Set<QuizStats> getQuizStats(){ return quizStats; }

    public void setCourseExecution(CourseExecution courseExecution) {
        this.courseExecution = courseExecution;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
        this.teacher.addDashboard(this);
    }

    public Set<QuestionStats> getQuestionStats() { return question; }

    public boolean addQuestionStats(QuestionStats questionStats) {
        return question.add(questionStats);
    }

    public boolean removeQuestionStats(QuestionStats questionStats) {
        return question.remove(questionStats);
    }

    public void accept(Visitor visitor) {
        // Only used for XML generation
    }

    public void update() {
        for (QuestionStats qts : question) {
            qs.update();
        }
        for (QuizStats qzs: getQuizStats()){
            qs.update();
        }
    }

    @Override
    public String toString() {
        return "Dashboard{" +
                "id=" + id +
                ", courseExecution=" + courseExecution +
                ", teacher=" + teacher +
                '}';
    }

}
