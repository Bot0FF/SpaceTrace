package org.botoff.service;

import lombok.RequiredArgsConstructor;
import org.botoff.entity.Role;
import org.botoff.repository.RoleRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Role getUserRole() {
        return roleRepository.findByName("ROLE_USER").get();
    }

}
