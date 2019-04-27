package sidddharth.co.btc.models;

public class NewBlock {
    String hash;
    double totalBTCSent;
    double reward;
    double size;
    int height;

    public String getHash() {
        return hash;
    }

    public double getTotalBTCSent() {
        return totalBTCSent;
    }

    public double getReward() {
        return reward;
    }

    public double getSize() {
        return size;
    }


    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setTotalBTCSent(double totalBTCSent) {
        this.totalBTCSent = totalBTCSent;
    }

    public void setReward(double reward) {
        this.reward = reward;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
