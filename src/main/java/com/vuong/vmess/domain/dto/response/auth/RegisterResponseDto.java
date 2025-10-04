package com.vuong.vmess.domain.dto.response.auth;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterResponseDto {
    String username;
    String role;
    String email;
}