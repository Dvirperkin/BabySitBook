import * as functions from "firebase-functions";
import * as admin from "firebase-admin";
admin.initializeApp();


// Start writing Firebase Functions
// https://firebase.google.com/docs/functions/typescript

export const userCreate = functions.auth.user().onCreate((user, context) => {
  return admin.firestore().collection("new-users").doc(user.uid).set({
    email: user.email,
  });
});

