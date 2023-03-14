package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuestionStats;

public class QuestionStatsDto {

    private Integer id;
    private Integer numAvailable, answeredQuestionsUnique;
    private Float averageQuestionsAnswered;

    public QuestionStatsDto() {}

    public QuestionStatsDto(QuestionStats questionStats) {
        this.id = questionStats.getId();
        this.numAvailable = questionStats.getNumAvailable();
        this.answeredQuestionsUnique = questionStats.getAnsweredQuestionsUnique();
        this.averageQuestionsAnswered = questionStats.getAverageQuestionsAnswered();
    }

    public Integer getId() {
        return this.id;
    }

    public Integer getNumAvailable() {
        return this.numAvailable;
    }

    public void setNumAvailable(int numAvailable) {this.numAvailable = numAvailable;}

    public Integer getAnsweredQuestionsUnique() {
        return this.answeredQuestionsUnique;
    }

    public void setAnsweredQuestionsUnique(int answeredQuestionsUnique) {this.answeredQuestionsUnique = answeredQuestionsUnique;}

    public Float getAverageQuestionsAnswered() {
        return this.averageQuestionsAnswered;
    }

    public void setAverageQuestionsAnswered(Float averageQuestionsAnswered) {
        this.averageQuestionsAnswered = averageQuestionsAnswered;
    }

    @Override
    public String toString() {
        return "QuestionStats{" +
                "id=" + id +
                ", numAvailable=" + numAvailable +
                ", answeredQuestionsUnique=" + answeredQuestionsUnique +
                ", averageQuestionsAnswered=" + averageQuestionsAnswered +
                '}';
    }
}
