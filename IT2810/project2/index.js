const express = require('express');
const app = express();
var path = require("path");
process.title = "project2";

app.use('/assets/style', express.static(path.join(__dirname, 'assets/style')));
app.use('/assets/images', express.static(path.join(__dirname, 'assets/images')));
app.use('/assets/js', express.static(path.join(__dirname, 'assets/js')));
app.use('/assets/fonts', express.static(path.join(__dirname, 'assets/fonts')));
app.use('/assets/content', express.static(path.join(__dirname, 'assets/content')));

app.get('/', function (req, res) {
  res.sendFile(path.join(__dirname + "/index.html" ));
});

app.listen(8082, function () {
  console.log('Server has started, listening on port 8082')
});