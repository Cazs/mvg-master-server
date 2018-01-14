package server.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.CONFLICT, reason="Invalid Job object. Please check object attributes and make sure they conform to standard.")  // 409
public class InvalidJobException extends RuntimeException
{
}
