plugins {
    id("java")
    id("org.springframework.boot") version "3.5.13"
    id("io.spring.dependency-management") version "1.1.4"

}

group = "org.dev.ssc"
version = "0.01-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

configurations {
    all {
        exclude(group="org.springframework.boot", module="spring-boot-starter-logging")
    }
}

dependencies {
//    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("com.lmax:disruptor:3.4.4")
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.8.15")

    testImplementation("org.junit.jupiter:junit-jupiter")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // YAML 설정을 읽기 위한 필수 라이브러리
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")
    implementation("com.fasterxml.jackson.core:jackson-databind")

    implementation("com.github.davidmoten:rtree2:0.9.3")
}




tasks.test {
    useJUnitPlatform()
}



// 자바 컴파일 인코딩 설정
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

// 실행 시 인코딩 설정 (bootRun 혹은 JavaExec)
tasks.withType<JavaExec> {
    jvmArgs("-Dfile.encoding=UTF-8", "-Dconsole.encoding=UTF-8")
}
