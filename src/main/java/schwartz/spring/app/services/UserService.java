package schwartz.spring.app.services;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import schwartz.spring.Utils.Filter;
import schwartz.spring.Utils.Utils;
import schwartz.spring.app.domain.user.User;
import schwartz.spring.app.domain.user.UserListRequest;
import schwartz.spring.app.repository.DynamicQueryBuilder;
import schwartz.spring.app.repository.UserRepository;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final DynamicQueryBuilder DB;

    public UserService(UserRepository userRepository, DynamicQueryBuilder db) {
        this.userRepository = userRepository;
        DB = db;
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
            spec = spec.and(((root, query, cb) ->
                    cb.equal(root.get("is_active"), request.active())));
        }
        if (!Utils.isEmpty(request.login())) {
            spec = spec.and(((root, query, cb) ->
                    cb.like(root.get("login"), request.login())));
        }
        if (!Utils.isEmpty(request.public_code())) {
            spec = spec.and(((root, query, cb) ->
                    cb.like(root.get("public_code"), request.public_code())));
        }
        if (!Utils.isEmpty(request.role())) {
            spec = spec.and(((root, query, cb) ->
                    cb.equal(root.get("role"), request.role())));
        }

        Filter CreatedTimeUser = request.filter();
        if (!Utils.isEmpty(CreatedTimeUser) && "create_time".equals(request.filter().property())) {
            spec = DB.dateTimeQuery(CreatedTimeUser, "create_time", spec);
        }

        return userRepository.findAll(spec);

    }
}
