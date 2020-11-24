<template>
  <canvas ref="troves"></canvas>
</template>

<script>
import Chart from 'chart.js'

export default {
  name: 'chart-troves',
  mounted() {
    const canvas = this.$refs.troves

    const xlog = new Chart(canvas, {
      type: 'scatter',
      data: {
        datasets: [{
          borderColor: 'rgb(0, 0, 0)',
          data: (
            Array(120).fill({}).map((v, i) => ({
              x: Date.now() / 1000 - 120 - i,
              y: Math.random() * 10
            }))
          ),
        }]
      },
      options: {
        legend: {
          display: false
        },
        elements: {
          point: {pointStyle: 'cross', rotation: 45}
        },
        tooltips: {
          // enabled: false
        },
        layout: {
          padding: {
            right: 15
          }
        },
        scales: {
          xAxes: [
            {
              gridLines: {
                color: 'rgba(200,200,200,.2)'
              },
              ticks: {
                type: 'linear',
                fontColor: '#ccc',
                maxRotation: 0,
                minRotation: 0,
                max: Date.now() / 1000,
                min: Date.now() / 1000 - 120,
                autoSkip: false,
                stepSize: 4,
                sampleSize: 20,
                callback: (value, index) => {
                  return value % 20 !== 0 ? '' : new Date(value * 1000).format('mm:ss')
                }
              }
            }
          ],
          yAxes: [
            {
              gridLines: {
                color: 'rgba(200,200,200,.2)'
              },
              ticks: {
                max: 10,
                min: 0
              }
            }
          ]
        }
      }
    })

    window.x = xlog

    setInterval(() => {
      if (xlog.paused) return

      const data = xlog.data.datasets[0].data
      const max = Date.now() / 1000
      const min = max - 120

      data.push({
        x: max,
        y: Math.random() * 10
      })

      while (data[0].x < min) {
        data.shift()
      }


      xlog.options.scales.xAxes[0].ticks.min = min
      xlog.options.scales.xAxes[0].ticks.max = max
      xlog.update()
    }, 1000)
  }
}
</script>
