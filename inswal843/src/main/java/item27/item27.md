## Item 27: 비검사 경고를 제거하라

제네릭을 사용하면 수많은 컴파일러 경고를 보게 된다. **비검사 경고**는 타입 안전성이 깨질 수 있는 위험을 알려주는 경고이므로 **모든 비검사 경고는 가능한 한 모두 제거해야 한다.** 모든 경고를 제거하면 코드는 타입 안전성이 보장되어 런타임에 `ClassCastException`이 발생할 일이 없다는 확신을 가질 수 있다.

---

### 1. 비검사 경고가 발생하는 이유

비검사 경고는 컴파일러가 코드의 타입 안전성을 100% 보장할 수 없을 때 발생한다.

**경고 발생**

```java
Set<Lark> exaltation = new HashSet(); // 비검사 경고 발생
```

위 코드는 `HashSet`에 타입 매개변수를 명시하지 않았기 때문에 비검사 경고가 발생한다. 자바 7부터 도입된 **다이아몬드 연산자(<>**)를 사용하면 간단히 해결할 수 있다.

**수정된 코드**

```java
Set<Lark> exaltation = new HashSet<>(); // 경고 없음
```

---

### 2. 경고를 제거할 수 없을 때 `@SuppressWarnings`

경고를 제거할 수는 없지만 해당 코드가 **타입 안전하다고 확신**할 수 있는 경우가 있다. 이럴 때만 **`@SuppressWarnings("unchecked")`** 애너테이션을 사용해 경고를 숨기자.

**`@SuppressWarnings` 사용시 주의점**

1. **최대한 좁은 범위에 적용하자.**
    - 클래스 전체나 메서드 전체에 적용하지 말고 경고가 발생하는 **선언이나 변수 초기화 구문**에 직접 적용해야 한다. 이렇게 하면 다른 잠재적 위험을 가진 경고를 실수로 숨기는 것을 방지할 수 있다.
    
    ```java
    // 나쁜 예: 메서드 전체에 적용
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        // ...
    }
    
    // 좋은 예: 경고가 발생하는 변수 선언에만 적용
    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            @SuppressWarnings("unchecked") 
            T[] result = (T[]) Arrays.copyOf(elements, size, a.getClass());
            return result;
        }
        // ...
    }
    ```
    
2. **경고를 무시해도 안전한 이유를 주석으로 남겨라.**
    - `@SuppressWarnings`를 사용할 때는 반드시 **왜 이 코드가 타입 안전한지** 설명하는 주석을 함께 달아야 한다. 이는 다른 개발자가 코드를 이해하고, 나중에 실수로 코드를 수정하여 타입 안전성을 깨뜨리는 것을 방지하는 데 큰 도움이 된다.

```java
public <T> T[] toArray(T[] a) {
    if (a.length < size) {
        // 생성한 배열과 매개변수로 받은 배열의 타입이 모두 T[]로 같으므로
        // 올바른 형변환이다.
        @SuppressWarnings("unchecked")
        T[] result = (T[]) Arrays.copyOf(elements, size, a.getClass());
        return result;
    }
    System.arraycopy(elements, 0, a, 0, size);
    if (a.length > size) {
        a[size] = null;
    }
    return a;
}
```

---

### 요약

- **모든 비검사 경고를 제거하자.** 코드의 타입 안전성을 보장하는데 매우 중요하다.
- 경고를 제거할 수 없고 해당 코드가 타입 안전함을 **확신**할 수 있을 때만 **`@SuppressWarnings("unchecked")`**를 사용하자.
- `@SuppressWarnings`를 사용할 때는 **가장 좁은 범위에 적용**하고 **안전한 이유를 반드시 주석으로** 남겨야 한다.
