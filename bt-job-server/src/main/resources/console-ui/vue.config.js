module.exports = {
  publicPath: './',
  outputDir: '../static',
  assetsDir: 'assets',
  indexPath: 'index.html',
  filenameHashing: true,
  devServer: {
    proxy: {
      '/job': {
          target: 'http://127.0.0.1:8778',
          changeOrigin: true
      }
    }
  }
}
