package online.partyrun.partyrunbattleservice.domain.battle.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import online.partyrun.partyrunbattleservice.domain.member.service.MemberService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SqsMessageListener {

    ObjectMapper objectMapper;
    MemberService memberService;

    @SqsListener(value = "${spring.cloud.aws.sqs.queue-url}")
    public void receive(String message) throws JsonProcessingException {

        JsonNode jsonNode = objectMapper.readTree(message);
        final String event = jsonNode.get("Message").asText();
        final SqsMessage sqsMessage = objectMapper.readValue(event, SqsMessage.class);

        if (sqsMessage.isCreatedMessage()) {
            memberService.save((String) sqsMessage.value());
        }
    }
}
