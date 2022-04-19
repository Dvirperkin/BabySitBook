import * as functions from "firebase-functions";
import * as admin from "firebase-admin";

admin.initializeApp();

const firestore = admin.firestore();

// Start writing Firebase Functions
// https://firebase.google.com/docs/functions/typescript

export const userCreate = functions.auth.user().onCreate((user, context) => {
  return firestore.collection("new-users").doc(user.uid).set({
    email: user.email,
  });
});

export const isNewUser = functions.https.onCall((async (data, context) => {
  return firestore.collection("new-users").doc(data.uid).get()
      .then((doc) => {
        if (doc.exists) {
          return {
            "isNewUser": true,
          };
        } else {
          return {
            "isNewUser": false,
          };
        }
      });
}));

export const updateNewUser = functions.https.onCall((async (data, context) => {
  await firestore.collection(data.profile).doc(data.uid).set(data);
  await firestore.collection("new-users").doc(data.uid).delete();
}));

export const createEvent = functions.https.onCall((async (data, context) => {
  const date = data.event.date.replace(/\//gi, "-");
  return firestore.collection("calendar").doc(data.uid)
      .collection("events").doc(date + data.event.title).get()
      .then((doc) => {
        if (doc.exists) {
          return firestore.collection("calendar").doc(data.uid)
              .collection("events").doc(date + data.event.title)
              .update(data.event);
        } else {
          return firestore.collection("calendar").doc(data.uid)
              .collection("events").doc(date + data.event.title)
              .set(data.event);
        }
      });
}));

// eslint-disable-next-line max-len
export const isEventTitleExists = functions.https.onCall((async (data, context) => {
  const eventId = data.date.replace(/\//gi, "-") + data.title;
  return firestore.collection("calendar").doc(data.uid)
      .collection("events").doc(eventId).get()
      .then((doc) => {
        if (doc.exists) {
          return {
            "isEventTitleExists": true,
          };
        } else {
          return {
            "isEventTitleExists": false,
          };
        }
      });
}));

export const deleteEvent = functions.https.onCall((async (data, context) => {
  const eventId = data.date.replace(/\//gi, "-") + data.title;
  await firestore.collection("calendar").doc(data.uid)
      .collection("events").doc(eventId).delete();
}));
