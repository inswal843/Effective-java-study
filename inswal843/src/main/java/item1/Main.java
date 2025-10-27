package item1;

public class Main {
    public static void main(String[] args) {
        // 장점 1: 이름을 가질 수 있다.
        Person student = Person.createStudent("KIM", 16);
        Person adult = Person.createAdult("PARK", 25);
        System.out.println("1. " + student + ", " + adult);

        // 장점 2: 호출될 때마다 인스턴스를 새로 생성하지는 않아도 된다.
        Person defaultStudent1 = Person.getDefaultStudent();
        Person defaultStudent2 = Person.getDefaultStudent();
        System.out.println("2. Same object?: " + (defaultStudent1 == defaultStudent2));

        // 장점 3: 반환타입의 하위 타입 객체를 반환할 수 있는 능력이 있다.
        Person person = Person.createPerson("HWANG", 18);
        System.out.println("3. " + person + " (actual class: " + person.getClass().getSimpleName() + ")");

        // 장점 4: 입력 매개변수에 따라 매번 다른 클래스의 객체를 반환할 수 있다.
        Person person1 = Person.createByAge("STUDENT", 17);  // Student 클래스
        Person person2 = Person.createByAge("ADULT", 30); // Adult 클래스
        System.out.println("4. " + person1 + " (class: " + person1.getClass().getSimpleName() + ")");
        System.out.println("   " + person2 + " (class: " + person2.getClass().getSimpleName() + ")");
    }
}
