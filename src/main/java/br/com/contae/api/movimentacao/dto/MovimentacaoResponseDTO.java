package br.com.contae.api.movimentacao.dto;

import br.com.contae.domain.movimentacao.TipoDespesa;
import br.com.contae.domain.movimentacao.TipoMovimentacao;
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
public class MovimentacaoResponseDTO {

    private Long id;
    private Long contaId;
    private Long categoriaId;
    private String descricao;
    private BigDecimal valor;
    private TipoMovimentacao tipoMovimentacao;
    private TipoDespesa tipoDespesa;
    private LocalDate data;
    private Boolean recorrente;

}
