package br.com.contae.api;

import br.com.contae.api.usuario.dto.UsuarioRequestDTO;
import br.com.contae.application.usuario.UsuarioService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StatusHttpTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioService usuarioService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String token;

    @BeforeEach
    void autenticarUsuario() throws Exception {
        String email = UUID.randomUUID() + "@example.com";
        usuarioService.cadastrar(new UsuarioRequestDTO("Teste", email, "senha123"));
        MvcResult login = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", email, "senha", "senha123"))))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode body = objectMapper.readTree(login.getResponse().getContentAsString());
        token = body.path("token").asText();
        assertNotNull(token);
    }

    @Test
    void contaRetorna201AoCriarE204AoExcluir() throws Exception {
        MvcResult criada = mockMvc.perform(post("/contas")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Conta\",\"tipo\":\"CORRENTE\",\"saldo\":0}"))
                .andExpect(status().isCreated())
                .andReturn();
        long id = objectMapper.readTree(criada.getResponse().getContentAsString()).path("id").asLong();

        mockMvc.perform(delete("/contas/{id}", id).header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    void categoriaRetorna201AoCriarE204AoExcluir() throws Exception {
        MvcResult criada = mockMvc.perform(post("/categorias")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Alimentação\"}"))
                .andExpect(status().isCreated())
                .andReturn();
        long id = objectMapper.readTree(criada.getResponse().getContentAsString()).path("id").asLong();

        mockMvc.perform(delete("/categorias/{id}", id).header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }
}
