<template>
  <v-card
    elevation="1"
    outlined
    style="position:relative"
  >
    <v-card-title>
      CPU
      <v-icon>mdi-developer-board</v-icon>
    </v-card-title>
    <v-responsive height="200">
      <chart-cpu-lines @setCpuAvg="setCpuAvg"/>
    </v-responsive>

    <v-progress-circular
      :rotate="270"
      :size="50"
      :width="25"
      :value="cpuAvg"
      :color="cpuAvg > 80 ? 'red' : cpuAvg > 50 ? 'orange' : cpuAvg > 20 ? 'blue' : 'green'"
      class="avg"
    >
      <div class="avg--value">{{ cpuAvg }}%</div>
    </v-progress-circular>
  </v-card>
</template>

<script>
import ChartCpuLines from './ChartCpuLines'

export default {
  name: 'chart-cpu',
  components: {ChartCpuLines},
  methods: {
    setCpuAvg: function (avg) {
      this.cpuAvg = Math.round(avg * 100)
    }
  },
  data() {
    return {
      cpuAvg: 0
    }
  }
}
</script>

<style lang="scss" scoped>
.avg {
  position: absolute;
  right: 10px;
  top: 10px;

  &--value {
    color: white;
    position: relative;
    font-weight: 1600;
    font-size: 18px;
    text-shadow: 0 0 2px #000;
  }
}
</style>
