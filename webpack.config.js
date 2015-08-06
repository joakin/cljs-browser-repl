var path = require('path')
var webpack = require('webpack')
var ExtractTextPlugin = require('extract-text-webpack-plugin')

var out = 'resources/public/'

module.exports = {
  entry: {
    app: [
      './src/cljs_browser_repl/ui/less/index.less'
    ]
  },
  output: {
    path: path.join(__dirname, out),
    filename: '[name].js'
  },
  module: {
    loaders: [
      { test: /\.(gif|png|jpg)$/, loader: 'url?limit=25000' },
      { test: /\.less$/, loader: ExtractTextPlugin.extract('style', 'css!autoprefixer!less') }
    ]
  },
  plugins: [
    new webpack.NoErrorsPlugin(),
    new ExtractTextPlugin('style.css', { allChunks: true }),
    new webpack.optimize.DedupePlugin()
  ]
}
