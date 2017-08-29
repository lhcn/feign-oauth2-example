package cc.coderider.scooauth2server.innerconsumer;

public class ProviderClientHystrix implements ProviderClient{

        @Override
        public CalcResult add(int a, int b) {
            return new CalcResult();
        }

        @Override
        public CalcResult minus(int a, int b) {
            return new CalcResult();
        }

        @Override
        public CalcResult times(int a, int b) {
            return new CalcResult();
        }
    }