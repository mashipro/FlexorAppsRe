"use strict"

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

//Notification Functions
exports.sendNotification = functions.database.ref('UsersData/{userID}/Notification/{messageID}').onCreate((snapshot, context) =>{
	console.log('sendNotification Triggered')

	const receiverID = context.params.userID;
	console.log('receiverID: ',receiverID);

	const messageID = context.params.messageID;
	console.log('messageID: ', messageID);

	const notificationData = snapshot.val();
	console.log('notificationData: ', notificationData);

	const notificationStatsCode = notificationData.notificationStatsCode;
	console.log('notificationStatsCode: '+notificationStatsCode);

	const notificationReference = notificationData.notificationReference;
	console.log('notificationReference: ', notificationReference);

	return admin.database().ref('UsersData/'+receiverID+'/UNToken').once('value').then(token =>{
		const userToken = token.val();
		console.log('userToken: ', userToken);

		const payload = {
			data:{
				id: messageID,
				stats: notificationStatsCode.toString(),
				reference: notificationReference
			}
		};
		console.log('payload: ',payload);

		const options = {
			priority: "high",
			TimeToLive: 60*60*24
		}
		console.log('options: '+ options);

		return admin.messaging().sendToDevice(userToken, payload, options);
	});
	

	
});

// //Firestore Trigger box changes

// exports.saveUserEvent = functions.firestore
// 	.document('Boxes/{boXid}')
// 	.onUpdate((change, context) =>{
// 		const boxHeaderID = context.params.boXid;
// 		console.log('Box Changes detected on box ID: '+boxHeaderID);
		
// 		const newValue = change.after.data();
// 		const oldValue = change.before.data();
// 		console.log('Box Changes = '+ oldValue +' ///into >>> '+newValue);
// });

// //GoogleCalendar