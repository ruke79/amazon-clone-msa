const { merge } = require("webpack-merge");
const baseConfig = require("./webpack.config.base");

module.exports = merge(baseConfig, {
  mode: "development",
  devServer: { 
    host: "localhost",   
    port: 3000,
    open: true,
    historyApiFallback: true,    
    hot: true
  },
});