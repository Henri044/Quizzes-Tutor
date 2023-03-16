package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard;import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuestionStats;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuizStats;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.StudentStats;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard;

import java.util.ArrayList;
import java.util.List;

public class TeacherDashboardDto {
    private Integer id;

    private Integer numberOfStudents;

  private List<QuizStatsDto> courseExecutionQuizStats = new ArrayList<>();

    private List<QuestionStatsDto> questionStatsDtoList = new ArrayList<>();

    private List<StudentStatsDto> studentStats = new ArrayList<>();

    public TeacherDashboardDto() {
    }

    public TeacherDashboardDto(TeacherDashboard teacherDashboard) {
        this.id = teacherDashboard.getId();
        // For the number of students, we consider only active students
        this.numberOfStudents = teacherDashboard.getCourseExecution().getNumberOfActiveStudents();
        for (int i = 0; i< teacherDashboard.getQuizStats().size();i++){
            QuizStatsDto quizStatsDto1 = new QuizStatsDto(teacherDashboard.getQuizStats().get(i));
            courseExecutionQuizStats.add(quizStatsDto1);
        }
        List<QuestionStats> auxQuestionStats = teacherDashboard.getQuestionStats();
        for(QuestionStats x: auxQuestionStats) {
            this.questionStatsDtoList.add(new QuestionStatsDto(x));
        }
        List<StudentStats> aux_studentStats = teacherDashboard.getStudentStats();
        for(StudentStats x : aux_studentStats){
            this.studentStats.add(new StudentStatsDto(x));
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

    public List<QuestionStatsDto> getQuestionStats() {
        return questionStatsDtoList;
    }

    public void setQuestionStats(List<QuestionStatsDto> stats) {
        this.questionStatsDtoList = stats;
    }

    public List<StudentStatsDto> getStudentStats() {
        return studentStats;
    }

    public void setStudentStats(List<StudentStatsDto> stats) {
        this.studentStats = stats;
    }

    @Override
    public String toString() {
        return "TeacherDashboardDto{" +
                "id=" + id +
                ", numberOfStudents=" + this.getNumberOfStudents() +
                "}";
    }
}
