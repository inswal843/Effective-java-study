package item1;

public class Person {
    protected String name;
    protected int age;

    // 학생 내부 클래스
    static class Student extends Person {
        public Student(String name, int age) {
            super(name, age);
        }

        @Override
        public String toString() {
            return name + "(age:" + age + ", student)";
        }
    }

    // 성인 내부 클래스
    static class Adult extends Person {
        public Adult(String name, int age) {
            super(name, age);
        }

        @Override
        public String toString() {
            return name + "(age:" + age + ", adult)";
        }
    }

    // 미리 생성된 인스턴스 (장점 2 캐싱용)
    private static final Person DEFAULT_STUDENT = new Student("DEFAULT_STUDENT", 17);
    private static final Person DEFAULT_ADULT = new Adult("DEFAULT_ADULT", 27);

    // 하위 클래스에서 사용 가능하도록 생성자는 protected로 지정
    protected Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // 장점 1: 이름을 가질 수 있다.
    public static Person createStudent(String name, int age) {
        return new Student(name, age);
    }

    public static Person createAdult(String name, int age) {
        return new Adult(name, age);
    }

    // 장점 2: 호출될 때마다 인스턴스를 새로 생성하지는 않아도 된다. (캐싱된 인스턴스 반환)
    public static Person getDefaultStudent() {
        return DEFAULT_STUDENT;
    }
    public static Person getDefaultAdult() {
        return DEFAULT_ADULT;
    }

    // 장점 3: 반환 타입의 하위 타입 객체를 반환할 수 있는 능력이 있다.
    public static Person createPerson(String name, int age) {
        // Person 타입으로 반환하지만 실제로는 하위 클래스 객체를 반환
        if (age < 20) {
            return new Student(name, age);
        } else {
            return new Adult(name, age);
        }
    }

    // 장점 4: 입력 매개변수에 따라 매번 다른 클래스의 객체를 반환할 수 있다.
    public static Person createByAge(String name, int age) {
        if (age < 20) {
            return new Student(name, age);    // Student 클래스 객체 반환
        } else {
            return new Adult(name, age);    // Adult 클래스 객체 반환
        }
    }

    @Override
    public String toString() {
        return name + "(age:" + age + ")";
    }
}