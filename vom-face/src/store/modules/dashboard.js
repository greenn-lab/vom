const state = {
  clients: ['DEV0', 'DEV1'],
  colors: ['red', 'orange', 'yellow', 'green', 'blue', 'indigo', 'violet', 'magenta'],
  cpu: {
    'DEV0': Array(30).fill(0).map((n, i) => ({
      x: Date.now() - (30 - i) * 1000,
      y: 0.7
    })),
    'DEV1': Array(30).fill(0).map((n, i) => ({
      x: Date.now() - (30 - i) * 1000,
      y: 0.7
    }))
  }
}

const mutations = {
  addCpuStats({clients, cpu}, {id, stats}) {
    if (!clients.includes(id)) {
      clients.push(id)
    }

    const fn = Array.isArray(stats) ? 'concat' : 'push'
    cpu[id][fn](stats)
  }
}

const actions = {
  addCpuStats({commit}, stats) {
    commit('addCpuStats', stats)
  }
}

const getters = {
  clients({clients}) {
    return clients
  },
  colors({colors}) {
    return colors
  },
  cpuStats({cpu}) {
    return cpu
  }
}


export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters
}
