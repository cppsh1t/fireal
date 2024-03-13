package fireal.structure;

public class Tuple<T, R> {

    private final T firstKey;
    private final R secondKey;

    public Tuple(T firstKey, R secondKey) {
        this.firstKey = firstKey;
        this.secondKey = secondKey;
    }

    public T getFirstKey() {
        return firstKey;
    }

    public R getSecondKey() {
        return secondKey;
    }

    @Override
    public String toString() {
        return "{ " + "firstKey: " + firstKey + ", secondKey: " + secondKey + " }";
    }
}
