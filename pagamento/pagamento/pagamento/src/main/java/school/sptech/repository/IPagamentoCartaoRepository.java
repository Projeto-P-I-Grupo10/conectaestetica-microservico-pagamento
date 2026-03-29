package school.sptech.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.model.PagamentoCartao;

public interface IPagamentoCartaoRepository extends JpaRepository<PagamentoCartao, Long> {
}
