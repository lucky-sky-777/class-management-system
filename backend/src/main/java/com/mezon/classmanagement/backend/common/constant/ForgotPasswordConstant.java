package com.mezon.classmanagement.backend.common.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public final class ForgotPasswordConstant {

    @Value(value = "${forgot-password.otp-length}")
    public int OTP_LENGTH;

    @Value(value = "${forgot-password.otp-expiry-minutes}")
    public long OTP_EXPIRY_MINUTES;

    @Value(value = "${forgot-password.max-attempts}")
    public int MAX_ATTEMPTS;

}