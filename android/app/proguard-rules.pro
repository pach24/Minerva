-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class kotlinx.serialization.json.** { kotlinx.serialization.KSerializer serializer(...); }

-keep,includedescriptorclasses class com.minerva.app.**$$serializer { *; }
-keepclassmembers class com.minerva.app.** {
    *** Companion;
}
-keepclasseswithmembers class com.minerva.app.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep interface retrofit2.** { *; }
-keepclasseswithmembers class * { @retrofit2.http.* <methods>; }
