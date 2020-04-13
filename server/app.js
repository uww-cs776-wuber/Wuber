const express = require("express");

const app = express();

const mongoClient = require("mongodb").MongoClient;

const url = "mongodb://127.0.0.1:27017";

var CryptoJS = require("crypto-js");

const encryptedBase64Key = "VXdXQFdhckhhd2tzMTg2OA=="; // Key

const parsedBase64Key = CryptoJS.enc.Base64.parse(encryptedBase64Key);
const bcrypt = require('bcrypt');



app.use(express.json()); // enable json parsing

//AES encryption
function encrypt(plaintText){
  var encryptedData = CryptoJS.AES.encrypt(plaintText, parsedBase64Key, {
    mode: CryptoJS.mode.ECB,
    padding: CryptoJS.pad.Pkcs7
  });
  console.log(encryptedData.toString(CryptoJS.enc.Utf8))
  return encryptedData.toString();
}

//AES decryption
function decrypt(cipherText) 
{
  var decryptedData = CryptoJS.AES.decrypt(cipherText, parsedBase64Key, {
    mode: CryptoJS.mode.ECB,
    padding: CryptoJS.pad.Pkcs7
  });
 
  var decryptedText = decryptedData.toString(CryptoJS.enc.Utf8);
  console.log("DecryptedText = " + decryptedText);
  return decryptedText;
}

//Database connection
mongoClient.connect(url, (err, db) => {
  if (err) {
    console.log("Error while connection mongoClient");
  } else {
    console.log("Connected with mongoClient");
    const rideShareDb = db.db("rideShareDb"); // This is the database
    const collection = rideShareDb.collection("user"); // This is the user table
    const locationTable = rideShareDb.collection("locationTable"); // This is the Table for storing everything sent by the passenger while requesting a ride
    const rideService = rideShareDb.collection("rideService"); // This table stores all the passenger requests accepted by drivers.

    app.post("/signup", (req, res) => {
      //signup route
      const password=decrypt(req.body.password);
      let hashPassword = bcrypt.hashSync(password, 10);
      const newUser = {
        email: decrypt(req.body.email),
        password: hashPassword,
        userType: decrypt(req.body.userType)
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
      const decryptedPassword=decrypt(req.body.password);
      const hashedPassword=bcrypt.hashSync(decryptedPassword, 10);
      console.log(decryptedPassword+" "+hashedPassword);
      const currUser = {
        email: decrypt(req.body.email),
        password: hashedPassword,
        userType: decrypt(req.body.userType)
      };
      collection.findOne({email:currUser.email}, (err, result) => {      
        if (result != null && bcrypt.compareSync(decryptedPassword,result.password)) { //check if user email and hashed password matchess 
          const responseToClient = {
            email: encrypt(result.email),
            userType: encrypt(result.userType)
          };
          console.log(JSON.stringify(responseToClient));
          res.status(200).send(JSON.stringify(responseToClient));
        }
         else {
          res.status(404).send();
        }
      });
    });

    //Route to handle user requests.
    app.post("/clientRequest", (req, res) => {
      const userRequest={
        email:decrypt(req.body.email),
        gpsCordinates: decrypt(req.body.gpsCordinates),
        destination: decrypt(req.body.destination),
        pickuptime: decrypt(req.body.pickuptime)
      }
       console.log(userRequest.email);
      const updateRequest = {
        $set: {
          gpsCordinates: userRequest.gpsCordinates,
          destination: userRequest.destination,
          pickuptime: userRequest.pickuptime
        }
      };

      const email = { email: userRequest.email };
      const resToClient = {
        email: userRequest.email,
        gpsCordinates: userRequest.gpsCordinates,
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

    app.post("/takePassenger", (req, res) => {
      // Route to post the information of the passenger whose request has been accepted by a driver.
      var GPScordinates = req.body.gpsCordinates
        .replace("\t", "")
        .replace("\n", "")
        .replace("\t", "");

      const userRequest = {
        email: req.body.email,
        gpsCordinates: GPScordinates,
        destination: req.body.destination,
        pickuptime: req.body.pickuptime,
        driver: req.body.driver,
        driverLocation: req.body.driverLocation,
        arrived: req.body.arrived
      };
      console.log(userRequest);
      const updateRequest = {
        $set: {
          gpsCordinates: userRequest.gpsCordinates,
          destination: userRequest.destination,
          pickuptime: userRequest.pickuptime,
          driver: req.body.driver,
          driverLocation: req.body.driverLocation,
          arrived: req.body.arrived
        }
      };

      const email = { email: userRequest.email };

      rideService.findOne(email, (err, result) => {
        if (result == null) {
          rideService.insertOne(userRequest, (err, result) => {
            console.log("user request added!");
            res.status(200).send(JSON.stringify("Ride in progress")); // Insert for user request
          });
        } else if (result != null) {
          rideService.updateOne(email, updateRequest, (err, result) => {
            console.log("user request update!");
            console.log(userRequest);
            res.status(200).send(JSON.stringify(userRequest)); // Update the user request for user
          });
        } else {
          res.status(400).send();
        }
      });
    });

    app.delete("/closeClientRequest/:email", (req, res) => {
      // Route to delete the request of the passenger by the driver after pickup is done.
      const email = { email: req.params.email };
      rideService.deleteOne(email, function (err, obj) {
        if (err) throw err;
        console.log("1 document deleted");
        res.status(200).send();
      });
      rideService.deleteOne(email, function (err, obj) {
        if (err) throw err;
        console.log("1 document deleted");
        res.status(200).send();
      });
    });

    app.delete("/addClientRequest/:email", (req, res) => {
      // Route to delete the request of the passenger by the driver after pickup is done.
      const email = { email: req.params.email };
      locationTable.deleteOne(email, function (err, obj) {
        if (err) throw err;
        console.log("1 document deleted");
        res.status(200).send();
      });
      rideService.deleteOne(email, function (err, obj) {
        if (err) throw err;
        console.log("1 document deleted");
        res.status(200).send();
      });
    });

    app.get("/driverNotify", (req, res) => {
      //Route to get all the client request in an array.
      locationTable.find({}).toArray(function (err, result) {
        if (err) throw err;
        console.log(result);
        res.status(200).send(JSON.stringify(result));
      });
    });

    app.get("/passengerNotify/:email", (req, res) => {
      // Route to get the driver name and location who has accepted the passenger request.
      const email = { email: req.params.email }
      rideService.findOne(email, (err, result) => {
        if (result != null) {
          const responseToClient = {
            email: result.email,
            gpsCordinates: result.gpsCordinates,
            destination: result.destination,
            pickuptime: result.pickuptime,
            driver: result.driver,
            driverLocation: result.driverLocation,
            arrived: result.arrived
          };
          console.log(result)
          res.status(200).send(JSON.stringify(responseToClient));
        } else {
          res.status(404).send();
        }
      });
    });

    app.get("/pickupInProgress/:driver", (req, res) => {
      // Route to get only the  ride requests in progess.
      rideService.find({ driver: req.params.driver }).toArray(function (err, result) {
        if (err) throw err;
        console.log(result);
        console.log("Ride in Progress")
        res.status(200).send(JSON.stringify(result));
      });
    });
  }
});

app.listen(3000, () => {
  console.log("server is listening on port 3000");
});
