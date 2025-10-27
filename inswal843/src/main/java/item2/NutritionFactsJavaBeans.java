package item2;

public class NutritionFactsJavaBeans {

    // 자바 빈즈 패턴은 객체 하나를 만들려면 메서드를 여러 개 호출해야 하고,
    // 객체가 완전히 생성되기 전까지는 일관성이 무너진 상태에 놓이게 된다.

    // 매개변수들은 (기본값이 있다면) 기본값으로 초기화 된다.
    private int servingSize      = -1;  // 필수 입력값
    private int servings         = -1;  // 필수 입력값
    private int calories         = 0;  // 선택 입력값
    private int fat              = 0;  // 선택 입력값
    private int sodium           = 0;  // 선택 입력값
    private int carbohydrate     = 0;  // 선택 입력값

    public NutritionFactsJavaBeans() { }

    public void setServingSize(int servingSize) { this.servingSize = servingSize; }
    public void setServings(int servings) { this.servings = servings; }
    public void setCalories(int calories) { this.calories = calories; }
    public void setFat(int fat) { this.fat = fat; }
    public void setSodium(int sodium) { this.sodium = sodium; }
    public void setCarbohydrate(int carbohydrate) { this.carbohydrate = carbohydrate; }
}