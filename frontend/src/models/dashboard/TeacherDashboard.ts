// @ts-ignore

import StudentStatsModel from '@/models/dashboard/StudentStatsModel';
export default class TeacherDashboard {
  id!: number;
  studentStats: StudentStatsModel[] = [];

  constructor(jsonObj?: TeacherDashboard) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.studentStats = jsonObj.studentStats.map(
      (studentStats: StudentStatsModel) => {
        return new StudentStatsModel(studentStats);
      }
      );
  }
}
}