package util;

public class myPair<T, U> {
    public T first;
    public U second;
    public T getFirst() {
        return first;
    }

    public void setFirst(T first) {
        this.first = first;
    }

    public U getSecond() {
        return second;
    }

    public void setSecond(U second) {
        this.second = second;
    }

    public myPair() {
    }

    public myPair(T first, U second) {
        this.first = first;
        this.second = second;
    }

}
