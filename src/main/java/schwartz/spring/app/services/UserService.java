package schwartz.spring.app.services;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import schwartz.spring.Exceptions.LoginException;
import schwartz.spring.Utils.Filter;
import schwartz.spring.Utils.Utils;
import schwartz.spring.app.auth.services.TokenService;
import schwartz.spring.app.domain.user.*;
import schwartz.spring.app.infra.PublicIdGenerator;
import schwartz.spring.app.repository.DynamicQueryBuilder;
import schwartz.spring.app.repository.UserRepository;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final DynamicQueryBuilder DB;
    private final PublicIdGenerator publicIdGenerator;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public UserService(UserRepository userRepository, DynamicQueryBuilder db, PublicIdGenerator publicIdGenerator, AuthenticationManager authenticationManager, TokenService tokenService) {
        this.userRepository = userRepository;
        this.DB = db;
        this.publicIdGenerator = publicIdGenerator;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    public User getAuthenticatedUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            return user;
        }

        return null;
    }

    public List<User> list(UserListRequest request) {
        if (Utils.isEmpty(request)) {
            return userRepository.findAll();
        }

        Specification<User> spec = Specification.where((root, query, cb) -> cb.conjunction());

        if (!Utils.isEmpty(request.active())) {
            spec = spec.and(((root, query, cb) -> cb.equal(root.get("is_active"), request.active())));
        }
        if (!Utils.isEmpty(request.login())) {
            spec = spec.and(((root, query, cb) -> cb.like(root.get("login"), request.login())));
        }
        if (!Utils.isEmpty(request.public_code())) {
            spec = spec.and(((root, query, cb) -> cb.like(root.get("public_code"), request.public_code())));
        }
        if (!Utils.isEmpty(request.role())) {
            spec = spec.and(((root, query, cb) -> cb.equal(root.get("role"), request.role())));
        }

        Filter CreatedTimeUser = request.filter();
        if (!Utils.isEmpty(CreatedTimeUser) && "create_time".equals(request.filter().property())) {
            spec = DB.dateTimeQuery(CreatedTimeUser, "create_time", spec);
        }

        return userRepository.findAll(spec);

    }

    public User update(UUID user_public_id, UserUpdateRequest request) {
        if (Utils.isEmpty(user_public_id)) {
            // TODO exception -> invalid selection user
            return null;
        }
        User updated_user = userRepository.findByPublicId(user_public_id);
        if (Utils.isEmpty(updated_user)) {
            // TODO exception -> user not exists
            return null;
        }
        if (!Utils.isEmpty(request)) {
            User request_user = this.getAuthenticatedUser();
            boolean admin_user = "ADMIN_USER".equals(request_user.getRole().getRole());
            boolean same_user = request_user.getPublicId().equals(user_public_id);
            if (admin_user) {
                if (!Utils.isEmpty(request.password())) {
                    String encryptedPassword = new BCryptPasswordEncoder().encode(request.password());
                    updated_user.setPassword(encryptedPassword);
                }
                if (!Utils.isEmpty(request.isActive())) {
                    updated_user.setIsActive(request.isActive());
                }
                if (!Utils.isEmpty(request.login())) {
                    updated_user.setLogin(request.login());
                }
                if (!Utils.isEmpty(request.role())) {
                    updated_user.setRole(request.role());
                }
                if (!Utils.isEmpty(request.team_id())) {
                    updated_user.setTeamId(request.team_id());
                }
                if (!Utils.isEmpty(request.client_id())) {
                    updated_user.setClientId(request.client_id());
                }
            } else if (same_user) {
                if (!Utils.isEmpty(request.password())) {
                    String encryptedPassword = new BCryptPasswordEncoder().encode(request.password());
                    updated_user.setPassword(encryptedPassword);
                }
            }

            return userRepository.save(updated_user);

        }
        // TODO exception -> nothing updated
        return null;

    }

    @Modifying(clearAutomatically = true)
    @Transactional
    public User register(UserRegisterRequest request) {
        if (!Utils.isEmpty(userRepository.findByLogin(request.login()))) {
            // TODO exception -> usuário já existe
            return null;
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(request.password());
        User newUser = new User(request.login(), encryptedPassword, request.role(), publicIdGenerator.generate());
        userRepository.saveAndFlush(newUser);
        newUser.setPublicCode(String.format("USR-%08d", newUser.getId()));
        return userRepository.save(newUser);
    }

    public String login(AuthenticationRequest request) {
        try {
            var userNamePassword = new UsernamePasswordAuthenticationToken(request.login(), request.password());
            var auth = this.authenticationManager.authenticate(userNamePassword);
            return tokenService.generateToken((User) auth.getPrincipal());
        } catch (BadCredentialsException e) {
            throw new LoginException("Login ou senha inválidos");
        }

    }
}
