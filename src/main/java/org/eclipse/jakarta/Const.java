package org.eclipse.jakarta;

import java.security.Key;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public final class Const {
    public static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
}
