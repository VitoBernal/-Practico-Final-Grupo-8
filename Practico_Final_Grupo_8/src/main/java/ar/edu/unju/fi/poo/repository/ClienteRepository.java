package ar.edu.unju.fi.poo.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ar.edu.unju.fi.poo.model.Cliente;
import ar.edu.unju.fi.poo.model.TipoCliente;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    Cliente findByNumeroIdentificacion(String numeroIdentificacion);

    Cliente findByEmail(String email);

    List<Cliente> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCaseOrRazonSocialContainingIgnoreCase(
            String nombre, String apellido, String razonSocial);

    List<Cliente> findByTipo(TipoCliente tipo);

}
