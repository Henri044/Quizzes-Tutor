package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.StudentStats;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.TeacherDashboard;

import java.util.ArrayList;
import java.util.List;

public class TeacherDashboardDto {
    private Integer id;
    private Integer numberOfStudents;

    private List<StudentStatsDto> studentStats = new ArrayList<>();

    public TeacherDashboardDto() {
    }

    public TeacherDashboardDto(TeacherDashboard teacherDashboard) {
        this.id = teacherDashboard.getId();
        // For the number of students, we consider only active students
        this.numberOfStudents = teacherDashboard.getCourseExecution().getNumberOfActiveStudents();
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
