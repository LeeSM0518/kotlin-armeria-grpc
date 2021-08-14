import com.google.protobuf.gradle.*

apply(plugin = "com.google.protobuf") // 해당 모듈에 protobuf 적용

// proto에서 발생하는 문제를 해결하기 위함
configurations.forEach {
    if (it.name.toLowerCase().contains("proto")) {
        it.attributes.attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class.java, "java-runtime"))
    }
}

dependencies {
    compileOnly("javax.annotation:javax.annotation-api:1.3.2")

    /***
     * scope
     * api : 해당 라이브러리를 다른 곳에서 사용할 수 있도록 한다.
     * implementation : 해당 라이브러리를 다른 곳에서 사용할 수 없다.
     */
    // protobuf 에서 제공해주는 기본 메시지를 자바로 컴파일된 것들
    api("com.google.protobuf:protobuf-java-util:3.17.0")

    // 코틀린으로 컴파일한것들을 사용하기 위한 것
    api("io.grpc:grpc-kotlin-stub:1.0.0")

    // gRPC를 사용하기 위한 기본 라이브러리
    api("io.grpc:grpc-protobuf:1.39.0")
    api("io.grpc:grpc-netty-shaded:1.39.0")
}

protobuf {
    generatedFilesBaseDir = "$projectDir/build/generated/source" // 컴파일된 저장되는 위치
    protoc { // proto 컴파일
        artifact = "com.google.protobuf:protoc:3.17.0"
    }
    plugins {
        id("grpc") {
            // java로 컴파일
            artifact = "io.grpc:protoc-gen-grpc-java:1.39.0"
        }
        id("grpckt") {
            // kotlin로 컴파일
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.0.0:jdk7@jar"
        }
    }

    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("grpc")
                id("grpckt")
            }

            it.generateDescriptorSet = true
            it.descriptorSetOptions.includeSourceInfo = true
            it.descriptorSetOptions.includeImports = true
            it.descriptorSetOptions.path = "$buildDir/resources/META-INF/armeria/grpc/service-name.dsc"
        }
    }
}

sourceSets {
    main {
        java.srcDir("build/generated/source/main/grpckt")
        java.srcDir("build/generated/source/main/grpc")
        java.srcDir("build/generated/source/main/java")
    }
}