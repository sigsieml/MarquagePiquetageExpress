package fr.sieml.marquagepiquetage.Marquage;

import org.jetbrains.annotations.NotNull;

public class Techniques {

    public boolean forageAvecTariere = false;
    public boolean forageDirige = false;
    public boolean fuseOuOgive = false;
    public boolean briseRoche = false;
    public boolean enginElevateur = false;
    public boolean enginVibrant = false;
    public boolean grue = false;
    public boolean manuelOuManutentionDobjetOuMateriel = false;
    public boolean pelleMecanique = false;
    public boolean trancheuse = false;
    public boolean raboteuse = false;
    public boolean techniqueDouce = false;
    public Techniques(){}

    public Techniques(boolean forageAvecTariere, boolean forageDirige, boolean fuseOuOgive, boolean briseRoche, boolean enginElevateur, boolean enginVibrant, boolean grue, boolean manuelOuManutentionDobjetOuMateriel, boolean pelleMecanique, boolean trancheuse, boolean raboteuse, boolean techniqueDouce) {
        this.forageAvecTariere = forageAvecTariere;
        this.forageDirige = forageDirige;
        this.fuseOuOgive = fuseOuOgive;
        this.briseRoche = briseRoche;
        this.enginElevateur = enginElevateur;
        this.enginVibrant = enginVibrant;
        this.grue = grue;
        this.manuelOuManutentionDobjetOuMateriel = manuelOuManutentionDobjetOuMateriel;
        this.pelleMecanique = pelleMecanique;
        this.trancheuse = trancheuse;
        this.raboteuse = raboteuse;
        this.techniqueDouce = techniqueDouce;
    }

    public Techniques(@NotNull Techniques techniques) {
        this.forageAvecTariere = techniques.forageAvecTariere;
        this.forageDirige = techniques.forageDirige;
        this.fuseOuOgive = techniques.fuseOuOgive;
        this.briseRoche = techniques.briseRoche;
        this.enginElevateur = techniques.enginElevateur;
        this.enginVibrant = techniques.enginVibrant;
        this.grue = techniques.grue;
        this.manuelOuManutentionDobjetOuMateriel = techniques.manuelOuManutentionDobjetOuMateriel;
        this.pelleMecanique = techniques.pelleMecanique;
        this.trancheuse = techniques.trancheuse;
        this.raboteuse = techniques.raboteuse;
        this.techniqueDouce = techniques.techniqueDouce;

    }

    @Override
    public String toString() {
        return "Techniques{" +
                "forageAvecTariere=" + forageAvecTariere +
                ", forageDirige=" + forageDirige +
                ", fuseOuOgive=" + fuseOuOgive +
                ", briseRoche=" + briseRoche +
                ", EnginElevateur=" + enginElevateur +
                ", enginVibrant=" + enginVibrant +
                ", grue=" + grue +
                ", manuelOuManutentionDobjetOuMateriel=" + manuelOuManutentionDobjetOuMateriel +
                ", pelleMecanique=" + pelleMecanique +
                ", trancheuse=" + trancheuse +
                ", raboteuse=" + raboteuse +
                ", techniqueDouce=" + techniqueDouce +
                '}';
    }
}
