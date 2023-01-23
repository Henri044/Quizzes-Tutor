<template>
  <div class="container">
    <h1>Teacher Dashboard</h1>

    <div v-if="show === 'Global'" class="stats-container">
      <global-stats-view :dashboardId="dashboardId"></global-stats-view>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import GlobalStatsView from '@/views/teacher/dashboard/TeacherStatsView.vue';

@Component({
  components: {
    GlobalStatsView
  },
})
export default class TeacherDashboardView extends Vue {
  dashboardId: number | null = null;
  show: string | null = null;

  async created() {
    await this.$store.dispatch('loading');
    try {
      let teacherDashboard = await RemoteServices.getTeacherDashboard();

      this.dashboardId = teacherDashboard.id;
      this.show = 'Global';
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>
