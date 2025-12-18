package toby.toby_spring.ch3.ch3_5_1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {

    public <T> T lineReadTemplate(String filepath, LineCallback<T> callback, T initVal) throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filepath));
            T res = initVal;
            String line = null;
            while ((line = br.readLine()) != null) {
                res = callback.doSomethingWithLine(line, res);
            }
            return res;

        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw e;

        } finally {
            if (br != null) {
                try {
                    br.close();

                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }

            }

        }
    }

    public String concatenate(String filepath) throws IOException {
        return lineReadTemplate(filepath, (line, value) -> value + line, "");
    }
    public Integer calcMultiply(String filepath) throws IOException {
        LineCallback<Integer> lineCallback = (line, value) -> value * Integer.parseInt(line);
        return lineReadTemplate(filepath, lineCallback, 1);
    }

    public Integer calcSum(String filepath) throws IOException {
        LineCallback<Integer> lineCallback = (line, value) -> value + Integer.parseInt(line);
        return lineReadTemplate(filepath, lineCallback, 0);
    }
}
