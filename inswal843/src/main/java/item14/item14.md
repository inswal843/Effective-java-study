## Item 14: Comparable을 구현할지 고려하라

Comparable 인터페이스를 구현한다는 것은 그 클래스의 인스턴스들에게 **자연적인 순서(natural ordering)**가 있음을 뜻한다. 그래서 Arrays.sort() 를 활용한 손쉬운 정렬이 가능해지고, TreeSet, TreeMap과 같은 정렬된 컬렉션의 원소로 사용될 수 있다.

---

### 1. compareTo 메서드의 일반 규약

compareTo 메서드는 현재 객체(this)를 주어진 객체(o)와 비교해서 현재 객체가 더 작으면 음수, 같으면 0, 더 크면 양수를 반환한다.

1. 모든 x, y에 대해`sgn(x.compareTo(y)) == -sgn(y.compareTo(x))`여야 한다.
2. **추이성:** `(x.compareTo(y) > 0 && y.compareTo(z) > 0)`이면 `x.compareTo(z) > 0`여야 한다.
    - x가 y보다 크고 y가 z보다 크면, x는 z보다 커야 한다.
3. **일관성:** `x.compareTo(y) == 0`이면, 모든 z에 대해 `sgn(x.compareTo(z)) == sgn(y.compareTo(z))`여야 한다.
    - x와 y의 순서가 같다면, 어떤 z와 비교하더라도 그 결과는 같아야 한다.
4. **equals와의 연관 (필수가 아닌 권장 사항):** `(x.compareTo(y) == 0) == (x.equals(y))`여야 한다.
    - **필수는 아니지만 강력한 권장 사항이다.** 만약 이 규약을 지키지 않아 compareTo로는 0을 반환하지만 equals로는 false인 객체가 있다면 정렬된 컬렉션(TreeSet, TreeMap)이 equals의 규약과 다르게 동작하여 예측 불가능한 결과를 반환한다.
    - 예를 들어, new BigDecimal("1.0")과 new BigDecimal("1.00")은 equals로는 false지만 compareTo로는 0이다. 이 두 객체를 HashSet에 넣으면 원소가 2개가 되지만, TreeSet에 넣으면 원소가 1개가 되는 문제가 발생한다.

---

### 2. compareTo 메서드 작성 요령

compareTo는 각 필드를 순서대로 비교하도록 작성한다. 비교는 **가장 핵심적인 필드**부터 시작해서 결과가 0이 아닐 경우(순서가 결정된 경우) 바로 그 결과를 반환한다.

### 직접 비교 로직 작성

```java
public final class PhoneNumber implements Comparable<PhoneNumber> {
    private final short areaCode, prefix, lineNum;
   
    @Override
    public int compareTo(PhoneNumber pn) {
        // 1. 가장 중요한 필드(areaCode)부터 비교
        int result = Short.compare(areaCode, pn.areaCode);
        if (result == 0) {
		        // 2. 두 번째로 중요한 필드(prefix) 비교
		        result = Short.compare(prefix, pn.prefix);
	          if (result == 0) {
			          // 3. 마지막 필드(lineNum) 비교
			          result = Short.compare(lineNum, pn.lineNum);
	          }
        }
        return result;
    }
}
```

- 이전에는 정수 기본타입 필드를 비교할 때는 관계 연산자인 <와 >를, 실수 기본 타입 필드를 비교할 때에는 정적 메서드인 Float.compare, Double.compare를 사용하라고 권고했다.
- 자바 7부터는 박싱된 **기본타입 클래스의 정적 compare 메서드를 사용하는게 추천된다**.

### Comparator를 활용한 간결한 구현

자바 8부터는 Comparator 인터페이스가 제공하는 보조 생성 메서드를 활용하여 compareTo를 훨씬 간결하고 안전하게 구현할 수 있다.

```java
import java.util.Comparator;

public final class PhoneNumber implements Comparable<PhoneNumber> {
    private final short areaCode, prefix, lineNum;
    
    private static final Comparator<PhoneNumber> COMPARATOR = 
        Comparator.comparingInt((PhoneNumber pn) -> pn.areaCode)
                  .thenComparingInt(pn -> pn.prefix)
                  .thenComparingInt(pn -> pn.lineNum);

    @Override
    public int compareTo(PhoneNumber pn) {
        return COMPARATOR.compare(this, pn);
    }
}
```

- comparingInt, thenComparingInt와 같은 메서드를 연쇄적으로 호출하여 Comparator를 생성한다.
- 코드가 더 짧아지고 가독성이 높아져 실수를 할 가능성이 줄어든다.

---

### 요약

- 순서를 고려해야 하는 값 클래스를 작성한다면 **반드시 Comparable 인터페이스를 구현**하여 compareTo 메서드를 제공하라.
- compareTo를 구현할 때는 규약을 반드시 지켜야 한다.
- compareTo의 결과가 0인 경우 equals의 결과도 true가 되도록 **강력하게 권장**된다.
- 자바 8 이상이라면 Comparator의 보조 생성 메서드(comparing, thenComparing 등)를 활용하여 compareTo를 구현하는 것이 가장 좋은 방법이다.
