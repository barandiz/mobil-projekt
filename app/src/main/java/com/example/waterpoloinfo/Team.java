package com.example.waterpoloinfo;

public class Team {
    private String team_name;
    private String team_id;

    public Team(String team_name, String team_id) {
        this.team_name = team_name;
        this.team_id = team_id;
    }

    public String getTeam_name() {return team_name;}

    public void setTeam_name(String team_name) {this.team_name = team_name;}

    public String getTeam_id() {return team_id;}

    public void setTeam_id(String team_id) {this.team_id = team_id;}
}
