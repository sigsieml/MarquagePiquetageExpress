package fr.sieml.marquagepiquetage.Marquage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Marquage {
    public String numOperation  = "";
    public String libelleChantier  = "";
    public String titulaire  = "";
    public String nomSignataire  = "";
    public int numRue  = 1;
    public int numRueFin = 1;
    public String nomRue  = "";
    public String commune  = "";
    public Calendar date  = Calendar.getInstance();
    public List<String> photos  = new ArrayList<>();
    public Boolean dtdict  = false;
    public Boolean recepisseDesDict  = false;
    public Boolean marquageExploitant  = false;
    public Boolean zoneMultiReseaux  = false;
    public Boolean instructionSieml  = false;
    public String signature  = "";
    public String numDict = "";
    public int chantierDuration = 12;

    public Techniques techniques = new Techniques();
    public String autreEnginDeChantier = "";
    public String observation = "";

    public Marquage(){}

    public Marquage(String numOperation, String libelleChantier, String titulaire, String numDict, String nomSignataire, int numRue, int numRueFin, String nomRue, String commune, Calendar date, List<String> photos, Boolean dtdict, Boolean recepisseDesDict, Boolean marquageExploitant, Boolean zoneMultiReseaux, Boolean instructionSieml, String signature, int chantierDuration, Techniques techniques, String autreEnginDeChantier, String observation) {
        this.numOperation = numOperation;
        this.libelleChantier = libelleChantier;
        this.titulaire = titulaire;
        this.numDict = numDict;
        this.nomSignataire = nomSignataire;
        this.numRue = numRue;
        this.numRueFin = numRueFin;
        this.nomRue = nomRue;
        this.commune = commune;
        this.date = date;
        this.photos = photos;
        this.dtdict = dtdict;
        this.recepisseDesDict = recepisseDesDict;
        this.marquageExploitant = marquageExploitant;
        this.zoneMultiReseaux = zoneMultiReseaux;
        this.instructionSieml = instructionSieml;
        this.signature = signature;
        this.chantierDuration = chantierDuration;
        this.techniques = techniques;
        this.autreEnginDeChantier = autreEnginDeChantier;
        this.observation = observation;
    }

    public Marquage(Marquage marquage){
        this.numOperation = marquage.numOperation;
        this.libelleChantier = marquage.libelleChantier;
        this.titulaire = marquage.titulaire;
        this.numDict = marquage.numDict;
        this.nomSignataire = marquage.nomSignataire;
        this.numRue = marquage.numRue;
        this.numRueFin = marquage.numRueFin;
        this.nomRue = marquage.nomRue;
        this.commune = marquage.commune;
        this.date = marquage.date;
        this.photos = marquage.photos;
        this.dtdict = marquage.dtdict;
        this.recepisseDesDict = marquage.recepisseDesDict;
        this.marquageExploitant = marquage.marquageExploitant;
        this.zoneMultiReseaux = marquage.zoneMultiReseaux;
        this.instructionSieml = marquage.instructionSieml;
        this.signature = marquage.signature;
        this.chantierDuration = marquage.chantierDuration;
        this.techniques = marquage.techniques;
        this.autreEnginDeChantier = marquage.autreEnginDeChantier;
        this.observation = marquage.observation;
    }

    @Override
    public String toString() {
        return "numOperation='" + numOperation + '\'' +
                ", libelleChantier='" + libelleChantier + '\'' +
                ", titulaire='" + titulaire + '\'' +
                ", numDict='" + numDict + "\'" +
                ", nomSignataire='" + nomSignataire + '\'' +
                ", numRue=" + numRue +
                ", numRueFin=" + numRueFin +
                ", nomRue='" + nomRue + '\'' +
                ", commune='" + commune + '\'' +
                ", date=" + date +
                ", photos=" + photos +
                ", dtdict=" + dtdict +
                ", recepisseDesDict=" + recepisseDesDict +
                ", marquageExploitant=" + marquageExploitant +
                ", zoneMultiReseaux=" + zoneMultiReseaux +
                ", instructionSieml=" + instructionSieml +
                ", chantierDuration=" + chantierDuration +
                ", techniques=" + techniques +
                ", autreEnginDeChantier='" + autreEnginDeChantier + '\'' +
                ", observation='" + observation + '\'' +
                ", signature='" + signature ;
    }
}