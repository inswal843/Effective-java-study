## Item 29: 이왕이면 제네릭 타입으로 만들라

클라이언트에서 직접 형변환해야 하는 클래스는 사용하기 불편하고 런타임 오류가 발생할 위험이 있다. 따라서 새로운 클래스를 만들 때는 **가능하면 제네릭으로 만들어서** 사용자가 형변환 없이 안전하게 사용할 수 있도록 하는 것이 좋다.

---

### 1. 타입 안전성

제네릭이 아닌 일반 클래스는 `Object`를 사용하여 모든 타입을 다룬다. 이 경우 값을 꺼내 쓸 때마다 클라이언트가 직접 형변환을 해야 하고 이 과정에서 `ClassCastException`이 발생할 위험이 있다.

**`Object` 기반 스택**

```java
public class Stack {
    private Object[] elements;
    private int size = 0;
    // ...

    public Object pop() {
        // ...
        Object result = elements[--size];
        elements[size] = null;
        return result;
    }
}

// 클라이언트 코드
Stack stack = new Stack();
stack.push("hello");
String s = (String) stack.pop(); // 직접 형변환 필요! 런타임 오류 가능성
```

제네릭으로 바꾸면 컴파일러가 타입 안전성을 보장해주어 클라이언트 코드가 훨씬 깔끔하고 안전해진다.

---

### 2. 제네릭 클래스로 구현

기존 클래스를 제네릭으로 바꾸는 것은 간단하다. 클래스 선언에 타입 매개변수(`E`)를 추가하고 코드에서 `Object`를 해당 타입 매개변수로 바꾸면 된다.

**제네릭 스택 `Stack<E>`**

```java
public class Stack<E> {
    private E[] elements; // 문제 발생 지점
    private int size = 0;
    // ...
    
    public Stack() {
        elements = new E[DEFAULT_INITIAL_CAPACITY]; // 컴파일 오류!
    }

    public void push(E e) {
        // ...
        elements[size++] = e;
    }
    
    public E pop() {
        // ...
        E result = elements[--size];
        elements[size] = null;
        return result;
    }
}
```

하지만 위 코드에서 `new E[DEFAULT_INITIAL_CAPACITY]` 부분은 **제네릭 배열 생성 금지** 규칙(Item 28) 때문에 컴파일 오류가 발생한다.

---

### 3. 제네릭 배열 생성 문제 해결법

이 문제를 해결하는 방법은 두 가지가 있다.

### 해결책 1: `Object` 배열 생성 후 형변환

1. `Object` 배열을 생성한다.
2. 이 배열을 제네릭 배열 타입(`E[]`)으로 형변환한다.
3. 이 형변환은 타입 안전성을 깨뜨릴 수 있다는 **경고**를 발생시켜 안전함을 확인한 뒤 `@SuppressWarnings("unchecked")`로 경고를 숨긴다.

**해결책 1**

```java
public class Stack<E> {
    private E[] elements;
    private int size = 0;

    public Stack() {
        // 비검사 형변환이지만 private 필드이고, 외부로 노출되지 않고,
        // push 메서드에서 E 타입만 받으므로 타입 안전성이 보장된다.
        @SuppressWarnings("unchecked")
        elements = (E[]) new Object[DEFAULT_INITIAL_CAPACITY];
    }
    // ...
}
```

이 방법은 가독성이 좋아 현업에서도 자주 쓰이지만 배열의 런타임 타입이 `E[]`가 아닌 `Object[]`라 힙 오염(item 32)을 일으킨다.

### 해결책 2: 필드의 타입을 `Object[]`로 사용

1. 필드의 타입을 `E[]`가 아닌 `Object[]`로 사용한다.
2. 배열에서 원소를 꺼내는 `pop` 메서드에서만 `E` 타입으로 형변환한다.

 **해결책 2**

```java
public class Stack<E> {
    private Object[] elements;
    private int size = 0;

    public Stack() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }
    // ... push 메서드 ...

    public E pop() {
        // ...
        // 비검사 형변환. push에서 E 타입만 넣었으므로 안전하다.
        @SuppressWarnings("unchecked")
        E result = (E) elements[--size];
        elements[size] = null;
        return result;
    }
}
```

이 방법은 형변환을 코드 한 곳에만 몰아넣는다는 장점이 있지만 배열 전체가 아닌 개별 원소를 읽을 때마다 형변환 해줘야해서 성능이 약간 저하될 수 있다.

---

### 요약

- 클라이언트의 타입 안전성과 편의성을 위해 **새로운 클래스는 가능하면 제네릭으로 설계하라.**
- 제네릭 타입의 배열을 만들어야 할 때는 `new E[]`가 불가능하므로, **`Object` 배열을 생성한 뒤 `(E[])`로 형변환하고 `@SuppressWarnings`를 사용**하는 것이 선호되는 해결책이다.
