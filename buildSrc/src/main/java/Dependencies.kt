@Suppress("unused")
object Version {
    const val core = "1.9.0"
    const val junit = "4.13.2"
    const val lifecycleRuntime = "2.6.0"
}

@Suppress("unused")
object Deps {
    const val core = "androidx.core:core-ktx:${Version.core}"
    const val androidxLifecycleRuntime =
        "androidx.lifecycle:lifecycle-runtime-ktx:${Version.lifecycleRuntime}"
    const val androidxActivityCompose = "androidx.activity:activity-compose:1.6.1"

    const val androidxLifecycleRuntimeCompose =
        "androidx.lifecycle:lifecycle-runtime-compose:${Version.lifecycleRuntime}"
    const val androidxComposeUi = "androidx.compose.ui:ui:1.3.3"
    const val androidxComposeUiTooling = "androidx.compose.ui:ui:1.3.3"
    const val androidxComposeMaterial = "androidx.compose.material:material:1.3.1"
    const val androidxLifecycleViewModelCompose =
        "androidx.lifecycle:lifecycle-viewmodel-compose:${Version.lifecycleRuntime}"
    const val androidxNavigationCompose = "androidx.navigation:navigation-compose:2.5.3"
    const val androidxAppcompat = "androidx.appcompat:appcompat:1.6.1"

    const val jackson = "com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2"
}

@Suppress("unused")
object Hilt {
    const val android = "com.google.dagger:hilt-android:2.45"
    const val compiler = "androidx.hilt:hilt-compiler:1.0.0"
    const val androidCompiler = "com.google.dagger:hilt-android-compiler:2.45"
    const val navigationCompose = "androidx.hilt:hilt-navigation-compose:1.0.0"
}

@Suppress("unused")
object TestDeps {

    const val junit = "junit:junit:${Version.junit}"
    const val androidxTest = "androidx.test.ext:junit:1.1.5"
    const val androidxEspresso = "androidx.test.espresso:espresso-core:3.5.1"
    const val androidxComposeUi = "androidx.compose.ui:ui-test-junit4:1.3.3"
    const val hiltAndroid = "com.google.dagger:hilt-android-testing:2.45"
}