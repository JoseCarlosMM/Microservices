package query.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by josec on 6/8/2016.
 */
@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="No ads found for the given parameters.")
public class NoAdsException extends RuntimeException {
}

