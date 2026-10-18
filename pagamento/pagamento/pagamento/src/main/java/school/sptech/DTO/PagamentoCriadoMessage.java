package school.sptech.DTO;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record PagamentoCriadoMessage(
        @JsonProperty("idTurma") @JsonAlias("idCurso") Long idCurso,
        Long idUsuario,
        String email,
        String metodoPagamento,
        String status,
        BigDecimal valor
) {
}
