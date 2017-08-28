package cc.coderider.scoconsumer;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by henry on 27/08/2017.
 */


@FeignClient(value = "provider-service", fallback = ProviderClient.ProviderClientHystrix.class)
public interface ProviderClient {

    // 不加 @RequestParam 的value也报错
    @GetMapping("/add")
    CalcResult add(
            @RequestParam(value = "a",required = false, defaultValue = "0") int a,
            @RequestParam(value = "a",required = false, defaultValue = "0") int b);

    @GetMapping("/minus")
    CalcResult minus(
            @RequestParam(value = "a",required = false, defaultValue = "0") int a,
            @RequestParam(value = "a",required = false, defaultValue = "0") int b);

    @GetMapping("/times")
    CalcResult times(
            @RequestParam(value = "a",required = false, defaultValue = "0") int a,
            @RequestParam(value = "a",required = false, defaultValue = "0") int b);

    class CalcResult {
        private int result;

        public int getResult() {
            return result;
        }

        public void setResult(int result) {
            this.result = result;
        }
    }

    class ProviderClientHystrix implements ProviderClient{

        @Override
        public CalcResult add(int a, int b) {
            return null;
        }

        @Override
        public CalcResult minus(int a, int b) {
            return null;
        }

        @Override
        public CalcResult times(int a, int b) {
            return null;
        }
    }

}
