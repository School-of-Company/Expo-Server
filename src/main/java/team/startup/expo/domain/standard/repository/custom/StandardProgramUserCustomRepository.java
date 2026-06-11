package team.startup.expo.domain.standard.repository.custom;

import team.startup.expo.domain.standard.entity.StandardProgramUser;

import java.util.List;

public interface StandardProgramUserCustomRepository {
    List<StandardProgramUser> findByStandardProgramIdWithFetch(Long standardProId);
}