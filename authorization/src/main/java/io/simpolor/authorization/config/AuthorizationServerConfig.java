package io.simpolor.authorization.config;

import io.simpolor.authorization.security.PasswordEncoding;
import io.simpolor.authorization.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import javax.sql.DataSource;

/**
 * OAuth 역할
 * - Resource Owner : 유저 혹은 서비스를 시용하는 고객
 * - Authorization Server : 인증 서버, 해당 사용자의 신원을 확인
 * - Resource Server : 자원(API 등)이 있는 서버, accessToken을 통해 자원을 활용할 수 있는지 확인
 * - Client : 서드파티앱
 */
@Configuration
@EnableAuthorizationServer // 인증 서버 활성화
@RequiredArgsConstructor
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Value("${security.oauth2.jwt.signkey}")
    private String signKey;

    @Value("${security.oauth2.client.client-id}")
    private String clientId;

    @Value("${security.oauth2.client.client-secret}")
    private String clientSecret;

    private final PasswordEncoding passwordEncoding;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final TokenStore tokenStore;
    private final DataSource dataSource;

    /* @Bean
    public TokenStore tokenStore() {
        // return new RedisTokenStore(jedisConnectionFactory());
        // new JdbcTokenStore(dataSource))
        return new InMemoryTokenStore();
    }*/

    /**
     * JWT 토큰 인증 방식
     * ( Jwt는 인증정보 자체가 관리되므로 저장소가 따로 필요하지 않음 )
     * @return
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter(){
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(signKey); // signKey 키를 이용한 변환
        return converter;
        // return new JwtAccessTokenConverter();
    }

    /**
     * Token 정보를 설정
     * access_token과 refresh_token을 저장하는 토큰 저장소
     * CRUD는 TokenStore 인터페이스로 구현하게 되어 있음
     * 구현체로 InMemoryTokenStore, JdbcTokenStore, RedisTokenStore 클래스가 제공
     * 인터페이스만 구현하면 되므로 어떠한 구현체를 사용해도됨
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .authenticationManager(authenticationManager)
                .userDetailsService(userService) // refresh 토큰을 사용할때, 재조회를 위하여 사용
                // .tokenStore(tokenStore);
                .accessTokenConverter(jwtAccessTokenConverter());
    }

    // oauth/check_token을 사용하기 위한 옵션

    /**
     * /oauth/check_token 사용하기 위한 설정
     * @param security
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security){
        security.tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()")
                .allowFormAuthenticationForClients();
    }

    /**
     * 클라이언트에 대한 설정 정보
     * @param clients
     * @throws Exception
     */
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

        clients.inMemory()
                .withClient(clientId)
                // .secret("testSecret")
                // PasswordEncoding을 사용할 경우 아래 둘 중에 하나를 사용해야함
                //.secret("{noop}testSecret")
                .secret(passwordEncoding.encode(clientSecret)) // 스프링 5.0 부터는 암호화해서 저장하는 것을 권장
                .redirectUris("/oauth2/callback")
                .authorizedGrantTypes("authorization_code", "password", "refresh_token", "client_credentials")
                .scopes("read", "write")
                .autoApprove(true)
                .accessTokenValiditySeconds(60 * 60 * 5)
                .refreshTokenValiditySeconds(60 * 60 * 24 * 120);

        /**
         * 클라이언트에 대한 설정 정보를 JDBC 클라이언트에 저장해서 사용
         * - client_id, client_secret 등을 저장하는 클라리언트 저장소
         * - CRUD : ClientDetailService 인터페이스로 구현
         * - 구현체 : InMemoryClientDetailService, JdbcClientDetailService 클래스를 제공
         */
        /* clients.jdbc(dataSource).passwordEncoder(passwordEncoding); // Jdbc 구현체 */
    }

    // Custom Jdbc Client 구현체
    /* @Bean
    public ClientDetailsService clientDetailsService() {
        return new JdbcClientDetailsService(dataSource);
    } */

    /**
     * 로그인 URL :
     * - http://localhost:9090/oauth/authorize?client_id=testClientId&redirect_uri=/oauth2/callback&response_type=code&scope=read
     */

    /**
     * --------------------------------------------------------
     * ClientDetailsServiceConfigurer 설정 정보
     * --------------------------------------------------------
     * withClient : 클라이언트 아이디
     *
     * secret : 클라이언트 시크릿
     *
     * redirectUris : 인증 완료 후 인증 결과를 수신한 URL에 code 값을 전달
     * - redirectUris과 요청시 redirect_uri이 동일해야함
     * - 요청시 redirect_uri 파라미터에 값이 없을 경우 redirectUris 자동으로 인식
     *
     * authorizedGrantTypes : 인증 방식 ( 개인적인 생각으로는 password와 refresh만 제공하면 될 것으로 판단 )
     * - authorization_code : 권한 코드 방식
     *   + authorization_code 방식을 사용할 경우 redirect_uri 반드시 설정해줘여함 redirect_url로 code를 받고 유효성음 검증하기 때문임
     *   + 가장 대중적인 방식으로 Service Provider가 제공하는 인증 화면에 로그인하면, 클라이언트 앱이 요청하는 리소스에 접근 요청을 승인하면
     *   + 지정한 redirect_uri로 code를 넘겨주는데, 해당  code로 acess_token을 얻음
     * - implict : 암묵적인 동의 방식
     *   + authorization_code와 비슷하나, 인증 후 redirect_uri로 직접 access_token을 전달 받으므로, 전체적으로 간단해지나 보안성은 떨어짐
     * - password credential : 사용자 소유자 비밀번호 방식
     *   + Resource Owner가 직접 Client에 아이디와 패스워드를 입력하고 인증 서버로 해당 정보로 인증받아 access_token을 직접 얻어오는 방식
     *   +  아이디 패스워가 노출되므로 보안성이 떨이짐
     * - client credential : 클라이언트 인증 방식
     *   + 정해진 인증 key(secret)로 요청하며, 일반적으로 서버간의 통신할 때 사용 됨
     * - refresh_token : 리프래쉬 토큰을 통한 발급 방식
     * - 방식별 차이 : https://daddyprogrammer.org/post/1239/spring-oauth-authorizationserver/#google_vignette
     *
     * scopes : 해당 클라이언트의 접근 범위 / accessToken으로 접근할 수 있는 리소스 범위
     * - resource 서버에서 제한하거나 노출 시킬 수 있음
     * - 종류 : read, write, trust
     *
     * autoApprove : OAuth Approval 화면 나오지 않게 처리
     * - 로그인 후 scopes 권한 선택화면 노출 여부
     *
     * accessTokenValiditySeconds :  access token 유효 기간 (초 단위)
     *
     * refreshTokenValiditySeconds : refresh token 유효 기간 (초 단위)
     */

    /**
     * /oauth/authorize :
     * - 사용자의 브라우저 또는 앱에 보여줄 리다이렉트 웹페이지
     * - 사용자에게 로그인과 사용할 데이터에 대한 동의를 구함
     * - 로그인 후 응답
     *   _csrf=2ed1dfa5-3959-4560-a72d-c9f364696fc3
     *   authorize=Authorize
     *   scope.read=true
     *   scope.write=true
     *   user_oauth_approval=true
     *
     * /oauth/token
     * - 클라이언트 인증 후 access_token을 발급
     * - Basic Auth로 요청(client_id, client_secret), grant_type이 필수 파라메터로 요구됨
     * - 발급 요청
     *   $ curl -X POST "http://localhost:8080/oauth/token" \
     *          -H 'authorization: Basic c2VydmljZS1hY2NvdW50LTE6c2VydmljZS1hY2NvdW50LTEtc2VjcmV0' \
     *          -d "grant_type=client_credentials"
     * - 발급 응답
     *   {
     *     "access_token": "bcf570e5-0f2f-481b-a2ae-6d57fa7f711c",
     *     "token_type": "bearer",
     *     "expires_in": 38010,
     *     "scope": "read:current_user read:users"
     *   }
     *
     * /oauth/check_token
     * - 요청 파라미터의 access_token 유효 여부와 유효시 해당 클라이언트 정보를 응답
     * - 발급 요청
     *   $ curl -X POST "http://localhost:8080/oauth/check_token" \
     *          -d "token=067d81ec-6c68-42f3-afc6-97e4b6dc2dd6"
     * - 발급 응답
     *   {
     *     "scope": [
     *         "read",
     *         "write"
     *     ],
     *     "exp": 1499199182,
     *     "client_id": "some_client_id"
     *   }
     */
}
