export default class TeacherDashboard {
  id!: number;
  numberOfStudents!: number;

  constructor(jsonObj?: TeacherDashboard) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.numberOfStudents = jsonObj.numberOfStudents;
    }
  }
}
