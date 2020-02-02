package com.unreal.world.stripealipay;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Source;
import com.stripe.android.model.SourceParams;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Stripe alipay manager
 *
 */
public class StripeAlipayManager  {
    private static final String LOG_TAG = "StripeAlipayManager";
    private static final String STRIPE_KEY = "pk_live_W0rFYuVragKFWKRZfRkf3v9r0070ErNght";  // pk_test_eKD9FcYHZpnF2hitYZV1hsqU00jfHaLuse  pk_live_W0rFYuVragKFWKRZfRkf3v9r0070ErNght

    public static final int SDK_PAY_FLAG = 1;
    public static Activity mActivity;
    private CallbackContext mEventsCallbackContext;


    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case SDK_PAY_FLAG:
                @SuppressWarnings("unchecked")
                Map<String, String> answer = (Map<String, String>) msg.obj;
                // The result info contains other information about the transaction
                String resultInfo = answer.get("result");
                String resultStatus = answer.get("resultStatus");
                if (TextUtils.equals(resultStatus, "9000")) {
                    Toast.makeText(mActivity.getApplicationContext(), "success", Toast.LENGTH_SHORT).show();

                    if (mEventsCallbackContext != null) {
                        mEventsCallbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, "Alipay succeed!"));
                    }
                } else {
                    Toast.makeText(mActivity.getApplicationContext(), "failed", Toast.LENGTH_SHORT).show();

                    if (mEventsCallbackContext != null) {
                        mEventsCallbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR, "Alipay failed!"));
                    }
                }
                break;
            default:
                break;
            }
        }
    };

    public StripeAlipayManager(Activity activity) {
        mActivity = activity;
    }

    void setEventsCallbackContext(CallbackContext callbackContext) {
        this.mEventsCallbackContext = callbackContext;
    }

    /**
     * Auto generate source in client. Spend the least money.
     * https://stripe.com/docs/mobile/android/sources#create-source-object
     */
    public void invokeAuto(CallbackContext callbackContext) {
        setEventsCallbackContext(callbackContext);

        try {
            final Stripe stripe = new Stripe(mActivity.getApplicationContext(),
                    STRIPE_KEY);

            SourceParams alipaySingleUseParams = SourceParams.createAlipaySingleUseParams(50L, // Amount is a long int in
                    // the lowest denomination.
                    // 50 cents in USD is the
                    // minimum
                    "jpy", "Mr. Sample", // customer name
                    "sample@sample.smp", // customer email
                    "mycompany://alipay"); // a redirect address to get the user back into your app

            // The asynchronous way to do it. Call this method on the main thread.
            stripe.createSource(
                    alipaySingleUseParams,
                    new ApiResultCallback<Source>() {
                        @Override
                        public void onSuccess(@NonNull Source source) {
                            // Store the source somewhere, use it, etc

                            if (isAliPayInstalled(mActivity.getApplicationContext())) {
                                invokeAlipayNative(source);
                            } else {
                                invokeAlipayWeb(source);
                            }
                        }
                        @Override
                        public void onError(@NonNull Exception error) {
                            // Tell the user that something went wrong
                            LOG.d(LOG_TAG,"JSONException on alipay");
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void invokeBySourceJson(String sourceJson, CallbackContext callbackContext) {
        setEventsCallbackContext(callbackContext);

        try {
            final Stripe stripe = new Stripe(mActivity.getApplicationContext(), STRIPE_KEY);

            Gson gson = new Gson();
            SourceParams alipaySingleUseParams = gson.fromJson(sourceJson, SourceParams.class);

            // The asynchronous way to do it. Call this method on the main thread.
            stripe.createSource(
                    alipaySingleUseParams,
                    new ApiResultCallback<Source>() {
                        @Override
                        public void onSuccess(@NonNull Source source) {
                            // Store the source somewhere, use it, etc
                            if (isAliPayInstalled(mActivity.getApplicationContext())) {
                                invokeAlipayNative(source);
                            } else {
                                invokeAlipayWeb(source);
                            }
                        }
                        @Override
                        public void onError(@NonNull Exception error) {
                            // Tell the user that something went wrong
                            LOG.d(LOG_TAG,"JSONException on alipay");
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void invokeAlipayWeb(Source source) {
        String redirectUrl = source.getRedirect().getUrl();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(redirectUrl));
        mActivity.startActivity(intent);
    }

    private void invokeAlipayNative(Source source) {
        Map<String, Object> alipayParams = source.getSourceTypeData();
        final String dataString = (String) alipayParams.get("data_string");

        Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                // The PayTask class is from the Alipay SDK. Do not run this function
                // on the main thread.
                PayTask alipay = new PayTask(mActivity);
                // Invoking this function immediately takes the user to the Alipay
                // app, if in stalled. If not, the user is sent to the browser.
                Map<String, String> result = alipay.payV2(dataString, true);

                // Once you get the result, communicate it back to the main thread
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * 检测是否安装支付宝
     * @param context
     * @return
     */
    public static boolean isAliPayInstalled(Context context) {
        Uri uri = Uri.parse("alipays://platformapi/startApp");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        ComponentName componentName = intent.resolveActivity(context.getPackageManager());
        return componentName != null;
    }

    /**
     * 检测是否安装微信
     * @param context
     * @return
     */
    public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }

}
