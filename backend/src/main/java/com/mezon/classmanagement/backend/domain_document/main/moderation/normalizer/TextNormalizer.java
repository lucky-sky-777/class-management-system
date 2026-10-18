package com.mezon.classmanagement.backend.domain_document.main.moderation.normalizer;

import java.text.Normalizer;
import java.util.Locale;

public class TextNormalizer {
    public String normalize(String text) {
        if (text == null) {
            return "";
        }

        //Chuẩn hóa Unicode tiếng việt
        text = Normalizer.normalize(
                text,
                Normalizer.Form.NFC
        );

        //Chuẩn hóa chữ thường
        text = text.toLowerCase(Locale.ROOT);

        //Gom nhiều khoảng tráng thành 1
        text = text.replaceAll("\\s+", " ").trim();


        return text;
    }
}
