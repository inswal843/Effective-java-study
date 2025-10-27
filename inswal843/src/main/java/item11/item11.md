## Item 11: equals를 재정의하려거든 hashCode도 재정의하라

equals를 재정의한 클래스에서 hashCode를 재정의하지 않으면, HashMap, HashSet, Hashtable과 같은 해시 기반 컬렉션이 올바르게 동작하지 않는 심각한 문제가 발생한다.

---

### 1. hashCode 재정의 규약

1. equals 비교에 사용되는 정보가 변경되지 않았다면, 객체의 hashCode 메서드는 몇 번을 호출해도 항상 **동일한 정수**를 반환해야 한다. (단, 애플리케이션을 다시 실행할 때의 값은 달라져도 상관없다.)
2. equals(Object) 메서드가 두 객체를 같다고 판단했다면, 두 객체의 hashCode는 **반드시 같은 값**을 가져야 한다.
3. equals(Object) 메서드가 두 객체를 다르다고 판단했더라도, 두 객체의 hashCode가 서로 **다른 값을 반환할 필요는 없다**. 하지만 다른 객체에 대해서는 다른 hashCode 값을 반환해야 해시 테이블의 성능이 좋아진다.

---

### 2. 규약을 어겼을 때의 문제점

가장 중요한 것은 **2번째 규약**이다. 만약 equals로는 같지만 hashCode 값이 다른 두 객체가 있다면, 해시 기반 컬렉션에서 이 두 객체를 **완전히 다른 객체로 취급**하게 된다.

```java
Map<PhoneNumber, String> map = new HashMap<>();
PhoneNumber pn1 = new PhoneNumber(707, 867, 5309);
map.put(pn1, "제니");

// equals는 true이지만 hashCode가 다른 객체로 get()을 시도하면...
PhoneNumber pn2 = new PhoneNumber(707, 867, 5309);
map.get(pn2); // null을 반환한다!
```

pn1과 pn2는 equals상 동등하지만, hashCode가 재정의되지 않아(물리적 주소 기반의 Object 기본 hashCode 사용) 다른 해시 버킷에 저장된다. 따라서 pn2로 값을 찾으려 해도 pn1을 찾을 수 없는 문제가 발생한다.

---

### 3. 좋은 hashCode를 작성하는 요령

좋은 해시 함수는 서로 다른 인스턴스에 대해 되도록 다른 hashCode를 반환한다. 이상적인 해시 함수는 주어진 인스턴스들을 32비트 정수 범위에 균일하게 분배해야 한다.

다음은 좋은 hashCode를 작성하는 간단한 요령이다.

1. int 변수 result를 선언하고, 객체의 첫 번째 핵심 필드에 대한 해시 코드 c로 초기화한다.
2. 나머지 핵심 필드 f에 대해 다음 작업을 순차적으로 수행한다.
a. 해당 필드의 해시 코드 c를 계산한다.
b. result = 31 * result + c; 와 같이 result를 갱신한다.
3. result를 반환한다.

**왜 31인가?**
31은 홀수이면서 소수(prime number)이기 때문이다. 만약 이 숫자가 짝수이고 오버플로가 발생한다면, 곱셈 연산 시 정보 손실이 발생할 수 있다.  “**31 * i”** 는 “**(i << 5) - i”** 로 시프트 연산을 통해 빠르게 처리될 수 있다는 장점도 있다.

---

### 4. 필드 타입별 해시 코드 계산 방법

- **기본 타입 (primitive type):** `[타입].hashCode(필드)`를 사용한다. (예: `Integer.hashCode(myInt)`)
- **참조 타입:** 해당 필드의 hashCode()를 재귀적으로 호출한다. 만약 필드 값이 null이면 0을 사용한다.
- **배열:** `Arrays.hashCode(배열)`를 사용한다. 각 원소의 해시 코드를 기반으로 계산해준다.

### 예시

```java
public class PhoneNumber {
    private final short areaCode, prefix, lineNum;

    @Override
    public int hashCode() {
        int result = Short.hashCode(areaCode);
        result = 31 * result + Short.hashCode(prefix);
        result = 31 * result + Short.hashCode(lineNum);
        return result;
    }
}
```

java.util.Objects 클래스의 hash() 메서드를 사용하면 위 과정을 더 간결하게 처리할 수 있다.

```java
@Override
public int hashCode() {
    return Objects.hash(areaCode, prefix, lineNum);
}
```

하지만 Objects.hash() 는 입력 인자를 담기 위한 배열 생성과 오토박싱(auto-boxing) 등으로 인해 성능이 직접 구현하는 것보다 느릴 수 있다.

---

### 요약

- **성능을 높인답시고 해시코드를 계산할 때 해심 필드를 생략해서는 안된다.**
- **해hashcode가 반환하는 값의 생성 규칙을 API 사용자에게 자세히 공표하지 말자. 그래야 클라이언트가 이 값에 의지하지 않게 되고, 추후에 계산 방식을 바꿀 수도 있다.**
