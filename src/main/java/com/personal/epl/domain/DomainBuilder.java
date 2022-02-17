package com.personal.epl.domain;


import java.io.Serializable;

public class DomainBuilder implements Serializable {
    private final String team;
    private final int position;
    private final int goals;
    private final float ratio;
    private final int points;

    private DomainBuilder(AuxDomainBuilder builder) {
        this.team = builder.team;
        this.position = builder.position;
        this.goals = builder.goals;
        this.ratio = builder.ratio;
        this.points = builder.points;
    }


    public String getTeam() {
        return team;
    }

    public int getPosition() {
        return position;
    }

    public int getGoals() {
        return goals;
    }

    public float getRatio() {
        return ratio;
    }

    public int getPoints() {
        return points;
    }

    public static class AuxDomainBuilder
    {
        private final String team; // required
        private final int position; // required
        private int goals; // optional
        private float ratio; // optional
        private int points;

        public AuxDomainBuilder(String team, int position) {
            this.team = team;
            this.position = position;
        }
        public AuxDomainBuilder goals(int goals) {
            this.goals = goals;
            return this;
        }
        public AuxDomainBuilder points(int points) {
            this.points = points;
            return this;
        }


        public AuxDomainBuilder ratio(float ratio) {
            this.ratio = ratio;
            return this;
        }

        public DomainBuilder build() {
            DomainBuilder user =  new DomainBuilder(this);
            return user;
        }

    }
}