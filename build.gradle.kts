plugins {
    kotlin("jvm") version "1.5.10"
    id("com.google.protobuf") version "0.8.15" // 플러그인 추가 (gRPC를 활용하기 위한 메시지)
}

allprojects {
    group = "io.wisoft"
    version = "2021.32"

    apply(plugin = "kotlin") // 플러그인 적용

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation(kotlin("stdlib"))
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
    }
}