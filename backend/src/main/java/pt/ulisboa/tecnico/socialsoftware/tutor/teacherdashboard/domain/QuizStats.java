package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard;

import javax.persistence.*;
import java.util.Set;
import java.util.HashSet;

@Entity
public class QuizStats implements DomainEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    private TeacherDashboard teacherDashboard;

    private int numQuizzes;
    private int uniqueQuizzesSolved;
    private float averageQuizzesSolved;
    @OneToOne
    private CourseExecution courseExecution;
    public QuizStats(){
    }
    public QuizStats(CourseExecution courseExecution){
        setCourseExecution(courseExecution);
    }

    public CourseExecution getCourseExecution() { return courseExecution; }

    public void setCourseExecution(CourseExecution courseExecution) { this.courseExecution = courseExecution; }

    public TeacherDashboard getTeacherDashboard() { return teacherDashboard; }

    public void setTeacherDashboard(TeacherDashboard teacherDashboard) { this.teacherDashboard = teacherDashboard; }

    public void remove() {

    }
    public Integer getId() {
        return id;
    }

    public float getAverageQuizzesSolved(){ return averageQuizzesSolved; }

    public void setAverageQuizzesSolved(float averageQuizzesSolved){ this.averageQuizzesSolved = averageQuizzesSolved; }

    public int getNumQuizzes(){ return numQuizzes; }

    public void setNumQuizzes(int numQuizzes){ this.numQuizzes = numQuizzes; }

    public int getUniqueQuizzesSolved(){ return uniqueQuizzesSolved; }

    public void setUniqueQuizzesSolved(int uniqueQuizzesSolved){ this.uniqueQuizzesSolved = uniqueQuizzesSolved; }

    public void update(){
        this.setNumQuizzes(this.getCourseExecution().getNumberOfQuizzes());
        Set<Student> students = new HashSet<>(this.getCourseExecution().getStudents());
        Set<Quiz> quizzes = new HashSet<>();

        for (Student s: students) {
            Set<QuizAnswer> quizAnswers = new HashSet<>(s.getQuizAnswers());
            for (QuizAnswer qa: quizAnswers){
                quizzes.add(qa.getQuiz());
            }
        }

        for (Quiz q1: quizzes){
            for(Quiz q2: quizzes){
                if (q1.getId() != q2.getId()){
                    quizzes.remove(q2);
                }
            }
        }

        this.setUniqueQuizzesSolved(quizzes.size());

        this.setAverageQuizzesSolved(getUniqueQuizzesSolved() / students.size());
    }

    public void accept(Visitor visitor) {
        // Only used for XML generation
    }
    @Override
    public String toString() {
        return "QuizStats{" +
                "numQuizzes=" + numQuizzes +
                ", uniqueQuizzesSolved=" + uniqueQuizzesSolved +
                ", averaqeQuizzesSolved=" + averageQuizzesSolved +
                '}';
    }

}
