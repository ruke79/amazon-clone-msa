const webpack = require("webpack");
const HtmlWebPackPlugin = require("html-webpack-plugin");
const { merge } = require("webpack-merge");
const baseConfig = require("./webpack.config.base");
const CssMinimizerPlugin = require("css-minimizer-webpack-plugin");
const TerserPlugin = require("terser-webpack-plugin");
const dotenv = require('dotenv');


dotenv.config({path: './.env.production'})

module.exports = merge(baseConfig, {
  mode: "production",

  plugins: [
    new HtmlWebPackPlugin({
      template: './public/index.html'
    }),     
    new webpack.EnvironmentPlugin(["REACT_APP_API_URL", "REACT_APP_PROFILE", 
      "PUBLIC_URL", "REACT_APP_PAYPAL_CLIENT_ID", "REACT_APP_PAYPAL_CLIENT_SECRET" ]),
   
  ],

  optimization: {
    minimize: true,
    splitChunks: {},
    concatenateModules: true,
    minimizer: [           
      new CssMinimizerPlugin(),
      new TerserPlugin({
        terserOptions: {
          compress: {
            drop_console: true, // 콘솔 로그를 제거한다.
          },
        },
      }),
    ],
  },
});