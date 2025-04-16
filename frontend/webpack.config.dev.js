const webpack = require("webpack");
const HtmlWebPackPlugin = require("html-webpack-plugin");
const { merge } = require("webpack-merge");
const baseConfig = require("./webpack.config.base");
const dotenv = require('dotenv');
const path = require('path');
const fs = require("fs");

dotenv.config({path: './.env.development'})

module.exports = merge(baseConfig, {
  mode: "development",

  devServer: {     
    port: 443,    
    server : {
      type : 'https',
      options: {        
        key: fs.readFileSync(path.resolve(__dirname, 'mywebserv.site_202504149248A.key.pem')),
        cert: fs.readFileSync(path.resolve(__dirname, 'mywebserv.site_202504149248A.crt.pem')),                
      },    
    },
    static: {
      directory: path.join(__dirname, 'public'),
    },
    open: true,
    historyApiFallback: true,
    hot: true,
    liveReload: false,     
  },

  plugins: [
    new HtmlWebPackPlugin({
      template: './public/index.html'
    }),     
    new webpack.EnvironmentPlugin(["REACT_APP_API_URL", "REACT_APP_PROFILE", 
      "PUBLIC_URL", "REACT_APP_PAYPAL_CLIENT_ID", "REACT_APP_PAYPAL_CLIENT_SECRET" ]),
  ],
});