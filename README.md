# 1.Install plugin

```
cordova plugin add cordova-plugin-stripe-alipay
```

# 2.Test stripe alipay

## 2.1.Test alipay with default source

Called in javascript, spend least money.

```JavaScript
window.StripeAlipay.alipayTest(
    result => {
        alert("succeed:" + JSON.stringify(result));
    },
    err => {
        alert("err:" + JSON.stringify(err));
    }
);
```

## 2.2.Test alipay with source json

Called in javascript

```JavaScript
let source = `{"amount":50, "currency":"jpy","extraParams":{},"owner":{"email":"sample@sample.smp","name":"Mr. Sample"},"returnUrl":"mycompany://alipay","type":"alipay","typeRaw":"alipay"}`;
window.StripeAlipay.alipayBySourceJson(
    source,
    result => {
        alert("succeed:" + JSON.stringify(result));
    },
    err => {
        alert("err:" + JSON.stringify(err));
    }
);
```

# Alipay on iOS

## 1.config reference

https://stripe.com/docs/sources/alipay/ios

# 3.QA

## 3.1.Use test key maybe get some error

test key like `pk_test_ABACDF2hitYZV1hsqU00jfHaLuse` maybe get some unknown error!

Suggest sue pk_live_XXX key.

```
pk_test_ABACDF2hitYZV1hsqU00jfHaLuse

Map<String, Object> alipayParams = source.getSourceTypeData();
final String dataString = (String) alipayParams.get("data_string");
```

get data_string all return "null"，

## 3.2.Error:Invalid currency

```
com.stripe.android.exception.InvalidRequestException: Invalid currency: cny. The payment method `alipay` only supports the following currencies: aud, cad, eur, gbp, hkd, jpy, nzd, sgd, usd.
```

The currency you use should the same as your company region. not the customer's region.

your company region: 'japan', maybe you should use 'jpy'  
your company region: 'America', maybe you should use 'usd'  
your company region: 'china', maybe you should use 'cny' or 'hkd'（i don't know，because my company is not in china's mainland, somebody who use it may tell me the result)

## 3.3.Too little money to test

```
com.stripe.android.exception.InvalidRequestException: Amount must convert to at least 400 cents. ¥50 converts to approximately \$3.58.
```

Too little money to test, add it.
