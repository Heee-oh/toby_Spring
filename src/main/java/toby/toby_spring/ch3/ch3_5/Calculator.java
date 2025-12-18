package toby.toby_spring.ch3.ch3_5;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {

    public Integer lineReadTemplate(String filepath, LineCallback callback, int initVal) throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(filepath));
            Integer res = initVal;
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

    public Integer calcMultiply(String filepath) throws IOException {
        LineCallback lineCallback = new LineCallback() {
            @Override
            public Integer doSomethingWithLine(String line, Integer value) {
                return value * Integer.parseInt(line);
            }
        };
        return lineReadTemplate(filepath, lineCallback, 1);
    }

    public Integer calcSum(String filepath) throws IOException {
        LineCallback lineCallback = new LineCallback() {
            @Override
            public Integer doSomethingWithLine(String line, Integer value) {
                return value + Integer.parseInt(line);
            }
        };
        return lineReadTemplate(filepath, lineCallback, 0);
    }
    public Integer fileReadTemplate(String filepath, BufferedReaderCallback callback) throws IOException{
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(filepath));
            return callback.doSomethingWithReader(br);

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
//    public Integer calcSum(String filepath) throws IOException {
//        BufferedReaderCallback sumCallback = new BufferedReaderCallback() {
//            @Override
//            public Integer doSomethingWithReader(BufferedReader br) throws IOException {
//                Integer sum = 0;
//                String line = null;
//
//                while ((line = br.readLine()) != null) {
//                    sum += Integer.parseInt(line);
//                }
//                return sum;
//            }
//        };
//
//        return fileReadTemplate(filepath, sumCallback);
//    }
//
//    public Integer calcMultiply(String filePath) throws IOException{
//        BufferedReaderCallback multiplyCallback = new BufferedReaderCallback() {
//            @Override
//            public Integer doSomethingWithReader(BufferedReader br) throws IOException {
//                Integer sum = 1;
//                String line = null;
//
//                while ((line = br.readLine()) != null) {
//                    sum *= Integer.parseInt(line);
//                }
//                return sum;
//            }
//        };
//
//        return fileReadTemplate(filePath, multiplyCallback);
//    }
}
