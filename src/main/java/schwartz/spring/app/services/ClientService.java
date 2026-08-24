package schwartz.spring.app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import schwartz.spring.Exceptions.ClientNotFoundException;
import schwartz.spring.Utils.Utils;
import schwartz.spring.app.domain.client.Client;
import schwartz.spring.app.repository.ClientRepository;

import java.util.List;

@Service
public class ClientService {

    @Autowired
    ClientRepository clientRepository;

    public Client saveClient(Client client){
        if(Utils.isEmpty(client.getName())) {
        throw new IllegalArgumentException("Client name cannot be null or empty");
        }
        if(Utils.isEmpty(client.getEmail())){
            throw new IllegalArgumentException("Client e-mail cannot be null or empty");
        }
        if(!Utils.validarEMAIL(client.getEmail())){
            throw new IllegalArgumentException("Client e-mail informed is invalid");
        }

        return clientRepository.save(client);
    }

    public void deleteClientById(String id){
        if(Utils.isEmpty(id)){
            throw new IllegalArgumentException("id cannot be null or empty");
        }
        if(Utils.isEmpty(clientRepository.findById(id))){
            throw new ClientNotFoundException("Client not found");
        }
        clientRepository.deleteById(id);
    }

    public List<Client> listAll(){
        return clientRepository.findAll();
    }

    public List<Client> listAllById(String id){
        if(Utils.isEmpty(id)){
            throw new IllegalArgumentException("id cannot be null");
        }
        return clientRepository.findAllById(id);
    }

    public List<Client> listAllByName(String name){
        if(Utils.isEmpty(name)){
            throw new IllegalArgumentException("id cannot be null");
        }
        return clientRepository.findAllByName(name);
    }

}
