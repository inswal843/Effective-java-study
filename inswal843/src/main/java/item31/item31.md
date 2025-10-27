## Item 31: 한정적 와일드카드를 사용해 API 유연성을 높이라

**매개변수화 타입**은 **불공변**이다. 즉, `List<Type1>`은 `List<Type2>`의 하위 타입도 상위 타입도 아니다. 때문에 불공변 방식은 유연성이 떨어질 수 있는데 **한정적 와일드카드**를 사용하면 이 문제를 해결하고 훨씬 유연하고 강력한 API를 만들 수 있다.

---

### 1. 와일드카드가 필요한 이유: 불공변의 한계

`Stack<Number>`에 `Integer` 타입의 원소를 모두 넣고 싶다고 해보자. `Integer`는 `Number`의 하위 타입이지만 아래 코드는 컴파일되지 않는다.

```java
public class Stack<E> {
    public void pushAll(Iterable<E> src) {
        for (E e : src) {
            push(e);
        }
    }
}

Stack<Number> numberStack = new Stack<>();
Iterable<Integer> integers = List.of(1, 2, 3);
numberStack.pushAll(integers); // 컴파일 오류! Iterable<Integer>는 Iterable<Number>의 하위 타입이 아니다.
```

`pushAll` 메서드는 `Iterable<E>`를 기대하는데, `numberStack`의 `E`는 `Number`이므로 `Iterable<Number>`만 받을 수 있다. 타입이 정확히 일치하지 않으면 제네릭 메서드는 동작하지 않는다.

이 문제를 해결하기 위해 와일드카드를 사용한다.

---

### 2. PECS: Producer-Extends, Consumer-Super

와일드카드를 언제 사용해야 하는지 기억하는 간단한 공식이 바로 **PECS**다.

### Producer-Extends (`? extends E`)

- 매개변수화 타입 `T`가 **생산자(Producer)** 역할을 한다면 `? extends T`를 사용하라.
- `T`나 `T`의 하위 타입 인스턴스를 **생산**하여 컬렉션에 **제공(전달)**하는 역할일 때 사용한다.
- 위 예시의 `pushAll` 메서드는 `src`로부터 `E` 타입의 원소를 꺼내서(생산해서) 스택에 넣는다. 따라서 `src`는 생산자다.

**생산자 매개변수에 와일드카드 타입 적용**

```java
public void pushAll(Iterable<? extends E> src) {
    for (E e : src) {
        push(e);
    }
}
```

이제 `Iterable<Integer>`를 `Iterable<? extends Number>` 타입의 변수에 할당할 수 있어서 `numberStack.pushAll(integers)`가 성공적으로 컴파일된다.

### Consumer-Super (`? super E`)

- 매개변수화 타입 `T`가 **소비자(Consumer)** 역할을 한다면 `? super T`를 사용하라.
- `T`나 `T`의 상위 타입 인스턴스를 **소비**하여 컬렉션으로부터 값을 **가져오는** 역할일 때 사용한다.
- 스택에서 원소를 꺼내 주어진 컬렉션에 모두 담는 `popAll` 메서드를 생각해보자. 이 메서드의 목적지 컬렉션 `dst`는 스택으로부터 `E` 타입의 원소를 전달받아 소비한다. 따라서 `dst`는 소비자다.

**소비자 매개변수에 와일드카드 타입 적용**

```java
public void popAll(Collection<? super E> dst) {
    while (!isEmpty()) {
        dst.add(pop());
    }
}

Stack<Number> numberStack = new Stack<>();
Collection<Object> objects = new ArrayList<>();
numberStack.popAll(objects); // 성공! Object는 Number의 상위 타입이다.
```

`Collection<Object>`는 `Collection<? super Number>`를 만족해 `Number` 타입의 원소를 안전하게 담을 수 있다.

---

### PECS 공식

| **생산자** | `? extends E` | `E`의 하위 타입을 생산한다. | **P**roducer-**E**xtends |
| --- | --- | --- | --- |
| **소비자** | `? super E` | `E`의 상위 타입을 소비한다. | **C**onsumer-**S**uper |
- **메서드 반환 타입에는 와일드카드를 사용하면 안 된다.** 클라이언트 코드에서도 와일드카드를 써야 하는 불편함이 생긴다.
- 만약 매개변수가 생산자와 소비자 역할을 **모두** 한다면 와일드카드를 사용해서는 안 된다. 타입이 정확히 일치해야 한다. `Function<T, E>`에서 `T`는 소비자, `E`는 생산자이므로 `Function<? super T, ? extends E>`로 표현할 수 있다.

---

### 요약

- 조금 복잡하더라도 메서드 시그니처에 **한정적 와일드카드**를 적용하면 API가 훨씬 유연해지고 클라이언트 코드를 편하게 만들어준다.
- **PECS** 공식을 기억하자 "생산자는 extends, 소비자는 super".
- 모든 `Comparable`과 `Comparator`는 소비자들이므로 `Comparable<? super T>`, `Comparator<? super T>`를 사용하는 것이 좋다.
