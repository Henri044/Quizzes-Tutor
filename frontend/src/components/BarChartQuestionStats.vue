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
export default class BarChartQuestionStats extends Vue {
  @Prop({ type: Array, required: true })
  labels!: string[];

  @Prop({ type: Array, required: true })
  numAvailable!: number[];

  @Prop({ type: Array, required: true })
  answeredQuestionsUnique!: number[];

  @Prop({ type: Array, required: true })
  averageQuestionsAnswered!: number[];
  data(): { data: { labels: string[]; datasets: any[] } } {
    return {
      data: {
        labels: this.labels,
        datasets: [
          {
            label: 'Number of Questions',
            backgroundColor: '#B22222',
            data: this.numAvailable,
          },
          {
            label: 'Number of Questions Solved (Unique)',
            backgroundColor: '#4682B4',
            data: this.answeredQuestionsUnique,
          },
          {
            label:
              'Number of Questions Correctly Solved (Unique, Average Per Student)',
            backgroundColor: '#3CB371',
            data: this.averageQuestionsAnswered,
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
