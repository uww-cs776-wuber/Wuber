const mongoose = require('mongoose')

const passengerSchema = new mongoose.Schema({
  email: {
    type: String,
    required: true
  },
  gpsCordinates: {
    type: String,
    required: true
  },
  destination: {
    type: String,
    required: true,
  },
  pickuptime:{
      type:String,
      required:true
  }

})

module.exports = mongoose.model('Passenger', passengerSchema)
