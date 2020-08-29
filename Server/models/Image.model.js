const mongoose = require('mongoose')
const Schema = mongoose.Schema

const ImageSchema = new Schema({
    user_email: {
        type: String,
        required: true
    },
    name: {
        type: String,
        required: true
    }
}, {
    timestamps: true
})

const Image = mongoose.model('Image', ImageSchema)

module.exports = Image