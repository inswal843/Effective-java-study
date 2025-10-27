package item2;

public class NutritionFactsBuilder {

    // 점층적 생성자 패턴의 안전성과 자바빈즈 패턴의 가독성을 겸비한 빌더 패턴

    private final int servingSize;      // 필수 입력값
    private final int servings;         // 필수 입력값
    private final int calories;         // 선택 입력값
    private final int fat;              // 선택 입력값
    private final int sodium;           // 선택 입력값
    private final int carbohydrate;     // 선택 입력값

    public static class Builder {
        // 필수 매개변수
        private final int servingSize;
        private final int servings;

        // 선택 매개변수 - 기본값으로 초기화한다.
        private int calories        = 0;
        private int fat             = 0;
        private int sodium          = 0;
        private int carbohydrate    = 0;

        // 필수 매개변수만으로 생성자를 호출해 빌더 객체를 얻는다.
        public Builder(int servingSize, int servings) {
            this.servingSize = servingSize;
            this.servings = servings;
        }

        // 빌더 객체가 제공하는 세터 메서드들로 선택 매개변수 설정이 가능하다.

        public Builder calories(int val) {
            calories = val;
            return this;
        }

        public Builder fat(int val) {
            fat = val;
            return this;
        }

        public Builder sodium(int val) {
            sodium = val;
            return this;
        }

        public Builder carbohydrate(int val) {
            carbohydrate = val;
            return this;
        }

        // build() 호출로 최종 불변 객체를 얻는다.
        public NutritionFactsBuilder build() {
            return new NutritionFactsBuilder(this);
        }
    }

    private NutritionFactsBuilder(Builder builder) {
        servingSize     = builder.servingSize;
        servings        = builder.servings;
        calories        = builder.calories;
        fat             = builder.fat;
        sodium          = builder.sodium;
        carbohydrate    = builder.carbohydrate;
    }
}