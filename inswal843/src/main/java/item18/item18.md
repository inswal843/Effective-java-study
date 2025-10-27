## Item 18: 상속보다는 컴포지션을 사용하라

**상속**은 코드를 재사용하는 강력한 수단이지만, 캡슐화를 깨뜨릴 수 있고 상위 클래스의 변경에 취약한 하위 클래스를 만들 수 있어 잘못 사용하면 심각한 문제를 일으킨다. **두 클래스가 명백한 is-a 관계일 때만 상속을 사용하고, 그 외에는 컴포지션(Composition)을 우선적으로 고려해야 한다.**

---

### 1. 상속의 문제점

상속은 상위 클래스의 구현 세부사항까지 하위 클래스에 전파된다. 이로 인해 여러 문제가 발생한다.

- **캡슐화 위반:** 하위 클래스는 상위 클래스의 내부 구현에 의존하게 된다. 상위 클래스의 내부 구현이 다음 릴리스에서 변경되면, 하위 클래스는 예기치 않게 오작동할 수 있다.
- 상위 클래스에 새로운 메서드가 추가되거나 기존 메서드의 동작 방식이 바뀌면, 하위 클래스의 동작이 깨질 수 있다. 예를 들어, `HashSet`을 상속받아 `add`와 `addAll` 메서드를 오버라이드하여 원소 추가 횟수를 세는 클래스를 만들었다.
    
    ```java
    // 상속을 잘못 사용한 예
    public class InstrumentedHashSet<E> extends HashSet<E> {
        private int addCount = 0;
    
        @Override public boolean add(E e) {
            addCount++;
            return super.add(e);
        }
    
        @Override public boolean addAll(Collection<? extends E> c) {
            addCount += c.size();
            return super.addAll(c); // HashSet의 addAll은 내부적으로 add를 호출한다.
        }
    }
    ```
    
    위 코드에서 `addAll`을 호출하면, `addAll` 내부에서 `add` 메서드를 호출하기 때문에 원소 개수가 **두 번씩 더해지는** 버그가 발생한다. 이는 `HashSet`의 내부 구현(`addAll`이 `add`를 사용한다는 사실)을 알지 못하면 예상하기 매우 어려운 문제다.
    
- **API 제약:** 상위 클래스의 결함까지 그대로 승계한다. 또한, 상위 클래스가 제공하지 않는 기능을 추가하기는 쉽지만, 상위 클래스의 메서드 일부를 제거하거나 접근을 막는 것은 거의 불가능하다.

---

### 2. 해결방법: 컴포지션(Composition)과 전달(Forwarding)

컴포지션은 기존 클래스를 확장하는 대신 새로운 클래스의 **`private` 필드**로 기존 클래스의 인스턴스를 참조하는 방식이다. 새로운 클래스는 기존 클래스의 내부 구현에 의존하지 않고 공개된 API에만 영향을 받는다.

새로운 클래스의 메서드는 기존 클래스의 메서드를 호출하여 그 결과를 반환한다. 이를 **전달(Forwarding)**이라고 하며 새로운 클래스를 **전달 클래스(Forwarding Class)** 또는 **래퍼 클래스(Wrapper Class)**라고 부른다.

**컴포지션을 사용 예시**

```java
// 래퍼 클래스 - 컴포지션과 전달을 사용
public class InstrumentedSet<E> extends ForwardingSet<E> {
    private int addCount = 0;

    public InstrumentedSet(Set<E> s) {
        super(s); // Set 인스턴스를 필드로 참조
    }

    @Override public boolean add(E e) {
        addCount++;
        return super.add(e); // 내부 Set의 add 메서드에 전달
    }

    @Override public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return super.addAll(c); // 내부 Set의 addAll 메서드에 전달
    }

    public int getAddCount() {
        return addCount;
    }
}

// 재사용 가능한 전달 클래스
public class ForwardingSet<E> implements Set<E> {
    private final Set<E> s;
    public ForwardingSet(Set<E> s) { this.s = s; }

    public void clear() { s.clear(); }
    // Set의 모든 메서드를 내부 Set s에 그대로 전달한다
    public boolean add(E e) { return s.add(e); }
    public boolean addAll(Collection<? extends E> c) { return s.addAll(c); }
    // ...
}
```

이 방식은 `HashSet`의 내부 구현이 어떻게 바뀌든 전혀 영향을 받지 않는다. `InstrumentedSet`은 `Set` 인터페이스의 규약만 따르기 때문에 견고하고 유연하다.

---

### 3. 그럼 상속은 언제 사용해야 할까?

상속을 사용해도 좋은 경우는, 확장하려는 클래스의 API에 아무런 결함이 없으며, 두 클래스의 관계가 명백한 **is-a 관계**일 때 뿐이다.

- **"B는 A인가?"** 라는 질문에 **"그렇다"**고 확신할 수 있을 때만 상속을 사용해야 한다.
- 만약 "B는 A의 **기능을 사용**한다" 또는 "B는 A로 **구현**되었다"에 가깝다면 컴포지션을 사용해야 한다. (has-a 관계)
- 상속은 하위 클래스가 상위 클래스의 **모든 규약을 완벽히 따를 수 있을 때**만 적절하다.

---

### 요약

- 상속은 강력하지만 캡슐화를 해치고 취약한 설계를 만들 수 있으므로 **남용하지 말아야 한다.**
- 코드를 재사용하고 싶을 땐, 상속 대신 **컴포지션과 전달**을 우선적으로 고려하자. 래퍼 클래스는 내부 객체를 감싸고 있어 견고하고 강력하다.
- 상속은 두 클래스가 명확한 **is-a 관계**이고 상위 클래스의 API에 결함이 없을 때만 신중하게 사용하라.
