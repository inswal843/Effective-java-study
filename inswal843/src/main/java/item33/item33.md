## Item 33: 타입 안전 이종 컨테이너를 고려하라

컨테이너(Collection)에 여러 다른 타입의 값을 담으면서 값을 꺼낼 때는 **타입 안전성**을 보장받고 싶은 경우가 있다. 일반적인 제네릭(`List<E>`, `Map<K, V>`)은 컨테이너 전체에 단일 타입 매개변수만 적용하므로 이런 상황에는 사용할 수 없다.

**타입 안전 이종 컨테이너(typesafe heterogeneous container)** 패턴은 이러한 문제를 해결할 수 있는 방법이다. 이 패턴의 핵심은 **키(key)를 매개변수화**해서 값을 넣고 뺄 때 키에 해당하는 정확한 타입의 값을 다룰 수 있도록 하는 것이다.

---

### 1. 매개변수화된 키 `Class<?>`

이 패턴의 핵심은 키로 `Class` 객체를 사용하는 것이다. `String.class`는 `Class<String>` 타입이고, `Integer.class`는 `Class<Integer>` 타입이다. 이처럼 제네릭 `Class` 객체를 키로 사용하면 컴파일러는 값의 타입이 키가 나타내는 타입과 일치함을 보장할 수 있다.

컨테이너는 내부적으로 Map<Class<?>, Object>를 사용하여 값을 저장한다.

- **Key:** `Class<?>` (와일드카드는 어떤 타입이든 키가 될 수 있음을 의미)
- **Value:** `Object` (모든 타입의 값을 저장해야 하므로)

```java
public class Favorites {
    private Map<Class<?>, Object> favorites = new HashMap<>();

    public <T> void putFavorite(Class<T> type, T instance) {
        favorites.put(Objects.requireNonNull(type), instance);
    }

    public <T> T getFavorite(Class<T> type) {
        return type.cast(favorites.get(type));
    }
}
```

---

### 2. 동작 방법

### `putFavorite(Class<T> type, T instance)`

- `putFavorite` 메서드는 `Class<T>` 타입의 키와 `T` 타입의 값을 받는다. 이것이 키와 값의 타입이 **컴파일 시점에 일치함**을 보장한다.
- `Map`에는 `Object` 타입으로 저장되지만 메서드 시그니처 덕분에 안전하게 값을 넣을 수 있다.

### `getFavorite(Class<T> type)`

- `getFavorite` 메서드는 `Class<T>` 타입의 키를 받아 `T` 타입의 값을 반환한다.
- `map.get(type)`은 `Object`를 반환하지만, `type.cast(...)` 메서드를 사용해 이 `Object`를 `T` 타입으로 **안전하게 동적 형변환**한다.
- `putFavorite`에서 키와 값의 타입 일치를 보장했기 때문에 `cast` 메서드는 항상 성공하며 `ClassCastException`이 발생하지 않는다.

**예시**

```java
Favorites f = new Favorites();

f.putFavorite(String.class, "Java");
f.putFavorite(Integer.class, 0xcafebabe);
f.putFavorite(Class.class, Favorites.class);

String favoriteString = f.getFavorite(String.class); // 형변환 필요 없음
int favoriteInteger = f.getFavorite(Integer.class);
Class<?> favoriteClass = f.getFavorite(Class.class);

System.out.printf("%s %x %s%n", favoriteString, favoriteInteger, favoriteClass.getName());
// 출력: Java cafebabe Favorites
```

---

### 3. 특징

- **로 타입 `Class` 사용 금지:** `putFavorite(Class.class, "...")`와 같이 로 타입 `Class`를 사용하면 타입 안전성이 깨진다. 항상 `Class<T>`를 사용해야 한다.
- **실체화 불가 타입 저장 불가:** `List<String>.class`와 같은 제네릭 타입의 클래스 리터럴은 얻을 수 없으므로, `List<String>` 자체를 키로 사용할 수는 없다.
- **한정적 타입 토큰:** 컨테이너가 받아들일 수 있는 타입을 제한하고 싶다면 `Class<? extends Annotation>`과 같이 한정적 와일드카드를 사용한 **한정적 타입 토큰**을 활용할 수 있다.

---

### 요약

- 여러 다른 타입의 객체를 하나의 컨테이너에 담으면서 타입 안전성을 유지하고 싶을 때 **타입 안전 이종 컨테이너 패턴**을 사용하라.
- 이 패턴의 핵심은 키를 **매개변수화된 `Class<T>` 객체**로 사용해 값의 타입과 키의 타입이 일치함을 컴파일 시점에 보장하는 것이다.
- `Favorites` 클래스가 대표적인 예 이고 데이터베이스의 행(컨테이너)과 열(키) 또는 애너테이션 API 등에서 유용하게 활용된다.
