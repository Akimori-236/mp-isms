importScripts("https://www.gstatic.com/firebasejs/9.22.0/firebase-app-compat.js");
importScripts(
  "https://www.gstatic.com/firebasejs/9.22.0/firebase-messaging-compat.js"
);

firebase.initializeApp({
  apiKey: "AIzaSyCO5ueF6ep4lS3zYwaZBJUiFb5MGZNSJK4",
  authDomain: "tfip-project-382915.firebaseapp.com",
  projectId: "tfip-project-382915",
  storageBucket: "tfip-project-382915.appspot.com",
  messagingSenderId: "869245493728",
  appId: "1:869245493728:web:09a9dc750bdd8229988b85",
  measurementId: "G-2LGFVP8ZK4",
});

const messaging = firebase.messaging();

messaging.onBackgroundMessage(function (payload) {
  console.log("Received background message ", payload);

  const notificationTitle = payload.notification.title;
  const notificationOptions = {
    body: payload.notification.body,
  };

  self.registration.showNotification(notificationTitle, notificationOptions);
});
