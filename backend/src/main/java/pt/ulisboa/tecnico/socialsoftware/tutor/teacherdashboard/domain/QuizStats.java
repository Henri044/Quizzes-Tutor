package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;

public class QuizStats implements DomainEntity {
    private Integer numQuizzes;
    private Integer uniqueQuizzesSolved;
    private float averageQuizzesSolved;

    public QuizStats(){
    }

    public float getAverageQuizzesSolved(){ return averageQuizzesSolved; }

    public void setAverageQuizzesSolved(float averageQuizzesSolved){ this.averageQuizzesSolved = averageQuizzesSolved; }

    public Integer getNumQuizzes(){ return numQuizzes; }

    public void setNumQuizzes(Integer numQuizzes){ this.numQuizzes = numQuizzes; }

    public Integer getUniqueQuizzesSolved(){ return uniqueQuizzesSolved; }

    public void setUniqueQuizzesSolved(Integer uniqueQuizzesSolved){ this.uniqueQuizzesSolved = uniqueQuizzesSolved; }

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
