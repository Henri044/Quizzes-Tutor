import QuestionStats from '@/models/dashboard/QuestionStats';
export default class TeacherDashboard {
  id!: number;
  questionStats: QuestionStats[] = [];

  constructor(jsonObj?: TeacherDashboard) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.questionStats = jsonObj.questionStats.map(
        (questionStats: QuestionStats) => {
          return new QuestionStats(questionStats);
        }
      );
    }
  }
}
