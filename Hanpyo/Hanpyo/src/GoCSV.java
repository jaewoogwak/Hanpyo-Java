import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class GoCSV {
    /*
     * 작성 날짜 : 2021-07-31
     * 작성자    : 고석준
     * 프로그램 요약 :  CSV 파일을 읽거나 쓸 수 있다. jdk 1.6에서는 openCSV 라이브러리가 작동하지 않았기 때문에 내가 스스로 작성하였다.
     * 기능 : 파일 읽어서 리스트로 변환*/

    private String filePath;
    private BufferedReader bufferedReader;
    private List<String[]> readCSV;

    private int index;

    //This constructor is for read CSV File
    public GoCSV(String filePath) throws IOException {
        this.filePath = filePath;
        bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(this.filePath), "UTF-8"));
        readCSV = new ArrayList<>();

        makeList(bufferedReader);
        this.index = 0;
    }

    public void makeList(BufferedReader bufferedReader) throws IOException {
        String line = null;
        while((line = bufferedReader.readLine())!=null) {
            String[] lineContents = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)",-1);

            readCSV.add(lineContents);
        }
    }

    //한 행을 읽음
    public String[] nextRead(){
        if(readCSV.size() == index){
            return null;
        }
        return readCSV.get(index++);
    }
}