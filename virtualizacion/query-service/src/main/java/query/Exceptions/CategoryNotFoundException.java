package query.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

/**
 * Created by josec on 6/5/2016.
 */
@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="Category not found")
public class CategoryNotFoundException extends RuntimeException {
}

