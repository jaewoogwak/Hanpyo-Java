import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.beans.EventHandler;
import java.io.IOException;
import java.sql.Array;
import java.util.*;
import java.util.LinkedList; //import
import java.util.List;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.regex.PatternSyntaxException;
import javax.imageio.ImageIO;


public class App extends JFrame {
    String subjectHeader[] = {"코드", "과목명", "분반", "대상", "비고", "학점", "설계", "정원", "교수님", "개설학부"};
    Color[] colorList = {Color.MAGENTA, Color.PINK, Color.YELLOW, Color.CYAN, Color.BLUE, Color.green, Color.lightGray, Color.red, Color.GRAY, Color.WHITE};
    String[][] contents = new String[804][11]; // 시간표 DB
    int cnt = 0;

    String []timeInfo = { // 시간 단위 배열
            "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", "12:00", "12:30", "13:00",
            "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30", "17:00", "17:30"
    };
    String []timeCode = { // 시간 코드 배열
            "01A", "01B", "02A", "02B", "03A", "03B", "04A", "04B", "05A",
            "05B", "06A", "06B", "07A", "07B", "08A", "08B", "09A", "09B"
    };
    HashMap<String, String>hashMap = new HashMap<String, String>();

    Container contentPane; // 컨텐트 패인
    JLabel header; // 상단 타이틀 헤더
    JLabel logo; // 한표 로고
    JTextField searchField; // 검색창
    JButton searchBtn; // 검색 버튼

    JComboBox comboBox; // 개설학부 콤보박스
    JButton putSubjectBtn; // 과목 담기 버튼
    JScrollPane tableScrollPane; // 테이블 스크롤 래퍼
    JTable subjectList; // 과목 리스트
    int subjectCount =0; // 과목 갯수
    DefaultTableModel model; // 과목 리스트 컨트롤 모델

    JButton deleteSubjectInQueueBtn; // 과목 빼기 버튼
    JButton clearSubjectInQueueBtn; // 과목 초기화 버튼

    int classCredit; // 수강 학점

    JLabel classCreditView; // 수강 학점 뷰

    JScrollPane queueScrollPane; // 시간표 큐 스크롤 래퍼
    JTable subJectQueueTable; // 시간표 큐 테이블
    List<String[]> subjectQueue = new ArrayList<String[]>(); // 시간표 큐
    String [][] columnQueue; // 시간표 큐의 테이블 컨트롤 모델 초기화에 필요
    DefaultTableModel queueModel; // 큐 컨트롤 모델

//    ArrayList<ArrayList<String>>subjectTable = new ArrayList<ArrayList<String>>(804); // 시간표 테이블
    ArrayList<String>[] subjectTable = new ArrayList[5]; // 시간표 테이블
    JPanel wrapper = new JPanel(); // 시간표 테이블 뷰

    JButton imageSaveBtn; // 이미지 저장 버튼
    JButton recommandBtn; // 과목 추천 버튼


    public App() {
        setTitle("한표");

        createHeader();
        createSearchBar();
        createSearchBtn();
        createComboBox();
        createPushSubjectBtn();
        createScrolledTable();
        createDeleteSubjectBtn();
        createClearSubjectBtn();
        createClassCreditLabel();
        createSubjectQueue();

        initSubjectTalbe();
        initComboBoxHashMap();
        printSubjectTable();

        createImageSaveBtn();
        createRecommandBtn();
        createTimetable();
        setSize(1450, 1000);
        setVisible(true);
        initApp();

    }

    public void initApp() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        maximizeWindow();

    }

    public void initComboBoxHashMap() {
        String []subjects = { "개설학부", "컴공", "디공,건축", "기계", "전전통", "에신화", "산경", "메카", "교양학부", "HRD학과", "융합"};
        for(String subject: subjects) {
            String text="";
            if (subject.equals("컴공")) text = "컴퓨터공학부";
            else if (subject.equals("디공,건축")) text="디자인ㆍ건축공학부";
            else if (subject.equals("기계")) text="기계공학부";
            else if (subject.equals("전전통")) text="전기ㆍ전자ㆍ통신공학부";
            else if (subject.equals("에신화")) text ="에너지신소재화학공학부";
            else if (subject.equals("산경")) text="산업경영학부";
            else if (subject.equals("메카")) text="메카트로닉스공학부";
            else if (subject.equals("융합")) text= "융합학과";
            else if (subject.equals("HRD학과")) text = "HRD학과";
            else text = "개설학부";
            hashMap.put(subject, text);
        }

    }

    public void createHeader() {
        contentPane = getContentPane();
        contentPane.setLayout(null);

        ImageIcon icon = new ImageIcon("/Users/jaewoogwak/IdeaProjects/Hanpyo/src/logo.png");
        // ImageIcon 객체에서 Image 추출
        Image img = icon.getImage();

        // 추출된 Image의 크기 조절하여 새로운 Image 객체 생성
        Image updateImg = img.getScaledInstance(200, 100, Image.SCALE_SMOOTH);
        ImageIcon updateIcon = new ImageIcon(updateImg);
        logo = new JLabel(updateIcon);
        logo.setSize(200,90);
        header = new JLabel();
        header.add(logo);
        header.setBorder(new LineBorder(Color.black));
        header.setSize(1450, 100);

        contentPane.add(header);
    }

    // 검색바 만드는 함수
    public void createSearchBar() {
        contentPane = getContentPane();
//        contentPane.setLayout(null);

        searchField = new JTextField(20);
        searchField.setBorder(new LineBorder(Color.BLACK));
        searchField.setLocation(0, 100); // 버튼의 위치 설정
        searchField.setSize(200, 20);
        contentPane.add(searchField);
    }

    // 개설학부 콤보박스 만드는 함수
    public void createComboBox() {
        contentPane = getContentPane();

        String []subjects = { "개설학부", "컴공", "디공,건축", "기계", "전전통", "에신화", "산경", "메카", "교양학부", "HRD학과", "융합"};
        comboBox = new JComboBox(subjects);
        comboBox.setLocation(280, 100); // 버튼의 위치 설정
        comboBox.setSize(100, 20);
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox<String> cb=(JComboBox<String>)e.getSource();
                int index=cb.getSelectedIndex();
                sortByComboBox(subjects[index]);
            }
        });

        contentPane.add(comboBox);
    }

    // 검색 필터에 따른 테이블 정렬
    public void sortBySearchField() {

        final TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(model);
        subjectList.setRowSorter(sorter);
        String text = searchField.getText();
        if(text.length() == 0) {
            sorter.setRowFilter(null);
        } else {
            try {
                sorter.setRowFilter(RowFilter.regexFilter(text));
            } catch(PatternSyntaxException pse) {
                System.out.println("Bad regex pattern");
            }
        }

    }

    // 개설 학부 콤보박스에 따른 테이블 정렬
    public void sortByComboBox(String text) {
        System.out.println("sortByCombobox" + text);
        RowFilter<Object, Object> filter = new RowFilter<Object, Object>() {
            public boolean include(Entry entry) {
                String department = (String) entry.getValue(7);
                return department.contains(hashMap.get(text));
            }
        };

        final TableRowSorter<TableModel> sorter2 = new TableRowSorter<TableModel>(model);
        subjectList.setRowSorter(sorter2);

        if (text.equals("개설학부")) {
            sorter2.setRowFilter(null);
        } else {
            sorter2.setRowFilter(filter);
            subjectList.setRowSorter(sorter2);
        }

    }

    // 검색 버튼 만드는 함수
    public void createSearchBtn() {
        contentPane = getContentPane();
        searchBtn = new JButton("검색");
        searchBtn.setLocation(210, 100);
        searchBtn.setSize(70, 20);
        searchBtn.addActionListener(new MyActionListener());
        contentPane.add(searchBtn);
    }

    // 과목 담기 버튼 만드는 함수
    public void createPushSubjectBtn() {
        contentPane = getContentPane();
        putSubjectBtn = new JButton("과목 담기");
        putSubjectBtn.addActionListener(new MyActionListener());

        putSubjectBtn.setSize(70, 20);
        putSubjectBtn.setLocation(390, 100);
        contentPane.add(putSubjectBtn);
    }

    // 과목 담기
    public boolean putSubject() {
        if (subjectList.getSelectedRow() == -1) {
            return false;
        } else {
            String subjectStr[] = new String[10];
            int idx = subjectList.getSelectedRow();

            for (int i=0; i<10; i++) {
                subjectStr[i] = subjectList.getValueAt(idx, i).toString();
            }

            // 시간표가 겹치는지 확인
            int realIdx = findIndex(subjectStr);
            idx = realIdx;
            int arr[] = toArray(contents[idx][10]);

            int daysIdx; // 요일
            int timeIdx; // 시간

            boolean isTableOverlaped = false;

            for(int i=0; i<arr.length; i++) {
                daysIdx = arr[i] / 100;
                timeIdx = arr[i] % 100;

                if (isOverlaped(daysIdx, timeIdx)) {
                    isTableOverlaped = true;
                }
            }

            // 하나도 안 겹친다면
            if (isTableOverlaped) {
                System.out.println("시간표 테이블에 강의 시간이 겹치는 과목이 존재합니다.");
                return false;
            }

            // 시간표 테이블에 추가
            String lectureText = subjectStr[1] + subjectStr[2] + subjectStr[8];
            isTableOverlaped = addItemInSubjectTable(contents[idx][10], lectureText);

            if (isTableOverlaped) {
                System.out.println("겹치는 과목이 존재합니다~ 큐도 못넣엉~");
                return false;
            }

            // 과목 학점 가져와서 수강 학점에 추가
            int subjectCredit = Integer.parseInt(subjectStr[5]);
            increaseClassCredit(subjectCredit);

            // 시간표 큐 (subjectQueue)에도 추가
            subjectQueue.add(contents[idx]);

            System.out.println(subjectStr);
            queueModel.addRow(subjectStr);
            subjectCount += 1;
        }
        return true;
    }

    // 시간표 리스트 만드는 함수
    public void createScrolledTable() {
        contentPane = getContentPane();
        contentPane.setLayout(null);

        ArrayList<String>[] arr = new ArrayList[804];
        ArrayList<ArrayList<String>> alist =new ArrayList<ArrayList<String>>();

        try {
            GoCSV goCSV = new GoCSV("/Users/jaewoogwak/IdeaProjects/JavaGUI/src/csv/data.csv");
            String[] line=null;
            int count = 0;
            while((line = goCSV.nextRead())!=null){
                ArrayList<String> rows = new ArrayList<String>();
                for(String a : line) {
                    rows.add(a);
                }
                alist.add(rows);
            }

            for(int i =0; i<804; i++) {
                ArrayList<String> row = alist.get(i);
                contents[i] = row.toArray(new String[row.size()]);
                System.out.println("cccc" + contents[i][10]);
            }
            model = new DefaultTableModel(contents, subjectHeader);
            subjectList = new JTable(model) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            subjectList.setRowSorter(new TableRowSorter(model)); // 테이블 정렬 기능 추가
            subjectList.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        System.out.println("더블클릭~");
                        putSubject();
                    } else {
//                        System.out.println("그냥 클릭~");
//                        String subjectStr[] = new String[10];
//                        int idx = subjectList.getSelectedRow();
//
//                        for (int i=0; i<10; i++) {
//                            subjectStr[i] = subjectList.getValueAt(idx, i).toString();
//                        }
//
//                        int realIdx = findIndex(subjectStr);
//                        idx = realIdx;
//                        int arr[] = toArray(contents[idx][10]);
//                        System.out.println("굵은선 만들기 기다려" + Arrays.toString(arr));
//                        paintBorder(arr);


                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {}

                @Override
                public void mouseReleased(MouseEvent e) {}

                @Override
                public void mouseEntered(MouseEvent e) {}

                @Override
                public void mouseExited(MouseEvent e) {}
            });


            tableScrollPane = new JScrollPane(subjectList);
            tableScrollPane.setBorder(new LineBorder(Color.black));
            tableScrollPane.setSize(750, 400);
            tableScrollPane.setLocation(0, 130); // 버튼의 위치 설정
            contentPane.add(tableScrollPane);
        } catch (IOException e) {

        }
    }

    // 과목 빼기 버튼 만드는 함수
    public void createDeleteSubjectBtn() {
        contentPane = getContentPane();
        deleteSubjectInQueueBtn = new JButton("과목 빼기");
        deleteSubjectInQueueBtn.addActionListener(new MyActionListener());

        deleteSubjectInQueueBtn.setSize(70,20);
        deleteSubjectInQueueBtn.setLocation(0, 530);
        contentPane.add(deleteSubjectInQueueBtn);
    }

    // 과목 빼기
    public void deleteSubjectInQueue() {
        System.out.println("과목 빼기");
        int deleteRow = subJectQueueTable.getSelectedRow();
        if (deleteRow == -1) {
            return;
        } else {
            System.out.println("삭제할 rowIdx" +  deleteRow);
            DefaultTableModel queueModel = (DefaultTableModel) subJectQueueTable.getModel();
            String subjectInQueue[] = new String[10];
//            for (int i = 0; i < 10; i++) {
//                subjectInQueue[i] = subjectQueue.get(deleteRow).toString();
//            }
            subjectInQueue = subjectQueue.get(deleteRow);
            System.out.println("subjectInQueue" + Arrays.toString(subjectInQueue) );
            int idx = findIndex(subjectInQueue);
            System.out.println("삭제할 시간표 DB에서의 idx" +  idx + Arrays.toString(subjectInQueue));

            // 시간표 테이블에서도 삭제
            deleteItemInSubjectTable(contents[idx][10]);


            // 시간표 큐 (subjectQueue)에서도 삭제
            subjectQueue.remove(deleteRow);

            int subjectCredit = Integer.parseInt(subjectInQueue[5]);
            decreaseClassCredit(subjectCredit);
            queueModel.removeRow(deleteRow);
            subjectCount -= 1;
        }
    }

    // 수강 학점 더하는 함수
    public void increaseClassCredit(int credit) {
        contentPane = getContentPane();
        classCredit += credit;
        System.out.println("increase credit"+ classCredit);
        String nextCredit = Integer.toString(classCredit);
        classCreditView.setText(nextCredit);

    }

    // 수강학점 빼는 함수
    public void decreaseClassCredit(int credit) {
        contentPane = getContentPane();
        classCredit -= credit;
        System.out.println("decrease credit"+ classCredit);
        String nextCredit = Integer.toString(classCredit);
        classCreditView.setText(nextCredit);
    }

    // 더블 클릭해서 과목 빼기
    public void onDoubleClickDel블eteSubject() {

    }

    // 과목 초기화 버튼 만드는 함수
    public void createClearSubjectBtn() {
        contentPane = getContentPane();
        clearSubjectInQueueBtn = new JButton("과목 초기화");
        clearSubjectInQueueBtn.addActionListener(new MyActionListener());

        clearSubjectInQueueBtn.setSize(70,20);
        clearSubjectInQueueBtn.setLocation(75, 530);
        contentPane.add(clearSubjectInQueueBtn);
    }

    // 과목 초기화
    public void clearSubject() {
        for (int i=0; i<subjectQueue.size(); i++) {
            System.out.println("과목 초기화" +  Arrays.toString(subjectQueue.get(i)));

        }
        DefaultTableModel queueModel = (DefaultTableModel) subJectQueueTable.getModel();

        // 시간표 테이블 초기화
        initSubjectTalbe();

        // 시간표 큐 초기화
        while(subjectQueue.size() > 0) {
            queueModel.removeRow(0);
            subjectQueue.remove(0);
        }
        int []emptyArr = {};
        paintTable(emptyArr, "");

        // 수강학점 초기화
        classCredit = 0;
        classCreditView.setText(Integer.toString(classCredit));
        subjectCount = 0;
    }

    // 수강 학점 라벨 만드는 함수
    public void createClassCreditLabel() {
        contentPane = getContentPane();
        classCreditView = new JLabel("0");
        classCreditView.setSize(80, 20);
        classCreditView.setLocation(150, 530);
        contentPane.add(classCreditView);
    }


    // 시간표 큐 (과목 큐) 만드는 함수
    public void createSubjectQueue() {
        contentPane = getContentPane();

        queueModel = new DefaultTableModel(columnQueue, subjectHeader);
        subJectQueueTable = new JTable(queueModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        subJectQueueTable.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    System.out.println("더블클릭~");
                    deleteSubjectInQueue();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {}

            @Override
            public void mouseExited(MouseEvent e) {}
        });
        queueScrollPane = new JScrollPane(subJectQueueTable);
        queueScrollPane.setLocation(0,550);
        queueScrollPane.setSize(750, 300);
        contentPane.add(queueScrollPane);
    }

    // 시간표 테이블 만드는 함수
    public void createTimetable() {
        contentPane = getContentPane();
        // 먼저 틀 세팅하고
        setDefaultTable();
        contentPane.add(wrapper);

    }

    // 시간표 테이블 틀 세팅 함수
    public void setDefaultTable() {
        wrapper = new JPanel();
        wrapper.setLayout(new GridLayout(22, 7));
        wrapper.setBorder(new LineBorder(Color.black));
        for (int i=0; i<22; i++) {
            for (int j =0; j<7; j++) {
                String text = "";
                if (i==1) {
                    if (j == 2) text = "월요일";
                    else if (j==3) text = "화요일";
                    else if (j==4) text = "수요일";
                    else if (j==5) text = "목요일";
                    else if (j==6) text = "금요일";
                }
                else if (i>=2 && i<=19) {
                    if (j == 0) text = timeCode[i-2];
                    else if (j==1) text = timeInfo[i-2];
                }
                else if (i==20 || i==21) {
                    if (j==0 || j==1) {
                        text = "이후";
                    }
                }
                JLabel la =  new JLabel(text);
                la.setBorder(new LineBorder(Color.black));
                la.addMouseListener(new MouseListener() {

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        JLabel label = (JLabel)e.getSource();
//                        System.out.println("클릭이요~" + label.getText());
//                        label.setBorder(new LineBorder(Color.BLUE));
                    }
                    @Override
                    public void mousePressed(MouseEvent e) {}
                    @Override
                    public void mouseReleased(MouseEvent e) {}
                    @Override
                    public void mouseEntered(MouseEvent e) {}
                    @Override
                    public void mouseExited(MouseEvent e) {}
                });
                wrapper.add(la);
            }
        }
        wrapper.setSize(600, 700);
        wrapper.setLocation(760, 130);
        System.out.println("setDefaultTable");

    }



    // 시간표 테이블 초기화 함수
    public void initSubjectTalbe() {
        cnt =0;
        for (int i=0; i<subjectTable.length; i++) {
            subjectTable[i] = new ArrayList<String>();
        }

        for (int i=0; i<subjectTable.length; i++) {
            for(int j =0; j<24; j++) {
                subjectTable[i].add("none");
            }
        }
    }

    // 시간표 테이블 출력 함수
    public void printSubjectTable() {
        for (int i=0; i<subjectTable.length; i++) {
            for(int j =0; j<24; j++) {
                System.out.print(subjectTable[i].get(j) + " ");
            }
            System.out.println();
        }
    }

    // 시간표 테이블에 값 넣는 함수
    // String arr[]: 0~23의 '요일+시간대' 리스트
    // String text: '과목명 + 분반 + 교수님'
    public boolean addItemInSubjectTable(String arrText, String text) {
        // String arr[]: "[304, 305, 306, 307]" -> [304, 305, 306, 307]
        String splitedArrText[] = arrText.split(", ");
        System.out.println("splited" + Arrays.toString(splitedArrText));
        int arr[] = new int[splitedArrText.length];
        for (int i =0; i<splitedArrText.length; i++) {
            if (i == 0 || i == splitedArrText.length - 1) {
                if (i == 0) {
                    arr[i] = Integer.parseInt(splitedArrText[i].substring(2));
                } else {
                    arr[i] = Integer.parseInt(splitedArrText[i].substring(0, splitedArrText[i].length() - 2));
                }
            } else arr[i] = Integer.parseInt(splitedArrText[i]);
        }
        System.out.println("arr" + Arrays.toString(arr));

        int daysIdx; // 요일
        int timeIdx; // 시간

        // 먼저 겹치는지 확인
        boolean isTableOverlaped = false;

        for(int i=0; i<arr.length; i++) {
            daysIdx = arr[i] / 100;
            timeIdx = arr[i] % 100;

            if (isOverlaped(daysIdx, timeIdx)) {
                isTableOverlaped = true;
            }
        }

        // 하나도 안 겹친다면
        if (isTableOverlaped) {
            System.out.println("시간표 테이블에 강의 시간이 겹치는 과목이 존재합니다.");
            return false;
        }

        for (int i=0; i<arr.length; i++) {
            daysIdx = arr[i] / 100;
            timeIdx = arr[i] % 100;
            System.out.println("days and time" +  daysIdx +  " " + timeIdx);
            subjectTable[daysIdx].set(timeIdx, text);
        }
        paintTable(arr, text);

        printSubjectTable();
        return false;
    }

    // String으로된 배열을 받아서 순수 배열로 리턴해줌
    public int[] toArray(String arrText) {
        String newText = "".concat(arrText);
        System.out.println("newText" + newText);
        String splitedArrText[] = newText.split(", ");
        int arr[] = new int[splitedArrText.length];
        for (int i =0; i<splitedArrText.length; i++) {
            if (i == 0 || i == splitedArrText.length - 1) {
                if (i == 0) {
                    arr[i] = Integer.parseInt(splitedArrText[i].substring(2));
                } else {
                    arr[i] = Integer.parseInt(splitedArrText[i].substring(0, splitedArrText[i].length() - 2));
                }
            } else arr[i] = Integer.parseInt(splitedArrText[i]);
        }
        System.out.println("arr in toArray fc" + Arrays.toString(arr));
        return arr;
    }

    // 시간표 테이블에 서로 다른(혹은 같x은) 과목이 같은 자리에 들어가는지 체크
    // int rowIdx, int colIdx
    public boolean isOverlaped(int rowIdx, int colIdx)  {
        System.out.println("isOverlaped" + Arrays.toString(subjectTable));
        if (!subjectTable[rowIdx].get(colIdx).equals("none")) {
            return true;
        }
        return false;
    }

    // 테이블에서 시간표 인덱스를 찾는 함수
    public int findIndex(String[] subjectStr) {
        for(int i=0; i<804; i++) {
            if (subjectStr[0].equals(contents[i][0]) && subjectStr[1].equals(contents[i][1]) && subjectStr[2].equals(contents[i][2])) {
                return i;
            }
        }
        return -1;
    }

    // 시간표 테이블에서 값 빼는 함수
    public boolean deleteItemInSubjectTable(String arrText) {
        String splitedArrText[] = arrText.split(", ");
        System.out.println("delete splited" + Arrays.toString(splitedArrText));
        int arr[] = new int[splitedArrText.length];
        for (int i =0; i<splitedArrText.length; i++) {
            if (i == 0 || i == splitedArrText.length - 1) {
                if (i == 0) {
                    arr[i] = Integer.parseInt(splitedArrText[i].substring(2));
                } else {
                    arr[i] = Integer.parseInt(splitedArrText[i].substring(0, splitedArrText[i].length() - 2));
                }
            } else arr[i] = Integer.parseInt(splitedArrText[i]);
        }
        System.out.println("arr" + Arrays.toString(arr));


        int daysIdx; // 요일
        int timeIdx; // 시간

        for (int i=0; i<arr.length; i++) {
            daysIdx = arr[i] / 100;
            timeIdx = arr[i] % 100;
            System.out.println("days and time" +  daysIdx +  " " + timeIdx);
            subjectTable[daysIdx].set(timeIdx, "none");
        }

        paintTable(arr, "");
        return true;
    }


    // 시간표 테이블 paint 함수
    public void paintTable(int times[], String lecture) {
        System.out.println("paintTable --- " + Arrays.toString(times) + " " + lecture);
        ArrayList<Integer> days = new ArrayList<Integer>();
        ArrayList<Integer> time = new ArrayList<Integer>();

        if (times.length != 0) {
            for(int i=0; i<times.length; i++) {
                days.add(times[i] / 100);
                time.add(times[i] % 100);
            }
        }

        System.out.println("days:" + days + " time:" + time);

        wrapper = new JPanel();
        wrapper.setLayout(new GridLayout(22, 7));
        wrapper.setBorder(new LineBorder(Color.black));
        for (int i=0; i<22; i++) {
            for (int j = 0; j < 7; j++) {
                String text = "";
                JLabel la = new JLabel(text);
                la.setOpaque(true);

                if (i == 1) {
                    if (j == 2) text = "월요일";
                    else if (j == 3) text = "화요일";
                    else if (j == 4) text = "수요일";
                    else if (j == 5) text = "목요일";
                    else if (j == 6) text = "금요일";
                } else if (i >= 2 && i <= 19) {
                    if (j == 0) text = timeCode[i - 2];
                    else if (j == 1) text = timeInfo[i - 2];
                } else if (i == 20 || i == 21) {
                    if (j == 0 || j == 1) {
                        text = "이후";
                    }
                }
                if (times.length != 0 && j >= 2 && j <=6 && i >=2 && i<= 19) {
                    if (!subjectTable[j-2].get(i-2).equals("none")) {
                        text = subjectTable[j-2].get(i-2);
                        la.setBackground(Color.PINK);
                    }
                }
                la.setText(text);
                la.setBorder(new LineBorder(Color.black));
                wrapper.add(la);
            }
        }
        cnt +=1;
        wrapper.setSize(600, 700);
        wrapper.setLocation(760, 130);
        contentPane.add(wrapper);
        System.out.println("paintTable");
    }

    // 시간표 테이블 paint 함수
    public void paintBorder(int times[]) {
        System.out.println("paintBorder --- " + Arrays.toString(times) );
        ArrayList<Integer> days = new ArrayList<Integer>();
        ArrayList<Integer> time = new ArrayList<Integer>();

        if (times.length != 0) {
            for(int i=0; i<times.length; i++) {
                days.add(times[i] / 100);
                time.add(times[i] % 100);
            }
        }

        System.out.println("days:" + days + " time:" + time);

        wrapper = new JPanel();
        wrapper.setLayout(new GridLayout(22, 7));
        wrapper.setBorder(new LineBorder(Color.black));
        for (int i=0; i<22; i++) {
            for (int j = 0; j < 7; j++) {
                String text = "";
                JLabel la = new JLabel(text);
                la.setOpaque(true);

                if (i == 1) {
                    if (j == 2) text = "월요일";
                    else if (j == 3) text = "화요일";
                    else if (j == 4) text = "수요일";
                    else if (j == 5) text = "목요일";
                    else if (j == 6) text = "금요일";
                } else if (i >= 2 && i <= 19) {
                    if (j == 0) text = timeCode[i - 2];
                    else if (j == 1) text = timeInfo[i - 2];
                } else if (i == 20 || i == 21) {
                    if (j == 0 || j == 1) {
                        text = "이후";
                    }
                }
                if (times.length != 0 && j >= 2 && j <=6 && i >=2 && i<= 19) {
                    if (!subjectTable[j-2].get(i-2).equals("none")) {
                        text = subjectTable[j-2].get(i-2);
                        la.setBackground(Color.PINK);
                    }
                }
//                la.setText(text);
                la.setBorder(new LineBorder(Color.black));
                wrapper.add(la);
            }
        }
        cnt +=1;
        wrapper.setSize(600, 700);
        wrapper.setLocation(760, 130);
        contentPane.add(wrapper);
        System.out.println("paintBorder");
    }


    // 이미지 저장 버튼 생성
    public void createImageSaveBtn() {
        contentPane = getContentPane();
        imageSaveBtn = new JButton("이미지 저장");
        imageSaveBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("이미지 저장 클릭");
                BufferedImage image = new BufferedImage(wrapper.getWidth(),
                        wrapper.getHeight(), BufferedImage.TYPE_INT_RGB);
                wrapper.paint(image.getGraphics());
                BufferedImage img = ScreenShot.getScreenShot(wrapper);
                String fileName = "/Users/jaewoogwak/IdeaProjects/Hanpyo/src/test.png";
                try {
                    ImageIO.write(img, "png", new File(fileName));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        imageSaveBtn.setSize(100, 20);
        imageSaveBtn.setLocation(760, 100);
        contentPane.add(imageSaveBtn);
    }

    // 과목 추천 버튼 생성
    public void createRecommandBtn() {
        contentPane = getContentPane();
        recommandBtn = new JButton("과목 추천");
        recommandBtn.setSize(100,20);
        recommandBtn.setLocation(870, 100);
        contentPane.add(recommandBtn);
    }



    public void maximizeWindow() {
        // 최대화 상태로 GUI를 open
        Dimension DimMax = Toolkit.getDefaultToolkit().getScreenSize();
        setMaximumSize(DimMax);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }



    class MyActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JButton b = (JButton) e.getSource();
            if (b.getText().equals("검색")) {
                System.out.println("검색");
                 sortBySearchField();
            } else if (b.getText().equals("과목 담기")) {
                putSubject();
            } else if(b.getText().equals("과목 빼기")) {
                deleteSubjectInQueue();
            } else if(b.getText().equals("과목 초기화")) {
                System.out.println("과목 초기화");
                clearSubject();
            }
        }
    }








    public static void main(String args[]) {
        new App();

    }
}
