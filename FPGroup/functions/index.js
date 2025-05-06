const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

// Helper function to send FCM notification
async function sendNotification(token, title, body, type) {
  const message = {
    notification: {
      title: title,
      body: body,
    },
    data: {
      type: type,
    },
    token: token,
  };

  try {
    const response = await admin.messaging().send(message);
    console.log('Successfully sent message:', response);
  } catch (error) {
    console.log('Error sending message:', error);
  }
}

// Trigger when a job is saved
exports.onJobSaved = functions.firestore
  .document('savedJobs/{docId}')
  .onCreate(async (snap, context) => {
    const data = snap.data();
    const userId = data.userId;

    const userDoc = await admin.firestore().collection('users').doc(userId).get();
    const fcmToken = userDoc.data().fcmToken;

    if (fcmToken) {
      await sendNotification(fcmToken, 'Job Saved', `You saved ${data.jobTitle} at ${data.jobCompany}.`, 'job_saved');
    }
  });

// Trigger when an application is submitted
exports.onApplicationSubmitted = functions.firestore
  .document('applications/{docId}')
  .onCreate(async (snap, context) => {
    const data = snap.data();
    const userId = data.userId;

    const userDoc = await admin.firestore().collection('users').doc(userId).get();
    const fcmToken = userDoc.data().fcmToken;

    if (fcmToken) {
      await sendNotification(fcmToken, 'Application Submitted', `You applied to ${data.jobTitle} at ${data.jobCompany}.`, 'application_submitted');
    }
  });

// Trigger when a job is updated
exports.onJobUpdated = functions.firestore
  .document('jobs/{jobId}')
  .onUpdate(async (change, context) => {
    const newData = change.after.data();
    const jobTitle = newData.title;

    // Assuming you have a list of user IDs to notify
    const usersSnapshot = await admin.firestore().collection('users').get();

    usersSnapshot.forEach(async (userDoc) => {
      const fcmToken = userDoc.data().fcmToken;
      if (fcmToken) {
        await sendNotification(fcmToken, 'Job Updated', `${jobTitle} has new updates.`, 'job_updated');
      }
    });
  });
