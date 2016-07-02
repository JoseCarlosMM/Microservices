package matching;

import matching.Exceptions.CategoryNotFoundException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

/**
 * Created by josec on 6/4/2016.
 */
//@RestController
public class MatchingController {
    @RequestMapping(value = "/matching",method = RequestMethod.GET, produces = "application/json")
    public ArrayList matching(
            @RequestParam(value = "category", required = true) Integer category
    ) throws CategoryNotFoundException {
        Handler handler = new Handler();
        return handler.execute(category);
    }
}
