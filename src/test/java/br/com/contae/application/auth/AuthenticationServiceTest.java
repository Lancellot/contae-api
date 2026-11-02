package br.com.contae.application.auth;

import br.com.contae.api.auth.dto.LoginRequestDTO;
import br.com.contae.domain.exception.CredenciaisInvalidasException;
import br.com.contae.infrastructure.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void loginInativoRetornaErroGenericoDeCredenciais() {
        when(authenticationManager.authenticate(any())).thenThrow(new DisabledException("conta inativa"));

        assertThrows(CredenciaisInvalidasException.class, () -> authenticationService.login(
                new LoginRequestDTO("usuario@contae.com", "senha")));
        verifyNoInteractions(jwtService);
    }
}
