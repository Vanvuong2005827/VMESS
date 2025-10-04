package com.vuong.vmess.service.impl;

import com.vuong.vmess.constant.ErrorMessage;
import com.vuong.vmess.domain.entities.User;
import com.vuong.vmess.exception.extended.NotFoundException;
import com.vuong.vmess.repository.UserRepository;
import com.vuong.vmess.security.UserPrincipal;
import com.vuong.vmess.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_USERNAME,
                        new String[]{username}));
        return UserPrincipal.create(user);
    }
}
