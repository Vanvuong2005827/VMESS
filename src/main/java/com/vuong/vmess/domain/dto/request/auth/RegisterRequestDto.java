package com.vuong.vmess.domain.dto.request.auth;

import com.vuong.vmess.constant.ErrorMessage;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDto {
    @NotBlank(message = ErrorMessage.Validation.NOT_BLANK_FIELD)
    @Size(min = 3, max = 32, message = "Username must be 3-32 characters")
    @Pattern(
            regexp = "^[A-Za-z0-9._-]+$",
            message = "Username can only contain letters, numbers, dot, underscore and hyphen"
    )
    private String username;

    @NotBlank(message = ErrorMessage.Validation.NOT_BLANK_FIELD)
    @Size(min = 8, max = 64, message = "Password must be 8-64 characters")
    @Pattern(
            regexp = "^(?=\\S+$)(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
            message = "Password must contain uppercase, lowercase, number, special character and no spaces"
    )
    private String password;

    @Email(message = "Email is not valid")
    @NotBlank(message = ErrorMessage.Validation.NOT_BLANK_FIELD)
    @Size(max = 254, message = "Email must be less than 254 characters")
    private String email;
}
