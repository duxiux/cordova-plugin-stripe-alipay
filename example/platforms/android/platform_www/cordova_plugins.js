cordova.define('cordova/plugin_list', function(require, exports, module) {
  module.exports = [
    {
      "id": "cordova-plugin-stripe-alipay.StripeAlipay",
      "file": "plugins/cordova-plugin-stripe-alipay/www/StripeAlipay.js",
      "pluginId": "cordova-plugin-stripe-alipay",
      "clobbers": [
        "StripeAlipay"
      ]
    }
  ];
  module.exports.metadata = {
    "cordova-plugin-stripe-alipay": "1.0.9",
    "cordova-plugin-whitelist": "1.3.4"
  };
});