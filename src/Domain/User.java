package Domain;

import java.util.*;

public class User {

    private final String userId;
    private final String username;
    private Double balance;
    private final String country;
    private final Integer frozen;
    private final Double depositMin;
    private final Double depositMax;
    private final Double withdrawMin;
    private final Double withdrawMax;


    public User(String userId, String username, Double balance, String country, Integer frozen, Double depositMin, Double depositMax,
                Double withdrawMin, Double withdrawMax) {
        this.userId = userId;
        this.username = username;
        this.balance = balance;
        this.country = country;
        this.frozen = frozen;
        this.depositMin = depositMin;
        this.depositMax = depositMax;
        this.withdrawMin = withdrawMin;
        this.withdrawMax = withdrawMax;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getCountry() {
        return country;
    }

    public Integer getFrozen() {
        return frozen;
    }

    public Double getDepositMin() {
        return depositMin;
    }

    public Double getDepositMax() {
        return depositMax;
    }

    public Double getWithdrawMin() {
        return withdrawMin;
    }

    public Double getWithdrawMax() {
        return withdrawMax;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", balance=" + balance +
                ", country='" + country + '\'' +
                ", frozen=" + frozen +
                ", depositMin=" + depositMin +
                ", depositMax=" + depositMax +
                ", withdrawMin=" + withdrawMin +
                ", withdrawMax=" + withdrawMax +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof User)) {
            return false;
        }
        User other = (User) obj;


        return Objects.equals(userId, other.userId);
    }
}
