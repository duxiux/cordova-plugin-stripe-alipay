# example

Test cordova-plugin-stripe-alipay, 前端: cordoa + vue

## 1.Add plugin

不安装 npm 上的线上插件，测试本地插件。

```
cordova plugin add .. --link
```

要添加`--link` 否则会报错：`ENAMETOOLONG: name too long, stat' error`

## 2.启动命令

```
yarn
yarn build
cordova run android
```

## 3.其他命令

删除重新安装

```
find . -name cordova-plugin-stripe-alipay -exec rm -rf {} \;
cordova platform rm android
cordova platform add android

// 没有自动添加组件，执行以下命令
cordova plugin add cordova-plugin-stripe-alipay
```

# 其他

## 自定义 gradle

https://cordova.apache.org/docs/en/9.x/plugin_ref/spec.html#framework
https://www.jianshu.com/p/67443192c4eb

## example 参考:

https://github.com/infobip/mobile-messaging-cordova-plugin
