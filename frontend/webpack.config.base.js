const webpack = require("webpack");
const path = require("path");
const dotenv = require('dotenv');
const HtmlWebPackPlugin = require("html-webpack-plugin");
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const CssMinimizerPlugin = require("css-minimizer-webpack-plugin");


dotenv.config({path: './.env.development'})

module.exports = {
  mode: "development",
  entry: "./src/index.js",
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
        "pages": path.resolve(__dirname, './src/pages'),
        "util": path.resolve(__dirname, './src/util'),
        "store": path.resolve(__dirname, './src/store'),
        "public": path.resolve(__dirname, './public'),
        "error" : path.resolve(__dirname, './src/error'),
    },
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
  //new MiniCssExtractPlugin({ filename: 'app.css' }),
  //new MiniCssExtractPlugin(),    
  new webpack.DefinePlugin({
    'process.env': JSON.stringify(process.env),
  }),
  
  ],
  optimization: {  
    minimizer: [new CssMinimizerPlugin()],  
  },
};