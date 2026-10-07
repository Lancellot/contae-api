package br.com.contae.domain.exception;

public class ConflitoDeNegocioException extends RuntimeException {
    public ConflitoDeNegocioException(String mensagem) {
        super(mensagem);
    }
}
