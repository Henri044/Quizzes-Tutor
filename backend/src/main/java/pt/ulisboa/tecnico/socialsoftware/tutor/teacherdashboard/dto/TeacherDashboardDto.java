package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard;import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;import java.util.ArrayList;import java.util.List;

public class TeacherDashboardDto {
    private Integer id;
    private Integer numberOfStudents;
    private List<QuizStatsDto> courseExecutionQuizStats;

    public TeacherDashboardDto() {
    }

    public TeacherDashboardDto(TeacherDashboard teacherDashboard) {
        this.id = teacherDashboard.getId();
        // For the number of students, we consider only active students
        this.numberOfStudents = teacherDashboard.getCourseExecution().getNumberOfActiveStudents();
        for (int i = 0; i< teacherDashboard.getQuizStats().size();){
            QuizStatsDto quizStatsDto1 = new QuizStatsDto(teacherDashboard.getQuizStats().get(i));
            courseExecutionQuizStats.add(quizStatsDto1);
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

    public List<QuizStatsDto> getCourseExecutionQuizStats() {
        return courseExecutionQuizStats;
    }

    public void setCourseExecutionQuizStats(List<QuizStatsDto> courseExecutionQuizStats) {
        this.courseExecutionQuizStats = courseExecutionQuizStats;
    }

  @Override
  public String toString() {
        return "TeacherDashboardDto{" +
                "id=" + id +
                ", numberOfStudents=" + this.getNumberOfStudents() +
                "}";
    }
}
