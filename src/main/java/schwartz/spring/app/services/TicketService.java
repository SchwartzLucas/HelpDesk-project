package schwartz.spring.app.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import schwartz.spring.Exceptions.MissingAttributeException;
import schwartz.spring.Exceptions.TicketNotFoundException;
import schwartz.spring.Utils.Utils;
import schwartz.spring.app.domain.client.Client;
import schwartz.spring.app.domain.ticket.Ticket;
import schwartz.spring.app.domain.ticket.TicketCreateRequest;
import schwartz.spring.app.domain.ticket.TicketListRequest;
import schwartz.spring.app.domain.ticket.TicketUpdateRequest;
import schwartz.spring.app.infra.PublicIdGenerator;
import schwartz.spring.app.repository.ClientRepository;
import schwartz.spring.app.repository.TicketRepository;
import schwartz.spring.auth.domain.user.User;
import schwartz.spring.auth.domain.user.UserRole;
import schwartz.spring.auth.services.UserService;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final PublicIdGenerator publicIdGenerator;
    private final UserService userService;
    private final ClientRepository clientRepository;

    @PersistenceContext
    private EntityManager em;

    public TicketService(
            TicketRepository ticketRepository,
            PublicIdGenerator publicIdGenerator,
            UserService userService,
            ClientRepository clientRepository,
            EntityManager em
    ) {
        this.ticketRepository = ticketRepository;
        this.publicIdGenerator = publicIdGenerator;
        this.userService = userService;
        this.clientRepository = clientRepository;
        this.em = em;
    }

    public Ticket create(TicketCreateRequest request) {
        User user = userService.getAuthenticatedUser();
        Client client = clientRepository.findById(user.getClient_id());
        String title = request.title();
        String description = request.description();
        Integer category = request.category();
        Integer priority = request.prority() != null ? request.prority() : 0;


        Ticket ticket = new Ticket();
        ticket.setPublicId(publicIdGenerator.generate());
        ticket.setTitle(title);
        ticket.setCategory(category);
        ticket.setDescription(description);
        ticket.setPriority(priority);
        ticket.setStatus(1);
        ticket.setClientId(client.getId());
        ticket.setCreatedDate(LocalDateTime.now());
        ticket.setUpdatedDate(LocalDateTime.now());
        ticket.setSlaExpiration(LocalDateTime.now().plusDays(3L));

        ticketRepository.saveAndFlush(ticket);

        ticket.setPublicCode(String.format(
                        "FXG-%s-%06d", ticket.getCreatedDate().getYear(), ticket.getId()
                )
        );

        ticketRepository.save(ticket);
        return ticket;
    }


    @Transactional
    public Ticket update(TicketUpdateRequest request) {
        UUID id = request.id();
        String title = request.title();
        String description = request.description();
        Integer priority = request.priority();
        Integer status = request.status();
        Long teamId = request.team_id();
        Long responsibleId = request.responsable_id();
        LocalDateTime now = LocalDateTime.now();

        // validações
        if (Utils.isEmpty(id)) {
            throw new MissingAttributeException("public id");
        }

        Ticket existing = ticketRepository.findByPublicId(id);
        if (Utils.isEmpty(existing)) {
            throw new TicketNotFoundException();
        }

        // montar UPDATE dinâmico
        StringBuilder sql = new StringBuilder("UPDATE ticket SET ");
        List<String> sets = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();

        if (!Utils.isEmpty(title)) {
            sets.add("title = :title");
            params.put("title", title);
        }
        if (!Utils.isEmpty(description)) {
            sets.add("description = :description");
            params.put("description", description);
        }
        if (!Utils.isEmpty(priority)) {
            sets.add("priority = :priority");
            params.put("priority", priority);
        }
        if (!Utils.isEmpty(status)) {
            sets.add("status = :status");
            params.put("status", status);
        }
        if (!Utils.isEmpty(teamId)) {
            sets.add("team_id = :team_id");
            params.put("team_id", teamId);
        }
        if (!Utils.isEmpty(responsibleId)) {
            sets.add("responsible_id = :responsible_id");
            params.put("responsible_id", responsibleId);
        }
        if (sets.isEmpty()) {
            // nada para atualizar
            return existing;
        }
        sets.add("updated_date = :updated_date");
        params.put("updated_date", now);

        sql.append(String.join(", ", sets));
        sql.append(" WHERE public_id = :public_id");
        params.put("public_id", id);

        var query = em.createNativeQuery(sql.toString());
        params.forEach(query::setParameter);
        query.executeUpdate();

        // retorna o ticket atualizado (recarrega do banco)
        return ticketRepository.findByPublicId(id);
    }

    public List<Ticket> list(TicketListRequest request) {
        User user = userService.getAuthenticatedUser();
        List<Ticket> tickets = null;
        if (!Utils.isEmpty(request)) {
            StringBuilder sql = new StringBuilder("SELECT t.* FROM ticket t WHERE 1=1");
            List<String> conditions = new ArrayList<>();
            Map<String, Object> params = new HashMap<>();

            // create_date
            if (request.create_date() != null) {
                conditions.add("t.create_date = :create_date");
                params.put("create_date", request.create_date());
            }

            // status
            if (request.status() != null) {
                conditions.add("t.status = :status");
                params.put("status", request.status());
            }

            // category
            if (request.category() != null) {
                conditions.add("t.category = :category");
                params.put("category", request.category());
            }

            // title (like)
            if (!Utils.isEmpty(request.title())) {
                conditions.add("LOWER(t.title) LIKE :title");
                params.put("title", "%" + request.title().trim().toLowerCase() + "%");
            }

            // client_id (com lógica de permissão)
            if (request.client_id() != null) {
                if (UserRole.ADMIN_USER.equals(user.getRole()) || UserRole.SUPPORT_USER.equals(user.getRole())) {
                    conditions.add("t.client_id = :client_id");
                    params.put("client_id", request.client_id());
                } else {
                    conditions.add("t.client_id = :client_id");
                    params.put("client_id", user.getClient_id());
                }
            }

            // responsible_id
            if (request.responsible_id() != null) {
                conditions.add("t.responsible_id = :responsible_id");
                params.put("responsible_id", request.responsible_id());
            }

            // priority
            if (request.priority() != null) {
                conditions.add("t.priority = :priority");
                params.put("priority", request.priority());
            }

            // public_code (like)
            if (!Utils.isEmpty(request.public_code())) {
                conditions.add("LOWER(t.public_code) LIKE :public_code");
                params.put("public_code", "%" + request.public_code().trim().toLowerCase() + "%");
            }

            // junta todas as condições
            if (!conditions.isEmpty()) {
                sql.append(" AND ").append(String.join(" AND ", conditions));
            }

            sql.append(" ORDER BY t.created_date ASC");
            Query query = em.createNativeQuery(sql.toString(), Ticket.class);
            params.forEach(query::setParameter);
            List<Ticket> resultados = query.getResultList();
            return resultados;
        }
        return ticketRepository.listAllByClient_id(user.getClient_id());
    }
}