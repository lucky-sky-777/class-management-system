package com.mezon.classmanagement.backend.common.util;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class EmailService {

    JavaMailSender javaMailSender;

    @Async
    public void sendOtpEmail(String toEmail, String otpCode, long expiryMinutes) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("[Class Management] Mã xác nhận đặt lại mật khẩu");
        message.setText(
                """
                Xin chào,

                Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn.

                MÃ XÁC NHẬN
                --------------------
                       %s
                --------------------

                Mã này có hiệu lực trong %d phút và chỉ được sử dụng một lần.
                Vui lòng không chia sẻ mã xác nhận này với bất kỳ ai.

                Nếu bạn không thực hiện yêu cầu trên, hãy bỏ qua email này. Mật khẩu của bạn vẫn được giữ nguyên.

                Trân trọng,
                Class Management System
                """.formatted(otpCode, expiryMinutes)
        );
        javaMailSender.send(message);
    }

}
