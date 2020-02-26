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

    app.post("/signup",(req, res) => { //signup route
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
      }
    );


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

    app.post("/clientlocate", (req, res) => {
      var GPScordinates = req.body.location
        .replace("\t", "")
        .replace("\n", "")
        .replace("\t", "");

      const userLocation = {
        email: req.body.email,
        location: GPScordinates
      };
      console.log(userLocation.email);
      const newLocation = { $set: { location: userLocation.location } };
      const email = { email: userLocation.email };
      const resToClient = {
        email: userLocation.email,
        gpsCordinates: userLocation.location
      };
      locationTable.findOne(email, (err, result) => {
        if (result == null) {
          locationTable.insertOne(userLocation, (err, result) => {
            console.log("location added!");
            res.status(200).send(JSON.stringify(resToClient)); // Insert location for user
          });
        } else if (result != null) {
          locationTable.updateOne(email, newLocation, (err, result) => {
            console.log("location update!");
            console.log(resToClient);
            res.status(200).send(JSON.stringify(resToClient)); // Update location for user
          });
        } else {
          res.status(400).send();
        }
      });
    });

    app.post("/destination", (req, res) => {
      const userDestination = {
        email: req.body.email,
        destination: req.body.destination
      };
      const newDestination = {
        $set: { destination: userDestination.destination }
      };
      const email = { email: userDestination.email };
      const resToClient = {
        email: userDestination.email,
        destination: userDestination.destination
      };

      locationTable.findOne(email, (err, result) => {
        if (result == null) {
          locationTable.insertOne(userDestination, (err, result) => {
            console.log("destination added!");
            res.status(200).send(JSON.stringify(resToClient)); // Insert destination for user
          });
        } else if (result != null) {
          locationTable.updateOne(email, newDestination, (err, result) => {
            console.log("new destination update!");
            console.log(resToClient);
            res.status(200).send(JSON.stringify(resToClient)); // Update destination for user
          });
        } else {
          res.status(400).send();
        }
      });
    });

    app.post("/pickuptime", (req, res) => {
      const userPickupTime = {
        email: req.body.email,
        pickuptime: req.body.pickuptime
      };
      const newPickupTime = { $set: { pickuptime: userPickupTime.pickuptime } };
      const email = { email: userPickupTime.email };
      const resToClient = {
        email: userPickupTime.email,
        pickuptime: userPickupTime.pickuptime
      };

      locationTable.findOne(email, (err, result) => {
        if (result == null) {
          locationTable.insertOne(userPickupTime, (err, result) => {
            console.log("Pickup Time added!");
            res.status(200).send(JSON.stringify(resToClient)); // Insert pickuptime for user
          });
        } else if (result != null) {
          locationTable.updateOne(email, newPickupTime, (err, result) => {
            console.log("new pickup time update!");
            console.log(resToClient);
            res.status(200).send(JSON.stringify(resToClient)); // Update pickup time for user
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
