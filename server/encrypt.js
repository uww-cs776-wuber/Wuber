const crypto = require('crypto');
const algorithm = 'aes-256-cbc';
const key = "a4e1112f45e84f785358bb86ba750f48";
const iv = crypto.randomBytes(16);
//B@9907db3
function encrypt(text) {
 let cipher = crypto.createCipheriv('aes-256-cbc', Buffer.from(key), iv);
 let encrypted = cipher.update(text);
 encrypted = Buffer.concat([encrypted, cipher.final()]);
 return { iv: iv.toString('hex'), encryptedData: encrypted.toString('hex') };
}

function decrypt(text) {
 let iv = Buffer.from(text.iv, 'hex');
 let encryptedText = Buffer.from(text.encryptedData, 'hex');
 let decipher = crypto.createDecipheriv('aes-256-cbc', Buffer.from(key), iv);
 let decrypted = decipher.update(encryptedText);
 decrypted = Buffer.concat([decrypted, decipher.final()]);
 return decrypted.toString();
}

var hw = encrypt("Hello World")
console.log(hw)
console.log(decrypt(hw))
// function AES(){
// var decrypted = 'hello';
// var key = [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15];
// var cc = crypto.createCipher('aes-128-ecb', new Buffer(key));
// var encrypted = Buffer.concat([cc.update(decrypted, 'utf8'), cc.final()]).toString('base64');

// var encrypted = '[B@88f6407';
// var key = [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15];
// var cc = crypto.createDecipher('aes-128-ecb', new Buffer(key));
// var decrypted = Buffer.concat([cc.update(encrypted, 'base64'), cc.final()]).toString('utf8');

// console.log("Dec "+decrypted);
// console.log("Enc "+encrypted);
// }
// AES();