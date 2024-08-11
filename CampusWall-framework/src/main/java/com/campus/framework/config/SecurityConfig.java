package com.campus.framework.config;

import com.campus.framework.filter.JwtAuthenticationTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    @Autowired
    AuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    AccessDeniedHandler accessDeniedHandler;

    /**
     * 配置密码编码器
     * 使用BCrypt算法对密码进行加密
     * @return 返回配置好的密码编码器
     */
//    @Bean
//    public PasswordEncoder passwordEncoder(){
//        return new BCryptPasswordEncoder();
//    }
//

    /**
     * 配置密码编码器
     * 使用NoOpPasswordEncoder不对密码进行加密
     * @return 返回配置好的密码编码器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance(); // 使用 NoOpPasswordEncoder 不进行密码加密
    }

    /**
     * 配置HTTP安全策略
     * @param http HttpSecurity对象
     * @throws Exception 配置过程中可能抛出的异常
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 关闭csrf
        http.csrf().disable()
        // 不通过Session获取SecurityContext
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authorizeRequests()
        // 对于登录接口 允许匿名访问
        .antMatchers("/doc.html", "/doc.html/**", "/login", "/swagger-ui.html", "/webjars/**", "/v2/**", "/swagger-resources/**").permitAll()
        .antMatchers("/logout").authenticated()
        // 除上面外的所有请求都需要认证才能访问
        .anyRequest().authenticated();
        //注销接口需要认证才能访问
//        // 对于登录接口 允许匿名访问
//        .antMatchers("/doc.html#/**","/login","/swagger-ui.html", "/webjars/**", "/v2/**", "/swagger-resources/**").anonymous()
//        // 除上面外的所有请求全部不需要认证即可访问
//        .anyRequest().permitAll();
        //配置异常处理器
        http.exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler);
        // 禁用登出功能
        http.logout().disable();
        // 把jwtAuthenticationTokenFilter添加到SpringSecurity的过滤器链中
        http.addFilterBefore(jwtAuthenticationTokenFilter,
                UsernamePasswordAuthenticationFilter.class);
        // 允许跨域
        http.cors();
    }


    /**
     * 创建AuthenticationManager对象
     * @return 返回AuthenticationManager对象
     * @throws Exception 配置过程中可能抛出的异常
     */
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}


