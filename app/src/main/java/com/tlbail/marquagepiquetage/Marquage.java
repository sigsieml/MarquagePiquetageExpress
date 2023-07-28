package com.tlbail.marquagepiquetage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Marquage {
    public String numOperation  = "";
    public String libelleChantier  = "";
    public String titulaire  = "";
    public String nomSignataire  = "";
    public int numRue  = 1;
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

    public Marquage(){}

    public Marquage(String numOperation, String libelleChantier, String titulaire, String nomSignataire, int numRue, String nomRue, String commune, Calendar date, List<String> photos, Boolean dtdict, Boolean recepisseDesDict, Boolean marquageExploitant, Boolean zoneMultiReseaux, Boolean instructionSieml, String signature) {
        this.numOperation = numOperation;
        this.libelleChantier = libelleChantier;
        this.titulaire = titulaire;
        this.nomSignataire = nomSignataire;
        this.numRue = numRue;
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
    }

    public Marquage(Marquage marquage){
        this.numOperation = marquage.numOperation;
        this.libelleChantier = marquage.libelleChantier;
        this.titulaire = marquage.titulaire;
        this.nomSignataire = marquage.nomSignataire;
        this.numRue = marquage.numRue;
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
    }

    @Override
    public String toString() {
        return "numOperation='" + numOperation + '\'' +
                ", libelleChantier='" + libelleChantier + '\'' +
                ", titulaire='" + titulaire + '\'' +
                ", nomSignataire='" + nomSignataire + '\'' +
                ", numRue=" + numRue +
                ", nomRue='" + nomRue + '\'' +
                ", commune='" + commune + '\'' +
                ", date=" + date +
                ", photos=" + photos +
                ", dtdict=" + dtdict +
                ", recepisseDesDict=" + recepisseDesDict +
                ", marquageExploitant=" + marquageExploitant +
                ", zoneMultiReseaux=" + zoneMultiReseaux +
                ", instructionSieml=" + instructionSieml +
                ", signature='" + signature ;
    }
}