# oauth2 feign example

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
sInit sco-consumer Consumer "Consumer service" cloud-eureka,cloud-feign,cloud-hystrix


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

## Step 4: Registry
Registry,请开始你的表演。
@FeignClient里面有用serviceName离不开Registry,所以先把Registry准备好。
本来一个 @EnableEurekaServer就表演完毕了，不过在这补充一下说明：

这份代码是完全写好才分开提交的，所以注意一下几点
1. 有些代码和 application.yml 里面已经配置了registry,提交前先false了，所以这次直接改fetch-registry: true.
```yml
eureka:
  client:
    register-with-eureka: false
    fetch-registry: true
    service-url:
      defaultZone: http://localhost:${server.port}/eureka/

```
2. application.name如果不用Registry其实是可以不填的，但是也早已提交
```yml
spring:
  application:
    name: oauth2-service
```
3. 还有 @EnableEurekaClient等注解也在前一次已经提交

## Step 5: Consumer Service
### ProviderClient(顺手加了个hystrix,还没测试，不了解的可以忽略）
```java
@FeignClient(value = "provider-service", fallback = ProviderClient.ProviderClientHystrix.class)
public interface ProviderClient {

    // 不加 @RequestParam 的value也报错
    @GetMapping("/add")
    CalcResult add(
            @RequestParam(value = "a",required = false, defaultValue = "0") int a,
            @RequestParam(value = "a",required = false, defaultValue = "0") int b);
...
}
```
### FeignClient结合oauth2的关键代码，就是加一个拦截器使用 client_credentials 去获取 token
#### ClientCredentials写到了yml
```yml
security:
  oauth2:
    client:
      clientId: micro-service
      clientSecret: service-to-service-call-secret
      accessTokenUri: http://localhost:6660/oauth/token
      grant-type: client_credentials
      scope: service
    resource:
      user-info-uri: http://localhost:6660/me
```

#### 而拦截器直接配置即可，没有的话就是调feign时401（因为不会自己拿token和加token)
```java
public class ResourceServerConfig {
...
    // 下面3个Bean 配合 application.yml 对应的EndPoint 需要加上 #oauth2.hasScope('server')
    @Bean
    @ConfigurationProperties(prefix = "security.oauth2.client")
    public ClientCredentialsResourceDetails clientCredentialsResourceDetails() {
        return new ClientCredentialsResourceDetails();
    }

    @Bean
    public RequestInterceptor oauth2FeignRequestInterceptor(){
        return new OAuth2FeignRequestInterceptor(new DefaultOAuth2ClientContext(), clientCredentialsResourceDetails());
    }

    @Bean
    public OAuth2RestTemplate clientCredentialsRestTemplate() {
        return new OAuth2RestTemplate(clientCredentialsResourceDetails());
    }
...
```
jhipster 自己写了个@AuthorizedFeignClient，能实现同样的功能

#### 运行Test
```java
@Test
public void calc() throws Exception {
    this.mvc.perform(get("/calc").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andDo(print());
}
```

## Step 6 DONE

值得一提的几个坑
- oauth2 server 和 resource server 同时存在是，过滤器顺序的问题
    ```yml
    security:
      oauth2:
        resource:
          filter-order: 3 # 当OAuth2server 和 ResourceServer同时存在是需要考虑这个优先级的问题
      ```
- resource server 里面权限和client_credentials 声明的要一致
```java
@RestController
public class ProviderController {

    @PreAuthorize("#oauth2.hasScope('service') or hasAuthority('system')")
    @GetMapping("/add")
....
}

public class WebSecurityConfig{ // in sco-oauth2-server
...
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.inMemoryAuthentication()
                    .withUser("user1").password("password1").roles("USER")
                    .and()
                    .withUser("user2").password("password2").roles("USER")
                    .authorities("provider.times"); //如果没有给clientId加Authority 那就用scope来区分也可以@PreAuthorized('#oauth2.hasScope('service')
            //auth.parentAuthenticationManager(authenticationManager);
    }

...
}
```