package ugb.applicationMultitiers.gestionDeProjet.service;

import ugb.applicationMultitiers.gestionDeProjet.entity.Candidature;
import ugb.applicationMultitiers.gestionDeProjet.entity.Enseignant;
import ugb.applicationMultitiers.gestionDeProjet.entity.Etudiant;
import ugb.applicationMultitiers.gestionDeProjet.entity.ResponsablePedagogique;
import ugb.applicationMultitiers.gestionDeProjet.entity.Statut;
import ugb.applicationMultitiers.gestionDeProjet.entity.Sujet;

/** Fabriques d'objets de test partagées par les tests de services. */
final class TestData {

    private TestData() {
    }

    static Enseignant enseignant(Long id) {
        Enseignant e = new Enseignant();
        e.setId(id);
        e.setNom("Diop");
        e.setPrenom("Awa");
        e.setEmail("awa" + id + "@univ.test");
        e.setDepartement("Informatique");
        return e;
    }

    static Etudiant etudiant(Long id) {
        Etudiant e = new Etudiant();
        e.setId(id);
        e.setNom("Ndiaye");
        e.setPrenom("Moussa");
        e.setEmail("moussa" + id + "@univ.test");
        e.setNumeroEtudiant("N" + id);
        e.setFiliere("GDIL");
        return e;
    }

    static ResponsablePedagogique responsable(Long id) {
        ResponsablePedagogique r = new ResponsablePedagogique();
        r.setId(id);
        r.setNom("Fall");
        r.setPrenom("Ibrahima");
        r.setEmail("resp@univ.test");
        return r;
    }

    static Sujet sujet(Long id, Enseignant proprietaire, int capaciteMax, boolean disponible) {
        Sujet s = new Sujet();
        s.setId(id);
        s.setTitre("Sujet " + id);
        s.setDescription("Description");
        s.setCapaciteMax(capaciteMax);
        s.setDisponible(disponible);
        s.setEnseignant(proprietaire);
        return s;
    }

    static Candidature candidature(Long id, Etudiant etudiant, Sujet sujet, Statut statut) {
        Candidature c = new Candidature();
        c.setId(id);
        c.setEtudiant(etudiant);
        c.setSujet(sujet);
        c.setStatut(statut);
        return c;
    }
}
