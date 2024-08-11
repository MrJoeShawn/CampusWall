package com.campus.framework.untils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.UUID;

public class JwtUtil {

    // 定义JWT的有效期为24小时
    public static final Long JWT_TTL = 24 * 60 * 60 * 1000L; // 24小时的毫秒数
    // 定义JWT的密钥
    public static final String JWT_KEY = "MrJoe";

    /**
     * 生成UUID作为JWT的唯一标识
     * @return 生成的UUID字符串
     */
    public static String getUUID() {
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        return token;
    }

    /**
     * 创建JWT令牌，使用默认的过期时间和UUID
     * @param subject 要存放在token中的数据（JSON格式）
     * @return 生成的JWT令牌字符串
     */
    public static String createJWT(String subject) {
        JwtBuilder builder = getJwtBuilder(subject, null, getUUID()); // 设置过期时间
        return builder.compact();
    }

    /**
     * 创建JWT令牌，自定义过期时间
     * @param subject 要存放在token中的数据（JSON格式）
     * @param ttlMillis token的过期时间，单位为毫秒
     * @return 生成的JWT令牌字符串
     */
    public static String createJWT(String subject, Long ttlMillis) {
        JwtBuilder builder = getJwtBuilder(subject, ttlMillis, getUUID()); // 设置过期时间
        return builder.compact();
    }

    /**
     * 内部方法，用于构建JWT
     * @param subject 要存放在token中的数据（JSON格式）
     * @param ttlMillis token的过期时间，单位为毫秒
     * @param uuid JWT的唯一标识
     * @return JwtBuilder对象，用于构建JWT
     */
    private static JwtBuilder getJwtBuilder(String subject, Long ttlMillis, String uuid) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        SecretKey secretKey = generalKey();
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        if (ttlMillis == null) {
            ttlMillis = JwtUtil.JWT_TTL;
        }
        long expMillis = nowMillis + ttlMillis;
        Date expDate = new Date(expMillis);
        return Jwts.builder()
                .setId(uuid) // 唯一的ID
                .setSubject(subject) // 主题，可以是JSON数据
                .setIssuer("MrJoe") // 签发者
                .setIssuedAt(now) // 签发时间
                .signWith(signatureAlgorithm, secretKey) // 使用HS256算法签名
                .setExpiration(expDate); // 设置过期时间
    }

    /**
     * 创建JWT令牌，自定义ID、过期时间和UUID
     * @param id JWT的唯一标识
     * @param subject 要存放在token中的数据（JSON格式）
     * @param ttlMillis token的过期时间，单位为毫秒
     * @return 生成的JWT令牌字符串
     */
    public static String createJWT(String id, String subject, Long ttlMillis) {
        JwtBuilder builder = getJwtBuilder(subject, ttlMillis, id); // 设置过期时间
        return builder.compact();
    }

    public static void main(String[] args) throws Exception {
        // 生成JWT
        String jwt = createJWT("123456");
        System.out.println("Generated JWT: " + jwt);

        // 解析JWT
        Claims claims = parseJWT(jwt);
        String subject = claims.getSubject();
        System.out.println("Parsed Subject: " + subject);

        // 生成并输出加密后的密码
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String rawPassword = "123456";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        System.out.println("Encoded Password: " + encodedPassword);
    }

    /**
     * 生成加密后的密钥
     * @return 加密后的密钥
     */
    public static SecretKey generalKey() {
        // 直接使用字符串字节数组作为密钥
        byte[] encodedKey = JwtUtil.JWT_KEY.getBytes();
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return key;
    }


    /**
     * 解析JWT令牌并返回其中的claims
     * @param jwt 要解析的JWT字符串
     * @return Claims对象，包含JWT中的数据
     * @throws Exception 如果解析失败，抛出异常
     */
    public static Claims parseJWT(String jwt) throws Exception {
        SecretKey secretKey = generalKey();
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwt)
                .getBody();
    }

}
