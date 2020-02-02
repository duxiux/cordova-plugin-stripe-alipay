// 参考： https://juejin.im/post/5c77d0b9e51d455fb110c394
const path = require("path")

function resolve(dir) {
    return path.join(__dirname, dir)
}

module.exports = {
    lintOnSave: false,

    publicPath: process.env.NODE_ENV === "production" ? "./" : "",

    outputDir: "./www",

    // 放置生成的静态资源 (js、css、img、fonts) 的 (相对于 outputDir 的) 目录
    assetsDir: "static",

    // 指定生成的 index.html 的输出路径 (相对于 outputDir),也可以是一个绝对路径
    indexPath: "index.html",

    // 生产环境 sourceMap
    productionSourceMap: false,

    css: {
        loaderOptions: {
            less: {
                javascriptEnabled: true
            }
        }
    },

    // 配置iview-loader
    chainWebpack: config => {
        config.module
            .rule("md-loader")
            .test(/\.md$/i)
            .use("raw-loader")
            .loader("raw-loader")
            .end()

        config.resolve.alias
            .set("@", resolve("src"))
            .set("assets", resolve("src/assets"))
            .set("styles", resolve("src/styles"))
            .set("components", resolve("src/components"))
            .set("vcore", resolve("src/vcore"))

        config.plugin("define").tap(definitions => {
            definitions[0] = Object.assign(definitions[0], {
                G_MOBILE: JSON.stringify(true)
            })
            return definitions
        })
    }
}