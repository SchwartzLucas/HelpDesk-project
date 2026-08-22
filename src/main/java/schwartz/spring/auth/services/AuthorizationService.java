package schwartz.spring.auth.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import schwartz.spring.auth.domain.user.User;
import schwartz.spring.auth.repository.user.UserRepository;

// @ service -> dizer para o Spring que é um serviço
@Service
public class AuthorizationService implements UserDetailsService {
    // implements UserDetailsService para o Spring utilizar esse servico para os detalhes de autenticação dos usuários

    @Autowired
    UserRepository userRepository;
    // Autowired como um @Inject -> implementa / injeta as propriedade desse UserRepository para consultarmos os usuarios
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // carregando usuário pelo nome dos usuários
        User user = userRepository.findByLogin(username); // capaz de consultar o usuário pelo username
        if(user == null){
            throw new UsernameNotFoundException("User not found");
        }
        return user;


    }

}
