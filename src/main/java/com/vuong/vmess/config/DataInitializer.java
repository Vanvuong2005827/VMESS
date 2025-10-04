package com.vuong.vmess.config;

import com.vuong.vmess.constant.RoleConstant;
import com.vuong.vmess.domain.entities.Role;
import com.vuong.vmess.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role(null, RoleConstant.ADMIN, null));
            roleRepository.save(new Role(null, RoleConstant.USER, null));
            roleRepository.save(new Role(null, RoleConstant.LEADER, null));
        }
    }
}
