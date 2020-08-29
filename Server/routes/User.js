const express = require('express')
const route = express.Router()
const cors = require('cors')
const jwt = require('jsonwebtoken')
const bcrypt = require('bcrypt')
const multer = require('multer')
const upload = multer({dest: './uploads'})
var Storage = require('dom-storage')
const ls = new Storage();

let Image = require('../models/Image.model')
let User = require('../models/User.model')
const { findOne, findOneAndUpdate } = require('../models/Image.model')
const { json } = require('body-parser')

route.use(cors())

// ROUTE DE RECUPERATION DE TOUTES LES IMAGES ENREGISTREES DU USER
route.get('/gallery', upload.none(), (req, res) => {
    user_email = req.query.user_email
    gallery = ""

    Image.find({user_email: user_email}, {name: 1, _id: 0})
    .then(result => {
        result.forEach(element => {
            gallery += element.name + "\n"
        });
        res.json({
            error: false,
            message: gallery
        })
    })
    .catch(err => {
        res.status(400).json({
            error: true,
            message: err
        })
    })
})

// ROUTE DE CREATION DU COMPTE
route.post('/register', upload.none(), (req, res) => {
    const userData = {
        email: req.body.email,
        username: req.body.username,
        password: req.body.password,
        phone_number: req.body.phone_number
    }
    
    User.findOne({
        email: req.body.email
    })
    .then(user => {
        if(!user) {
            bcrypt.hash(req.body.password, 10, (err, hash) => {
                userData.password = hash
                User.create(userData)
                .then(user => res.json({
                    error: false,
                    message: user.username + ' is now registred!'
                }))
                .catch(err => res.status(400).json({
                    error: true,
                    message: err
                }))
            })
        }else {
            res.json({
                error: true,
                message: 'Email already exists!'
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

// ROUTE D'AUTHENTIFICATION
route.post('/login', upload.none(), (req, res) => {
    User.findOne({
        email: req.body.email
    })
     .then(user => {
         if(user) {
             if(bcrypt.compareSync(req.body.password, user.password)) {
                 const payload = {
                     id: user._id,
                     email: user.email,
                     username: user.username,
                     phone_number: user.phone_number
                 }
                 jwt.sign({ payload }, 'secretkey', { expiresIn: '12h' }, (err, token) => {
                    if (err) {
                        res.json({
                            error: true,
                            message: 'error while authentication'
                        });
                    } else {
                        ls.setItem('token', token);
                        res.json({
                            error: false,
                            message: token
                        });
                    }
                })
             }else{
                 res.json({
                     error: true,
                     message: "Wrong password"
                    })
             }
         }else{
             res.json({
                 error: true,
                 message: "User does not exist"
            })
         }
     })
     .catch(err => res.status(400).json({
         error: true,
         message: err
        }))
})

// ROUTE DU GET DU PROFIL
route.get("/profile", upload.none(), (req, res) => {
    user_email = req.query.user_email

    User.findOne({email: user_email})
    .then(user => {
        res.json({
            email: user.email,
            username: user.username,
            phone_number: user.phone_number
        })
    })
    .catch(err => {
        res.status(400).json(err)
    })
})

route.put("/profile", upload.none(), (req, res) => {
    const userInfo = {
        username: req.body.username,
        phone_number: req.body.phone_number
    }

    User.findOneAndUpdate({email: req.body.email}, userInfo)
    .then(user => {
        res.json({
            error: false,
            message: "User has been updated!"
        })
    })
    .catch(err => {
        json.status(400).json({
            error: true,
            message: err
        })
    })
})

module.exports = route