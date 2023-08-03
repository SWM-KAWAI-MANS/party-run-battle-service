package online.partyrun.partyrunbattleservice.domain.battle.acceptanceTest;

import online.partyrun.partyrunbattleservice.domain.battle.dto.BattleWebSocketResponse;

import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;
import java.util.concurrent.BlockingQueue;

public class StompFrameHandlerImpl implements StompFrameHandler {

    private final BlockingQueue<BattleWebSocketResponse> responses;

    public StompFrameHandlerImpl(final BlockingQueue<BattleWebSocketResponse> responses) {
        this.responses = responses;
    }

    @Override
    public Type getPayloadType(final StompHeaders headers) {
        return BattleWebSocketResponse.class;
    }

    @Override
    public void handleFrame(final StompHeaders headers, final Object payload) {
        responses.offer((BattleWebSocketResponse) payload);
    }
}
