package online.partyrun.partyrunbattleservice.domain.battle.event;

public record SqsMessage(String type, Object value) {
    private static final String CREATED_EVENT_TYPE = "CREATED";
    private static final String DELETED_EVENT_TYPE = "DELETED";

    public boolean isCreatedMessage() {
        return CREATED_EVENT_TYPE.equals(type);
    }

    public boolean isDeletedMessage() {
        return DELETED_EVENT_TYPE.equals(type);
    }
}
