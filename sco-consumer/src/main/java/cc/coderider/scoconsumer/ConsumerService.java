package cc.coderider.scoconsumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by henry on 27/08/2017.
 */
@Service
public class ConsumerService {

    @Autowired
    ProviderClient providerClient;

    public Map complexCalc(){
        int add=providerClient.add(1,3).getResult();
        //int minus=providerClient.times(add,1024).getResult();

        // more and more logic
        // ......
        int finalResult=add;

        Map result = new HashMap();
        result.put("key1","someValue");
        result.put("result",finalResult);

        return result;
    }
}
