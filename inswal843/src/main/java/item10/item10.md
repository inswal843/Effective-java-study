## Item 10: equals는 일반 규약을 지켜 재정의하라

equals 메서드는 재정의하기 쉬워 보이지만, 잘못 구현하면 끔찍한 결과를 초래한다.

### 1. equals를 재정의하지 않아도 되는 경우

다음과 같은 상황에서는 **Object**의 기본 **equals** 메서드를 그대로 사용하는 것이 좋다.

- **각 인스턴스가 본질적으로 고유할 때:** 값을 표현하는 것이 아니라 동작하는 개체를 표현하는 클래스(예: Thread)의 경우
- **논리적 동치성을 검사할 일이 없을 때:** 클래스 사용 방식상 두 인스턴스가 논리적으로 같은지 확인할 필요가 없는 경우
- **상위 클래스에서 재정의한 equals가 하위 클래스에도 완벽히 들어맞을 때:** AbstractSet을 상속받는 HashSet처럼, 상위 클래스의 equals 구현을 그대로 사용해도 되는 경우
- **클래스가 private이거나 package-private이고 equals를 호출할 일이 없을 때:** 실수로라도 equals가 호출되는 것을 막고 싶은 경우
    
    ```java
    @Override
    public boolean equals(Object o) {
        throw new AssertionError(); // 호출 금지!
    }
    ```
    

### 2. equals를 재정의해야 하는 경우

**객체 식별성(같은 객체인가?)**이 아닌 **논리적 동치성(같은 값을 가지는가?)**을 확인해야 할 때, 그리고 상위 클래스의 equals가 논리적 동치성을 비교하도록 구현되어 있지 않을 때 재정의해야 한다.

주로 **Integer, String과 같은 값 클래스** 들이 이에 해당한다. 두 객체가 같은 값을 나타내는지 여부가 중요하기 때문이다.

### 3. equals 재정의 시 반드시 지켜야 할 일반 규약

1. **반사성 (Reflexivity):** null 이 아닌 모든 참조 값 x 에 대해, x.equals(x) 는 true 다.
2. **대칭성 (Symmetry):** null 이 아닌 모든 참조 값 x, y 에 대해, y.equals(x) 가 true 면 x.equals(y)도 true 다.
3. **추이성 (Transitivity):** null 이 아닌 모든 참조 값 x, y, z 에 대해, x.equals(y) 가 true 이고 y.equals(z)도 true 면 x.equals(z) 도 true 다.
4. **일관성 (Consistency):** null 이 아닌 모든 참조 값 x, y 에 대해, 정보가 변경되지 않았다면, x.equals(y) 호출 결과는 항상 같아야 한다.
5. **null-아님:** null이 아닌 모든 참조 값 x에 대해, x.equals(null)은 항상 false 다.

### 4. 올바른 equals 구현 단계

1. **== 연산자를 사용하여 입력이 자기 자신의 참조인지 확인한다.**
    - 성능 최적화를 위해, 만약 같다면 바로 true를 반환
2. **instanceof 연산자로 입력이 올바른 타입인지 확인한다.**
    - 타입이 다르다면 false 를 반환한다.
3. **입력을 올바른 타입으로 형변환한다.**
    - `instanceof` 검사를 통과했으므로 이 단계는 100% 성공한다.
4. **핵심 필드들이 모두 일치하는지 하나씩 검사한다.**
    - 모든 필드가 일치하면 true를, 하나라도 다르면 false를 반환한다.

```java
public class PhoneNumber {
    private final short areaCode, prefix, lineNum;

    public PhoneNumber(int areaCode, int prefix, int lineNum) {
        // ... (생성자)
    }

    @Override
    public boolean equals(Object o) {
        // 1. 자기 자신 참조 확인
        if (o == this) {
            return true;
        }

        // 2. 타입 확인
        if (!(o instanceof PhoneNumber)) {
            return false;
        }

        // 3. 형변환
        PhoneNumber pn = (PhoneNumber) o;

        // 4. 핵심 필드 비교
        return pn.lineNum == lineNum
                && pn.prefix == prefix
                && pn.areaCode == areaCode;
    }
}
```

### 요약

- **equals를 재정의할 땐 반드시 hashCode도 재정의하자(Item 11).**
- **너무 복잡하게 해결하려 들지 말자**. 필드의 동치성만 검사해도 equals 규약을 어렵지 않게 지킬 수 있다.
- **Object 외의 타입을 매개변수로 받는 equals 메서드는 선언하지 말자.** @Override 어노테이션을 일관되게 사용하면 이런 실수를 예방할 수 있다.
