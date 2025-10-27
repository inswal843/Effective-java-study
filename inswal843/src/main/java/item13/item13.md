## Item 13: clone 재정의는 주의해서 진행하라

Cloneable 인터페이스는 복제해도 되는 클래스임을 명시하는 용도의 **믹스인 인터페이스(mixin interface, item 20)**이지만, 의도한 목적을 제대로 이루지 못했다. clone 메서드는 여러 설계상의 문제점을 가지고 있으므로, 새로운 클래스를 만들 때 Cloneable 구현 여부는 신중하게 정하는 것이 좋다.

---

### 1. clone과 Cloneable의 문제점

1. Cloneable은 clone 메서드를 포함하고 있지 않다. Object의 protected 메서드인 clone의 동작 방식을 결정할 뿐이다. Cloneable을 구현하지 않은 클래스가 clone을 호출하면 CloneNotSupportedException이 발생한다.
2. clone 메서드의 일반 규약은 매우 허술하다. x.clone() != x 여야 하고, x.clone().getClass() == x.getClass() 여야 하지만, 이것이 항상 지켜지지는 않는다.
3. clone은 생성자를 호출하지 않고 객체를 생성한다. 이 과정에서 필드 복사가 누락되거나 잘못될 경우, 원본 객체와 복제된 객체 모두가 비정상적으로 동작할 수 있다.
4. final 필드는 생성자에서만 값을 할당할 수 있는데, clone은 생성자를 사용하지 않으므로 final 필드를 정상적으로 복제할 수 없다. 이 때문에 final 필드는 복제된 객체에서 수정될 수 없어 문제가 생긴다.
5. Object의 clone 메서드는 CloneNotSupportedException을 던지도록 선언되어 있다. Cloneable을 구현한 클래스에서는 이 예외가 발생할 일이 없으므로 불필요한 try-catch 블록이나 throws 선언이 강제된다.

---

### 2. clone 메서드 구현 방법

기존 클래스를 확장하거나 특정 요구사항 때문에 clone을 구현해야 한다면 다음 지침을 따라 구현해야 한다.

### 단계 1: Cloneable 구현 및 접근 제한자 변경

```java
// Cloneable을 구현하고, 접근 제한자를 public으로 변경한다.
// 반환 타입은 해당 클래스 타입으로 공변 반환 타이핑(covariant return typing)을 사용한다.
public class PhoneNumber implements Cloneable {

    @Override
    public PhoneNumber clone() {
        try {
            // super.clone()을 호출하여 필드를 복사한다.
            return (PhoneNumber) super.clone();
        } catch (CloneNotSupportedException e) {
            // 발생할 수 없는 예외. 이 클래스는 Cloneable을 구현했기 때문.
            throw new AssertionError();
        }
    }
}
```

### 단계 2: 가변 상태(Mutable State)의 깊은 복사(Deep Copy)

만약 클래스가 String이나 기본 타입 필드만 가지고 있다면 super.clone() 호출만으로 충분하다. 하지만 **배열이나 다른 가변 객체를 필드로 가지고 있다면** super.clone()이 반환한 객체의 해당 필드들을 다시 **깊은 복사**해주어야 한다. 그렇지 않으면 원본과 복제본이 같은 객체를 참조하는 **얕은 복사(shallow copy)**가 되어 한쪽을 수정하면 다른 쪽도 변경되는 심각한 문제가 발생한다.

**예시: 스택 클래스의 clone**

```java
public class Stack implements Cloneable {
    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    // ... push, pop 메서드

    @Override
    public Stack clone() {
        try {
            Stack result = (Stack) super.clone();
            // elements 필드를 깊은 복사한다.
            result.elements = this.elements.clone();
            return result;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
```

elements 배열에 대해 .clone()을 재귀적으로 호출하여 배열의 내용을 복사했다. 만약 elements가 가변 객체를 담는 배열이라면 각 원소에 대해서도 clone을 호출해야 한다.

---

### 3. 더 나은 방법 → 복사 생성자와 복사 팩터리

clone의 모든 문제점을 피하면서 객체 복사를 안전하고 명확하게 할 수 있는 방법이 있다.

1. **복사 생성자 (Copy Constructor):** 자기 자신 타입의 인스턴스를 인자로 받는 생성자다.
    
    ```java
    public Yum(Yum yum) { ... };
    ```
    
2. **복사 팩터리 (Copy Factory):** 복사 생성자와 비슷한 역할을 하는 정적 팩터리 메서드다.
    
    ```java
    public static Yum newInstance(Yum yum) { ... };
    ```
    

**장점:**

- 생성자를 사용하는 정상적인 객체 생성 메커니즘을 따른다.
- final 필드 문제에서 자유롭다.
- 불필요한 예외 처리가 필요 없다.
- 형변환이 필요 없다.
- 인터페이스 타입을 인자로 받을 수 있어 유연하다.

---

### 요약

- 복제가 필요하다면 **복사 생성자**나 **복사 팩터리**를 제공하는 것이 훨씬 안전하고 좋은 방법이다.
- 기존에 Cloneable을 구현한 클래스를 확장할 때만 어쩔 수 없이 clone을 재정의해야 할 수 있다. 이때는 super.clone()을 호출한 뒤 모든 가변 필드에 대해 **깊은 복사**를 수행해야 한다.
