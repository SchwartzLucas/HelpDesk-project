package schwartz.spring.app.controller;

import schwartz.spring.app.repository.TeamRepository;

public class TeamController {

    TeamRepository teamRepository;

    public TeamController(TeamRepository teamRepository){
        this.teamRepository = teamRepository;
    }
}
