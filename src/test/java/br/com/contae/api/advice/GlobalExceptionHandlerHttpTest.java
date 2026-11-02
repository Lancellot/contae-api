package br.com.contae.api.advice;

import br.com.contae.domain.exception.ConflitoDeNegocioException;
import br.com.contae.domain.exception.CredenciaisInvalidasException;
import br.com.contae.domain.exception.RecursoNaoEncontradoException;
import br.com.contae.domain.exception.SaldoInsuficienteException;
import br.com.contae.api.conta.dto.ContaRequestDTO;
import br.com.contae.api.movimentacao.dto.MovimentacaoRequestDTO;
import br.com.contae.api.usuario.dto.UsuarioRequestDTO;
import jakarta.validation.Valid;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @Test
    void validacaoDeContaAceitaLimiteMonetarioERejeitaOverflow() throws Exception {
        mockMvc.perform(post("/test/errors/conta").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Conta\",\"tipo\":\"CORRENTE\",\"saldo\":9999999999999.99}"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/test/errors/conta").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Conta\",\"tipo\":\"CORRENTE\",\"saldo\":10000000000000.00}"))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/test/errors/conta").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Conta\",\"tipo\":\"CORRENTE\",\"saldo\":1.001}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void validacaoDeMovimentacaoLimitaValoresDescricaoEIds() throws Exception {
        mockMvc.perform(post("/test/errors/movimentacao").contentType(MediaType.APPLICATION_JSON)
                        .content(movimentacaoJson(1, 1, "x".repeat(255), "9999999999999.99")))
                .andExpect(status().isOk());
        mockMvc.perform(post("/test/errors/movimentacao").contentType(MediaType.APPLICATION_JSON)
                        .content(movimentacaoJson(1, 1, "x".repeat(255), "10000000000000.00")))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/test/errors/movimentacao").contentType(MediaType.APPLICATION_JSON)
                        .content(movimentacaoJson(1, 1, "x".repeat(256), "1.00")))
                .andExpect(status().isBadRequest());
        mockMvc.perform(post("/test/errors/movimentacao").contentType(MediaType.APPLICATION_JSON)
                        .content(movimentacaoJson(0, 1, "Compra", "1.00")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void emailCom150CaracteresEValidoE151ERejeitado() throws Exception {
        String email150 = "a".repeat(64) + "@" + "b".repeat(63) + "." + "c".repeat(17) + ".com";
        String email151 = "a".repeat(64) + "@" + "b".repeat(63) + "." + "c".repeat(18) + ".com";
        mockMvc.perform(post("/test/errors/usuario").contentType(MediaType.APPLICATION_JSON)
                        .content(usuarioJson(email150)))
                .andExpect(status().isOk());
        mockMvc.perform(post("/test/errors/usuario").contentType(MediaType.APPLICATION_JSON)
                        .content(usuarioJson(email151)))
                .andExpect(status().isBadRequest());
    }

    private String movimentacaoJson(long contaId, long categoriaId, String descricao, String valor) {
        return "{\"contaId\":" + contaId + ",\"categoriaId\":" + categoriaId
                + ",\"descricao\":\"" + descricao + "\",\"valor\":" + valor
                + ",\"tipoMovimentacao\":\"DESPESA\",\"data\":\"2026-01-01\",\"recorrente\":false}";
    }

    private String usuarioJson(String email) {
        return "{\"nome\":\"Teste\",\"email\":\"" + email + "\",\"senha\":\"senha123\"}";
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

        @PostMapping("/conta")
        ResponseEntity<Void> validarConta(@Valid @RequestBody ContaRequestDTO dto) { return ResponseEntity.ok().build(); }

        @PostMapping("/movimentacao")
        ResponseEntity<Void> validarMovimentacao(@Valid @RequestBody MovimentacaoRequestDTO dto) { return ResponseEntity.ok().build(); }

        @PostMapping("/usuario")
        ResponseEntity<Void> validarUsuario(@Valid @RequestBody UsuarioRequestDTO dto) { return ResponseEntity.ok().build(); }
    }
}
