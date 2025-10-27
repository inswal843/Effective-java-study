## Item 26: 로 타입은 사용하지 말라

즉 타입 매개변수가 없는 제네릭 타입(**로 타입)**은 하위 호환성을 위해 존재하는 것이므로 새로 작성하는 코드에서는 절대 사용하면 안 된다. 로 타입을 사용하면 제네릭이 제공하는 **타입 안전성**과 **표현력**을 모두 잃게 된다.

---

### 1. 로 타입(Raw Type)이란?

- **로 타입:** 타입 매개변수를 전혀 사용하지 않고 쓴 제네릭 타입. 예: `List`, `Set`
- **매개변수화 타입 (Parameterized Type):** 타입 매개변수를 명시한 제네릭 타입. 예: `List<String>`, `Set<Integer>`

`List`와 `List<Object>`는 다르다. `List`는 제네릭 타입 시스템을 완전히 무시하겠다는 것이지만 `List<Object>`는 **모든 타입의 객체를 담을 수 있다**는 것을 컴파일러에게 명확히 알려주는 것이다.

---

### 2. 타입 안전성 상실

로 타입을 사용하면 컴파일러의 타입 체크를 우회하게 되어 런타임에 `ClassCastException`이 발생할 수 있는 위험한 코드를 만들게 된다.

**예: 로 타입 `List` 사용**

```java
private final Collection stamps = new ArrayList(); // 로 타입 사용

stamps.add(new Coin(...)); // Coin 객체를 넣는다.

for (Iterator i = stamps.iterator(); i.hasNext(); ) {
    Stamp stamp = (Stamp) i.next(); // 런타임에 ClassCastException 발생
    stamp.cancel();
}
```

위 코드에서 `stamps`는 로 타입 `Collection`이므로, 컴파일러는 `Coin` 객체를 넣는 것을 막지 못한다. 결국 `Stamp`로 형변환하는 지점에서 런타임 오류가 발생한다.

**예: 매개변수화 타입 `List<Stamp>` 사용**

```java
private final Collection<Stamp> stamps = new ArrayList<>();

stamps.add(new Coin(...)); // 컴파일 오류! Stamp가 아닌 타입을 넣을 수 없다.
```

제네릭을 사용하면 의도한 객체와 다른 타입의 객체를 넣으려는 시도를 **컴파일 시점에** 잡아낼 수 있다. 이것으로 프로그램의 안정성이 크게 향상된다.

---

### 3. 비한정적 와일드카드

제네릭 타입을 쓰고 싶지만 실제 타입 매개변수가 무엇인지 신경 쓰고 싶지 않다면 **물음표(?)**를 사용하자. 이것이 어떤 타입으로라도 담을 수 있는 가장 범용적인 방법이다.

**`Set<Object>` vs `Set<?>` vs `Set`**

- `Set<Object>`: **모든 타입의 객체**를 추가할 수 있다.
- `Set<?>` (**비한정적 와일드카드** 타입): **어떤 특정 타입**의 원소만 담는 `Set`이라는 의미다. 어떤 타입인지는 알 수 없으므로 **`null` 외에는 어떤 원소도 넣을 수 없다.**
- `Set` (로 타입): 제네릭 타입 시스템을 완전히 무시한다. **절대 사용하면 안 된다.**

**예: 비한정적 와일드카드**

```java
// 비한정적 와일드카드 사용 (안전하고 유연함)
static int numElementsInCommon(Set<?> s1, Set<?> s2) {...}
```

---

### 4. 예외적으로 로 타입을 써야 하는 경우

로 타입은 딱 두 가지 경우 예외적으로 사용된다.

1. **클래스 리터럴(Class Literal):** `List.class`는 허용되지만 `List<String>.class`는 허용되지 않는다.
2. **`instanceof` 연산자:** `if (o instanceof Set)`은 가능하지만, `if (o instanceof Set<String>)`은 불가능하다. 제네릭 타입 정보는 런타임에 소거되기 때문이다.

---

### 요약

- **로 타입(`List`, `Set`)은 사용하지 말라.** 타입 안전성을 잃고 런타임 오류의 원인이 된다.
- 제네릭 타입을 사용할 때는 항상 **매개변수화 타입(`List<String>`)**을 사용하자.
- 어떤 타입인지 신경 쓰고 싶지 않다면 로 타입 대신 **비한정적 와일드카드(`Set<?>`)**를 사용하자. 타입 안전성을 보장하는 가장 범용적인 방법이다.
