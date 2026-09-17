package br.com.contae.api.movimentacao.dto;

import br.com.contae.domain.movimentacao.TipoDespesa;
import br.com.contae.domain.movimentacao.TipoMovimentacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MovimentacaoRequestDTO {

    @NotNull
    private Long contaId;

    @NotNull
    private Long categoriaId;

    @NotBlank
    private String descricao;

    @NotNull
    @Positive
    private BigDecimal valor;

    @NotNull
    private TipoMovimentacao tipoMovimentacao;

    private TipoDespesa tipoDespesa;

    @NotNull
    private LocalDate data;

    @NotNull
    private Boolean recorrente;

}
