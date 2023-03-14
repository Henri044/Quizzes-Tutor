package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.*;
import javax.persistence.*;
import java.io.Serializable;

public class QuizStatsDto implements Serializable {

  private Integer id;

  private CourseExecution courseExecution;

  private TeacherDashboard teacherDashboard;

  private int numQuizzes;

  private int numUniqueAnsweredQuizzes;

  private float averageQuizzesSolved;
  public QuizStatsDto(){

  }
  public QuizStatsDto (QuizStats quizStats){
      this.id = quizStats.getId();
      this.courseExecution = quizStats.getCourseExecution();
      this.teacherDashboard = quizStats.getTeacherDashboard();
      this.numQuizzes = quizStats.getNumQuizzes();
      this.numUniqueAnsweredQuizzes = quizStats.getNumUniqueAnsweredQuizzes();
      this.averageQuizzesSolved = quizStats.getAverageQuizzesSolved();
  }

  public Integer getId(){return id;}

  public void setId(Integer id){this.id = id;}

  public CourseExecution getCourseExecution(){return courseExecution;}

  public void setCourseExecution(CourseExecution courseExecution) {this.courseExecution = courseExecution;}

  public TeacherDashboard getTeacherDashboard() {return teacherDashboard;}

  public void setTeacherDashboard(TeacherDashboard teacherDashboard) {this.teacherDashboard = teacherDashboard;}

  public int getNumQuizzes() {return numQuizzes;}

  public void setNumQuizzes(int numQuizzes) {this.numQuizzes = numQuizzes;}

  public int getNumUniqueAnsweredQuizzes() {return numUniqueAnsweredQuizzes;}

  public void setNumUniqueAnsweredQuizzes(int numUniqueAnsweredQuizzes) {this.numUniqueAnsweredQuizzes = numUniqueAnsweredQuizzes;}

  public float   getAverageQuizzesSolved() {return averageQuizzesSolved;}

  public void setAverageQuizzesSolved(float averageQuizzesSolved) {this.averageQuizzesSolved = averageQuizzesSolved;
  }
}
