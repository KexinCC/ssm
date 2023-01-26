const { defineConfig } = require('@vue/cli-service')
module.exports = {
  ...defineConfig({
    transpileDependencies: true,
    lintOnSave: false,
  }),
  devServer: {
    port: 80,
    proxy: 'http://192.168.1.101:8088/admin',
  }
}


