package com.hits.open.world.websocket.exception;

import com.hits.open.world.public_interface.exception.ExceptionInApplication;
import com.hits.open.world.public_interface.exception.ExceptionType;
import lombok.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import java.util.Map;


public class WebSocketExceptionInterceptor extends StompSubProtocolErrorHandler {
    private static final Map<ExceptionType, String> STATUS_CODE_MAP = Map.of(
            ExceptionType.INVALID, "1011",
            ExceptionType.ALREADY_EXISTS, "1008",
            ExceptionType.NOT_FOUND, "1008",
            ExceptionType.UNAUTHORIZED, "1008"
    );

    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]>clientMessage, @NonNull Throwable exception) {
        if (exception instanceof ExceptionInApplication exceptionInApplication) {
            return handleExceptionInApplication(clientMessage, exceptionInApplication);
        }

        return super.handleClientMessageProcessingError(clientMessage, exception);
    }

    private Message<byte[]> handleExceptionInApplication(Message<byte[]> clientMessage, ExceptionInApplication exception) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        setReceiptIdForClient(clientMessage, accessor);
        accessor.setMessage(STATUS_CODE_MAP.get(exception.getType()));
        accessor.setLeaveMutable(true);

        String message = transformMessageToJSONString(exception.getMessage());

        return MessageBuilder.createMessage(message.getBytes(), accessor.getMessageHeaders());
    }

    private String transformMessageToJSONString(String message) {
        return "{\"message\": \"" + message + "\"}";
    }

    private void setReceiptIdForClient(Message<byte[]> clientMessage, StompHeaderAccessor accessor) {
        StompHeaderAccessor clientAccessor = StompHeaderAccessor.wrap(clientMessage);
        String receiptId = clientAccessor.getReceipt();
        if (receiptId != null) {
            accessor.setReceiptId(receiptId);
        }
    }
}
