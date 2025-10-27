## Item 25: 톱레벨 클래스는 한 파일에 하나만 담으라

하나의 소스 파일에 여러 개의 톱레벨 클래스를 선언하는 것은 가능하지만 심각한 문제를 일으킬 수 있으므로 **그렇게 해서는 안 된다.** 소스 파일 하나에는 반드시 **단 하나의 톱레벨 클래스**만 담아야 한다.

---

### 1. 한 파일에 여러 톱레벨 클래스가 있을 때의 문제

컴파일러는 소스 파일을 순서대로 읽는다. 만약 한 파일에 여러 톱레벨 클래스가 정의되어 있는 경우 컴파일하는 순서에 따라 어느 소스 파일을 사용할지 매번 달라지기 문제가 발생할 수 있다.

**문제: `Utensil.java` 파일 하나에 두 개의 클래스 선언**

```java
// Main.java 파일
public class Main {
    public static void main(String[] args) {
        System.out.println(Utensil.NAME + Dessert.NAME);
    }
}
```

```java
// Utensil.java 파일에 다른 톱레벨 클래스를 선언
class Utensil {
    static final String NAME = "pan";
}

class Dessert {
    static final String NAME = "cake";
}
```

위 코드는 `javac Main.java` 명령으로 컴파일하면 문제없이 동작한다.

하지만 `Utensil.java`와 `Dessert.java` 파일을 먼저 만들고 그 안에 각각 `Utensil`과 `Dessert` 클래스를 정의한 경우를 가정해보자.

```java
// Dessert.java 파일
class Utensil {
    static final String NAME = "pot";
}
class Dessert {
    static final String NAME = "pie";
}
```

이제 `javac Main.java Dessert.java` 명령을 실행하면 컴파일러는 클래스를 중복 정의했다고 알려줄 것이다. 하지만 `javac Main.java` 나 `javac Main.java Utensil.java`명령으로 컴파일 하면 `Dessert.java` 파일을 작성하기 전처럼 출력한다. 이처럼 **어느 소스 파일을 먼저 제공하느냐에 따라 동작이 달라지는** 심각한 문제가 발생한다.

---

### 2. 클래스를 분리하라

해결책은 아주 간단하다. **톱레벨 클래스를 각각의 소스 파일로 분리**하면 된다.

- `Main.java`
- `Utensil.java`
- `Dessert.java`

이렇게 하면 컴파일 순서에 상관없이 항상 일관된 결과를 보장받을 수 있다.

### 정적 멤버 클래스

굳이 여러 클래스를 하나의 파일에 두고 싶다면 **정적 멤버 클래스(item 24)**로 만드는 것을 고려할 수 있다. 다른 클래스에 딸린 부차적인 클래스라면 정적 멤버 클래스로 만드는 것이 일반적으로 나을 것이다.

**정적 멤버 클래스**

```java
public class Test {
    public static void main(String[] args) {
        System.out.println(Utensil.NAME + Dessert.NAME);
    }

    private static class Utensil {
        static final String NAME = "pan";
    }

    private static class Dessert {
        static final String NAME = "cake";
    }
}
```

---

### 요약

- **하나의 소스 파일에는 반드시 하나의 톱레벨 클래스(혹은 톱레벨 인터페이스)만 선언하라.**
- 이 규칙을 따르면 컴파일러가 한 클래스에 대한 정의를 여러 개 만들어 내는 일이 사라진다. 소스 파일을 어떤 순서로 컴파일 하든 프로그램의 동작이 달라지는 일이 발생하지 않는다.
