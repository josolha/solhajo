package com.josolha.solhajo.globalexception;


import com.josolha.solhajo.util.ApiResponse;
import com.josolha.solhajo.util.LogUtil;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler{

    private final ApiResponse response;
    private final SimpMessagingTemplate messagingTemplate;


    @ExceptionHandler(IOException.class)
    public ResponseEntity<?> handleIOException(IOException e) {
        LogUtil.customInfo("IO 오류 발생 : "+e);
        return response.error("서버 내부에서 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
    }
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<?> handleDatabaseException(DataAccessException e) {
        LogUtil.customInfo("데이터베이스 연결 오류 발생 : " + e);
        return response.error("데이터베이스 연결에 실패했습니다. 잠시 후 다시 시도해주세요.");
    }
//    @MessageExceptionHandler
//    public void handleWebSocketException(Exception e, ChatRequestDto.ChatMessage chatMessage) {
//        LogUtil.customInfo("Error handling chat message: " + e.getMessage());
//
//        // 클라이언트에게 오류 메시지 전송
//        ChatRequestDto.ErrorMessage errorMessage = new ChatRequestDto.ErrorMessage();
//        errorMessage.setContent("Error processing message: " + e.getMessage());
//        errorMessage.setRoomId(chatMessage.getRoomId());
//
//        messagingTemplate.convertAndSend("/chatroom/" + chatMessage.getRoomId(), errorMessage);
//    }
}
