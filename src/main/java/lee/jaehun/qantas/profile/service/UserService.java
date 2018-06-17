package lee.jaehun.qantas.profile.service;

import lee.jaehun.qantas.profile.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService extends UserDetailsService {
    User findByUsername(String username) throws UsernameNotFoundException;
}
