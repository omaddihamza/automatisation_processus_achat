package ma.mm.automatisation_processus_achat.rag;

import ma.mm.automatisation_processus_achat.model.Poste;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CpsService {

    private List<Poste> postes = new ArrayList<>();

    public void savePostes(List<Poste> postesExtraits) {
        this.postes = postesExtraits;
    }


    public List<Poste> getPostes() {
        return postes;
    }
}

