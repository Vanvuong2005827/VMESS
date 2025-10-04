package com.vuong.vmess.domain.dto.request.auth;

import com.vuong.vmess.constant.ErrorMessage;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {
    @NotBlank(message = ErrorMessage.Validation.NOT_BLANK_FIELD)
    private String username;

    @NotBlank(message = ErrorMessage.Validation.NOT_BLANK_FIELD)
    private String password;
}

