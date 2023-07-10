package online.partyrun.partyrunbattleservice.domain.battle.acceptanceTest;

import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;

import java.lang.reflect.Type;
import java.util.concurrent.BlockingQueue;

public class StompFrameHandlerImpl<T> implements StompFrameHandler {

    private final Class<T> response;
    private final BlockingQueue<T> responses;

    public StompFrameHandlerImpl(final Class<T> response, final BlockingQueue<T> responses) {
        this.response = response;
        this.responses = responses;
    }

    @Override
    public Type getPayloadType(final StompHeaders headers) {
        return response;
    }

    @Override
    public void handleFrame(final StompHeaders headers, final Object payload) {
        responses.offer((T) payload);
    }
}
