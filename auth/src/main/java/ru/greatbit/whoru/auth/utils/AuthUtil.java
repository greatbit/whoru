package ru.greatbit.whoru.auth.utils;

import java.security.NoSuchAlgorithmException;

import static ru.greatbit.utils.string.StringUtils.getMd5String;

public final class AuthUtil {

    private AuthUtil() {

    }

    public static String getMd5(String input, String salt) throws NoSuchAlgorithmException {
        return getMd5String(input + salt);
    }
}
