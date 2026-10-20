package ugb.applicationMultitiers.gestionDeProjet.service;

import ugb.applicationMultitiers.gestionDeProjet.dto.ProjetAdminResponse;
import ugb.applicationMultitiers.gestionDeProjet.mapper.ProjetMapper;
import ugb.applicationMultitiers.gestionDeProjet.repository.SujetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjetAdminService {

    private final SujetRepository sujetRepository;
    private final ProjetMapper mapper;

    @Transactional(readOnly = true)
    public List<ProjetAdminResponse> findAll() {
        return sujetRepository.findAllAvecDetails().stream().map(mapper::toAdminResponse).toList();
    }
}
