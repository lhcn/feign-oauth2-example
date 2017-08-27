# spring-cloud oauth2 example

## 先生成几个必须的服务
- sco-registry Eureka 注册中心
- sco-oauth2-server oauth2服务
- sco-provider 服务1
- sco-comsumer 服务2

```bash
#!/bin/bash
JAVA_VERSION=1.8
BOOT_VERSION=1.5.6.RELEASE
GROUP_ID=cc.coderider
CLOUD_BASE_DEPENDENCE=web,devtools,cloud-starter,cloud-security,cloud-oauth2
function sInit(){
    artifactId=$1;baseDir=$1
    name=$2
    description=$3
    dependencyX=$4

    curl https://start.spring.io/starter.tgz -d language=java -d type=maven-project \
    -d javaVersion=${JAVA_VERSION} -d bootVersion=${BOOT_VERSION} \
    -d groupId=${GROUP_ID} -d artifactId=${artifactId} -d baseDir=${artifactId} -d name=${name} \
    -d dependencies=${CLOUD_BASE_DEPENDENCE},${dependencyX} \
    -d description=${description} \
    |tar -xzvf -
}

sInit sco-registry RegistryServer "Eureka server" cloud-eureka-server
sInit sco-oauth2-server OAuth2Server "Oauth2 server" cloud-eureka
sInit sco-provider Provider "Provider service" cloud-eureka
sInit sco-consumer Consumer "Consumer service" cloud-eureka,cloud-hystrix


```


