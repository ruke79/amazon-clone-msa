const webpack = require("webpack");
const path = require("path");
const dotenv = require('dotenv');
const HtmlWebPackPlugin = require("html-webpack-plugin");


dotenv.config({path: './.env.development'})

module.exports = {
  infrastructureLogging: {
    level: 'verbose',
 },
  mode: "development",
  entry: "./src/index.jsx",
  output: {
    publicPath: '/',
	  path: path.resolve(__dirname, "dist"),
	filename: '[name].bundle.js',
  clean: true,
  },  
  resolve: {
    alias: {
        "components": path.resolve(__dirname, './src/components'),
        "pages": path.resolve(__dirname, './src/pages'),        
        "util": path.resolve(__dirname, './src/util'),
        "store": path.resolve(__dirname, './src/store'),
        "hook": path.resolve(__dirname, './src/hook'),        
        "constants": path.resolve(__dirname, './src/constants'),
        "public": path.resolve(__dirname, './public'),
        "error" : path.resolve(__dirname, './src/error'),
    },
    extensions: ['.js', '.jsx'],
  },
  module: {    

    rules: [
      {
        test: /\.(js|jsx)$/,
        exclude: "/node_modules",
        use: {
          loader: 'babel-loader',
          options:{
            presets: [
              "@babel/preset-env", ["@babel/preset-react", {"runtime": "automatic"}]
            ]
          }          
        },
      },
      

      {
        test: /\.css$/,
        use: ["style-loader", "css-loader", "postcss-loader"],
        
      },
      {
        test: /\.(jpg|jpeg|gif|png|svg|ico)?$/,
        exclude: /node_modules/,          
        use: [
          {
            loader: "url-loader",
            options: {
              limit: 10000,
              fallback: "file-loader",
              name: "image/[name].[ext]"
            }
          }
          
        ]
      }
    ],
  },
  plugins: [
	new HtmlWebPackPlugin({
	  template: './public/index.html'
	}), 
  
  new webpack.DefinePlugin({
    'process.env': JSON.stringify(process.env),
  }),
  
  ],
  
};