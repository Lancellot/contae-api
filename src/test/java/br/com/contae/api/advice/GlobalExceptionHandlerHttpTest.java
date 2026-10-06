package br.com.contae.api.advice;

import br.com.contae.domain.exception.ConflitoDeNegocioException;
import br.com.contae.domain.exception.CredenciaisInvalidasException;
import br.com.contae.domain.exception.RecursoNaoEncontradoException;
import br.com.contae.domain.exception.SaldoInsuficienteException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(GlobalExceptionHandlerHttpTest.EndpointsConfiguration.class)
class GlobalExceptionHandlerHttpTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void saldoInsuficienteRetorna422() throws Exception {
        mockMvc.perform(get("/test/errors/saldo"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void conflitoDeNegocioERestricaoDeBancoRetornam409() throws Exception {
        mockMvc.perform(get("/test/errors/conflito")).andExpect(status().isConflict());
        mockMvc.perform(get("/test/errors/integridade")).andExpect(status().isConflict());
    }

    @Test
    void recursoAusenteRetorna404() throws Exception {
        mockMvc.perform(get("/test/errors/nao-encontrado"))
                .andExpect(status().isNotFound());
    }

    @Test
    void credenciaisInvalidasEContaDesabilitadaRetornam401Generico() throws Exception {
        mockMvc.perform(get("/test/errors/credenciais"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Email ou senha inválidos"));
        mockMvc.perform(get("/test/errors/desabilitada"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Email ou senha inválidos"));
        mockMvc.perform(get("/test/errors/credenciais-dominio"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Email ou senha inválidos"));
    }

    @Test
    void falhaDeConversaoRetorna400() throws Exception {
        mockMvc.perform(get("/test/errors/data/nao-e-data"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void falhaInesperadaRetornaMensagemGenerica() throws Exception {
        mockMvc.perform(get("/test/errors/inesperado"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Erro interno do servidor"));
    }

    @TestConfiguration
    static class EndpointsConfiguration {
        @Bean
        ErrorEndpoints errorEndpoints() {
            return new ErrorEndpoints();
        }
    }

    @RestController
    @RequestMapping("/test/errors")
    static class ErrorEndpoints {
        @GetMapping("/saldo")
        void saldo() { throw new SaldoInsuficienteException(); }

        @GetMapping("/conflito")
        void conflito() { throw new ConflitoDeNegocioException("Email duplicado"); }

        @GetMapping("/integridade")
        void integridade() { throw new DataIntegrityViolationException("constraint details"); }

        @GetMapping("/nao-encontrado")
        void naoEncontrado() { throw new RecursoNaoEncontradoException("Não encontrado"); }

        @GetMapping("/credenciais")
        void credenciais() { throw new BadCredentialsException("detalhe sensível"); }

        @GetMapping("/desabilitada")
        void desabilitada() { throw new DisabledException("detalhe sensível"); }

        @GetMapping("/credenciais-dominio")
        void credenciaisDominio() { throw new CredenciaisInvalidasException(); }

        @GetMapping("/data/{data}")
        LocalDate data(@PathVariable LocalDate data) { return data; }

        @GetMapping("/inesperado")
        void inesperado() { throw new IllegalStateException("internal detail"); }
    }
}
