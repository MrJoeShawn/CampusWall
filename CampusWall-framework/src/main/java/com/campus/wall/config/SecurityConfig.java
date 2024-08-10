package com.campus.wall.config;

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

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

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
        http
                //关闭csrf
                .csrf().disable()
                //不通过Session获取SecurityContext
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                // 对于登录接口 允许匿名访问
                .antMatchers("/login","/swagger-ui.html","/doc.html#/**", "/webjars/**", "/v2/**", "/swagger-resources/**").anonymous()
                // 除上面外的所有请求全部不需要认证即可访问
                .anyRequest().permitAll();
        http.logout().disable();
        //允许跨域
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


