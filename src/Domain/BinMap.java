package Domain;

import Enums.ECardType;

public class BinMap {
    private final String name;
    private final long rangeFrom;
    private final long rangeTo;
    private final ECardType type;
    private final String country;

    public BinMap(String name, long rangeFrom, long rangeTo, ECardType type, String country) {

        this.name = name;
        this.rangeFrom = rangeFrom;
        this.rangeTo = rangeTo;
        this.type = type;
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public long getRangeFrom() {
        return rangeFrom;
    }

    public long getRangeTo() {
        return rangeTo;
    }

    public ECardType getType() {
        return type;
    }

    public String getCountry() {
        return country;
    }

    @Override
    public String toString() {
        return "BinMap{" +
                "name='" + name + '\'' +
                ", rangeFrom=" + rangeFrom +
                ", rangeTo=" + rangeTo +
                ", type=" + type +
                ", country='" + country + '\'' +
                '}';
    }
}
