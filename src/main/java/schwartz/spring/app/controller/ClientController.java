package schwartz.spring.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import schwartz.spring.app.domain.client.Client;
import schwartz.spring.app.domain.client.ClientCreateRequest;
import schwartz.spring.app.domain.client.ClientResponse;
import schwartz.spring.app.services.ClientService;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @PostMapping("/create")
    public ResponseEntity<ClientResponse> create(@RequestBody @Validated ClientCreateRequest request) {

        Client client = clientService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ClientResponse.from(client));

    }
}
