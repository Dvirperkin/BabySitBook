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
export const getDisplayName = functions.https.onCall((async (data, context) => {
    if (context.auth?.uid == undefined) {
        return {
            "error": "You don't have permission to access this service",
        };
    }
    return firestore.collection("Users").doc(context.auth?.uid).get()
        .then((doc) => {
            if (doc.exists) {
                console.log(doc.get("displayName"))
                return {
                    "displayName": doc.get("displayName"),
                };
            } else {
                return {
                    "NoUser": true,
                };
            }
        });
}));
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
          text: data.displayName + " has sent you a friend request",
          title: "Friend Request"
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
      .where("email", "==", data.email)
      .where("title", "==", "Friend Request").get()
      .then((doc) => {
        firestore.collection("Users")
            .doc(myUid)
            .collection("Notifications")
            .doc(doc.docs[0].id).delete();
      });
  return;
});

export const getFriendList = functions.https.onCall(async (data,context)=>{
    let myUid: string;
    if (context.auth?.uid != null) {
        myUid = context.auth?.uid;
    } else {
        return {
            "error": "You don't have permission to access this service",
        };
    }
    return firestore.collection("Users")
        .doc(myUid)
        .collection("Friends")
        .get()
        .then((doc) => {
            if (!doc.empty) {
                let friendsID = doc.docs.map((currentDocument)=>{
                    if(currentDocument.get("relation")=="friends")
                        return currentDocument.id;
                    return ;
                })
                console.log(friendsID);
                return friendsID;
            } else {
                return;
            }
        })
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
    let myUid: string;
    if (context.auth?.uid != null) {
        myUid = context.auth?.uid;
    } else {
        return {
            "error": "You don't have permission to access this service",
        };
    }

  await firestore.collection("calendar").doc(data.uid)
      .collection("events").add(data)
      .then(doc => {
          data.eventID = doc.id
          doc.update({"eventID": doc.id})
      });

    if(data.contactToShare != "") {
        const otherUid = await getUidFromEmail(data.contactToShare);
        const myData = await getUidData(myUid);

        return firestore.collection("Users").doc(otherUid)
            .collection("Notifications").add({
                email: context.auth?.token.email,
                text: myData?.displayName + " has shared an event with you:\n"
                    + data.title + ", at " + data.date + ", " + data.startTime + "-" + data.endTime,
                title: "Event Sharing Request",
                notificationID: "",
                eventID: data.eventID
            }).then(doc => {
                doc.update({"eventSharingID": doc.id})
            });
    }
}));

export const updateEvent = functions.https.onCall((async (data, context) => {
    let myUid: string;
    if (context.auth?.uid != null) {
        myUid = context.auth?.uid;
    } else {
        return {
            "error": "You don't have permission to access this service",
        };
    }

    await firestore.collection("calendar").doc(data.uid).collection("events")
        .doc(data.eventID).update(data)

    if(data.contactToShare != "") {
        const otherUid = await getUidFromEmail(data.contactToShare);
        const myData = await getUidData(myUid);

        return firestore.collection("Users").doc(otherUid)
            .collection("Notifications").add({
                email: context.auth?.token.email,
                text: myData?.displayName + " has updated an event into:\n"
                    + data.title + ", at " + data.date + ", " + data.startTime + "-" + data.endTime,
                title: "Event Updating Request",
                notificationID: "",
                eventID: data.eventID
            }).then(doc => {
                doc.update({"eventUpdatingID": doc.id})
            });
    }
}));

export const deleteEvent = functions.https.onCall((async (data, context) => {
    await firestore.collection("calendar").doc(data.uid).collection("events")
        .where("eventID", "==", data.eventID)
        .get()
        .then(async (doc) => {
            const otherUid = await getUidFromEmail(doc.docs[0].get("contactToShare"))
            if (otherUid != "") {
                //todo sent a notification about deletion
                await firestore.collection("calendar").doc(otherUid).collection("events")
                    .doc(data.eventID).delete()
            }
            await firestore.collection("calendar").doc(data.uid).collection("events")
                .doc(data.eventID).delete()
        });
}));

export const acceptEventSharing = functions.https.onCall(async (data, context) => {
    let myUid: string;
    if (context.auth?.uid != null) {
        myUid = context.auth?.uid;
    } else {
        return {
            "error": "You don't have permission to access this service",
        };
    }

    const otherUid = await getUidFromEmail(data.email);
    let eventID = ""

    await firestore.collection("Users")
        .doc(myUid)
        .collection("Notifications")
        .where("notificationID", "==", data.notificationID)
        .get()
        .then((doc) => {
            eventID = doc.docs[0].get("eventID")
            firestore.collection("Users")
                .doc(myUid)
                .collection("Notifications")
                .doc(doc.docs[0].id).delete();
        });

    await firestore.collection("calendar").doc(otherUid).collection("events")
        .where("eventID", "==", eventID)
        .get()
        .then((doc) => {
            firestore.collection("calendar").doc(myUid).collection("events")
                .doc(eventID).set({
                uid: myUid,
                title: doc.docs[0].get("title"),
                date: doc.docs[0].get("date"),
                startTime: doc.docs[0].get("startTime"),
                endTime: doc.docs[0].get("endTime"),
                details: doc.docs[0].get("details"),
                contactToShare: data.email,
                eventID: eventID
            })
        });

    return;
})

export const ignoreEventSharing = functions.https.onCall(async (data, context) => {
    let myUid: string;
    if (context.auth?.uid != null) {
        myUid = context.auth?.uid;
    } else {
        return {
            "error": "You don't have permission to access this service",
        };
    }

    const otherUid = await getUidFromEmail(data.email);
    let eventID = ""

    await firestore.collection("Users")
        .doc(myUid)
        .collection("Notifications")
        .where("notificationID", "==", data.notificationID)
        .get()
        .then((doc) => {
            eventID = doc.docs[0].get("eventID")
            firestore.collection("Users")
                .doc(myUid)
                .collection("Notifications")
                .doc(doc.docs[0].id).delete();
        });

    await firestore.collection("calendar").doc(otherUid).collection("events")
        .doc(eventID).update({contactToShare: ""})
    return;
})

export const acceptEventUpdating = functions.https.onCall(async (data, context) => {
    let myUid: string;
    if (context.auth?.uid != null) {
        myUid = context.auth?.uid;
    } else {
        return {
            "error": "You don't have permission to access this service",
        };
    }

    const otherUid = await getUidFromEmail(data.email);
    let eventID = ""

    await firestore.collection("Users")
        .doc(myUid)
        .collection("Notifications")
        .where("notificationID", "==", data.notificationID)
        .get()
        .then((doc) => {
            eventID = doc.docs[0].get("eventID")
            firestore.collection("Users")
                .doc(myUid)
                .collection("Notifications")
                .doc(doc.docs[0].id).delete();
        });

    await firestore.collection("calendar").doc(otherUid).collection("events")
        .where("eventID", "==", eventID)
        .get()
        .then((doc) => {
            firestore.collection("calendar").doc(myUid).collection("events")
                .doc(eventID).update({
                uid: myUid,
                title: doc.docs[0].get("title"),
                date: doc.docs[0].get("date"),
                startTime: doc.docs[0].get("startTime"),
                endTime: doc.docs[0].get("endTime"),
                details: doc.docs[0].get("details"),
                contactToShare: data.email,
                eventID: eventID
            })
        });

    return;
})

export const ignoreEventUpdating = functions.https.onCall(async (data, context) => {
    let myUid: string;
    if (context.auth?.uid != null) {
        myUid = context.auth?.uid;
    } else {
        return {
            "error": "You don't have permission to access this service",
        };
    }

    const otherUid = await getUidFromEmail(data.email);
    let eventID = ""

    await firestore.collection("Users")
        .doc(myUid)
        .collection("Notifications")
        .where("notificationID", "==", data.notificationID)
        .get()
        .then((doc) => {
            eventID = doc.docs[0].get("eventID")
            firestore.collection("Users")
                .doc(myUid)
                .collection("Notifications")
                .doc(doc.docs[0].id).delete();
        });

    await firestore.collection("calendar").doc(myUid).collection("events")
        .doc(eventID).update({contactToShare: ""})
    await firestore.collection("calendar").doc(otherUid).collection("events")
        .doc(eventID).update({contactToShare: ""})
    return;
})

export const deletePost = functions.https.onCall((async (data, context) => {
    if (context.auth?.uid != null) {
        await firestore.collection("Post").doc(data.postID).delete();
    }
}));

export const charge = functions.https.onCall((async (data, context) => {
    let myUid: string;
    if (context.auth?.uid != null) {
        myUid = context.auth?.uid;
    } else {
        return {
            "error": "You don't have permission to access this service",
        };
    }

  await firestore.collection("OpenBills").add(data);

    const otherUid = await getUidFromEmail(data.emailToCharge);
    const myData = await getUidData(myUid);

    return firestore.collection("Users").doc(otherUid)
        .collection("Notifications").add({
            email: context.auth?.token.email,
            text: myData?.displayName + " has charged you for " + data.totalSum,
            title: "Charge Request",
            notificationID: ""
        }).then(doc => {
            doc.update({"chargeID": doc.id})
        });
}));

export const acceptCharge = functions.https.onCall(async (data, context) => {
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

    await firestore.collection("OpenBills")
        .where("uid", "==", otherUid)
        .where("emailToCharge", "==", myData?.email)
        .get()
        .then((doc) => {
            firestore.collection("ClosedBills").add({
                "uid": doc.docs[0].get("uid"),
                "date": doc.docs[0].get("date"),
                "time": doc.docs[0].get("time"),
                "totalSum": doc.docs[0].get("totalSum"),
                "emailToCharge": doc.docs[0].get("emailToCharge"),
                "paid": true
            })
            firestore.collection("OpenBills")
                .doc(doc.docs[0].id)
                .delete()
        });

    await firestore.collection("Users")
        .doc(myUid)
        .collection("Notifications")
        .where("notificationID", "==", data.notificationID)
        .get()
        .then((doc) => {
            firestore.collection("Users")
                .doc(myUid)
                .collection("Notifications")
                .doc(doc.docs[0].id).delete();
        });
    return;
})

export const ignoreCharge = functions.https.onCall(async (data, context) => {
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

    await firestore.collection("OpenBills")
        .where("uid", "==", otherUid)
        .where("emailToCharge", "==", myData?.email)
        .get()
        .then((doc) => {
            firestore.collection("ClosedBills").add({
                "uid": doc.docs[0].get("uid"),
                "date": doc.docs[0].get("date"),
                "time": doc.docs[0].get("time"),
                "totalSum": doc.docs[0].get("totalSum"),
                "emailToCharge": doc.docs[0].get("emailToCharge"),
                "paid": false
            })
            firestore.collection("OpenBills")
                .doc(doc.docs[0].id)
                .delete()
        });

    await firestore.collection("Users")
        .doc(myUid)
        .collection("Notifications")
        .where("notificationID", "==", data.notificationID)
        .get()
        .then((doc) => {
            firestore.collection("Users")
                .doc(myUid)
                .collection("Notifications")
                .doc(doc.docs[0].id).delete();
        });
    return;
})

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
    console.log("hey from sendMessage")

    firestore.collection("Chats").doc(data.chatKey)
        .collection("Messages").add({
        "message": data.message,
        "date": data.date,
        "sender":  context.auth?.uid
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

export const isSender = functions.https.onCall((async (data, context) => {
    console.log("hey from sender")
    return firestore.collection("Chats").doc(data.chatKey).collection("Messages")
        .doc().get()
        .then((doc) => {
            if (doc.exists && doc.get("sender")==context.auth?.uid) {
                console.log("isSender true")
                return {
                    "isSender": true,
                };

            } else {
                console.log("isSender false")
                return {
                    "isSender": false,
                };
            }
        });
}));

export const postToFeed = functions.https.onCall((data, context) => {
  console.log("hey from postToFeed")
  firestore.collection("Post").add({
      "displayName": data.displayName,
      "postedKey": data.postedKey,
      "postContent": data.postContent,
      "date": data.date,
      "postID": "",
      "email": data.email
  }).then(doc=>{
      firestore.collection("Post").doc(doc.id).update({"postID": doc.id})
  })
})

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
