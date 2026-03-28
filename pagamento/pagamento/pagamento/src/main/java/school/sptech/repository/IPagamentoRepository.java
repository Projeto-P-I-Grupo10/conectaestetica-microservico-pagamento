package school.sptech.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.model.Pagamento;

public interface IPagamentoRepository extends JpaRepository<Pagamento,Long> {
}
