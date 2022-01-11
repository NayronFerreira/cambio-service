package br.com.missaodev.cambioservice.controller;

import br.com.missaodev.cambioservice.model.Cambio;
import br.com.missaodev.cambioservice.repository.CambioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
@Tag(name = "Microservice Cambio endpoint")
@RestController
@RequestMapping ("cambio-service")
public class CambioController {

	@Autowired
	private Environment environment;
	@Autowired
	private CambioRepository repository;

	@Operation(summary = "Converte o valor passado como parametro de/para as moedas também passadas por parametro.")
	@GetMapping (value = "/{amount}/{from}/{to}")
	public Cambio getCambio(
			@PathVariable ("amount")BigDecimal amount,
			@PathVariable ("from") String from,
			@PathVariable ("to") String to
			) {

		Cambio cambio = repository.findByFromAndTo(from, to);
		if (cambio==null) throw new RuntimeException("Preencha os 3 carcteres dos campos FROM e TO");

		var port = environment.getProperty("local.server.port");
		BigDecimal conversionFactor = cambio.getConversionFactor();
		BigDecimal convertedValue = conversionFactor.multiply(amount);
		cambio.setConvertedValue(convertedValue.setScale(2, RoundingMode.CEILING));
		cambio.setEnvironment(port);
		return cambio;
	}
}
