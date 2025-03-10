const { merge } = require("webpack-merge");
const baseConfig = require("./webpack.config.base");
const CssMinimizerPlugin = require("css-minimizer-webpack-plugin");
const TerserPlugin = require("terser-webpack-plugin");

module.exports = merge(baseConfig, {
  mode: "production",

  optimization: {
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