package cn.chessroad.anco_env

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

import android.os.Build
import java.io.File
import java.lang.reflect.Field

/** AncoEnvPlugin */
class AncoEnvPlugin :
    FlutterPlugin,
    MethodCallHandler {
    private lateinit var channel: MethodChannel
    private var context: android.content.Context? = null

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "anco_env")
        channel.setMethodCallHandler(this)
        context = flutterPluginBinding.applicationContext
    }

    override fun onMethodCall(
        call: MethodCall,
        result: Result
    ) {
        when (call.method) {
            "getPlatformVersion" -> {
                result.success("Android ${android.os.Build.VERSION.RELEASE}")
            }
            "isHarmonyAnco" -> {
                result.success(isHarmonyAnco())
            }
            "getEnvInfo" -> {
                result.success(getEnvInfo())
            }
            "getProp" -> {
                val key = call.argument<String>("key")
                if (key != null) {
                    result.success(getProp(key))
                } else {
                    result.error("INVALID_ARGUMENT", "Property key is null", null)
                }
            }
            else -> {
                result.notImplemented()
            }
        }
    }

    private fun getProp(key: String): String? {
        return try {
            val systemProperties = Class.forName("android.os.SystemProperties")
            val get = systemProperties.getMethod("get", String::class.java)
            val value = get.invoke(systemProperties, key) as String
            if (value.isEmpty()) null else value
        } catch (e: Exception) {
            null
        }
    }

    private fun isHarmonyAnco(): Boolean {
        val device = getProp("ro.product.product.device")
        val ancoApi = getProp("ro.product.os.dist.anco.apiversion")
        val buildFlavor = getProp("ro.build.flavor")

        return device?.contains("anco") == true ||
                ancoApi != null ||
                buildFlavor?.contains("anco") == true
    }

    private fun getEnvInfo(): Map<String, Any> {
        val info = mutableMapOf<String, Any>()

        // 1. Collect android.os.Build fields
        val buildInfo = mutableMapOf<String, String>()
        val fields: Array<Field> = Build::class.java.declaredFields
        for (field in fields) {
            try {
                field.isAccessible = true
                val value = field.get(null)
                if (value != null) {
                    buildInfo[field.name] = value.toString()
                }
            } catch (e: Exception) {
                // Ignore
            }
        }
        buildInfo["VERSION.SDK_INT"] = Build.VERSION.SDK_INT.toString()
        buildInfo["VERSION.RELEASE"] = Build.VERSION.RELEASE
        buildInfo["VERSION.INCREMENTAL"] = Build.VERSION.INCREMENTAL
        buildInfo["VERSION.CODENAME"] = Build.VERSION.CODENAME
        buildInfo["VERSION.BASE_OS"] = Build.VERSION.BASE_OS
        buildInfo["VERSION.PREVIEW_SDK_INT"] = Build.VERSION.PREVIEW_SDK_INT.toString()
        buildInfo["VERSION.SECURITY_PATCH"] = Build.VERSION.SECURITY_PATCH

        info["build"] = buildInfo

        // 2. Collect System Properties
        val sysProps = mutableMapOf<String, String>()
        val keys = listOf(
            "os.name", "os.arch", "os.version",
            "java.vendor", "java.version", "java.home",
            "user.language", "user.region",
            "http.agent"
        )
        for (key in keys) {
            System.getProperty(key)?.let { sysProps[key] = it }
        }
        info["systemProperties"] = sysProps

        // 3. Check for specific files
        val filesToCheck = listOf(
            "/system/lib/libohos.so",
            "/system/framework/hw.jar",
            "/system/build.prop",
            "/proc/version",
            "/system/bin/su",
            "/system/xbin/su"
        )
        val fileExistence = mutableMapOf<String, Boolean>()
        for (path in filesToCheck) {
            fileExistence[path] = File(path).exists()
        }
        info["files"] = fileExistence

        // 4. Check for specific packages
        val packagesToCheck = listOf(
            "com.huawei.system",
            "com.zhard.s",
            "com.android.vending",
            "com.google.android.gms",
            "com.huawei.hwid",
            "com.huawei.android.launcher"
        )
        val packageExistence = mutableMapOf<String, Boolean>()
        val pm = context?.packageManager
        if (pm != null) {
            for (pkg in packagesToCheck) {
                try {
                    pm.getPackageInfo(pkg, 0)
                    packageExistence[pkg] = true
                } catch (e: Exception) {
                    packageExistence[pkg] = false
                }
            }
        }
        info["packages"] = packageExistence

        // 5. Build Properties via getprop command
        val propCommand = mutableMapOf<String, String>()
        try {
            val process = Runtime.getRuntime().exec("getprop")
            val reader = java.io.BufferedReader(java.io.InputStreamReader(process.inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                line?.let {
                    val parts = it.split(": ")
                    if (parts.size >= 2) {
                        val key = parts[0].trim('[', ']')
                        val value = parts[1].trim('[', ']')
                        if (key.contains("ro.build") || key.contains("hw") || key.contains("harmony") || key.contains("zy") || key.contains("product")) {
                            propCommand[key] = value
                        }
                    }
                }
            }
            reader.close()
        } catch (e: Exception) {
            propCommand["error"] = e.message ?: "Unknown error"
        }
        info["systemProps"] = propCommand
        
        // Add isHarmonyAnco flag to the info
        info["isHarmonyAnco"] = isHarmonyAnco()

        return info
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
        context = null
    }
}
