package ma.mm.automatisation_processus_achat.repository;

import ma.mm.automatisation_processus_achat.entites.Fournisseur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FournisseurRepository extends JpaRepository<Fournisseur, Long> {
}
