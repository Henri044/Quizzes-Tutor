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
export default class BarChartQuizStats extends Vue {
  @Prop({ type: Array, required: true })
  labels!: string[];

  @Prop({ type: Array, required: true })
  numQuizzes!: number[];

  @Prop({ type: Array, required: true })
  numUniqueAnsweredQuizzes!: number[];

  @Prop({ type: Array, required: true })
  averageQuizzesSolved!: number[];
  data(): { data: { labels: string[]; datasets: any[] } } {
    return {
      data: {
        labels: this.labels,
        datasets: [
          {
            label: 'Number of Quizzes',
            backgroundColor: '#B22222',
            data: this.numQuizzes,
          },
          {
            label: 'Number of Quizzes Solved (Unique)',
            backgroundColor: '#4682B4',
            data: this.numUniqueAnsweredQuizzes,
          },
          {
            label:
                'Number of Quizzes Solved (Unique, Average Per Student)',
            backgroundColor: '#3CB371',
            data: this.averageQuizzesSolved,
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