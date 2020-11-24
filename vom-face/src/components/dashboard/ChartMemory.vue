<template>
  <v-card
    elevation="1"
    outlined
  >
    <v-card-title>
      Memory
      <v-icon>mdi-memory</v-icon>
    </v-card-title>
    <v-card-text>
      <canvas ref="canvas"/>
    </v-card-text>
  </v-card>
</template>

<script>
import {mapGetters} from "vuex";
import Chart from 'chart.js'

export default {
  mounted() {
    const {canvas} = this.$refs

    this.chart = new Chart(canvas, {
      type: 'horizontalBar',
      data: {
        labels: this.clients,
        datasets: [
          {
            label: 'A',
            backgroundColor: [
              `rgba(${window.Color(this.colors[0]).values.rgb.join(',')}, .1)`,
              `rgba(${window.Color(this.colors[1]).values.rgb.join(',')}, .1)`
            ],
            borderColor: [
              this.colors[0],
              this.colors[1]
            ],
            borderWidth: 1.5,
            data: this.stats
          },
          {
            label: 'B',
            backgroundColor: '#f5f5f5',
            borderColor: '#eee',
            borderWidth: 1.5,
            data: [1, 1]
          }
        ]
      },
      options: {
        tooltips: {
          enabled: false
        },
        indexAxis: 'y',
        legend: {
          display: false
        },
        elements: {
          bar: {
            borderWidth: 2,
          }
        },
        hover: false,
        scales: {
          xAxes: [{
            display: false,
            stacked: false,
            ticks: {
              suggestedMin: 0,
              suggestedMax: 1
            }
          }],
          yAxes: [{
            // display: false,
            stacked: true
          }]
        },
        responsive: true
      }
    })

    setInterval(function () {
      this.chart.data.datasets[0].data = [Math.random(), Math.random()]
      this.chart.update()
    }.bind(this), 1000)
  },
  computed: {
    ...mapGetters(
      'dashboard',
      {
        clients: 'clients',
        colors: 'colors',
        cpuStats: 'cpuStats'
      }
    )
  },
  data() {
    return {
      chart: null,
      stats: []
    }
  }
}
</script>
<style lang="scss" scoped>
.chartjs-size-monitor {
  height: 100%;

  canvas {
    height: 100% !important;
  }
}

</style>
