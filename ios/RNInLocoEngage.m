
#import "RNInLocoEngage.h"
@import InLocoMediaSDKEngage;

@implementation RNInLocoEngage

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(init:(NSString *)appId withLogsEnabled:(BOOL)logsEnabled)
{
    ILMEngageOptions *options = [[ILMEngageOptions alloc] init];
    [options setLogEnabled:logsEnabled];
    [options setApplicationId:appId];
    [ILMInLocoEngage initWithOptions:options];
}

RCT_EXPORT_METHOD(setUser:(NSString *)userId)
{
    ILMEngageUser *user = [[ILMEngageUser alloc] initWithId:userId];
    [ILMInLocoEngage setUser:user];
}

RCT_EXPORT_METHOD(clearUser)
{
    [ILMInLocoEngage clearUser];
}

RCT_EXPORT_METHOD(setPushProvider:(NSString *)name andToken:(NSString *)token)
{
    ILMPushProvider* pushProvider  = [[ILMPushProvider alloc] initWithName:name token:token];
    [ILMInLocoEngage setPushProvider:pushProvider];
}

RCT_EXPORT_METHOD(setPushNotificationsEnabled:(BOOL) enabled)
{
    [ILMInLocoEngage setPushNotificationsEnabled:enabled];
}

RCT_EXPORT_METHOD(requestLocationPermission)
{
    [ILMInLocoEngage requestLocationAuthorization];
}

RCT_EXPORT_METHOD(trackEvent:(NSString *)name properties:(NSDictionary *)properties)
{
    [ILMInLocoEngage trackEvent:name properties:properties];
}
@end
