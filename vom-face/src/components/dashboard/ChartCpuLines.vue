<script>
import {mapGetters} from "vuex";

import {Line} from 'vue-chartjs'
import 'chartjs-plugin-streaming'

export default {
  name: 'chart-cpu-lines',
  extends: Line,
  computed: {
    ...mapGetters(
      'dashboard',
      {
        clients: 'clients',
        colors: 'colors',
        stats: 'cpuStats'
      }
    )
  },
  mounted() {
    this.renderChart(
      {
        datasets: [
          {
            backgroundColor: `rgba(${window.Color(this.colors[0]).values.rgb.join(',')}, .1)`,
            borderColor: this.colors[0],
            borderWidth: 1.5,
            lineTension: 0.1,
            data: this.stats[this.clients[0]]
          },
          {
            backgroundColor: `rgba(${window.Color(this.colors[1]).values.rgb.join(',')}, .1)`,
            borderColor: this.colors[1],
            borderWidth: 1.5,
            lineTension: 0.1,
            data: this.stats[this.clients[1]]
          }
        ]
      },
      {
        elements: {
          point: {
            radius: 0
          }
        },
        legend: {
          display: false
        },
        maintainAspectRatio: false,
        scales: {
          xAxes: [
            {
              display: false,
              type: 'time',
              realtime: {
                refresh: 1000,
                delay: 1000,
                onRefresh: chart => {
                  let total = 0

                  chart.data.datasets.forEach(dataset => {
                      const value = Math.random()
                      total += value
                      dataset.data.push({
                        x: Date.now(),
                        y: value
                      })
                    }
                  )

                  this.$emit('setCpuAvg', total / chart.data.datasets.length)
                }
              }
            }
          ],
          yAxes: [
            {
              display: false,
              ticks: {
                beginAtZero: true,
                suggestedMin: 0,
                suggestedMax: 1
              }
            }
          ]
        }
      }
    )
    ;
  }
}
</script>
<style scoped>
div:first-child {
  height: 100%;
}

canvas {
  height: 100% !important;
}
</style>
