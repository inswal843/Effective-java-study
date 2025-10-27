package item3;

public class Elvis {
//    public static final 방식의 싱글턴

//    private 생성자는 public static final필드인 Elvis.INSTANCE를 초기화할 때 딱 한 번만 호출된다.
//    public이나 protected 생성자가 없으므로 Elvis클래스가 초기화될 때 만들어진 인스턴스가
//    전체 시스템에서 하나뿐임이 보장된다.
//    예외
//    리플렉션 API(아이템 65)인 AccessibleObject.setAccessible을 사용해 private 생성자를 호출할 수 있다.
//    따라서 생성자를 수정해 두 번째 객체가 생성되려 할 때 예외를 던지게 하면 된다.
//    장점
//    1. 해당 클래스가 싱글턴임이 API에 명백히 드러난다. public static 필드가 final이니 절대로 다른 객체를 참조할 수 없다.
//    2. 간결함

//    public static final Elvis INSTANCE = new Elvis();
//    private Elvis() {
//        if(INSTANCE != null) {
//            throw new IllegalStateException("Instance already instantiated");
//        }
//    }
//    public void leaveTheBuilding() { }


//    정적 팩터리 방식의 싱글턴

//    Elvis.getInstance는 항상 같은 객체의 참조를 반환하므로 제2의 Elvis 인스턴스는 만들어지지 않는다. (리플랙션 처리 필요)
//    장점
//    1. (마음이 바뀌면) API를 바꾸지 않고도 싱글턴이 아니게 변경할 수 있다.
//       유일한 인스턴스를 반환하던 팩터리 메서드가 (예컨대) 호출하는 스레드 별로 다른 인스턴스를 넘겨주게 할 수 있다.
//    2. 원한다면 정적 팩터리를 제네릭 싱글턴 팩터리로 만들 수 있다.(아이템 30)
//    3. 정적 팩터리의 메서드 참조를 공급자(supplier)로 사용할 수 있다. Elvis::getInstance를 Supplier<Elvis>로 사용하는 식(아이템 43, 44)
//    private static final Elvis INSTANCE = new Elvis();
//    private Elvis() { }
//    public static Elvis getInstance() { return INSTANCE; }
//
//    public void leaveTheBuilding() { }
}

// 직렬화, 역직렬화 공부 필요...

// 원소가 하나인 열거 타입 선언으로 싱글턴을 만드는 방법
// public 필드 방식과 비슷하지만, 더 간결하고, 추가 노력 없이 직렬화할 수 있고,
// 심지어 아주 복잡한 직렬화 상황이나 리플렉션 공격에서도 제2의 인스턴스가 생기는 일을 완벽히 막아준다.
// 따라서 대부분의 상황에서는 원소가 하나뿐인 열거 타입이 싱글턴을 만드는 가장 좋은 방법이다.

//public enum Elvis {
//    INSTANCE;
//
//    public void leaveTheBuilding() { }
//}