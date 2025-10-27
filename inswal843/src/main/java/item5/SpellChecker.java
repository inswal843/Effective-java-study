package item5;

public class SpellChecker {

//    많은 클래스가 하나 이상의 자원에 의존한다.
//    맞춤법 검사기는 사전에 의존하는데, 이런 클래스를 정적 유틸리티 클래스(아이템 4)로 구현한 모습을 볼 수 있다.

//    정적 유틸리티를 잘못 사용한 예 - 유연하지 않고 테스트하기 어렵다.
//    private static final Lexicon dictionary = ...;
//    private SpellChecker() {} // 객체 생성 방지
//    public static boolean isValid(String word) { ... }
//    public static List<String> suggestions(String typo) { ... }

//    비슷하게 싱글턴(아이템 3)으로 구현하는 경우도 흔하다.
//    싱글턴을 잘못 사용한 예 - 유연하지 않고 테스트하기 어렵다.
//    private final Lexicon dictionary;
//    private SpellChecker(...) {}
//    private static SpellChecker INSTANCE = new SpellChecker(...);
//    public boolean isValid(String word) {}
//    public List<String> suggestions(String typo) {}

//    두 방식 모두 사전을 단 하나만 사용한다고 가정한다는 점에서 그리 훌륭해 보이지 않는다.
//    그렇다고 사전 필드에 final 한정자를 제거하고 다른 사전으로 교체하는 메서드를 추가하면
//    어색하고 오류는 내기 쉬우며 멀티스레드 환경에서는 쓸 수 없다.

//    즉, 사용하는 자원에 따라 동작이 달라지는 클래스에는 정적 유틸리티 클래스나 싱글턴 방식이 적합하지 않다.
//    따라서 인스턴스를 생성할 떄 생성자에 필요한 자원을 넘겨주는 방식인 의존 객체 주입 패턴을 사용하자.

//    의존 객체 주입은 유연성과 테스트 용이성을 높여준다.
//    private final Lexicon dictionary;

//    private SpellChecker(Lexicon dictionary) {
//        this.dictionary = Objects.requireNonNull(dictionary);
//    }

//    public boolean isValid(String word) {}
//    public List<String> suggestions(String typo) {}

//    예에서는 사전 자원 하나만 사용하지만, 자원이 몇 개든 의존 관계가 어떻든 상관없이 잘 작동한다.
//    또, 불변(아이템 17)을 보장하여 (같은 자원을 사용하려는) 여러 클라이언트가 의존 객체들을 안심하고 공유할 수 있다.
//    생성자에 자원 팩터리를 넘겨주는 방식으로 변형해서 사용하면 효과적이다.

//    의존 객체 주입이 유연성과 테스트 용이성을 개선해주긴 하지만, 의존성이 많은 큰 프로젝트에서는
//    코드를 어지럽게 만들기도 한다.
//    따라서 대거, 주스, 스프링 같은 의존 객체 주입 프레임워크를 사용해 해결이 가능하다.

//    핵심 정리
//    클래스가 내부적으로 하나 이상의 자원에 의존하고, 그 자원이 클래스 동작에 영향을 준다면
//    싱글턴과 정적 유틸리티 클래스는 사용하지 않는 것이 좋다.
//    이 자원들을 클래스가 직접 만들게 해서도 안 된다.
//    대신 필요한 자원을 (혹은 그 자원을 만들어주는 팩터리를) 생성자에 (혹은 정적 팩터리나 빌더에) 넘겨주자.
//    의존 객체 주입이라 하는 이 기법은 클래스의 유연성, 재사용성, 테스트 용이성을 기막히게 개선해준다.
    
}
