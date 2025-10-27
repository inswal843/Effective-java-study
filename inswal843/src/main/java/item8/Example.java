package item8;

public class Example {

    // # item 8 : finalizer와 cleaner 사용을 피하라

//    자바는 두 가지 객체 소멸자 finalizer와 cleaner를 제공한다.
//    finalizer는 오동작, 낮은 성능, 이식성 문제로 deprecated 되었고
//    cleaner는 finalizer보다는 덜 위험하지만 여전히 예측할 수 없고, 느리고 불필요해서 쓰지 않는게 좋다.


//    ## 즉시 수행된다는 보장이 없다.
//    객체에 접근 할수 없게 된 후 finalizer와 cleaner가 언제 실행될지 알 수 없다.
//    즉, finalizer와 cleaner로는 제때 실행되어야 하는 작업은 절대 할 수 없다.
//    finalizer와 cleaner의 수행 속도는 가비지 컬렉터에 달렸으며 가비지 컬렉터 구현마다 다르다.

//    ## finalizer를 단 클래스는 회수가 지연될 수 있다.
//    finalizer 스레드는 다른 애플리케이션 스레드보다 우선 순위가 낮아 실행될 기회를 제대로 얻지 못할 수 있다.
//    cleaner는 자신을 수행할 스레드를 제어할 수 있다는 면에서 조금 낫긴 하다.

//    ## 수행 여부조차 보장하지 않는다.
//    접근할 수 없는 일부 객체에 대한 종료 작업을 전혀 수행하지 못한 채 프로그램이 중단될 수 있다.
//    따라서 상태를 영구적으로 수정하는 작업에서는 절대 finalizer나 cleaner에 의존해서는 안 된다.

//    ## finalizer는 동작 중 발생한 예외가 무시된다.
//    finalizer는 동작 중 발생할 예외를 무시하며 처리할 작업이 남았더라도 그 순간 종료된다.
//    잡지 못한 예외로 해당 객체가 마무리가 덜 된 상태로 남을 수 있고 이 객체를 접근하면 어떤 문제가 생길지 모른다.
//    cleaner를 사용하는 라이브러리는 자신의 스레드를 통제하기 때문에 이러한 문제는 발생하지 않는다.

//    ## 심각한 성능 문제가 있다.
//    finalizer와 cleaner는 가비지 컬렉터의 효율을 떨어뜨리기 때문에 심각한 성능 문제가 있다.

//    ## finalizer는 심각한 보안 문제가 있다.
//    생성자나 직렬화 과정에서 예외가 발생하면 이 객체에서 악의적인 하위 클래스의 finalizer가 수행될 수 있다.
//    또 이 finalizer는 정적 필드에 자신의 참조를 할당해서 가비지 컬렉터가 수집하지 못하게 막을 수 도 있다.


//    # 정상적으로 자원을 반납하는 법
//    파일이나 스레드 등 종료해야 할 자원을 담고 있는 객체의 클래스에서 AutoCloseable을 구현해주고,
//    클라이언트에서 인스턴스를 다 쓰고 나면 close 메서드를 호출하면 된다.
//    일반적으로 예외가 발생해도 제대로 종료되도록 try-with-resource 를 사용해야 한다.(아이템 9)

//    각 인스턴스는 자신이 닫혔는지를 추적하는 것이 좋다.
//    추적을 위해 close 메서드에서 이 객체는 더 이상 유효하지 않음을 필드에 기록하고,
//    다른 메서드는 이 필드를 검사해서 객체가 닫힌 후에 불렸다면 IllegalStateException을 던지도록 구현한다.


//    # 그럼 finalizer와 cleaner는 어디에 쓰일까?

//    ## 1. 자원의 소유자가 close 메서드를 호출하지 않았을 때의 **안전망**역할
//    즉시 호출이 된다는 보장은 없지만, 클라이언트가 하지 않은 자원 회수를
//    늦게라도 해주는 것이 아예 안 하는 것보다는 낫다.
//    FileInputStream, FileOutputStream, ThreadPoolExecutor 등의 자바 라이브러이의 일부 클래스는
//    안전망 역할의 finalizer를 제공한다.

//    ## 2. 네이티브 피어와 연결된 객체
//    네이티브 피어는 일반 자바 객체가 네이티브 메서드를 통해 기능을 위임한 네이티브 객체를 말한다.
//    자바 객체가 아니니 가비지 컬렉터의 대상이 되지 못한다.
//    따라서 finalizer와 cleaner를 이용해 처리할 수 있다.
//    하지만 성능 저하를 감당할 수 없거나 즉시 회수해야 한다면 close 메서드를 사용해야 한다.

//    ## cleaner를 안전망으로 활용하는 AutoCloseable 클래스
//    public class Room implements AutoCloseable{
//        private static final Cleaner cleaner = Cleaner.create();
//
//        // 청소가 필요한 자원, **절대 Room을 참조해서는 안된다!**
//        private static class State implements Runnable {
//            int numJunkPiles; // 방 안의 쓰레기 수
//
//            public State(final int numJunkPiles) {
//                this.numJunkPiles = numJunkPiles;
//            }
//
//            // close 메서드나 cleaner가 호출한다.
//            @Override
//            public void run() {
//                System.out.println("방청소");
//                numJunkPiles = 0;
//            }
//        }
//
//    // 방의 상태. cleanable과 공유한다.
//    private final State state;
//
//    // cleanable 객체. 수거 대상이 되면 방을 청소한다.
//    private final Cleaner.Cleanable cleanable;
//
//    public Room(int numJunkFiles) {
//        state = new State(numJunkFiles);
//        cleanable = cleaner.register(this, state);
//    }
//
//        @Override
//        public void close() throws Exception {
//            cleanable.clean();
//        }
//    }
//}

//    Room의 cleaner는 단순히 안전망으로만 쓰였다.
//    모든 Room 생성을 try-with-resources 블록으로 감쌌다면 자동 청소는 필요가 없다.
//    잘 짜여진 클라이언트 코드의 예를 보자.
//    public class Adult {
//        public static void main(String[] args) {
//            try (Room myRoom = new Room(7)) {
//                System.out.println("안녕~");
//            }
//        }
//    }
}































