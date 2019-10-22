"use strict"

const functions = require('firebase-functions');
const admin 	= require('firebase-admin');
admin.initializeApp();

//Notification Functions
exports.sendNotification = functions.database.ref('UsersData/{userID}/Notification/{messageID}').onCreate((snapshot, context) =>{
	const receiverID = context.params.userID;
	const messageID = context.params.messageID;
	const notificationData = snapshot.val();
	const notificationStatsCode = notificationData.notificationStatsCode;
	const notificationReference = notificationData.notificationReference;
	console.log('sendNotification Triggered for userID: '+receiverID+' with notification id: '+messageID);

	return admin.database().ref('UsersData/'+receiverID+'/UNToken').once('value').then(token =>{
		const userToken = token.val();

		const payload = {
			data:{
				id: messageID,
				stats: notificationStatsCode.toString(),
				reference: notificationReference
			}
		};

		const options = {
			priority: "high",
			TimeToLive: 60*60*24
		}
		return admin.messaging().sendToDevice(userToken, payload, options);
	});	
});

//getting user data
async function getUserData(userUID){
	console.log('requesting user data of: '+userUID);
	
	let db = admin.firestore().collection('Users').doc(userUID);
	let userData = db.get().then(doc=>{
		if (doc.exists) {
			const dataIs = doc.data();
			// console.log('doc found: '+Object.keys(dataIs));
			return dataIs;
		}else{
			return console.log('doc empty');
		}
	})
	.catch(error =>{
		console.log('error getting data: '+error);
		throw new functions.https.HttpsError('not-found','database error: ', 'user data not found :(')
	});
	return userData;
}

async function getTransactionDetails(transactionID){
	console.log('requesting transaction data of: '+transactionID);
	let transactionRef = admin.firestore().collection('Transactions').doc(transactionID);
	return transactionRef.get().then(doc=>{
		if (doc.exists) {
			const transactionDataGet = doc.data();
			return transactionDataGet; 
		}else{
			return console.log('doc empty');
		}
	})
}

//post notification to notification firebase database
async function postNotification(userID, notificationData){
	console.log('post notification!');
	var notificationFireRef 	= admin.database().ref('UsersData/'+userID+'/Notification/').push();
	var notificationID 			= notificationFireRef.key;
	console.log('post notification to user: '+userID+" with notificationID: "+notificationID);
	
	let notificationDataPost 	= {
		notificationID			: notificationID,
		notificationReference	: notificationData.notificationReference,
		notificationStatsCode	: notificationData.notificationStatsCode,
		notificationIsActive	: true,
		notificationTime		: admin.database.ServerValue.TIMESTAMP
	};
	let postNotificationtoDatab	= notificationFireRef.set(notificationDataPost).then((res =>{
		console.log('notification posted with id: '+notificationID);
		
		return res;
	}));
	return await postNotificationtoDatab;
}

//post transaction log to user
async function postUserTransactionLog(userID, transactionLogData){
	console.log('post transaction log to users!');

	let transactionID 			= transactionLogData.transactionID;
	let transactionStats		= transactionLogData.transactionStats;
	let transactionRefStats		= transactionLogData.transactionRefStats;
	let transactionChangeTime	= transactionLogData.transactionChangeTime;
	let transactionLog 			= {	
		transactionID 		: transactionID,
		transactionChangeTime 	: transactionChangeTime,
		transactionStats		: transactionStats,
		transactionRefStats		: transactionRefStats								
								};

	let transactionFireRefSource		= admin.firestore().collection('Users').doc(userID).collection('MyTransaction').doc(transactionID);
	return transactionFireRefSource.set(transactionLog).then(res =>{
		console.log('transactionLOG for id: '+userID+" with transactionID: "+transactionID+ " saved!!!");
		return res;
	});
}

//Direct trigger to request transaction (no transaction found yet! and straight complete)
exports.transactionRequest = functions.https.onCall(async (data, context)=>{
	if (!context.auth) {
		throw new functions.https.HttpsError("unauthenticated",'user not authenticated', 'check user');
	}

 	let transactionID 		= data.transactionID;
 	let transactionStats 	= data.transactionStats;
	let sourceID 			= data.sourceID;
	let targetID 			= data.targetID;
 	let transactionRef		= data.transactionRef;
	let transactionValue	= data.transactionValue;
	 
	let FieldValue 	= require('firebase-admin').firestore.FieldValue;

	let transactionData		= {	transactionID			: transactionID,
								transactionStats		: transactionStats,
								transactionRefStats		: 591,
								sourceID				: sourceID,
								targetID				: targetID,
								transactionRef			: transactionRef,
								transactionValue		: transactionValue,
								transactionChangeTime	: FieldValue.serverTimestamp(),
								transactionStartTime	: FieldValue.serverTimestamp()};
	
	console.log('transaction request from user ID: ',context.auth.uid,' //with transaction ID: ',transactionID);
	if (context.auth.uid !== sourceID) {
		console.log('transaction source and transaction requester id not matched');
		throw new functions.https.HttpsError('unknown','transaction not authenticated!!!')
		// return;
	}
	let userData = await getUserData(sourceID);	
	let userData2= await getUserData(targetID);
	console.log('userData: ',userData);
	console.log('transaction value: ',transactionValue);	
	
	if (userData.userBalance >= transactionValue) {
		console.log('user balance is sufficient');
		let transactionFireRef = admin.firestore().collection('Transactions').doc(transactionID);
		let saveTransactionData = transactionFireRef.set(transactionData).then(res=>{
			console.log('saving transaction data: ',res);
			let notificationData = {
				notificationStatsCode		: 411,
				notificationReference		: transactionRef
			};
			let totalBalanceSource 	= userData.userBalance-transactionValue;
			console.log('totBalSource: '+totalBalanceSource);
			let totalBalanceTarget 	= userData2.userBalance+transactionValue;
			console.log('totBalTarget: '+totalBalanceTarget);
			let postTransactionLog 	= postUserTransactionLog(sourceID, transactionData);
			let postTransactionLog2 = postUserTransactionLog(targetID, transactionData);
			// let postNotificationToSource = postNotification(sourceID, notificationData);
			let postNotificationToTarget = postNotification(targetID, notificationData);

			let transactionFireRefSource	= admin.firestore().collection('Users').doc(sourceID).update({userBalance: totalBalanceSource}).then(res=>{
				console.log('source balance updated!');
				return res;
			});
			let transactionFireRefTarget 	= admin.firestore().collection('Users').doc(targetID).update({userBalance: totalBalanceTarget}).then(res=>{
				console.log('target balance updated');
				return res;
			});
			
			return Promise.all([
				transactionFireRefSource,
				transactionFireRefTarget,
				postTransactionLog,postTransactionLog2,
				postNotificationToTarget
			]).then(result=>{
				return result;
			});	
		});
		return saveTransactionData;
		
	}else{
		console.log('balance insufficient to make transaction');
		// throw new functions.HttpsError('unknown','insufficient user balance')
		throw new functions.https.HttpsError('failed-precondition', 'insufficient balance','user balance insufficient, transaction cancelled');
	}
})

//make top up transaction
exports.newTopUpRequest = functions.https.onCall(async(data, context)=>{
	if (!context.auth) {
		throw new functions.https.HttpsError("unauthenticated",'user not authenticated', 'check user');
	}
	let transactionID 		= data.transactionID;
	let sourceID 			= data.sourceID;
 	let transactionRef		= data.transactionRef;
	let transactionValue	= data.transactionValue;

	let FieldValue 	= require('firebase-admin').firestore.FieldValue;
	let transactionData		= {	
		transactionID			: transactionID,
		transactionStats		: 511,
		transactionRefStats		: 590,
		sourceID				: sourceID,
		transactionRef			: transactionRef,
		transactionValue		: transactionValue,
		transactionChangeTime	: FieldValue.serverTimestamp(),
		transactionStartTime	: FieldValue.serverTimestamp()};
	
	console.log('Top-up Request from user ID: '+sourceID+' //with transaction ID: '+transactionID);
	let transactionFireRef = admin.firestore().collection('Transactions').doc(transactionID);
	let saveTransactionData = transactionFireRef.set(transactionData).then(res=>{
		console.log('saving transaction data! '+res);
		let postTransactionLog = postUserTransactionLog(sourceID, transactionData);
		return postTransactionLog;
	});
	return saveTransactionData;
})

//Accepting top up request
exports.acceptTopUpRequest = functions.https.onCall(async(data, context)=>{
	if (context.auth) {
		throw new functions.https.HttpsError("unauthenticated",'user not authenticated', 'check user');
	}
	let transactionID 		= data.transactionID;

	let FieldValue 			= require('firebase-admin').firestore.FieldValue;
	console.log('accepting transaction with ID: '+transactionID,' //accepted by ID: '+context.auth.uid);
	let transactionFireRef 		= admin.firestore().collection('Transactions').doc(transactionID);
	let saveTransactionData 	= transactionFireRef.update({transactionRefStats: 591, transactionChangeTime: FieldValue.serverTimestamp()}).then(res=>{
		let transactionDetails 	= getTransactionDetails(transactionID);
		let userDetails			= getUserData(transactionDetails.sourceID);
		let userFinalBalance	= userDetails.userBalance + transactionValue;
		let updateUser			= admin.firestore().collection('Users').doc(transactionDetails.sourceID).update({userBalance: userFinalBalance}).then(res=>{
			let postTransactionLog 		= postUserTransactionLog(transactionDetails);
			let notificationData		= {
				notificationStatsCode	: 422,
				notificationReference	: transactionID
			};
			let postNotificationToUser 	= postNotification(userDetails.userID, notificationData);
			return {res,postTransactionLog,postNotificationToUser};
		})
		console.log('saving transaction data! '+res);
		return updateUser;
	});
	return saveTransactionData;
	
})

