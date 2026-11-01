package br.com.contae.api.auth.dto;

public record LoginResponseDTO(
        String token,
        String tipo
) {
    public LoginResponseDTO(String token) {
        this(token, "Bearer");
    }
}
