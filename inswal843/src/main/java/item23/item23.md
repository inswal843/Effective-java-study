## Item 23: 태그 달린 클래스보다는 클래스 계층구조를 활용하라

- **태그 달린 클래스**는 두 가지 이상의 의미를 표현할 수 있으며, 그중 현재 표현하는 의미를 **태그(tag)** 필드로 구분하는 클래스다. 이 방식은 장황하고, 오류를 내기 쉬우며, 비효율적이므로 사용하지 말아야 한다.

---

### 1. 태그 달린 클래스의 단점

**나쁜 예: 도형을 표현하는 태그 달린 클래스**

```java
class Figure {
    enum Shape { RECTANGLE, CIRCLE }

    // 태그 필드 - 현재 모양을 나타낸다.
    final Shape shape;

    // RECTANGLE일 때만 사용하는 필드
    double length;
    double width;

    // CIRCLE일 때만 사용하는 필드
    double radius;

    // 원용 생성자
    Figure(double radius) {
        shape = Shape.CIRCLE;
        this.radius = radius;
    }

    // 사각형용 생성자
    Figure(double length, double width) {
        shape = Shape.RECTANGLE;
        this.length = length;
        this.width = width;
    }

    double area() {
        switch (shape) {
            case RECTANGLE:
                return length * width;
            case CIRCLE:
                return Math.PI * (radius * radius);
            default:
                throw new AssertionError(shape);
        }
    }
}
```

**단점:**

- **가독성 저하:** 하나의 클래스에 여러 타입의 데이터 필드와 로직이 뒤섞여 있어 코드가 장황하고 읽기 어렵다. `switch` 문이 곳곳에 등장한다.
- **메모리 낭비:** 인스턴스가 자신이 표현하는 모양에 쓰이지 않는 필드들까지 모두 가지게 되어 메모리를 낭비한다.
- **오류 발생 가능성:** `final` 필드를 선언하기 어렵고 생성자에서 태그에 맞는 필드를 초기화하는 것을 컴파일러가 도울 수 없다. 다른 모양을 위한 필드를 실수로 사용할 수도 있다.
- **확장성 저하:** 새로운 모양을 추가하려면 클래스 자체를 수정해야 한다. 모든 `switch` 문에 새로운 `case`를 추가해야 하고 하나라도 빠뜨리면 런타임 오류가 발생한다.

---

### 2. 클래스 계층구조 (서브타이핑)

태그 달린 클래스가 있다면, 이를 **클래스 계층구조로 리팩터링**하는 것이 올바른 방법이다.

1. 계층구조의 루트가 될 **추상 클래스**를 정의하고, 공통 메서드(`area` 등)를 추상 메서드로 선언한다.
2. 태그에 따라 나뉘는 요소들을 각각의 **구체 서브클래스**로 만든다.
3. 각 서브클래스는 자신에게 필요한 데이터 필드만 가진다.

**좋은 예: 클래스 계층구조**

```java
// 계층구조의 루트 (추상 클래스)
abstract class Figure {
    abstract double area();
}

// 원을 위한 구체 서브클래스
class Circle extends Figure {
    final double radius;

    Circle(double radius) { this.radius = radius; }

    @Override 
    double area() { return Math.PI * (radius * radius); }
}

// 사각형을 위한 구체 서브클래스
class Rectangle extends Figure {
    final double length;
    final double width;

    Rectangle(double length, double width) {
        this.length = length;
        this.width = width;
    }

    @Override 
    double area() { return length * width; }
}
```

**장점:**

- **명확하고 간결함:** 각 클래스는 자신이 맡은 모양에만 집중한다. 불필요한 필드나 `switch` 문이 없다.
- **메모리 효율성:** 각 인스턴스는 자신에게 필요한 필드만 가진다.
- **확장성:** 새로운 도형을 추가하고 싶은 경우 새로운 `Figure`의 서브클래스를 만들기만 하면 된다. 기존 코드는 전혀 수정할 필요가 없다.
- **계층적 관계:** 클래스 간의 관계가 명확하게 표현된다.
- **타입 안전성:** 각 도형은 자신만의 타입을 가지므로, 컴파일러가 타입을 검사해 실수를 방지해준다.

---

### 요약

- 여러 의미를 표현하는 **태그 달린 클래스는 절대 사용하지 말자.**
- 클래스가 태그 필드를 가지고 있고 그 값에 따라 동작이 달라진다면 계층 구조로 대체하는 방법을 생각해 보자.
- 태그 달린 클래스는 **클래스 계층구조로 전환해** 독립된 서브클래스로 만들어야 한다. 코드를 더 명확하고, 안전하며, 유연하게 해준다.
