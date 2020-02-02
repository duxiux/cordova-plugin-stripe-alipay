var exec = require("cordova/exec")

exports.coolMethod = function(arg0, success, error) {
    exec(success, error, "StripeAlipay", "coolMethod", [arg0])
}

/**
 * Test alipay
 */
exports.alipayTest = function(success, error) {
    exec(success, error, "StripeAlipay", "alipayTest", [])
}

/**
     * Alipay by source json. Source was created by server.
    e.g
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
    */
exports.alipayBySourceJson = function(arg0, success, error) {
    exec(success, error, "StripeAlipay", "alipayBySourceJson", [arg0])
}