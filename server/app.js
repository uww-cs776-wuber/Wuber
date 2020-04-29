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


        app.post("/signup", (req, res) => {
            //signup route
            const newUser = {
                email: req.body.email,
                password: req.body.password,
                userType: req.body.userType
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
                password: req.body.password,
                userType: req.body.userType
            };
            collection.findOne(currUser, (err, result) => {
                if (result != null) {
                    const responseToClient = { email: result.email, userType: req.body.userType };
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
                gpsCordinates: GPScordinates,
                destination: req.body.destination,
                pickuptime: req.body.pickuptime,
                usesWheelchair: req.body.usesWheelchair

            };
            console.log(userRequest.email);
            const updateRequest = {
                $set: {
                    gpsCordinates: userRequest.gpsCordinates,
                    destination: userRequest.destination,
                    pickuptime: userRequest.pickuptime,
                    usesWheelchair: userRequest.usesWheelchair
                }
            };

            const email = { email: userRequest.email };
            const resToClient = {
                email: userRequest.email,
                gpsCordinates: userRequest.gpsCordinates,
                destination: userRequest.destination,
                pickuptime: userRequest.pickuptime,
                usesWheelchair: userRequest.usesWheelchair
            };
            locationTable.findOne(email, (err, result) => {
                if (result == null) {
                    locationTable.insertOne(userRequest, (err, result) => {
                        console.log("user request added!");
                        console.log(JSON.stringify(resToClient));
                        res.status(200).send(JSON.stringify(resToClient)); // Insert for user request
                    });
                } else if (result != null) {
                    locationTable.updateOne(email, updateRequest, (err, result) => {
                        console.log("user request update!");
                        console.log(JSON.stringify(resToClient));
                        res.status(200).send(JSON.stringify(resToClient)); // Update the user request for user
                    });
                } else {
                    res.status(400).send();
                }
            });
        });

        app.delete("/closeClientRequest/:email", (req, res) => { // delete the request of the client by the driver after pickup is done.
            const email = { email: req.params.email }
            locationTable.deleteOne(email, function (err, obj) {
                if (err) throw err;
                console.log("1 document deleted");
                res.status(200).send();
            });
        });

        app.get("/driverNotify", (req, res) => { // get all the client request in an array. 
            locationTable.find({}).toArray(function (err, result) {
                if (err) throw err;
                console.log(result);
                res.status(200).send(JSON.stringify(result))

            });
        });
    }
});

app.listen(3000, () => {
    console.log("server is listening on port 3000");
});
