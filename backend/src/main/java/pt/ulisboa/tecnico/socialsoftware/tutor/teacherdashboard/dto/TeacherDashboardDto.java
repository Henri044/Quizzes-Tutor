package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuestionStats;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuizStats;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard;

import java.util.ArrayList;
import java.util.List;

public class TeacherDashboardDto {
    private Integer id;
    private Integer numberOfStudents;

    private List<QuestionStatsDto> questionStatsDtoList = new ArrayList<>();

    public TeacherDashboardDto() {
    }

    public TeacherDashboardDto(TeacherDashboard teacherDashboard) {
        this.id = teacherDashboard.getId();
        // For the number of students, we consider only active students
        this.numberOfStudents = teacherDashboard.getCourseExecution().getNumberOfActiveStudents();
        List<QuestionStats> auxQuestionStats = teacherDashboard.getQuestionStats();
        for(QuestionStats x: auxQuestionStats) {
            this.questionStatsDtoList.add(new QuestionStatsDto(x));
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumberOfStudents() {
        return numberOfStudents;
    }

    public void setNumberOfStudents(Integer numberOfStudents) {
        this.numberOfStudents = numberOfStudents;
    }

    public List<QuestionStatsDto> getQuestionStats() {
        return questionStatsDtoList;
    }

    public void setQuestionStats(List<QuestionStatsDto> stats) {
        this.questionStatsDtoList = stats;
    }

    @Override
    public String toString() {
        return "TeacherDashboardDto{" +
                "id=" + id +
                ", numberOfStudents=" + this.getNumberOfStudents() +
                "}";
    }
}
