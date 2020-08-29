express = require('express')
const fs = require('fs')
const route = express.Router()
multer = require('multer')
upload = multer({dest: './uploads'})
cors = require('cors')
jwt = require('jsonwebtoken')
bcrypt = require('bcrypt')
var Storage = require('dom-storage')

let Image = require('../models/Image.model')
let User = require('../models/User.model')



// ROUTE DE LA CREATION DE LA PHOTO ET DU FICHIER ENCODEE EN BASE64
route.post('/create', upload.single('file'), (req, res) => {
    const imageData = {
        user_email: req.body.user_email,
        name: req.body.name
    }

    User.findOne({
        email: imageData.user_email
    })
    .then(user => {
        if(!user) {
            res.status(400).json({
                error: true,
                message: 'Image does not belong to a specified user!'
            })
        } else {
            Image.findOne({
                name: imageData.name,
                user_email: user.email
            })
            .then(image => {
                if(!image) {
                    Image.create(imageData)
                    .then(image => {
                        fs.readFile(req.file.path, 'base64', (err, data) => {
                            if (err)
                                return console.log(err)
                            base64_decode(data, './storage/' + imageData.user_email + '_' + imageData.name)
                            fs.unlinkSync(req.file.path)
                        })
                        res.json({
                            error: false,
                            message: imageData.name + ' is now stored :)' 
                        })
                    })
                    .catch(err => {
                        res.status(400).json({
                            error: true,
                            message: err
                        })
                    })
                } else {
                    res.status(400).json({
                        error: true,
                        message: 'The name '+ imageData.name +' already used :/'
                    })
                }
            })
            .catch(err => {
                res.status(400).json({
                    error: true,
                    message: err
                })
            })
        }
    })
    .catch(err => {
        res.status(400).json({
            error: true,
            message: err
        })
    })
})


// ROUTE D'ENVOIE DU FICHIER ENCODEE EN BASE64
route.get('/image', (req, res) => {

    user_email = req.query.user_email
    name = req.query.name


    User.findOne({
        email: user_email
    })
    .then(user => {
        if(!user) {
            res.status(400).json({
                error: true,
                message: 'The user does not exist!'
            })
        } else {
            Image.findOne({
                user_email: user_email,
                name: name
            })
            .then(image => {
                if(!image) {
                    res.status(400).json({
                        error: true,
                        message: 'The photo does not exist!'
                    })
                } else {
                    res.sendFile('./storage/' + user_email + '_' + name , { root: '.' })
                }
            })
            .catch(err => {
                res.status(400).json({
                    error: true,
                    message: err
                })
            })
        }
    })
    .catch(err => {
        res.status(400).json({
            error: true,
            message: err
        })
    })
})

route.put('/image', upload.none(), (req, res) => {
    user_email = req.body.user_email
    name = req.body.name
    new_name = req.body.new_name

    Image.findOneAndUpdate({
        user_email: user_email,
        name: name
    }, {
        name: new_name
    })
    .then(user => {
        fs.rename('./storage/' + user_email + '_' + name, './storage/' + user_email + '_' + new_name, err => {
            if(err) console.log('ERROR ' + err)
        })
        res.json({
            error: false,
            message: "Image has been updated!"
        })
    })
    .catch(err => {
        res.status(400).json({
            error: true,
            message: err
        })
    })
})

route.post("/delete", upload.none(), (req, res) => {
    user_email = req.query.user_email
    name = req.query.name

    Image.findOneAndDelete({
        user_email: user_email,
        name: name
    })
    .then(image => {
        fs.unlink('./storage/' + user_email + '_' + name, err => {
            if (err) console.log('ERROR' + err)
        })
        res.json({
            error: false,
            message: name + ' has been deleted successfully!'
        })
    })
    .catch(err => {
        res.status(400).json({
            error: true,
            message: err
        })
    })
})

function base64_decode(base64str, file) {
    // create buffer object from base64 encoded string, it is important to tell the constructor that the string is base64 encoded
    var bitmap = new Buffer(base64str, 'base64');
    // write buffer to file
    fs.writeFileSync(file, bitmap);
    console.log('******** File created from base64 encoded string ********');
} 


module.exports = route