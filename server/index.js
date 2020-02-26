var admin = require("firebase-admin");

var serviceAccount = require("X:/MastersInCSsem2/SoftwareEngineering/server/firebase-adminsdk.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://rideshare-10681.firebaseio.com"
});

var token
