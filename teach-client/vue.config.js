const isProduction = process.env.NODE_ENV === 'production';
module.exports = {
  configureWebpack(config) {
    // ...
    config.externals = {
      'BMap':'BMap' // 高德地图配置
    }
  },
  devServer: {
    hot: true,
    disableHostCheck: true,
    https: false,
    proxy: {
      '^/api':
      {
        target: 'http://localhost:9090',
        changeOrigin: true
      },

    },
  },
  //outputDir : '../teach/src/main/resources/public/',
  outputDir: 'C:\\teach\\teach-server\\src\\main\\resources\\public'
  //outputDir:'D:\\newTeach\\teach-server\\teach-server\\src\\main\\resources\\public'
}
