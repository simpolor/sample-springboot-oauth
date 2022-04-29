package io.simpolor.authorization.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.ByteStreams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.savedrequest.Enumerator;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/***
 * 인증 요청시 특정 URL 및 content-type의 변환시키기 위해 사용되는 필터
 * ( application/json은 지원하지 않으므로 application/x-www-form-urlencoded로 변환시켜줘여함 )
 */
@Slf4j
@Component
public class JsonToUrlEncodedAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (Objects.equals(request.getServletPath(), "/oauth/token") && request.getContentType().startsWith("application/json")) {
            byte[] json = ByteStreams.toByteArray(request.getInputStream());
            Map<String, String> jsonMap = new ObjectMapper().readValue(json, Map.class);
            Map<String, String[]> parameters
                    = jsonMap.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> new String[]{e.getValue()})
                    );

            if (parameters.containsKey("grantType")) {
                String[] grantType = parameters.remove("grantType");
                parameters.put("grant_type", grantType);
            }

            HttpServletRequest requestWrapper = new RequestWrapper(request, parameters);

            try {
                filterChain.doFilter(requestWrapper, response);
            } catch(Exception e) {
                logger.error(e);
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private class RequestWrapper extends HttpServletRequestWrapper {

        private final Map<String, String[]> params;

        RequestWrapper(HttpServletRequest request, Map<String, String[]> params) {
            super(request);
            this.params = params;
        }

        @Override
        public String getParameter(String name) {
            if (this.params.containsKey(name)) {
                return this.params.get(name)[0];
            }
            return "";
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            return this.params;
        }

        @Override
        public Enumeration<String> getParameterNames() {
            return new Enumerator<>(params.keySet());
        }

        @Override
        public String[] getParameterValues(String name) {
            return params.get(name);
        }
    }
}
