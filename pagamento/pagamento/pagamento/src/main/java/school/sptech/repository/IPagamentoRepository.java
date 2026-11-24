package school.sptech.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.model.Pagamento;

import java.util.Optional;

public interface IPagamentoRepository extends JpaRepository<Pagamento,Long> {
    Optional<Pagamento> findByIdUsuarioAndIdTurmaAndStatus(Long idUsuario, Long idTurma, String status);

    // Pode existir mais de um pagamento para o mesmo (curso, usuário) ao longo do
    // tempo (ex.: um PIX anterior expirou/cancelou e outro foi criado). Retorna o
    // mais recente (maior id) para evitar NonUniqueResultException.
    Optional<Pagamento> findFirstByIdTurmaAndIdUsuarioOrderByIdDesc(Long idTurma, Long idUsuario);

}
