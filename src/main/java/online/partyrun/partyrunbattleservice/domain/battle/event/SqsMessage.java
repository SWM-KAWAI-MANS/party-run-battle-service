package online.partyrun.partyrunbattleservice.domain.battle.event;

public record SqsMessage(String type, Object value) {
    private static final String CREATED_EVENT_TYPE = "CREATED";

    public boolean isCreatedMessage() {
        return CREATED_EVENT_TYPE.equals(type);
    }
}
