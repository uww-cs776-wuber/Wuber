const express = require("express");
const app = express();
const mongoClient = require("mongodb").MongoClient;
const url = "mongodb://127.0.0.1:27017";

app.use(express.json()); // enable json parsing

mongoClient.connect(url, (err, db) => {
  if (err) {
    console.log("Error while connection mongoClient");
  } else {
    console.log("Connected with mongoClient");
    const rideShareDb = db.db("rideShareDb"); // This is the database
    const collection = rideShareDb.collection("user"); // This is the user table
    const locationTable = rideShareDb.collection("locationTable"); // This is the Table for storing everything sent by the passenger while requesting a ride
    const driverTable = rideShareDb.collection("driverTable"); // This is the driver table.

    app.post("/signup", (req, res) => {
      //signup route
      const newUser = {
        email: req.body.email,
        password: req.body.password
      };
      console.log(newUser);
      const duplicateUser = { email: newUser.email };
      // Find if the user already exists.
      collection.findOne(duplicateUser, (err, result) => {
        if (result == null) {
          collection.insertOne(newUser, (err, result) => {
            res.status(200).send(); // If new user is successfully added to the table then send response status 200
          });
        } else {
          res.status(400).send(); // If new user is not added to the table, send status response 400
        }
      });
    });

    app.post("/login", (req, res) => {
      //login route
      const currUser = {
        email: req.body.email,
        password: req.body.password
      };
      collection.findOne(currUser, (err, result) => {
        if (result != null) {
          const responseToClient = { email: result.email };
          res.status(200).send(JSON.stringify(responseToClient));
        } else {
          res.status(404).send();
        }
      });
    });

    //Route to handle user requests.
    app.post("/clientRequest", (req, res) => {
      var GPScordinates = req.body.gpsCordinates
        .replace("\t", "")
        .replace("\n", "")
        .replace("\t", "");

      const userRequest = {
        email: req.body.email,
        location: GPScordinates,
        destination: req.body.destination,
        pickuptime: req.body.pickuptime
      };
      console.log(userRequest.email);
      const updateRequest = {
        $set: {
          location: userRequest.location,
          destination: userRequest.destination,
          pickuptime: userRequest.pickuptime
        }
      };

      const email = { email: userRequest.email };
      const resToClient = {
        email: userRequest.email,
        gpsCordinates: userRequest.location,
        destination: userRequest.destination,
        pickuptime: userRequest.pickuptime
      };

      locationTable.findOne(email, (err, result) => {
        if (result == null) {
          locationTable.insertOne(userRequest, (err, result) => {
            console.log("user request added!");
            res.status(200).send(JSON.stringify(resToClient)); // Insert for user request
          });
        } else if (result != null) {
          locationTable.updateOne(email, updateRequest, (err, result) => {
            console.log("user request update!");
            console.log(resToClient);
            res.status(200).send(JSON.stringify(resToClient)); // Update the user request for user
          });
        } else {
          res.status(400).send();
        }
      });
    });
  }
});

app.listen(3000, () => {
  console.log("server is listening on port 3000");
});
