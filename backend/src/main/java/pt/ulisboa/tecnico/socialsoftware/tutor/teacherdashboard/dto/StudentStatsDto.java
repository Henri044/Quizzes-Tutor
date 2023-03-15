package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.StudentStats;

public class StudentStatsDto {

    private Integer id;

    private int numStudents;
    private int numMore75CorrectQuestions;
    private int numAtLeast3Quizzes;

    public StudentStatsDto(){
    }

    public StudentStatsDto(StudentStats StudentStats){
        this.id = StudentStats.getId();
        this.numStudents = StudentStats.getNumStudents();
        this.numMore75CorrectQuestions = StudentStats.getNumMore75CorrectQuestions();
        this.numAtLeast3Quizzes = StudentStats.getNumAtLeast3Quizzes();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getNumStudents() {
        return numStudents;
    }

    public void setNumStudents(int numStudents) {
        this.numStudents = numStudents;
    }

    public int getNumMore75CorrectQuestions() {
        return numMore75CorrectQuestions;
    }

    public void setNumMore75CorrectQuestions(int numMore75CorrectQuestions) {
        this.numMore75CorrectQuestions = numMore75CorrectQuestions;
    }

    public int getNumAtLeast3Quizzes() {
        return numAtLeast3Quizzes;
    }

    public void setNumAtLeast3Quizzes(int numAtLeast3Quizzes) {
        this.numAtLeast3Quizzes = numAtLeast3Quizzes;
    }

    public String toString() {
        return "StudentStats{" +
                "id=" + id +
                ", numStudents=" + numStudents +
                ", numMore75CorrectQuestions=" + numMore75CorrectQuestions +
                ", numAtLeast3Quizzes=" + numAtLeast3Quizzes +
                '}';
    }
}
