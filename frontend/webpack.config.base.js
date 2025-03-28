const path = require("path");



module.exports = {  
  infrastructureLogging: {
    level: 'info',
 },  
  entry: "./src/index.jsx",
  output: {     
    publicPath: "/",   
	  path: path.resolve(__dirname, "dist"),
	  filename: '[name].bundle.js',
    clean: true,
  },  
  resolve: {
    alias: {
        "components": path.resolve(__dirname, './src/components'),
        "pages": path.resolve(__dirname, './src/pages'),        
        "util": path.resolve(__dirname, './src/util'),
        "reduxs": path.resolve(__dirname, './src/redux'),
        "store": path.resolve(__dirname, './src/store'),
        "hook": path.resolve(__dirname, './src/hook'),        
        "constants": path.resolve(__dirname, './src/constants'),        
        "error" : path.resolve(__dirname, './src/error'),
        "assets" : path.resolve(__dirname, './src/assets'),
    },
    extensions: ['.js', '.jsx'],
  },
  module: {    

    rules: [
    
      {
        test: /\.(js|jsx)$/,
        exclude: ["/node_modules"],
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
        exclude: ["/node_modules"],
        use: ["style-loader", "css-loader", "postcss-loader"],
        
      },
      {
        test: /\.(jpg|jpeg|gif|png|svg|ico)?$/,
        exclude: ["/node_modules"],          
        type : "asset/resource",
        generator: {
          filename: "assets/images/[name].[ext]"
        }
      }
    ],
  },  
  
};