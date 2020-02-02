package com.unreal.world.stripealipay;

import android.content.Context;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class StripeAlipay extends CordovaPlugin {
    private StripeAlipayManager mAlipayMgr;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        mAlipayMgr = new StripeAlipayManager(cordova.getActivity());
    }

    @Override
    protected void pluginInitialize() {
        Context context = cordova.getActivity().getApplicationContext();
    }


    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Context context = cordova.getActivity().getApplicationContext();
        if (action.equals("coolMethod")) {
            String message = args.getString(0);
            this.coolMethod(message, callbackContext);
            return true;
        } else if (action.equals("alipayTest")) {
            this.alipayTest(callbackContext);
            return true;
        } else if (action.equals("alipayBySourceJson")) {
            String sourceJson = args.getString(0);
            this.alipayBySourceJson(sourceJson, callbackContext);
            return true;
        }
        return false;
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
//        if (message != null && message.length() > 0) {
//            callbackContext.success(message);
//        } else {
//            callbackContext.error("Expected one non-empty string argument.");
//        }
    }

    private void alipayTest(CallbackContext callbackContext) {
        mAlipayMgr.invokeAuto(callbackContext);
    }

    private void alipayBySourceJson(String sourceJson, CallbackContext callbackContext) {
        mAlipayMgr.invokeBySourceJson(sourceJson, callbackContext);
    }


}
