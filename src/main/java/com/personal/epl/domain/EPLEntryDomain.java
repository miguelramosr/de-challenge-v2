package com.personal.epl.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EPLEntryDomain implements Serializable {

    private String Div;
    private String Date;
    private String HomeTeam;
    private String AwayTeam;
    private int FTHG;
    private int FTAG;
    private String FTR;
    private String HTR;
    private int HTHG;
    private int HTAG;
    private String Referee;
    private int HS;
    private int AS;
    private int HST;
    private int AST;
    private int HF;
    private int AF;
    private int HC;
    private int AC;
    private int HY;
    private int AY;
    private int HR;
    private int AR;

    @Override
    public String toString() {
        return "EPLEntryDomain{" +
                "Div='" + Div + '\'' +
                ", Date='" + Date + '\'' +
                ", HomeTeam='" + HomeTeam + '\'' +
                ", AwayTeam='" + AwayTeam + '\'' +
                ", FTHG=" + FTHG +
                ", FTAG=" + FTAG +
                ", FTR='" + FTR + '\'' +
                ", HTR='" + HTR + '\'' +
                ", HTHG=" + HTHG +
                ", HTAG=" + HTAG +
                ", Referee='" + Referee + '\'' +
                ", HS=" + HS +
                ", AS=" + AS +
                ", HST=" + HST +
                ", AST=" + AST +
                ", HF=" + HF +
                ", AF=" + AF +
                ", HC=" + HC +
                ", AC=" + AC +
                ", HY=" + HY +
                ", AY=" + AY +
                ", HR=" + HR +
                ", AR=" + AR +
                '}';
    }
}
