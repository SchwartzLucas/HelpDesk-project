package schwartz.spring.app.services;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import schwartz.spring.Utils.Filter;
import schwartz.spring.Utils.Utils;
import schwartz.spring.app.domain.user.User;
import schwartz.spring.app.domain.user.UserListRequest;
import schwartz.spring.app.domain.user.UserRegisterRequest;
import schwartz.spring.app.infra.PublicIdGenerator;
import schwartz.spring.app.repository.DynamicQueryBuilder;
import schwartz.spring.app.repository.UserRepository;

import java.util.List;

@Service
public class UserService {
    private final static Integer ACTIVE_USER = 1;
    private final UserRepository userRepository;
    private final DynamicQueryBuilder DB;
    private final PublicIdGenerator publicIdGenerator;

    public UserService(UserRepository userRepository, DynamicQueryBuilder db, PublicIdGenerator publicIdGenerator) {
        this.userRepository = userRepository;
        DB = db;
        this.publicIdGenerator = publicIdGenerator;
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

    public void update() {

    }

    @Modifying(clearAutomatically = true)
    @Transactional
    public User register(UserRegisterRequest request) {
        if (userRepository.findByLogin(request.login()) != null) {
            return null;
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(request.password());
        User newUser = new User(request.login(), encryptedPassword, request.role(), publicIdGenerator.generate());
        userRepository.saveAndFlush(newUser);
        newUser.setPublicCode(String.format("USR-%08d", newUser.getId()));
        return userRepository.save(newUser);
    }
}
