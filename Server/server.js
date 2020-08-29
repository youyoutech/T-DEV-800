var express = require('express')
var cors = require('cors')
var mongoose = require('mongoose')

var app = express()

app.use(express.json())
app.use(cors())

require('dotenv').config()

var port = process.env.PORT || 3000

const uri = "mongodb://127.0.0.1:27017/image_manager"

mongoose.connect(uri, { useNewUrlParser: true, useCreateIndex: true, useFindAndModify: false})
const connection = mongoose.connection
connection.once('open', () => {
    console.log("Connection to databese established")
})

var usersRouter = require('./routes/User')
var imagesRouter = require('./routes/Image')

app.use('/users', usersRouter)
app.use('/images', imagesRouter)

app.listen(port, () => {
    console.log(`Server is running on port: ${port}`)
})