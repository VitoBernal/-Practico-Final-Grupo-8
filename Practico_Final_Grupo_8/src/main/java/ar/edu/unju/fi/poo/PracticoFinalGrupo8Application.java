package ar.edu.unju.fi.poo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "API Practico Final Grupo 8", version = "1.0",
		description = "Gestion comercial de clientes, productos y ventas de contado o financiadas en 6 cuotas"))
public class PracticoFinalGrupo8Application {

	public static void main(String[] args) {
		SpringApplication.run(PracticoFinalGrupo8Application.class, args);
	}

}
