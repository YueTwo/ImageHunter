package util;

public class myPairThree<T, U, S> {
    public T first;
    public U second;
    public S third;

    public T getFirst() {
        return first;
    }

    public void setFirst(T first) {
        this.first = first;
    }

    public S getThird() {
        return third;
    }

    public void setThird(S third) {
        this.third = third;
    }

    public U getSecond() {
        return second;
    }

    public void setSecond(U second) {
        this.second = second;
    }


    myPairThree(T first, U second, S third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }
}
