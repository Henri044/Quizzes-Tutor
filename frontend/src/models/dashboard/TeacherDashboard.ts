// @ts-ignore

import StudentStatsModel from '@/models/dashboard/StudentStatsModel';
import QuizStats from '@/models/dashboard/QuizStats';
import QuestionStats from '@/models/dashboard/QuestionStats';

export default class TeacherDashboard {
  id!: number;
  numberOfStudents!: number;
  quizStats: QuizStats[] = [];
  studentStats: StudentStatsModel[] = [];
  questionStats: QuestionStats[] = [];

  constructor(jsonObj?: TeacherDashboard) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.numberOfStudents = jsonObj.numberOfStudents;
      this.quizStats = jsonObj.quizStats.map(
        (quizStats: QuizStats)=>{
          return new QuizStats(quizStats);
        }
      );
      this.studentStats = jsonObj.studentStats.map(
        (studentStats: StudentStatsModel) => {
          return new StudentStatsModel(studentStats);
        }
      );
      this.questionStats = jsonObj.questionStats.map(
        (questionStats: QuestionStats) => {
          return new QuestionStats(questionStats);
        }
      );
    }
  }
}
