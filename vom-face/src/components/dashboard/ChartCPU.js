import {Line} from 'vue-chartjs'
import 'chartjs-plugin-streaming'
import {mapGetters} from "vuex";

export default {
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
        labels: this.stats[this.clients[0]].map(i => i.x),
        datasets: [
          {
            borderColor: this.colors[0],
            backgroundColor: 'transparent',
            lineTension: 0.1,
            data: this.stats[this.clients[0]]
          },
          {
            borderColor: this.colors[1],
            backgroundColor: 'transparent',
            lineTension: 0.1,
            data: this.stats[this.clients[1]]
          }
        ]
      },
      {
        legend: {
          display: false
        },
        elements: {
          point: {
            radius: 0
          }
        },
        scales: {
          xAxes: [
            {
              type: 'time',
              //display: false,
              realtime: {
                refresh: 2000,
                delay: 2000,
                onRefresh: function (chart) {
                  console.log(chart)
                  window.c = chart
                  chart.data.datasets.forEach(dataset =>
                    dataset.data.push({
                      x: Date.now(),
                      y: Math.random()
                    })
                  )
                }
              }
            }
          ],
          yAxes: [
            {
              //display: false,
              ticks: {
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
