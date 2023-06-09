<template>
  <div class="stats-container">
    <h2>Statistics for this course execution</h2>
    <div v-if="teacherDashboard != null" class="stats-container">
      <div class="items">
        <div ref="totalStudents" class="icon-wrapper">
          <animated-number
            :number="teacherDashboard.studentStats[0].numStudents"
          />
        </div>
        <div class="project-name">
          <p>Number of Students</p>
        </div>
      </div>
      <div class="items">
        <div ref="totalStudents" class="icon-wrapper">
          <animated-number
            :number="teacherDashboard.studentStats[0].numMore75CorrectQuestions"
          />
        </div>
        <div class="project-name">
          <p>Number of Students who Solved >= 75% Questions</p>
        </div>
      </div>
      <div class="items">
        <div ref="totalStudents" class="icon-wrapper">
          <animated-number
            :number="teacherDashboard.studentStats[0].numAtLeast3Quizzes"
          />
        </div>
        <div class="project-name">
          <p>Number of Students who Solved >= 3 Quizzes</p>
        </div>
      </div>
      <div class="items">
        <div ref="totalStudents" class="icon-wrapper">
          <animated-number :number="teacherDashboard.quizStats[0].numQuizzes" />
        </div>
        <div class="project-name">
          <p>Number of Quizzes</p>
        </div>
      </div>
      <div class="items">
        <div ref="totalStudents" class="icon-wrapper">
          <animated-number
            :number="teacherDashboard.quizStats[0].numUniqueAnsweredQuizzes"
          />
        </div>
        <div class="project-name">
          <p>Number of Quizzes Solved (Unique)</p>
        </div>
      </div>
      <div class="items">
        <div ref="totalStudents" class="icon-wrapper">
          <animated-number
            :number="teacherDashboard.quizStats[0].averageQuizzesSolved"
          />
        </div>
        <div class="project-name">
          <p>Number of Quizzes Solved (Unique, Average Per Student)</p>
        </div>
      </div>
      <div class="items">
        <div ref="totalStudents" class="icon-wrapper">
          <animated-number
            :number="teacherDashboard.questionStats[0].numAvailable"
          />
        </div>
        <div class="project-name">
          <p>Number of Questions</p>
        </div>
      </div>
      <div class="items">
        <div ref="totalStudents" class="icon-wrapper">
          <animated-number
            :number="teacherDashboard.questionStats[0].answeredQuestionsUnique"
          />
        </div>
        <div class="project-name">
          <p>Number of Questions Solved (Unique)</p>
        </div>
      </div>
      <div class="items">
        <div ref="totalStudents" class="icon-wrapper">
          <animated-number
            :number="teacherDashboard.questionStats[0].averageQuestionsAnswered"
          />
        </div>
        <div class="project-name">
          <p>
            Number of Questions Correctly Solved (Unique, Average Per Student)
          </p>
        </div>
      </div>
    </div>
    <h2>Comparison with previous course executions</h2>
    <div
      v-if="
        teacherDashboard != null && teacherDashboard.studentStats.length > 1
      "
      class="stats-container"
    >
      <div ref="barchart" class="chart-container">
        <bar-chart-student-stats
          :labels="labels()"
          :numStudents="numStudents()"
          :numMore75CorrectQuestions="numMore75CorrectQuestions()"
          :numAtLeast3Quizzes="numAtLeast3Quizzes()"
        />
      </div>
      <div ref="barchart" class="chart-container">
        <bar-chart-quiz-stats
          :labels="labels()"
          :numQuizzes="numQuizzes()"
          :numUniqueAnsweredQuizzes="numUniqueAnsweredQuizzes()"
          :averageQuizzesSolved="averageQuizzesSolved()"
        />
      </div>
      <div ref="barchart" class="chart-container">
        <bar-chart-question-stats
          :labels="labels()"
          :numAvailable="numAvailable()"
          :answeredQuestionsUnique="answeredQuestionsUnique()"
          :averageQuestionsAnswered="averageQuestionsAnswered()"
        />
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import AnimatedNumber from '@/components/AnimatedNumber.vue';
import BarChartQuizStats from '@/components/BarChartQuizStats.vue';
import BarChartStudentStats from '@/components/BarChartStudentStats.vue';
import TeacherDashboard from '@/models/dashboard/TeacherDashboard';
import BarChartQuestionStats from '@/components/BarChartQuestionStats.vue';

@Component({
  components: {
    BarChartStudentStats,
    BarChartQuizStats,
    BarChartQuestionStats,
    AnimatedNumber,
  },
})
export default class TeacherStatsView extends Vue {
  @Prop() readonly dashboardId!: number;
  teacherDashboard: TeacherDashboard | null = null;

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.teacherDashboard = await RemoteServices.getTeacherDashboard();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
  labels() {
    let list = ['', '', ''];
    if (this.teacherDashboard!.studentStats.length >= 3) {
      list = [
        '' + this.teacherDashboard!.studentStats[2].courseExecutionYear,
        '' + this.teacherDashboard!.studentStats[1].courseExecutionYear,
        '' + this.teacherDashboard!.studentStats[0].courseExecutionYear + '(current)',
      ];
    } else if (this.teacherDashboard!.studentStats.length == 2) {
      list = [
        '',
        '' + this.teacherDashboard!.studentStats[1].courseExecutionYear,
        '' + this.teacherDashboard!.studentStats[0].courseExecutionYear + '(current)',
      ];
    }
    return list;
  }
  numStudents() {
    let list = [0, 0, 0];
    if (this.teacherDashboard!.studentStats.length >= 3) {
      list = [
        this.teacherDashboard!.studentStats[2].numStudents,
        this.teacherDashboard!.studentStats[1].numStudents,
        this.teacherDashboard!.studentStats[0].numStudents,
      ];
    } else if (this.teacherDashboard!.studentStats.length == 2) {
      list = [
        0,
        this.teacherDashboard!.studentStats[1].numStudents,
        this.teacherDashboard!.studentStats[0].numStudents,
      ];
    }
    return list;
  }
  numMore75CorrectQuestions() {
    let list = [0, 0, 0];
    if (this.teacherDashboard!.studentStats.length >= 3) {
      list = [
        this.teacherDashboard!.studentStats[2].numMore75CorrectQuestions,
        this.teacherDashboard!.studentStats[1].numMore75CorrectQuestions,
        this.teacherDashboard!.studentStats[0].numMore75CorrectQuestions,
      ];
    } else if (this.teacherDashboard!.studentStats.length == 2) {
      list = [
        0,
        this.teacherDashboard!.studentStats[1].numMore75CorrectQuestions,
        this.teacherDashboard!.studentStats[0].numMore75CorrectQuestions,
      ];
    }
    return list;
  }
  numAtLeast3Quizzes() {
    let list = [0, 0, 0];
    if (this.teacherDashboard!.studentStats.length >= 3) {
      list = [
        this.teacherDashboard!.studentStats[2].numAtLeast3Quizzes,
        this.teacherDashboard!.studentStats[1].numAtLeast3Quizzes,
        this.teacherDashboard!.studentStats[0].numAtLeast3Quizzes,
      ];
    } else if (this.teacherDashboard!.studentStats.length == 2) {
      list = [
        0,
        this.teacherDashboard!.studentStats[1].numAtLeast3Quizzes,
        this.teacherDashboard!.studentStats[0].numAtLeast3Quizzes,
      ];
    }
    return list;
  }
  numQuizzes() {
    let list = [0, 0, 0];
    if (this.teacherDashboard!.quizStats.length >= 3) {
      list = [
        this.teacherDashboard!.quizStats[2].numQuizzes,
        this.teacherDashboard!.quizStats[1].numQuizzes,
        this.teacherDashboard!.quizStats[0].numQuizzes,
      ];
    } else if (this.teacherDashboard!.quizStats.length == 2) {
      list = [
        0,
        this.teacherDashboard!.quizStats[1].numQuizzes,
        this.teacherDashboard!.quizStats[0].numQuizzes,
      ];
    } else if (this.teacherDashboard!.quizStats.length == 1) {
      list = [0, 0, this.teacherDashboard!.quizStats[0].numQuizzes];
    }
    return list;
  }
  numUniqueAnsweredQuizzes() {
    let list = [0, 0, 0];
    if (this.teacherDashboard!.quizStats.length >= 3) {
      list = [
        this.teacherDashboard!.quizStats[2].numUniqueAnsweredQuizzes,
        this.teacherDashboard!.quizStats[1].numUniqueAnsweredQuizzes,
        this.teacherDashboard!.quizStats[0].numUniqueAnsweredQuizzes,
      ];
    } else if (this.teacherDashboard!.quizStats.length == 2) {
      list = [
        0,
        this.teacherDashboard!.quizStats[1].numUniqueAnsweredQuizzes,
        this.teacherDashboard!.quizStats[0].numUniqueAnsweredQuizzes,
      ];
    } else if (this.teacherDashboard!.quizStats.length == 1) {
      list = [
        0,
        0,
        this.teacherDashboard!.quizStats[0].numUniqueAnsweredQuizzes,
      ];
    }
    return list;
  }
  averageQuizzesSolved() {
    let list = [0, 0, 0];
    if (this.teacherDashboard!.quizStats.length >= 3) {
      list = [
        this.teacherDashboard!.quizStats[2].averageQuizzesSolved,
        this.teacherDashboard!.quizStats[1].averageQuizzesSolved,
        this.teacherDashboard!.quizStats[0].averageQuizzesSolved,
      ];
    } else if (this.teacherDashboard!.quizStats.length == 2) {
      list = [
        0,
        this.teacherDashboard!.quizStats[1].averageQuizzesSolved,
        this.teacherDashboard!.quizStats[0].averageQuizzesSolved,
      ];
    } else if (this.teacherDashboard!.quizStats.length == 1) {
      list = [0, 0, this.teacherDashboard!.quizStats[0].averageQuizzesSolved];
    }
    return list;
  }
  numAvailable() {
    let list = [0, 0, 0];
    if (this.teacherDashboard!.questionStats.length >= 3) {
      list = [
        this.teacherDashboard!.questionStats[2].numAvailable,
        this.teacherDashboard!.questionStats[1].numAvailable,
        this.teacherDashboard!.questionStats[0].numAvailable,
      ];
    } else if (this.teacherDashboard!.questionStats.length == 2) {
      list = [
        0,
        this.teacherDashboard!.questionStats[1].numAvailable,
        this.teacherDashboard!.questionStats[0].numAvailable,
      ];
    } else if (this.teacherDashboard!.questionStats.length == 1) {
      list = [0, 0, this.teacherDashboard!.questionStats[0].numAvailable];
    }
    return list;
  }
  answeredQuestionsUnique() {
    let list = [0, 0, 0];
    if (this.teacherDashboard!.questionStats.length >= 3) {
      list = [
        this.teacherDashboard!.questionStats[2].answeredQuestionsUnique,
        this.teacherDashboard!.questionStats[1].answeredQuestionsUnique,
        this.teacherDashboard!.questionStats[0].answeredQuestionsUnique,
      ];
    } else if (this.teacherDashboard!.questionStats.length == 2) {
      list = [
        0,
        this.teacherDashboard!.questionStats[1].answeredQuestionsUnique,
        this.teacherDashboard!.questionStats[0].answeredQuestionsUnique,
      ];
    } else if (this.teacherDashboard!.questionStats.length == 1) {
      list = [
        0,
        0,
        this.teacherDashboard!.questionStats[0].answeredQuestionsUnique,
      ];
    }
    return list;
  }
  averageQuestionsAnswered() {
    let list = [0, 0, 0];
    if (this.teacherDashboard!.questionStats.length >= 3) {
      list = [
        this.teacherDashboard!.questionStats[2].averageQuestionsAnswered,
        this.teacherDashboard!.questionStats[1].averageQuestionsAnswered,
        this.teacherDashboard!.questionStats[0].averageQuestionsAnswered,
      ];
    } else if (this.teacherDashboard!.questionStats.length == 2) {
      list = [
        0,
        this.teacherDashboard!.questionStats[1].averageQuestionsAnswered,
        this.teacherDashboard!.questionStats[0].averageQuestionsAnswered,
      ];
    } else if (this.teacherDashboard!.questionStats.length == 1) {
      list = [
        0,
        0,
        this.teacherDashboard!.questionStats[0].averageQuestionsAnswered,
      ];
    }
    return list;
  }
}
</script>

<style lang="scss" scoped>
.stats-container {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  justify-content: center;
  align-items: stretch;
  align-content: center;
  height: 100%;

  .items {
    background-color: rgba(255, 255, 255, 0.75);
    color: #1976d2;
    border-radius: 5px;
    flex-basis: 25%;
    margin: 20px;
    cursor: pointer;
    transition: all 0.6s;
  }

  .bar-chart {
    background-color: rgba(255, 255, 255, 0.9);
    height: 400px;
  }
}

.icon-wrapper,
.project-name {
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-wrapper {
  font-size: 100px;
  transform: translateY(0px);
  transition: all 0.6s;
}

.icon-wrapper {
  align-self: end;
}

.project-name {
  align-self: start;
}

.project-name p {
  font-size: 24px;
  font-weight: bold;
  letter-spacing: 2px;
  transform: translateY(0px);
  transition: all 0.5s;
}

.items:hover {
  border: 3px solid black;

  & .project-name p {
    transform: translateY(-10px);
  }

  & .icon-wrapper i {
    transform: translateY(5px);
  }
}

.chart-container {
  background-color: white;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin: 10px;
}
</style>
