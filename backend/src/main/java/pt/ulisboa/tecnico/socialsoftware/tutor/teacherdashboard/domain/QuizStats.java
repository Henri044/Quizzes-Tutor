package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;

public class QuizStats implements DomainEntity {
    private int numQuizzes;
    private int uniqueQuizzesSolved;
    private float averageQuizzesSolved;
    private CourseExecution courseExecution;

    public QuizStats(int numQuizzes, int uniqueQuizzesSolved, float averageQuizzesSolved, CourseExecution courseExecution){
        setNumQuizzes(numQuizzes);
        setUniqueQuizzesSolved(uniqueQuizzesSolved);
        setAverageQuizzesSolved(averageQuizzesSolved);
        setCourseExecution(courseExecution);
    }

    public CourseExecution getCourseExecution() {
        return courseExecution;
    }

    public void setCourseExecution(CourseExecution courseExecution) { this.courseExecution = courseExecution; }

    public float getAverageQuizzesSolved(){ return averageQuizzesSolved; }

    public void setAverageQuizzesSolved(float averageQuizzesSolved){ this.averageQuizzesSolved = averageQuizzesSolved; }

    public int getNumQuizzes(){ return numQuizzes; }

    public void setNumQuizzes(int numQuizzes){ this.numQuizzes = numQuizzes; }

    public int getUniqueQuizzesSolved(){ return uniqueQuizzesSolved; }

    public void setUniqueQuizzesSolved(int uniqueQuizzesSolved){ this.uniqueQuizzesSolved = uniqueQuizzesSolved; }

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
