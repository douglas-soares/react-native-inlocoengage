import React, {Component} from 'react';
import {Platform, StyleSheet, Text, View, Button, Linking} from 'react-native';
import InLocoEngage from 'react-native-inlocoengage';
import firebase from 'react-native-firebase';
import Permissions from 'react-native-permissions'

const options = Platform.select({
  ios: {
    appId: "<YOUR_IOS_APP_ID>",
    enableLogs: true
  },
  android: {
    appId: "<YOUR_ANDROID_APP_ID>",
    enableLogs: true
  },
});

type Props = {};
export default class App extends Component<Props> {
  componentDidMount() {

    //Request permissions
    Permissions.request('location').then(response => {
      if(Platform.OS == 'ios') {
        Permissions.request('notification');
      }
    });

    // In Loco Engage initialization
    InLocoEngage.init(options);

    // Register a token refresh listener
    this.unsubscribeFromTokenRefreshListener = firebase.messaging().onTokenRefresh((fcmToken) => {
      InLocoEngage.setFirebasePushProvider(fcmToken);
    });

    // Retrieve and set current FCM token
    firebase.messaging().getToken().then((fcmToken) => {
      InLocoEngage.setFirebasePushProvider(fcmToken);
    });
    
    //Android specific code
    if(Platform.OS == 'android') {
      //Engage messages are received on Android through the onMessage fireabase callback
      this.unsubscribeFromMessageListener = firebase.messaging().onMessage((message) => {
        //Checks whether this is an Engage message
        if(InLocoEngage.isInLocoEngageMessage(message)) {
          //Presents the notification. The tracking of reception, impression and click is done automatically
          InLocoEngage.presentNotification(message);
        }
      });  
    }
    
    //iOS specific code
    if(Platform.OS == 'ios') {
      //The firebase onNotification callback is called when a notification is received and your app is in foreground. 
      //In this situation, it is up to you to decide if the notification should be shown.
      this.unsubscribeFromNotificationListener = firebase.notifications().onNotification((notification) => {
        //Checks whether this is an Engage notification
        if(InLocoEngage.isInLocoEngageMessage(notification)) {
          //Presents the notification
          const localNotification = new firebase.notifications.Notification()
            .setNotificationId(notification.notificationId)
            .setTitle(notification.title)
            .setSubtitle(notification.subtitle)
            .setBody(notification.body)
            .setData(notification.data)
            .ios.setBadge(notification.ios.badge);

          firebase.notifications()
            .displayNotification(localNotification)
            .catch(err => console.error(err));
          
          //Call InLocoEngage.onNotificationReceived() method to correctly update the push metrics
          InLocoEngage.onNotificationReceived(notification);
        };
      });

      //The firebase onNotificationDisplayed is called when the notification is shown
      this.unsubscribeFromNotificationDisplayedListener = firebase.notifications().onNotificationDisplayed((notification) => {
        //Checks whether this is an Engage notification
        if(InLocoEngage.isInLocoEngageMessage(notification)) {
          //Call InLocoEngage.onNotificationPresented() method to correctly update the push metrics
          InLocoEngage.onNotificationPresented(notification);
        }
      });
      
      //The firebase onNotificationOpened is called when the notification is clicked
      this.unsubscribeFromNotificationOpenedListener = firebase.notifications().onNotificationOpened((notificationOpen) => {
        const notification = notificationOpen.notification;
        //Checks whether this is an Engage notification
        if(InLocoEngage.isInLocoEngageMessage(notification)) {
          //Call InLocoEngage.onNotificationClicked() method to correctly update the push metrics
          InLocoEngage.onNotificationClicked(notification);
          
          //Open the notification link if possible
          const url = InLocoEngage.getUrl(notification)
          Linking.canOpenURL(url).then(supported => {
            if (supported) {
              return Linking.openURL(url);
            }
          });
        }
      });
      
      //If the app was closed and a notification was clicked, it will be available through the getInitialNotification() function
      firebase.notifications().getInitialNotification().then((notificationOpen) => {
        if (notificationOpen) {
          const notification = notificationOpen.notification;
          //Checks whether this is an Engage notification
          if(InLocoEngage.isInLocoEngageMessage(notification)) {
            //Call InLocoEngage.onAppLaunchedWithNotification() method to correctly update counters
            InLocoEngage.onAppLaunchedWithNotification(notification);

            //Open the notification link if possible
            const url = InLocoEngage.getUrl(notification)
            Linking.canOpenURL(url).then(supported => {
              if (supported) {
                return Linking.openURL(url);
              }
            });
          }
        }
      });
    }
  }

  componentWillUnmount() {
    //Unsubscribe from listeners
    this.unsubscribeFromTokenRefreshListener();

    if(Platform.OS == 'android') {
      this.unsubscribeFromMessageListener();
    }

    if(Platform.OS == 'ios') {
      this.unsubscribeFromNotificationListener();
      this.unsubscribeFromNotificationDisplayedListener();
      this.unsubscribeFromNotificationOpenedListener();
    }
  }

  setUser() {
    InLocoEngage.setUser("sample_user_id");
  }

  clearUser() {
    InLocoEngage.clearUser();
  }

  enableNotifications() {
    InLocoEngage.setPushNotificationsEnabled(true);
  }

  disableNotifications() {
    InLocoEngage.setPushNotificationsEnabled(false);
  }

  registerCustomEvent() {
    InLocoEngage.trackEvent("sample-event-name", {
      "custom_key_1": "custom_value_1",
      "custom_key_2": "custom_value_2"
    })
  }

  render() {
    return (
      <View style={styles.container}>
        <View style={styles.buttonContainer}>
          <Button color="#80BA40" title="Set user" onPress={() => this.setUser()}></Button>
        </View>
        <View style={styles.buttonContainer}>
          <Button color="#80BA40" title="Clear user" onPress={() => this.clearUser()}></Button>
        </View>
        <View style={styles.buttonContainer}>
          <Button color="#80BA40" title="Enable notifications" onPress={() => this.enableNotifications()}></Button>
        </View>
        <View style={styles.buttonContainer}>
          <Button color="#80BA40" title="Disable notifications" onPress={() => this.disableNotifications()}></Button>
        </View>
        <View style={styles.buttonContainer}>
          <Button color="#80BA40" title="Register custom event" onPress={() => this.registerCustomEvent()}></Button>
        </View>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    backgroundColor: '#F5FCFF',
  },
  buttonContainer: {
    margin:10
  },
});