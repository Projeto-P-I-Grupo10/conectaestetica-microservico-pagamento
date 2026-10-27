package school.sptech.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import school.sptech.model.Pagamento;

import java.util.Optional;

public interface IPagamentoRepository extends JpaRepository<Pagamento,Long> {
    Optional<Pagamento> findByIdUsuarioAndIdCursoAndStatus(Long idUsuario, Long idCurso, String status);

    // Pode existir mais de um pagamento para o mesmo (curso, usuário) ao longo do
    // tempo (ex.: um PIX anterior expirou/cancelou e outro foi criado). Retorna o
    // mais recente (maior id) para evitar NonUniqueResultException.
    Optional<Pagamento> findFirstByIdCursoAndIdUsuarioOrderByIdDesc(Long idCurso, Long idUsuario);

}
