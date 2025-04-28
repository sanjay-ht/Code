package com.dev.usersmanagementsystem.util;


import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class RandomService {
    private static final int OTP_MIN = 100000;
    private static final int OTP_MAX = 999999;
    private Random random = new Random();
    public  Integer createRandomOneTimePassword() {
        return OTP_MIN + random.nextInt(OTP_MAX - OTP_MIN + 1);
    }
}
