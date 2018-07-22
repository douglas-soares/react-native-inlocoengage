import React, {Component} from 'react';
import {Platform, StyleSheet, Text, View, Button} from 'react-native';
import InLocoEngage from 'react-native-inlocoengage';
import firebase from 'react-native-firebase';
import Permissions from 'react-native-permissions'

const options = Platform.select({
  ios: {
    appId: "<YOU_IOS_APP_ID>",
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

    // Retrieve and set current FCM token
    firebase.messaging().getToken().then((fcmToken) => {
      InLocoEngage.setFirebasePushProvider(fcmToken);
    });

    // Register a token refresh listener
    this.onTokenRefreshListener = firebase.messaging().onTokenRefresh((fcmToken) => {
      InLocoEngage.setFirebasePushProvider(fcmToken);
    });

    // Register a message listener
    this.onMessageListener = firebase.messaging().onMessage((message) => {
      if(InLocoEngage.isInLocoEngageMessage(message)) {
        InLocoEngage.presentNotification(message);
      }
    });
  }

  componentWillUnmount() {
    this.onTokenRefreshListener();
    this.onMessageListener();
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