// @ts-ignore

import StudentStatsModel from '@/models/dashboard/StudentStatsModel';
import QuizStats from '@/models/dashboard/QuizStats';

export default class TeacherDashboard {
  id!: number;
  quizStats: QuizStats[] = [];
  studentStats: StudentStatsModel[] = [];

  constructor(jsonObj?: TeacherDashboard) {
    if (jsonObj) {
      this.id = jsonObj.id;
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
    }
  }
}
