package com.artineer.spring_lecture_week_2.exception;

import com.artineer.spring_lecture_week_2.vo.ApiCode;
import org.apache.logging.log4j.util.Strings;
import org.springframework.lang.Nullable;

import java.util.Objects;

public class Asserts {
    public static void isNull(@Nullable Object obj, ApiCode code, String msg) {
        if(Objects.isNull(obj)) {
            throw new ApiException(code, msg);
        }
    }

    public static void isBlank(@Nullable String str, ApiCode code, String msg) {
        if(Strings.isBlank(str)) {
            throw new ApiException(code, msg);
        }
    }
}
