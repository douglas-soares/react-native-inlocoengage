
import { NativeModules } from 'react-native';
import { Platform } from 'react-native';

const { RNInLocoEngage } = NativeModules;

const init = (options) => {
	const appId = options.appId || null;
	const enableLogs = options.enableLogs || false;
	RNInLocoEngage.init(appId, enableLogs);
}

const setUser = (userId) => {
	RNInLocoEngage.setUser(userId);
}

const clearUser = (clearUser) => {
	RNInLocoEngage.clearUser();
}

const trackEvent = (name, properties) => {
	for (var property in properties) {
		if (properties.hasOwnProperty(property) && properties[property] != null) {
			properties[property] = properties[property].toString();
		}
	}
	RNInLocoEngage.trackEvent(name, properties);
}
 
const setPushProvider = (provider) => {
	const name = provider.name || null;
	const token = provider.token || null;
	RNInLocoEngage.setPushProvider(name, token);
}

const setFirebasePushProvider = (fcmToken) => {
	if (fcmToken) {
        setPushProvider({
          name: "google_fcm",
          token: fcmToken
		});
    }
}

const setPushNotificationsEnabled = (enabled) => {
	RNInLocoEngage.setPushNotificationsEnabled(enabled);
}

const isInLocoEngageMessage = (message) => {
	return 'in_loco_data' in message.data;
}

const presentNotification = (message, notificationId, channelId) => {
	if(Platform.OS == 'android') {
		notificationId = notificationId || 1111111;
		RNInLocoEngage.presentNotification(message.data['in_loco_data'], channelId, notificationId);
	}
}

export default {
	init: init,
	setUser: setUser,
	clearUser: clearUser,
	trackEvent: trackEvent,
	setPushProvider: setPushProvider,
	setFirebasePushProvider: setFirebasePushProvider,
	setPushNotificationsEnabled: setPushNotificationsEnabled,
	isInLocoEngageMessage: isInLocoEngageMessage,
	presentNotification: presentNotification
};
