# spring-cloud oauth2 example

## Step 1:生成几个必须的服务
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
sInit sco-consumer Consumer "Consumer service" cloud-eureka,cloud-feign,cloud-hystrix,cloud-hystrix


```


## Step 2: OAuth2 Server
使用jwt，所以要先生成个key, 然后把pubkey导出来，后面分发给每个微服务使用
```bash
cd sco-oauth2-server/src/main/resources
keytool -genkeypair -alias coderider -keyalg RSA -dname "CN=coderider, L=GuangZhou, ST=GuangDong, C=CN" -keypass mySecretKey -keystore keystore.jks -storepass mySecretKey

keytool -list -rfc --keystore keystore.jks | openssl x509 -inform pem -pubkey
# Enter keystore password:  mySecretKey

# 复制 PUBLIC KEY 到 src/main/resources/keystore_pub.cert
# 也可以用jhipster 那种通过rest来拿pubkey,好处就是不用到处复制，修改方便；弊端就是全部都要依赖oauth 服务才能启动
```
用SampleSecureOAuth2ApplicationTests 测试一遍

## Step 3: Provider Service
是一个ResourceServer，暂时没加注册中心,此时的
ProviderSelfTest 中 division公开返回200，而add返回401
```java
    @Test
    public void division() throws Exception {
        this.mvc.perform(get("/division").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andDo(print());
    }

    @Test
    public void add() throws Exception {
        this.mvc.perform(get("/add").accept(MediaType.APPLICATION_JSON))
```