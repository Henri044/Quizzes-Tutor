import QuizStats from '@/models/dashboard/QuizStats';

export default class TeacherDashboard {
  id!: number;
  quizStats: QuizStats[] = [];

  constructor(jsonObj?: TeacherDashboard) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.quizStats = jsonObj.quizStats.map(
          (quizStats: QuizStats)=>{
            return new QuizStats(quizStats);
          }
      )
    }
  }
}
