export default class StudentStatsModel {
  numStudents!: number;
  numMore75CorrectQuestions!: number;
  numAtLeast3Quizzes!: number;

  constructor(jsonObj?: StudentStatsModel) {
    if (jsonObj) {
      this.numStudents = jsonObj.numStudents;
      this.numMore75CorrectQuestions = jsonObj.numMore75CorrectQuestions;
      this.numAtLeast3Quizzes = jsonObj.numAtLeast3Quizzes;
    }
  }
}