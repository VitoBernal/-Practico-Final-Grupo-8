package ar.edu.unju.fi.poo.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ar.edu.unju.fi.poo.service.imp.ClienteServiceImp;
import jakarta.transaction.Transactional;
import ar.edu.unju.fi.poo.model.Cliente;
import ar.edu.unju.fi.poo.exception.ClienteException;
import ar.edu.unju.fi.poo.model.TipoCliente;
import java.util.List;

@SpringBootTest
@Transactional
@DisplayName("Pruebas unitarias para la clase ClienteService")
class ClienteServiceTest {
    @Autowired
    private ClienteServiceImp clienteServiceImp;
    private Cliente clienteParticular;
    private Cliente clienteEmpresa;

    @BeforeEach
    void setUp() {
        clienteParticular = Cliente.particular("12345678", "Juan", "Perez", "3886343434", "juan.perez@email.com", "Calle 123");
        clienteEmpresa = Cliente.empresa("87654321", "Empresa Energetica", "3886343434", "empresa@email.com", "Calle 321");
    }

    @Test
    @DisplayName("Guardar cliente")
    void testGuardarCliente() throws ClienteException {
        clienteServiceImp.guardarCliente(clienteParticular);
        clienteServiceImp.guardarCliente(clienteEmpresa);
        assertEquals(2, clienteServiceImp.obtenerTodosLosClientes().size());
        assertEquals(1, clienteServiceImp.obtenerClientesPorTipo(TipoCliente.PARTICULAR).size());
        assertEquals(1, clienteServiceImp.obtenerClientesPorTipo(TipoCliente.EMPRESA).size());
    }

    @Test
    void testGuardarClienteDuplicado() throws ClienteException {
        clienteServiceImp.guardarCliente(clienteParticular); // Intento de guardar un cliente duplicado
        assertThrows(ClienteException.class, () -> clienteServiceImp.guardarCliente(clienteParticular));
    }

    @Test
    @DisplayName("guardar cliente con numero de identificacion duplicado")
    void guardarClienteDuplicadoPorDni() throws ClienteException {
        clienteServiceImp.guardarCliente(clienteParticular);

        Cliente otro = Cliente.particular("12345678", "Pedro", "Gomez", "3886343434", "otro@email.com", "Los seibos 213");

        assertThrows(ClienteException.class, () -> clienteServiceImp.guardarCliente(otro));
    }

    @Test
    @DisplayName("Guardar cliente con email duplicado")
    void guardarClienteDuplicadoPorEmail() throws ClienteException {
        clienteServiceImp.guardarCliente(clienteParticular);

        Cliente otro = Cliente.particular("11111111", "Pedro", "Gomez", "3886000000", "juan.perez@email.com", "calle pascual aguero 1");

        assertThrows(ClienteException.class, () -> clienteServiceImp.guardarCliente(otro));
    }

    @Test
    @DisplayName("Buscar cliente por numero de identificacion")
    void buscarClientePorNumeroIdentificacion() throws ClienteException {
        clienteServiceImp.guardarCliente(clienteParticular);

        Cliente encontrado = clienteServiceImp.buscarClientePorNumeroIdentificacion("12345678");

        assertNotNull(encontrado);
        assertEquals("Juan", encontrado.getNombre());
    }

    @Test
    @DisplayName("buscar cliente por nombre o razon social")
    void buscarClientePorNombreOrazonSocial() throws ClienteException {
        clienteServiceImp.guardarCliente(clienteParticular);
        clienteServiceImp.guardarCliente(clienteEmpresa);

        List<Cliente> resultado = clienteServiceImp.buscarClientesPorNombreORazonSocial("Juan");
        assertEquals(1, resultado.size());
        assertEquals("Juan", resultado.get(0).getNombre());
    }

    @Test
    @DisplayName("obtener por tipo de cliente")
    void obtenerClientesPorTipo() throws ClienteException {
        Cliente empresa = Cliente.empresa("44444444", "Empresa", "3886343434", "empresa2@email.com", "av jose 325");
        clienteServiceImp.guardarCliente(clienteParticular);
        clienteServiceImp.guardarCliente(clienteEmpresa);
        clienteServiceImp.guardarCliente(empresa);

        List<Cliente> resultadoParticular = clienteServiceImp.obtenerClientesPorTipo(TipoCliente.PARTICULAR);
        List<Cliente> resultadoEmpresa = clienteServiceImp.obtenerClientesPorTipo(TipoCliente.EMPRESA);
        assertEquals(1, resultadoParticular.size());
        assertEquals("Juan", resultadoParticular.get(0).getNombre());
        assertEquals(2, resultadoEmpresa.size());
        assertEquals("Empresa", resultadoEmpresa.get(1).getRazonSocial());
    }

    @Test
    void buscarClientePorNumeroIdentificacionInexistente() throws ClienteException {
        assertThrows(ClienteException.class, () -> clienteServiceImp.buscarClientePorNumeroIdentificacion("99999999"));
    }

    @Test
    @DisplayName("Buscar cliente por dni validando varios campos")
    void buscarClientePorNumeroIdentificacionConAssertAll() throws ClienteException {
        clienteServiceImp.guardarCliente(clienteParticular);

        Cliente encontrado = clienteServiceImp.buscarClientePorNumeroIdentificacion("12345678");

        assertAll(
                () -> assertNotNull(encontrado),
                () -> assertEquals("12345678", encontrado.getNumeroIdentificacion()),
                () -> assertFalse(encontrado.getNombre().isBlank()),
                () -> assertEquals(TipoCliente.PARTICULAR, encontrado.getTipoCliente()));
    }

    @ParameterizedTest
    @DisplayName("buscar cliente por nombre parcial ")
    @ValueSource(strings = { "Juan", "JUAN", "Empresa Energetica" })
    void buscarClientePorNombreParcialParametrizado(String valorBusqueda) throws ClienteException {
        clienteServiceImp.guardarCliente(clienteParticular);
        clienteServiceImp.guardarCliente(clienteEmpresa);

        List<Cliente> resultados = clienteServiceImp.buscarClientesPorNombreORazonSocial(valorBusqueda);

        assertFalse(resultados.isEmpty());
        assertTrue(resultados.stream().anyMatch(c -> "Juan".equals(c.getNombre())
                || "Empresa Energetica".equals(c.getRazonSocial())));
    }

}
