package schwartz.spring.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import schwartz.spring.Utils.Utils;
import schwartz.spring.app.domain.client.Client;
import schwartz.spring.app.repository.ClientRepository;
import schwartz.spring.app.services.ClientService;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    @Autowired
    private ClientService clientService;
    @Autowired
    ClientRepository clientRepository;

    @GetMapping("")
    public ResponseEntity<List<Client>> clients(){
        return ResponseEntity.ok(clientRepository.findAll());
    }

    @GetMapping("/{client}")
    public ResponseEntity<List<Client>> clietnsBy(@PathVariable String client){
        try{
            List<Client> clients = null;
            if(Utils.isEmpty(client)){
                return ResponseEntity.badRequest().build();
            }
            clients = clientService.listAllByName(client);
            if(Utils.isEmpty(clients)){
                return ResponseEntity.notFound().build();
            }
             ResponseEntity.ok(clients);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
