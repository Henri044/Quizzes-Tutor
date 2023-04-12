<template>
  <div>
    <Bar :chartData="data" :chartOptions="options" />
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  BarElement,
  CategoryScale,
  LinearScale,
} from 'chart.js';
import { Bar } from 'vue-chartjs/legacy';

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend
);

@Component({ components: { Bar } })
export default class BarChartStudentStats extends Vue {
  @Prop({ type: Array, required: true })
  labels!: string[];

  @Prop({ type: Array, required: true })
  numStudents!: number[];

  @Prop({ type: Array, required: true })
  numMore75CorrectQuestions!: number[];

  @Prop({ type: Array, required: true })
  numAtLeast3Quizzes!: number[];
  data(): { data: { labels: string[]; datasets: any[] } } {
    return {
      data: {
        labels: this.labels,
        datasets: [
          {
            label: 'Number of Students',
            backgroundColor: '#B22222',
            data: this.numStudents,
          },
          {
            label: 'Number of Students who Solved >= 75% Questions',
            backgroundColor: '#4682B4',
            data: this.numMore75CorrectQuestions,
          },
          {
            label: 'Number of Students who Solved >= 3 Quizzes',
            backgroundColor: '#3CB371',
            data: this.numAtLeast3Quizzes,
          },
        ],
      },
    };
  }
  options(): { options: any } {
    return {
      options: {
        responsive: true,
        scales: {
          x: {
            ticks: {
              font: {
                size: 14,
              },
            },
          },
        },
      },
    };
  }
}
</script>
