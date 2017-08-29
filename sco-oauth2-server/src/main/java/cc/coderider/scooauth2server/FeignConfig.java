package cc.coderider.scooauth2server;

import cc.coderider.scooauth2server.innerconsumer.ProviderClient;
import feign.RequestInterceptor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.security.oauth2.client.feign.OAuth2FeignRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;

/**
 * Created by henry on 29/08/2017.
 * config 独立出来，防止 Feign构造的时候没有把RequestInterceptor构造进去
 */
@Configuration
public class FeignConfig {
    // 下面3个Bean 配合 application.yml 对应的EndPoint 需要加上 #oauth2.hasScope('server')
    @Bean
    @ConfigurationProperties(prefix = "security.oauth2.client")
    public ClientCredentialsResourceDetails clientCredentialsResourceDetails() {
        return new ClientCredentialsResourceDetails();
    }

    /**
     * // @FeignClient(configuration=FeignConfig.class 这样会导致 Interceptor 这个bean会执行两次
     * @return
     */
    @Bean
    public RequestInterceptor oauth2FeignRequestInterceptor(){
        return new OAuth2FeignRequestInterceptor(new DefaultOAuth2ClientContext(), clientCredentialsResourceDetails());
    }

    @Bean
    public OAuth2RestTemplate clientCredentialsRestTemplate() {
        return new OAuth2RestTemplate(clientCredentialsResourceDetails());
    }

    @Bean
    public ProviderClient.ProviderClientHystrix providerClientHystrix(){
        return new ProviderClient.ProviderClientHystrix();
    }
}
