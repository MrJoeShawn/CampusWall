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
        // 禁用 CSRF 防护，因为使用 JWT 不需要 CSRF 保护
        http.csrf().disable()

                // 配置 session 策略为无状态 (STATELESS)，因为我们使用 JWT 实现身份验证，而不使用 session 存储用户信息
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                // 配置 URL 权限控制
                .authorizeRequests()
                // 允许访问以下路径，无需认证
                .antMatchers("/doc.html", "/doc.html/**", "/login", "/swagger-ui.html", "/webjars/**", "/v2/**", "/swagger-resources/**").permitAll()
                // 只有认证用户才能访问以下路径
                .antMatchers("/logout").authenticated()                  // 登出
                .antMatchers("/upload").authenticated()                  // 文件上传
                .antMatchers("/uploadUserHeaderImg").authenticated()     // 上传用户头像
                .antMatchers("/userInfo").authenticated()                // 用户信息
                .antMatchers("/creatrdynamic").authenticated()           // 创建动态
                .antMatchers("/like").authenticated()                    // 喜欢动态
                .antMatchers("/collect").authenticated()                 // 收藏动态
                .antMatchers("/dynamic/list").permitAll()                    // 查看动态
                .antMatchers("/dynamic/**").authenticated()              // 查看动态
                .antMatchers("/comment").authenticated()
                // 其他所有请求默认允许
                .anyRequest().permitAll();

        // 配置异常处理器，当认证失败或授权失败时的响应处理
        http.exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)      // 认证失败时调用该入口
                .accessDeniedHandler(accessDeniedHandler);               // 授权失败时调用该处理器

        // 禁用 Spring Security 的默认登出功能，因为我们自定义了登出逻辑
        http.logout().disable();

        // 在请求过滤链中添加 JWT 认证过滤器，该过滤器会在用户名密码认证过滤器之前执行
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

        // 启用跨域请求支持
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


