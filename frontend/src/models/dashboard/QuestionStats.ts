export default class QuestionStats {
  numAvailable!: number;
  answeredQuestionsUnique!: number;
  averageQuestionsAnswered!: number;
  courseExecutionYear!: number;

  constructor(jsonObj?: QuestionStats) {
    if (jsonObj) {
      this.numAvailable = jsonObj.numAvailable;
      this.answeredQuestionsUnique = jsonObj.answeredQuestionsUnique;
      this.averageQuestionsAnswered = jsonObj.averageQuestionsAnswered;
      this.courseExecutionYear = jsonObj.courseExecutionYear;
    }
  }
}
