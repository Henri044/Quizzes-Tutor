package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;

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
        return setnumAtLeast3Quizzes;
    }

    public void setNumAtLeast3Quizzes(int num){
        this.setnumAtLeast3Quizzes = num;
    }

    public void update() {
  		//TO D0
    }

    public void remove() {
        teacherdashboard.getStudentStats().remove(this);
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
