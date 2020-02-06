/*
 Licensed to the Apache Software Foundation (ASF) under one
 or more contributor license agreements.  See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership.  The ASF licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.
 
 reference: https://github.com/stripe/stripe-ios/blob/771f778fcbad58f771583ebe6fe499a2a4bc6ae8/Example/Non-Card%20Payment%20Examples/AlipayExampleViewController.swift
 
1.pay => customer pay succed => back to app(enter application:openURL) => STPRedirectContext completion with no error!
2.pay => customer pay cancel => STPRedirectContext completion with no error!
 */

#include <sys/types.h>
#include <sys/sysctl.h>
#include "TargetConditionals.h"

#import "CDV.h"
#import "CDVStripeAlipay.h"
#import "STPSourceParams.h"
#import "STPAPIClient.h"
#import "STPRedirectContext.h"


@interface CDVStripeAlipay () {}
@property (nonatomic, retain) STPRedirectContext* redirectContext;
@end

@implementation CDVStripeAlipay

- (void)pluginInitialize
{
    // Stripe Configuration: pk_test_eKD9FcYHZpnF2hitYZV1hsqU00jfHaLuse  pk_live_W0rFYuVragKFWKRZfRkf3v9r0070ErNght
    [Stripe setDefaultPublishableKey:@"pk_live_W0rFYuVragKFWKRZfRkf3v9r0070ErNght"];
}

- (void)coolMethod: (CDVInvokedUrlCommand *)command {
    NSLog(@"coolMethod is called!");
    
    CDVPluginResult* pluginResult = nil;
    NSString* echo = [command.arguments objectAtIndex:0];

    if (echo != nil && [echo length] > 0) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:echo];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    }

    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

- (void)alipayTest: (CDVInvokedUrlCommand *)command {
    NSLog(@"alipayTest is called!");
    
    // Create a Source
    STPSourceParams *sourceParams = [STPSourceParams alipayParamsWithAmount:50
                                                                currency:@"JPY"
                                                                returnURL:@"myapp://safepay/"];
    [[STPAPIClient sharedClient] createSourceWithParams:sourceParams completion:^(STPSource *source, NSError *error) {
        if (error) {
            // Handle error
            CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[error localizedDescription]];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
            return;
        }
        
        // Redirect the customer to authorize the payment
        self.redirectContext = [[STPRedirectContext alloc] initWithSource:source completion:^(NSString *sourceID, NSString *clientSecret, NSError *error) {
            if (error) {
                // Handle error
                CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[error localizedDescription]];
                [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
                return;
            } else {
                CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"Succeed!"];
                [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
            }
            // Poll your backend for the status of the order
        }];
        [self.redirectContext startRedirectFlowFromViewController:[self viewController]];
    }];
}

/**
 ERROR: NSURLErrorDomain Code=-1009 "The Internet connection appears to be offline.    >  Connect WIFI or 4G
 */
- (void)alipayBySourceJson: (CDVInvokedUrlCommand *)command {
    NSLog(@"alipayBySourceJson 插件被调起了");
    
    id sourceJson = [command argumentAtIndex:0];
    NSDictionary* params = (NSDictionary*)[self JSONValue:sourceJson];
    
    unsigned int amount = [[params objectForKey:@"amount"] unsignedIntValue];
    NSString* currency = [params objectForKey:@"currency"];
    
    // Create a Source
       STPSourceParams *sourceParams = [STPSourceParams alipayParamsWithAmount:amount
                                                                   currency:currency
                                                                   returnURL:@"myapp://safepay/"];
       [[STPAPIClient sharedClient] createSourceWithParams:sourceParams completion:^(STPSource *source, NSError *error) {
           if (error) {
               // Handle error
               CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[error localizedDescription]];
               [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
               return;
           }
           
           // Redirect the customer to authorize the payment
           self.redirectContext = [[STPRedirectContext alloc] initWithSource:source completion:^(NSString *sourceID, NSString *clientSecret, NSError *error) {
               if (error) {
                   // Handle error
                   CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[error localizedDescription]];
                   [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
                   return;
               } else {
                   CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"Succeed!"];
                   [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
               }
               // Poll your backend for the status of the order
           }];
           [self.redirectContext startRedirectFlowFromViewController:[self viewController]];
       }];
}

-(id)JSONValue:(NSString *)str
{
    NSData* data = [str dataUsingEncoding:NSUTF8StringEncoding];
    __autoreleasing NSError* error = nil;
    id result = [NSJSONSerialization JSONObjectWithData:data options:kNilOptions error:&error];
    if (error != nil) return nil;
    return result;
}


@end
