const { merge } = require("webpack-merge");
const baseConfig = require("./webpack.config.base");

module.exports = merge(baseConfig, {
  mode: "development",
  devServer: {     
    port: 3000,
    // proxy: [
    //   {
    //     context : ['/api'],
    //     target: 'http://localhost:8081', 
    //     changeOrigin: true, // cross origin 허용 설정
    //   },
    // ],
    open: true,
    historyApiFallback: true,
    hot: true,
    liveReload: false,     
  },
});