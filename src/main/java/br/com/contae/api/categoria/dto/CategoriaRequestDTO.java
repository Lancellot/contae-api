package br.com.contae.api.categoria.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de entrada: dados enviados pelo cliente para criar uma categoria.
 * O dono da categoria NÃO vem aqui; ele é obtido do usuário autenticado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados enviados pelo cliente para criar ou atualizar uma categoria")
public class CategoriaRequestDTO {

    // Validação: o nome é obrigatório e limitado a 100 caracteres
    @NotBlank(message = "O nome da categoria é obrigatório")
    @Size(max = 100, message = "O nome da categoria deve ter no máximo 100 caracteres")
    @Schema(description = "Nome da categoria", example = "Alimentação")
    private String nome;
}