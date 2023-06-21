package org.eclipse.jakarta.utils;

import java.util.Date;

import org.eclipse.jakarta.Const;
import org.eclipse.jakarta.model.Account;

import io.jsonwebtoken.Jwts;

public final class PiggyUtils {

    public static String makeJwt(Account account) {
        // JWT avec 1h de validité
        return Jwts.builder()
                .claim("name", account.getUsername())
                .claim("userId", String.valueOf(account.getId()))
                .setSubject(account.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(Const.key)
                .compact();
    }

    public static Boolean verifyJWT(String authorizationHeader) {
        String bearerToken = "";
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            bearerToken = authorizationHeader.substring("Bearer ".length()).trim();
        } else {
            return false;
        }
        try {

            Jwts.parserBuilder()
                    .setSigningKey(Const.key)
                    .build()
                    .parseClaimsJws(bearerToken);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

}