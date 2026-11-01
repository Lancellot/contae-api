package br.com.contae.api.categoria.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoriaRequestDTO {
    @NotBlank(message = "O nome da categoria é obrigatório")
    @Size(max = 100, message = "O nome da categoria deve ter no máximo 100 caracteres")
    private String nome;

    public CategoriaRequestDTO() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

}
