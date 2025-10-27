package item9;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Example {

    // item 9 : try-finally 대신 try-with-resources를 사용하라

//    자바 라이브러리에는 close 메서드를 호출해 직접 닫아줘야 하는 자원이 많다.
//    ex) InputStream, OutputStream, java.sql.Connection
//    자원 닫기는 클라이언트가 놓치기 쉬워서 예측할 수 없는 성능 문제로 이어지기도 한다.
//    이런 자원 중 상당수가 안전망으로 finalizer를 활용하고 있지만 그리 믿을만 하지 못하다(아이템 8).
//    전통적으로 자원이 제대로 닫힘을 보장하는 수단으로 try-finally가 쓰였다.

//    try-finally - 더 이상 자원을 회수하는 최선의 방책이 아니다!
//    public static String firstLineOfFile(String path) throw IOException {
//        BufferedReader br = new BufferedReader(new FileReader(path));
//        try {
//            return br.readLine();
//        } finally {
//            br.close();
//        }
//    }

//    나쁘지 않지만 자원을 하나 더 사용한다면 어떨까?

//    자원이 둘 이상이면 try-finally 방식은 너무 지저분하다!
//    static void copy(String src, String dst) throws IOException {
//        InputStream in = new FileInputStream(src);
//        try {
//            OutputStream out = new FileOutputStream(dst);
//            try {
//                byte[] buf = new byte[BUFFER_SIZE];
//                int n;
//                while ((n = in.read(buf)) >= 0)
//                    out.write(buf, 0, n);
//            } finally {
//                out.close();
//            }
//        } finally {
//            in.close();
//        }
//    }

//    위 2개의 코드에은 미묘한 결점이 있다.
//    예외는 try 블록과 finally 블록 모두에서 발생할 수 있는데 예외 발생 지점에 따라 이전 예외를 집어삼키는 경우가 생긴다.
//    이런 경우 스택 추적 내역에 이전 예외에 관한 정보는 남지 않게 되어 디버깅이 매우 어려워 진다.


//    ## try-with-resources
//    위 문제들을 자바 7에서 등장한 try-with-resources가 해결해 준다.
//    이 구조를 사용하려면 해당 자원이 AutoCloseable 인터페이스를 구현해야한다.
//    단순히 void를 반환하는 close 메서드 하나만 정의한 인터페이스다.
//    많은 라이브러리의 클래스와 인터페이스에서 AutoCloseable을 구현하거나 확장해 두었고
//    여러분들도 닫아야 하는 자원을 뜻하는 클래스를 작성한다면 AutoCloseable을 반드시 구현해야 한다.

//    try-with-resources - 자원을 회수하는 최선책!
//    public static String firstLineOfFile(String path) throw IOException {
//        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
//            return br.readLine();
//        } catch (Exception e) {
//            return defaultVal;
//        }
//    }

//    복수의 자원을 처리하는 try-with-resources - 짧고 매혹적이다!
//    static void copy(String src, String dst) throws IOException {
//        try (InputStream in = new FileInputStream(src);
//             OutputStream out = new FileOutputStream(dst)) {
//            byte[] buf = new byte[BUFFER_SIZE];
//            int n;
//            while ((n = in.read(buf)) >= 0)
//                out.write(buf, 0, n);
//        }
//    }

//    try-with-resources 버전이 훨씬 짦고 읽기 수월할 뿐만 아니라 문제를 진단하기도 훨씬 좋다.
//    예외가 양족 모두에서 발생해도 이전 예외를 집어 삼키지 않고 숨겨졌다는 꼬리표를 달고 출력이 된다.
//    따라서 디버깅에 매우 큰 이점이 있다.
//    또한 자바7에서 Throwable에 추가된 getSuppressed 메서드를 쓰면 프로그램 코드에서 가져올 수도 있다.


//    ## try-with-resources의 catch
//    try-with-resources에서도 catch 절을 쓸 수 있다.
//    catch 절 덕분에 try 문을 더 중첩하지 않고도 다수의 예외를 처리할 수 있다.

//    try-with-resources를 catch 절과 함께 쓰는 모습
//    static String firstLineOfFile(String path, String defaultVal) {
//        try (BufferedReader br = new BufferedReader
//                (new FileReader(path))) {
//            return br.readLine();
//        } catch (IOException e) {
//            throw defaultVal;
//        }
//    }


//    ## 핵심 정리
//    꼭 회수해야 하는 자원을 다룰 때는 try-finally 말고 try-with-resources를 사용하자.
//    예외는 없다. 코드는 더 짧고 분명해지고, 만들어지는 예외 정보도 훨씬 유용하다.
//    try-finally로 작성하면 실용적이지 못할 만큼 코드가 지저분해지는 경우라도,
//    try-with-resources 로는 정확하고 쉽게 자원을 회수할 수 있다.
}


































