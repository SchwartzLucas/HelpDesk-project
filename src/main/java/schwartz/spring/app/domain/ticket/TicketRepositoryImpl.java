package schwartz.spring.app.domain.ticket;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import schwartz.spring.Utils.Utils;
import schwartz.spring.app.repository.TicketRepositoryCustom;
import schwartz.spring.auth.domain.user.User;
import schwartz.spring.auth.domain.user.UserRole;

import java.util.ArrayList;
import java.util.List;

public class TicketRepositoryImpl implements TicketRepositoryCustom {
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Ticket> listAllWithFilters(User user, TicketListRequest request) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Ticket> cq = cb.createQuery(Ticket.class);
        Root<Ticket> root = cq.from(Ticket.class);

        List<Predicate> predicates = new ArrayList<>();

        if (!Utils.isEmpty(request)) {
            // create_date
            if (request.create_date() != null) {
                predicates.add(cb.equal(root.get("createDate"), request.create_date()));
            }

            // status
            if (request.status() != null) {
                predicates.add(cb.equal(root.get("status"), request.status()));
            }

            // category
            if (request.category() != null) {
                predicates.add(cb.equal(root.get("category"), request.category()));
            }

            // title (like)
            if (!Utils.isEmpty(request.title())) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("title")),
                                "%" + request.title().trim().toLowerCase() + "%"
                        )
                );
            }

            // client_id
            if (request.client_id() != null) {
                if (UserRole.ADMIN_USER.equals(user.getRole()) || UserRole.SUPPORT_USER.equals(user.getRole())) {
                    predicates.add(cb.equal(root.get("clientId"), request.client_id()));
                } else {
                    predicates.add(cb.equal(root.get("clientId"), user.getClient_id()));
                }
            }

            // responsible_id
            if (request.responsible_id() != null) {
                predicates.add(cb.equal(root.get("responsibleId"), request.responsible_id()));
            }

            // priority
            if (request.priority() != null) {
                predicates.add(cb.equal(root.get("priority"), request.priority()));
            }

            // public_code (like)
            if (!Utils.isEmpty(request.public_code())) {
                predicates.add(
                        cb.like(
                                cb.lower(root.get("publicCode")),
                                "%" + request.public_code().trim().toLowerCase() + "%"
                        )
                );
            }
        }

        cq.where(predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0])));
        cq.orderBy(cb.asc(root.get("createDate")));

        return em.createQuery(cq).getResultList();
    }
}