## Item 22: 인터페이스는 타입을 정의하는 용도로만 사용하라

**인터페이스**는 자신을 구현한 클래스의 인스턴스가 **무엇을 할 수 있는지**를 클라이언트에 알려주는 **타입 역할**을 하기 위해 존재한다. 인터페이스는 오직 이 용도로만 사용해야 한다.

---

### 1. 잘못된 사용법: 상수 인터페이스 안티패턴

상수(static final 필드)를 외부에 공개할 목적으로 인터페이스를 사용하는 경우가 있는데 이는 **상수 인터페이스(Constant Interface)** 안티패턴이며 절대 사용해서는 안 된다.

**나쁜 예: 상수 인터페이스**

```java
public interface PhysicalConstants {
    static final double AVOGADROS_NUMBER = 6.022_140_857e23;
    static final double BOLTZMANN_CONSTANT = 1.380_648_52e-23;
    static final double ELECTRON_MASS = 9.109_383_56e-31;
}
```

**문제점:**

- **클래스의 내부 구현을 API로 노출하는 행위다.** 심한 경우 클라이언트의 코드가 내부 구현에 해당하는 이 상수들에 종속되게 한다.
- 클래스가 이 인터페이스를 구현하면 이 상수들이 클래스의 **API 네임스페이스를 오염**시킨다. `final`이 아닌 클래스가 이를 구현하면 모든 하위 클래스의 네임스페이스까지 오염된다.

---

### 2. 올바른 상수 공개 방법

1. **특정 클래스나 인터페이스와 강하게 연관된 상수일 경우:**
    - 해당 클래스나 인터페이스 자체에 `public static final` 필드로 추가한다.
    - 예: `Integer.MAX_VALUE`, `Double.MIN_VALUE`
2. **열거 타입(enum)으로 나타내기 적합한 상수일 경우:**
    - **열거 타입**을 사용한다(item 34).
3. **인스턴스화할 수 없는 유틸리티 클래스(item 4)에 담아 공개:**
    - 여러 클래스에서 공통으로 사용하는 상수의 경우 유틸리티 클래스에 모아두는 것이 좋다.
    - 생성자를 `private`으로 만들어 인스턴스화를 막는다.

**좋은 예: 상수 유틸리티 클래스**

```java
public final class PhysicalConstants {
    private PhysicalConstants() { } // 인스턴스화 방지

    public static final double AVOGADROS_NUMBER = 6.022_140_857e23;
    public static final double BOLTZMANN_CONSTANT = 1.380_648_52e-23;
    public static final double ELECTRON_MASS = 9.109_383_56e-31;
}
```

이제 `PhysicalConstants.AVOGADROS_NUMBER`처럼 클래스 이름을 통해 명확하게 상수를 사용할 수 있다. `static import`를 사용하면 클래스 이름을 생략하고 사용할 수도 있다.

---

### 요약

- 인터페이스의 유일한 목적은 **타입을 정의**하는 것이다. 이 이외의 용도로 사용하는 것은 피해야 한다.
- 상수를 공개할 목적으로 인터페이스를 사용하는 **상수 인터페이스 안티패턴**은 피해야 한다.
