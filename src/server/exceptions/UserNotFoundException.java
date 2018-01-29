package server.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="Incorrect user credentials.")  // 404
public class UserNotFoundException extends RuntimeException
{
}
