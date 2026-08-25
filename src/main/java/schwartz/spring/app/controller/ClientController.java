package schwartz.spring.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import schwartz.spring.Utils.Utils;
import schwartz.spring.app.domain.client.Client;
import schwartz.spring.app.domain.client.ClientCreateDTO;
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
            clients = clientService.listAllBy(client); // podendo ser o nome ou o ID
            if(Utils.isEmpty(clients)){
                return ResponseEntity.notFound().build();
            }
             ResponseEntity.ok(clients);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.internalServerError().build();
    }

    @PostMapping("/new")
    public ResponseEntity createClient(@RequestBody @Validated ClientCreateDTO clientCreateDTO){
        try {
            var requestBody = new Client(clientCreateDTO.name(), clientCreateDTO.email());
            clientService.saveClient(requestBody);
            return ResponseEntity.ok(clientCreateDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/{client}")
    public ResponseEntity deleteClient(String id){
        try {
            clientService.deleteClientById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
