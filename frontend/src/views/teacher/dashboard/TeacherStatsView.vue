<template>
  <div class="stats-container">
    <h2>Statistics for this course execution</h2>
    <div v-if="teacherDashboard != null" class="stats-container">
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
    <div v-if="teacherDashboard != null" class="stats-container">
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
import TeacherDashboard from '@/models/dashboard/TeacherDashboard';
import BarChartQuestionStats from '@/components/BarChartQuestionStats.vue';

@Component({
  components: { BarChartQuestionStats, AnimatedNumber },
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
    return ['2019', '2022', '2023'];
  }
  numAvailable() {
    let list = [0, 0, 0];
    // POPULAR A LISTA
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
    // POPULAR A LISTA
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
    // POPULAR A LISTA
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
