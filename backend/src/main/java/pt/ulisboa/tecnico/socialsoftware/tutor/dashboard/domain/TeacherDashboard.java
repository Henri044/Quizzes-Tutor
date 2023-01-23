package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Teacher;

import javax.persistence.*;


@Entity
public class TeacherDashboard implements DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private int numberOfStudents = 0;
    @ManyToOne
    private CourseExecution courseExecution;

    @ManyToOne
    private Teacher teacher;

    public TeacherDashboard() {
    }

    public TeacherDashboard(CourseExecution courseExecution, Teacher teacher) {
        setCourseExecution(courseExecution);
        setTeacher(teacher);
    }

    public void remove() {
        teacher.getDashboards().remove(this);
        teacher = null;
    }

    public Integer getId() {
        return id;
    }

    public int getNumberOfStudents() {
        return numberOfStudents;
    }

    public void setNumberOfStudents(int numberOfStudents) {
        this.numberOfStudents = numberOfStudents;
    }

    public CourseExecution getCourseExecution() {
        return courseExecution;
    }

    public void setCourseExecution(CourseExecution courseExecution) {
        this.courseExecution = courseExecution;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
        this.teacher.addDashboard(this);
    }

    public void accept(Visitor visitor) {
        // Only used for XML generation
    }

    @Override
    public String toString() {
        return "Dashboard{" +
                "id=" + id +
                ", numberOfStudents=" + numberOfStudents +
                ", courseExecution=" + courseExecution +
                ", teacher=" + teacher +
                '}';
    }

}