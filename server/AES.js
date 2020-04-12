var CryptoJS = require("crypto-js");
var encryptedBase64Key = "VXdXQFdhckhhd2tzMTg2OA==";
var parsedBase64Key = CryptoJS.enc.Base64.parse(encryptedBase64Key);
var encryptedData = null;
{
// Encryption process
var plaintText = "passenger@uww.edu";
// console.log( “plaintText = “ + plaintText );

// this is Base64-encoded encrypted data
encryptedData = CryptoJS.AES.encrypt(plaintText, parsedBase64Key, {
mode: CryptoJS.mode.ECB,
padding: CryptoJS.pad.Pkcs7
});
console.log( "encryptedData ="+ encryptedData.toString() );
}

{
// Decryption process
var encryptedCipherText = encryptedData ; // or encryptedData;
var decryptedData = CryptoJS.AES.decrypt( encryptedCipherText, parsedBase64Key, {
mode: CryptoJS.mode.ECB,
padding: CryptoJS.pad.Pkcs7
} );
// console.log( “DecryptedData = “ + decryptedData );

// this is the decrypted data as a string
var decryptedText = decryptedData.toString( CryptoJS.enc.Utf8 );
console.log( "DecryptedText = " + decryptedText );
}