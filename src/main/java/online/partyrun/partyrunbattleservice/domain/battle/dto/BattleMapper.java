package online.partyrun.partyrunbattleservice.domain.battle.dto;

import online.partyrun.partyrunbattleservice.domain.battle.entity.Battle;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BattleMapper {
    BattleResponse toResponse(Battle battle);
}
