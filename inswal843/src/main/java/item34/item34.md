## Item 34: int 상수 대신 열거 타입을 사용하라

- **열거 타입(enum type)**은 정수 상수(int enum pattern)의 모든 단점을 보완하고 타입 안전성, 가독성, 확장성을 제공하는 훨씬 좋은 대안이다. **상수를 열거해야 한다면 항상 열거 타입을 사용해야 한다.**

---

### 1. 정수 상수(int enum pattern)의 문제점

**정수 상수**

```java
public static final int APPLE_FUJI = 0;
public static final int APPLE_PIPPIN = 1;
public static final int APPLE_GRANNY_SMITH = 2;

public static final int ORANGE_NAVEL = 0;
public static final int ORANGE_TEMPLE = 1;
public static final int ORANGE_BLOOD = 2;
```

**단점:**

- **타입 안전성 없음:** `ORANGE_NAVEL`을 받아야 할 메서드에 `APPLE_FUJI`를 넘겨도 컴파일러는 아무런 오류를 내지 않는다. (`==` 연산자로 비교해도 마찬가지)
- **네임스페이스 오염:** `APPLE_`과 같은 접두어를 붙여 이름 충돌을 피해야만 한다.
- **깨지기 쉬움:** 상수의 값이 바뀌면 클라이언트 코드도 다시 컴파일해야 한다.
- **디버깅 어려움:** 상수를 출력하면 의미 없는 숫자(0, 1, 2...)만 보여서 디버깅이 어렵다.
- **확장성 부재:** 새로운 상수를 추가하기는 쉽지만 관련된 모든 상수를 순회하거나 상수의 총개수를 얻는 방법이 마땅치 않다.

---

### 2. 열거 타입(Enum)의 장점

자바의 `enum`은 이러한 문제를 전부 해결한다. `enum`은 그 자체로 **완전한 클래스**이고 단순한 상수의 집합 이상이다.

**열거 타입**

```java
public enum Apple { FUJI, PIPPIN, GRANNY_SMITH }
public enum Orange { NAVEL, TEMPLE, BLOOD }
```

**장점 목록:**

- **완벽한 타입 안전성:** 컴파일러가 타입을 체크해주어 `Apple` 타입의 매개변수에 `Orange` 타입의 값을 넘길 수 없다. 실수를 원천 차단한다.
- **독자적인 네임스페이스:** `Apple`과 `Orange`라는 각자의 이름 공간이 있어 이름 충돌이 없다.
- **가독성과 디버깅 용이성:** `toString()` 메서드가 자동으로 오버라이드되어 상수 이름을 그대로 출력해준다. (예: "FUJI")
- **메서드와 필드 추가 가능:** `enum`은 단순한 상수가 아니라 클래스이므로 임의의 메서드나 필드를 추가하여 **상수별로 다른 동작**을 구현할 수 있다.
- **고성능 구현:** `enum`은 내부적으로 `public static final` 필드로 구현되어 있어 성능 저하가 거의 없다.

---

### 3. 열거 타입을 활용해 데이터와 메서드 추가하기

`enum`은 단순한 이름의 목록 이외에 각 상수와 연관된 데이터와 메서드를 가질 수 있다.

**행성의 데이터와 동작을 포함하는 `enum`**

```java
public enum Planet {
    MERCURY(3.302e+23, 2.439e6),
    VENUS(4.869e+24, 6.052e6),
    EARTH(5.975e+24, 6.378e6),
    MARS(6.419e+23, 3.393e6);
    ...

    private final double mass;           // 질량 (kg)
    private final double radius;         // 반지름 (m)
    private final double surfaceGravity; // 표면중력 (m / s^2)

		// 중력상수(m^3 / kg s^2)
    private static final double G = 6.67300E-11;

    // 생성자
    Planet(double mass, double radius) {
        this.mass = mass;
        this.radius = radius;
        this.surfaceGravity = G * mass / (radius * radius);
    }

    public double mass() { return mass; }
    public double radius() { return radius; }
    public double surfaceGravity() { return surfaceGravity; }

    public double surfaceWeight(double mass) {
        return mass * surfaceGravity; // F = ma
    }
}

// 사용시 코드
double earthWeight = 185;
double mass = earthWeight / Planet.EARTH.surfaceGravity();
for (Planet p : Planet.values()) {
    System.out.printf("%s에서의 무게는 %f이다.%n", p, p.surfaceWeight(mass));
}
```

이와같이 `enum`을 사용하면 데이터와 관련 동작을 하나의 타입 안에 캡슐화하여 매우 깔끔하고 효율적인 코드를 작성할 수 있다.

---

### 요약

- 정수 상수는 타입 안전성과 확장성 등 수많은 문제가 있으므로 **절대 사용하지 말라.**
- 필요한 상수의 집합이 정해져 있다면 **항상 `enum`을 사용하라.**
- `enum`은 단순한 상수 이상으로 상수와 연관된 데이터나 메서드를 추가하여 다양한 기능을 가진 타입을 만들 수 있다.
