## Item 30: 이왕이면 제네릭 메서드로 만들라

클래스를 제네릭으로 만드는 것처럼 메서드도 제네릭으로 만들 수 있다. **제네릭 메서드**는 매개변수화 타입을 받는 정적 유틸리티 메서드에서 많이 사용되고 코드 중복을 줄이고 타입 안전성을 높여준다.

---

### 1. 제네릭 메서드가 필요한 이유

메서드를 제네릭으로 만들면, 여러 다른 타입에 대해 **단 하나의 메서드**로 안전하게 로직을 처리할 수 있다. 제네릭을 사용하지 않으면 특정 타입 전용 메서드를 여러 개 만들거나 `Object`를 사용한 뒤 위험한 형변환을 해야 한다.

**로 타입 사용 - 수용 불가! (item 26)**

```java
public static Set union(Set s1, Set s2) {
    Set result = new HashSet(s1);
    result.addAll(s2);
    return result; // 비검사 경고 발생!
}
```

이 메서드는 컴파일은 되지만 경고가 두 개 발생한다.

**제네릭 메서드로 개선**

```java
public static <E> Set<E> union(Set<E> s1, Set<E> s2) {
    Set<E> result = new HashSet<>(s1);
    result.addAll(s2);
    return result;
}
```

- **`<E>`:** 이 메서드가 제네릭 메서드임을 알리는 **타입 매개변수 목록**이다. 이 목록은 메서드의 **제한자**와 **반환 타입** 사이에 선언한다.
- 이 메서드는 세 개의 타입(`s1`, `s2`, 반환값)이 모두 같은 타입 `E`임을 보장한다. 따라서 컴파일 시점에 타입 안전성을 확보할 수 있고 클라이언트 코드에서 형변환이 필요 없다.

---

### 2. 제네릭 싱글턴 팩터리 패턴

때때로 **불변 객체**이면서 여러 타입으로 활용될 수 있는 객체가 필요할 때가 있다. 이럴 때 제네릭을 사용하면 요청된 타입 매개변수에 맞게 매번 같은 인스턴스를 반환하는 **제네릭 싱글턴 팩터리**를 만들 수 있다.

함수 객체(item 42)나 `Collections.emptySet` 같은 컬렉션용으로 사용한다.

**항등 함수(identity function) 싱글턴**

```java
private static UnaryOperator<Object> IDENTITY_FN = (t) -> t;

@SuppressWarnings("unchecked")
public static <T> UnaryOperator<T> identityFunction() {
    return (UnaryOperator<T>) IDENTITY_FN;
}

// 사용 예시
String[] strings = { "삼베", "대마", "나이론" };
UnaryOperator<String> sameString = identityFunction();
for (String s : strings) {
    System.out.println(sameString.apply(s));
}

Number[] numbers = { 1, 2.0, 3L };
UnaryOperator<Number> sameNumber = identityFunction();
for (Number n : numbers) {
    System.out.println(sameNumber.apply(n));
}
```

`identityFunction()`은 어떤 타입으로 요청을 받아도 항상 안전하게 형변환된 동일한 `IDENTITY_FN` 인스턴스를 반환한다.

---

### 3. 재귀적 타입 한정

타입 매개변수가 자기 자신을 참조하는 드문 제네릭 패턴도 있다. 이는 주로 `Comparable` 인터페이스(item 14)와 함께 사용된다.

`<E extends Comparable<E>>`

이 표현의 의미는 **"모든 타입 `E`는 자기 자신과 비교할 수 있다"** 이다. 이 한정을 사용하면 컬렉션에서 정렬, 최솟값, 최댓값을 찾는 메서드 등을 타입 안전하게 구현할 수 있다.

**컬렉션의 최댓값 찾기**

```java
public static <E extends Comparable<E>> E max(Collection<E> c) {
    if (c.isEmpty()) {
        throw new IllegalArgumentException("컬렉션이 비어 있습니다.");
    }

    E result = null;
    for (E e : c) {
        if (result == null || e.compareTo(result) > 0) {
            result = Objects.requireNonNull(e);
        }
    }
    return result;
}
```

---

### 요약

- 로 타입을 사용하는 메서드는 타입 안전하지 않으니 **제네릭 메서드로 만들어라.**
- 제네릭 메서드는 코드 중복을 줄이고, 클라이언트의 형변환을 없애주며, 타입 안전성을 제공한다.
- 여러 타입에 대해 동일한 인스턴스를 안전하게 반환하고 싶다면 **제네릭 싱글턴 팩터리** 패턴을 고려하라.
