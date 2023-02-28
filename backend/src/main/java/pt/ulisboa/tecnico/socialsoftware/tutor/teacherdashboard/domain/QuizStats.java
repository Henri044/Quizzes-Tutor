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

    public Integer getAverageQuizzesSolved(){ return averageQuizzesSolved; }

    public void setAverageQuizzesSolved(Integer averageQuizzesSolved){ this.averageQuizzesSolved = averageQuizzesSolved; }

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
