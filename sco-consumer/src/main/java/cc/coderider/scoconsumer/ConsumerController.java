package cc.coderider.scoconsumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by henry on 27/08/2017.
 */
@RestController
public class ConsumerController {

    @Autowired
    ConsumerService consumerService;

    @GetMapping("/calc")
    public Map complexCalc(){
        return consumerService.complexCalc();
    }
}
