package ar.edu.unju.fi.poo.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ar.edu.unju.fi.poo.dto.ClienteRequestDto;
import ar.edu.unju.fi.poo.dto.ClienteResponseDto;
import ar.edu.unju.fi.poo.exception.ClienteException;
import ar.edu.unju.fi.poo.exception.ConflictoException;
import ar.edu.unju.fi.poo.exception.NoEncontradoException;
import ar.edu.unju.fi.poo.model.TipoCliente;
import ar.edu.unju.fi.poo.service.imp.ClienteServiceImp;
import jakarta.transaction.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
@DisplayName("Pruebas unitarias para la clase ClienteService")
class ClienteServiceTest {

    @Autowired
    private ClienteServiceImp clienteServiceImp;

    private ClienteRequestDto clienteParticular;
    private ClienteRequestDto clienteEmpresa;

    @BeforeEach
    void setUp() {
        clienteParticular = ClienteRequestDto.particular(
                "12345678", "Juan", "Perez", "3886343434", "juan.perez@email.com", "Calle 123");
        clienteEmpresa = ClienteRequestDto.empresa(
                "87654321", "Empresa Energetica", "3886343434", "empresa@email.com", "Calle 321");
    }

    @Test
    @DisplayName("Guardar cliente particular y empresa")
    void testGuardarCliente() throws ClienteException {
        clienteServiceImp.guardarCliente(clienteParticular);
        clienteServiceImp.guardarCliente(clienteEmpresa);

        assertEquals(2, clienteServiceImp.obtenerTodosLosClientes().size());
        assertEquals(1, clienteServiceImp.obtenerClientesPorTipo(TipoCliente.PARTICULAR).size());
        assertEquals(1, clienteServiceImp.obtenerClientesPorTipo(TipoCliente.EMPRESA).size());
    }

    @Test
    @DisplayName("Guardar cliente duplicado lanza excepcion")
    void testGuardarClienteDuplicado() throws ClienteException {
        clienteServiceImp.guardarCliente(clienteParticular);
        assertThrows(ConflictoException.class, () -> clienteServiceImp.guardarCliente(clienteParticular));
    }

    @Test
    @DisplayName("Guardar cliente con numero de identificacion duplicado")
    void guardarClienteDuplicadoPorDni() throws ClienteException {
        clienteServiceImp.guardarCliente(clienteParticular);

        // Mismo DNI, distinto email
        ClienteRequestDto otro = ClienteRequestDto.particular(
                "12345678", "Pedro", "Gomez", "3886343434", "otro@email.com", "Los seibos 213");

        assertThrows(ConflictoException.class, () -> clienteServiceImp.guardarCliente(otro));
    }

    @Test
    @DisplayName("Guardar cliente con email duplicado")
    void guardarClienteDuplicadoPorEmail() throws ClienteException {
        clienteServiceImp.guardarCliente(clienteParticular);

        // Distinto DNI, mismo email
        ClienteRequestDto otro = ClienteRequestDto.particular(
                "11111111", "Pedro", "Gomez", "3886000000", "juan.perez@email.com", "calle pascual aguero 1");

        assertThrows(ConflictoException.class, () -> clienteServiceImp.guardarCliente(otro));
    }

    @Test
    @DisplayName("Buscar cliente por numero de identificacion")
    void buscarClientePorNumeroIdentificacion() throws ClienteException {
        clienteServiceImp.guardarCliente(clienteParticular);

        // El service devuelve ClienteResponseDto
        ClienteResponseDto encontrado = clienteServiceImp.buscarClientePorNumeroIdentificacion("12345678");

        assertNotNull(encontrado);
        assertEquals("Juan", encontrado.getNombre());
    }

    @Test
    @DisplayName("Buscar cliente por nombre o razon social")
    void buscarClientePorNombreOrazonSocial() throws ClienteException {
        clienteServiceImp.guardarCliente(clienteParticular);
        clienteServiceImp.guardarCliente(clienteEmpresa);

        List<ClienteResponseDto> resultado = clienteServiceImp.buscarClientesPorNombreORazonSocial("Juan");

        assertEquals(1, resultado.size());
        assertEquals("Juan", resultado.get(0).getNombre());
    }

    @Test
    @DisplayName("Obtener clientes por tipo")
    void obtenerClientesPorTipo() throws ClienteException {
        ClienteRequestDto empresa2 = ClienteRequestDto.empresa(
                "44444444", "Empresa", "3886343434", "empresa2@email.com", "av jose 325");

        clienteServiceImp.guardarCliente(clienteParticular);
        clienteServiceImp.guardarCliente(clienteEmpresa);
        clienteServiceImp.guardarCliente(empresa2);

        List<ClienteResponseDto> resultadoParticular = clienteServiceImp.obtenerClientesPorTipo(TipoCliente.PARTICULAR);
        List<ClienteResponseDto> resultadoEmpresa = clienteServiceImp.obtenerClientesPorTipo(TipoCliente.EMPRESA);

        assertEquals(1, resultadoParticular.size());
        assertEquals("Juan", resultadoParticular.get(0).getNombre());
        assertEquals(2, resultadoEmpresa.size());
        assertEquals("Empresa", resultadoEmpresa.get(1).getRazonSocial());
    }

    @Test
    @DisplayName("Buscar cliente por numero de identificacion inexistente lanza excepcion")
    void buscarClientePorNumeroIdentificacionInexistente() {
        assertThrows(NoEncontradoException.class,
                () -> clienteServiceImp.buscarClientePorNumeroIdentificacion("99999999"));
    }

    @Test
    @DisplayName("Buscar cliente por dni validando varios campos con assertAll")
    void buscarClientePorNumeroIdentificacionConAssertAll() throws ClienteException {
        clienteServiceImp.guardarCliente(clienteParticular);

        ClienteResponseDto encontrado = clienteServiceImp.buscarClientePorNumeroIdentificacion("12345678");

        assertAll(
                () -> assertNotNull(encontrado),
                () -> assertEquals("12345678", encontrado.getNumeroIdentificacion()),
                () -> assertFalse(encontrado.getNombre().isBlank()),
                () -> assertEquals(TipoCliente.PARTICULAR, encontrado.getTipo()),
                // nombreCompleto es un campo calculado que viene del mapper
                () -> assertEquals("Juan Perez", encontrado.getNombreCompleto()));
    }

    @ParameterizedTest
    @DisplayName("Buscar cliente por nombre parcial (parametrizado)")
    @ValueSource(strings = { "Juan", "JUAN", "Empresa Energetica" })
    void buscarClientePorNombreParcialParametrizado(String valorBusqueda) throws ClienteException {
        clienteServiceImp.guardarCliente(clienteParticular);
        clienteServiceImp.guardarCliente(clienteEmpresa);

        List<ClienteResponseDto> resultados = clienteServiceImp.buscarClientesPorNombreORazonSocial(valorBusqueda);

        assertFalse(resultados.isEmpty());
        assertTrue(resultados.stream().anyMatch(dto ->
                "Juan".equals(dto.getNombre()) || "Empresa Energetica".equals(dto.getRazonSocial())));
    }

    @Test
    @DisplayName("Eliminar cliente realiza borrado logico (habilitado=false)")
    void eliminarClienteBorradoLogico() throws ClienteException {
        // guardarCliente devuelve ResponseDto que incluye el id generado
        ClienteResponseDto guardado = clienteServiceImp.guardarCliente(clienteParticular);

        clienteServiceImp.eliminarCliente(guardado.getId());

        // El cliente sigue en la BD pero con habilitado=false
        ClienteResponseDto encontrado = clienteServiceImp.buscarClientePorId(guardado.getId());
        assertFalse(encontrado.isHabilitado());
    }

    @Test
    @DisplayName("Eliminar cliente ya deshabilitado lanza excepcion")
    void eliminarClienteYaDeshabilitado() throws ClienteException {
        ClienteResponseDto guardado = clienteServiceImp.guardarCliente(clienteParticular);
        clienteServiceImp.eliminarCliente(guardado.getId());

        assertThrows(ClienteException.class, () -> clienteServiceImp.eliminarCliente(guardado.getId()));
    }
}
