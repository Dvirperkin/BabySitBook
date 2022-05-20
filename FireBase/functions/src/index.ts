import * as functions from "firebase-functions";
import * as admin from "firebase-admin";
/* eslint-disable */
admin.initializeApp();

const firestore = admin.firestore();

// Start writing Firebase Functions
// https://firebase.google.com/docs/functions/typescript

const getUidFromEmail = async (email: string) => {
  const doc = await firestore.collection("Users")
      .where("email", "==", email)
      .get();

  if (!doc.empty) {
    return doc.docs[0].get("uid").toString();
  } else {
    return null;
  }
};

const getUidData = async (uid: string) => {
    const doc = await firestore.collection("Users")
        .where("uid", "==", uid)
        .get();

    if (!doc.empty) {
        return {
            "displayName": doc.docs[0].get("displayName"),
            "email": doc.docs[0].get("email"),
            "image": doc.docs[0].get("image"),
            "uid": doc.docs[0].get("uid").toString(),
            "gender": doc.docs[0].get("gender"),
        }
    } else {
        return null;
    }
};

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
export const isBabysitter = functions.https.onCall((async (data, context) => {
  return firestore.collection("Users").doc(data.uid).get()
      .then((doc) => {
        console.log(doc.get("profile"));
        if (doc.exists && doc.get("profile") === "Babysitter") {
          return {
            "isBabysitter": true,
          };
        } else {
          return {
            "isBabysitter": false,
          };
        }
      });
}));
export const getProfileData = functions.https.onCall((async (data, context) => {
  return firestore.collection("Users").where("email", "==", data.email).get()
      .then((doc) => {
        if (!doc.empty) {
            if(doc.docs[0].get("profile") == "Babysitter"){
              return {
                  "displayName": doc.docs[0].get("displayName"),
                  "image": doc.docs[0].get("image"),
                  "gender": doc.docs[0].get("gender"),
                  "birthdate": doc.docs[0].get("birthdate"),
                  "city": doc.docs[0].get("city"),
                  "experience": doc.docs[0].get("experience"),
                  "hourlyRate": doc.docs[0].get("hourlyRate"),
                  "mobility": doc.docs[0].get("mobility"),
                  "description": doc.docs[0].get("description"),
                  "likes": Object.keys(doc.docs[0].get("likes")).length.toString(),
                  "NoUser": false

              };
            }
            else {
                return {
                    "displayName": doc.docs[0].get("displayName"),
                    "image": doc.docs[0].get("image"),
                    "city": doc.docs[0].get("city"),
                    "children": doc.docs[0].get("children"),
                    "description": doc.docs[0].get("description"),
                    "NoUser": false
                };
            }
        } else {
          return {
            "NoUser": true,
          };
        }
      });
}));

// eslint-disable-next-line max-len
export const updateProfileDetails = functions.https.onCall((async (data, context) => {
  return firestore.collection("Users").doc(data.uid).get()
      .then((doc) => {
        if (doc.exists) {
          firestore.collection("Users").doc(data.uid).update(data);
        }
      });
}));
export const updateNewUser = functions.https.onCall((async (data, context) => {
  await firestore.collection("Users").doc(data.uid).set(data);
  await firestore.collection("new-users").doc(data.uid).delete();
}));

export const otherUserType = functions.https.onCall( (data, context) => {
  return firestore.collection("Users").where("email", "==", data.email).get()
      .then((doc) => {
        if (!doc.empty) {
          return {
            "profile": doc.docs[0].get("profile"),
          };
        }
        // Never happened
        return {
          "profile": "None"};
      });
});
// eslint-disable-next-line max-len
export const checkRelationShip = functions.https.onCall(async (data, context) => {
  const userUid = context.auth?.uid;
  const otherUid = await getUidFromEmail(data.email);

  if (userUid == undefined) {
    return {
      "error": "You don't have permission to access this service",
    };
  }
  if (otherUid == undefined) {
    return {
      "error": "No such user",
    };
  }

  return firestore.collection("Users")
      .doc(userUid)
      .collection("Friends")
      .doc(otherUid)
      .get()
      .then((doc) => {
        if (doc.exists) {
          if (doc.get("relation") == "friends") {
            return {
              "relation": "friends",
            };
          } else {
            return {
              "relation": "pending",
            };
          }
        } else {
          return {
            "relation": "not friends",
          };
        }
      }
      );
});

// eslint-disable-next-line max-len
export const sendFriendRequest = functions.https.onCall(async (data, context) => {
  const otherUid = await getUidFromEmail(data.email);

  if (context.auth?.uid != null) {
    await firestore.collection("Users")
        .doc(context.auth?.uid)
        .collection("Friends")
        .doc(otherUid).set({
          "relation": "pending",
        });
  }
  return firestore.collection("Users").doc(otherUid)
      .collection("Notifications").add({
        email: context.auth?.token.email,
        text: context.auth?.token.email + "sent you a friend request",
      });
});

export const likeFriend = functions.https.onCall(async (data, context) => {
    if (context.auth?.uid == null) {
        return {
            "error": "You don't have permission to access this service",
        };
    }
    const otherUid = await getUidFromEmail(data.email);
    let added = false
        return firestore.collection("Users")
            .doc(otherUid)
            .get()
            .then(document => {
                if (document.exists) {
                    let likes = new Map(Object.entries(document.get("likes")))
                    if (!likes.has(<string>context.auth?.uid)) {
                        likes.set(<string>context.auth?.uid, "")
                        added = true
                    } else {
                        likes.delete(<string>context.auth?.uid)
                    }
                    firestore.collection("Users").doc(otherUid).update({"likes": Object.fromEntries(likes)})
                    return {
                        "added": added
                    }
                } else {
                    console.log("yaniv")
                    return
                }
            })
});


// eslint-disable-next-line max-len
export const acceptFriendRequest = functions.https.onCall(async (data, context) => {
  let myUid: string;
  if (context.auth?.uid != null) {
    myUid = context.auth?.uid;
  } else {
    return {
      "error": "You don't have permission to access this service",
    };
  }

  const otherUid = await getUidFromEmail(data.email);

  const myData = await getUidData(myUid);
  const otherData = await getUidData(otherUid);

  await firestore.collection("Users")
      .doc(myUid)
      .collection("Friends")
      .doc(otherUid).set({
          "displayName": otherData?.displayName,
          "email": otherData?.email,
          "image": otherData?.image,
          "relation": "friends",
          }
      );
  await firestore.collection("Users")
      .doc(otherUid)
      .collection("Friends")
      .doc(context.auth?.uid).set({
          "displayName": myData?.displayName,
          "email": myData?.email,
          "image": myData?.image,
          "relation": "friends",
      });

  await firestore.collection("Users")
      .doc(myUid)
      .collection("Notifications")
      .where("email", "==", data.email).get()
      .then((doc) => {
        firestore.collection("Users")
            .doc(myUid)
            .collection("Notifications")
            .doc(doc.docs[0].id).delete();
      });
  return;
});

// eslint-disable-next-line max-len
export const ignoreFriendRequest = functions.https.onCall(async (data, context) => {
  const otherUid = await getUidFromEmail(data.email);

  if (context.auth?.uid != null) {
    await firestore.collection("Users")
        .doc(otherUid)
        .collection("Friends")
        .doc(context.auth?.uid).delete();
  }
});

export const deleteFriend = functions.https.onCall(async (data, context) => {
  let myUid: string;
  if (context.auth?.uid != null) {
    myUid = context.auth?.uid;
  } else {
    return {
      "error": "You don't have permission to access this service",
    };
  }
  const otherUid = await getUidFromEmail(data.email);

  await firestore.collection("Users")
      .doc(myUid)
      .collection("Friends")
      .doc(otherUid).delete();
  await firestore.collection("Users")
      .doc(otherUid)
      .collection("Friends")
      .doc(context.auth?.uid).delete();

  return;
});

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

export const charge = functions.https.onCall((async (data, context) => {
  const date = data.date.replace(/\//gi, "");
  await firestore.collection("bills")
      .doc(data.uid + date + data.startTime).set(data);
}));

export const deleteBill = functions.https.onCall((async (data, context) => {
  const date = data.date.replace(/\//gi, "");
  await firestore.collection("bills")
      .doc(data.uid + date + data.startTime).delete();
}));

export const createNewChat = functions.https.onCall(async (data, context) => {
    let myUid: string;
    if (context.auth?.uid != null) {
        myUid = context.auth?.uid;
    } else {
        return {
            "error": "You don't have permission to access this service",
        };
    }

    const otherUid = await getUidFromEmail(data.email);

    const myData = await getUidData(myUid);
    const otherData = await getUidData(otherUid);

    let docRef = await firestore.collection("Chats").add({
            "contacts": {
                "1": myUid,
                "2": otherUid
            }
        }
    )
    await firestore.collection("Users").doc(myUid)
        .collection("Chats").doc(otherUid)
        .set({
            "displayName": otherData?.displayName,
            "email": otherData?.email,
            "image": otherData?.image,
            "chatKey": docRef.id
        })

    await firestore.collection("Users").doc(otherUid)
        .collection("Chats").doc(myUid)
        .set({
            "displayName": myData?.displayName,
            "email": myData?.email,
            "image": myData?.image,
            "chatKey": docRef.id
        })
    return {
        "chatKey": docRef.id
    };
})
export const getChatKey = functions.https.onCall(async (data, context) => {
    let myUid: string;
    if (context.auth?.uid != null) {
        myUid = context.auth?.uid;
    } else {
        return {
            "error": "You don't have permission to access this service",
        };
    }

    const otherUid = await getUidFromEmail(data.email);

    return firestore.collection("Users").doc(myUid)
        .collection("Chats").doc(otherUid)
        .get()
        .then((doc) => {
            if (doc.exists) {
                return {
                    chatKey: doc.get("chatKey"),
                };
            } else {
                return {
                    chatKey: null,
                };
            }
        });
});
export const sendMessage = functions.https.onCall((data, context) => {
    firestore.collection("Chats").doc(data.chatKey)
        .collection("Messages").add({
        "message": data.message
    })
})

export const getHourlyRate = functions.https.onCall((async (data, context) => {
    return firestore.collection("Users").doc(data.uid).get()
        .then((doc) => {
            if (doc.exists) {
                return {
                    "hourlyRate": doc.get("hourlyRate")
                };
            } else {
                return {
                    "hourlyRate": 0
                };
            }
        });
}));

export const checkLike = functions.https.onCall((async (data, context) => {
    const otherUid = await getUidFromEmail(data.email);
    return firestore.collection("Users").doc(otherUid).get()
        .then((document) => {
            if(document.exists) {
                let likes = new Map(Object.entries(document.get("likes")))
                return {
                    "like": likes.has(<string>context.auth?.uid)
                }
            }
            else{
                return {
                    "error": "No such user!"
                }
            }
        });
}));