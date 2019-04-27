package sidddharth.co.btc.models;

public class UTX {
    String transactionHash;
    double transactionAmnt;
    long timestamp;

    public String getTransactionHash() {
        return transactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        this.transactionHash = transactionHash;
    }

    public double getTransactionAmnt() {
        return transactionAmnt;
    }

    public void setTransactionAmnt(double transactionAmnt) {
        this.transactionAmnt = transactionAmnt;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
