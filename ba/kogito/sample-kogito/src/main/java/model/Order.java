package model;

public class Order {
    private int price;
    private boolean approved;

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    @Override
    public String toString() {
        return "Order [approved=" + approved + ", price=" + price + "]";
    }
}